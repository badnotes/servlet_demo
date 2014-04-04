package com.test.demo.servlet;


import com.google.inject.Singleton;
import com.test.demo.servlet.base.BaseServlet;
import com.test.demo.servlet.base.Method;
import com.test.demo.servlet.base.MethodType;
import com.test.demo.servlet.base.Path;
import com.test.demo.utils.RequestUtil;
import com.test.demo.utils.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * hello servlet
 * User: xinge
 * Date: 14-3-25
 * Time: pm 10:08
 */
@Path("/hello")
@Singleton
public class HelloServlet extends BaseServlet {

    @Path("/test")
    public Object testGet(HttpServletRequest request) {
        String name = RequestUtil.getString(request, "name","anonymous user");
        return Result.build(Result.SUCCESS, "get," + name);
    }

    @Method(MethodType.POST)
    public Object testPost(HttpServletRequest request) {
        String name = RequestUtil.getString(request, "name", "anonymous user");
        return Result.build(Result.SUCCESS,"post," + name);
    }

    @Method(MethodType.ALL)
    public Object testAll(HttpServletRequest request) {
        String name = RequestUtil.getString(request, "name","anonymous user");
        return Result.build(Result.SUCCESS,"all," + name);
    }
}
