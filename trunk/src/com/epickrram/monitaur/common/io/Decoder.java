package com.epickrram.monitaur.common.io;

import java.io.IOException;

public interface Decoder<T>
{
    T decode(final DecoderStream decoderStream) throws IOException;
}