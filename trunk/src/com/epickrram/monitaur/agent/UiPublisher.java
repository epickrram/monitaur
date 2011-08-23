package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.agent.domain.GuageData;
import com.epickrram.monitaur.agent.domain.MonitorData;
import com.epickrram.monitaur.agent.domain.MonitorType;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

public final class UiPublisher extends Canvas implements Publisher
{
    private final Map<String, MonitorData> dataByLogicalNameMap = new LinkedHashMap<String, MonitorData>();
    private int width = 0;
    private int height = 0;
    private BufferedImage image = null;
    private Graphics buffer;

    @Override
    public void publish(final MonitorData data)
    {
        dataByLogicalNameMap.put(data.getLogicalName(), data);
        repaint();
    }

    @Override
    public void paint(final Graphics graphics)
    {
        final Dimension size = getSize();
        if(sizeChanged(size))
        {
            width = size.width;
            height = size.height;
            assignImage();
        }
        if(image != null)
        {
            drawData(size);

            graphics.drawImage(image, 0, 0, this);
        }
    }

    private void drawData(final Dimension size)
    {
        buffer.setColor(Color.WHITE);
        buffer.fillRect(0, 0, width, height);
        int xoffset = 10;
        int yoffset = 10;
        final int barWidth = size.width - 20;
        for (final MonitorData monitorData : dataByLogicalNameMap.values())
        {
            if(monitorData.getMonitorType() == MonitorType.GUAGE)
            {
                final GuageData guageData = (GuageData) monitorData.getDatum();
                drawGraphAt(xoffset, yoffset, barWidth, 10, guageData.getPercentage(), guageData.getCurrentValue(), guageData.getMaximum(), monitorData.getLogicalName());
                yoffset += 44;
            }
        }
    }

    private void drawGraphAt(final int xoffset, final int yoffset, final int width, final int height,
                             final float percentageFilled, final Number current, final Number maximum, final String logicalName)
    {
        buffer.setColor(Color.GREEN);
        buffer.fillRect(xoffset, yoffset, ((int) (width * (percentageFilled/100f))), height);
        buffer.setColor(Color.BLACK);
        buffer.drawRect(xoffset, yoffset, width, height);
        buffer.drawString(logicalName + "(" + current + "/" + maximum + ")", xoffset, yoffset + height + 2 + getFontMetrics(getFont()).getHeight());
    }

    @Override
    public void update(final Graphics graphics)
    {
        paint(graphics);
    }

    private boolean sizeChanged(final Dimension size)
    {
        return (size.width != width) || (size.height != height);
    }

    private void assignImage()
    {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        buffer = image.getGraphics();
    }
}