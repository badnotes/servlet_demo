package com.test.demo.servlet.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.test.demo.utils.Result;
import org.apache.commons.beanutils.MethodUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * base servlet
 * User: xinge
 * Date: 14-3-17
 * Time: pm 9:46
 */
public abstract class BaseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req,resp,MethodType.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req,resp,MethodType.POST);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    public void process(HttpServletRequest request,HttpServletResponse response, MethodType mType){
        String uri = request.getRequestURI();
        String action = uri.substring(uri.lastIndexOf("/")+1);
        // find method
        Method method;
        try {
            method = MethodUtils.getMatchingAccessibleMethod(this.getClass(), action,
                    new Class[]{HttpServletRequest.class});
        } catch (Exception e) {
            e.printStackTrace();
            response(request, response, Result.ERROR_NO_METHOD.toResult());
            return ;
        }
        if (method == null){
            response(request, response, Result.ERROR_NO_METHOD.toResult());
            return;
        }
        // 注解默认为get,为all是可以通过所有方法,其他标注是必须匹配才行
        com.test.demo.servlet.base.Method m = method.getAnnotation(com.test.demo.servlet.base.Method.class);
        if(m == null && mType != MethodType.GET){
            response(request, response, Result.ERROR_NO_METHOD.toResult());
            return;
        }
        if(m!=null && m.value()!=MethodType.ALL && m.value()!=mType){
            response(request, response, Result.ERROR_NO_METHOD.toResult());
            return;
        }
        // invoke servlet
        try {
            Object res =  method.invoke(this,request);
            if(res == null){
                response(request, response, Result.ERROR_NO_RESULT.toResult());
            }
            response(request, response, res);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response(request, response, Result.ERROR_ACCESS.toResult());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response(request, response, Result.ERROR_INVOKE.toResult());
        } catch (Exception e) {
            e.printStackTrace();
            response(request, response, Result.ERROR_UNKNOWN.toResult());
        }
    }

    public static void response(HttpServletRequest request,HttpServletResponse response,Object obj){
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            // Object to json by fastJson  JSON.toJSONStringZ
            String strValue = JSON.toJSONString(obj, SerializeConfig.getGlobalInstance(), SerializerFeature.WriteNullListAsEmpty);
            pw.write(strValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if(pw != null){
                pw.flush();
                pw.close();
            }
        }
    }

    public Object doProcess(HttpServletRequest request, HttpServletResponse response){
        return null;
    }
}
