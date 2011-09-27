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
package com.epickrram.monitaur.agent.instrumentation;

import com.epickrram.monitaur.agent.latency.MonitorLatency;
import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

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
            return classfileBuffer;
        }
        try
        {

            CtMethod[] methods = cc.getDeclaredMethods();
            for (CtMethod method : methods)
            {
                try
                {
                    final MonitorLatency annotation = (MonitorLatency) method.getAnnotation(MonitorLatency.class);
                    if(annotation != null)
                    {
                        cc.addField(CtField.make("private static final ThreadLocal METHOD_DURATION = new ThreadLocal();", cc));

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
                return null;
            }
            catch (CannotCompileException cce)
            {
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
