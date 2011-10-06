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
package com.epickrram.monitaur.server.servlet;

import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.server.Context;
import com.epickrram.monitaur.server.MonitorDataStore;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class JsonMonitorDataServlet extends MonitaurServlet
{
    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        final Context context = getContext(request);
        final Gson gson = new Gson();
        final MonitorDataStore monitorDataStore = context.getMonitorDataStore();
        final Set<String> logicalNames = monitorDataStore.getLogicalNames();
        final Map<String, List<MonitorData>> monitorData = new HashMap<String, List<MonitorData>>();

        for (String logicalName : logicalNames)
        {
            monitorData.put(logicalName, monitorDataStore.getMonitorDataByLogicalName(logicalName));
        }

        response.setContentType("text/json");
        final String json = gson.toJson(monitorData);
        response.setContentLength(json.length());
        response.getWriter().append(json);
        response.flushBuffer();
    }
}