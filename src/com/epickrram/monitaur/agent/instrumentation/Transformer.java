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
import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public final class Transformer implements ClassFileTransformer
{
    private static LatencyPublisher latencyPublisherExposedForTesting;

    private final LatencyPublisher latencyPublisher;

    public Transformer(final LatencyPublisher latencyPublisher)
    {
        this.latencyPublisher = latencyPublisher;
        latencyPublisherExposedForTesting = latencyPublisher;
    }

    public synchronized byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                                         final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException
    {
        if(className.startsWith("java"))
        {
            return classfileBuffer;
        }
        final ClassPool classPool = new ClassPool(ClassPool.getDefault());
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
            boolean classWasInstrumented = false;
            CtMethod[] methods = cc.getDeclaredMethods();
            for (CtMethod method : methods)
            {
                try
                {
                    final MonitorLatency annotation = (MonitorLatency) method.getAnnotation(MonitorLatency.class);
                    if (annotation != null)
                    {
                        classWasInstrumented = true;

                        System.err.println("Instrumenting method " + className + "." + method.getName());
                        addTiming(cc, className.replace('/', '.'), method.getName(), method.getParameterTypes(), annotation.durationThresholdMillis());
                        //                        System.err.println("About to add field..");
                        ////                        cc.addField(CtField.make("private static final ThreadLocal METHOD_DURATION = new ThreadLocal();", cc));
                        //                        System.err.println("Added field");
                        ////                        final String before = "{METHOD_DURATION.set(Long.valueOf(System.nanoTime()));}";
                        //                        final String before = "{System.err.println(\"foo\");}";
                        //                        method.insertBefore(before);
                        //                        final String after = "{final long durationNanos = System.nanoTime() - ((Long) METHOD_DURATION.get()).longValue();\n" +
                        //                                "com.epickrram.monitaur.agent.instrumentation.Transformer.reportMethodDuration(\"" + className.replace('/', '.') + "\", \"" + method.getName() + "\", durationNanos, " + annotation.durationThresholdMillis() + "L);}";
                        ////                        method.insertAfter(after, true);
                        //                        System.err.println("Before:\n" + before);
                        //                        System.err.println("After:\n" + after);
                        System.err.println("Wrapped call");
                    }
                }
                catch (CannotCompileException cce)
                {
                    System.err.println("CannotCompileException: " + cce.getMessage() + "; instrumenting method " + method.getLongName() + "; method will not be instrumented");
                    cce.printStackTrace();
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
            // return the new bytecode array:
            byte[] newClassfileBuffer = null;
            if (classWasInstrumented)
            {
                System.err.println("Attempting compile of instrumented code");
            }
            try
            {
                newClassfileBuffer = cc.toBytecode();
                if (classWasInstrumented)
                {
                    System.err.println("Returned instrumented class");
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
                return null;
            }
            catch (CannotCompileException cce)
            {
                cce.printStackTrace();
                return null;
            }
            return newClassfileBuffer;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return classfileBuffer;
    }

    private static void addTiming(final CtClass ctClass, final String className, final String methodName,
                                  final CtClass[] parameterTypes, final long latencyThresholdMillis) throws NotFoundException, CannotCompileException
    {

        //  get the method information (throws exception if method with
        //  given name is not declared directly by this class, returns
        //  arbitrary choice if more than one with the given name)
        CtMethod mold = ctClass.getDeclaredMethod(methodName, parameterTypes);

        //  rename old method to synthetic name, then duplicate the
        //  method with original name for use as interceptor
        String nname = methodName + "$impl";
        mold.setName(nname);
        System.err.println("Copying method");
        CtMethod mnew = CtNewMethod.copy(mold, methodName, ctClass, null);

        //  start the body text generation by saving the start time
        //  to a local variable, then call the timed method; the
        //  actual code generated needs to depend on whether the
        //  timed method returns a value
        String type = mold.getReturnType().getName();
        StringBuffer body = new StringBuffer();
        body.append("{\nfinal long start = System.currentTimeMillis();\n");
        if (!"void".equals(type))
        {
            body.append(type + " result = ");
        }
        body.append(nname + "($$);\n");

        //  finish body text generation with call to print the timing
        //  information, and return saved value (if not void)
        body.append(Transformer.class.getName()).append(".reportMethodDuration(\"").append(className).
                append("\", \"").append(methodName).append("\", System.currentTimeMillis() - start, ").
                append(latencyThresholdMillis).append("L);");
        if (!"void".equals(type))
        {
            body.append("return result;\n");
        }
        body.append("}");

        //  replace the body of the interceptor method with generated
        //  code block and add it to class
        System.err.println("Setting method body:\n" + body.toString());
        try
        {
            mnew.setBody(body.toString());
        }
        catch (Throwable t)
        {
            System.err.println("Caught exception");
            t.printStackTrace(System.err);
        }
        System.err.println("Adding new method");
        ctClass.addMethod(mnew);

        //  print the generated code block just to show what was done
        System.out.println("Interceptor method body:");
        System.out.println(body.toString());
    }

    public static void reportMethodDuration(final String className, final String methodId, final long durationMillis, final long thresholdMillis)
    {
        if (durationMillis > thresholdMillis)
        {
            latencyPublisherExposedForTesting.onCapturedLatency(className, methodId, durationMillis);
        }
    }

    static LatencyPublisher getLatencyPublisherExposedForTesting()
    {
        return latencyPublisherExposedForTesting;
    }
}
