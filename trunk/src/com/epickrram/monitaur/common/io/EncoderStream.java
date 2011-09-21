package com.epickrram.monitaur.common.io;

import java.io.IOException;

public interface EncoderStream
{
    public void writeBoolean(final boolean v) throws IOException;
    public void writeByte(final byte v) throws IOException;
    public void writeInt(final int v) throws IOException;
    public void writeLong(final long v) throws IOException;
    public void writeFloat(final float v) throws IOException;
    public void writeDouble(final double v) throws IOException;
    public void writeByteArray(final byte[] b) throws IOException;
    public void writeByteArray(final byte[] b, int off, int len) throws IOException;
    public void writeString(final String s) throws IOException;
    public <T> void writeObject(final T o) throws IOException;
}
