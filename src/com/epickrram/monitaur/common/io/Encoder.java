package com.epickrram.monitaur.common.io;

import java.io.IOException;

public interface Encoder<T>
{
    void encode(final T encodable, final EncoderStream encoderStream) throws IOException;
}