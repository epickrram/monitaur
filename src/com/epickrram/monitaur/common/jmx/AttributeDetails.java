package com.epickrram.monitaur.common.jmx;

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.protocol.AbstractTranslator;
import com.epickrram.freewheel.protocol.Translatable;
import com.epickrram.monitaur.common.domain.TranslatorCodes;

import java.io.IOException;

@Translatable(codeBookId = TranslatorCodes.ATTRIBUTE_DETAILS)
public final class AttributeDetails
{
    private final String objectName;
    private final String attributeName;
    private final String compositeKey;

    public AttributeDetails(final String objectName, final String attributeName, final String compositeKey)
    {
        this.objectName = objectName;
        this.attributeName = attributeName;
        this.compositeKey = compositeKey;
    }

    public AttributeDetails(final String objectName, final String attributeName)
    {
        this.objectName = objectName;
        this.attributeName = attributeName;
        compositeKey = null;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    public String getCompositeKey()
    {
        return compositeKey;
    }

    public boolean isCompositeData()
    {
        return compositeKey != null;
    }

    public String getDefaultLogicalName()
    {
        return isCompositeData() ? attributeName + "_" + compositeKey : attributeName;
    }

    @Override
    public String toString()
    {
        return "AttributeDetails{" +
                "objectName='" + objectName + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", compositeKey='" + compositeKey + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AttributeDetails details = (AttributeDetails) o;

        if (attributeName != null ? !attributeName.equals(details.attributeName) : details.attributeName != null)
            return false;
        if (compositeKey != null ? !compositeKey.equals(details.compositeKey) : details.compositeKey != null)
            return false;
        if (objectName != null ? !objectName.equals(details.objectName) : details.objectName != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = objectName != null ? objectName.hashCode() : 0;
        result = 31 * result + (attributeName != null ? attributeName.hashCode() : 0);
        result = 31 * result + (compositeKey != null ? compositeKey.hashCode() : 0);
        return result;
    }

    public static final class Translator extends AbstractTranslator<AttributeDetails>
    {
        @Override
        protected void doEncode(final AttributeDetails encodable, final EncoderStream encoderStream) throws IOException
        {
            encoderStream.writeString(encodable.objectName);
            encoderStream.writeString(encodable.attributeName);
            encoderStream.writeString(encodable.compositeKey);
        }

        @Override
        protected AttributeDetails doDecode(final DecoderStream decoderStream) throws IOException
        {
            final String objectName = decoderStream.readString();
            final String attributeName = decoderStream.readString();
            final String compositeKeyName = decoderStream.readString();

            return new AttributeDetails(objectName, attributeName, compositeKeyName);
        }
    }
}