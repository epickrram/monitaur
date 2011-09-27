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

public enum DataType
{
    INTEGER(true),
    LONG(true),
    DOUBLE(true),
    FLOAT(true),
    STRING(false),
    OBJECT(false);

    private final boolean isNumber;

    private DataType(final boolean number)
    {
        isNumber = number;
    }

    public boolean isNumber()
    {
        return isNumber;
    }

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
