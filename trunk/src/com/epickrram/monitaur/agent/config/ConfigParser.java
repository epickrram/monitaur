package com.epickrram.monitaur.agent.config;

import com.epickrram.monitaur.agent.collector.CalculatingNumberJmxCollector;
import com.epickrram.monitaur.agent.collector.ConstantNumberCollector;
import com.epickrram.monitaur.agent.collector.GaugeCollector;
import com.epickrram.monitaur.agent.collector.JmxCollector;
import com.epickrram.monitaur.agent.collector.NamedAttributeJmxCollector;
import com.epickrram.monitaur.agent.jmx.AttributePath;
import com.epickrram.monitaur.agent.jmx.lookup.JmxSearchTerm;
import com.epickrram.monitaur.common.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public final class ConfigParser implements JmxCollectorListener
{
    private static final Logger LOGGER = Logger.getLogger(ConfigParser.class);
    private final Collection<JmxCollector> jmxCollectors = new ArrayList<JmxCollector>();

    public void parse(final InputStream inputStream) throws IOException
    {
        try
        {
            SAXParserFactory.newInstance().newSAXParser().parse(inputStream, new Parser(this));
        }
        catch (SAXException e)
        {
            throw new IOException("Could not parse config resource", e);
        }
        catch (ParserConfigurationException e)
        {
            throw new IOException("Could not parse config resource", e);
        }
    }

    public Collection<JmxCollector> getJmxCollectors()
    {
        return jmxCollectors;
    }

    @Override
    public void receiveCollector(final String nodeName, final JmxCollector collector)
    {
        jmxCollectors.add(collector);
    }

    private static final class Parser extends DefaultHandler
    {
        private final Stack<JmxCollectorListener> listenerStack = new Stack<JmxCollectorListener>();
        private final Stack<String> parentNodeNameStack = new Stack<String>();
        private String lastNodeName = null;

        private Parser(final JmxCollectorListener listener)
        {
            listenerStack.push(listener);
        }

        @Override
        public void startElement(final String s, final String s1, final String nodeName, final Attributes attributes) throws SAXException
        {
            final Map<String, String> attributeMap = toAttributeMap(attributes);
            if("jmxAttributeCollector".equals(nodeName))
            {
                final JmxCollector collector = namedAttributeCollector(attributeMap);
                if(collector != null)
                {
                    listenerStack.peek().receiveCollector(lastNodeName, collector);
                }
            }
            else if("constantValueCollector".equals(nodeName))
            {
                listenerStack.peek().receiveCollector(lastNodeName,
                        new ConstantNumberCollector(getLogicalName(attributeMap),
                        Integer.valueOf(attributeMap.get("value"))));
            }
            else if("gauge".equals(nodeName))
            {
                final GaugeCollectorBuilder builder = new GaugeCollectorBuilder();
                builder.onStart(getLogicalName(attributeMap));
                listenerStack.push(builder);
            }
            else if("calculatingNumberJmxCollector".equals(nodeName))
            {
                final CalculatingJmxCollectorBuilder builder = new CalculatingJmxCollectorBuilder();
                builder.onStart(CalculatingNumberJmxCollector.Operator.valueOf(attributeMap.get("operator")), getLogicalName(attributeMap));
                listenerStack.push(builder);
            }

            if(lastNodeName != null)
            {
                parentNodeNameStack.push(lastNodeName);
            }
            lastNodeName = nodeName;
        }

        private <T> T safePeek(final Stack<T> stack)
        {
            return stack.size() != 0 ? stack.peek() : null;
        }

        private String getLogicalName(final Map<String, String> attributeMap)
        {
            return attributeMap.get("logicalName");
        }

        @Override
        public void endElement(final String s, final String s1, final String nodeName) throws SAXException
        {
            // TODO node-name broken for nested gauge/calculator collectors
            if("gauge".equals(nodeName))
            {
                final GaugeCollectorBuilder gaugeCollectorBuilder = (GaugeCollectorBuilder) listenerStack.pop();
                listenerStack.peek().receiveCollector(lastNodeName, gaugeCollectorBuilder.newInstance());
            }
            else if("calculatingNumberJmxCollector".equals(nodeName))
            {
                final CalculatingJmxCollectorBuilder calculatingJmxCollectorBuilder = (CalculatingJmxCollectorBuilder) listenerStack.pop();
                listenerStack.peek().receiveCollector(safePeek(parentNodeNameStack), calculatingJmxCollectorBuilder.newInstance());
            }
            if(parentNodeNameStack.size() != 0)
            {
                parentNodeNameStack.pop();
            }
        }

        private static Map<String, String> toAttributeMap(final Attributes attributes)
        {
            final Map<String, String> attributeMap = new HashMap<String, String>(attributes.getLength());
            for(int i = 0; i < attributes.getLength(); i++)
            {
                attributeMap.put(attributes.getQName(i), attributes.getValue(i));
            }
            return attributeMap;
        }
    }

    private static final class GaugeCollectorBuilder implements JmxCollectorListener
    {
        private Map<String, JmxCollector> gaugeCollectors = new HashMap<String, JmxCollector>();
        private String logicalName = null;

        private void onStart(final String logicalName)
        {
            this.logicalName = logicalName;
            gaugeCollectors.clear();
        }

        @Override
        public void receiveCollector(final String nodeName, final JmxCollector collector)
        {
            gaugeCollectors.put(nodeName, collector);
        }

        private GaugeCollector newInstance()
        {
            validateGaugeCollectors();
            return new GaugeCollector(logicalName,
                            gaugeCollectors.get("gaugeMinimumValueCollector"),
                            gaugeCollectors.get("gaugeCurrentValueCollector"),
                            gaugeCollectors.get("gaugeMaximumValueCollector"));
        }

        private void validateGaugeCollectors()
        {
            // TODO check contains correct nodeNames
            if(gaugeCollectors.size() != 3)
            {
                throw new IllegalStateException("Invalid gauge configuration. Should have exactly three child collectors.");
            }
        }
    }

    private static final class CalculatingJmxCollectorBuilder implements JmxCollectorListener
    {
        private CalculatingNumberJmxCollector.Operator operator = null;
        private JmxCollector operandOne = null;
        private JmxCollector operandTwo = null;
        private String logicalName = null;

        private void onStart(final CalculatingNumberJmxCollector.Operator operator, final String logicalName)
        {
            this.operator = operator;
            this.logicalName = logicalName;
            operandOne = null;
            operandTwo = null;
        }

        private JmxCollector newInstance()
        {
            return new CalculatingNumberJmxCollector(logicalName, operandOne, operandTwo, operator);
        }

        @Override
        public void receiveCollector(final String nodeName, final JmxCollector collector)
        {
            if(operandOne == null)
            {
                operandOne = collector;
            }
            else if(operandTwo == null)
            {
                operandTwo = collector;
            }
            else
            {
                throw new IllegalArgumentException("Only two child collectors allowed");
            }
        }
    }

    private static JmxCollector namedAttributeCollector(final Map<String, String> attributes)
    {
        final String objectNameRegex = attributes.get("objectName");
        final String attributeNameRegex = attributes.get("attributeName");
        final String compositeKey = attributes.get("compositeKey");
        final String logicalName = attributes.get("logicalName");

        final AttributePath attributePath = JmxSearchTerm.searchAttributes(objectNameRegex, attributeNameRegex);
        JmxCollector collector = null;
        if(attributePath != null)
        {
            try
            {
                collector = new NamedAttributeJmxCollector(logicalName, attributePath.getObjectName(),
                        attributePath.getAttributeInfo(), compositeKey);
            }
            catch(Throwable t)
            {
                LOGGER.error("Error constructing JMX collector", t);
            }
        }
        return collector;
    }
}