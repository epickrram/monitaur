package com.epickrram.monitaur.common.io;

import org.msgpack.packer.Packer;

import java.io.IOException;

public final class PackerEncoderStream implements EncoderStream
{
    private final CodeBook<String> codeBook;
    private final Packer packer;

    public PackerEncoderStream(final CodeBook<String> codeBook, final Packer packer)
    {
        this.codeBook = codeBook;
        this.packer = packer;
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException
    {
        packer.writeBoolean(v);
    }

    @Override
    public void writeByte(final byte v) throws IOException
    {
        packer.writeByte(v);
    }

    @Override
    public void writeInt(final int v) throws IOException
    {
        packer.writeInt(v);
    }

    @Override
    public void writeLong(final long v) throws IOException
    {
        packer.writeLong(v);
    }

    @Override
    public void writeFloat(final float v) throws IOException
    {
        packer.writeFloat(v);
    }

    @Override
    public void writeDouble(final double v) throws IOException
    {
        packer.writeDouble(v);
    }

    @Override
    public void writeByteArray(final byte[] b) throws IOException
    {
        packer.writeByteArray(b);
    }

    @Override
    public void writeByteArray(final byte[] b, final int off, final int len) throws IOException
    {
        packer.writeByteArray(b, off, len);
    }

    @Override
    public void writeString(final String s) throws IOException
    {
        packer.writeString(s);
    }

    public <T> void writeObject(final T o) throws IOException
    {
        packer.writeBoolean(o == null);
        if(o != null)
        {
            packer.writeString(o.getClass().getName());
            codeBook.getEncoder(o.getClass().getName()).encode(o, this);
        }
    }
}