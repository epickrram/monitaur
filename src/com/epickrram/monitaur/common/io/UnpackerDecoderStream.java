package com.epickrram.monitaur.common.io;

import org.msgpack.unpacker.Unpacker;

import java.io.IOException;

public final class UnpackerDecoderStream implements DecoderStream
{
    private final CodeBook<String> codeBook;
    private final Unpacker unpacker;

    public UnpackerDecoderStream(final CodeBook<String> codeBook, final Unpacker unpacker)
    {
        this.codeBook = codeBook;
        this.unpacker = unpacker;
    }

    @Override
    public boolean readBoolean() throws IOException
    {
        return unpacker.readBoolean();
    }

    @Override
    public byte readByte() throws IOException
    {
        return unpacker.readByte();
    }

    @Override
    public int readInt() throws IOException
    {
        return unpacker.readInt();
    }

    @Override
    public long readLong() throws IOException
    {
        return unpacker.readLong();
    }

    @Override
    public float readFloat() throws IOException
    {
        return unpacker.readFloat();
    }

    @Override
    public double readDouble() throws IOException
    {
        return unpacker.readDouble();
    }

    @Override
    public byte[] readByteArray() throws IOException
    {
        return unpacker.readByteArray();
    }

    @Override
    public String readString() throws IOException
    {
        return unpacker.readString();
    }

    @Override
    public <T> T readObject() throws IOException
    {
        final boolean isNull = unpacker.readBoolean();
        if(isNull)
        {
            return null;
        }
        else
        {
            final String className = unpacker.readString();
            return (T) codeBook.getDecoder(className).decode(this);
        }
    }
}
