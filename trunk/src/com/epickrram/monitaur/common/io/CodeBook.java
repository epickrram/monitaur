package com.epickrram.monitaur.common.io;

public interface CodeBook<CodeType>
{
    <T> Decoder<T> getDecoder(final CodeType code);
    <T> Encoder<T> getEncoder(final CodeType code);
}