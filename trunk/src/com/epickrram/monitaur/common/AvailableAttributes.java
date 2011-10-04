package com.epickrram.monitaur.common;

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.monitaur.common.io.Transferrable;
import com.epickrram.monitaur.common.jmx.AttributeDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Transferrable
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

    public static final class Transcoder implements com.epickrram.freewheel.io.Transcoder<AvailableAttributes>
    {
        @Override
        public void encode(final AvailableAttributes encodable, final EncoderStream encoderStream) throws IOException
        {
            encoderStream.writeString(encodable.agentId);
            encoderStream.writeInt(encodable.attributeDetails.size());
            for (AttributeDetails attributeDetail : encodable.attributeDetails)
            {
                encoderStream.writeObject(attributeDetail);
            }
        }

        @Override
        public AvailableAttributes decode(final DecoderStream decoderStream) throws IOException
        {
            final String agentId = decoderStream.readString();
            final int collectionSize = decoderStream.readInt();
            final List<AttributeDetails> attributeList = new ArrayList<AttributeDetails>(collectionSize);
            for(int i = 0; i < collectionSize; i++)
            {
                attributeList.add(decoderStream.<AttributeDetails>readObject());
            }

            return new AvailableAttributes(agentId, attributeList);
        }
    }
}
