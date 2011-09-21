package com.epickrram.monitaur.common.io;

import java.io.IOException;

public interface DecoderStream
{
    public boolean readBoolean() throws IOException;
    public byte readByte() throws IOException;
    public int readInt() throws IOException;
    public long readLong() throws IOException;
    public float readFloat() throws IOException;
    public double readDouble() throws IOException;
    public byte[] readByteArray() throws IOException;
    public String readString() throws IOException;
    public <T> T readObject() throws IOException;
}
