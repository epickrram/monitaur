package com.epickrram.monitaur.server;

import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.domain.MonitorType;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class MonitorDataStoreTest
{

    private static final int MAX_HISTORY_SIZE = 2;
    private MonitorDataStore monitorDataStore;

    @Test
    public void shouldStoreSingleValue() throws Exception
    {
        final String logicalName = "one";
        final String host = "host1";
        monitorDataStore.onMonitorData(monitorData(logicalName, host));

        assertThat(1, is(monitorDataStore.getMonitorDataByLogicalName(logicalName).size()));
        assertThat(host, equalTo(monitorDataStore.getMonitorDataByLogicalName(logicalName).get(0).getHost()));
    }

    @Test
    public void shouldStoreMultipleValuesForLogicalNameInReceivedOrder() throws Exception
    {
        final String logicalName1 = "one";
        final String host1 = "host1";
        monitorDataStore.onMonitorData(monitorData(logicalName1, host1));

        final String host2 = "host2";
        monitorDataStore.onMonitorData(monitorData(logicalName1, host2));

        assertThat(2, is(monitorDataStore.getMonitorDataByLogicalName(logicalName1).size()));
        assertThat(host1, equalTo(monitorDataStore.getMonitorDataByLogicalName(logicalName1).get(0).getHost()));
        assertThat(host2, equalTo(monitorDataStore.getMonitorDataByLogicalName(logicalName1).get(1).getHost()));
    }

    @Test
    public void shouldStoreValuesForMultipleLogicalNames() throws Exception
    {
        final String logicalName1 = "one";
        final String host1 = "host1";
        monitorDataStore.onMonitorData(monitorData(logicalName1, host1));

        final String logicalName2 = "two";
        final String host2 = "host2";
        monitorDataStore.onMonitorData(monitorData(logicalName2, host2));

        assertThat(1, is(monitorDataStore.getMonitorDataByLogicalName(logicalName1).size()));
        assertThat(1, is(monitorDataStore.getMonitorDataByLogicalName(logicalName2).size()));
        assertThat(host1, equalTo(monitorDataStore.getMonitorDataByLogicalName(logicalName1).get(0).getHost()));
        assertThat(host2, equalTo(monitorDataStore.getMonitorDataByLogicalName(logicalName2).get(0).getHost()));
    }

    @Test
    public void shouldReturnSetOfLogicalNames() throws Exception
    {
        final String logicalName1 = "one";
        monitorDataStore.onMonitorData(monitorData(logicalName1, "host1"));

        final String logicalName2 = "two";
        monitorDataStore.onMonitorData(monitorData(logicalName2, "host2"));

        assertThat(asSet(logicalName1, logicalName2), equalTo(monitorDataStore.getLogicalNames()));
    }

    @Test
    public void shouldReturnEmptyListIfLogicalNameDoesNotExist() throws Exception
    {
        assertThat(Collections.<MonitorData>emptyList(), equalTo(monitorDataStore.getMonitorDataByLogicalName("foo")));
    }

    @Test
    public void shouldNotStoreMoreThanMaxHistorySizePerLogicalName() throws Exception
    {
        final String logicalName1 = "one";
        final String host1 = "host1";
        monitorDataStore.onMonitorData(monitorData(logicalName1, host1));

        final String host2 = "host2";
        monitorDataStore.onMonitorData(monitorData(logicalName1, host2));

        final String host3 = "host3";
        monitorDataStore.onMonitorData(monitorData(logicalName1, host3));

        assertThat(2, is(monitorDataStore.getMonitorDataByLogicalName(logicalName1).size()));
        assertThat(host2, equalTo(monitorDataStore.getMonitorDataByLogicalName(logicalName1).get(0).getHost()));
        assertThat(host3, equalTo(monitorDataStore.getMonitorDataByLogicalName(logicalName1).get(1).getHost()));
    }

    @Before
    public void setUp()
    {
        monitorDataStore = new MonitorDataStore(MAX_HISTORY_SIZE);
    }

    private static <T> Set<T> asSet(final T... setValues)
    {
        final Set<T> set = new HashSet<T>();
        Collections.addAll(set, setValues);
        return set;
    }

    private MonitorData monitorData(final String logicalName, final String host)
    {
        return new MonitorData(MonitorType.SCALAR, logicalName, host, new Object(), System.currentTimeMillis());
    }
}