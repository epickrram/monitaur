package com.epickrram.monitaur.common.jmx;

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.io.Transcoder;
import com.epickrram.monitaur.common.io.Transferrable;

import java.io.IOException;

@Transferrable
public final class AttributeDetails
{
    private final String objectName;
    private final String attributeName;

    public AttributeDetails(final String objectName, final String attributeName)
    {
        this.objectName = objectName;
        this.attributeName = attributeName;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    @Override
    public String toString()
    {
        return "AttributeDetails{" +
                "objectName='" + objectName + '\'' +
                ", attributeName='" + attributeName + '\'' +
                '}';
    }

    public static final class Transcoder implements com.epickrram.freewheel.io.Transcoder<AttributeDetails>
    {
        @Override
        public void encode(final AttributeDetails encodable, final EncoderStream encoderStream) throws IOException
        {
            encoderStream.writeString(encodable.objectName);
            encoderStream.writeString(encodable.attributeName);
        }

        @Override
        public AttributeDetails decode(final DecoderStream decoderStream) throws IOException
        {
            final String objectName = decoderStream.readString();
            final String attributeName = decoderStream.readString();

            return new AttributeDetails(objectName, attributeName);
        }
    }

}
