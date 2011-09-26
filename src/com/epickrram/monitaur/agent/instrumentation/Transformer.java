package com.epickrram.monitaur.agent.instrumentation;

import com.epickrram.monitaur.agent.latency.MonitorLatency;
import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.TimeUnit;

public final class Transformer implements ClassFileTransformer
{
    private static final long METHOD_CALL_WARNING_THRESHOLD_NANOS = TimeUnit.MILLISECONDS.toNanos(500L);

    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException
    {
        final ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
        CtClass cc = null;
        try
        {
            cc = classPool.get(className.replace('/', '.'));
        }
        catch (NotFoundException nfe)
        {
            System.err.println("NotFoundException: " + nfe.getMessage() + "; transforming class " + className + "; returning uninstrumented class");
            return classfileBuffer;
        }
        try
        {

            CtMethod[] methods = cc.getDeclaredMethods();
            for (CtMethod method : methods)
            {
                try
                {
                    if(cc.getName().contains("TestObject"))
                    {
                        System.out.println("Processing " + method.getName());
                    }
                    final MonitorLatency annotation = (MonitorLatency) method.getAnnotation(MonitorLatency.class);
                    if(annotation != null)
                    {
                        cc.addField(CtField.make("private static final ThreadLocal METHOD_DURATION = new ThreadLocal();", cc));

                        System.out.println("Processing method: " + method.getName());
                        method.insertBefore("{" +
                                           "METHOD_DURATION.set(Long.valueOf(System.nanoTime()));}");
                        method.insertAfter("{final long durationNanos = System.nanoTime() - ((Long) METHOD_DURATION.get()).longValue();\n" +
                                           "com.epickrram.monitaur.agent.instrumentation.Transformer.reportMethodDuration(\"" + className + "\", \"" + method.getName() + "\", durationNanos, " + annotation.durationThresholdMillis() + "L);}", true);
                    }
                }
                catch (CannotCompileException cce)
                {
                    System.err.println("CannotCompileException: " + cce.getMessage() + "; instrumenting method " + method.getLongName() + "; method will not be instrumented");
                    cce.printStackTrace();
                }
            }
            // return the new bytecode array:
            byte[] newClassfileBuffer = null;
            try
            {
                newClassfileBuffer = cc.toBytecode();
            }
            catch (IOException ioe)
            {
                System.err.println("IOException: " + ioe.getMessage() + "; transforming class " + className + "; returning uninstrumented class");
                return null;
            }
            catch (CannotCompileException cce)
            {
                System.err.println("CannotCompileException: " + cce.getMessage() + "; transforming class " + className + "; returning uninstrumented class");
                return null;
            }
            return newClassfileBuffer;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return classfileBuffer;
    }

    public static void reportMethodDuration(final String className, final String methodId, final long durationNanos, final long thresholdMillis)
    {
        final long durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanos);
        if(durationMillis > thresholdMillis)
        {
            System.out.println("*** took " + durationMillis + "ms (greater than " + thresholdMillis + "ms)");
        }
    }
}