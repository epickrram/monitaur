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
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class must be run in a JVM launched with the following args:
 * -Dcom.epickrram.monitaur.latency.LatencyPublisherClass=com.epickrram.monitaur.agent.instrumentation.RecordingLatencyPublisher
 * -Xbootclasspath/a:./lib/dist/*
 * -javaagent:./build/dist/monitaur-0.1.jar
 */
public final class TransformerTest
{
    //-Dcom.epickrram.monitaur.latency.LatencyPublisherClass=com.epickrram.monitaur.agent.instrumentation.RecordingLatencyPublisher -Xbootclasspath/a:./lib/dist/* -javaagent:./build/dist/monitaur-0.1.jar
    private static final long LATENCY_THRESHOLD_MILLIS = 10L;

    private LatencyTestObject latencyTestObject;
    private RecordingLatencyPublisher latencyPublisher;

    @Test
    public void shouldNotPublishLatencyFiguresIfMethodLatencyUnderThreshold() throws Exception
    {
        latencyTestObject.setMethodInvocationDelayMillis(1L);

        latencyTestObject.invoke();
        latencyTestObject.invoke();
        latencyTestObject.invoke();
        latencyTestObject.invoke();

        assertThat(latencyPublisher.getCapturedLatencyList().size(), is(0));
    }

    @Test
    public void shouldPublicLatencyFiguresIfMethodLatencyOverThreshold() throws Exception
    {
        final long expectedMethodLatency = LATENCY_THRESHOLD_MILLIS + 5L;
        latencyTestObject.setMethodInvocationDelayMillis(expectedMethodLatency);

        latencyTestObject.invoke();
        latencyTestObject.invoke();
        latencyTestObject.invoke();
        latencyTestObject.invoke();

        assertThat(latencyPublisher.getCapturedLatencyList().size(), is(4));
        assertThat(latencyPublisher.getCapturedLatencyList().get(0), is(capturedLatency("com.epickrram.monitaur.agent.instrumentation.TransformerTest$LatencyTestObject", "invoke", expectedMethodLatency)));
        assertThat(latencyPublisher.getCapturedLatencyList().get(1), is(capturedLatency("com.epickrram.monitaur.agent.instrumentation.TransformerTest$LatencyTestObject", "invoke", expectedMethodLatency)));
        assertThat(latencyPublisher.getCapturedLatencyList().get(2), is(capturedLatency("com.epickrram.monitaur.agent.instrumentation.TransformerTest$LatencyTestObject", "invoke", expectedMethodLatency)));
        assertThat(latencyPublisher.getCapturedLatencyList().get(3), is(capturedLatency("com.epickrram.monitaur.agent.instrumentation.TransformerTest$LatencyTestObject", "invoke", expectedMethodLatency)));
    }

    public TransformerTest()
    {
        System.err.println("Test Constructed");
    }

    @Before
    public void setUp() throws Exception
    {
        System.err.println("setUp()");
        latencyTestObject = new LatencyTestObject();
        latencyPublisher = (RecordingLatencyPublisher) Transformer.getLatencyPublisherExposedForTesting();
        System.err.println("/setUp()");
    }

    private static Matcher<CapturedLatency> capturedLatency(final String className,
                                                            final String methodId,
                                                            final long minimumMethodLatency)
    {
        return new TypeSafeMatcher<CapturedLatency>()
        {
            @Override
            public boolean matchesSafely(final CapturedLatency capturedLatency)
            {
                return capturedLatency.getClassName().equals(className) &&
                        capturedLatency.getMethodId().equals(methodId) &&
                        capturedLatency.getLatencyMillis() >= minimumMethodLatency;
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText(String.format("captured latency with className %s, methodId %s, latency (at least) %dms",
                        className, methodId, minimumMethodLatency));
            }
        };
    }

    private static final class LatencyTestObject
    {
        private long methodInvocationDelayMillis = 0L;

        @MonitorLatency(durationThresholdMillis = LATENCY_THRESHOLD_MILLIS)
        private void invoke()
        {
            System.err.println("invoke() called");
            if(methodInvocationDelayMillis != 0)
            {
                try
                {
                    Thread.sleep(methodInvocationDelayMillis);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException("Insomnia", e);
                }
            }
            System.err.println("invoke() complete");
        }

        private void setMethodInvocationDelayMillis(final long methodInvocationDelayMillis)
        {
            this.methodInvocationDelayMillis = methodInvocationDelayMillis;
        }
    }
}