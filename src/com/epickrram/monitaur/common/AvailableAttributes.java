package com.epickrram.monitaur.common;

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.protocol.AbstractTranslator;
import com.epickrram.freewheel.protocol.Translatable;
import com.epickrram.monitaur.common.domain.TranslatorCodes;
import com.epickrram.monitaur.common.jmx.AttributeDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Translatable(codeBookId = TranslatorCodes.AVAILABLE_ATTRIBUTES)
public final class AvailableAttributes
{
    private final String agentId;
    private final List<AttributeDetails> attributeDetails;

    public AvailableAttributes(final String agentId, final List<AttributeDetails> attributeDetails)
    {
        this.agentId = agentId;
        this.attributeDetails = attributeDetails;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public List<AttributeDetails> getAttributeDetails()
    {
        return attributeDetails;
    }

    public static final class Translator extends AbstractTranslator<AvailableAttributes>
    {
        @Override
        protected void doEncode(final AvailableAttributes encodable, final EncoderStream encoderStream) throws IOException
        {
            encoderStream.writeString(encodable.agentId);
            encoderStream.writeCollection(encodable.attributeDetails);
        }

        @Override
        protected AvailableAttributes doDecode(final DecoderStream decoderStream) throws IOException
        {
            final String agentId = decoderStream.readString();
            final List<AttributeDetails> attributeList = new ArrayList<AttributeDetails>();
            decoderStream.readCollection(attributeList);

            return new AvailableAttributes(agentId, attributeList);
        }
    }
}
