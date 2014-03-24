package com.test.demo.servlet;

import com.google.inject.Singleton;
import com.test.demo.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * hello
 * User: xinge
 * Date: 14-3-17
 * Time: pm 10:08
 */
@Singleton
public class HelloServlet extends BaseServlet {

    public Object test(HttpServletRequest request) {
        String name = RequestUtil.getString(request, "name","anonymous user");
        return "hi," + name;
    }
}
