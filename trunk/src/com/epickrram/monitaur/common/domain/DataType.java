package com.epickrram.monitaur.common.domain;

public enum DataType
{
    INTEGER,
    LONG,
    DOUBLE,
    FLOAT,
    STRING,
    OBJECT;

    public static DataType fromClassname(final String cls)
    {
        final DataType dataType;
        if(Integer.class.getName().equals(cls) || int.class.getName().equals(cls))
        {
            dataType = INTEGER;
        }
        else if(Long.class.getName().equals(cls) || long.class.getName().equals(cls))
        {
            dataType = LONG;
        }
        else if(Float.class.getName().equals(cls) || float.class.getName().equals(cls))
        {
            dataType = FLOAT;
        }
        else if(Double.class.getName().equals(cls) || double.class.getName().equals(cls))
        {
            dataType = DOUBLE;
        }
        else if(String.class.getName().equals(cls))
        {
            dataType = STRING;
        }
        else
        {
            dataType = OBJECT;
        }

        return dataType;
    }
}
