package com.epickrram.monitaur.server.servlet;

import com.epickrram.monitaur.server.Context;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public abstract class MonitaurServlet extends HttpServlet
{
    protected Context getContext(final HttpServletRequest request)
    {
        return (Context) request.getAttribute(Context.REQUEST_ATTRIBUTE_KEY);
    }
}
