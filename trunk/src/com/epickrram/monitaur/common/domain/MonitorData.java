/*
Copyright 2011 Mark Price

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.epickrram.monitaur.common.domain;

import com.epickrram.monitaur.common.io.Decoder;
import com.epickrram.monitaur.common.io.DecoderStream;
import com.epickrram.monitaur.common.io.Encoder;
import com.epickrram.monitaur.common.io.EncoderStream;

import java.io.IOException;

public final class MonitorData
{
    private final MonitorType monitorType;
    private final String logicalName;
    private final String host;
    private final Object datum;
    private final long timestamp;

    public MonitorData(final MonitorType monitorType, final String logicalName, final String host, final Object datum, final long timestamp)
    {
        this.logicalName = logicalName;
        this.datum = datum;
        this.timestamp = timestamp;
        this.monitorType = monitorType;
        this.host = host;
    }

    public MonitorType getMonitorType()
    {
        return monitorType;
    }

    public String getLogicalName()
    {
        return logicalName;
    }

    public String getHost()
    {
        return host;
    }

    public Object getDatum()
    {
        return datum;
    }

    public String getDatumAsString()
    {
        return String.valueOf(datum);
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String toString()
    {
        return "MonitorData{" +
                "monitorType=" + monitorType +
                ", logicalName='" + logicalName + '\'' +
                ", host='" + host + '\'' +
                ", datum=" + getDatumAsString() +
                ", timestamp=" + timestamp +
                '}';
    }

    public static final class WireFormat implements Encoder<MonitorData>, Decoder<MonitorData>
    {
        @Override
        public void encode(final MonitorData encodable, final EncoderStream encoderStream) throws IOException
        {
            encoderStream.writeInt(encodable.monitorType.ordinal());
            encoderStream.writeString(encodable.logicalName);
            encoderStream.writeString(encodable.host);
            encoderStream.writeObject(encodable.datum);
            encoderStream.writeLong(encodable.timestamp);
        }

        @Override
        public MonitorData decode(final DecoderStream decoderStream) throws IOException
        {
            final MonitorType monitorType = MonitorType.values()[decoderStream.readInt()];
            final String logicalName = decoderStream.readString();
            final String host = decoderStream.readString();
            final Object datum = decoderStream.readObject();
            final long timestamp = decoderStream.readLong();

            return new MonitorData(monitorType, logicalName, host, datum, timestamp);
        }
    }
}