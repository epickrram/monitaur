package com.epickrram.monitaur.common.instrumentation;

import com.epickrram.freewheel.io.ClassnameCodeBook;
import com.epickrram.freewheel.io.Transcoder;
import com.epickrram.freewheel.util.Logger;
import com.epickrram.monitaur.common.io.Transferrable;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public final class TransferrableFinder implements ClassFileTransformer
{
    private static final Logger LOGGER = Logger.getLogger(TransferrableFinder.class);

    private final ClassnameCodeBook classnameCodeBook;

    public TransferrableFinder(final ClassnameCodeBook classnameCodeBook)
    {
        this.classnameCodeBook = classnameCodeBook;
    }

    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException
    {
        // TODO classBeingRedefined is null - create Transcoder class from CtClass
        final ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
        CtClass cc = null;
        try
        {
            cc = classPool.get(className.replace('/', '.'));

            final Transferrable transferrable = (Transferrable) cc.getAnnotation(Transferrable.class);
            if (transferrable != null)
            {

                LOGGER.info("Attempting to add Transcoder to codebook for class: " + className);
                final Transcoder<Object> transcoder = createTranscoder(classBeingRedefined);
                if (transcoder != null)
                {
                    classnameCodeBook.registerTranscoder(className, transcoder);
                    LOGGER.info("Added Transcoder to code book for class " + className);
                }
                else
                {
                    LOGGER.warn("Unable to create Transcoder for class: " + className);
                }
            }
        }
        catch (NotFoundException nfe)
        {

        }
        catch (ClassNotFoundException e)
        {

        }
        catch(Throwable t)
        {

        }
        return classfileBuffer;

    }

    private Transcoder<Object> createTranscoder(final Class<?> classBeingRedefined)
    {
        final Class<?>[] declaredClasses = classBeingRedefined.getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses)
        {
            if (declaredClass.getName().contains("Transcoder"))
            {
                try
                {
                    return (Transcoder<Object>) declaredClass.newInstance();
                }
                catch (InstantiationException e)
                {
                    LOGGER.error("Could not create Transcoder for class: " + classBeingRedefined, e);
                }
                catch (IllegalAccessException e)
                {
                    LOGGER.error("Could not create Transcoder for class: " + classBeingRedefined, e);
                }
            }
        }

        return null;
    }
}
