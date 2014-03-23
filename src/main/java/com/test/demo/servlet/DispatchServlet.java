package com.test.demo.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fst.context.user.UserManager;
import com.fst.utils.DateUtil;
import com.fst.web.filter.AuthorityKeys;
import com.fst.web.utils.Action;
import com.fst.web.utils.Keys;
import com.fst.web.utils.RequestUtil;
import com.fst.web.utils.Result;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: 	DispatchServlet
 * @Description:dispatch servlet
 * @author 		xinge imxingge@gmail.com
 * @date 		2013-3-29 下午12:24:20
 *
 */
@Singleton
public class DispatchServlet extends HttpServlet{

	/**
	 * serialVersionUID:
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -2696929348482708541L;
    private static final String PACKAGE_NAME = "com.fst.servlet.";
    private static final Logger logger = Logger.getLogger(DispatchServlet.class);
    @Inject
    private Injector injector;
    @Inject
    private UserManager userManager;

	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response){
		doProcess(request, response,false);
    }

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response){
		doProcess(request, response,true);
	}

    /**
     * @param request: auth
     * @param request: op
     * @param request: uid
     * @param request: sid
     * @param request: page
     * @param request: count
     * @param response
     */
	public void doProcess(HttpServletRequest request,HttpServletResponse response,boolean ispost){

        // start time
        long startTime = System.currentTimeMillis();

        dumpParams(request);

        // check auth
		String auth = RequestUtil.getString(request, Keys.AUTH);
		if(auth == null || !AuthorityKeys.isExist(auth)){
            this.errorNoMethod(request, response,"auth");
			return ;
		}
		// op convert to action
		Integer op = RequestUtil.getInteger(request, Keys.ACTION_OP);
		if(op == null){
            errorNoMethod(request, response,"op");
			return ;
		}
        Action action = Action.findAction(op); // find method
        if(action == null){
            this.errorNoMethod(request, response,"action");
            return ;
        }
        // action convert to servlet
        String servletName = PACKAGE_NAME+action.getModel();
        BaseServlet servlet = null;
        try {
            servlet = (BaseServlet)injector.getInstance(Class.forName(servletName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(servlet == null){
            this.errorNoMethod(request, response,"servlet");
            return;
        }
        // find method
        Method method;
        try {
            method = MethodUtils.getMatchingAccessibleMethod(servlet.getClass(), action.getMethod(),
                    new Class[]{
                            HttpServletRequest.class, HttpServletResponse.class
                    });
        } catch (Exception e) {
            e.printStackTrace();
            this.errorNoMethod(request, response,"method exception");
            return ;
        }
        if(method == null){
            this.errorNoMethod(request, response,"method null ");
            return ;
        }
        // check method
        ServletApi anno = method.getAnnotation(ServletApi.class);
        if(anno != null && anno.method() != MethodType.ALL){
            if(anno.method() == MethodType.GET && ispost){
                this.errorNoMethod(request, response,"method post");
                return ;
            }
            if(anno.method() == MethodType.POST && !ispost){
                this.errorNoMethod(request, response,"method get");
                return ;
            }
        }
        Long uid = RequestUtil.getLong(request, Keys.ACTION_UID);
        // check login status
        if(anno == null || anno.login()){
            String session = RequestUtil.getString(request, Keys.ACTION_SESSION);
            if(uid == null || session == null){
                newResponse(startTime,request, response, Result.ERROR_PARAMS.toResult());
                return;
            }
            if(!userManager.contains(uid) || !session.trim().equals(userManager.get(uid).getSession())){
                newResponse(startTime,request, response, Result.ERROR_SESSION.toResult());
                logger.info("No this session .. ");
                return;
            }
        }
        // add user to online list
        if(uid != null){
            userManager.update(uid);
        }
        // check params
        Integer page = RequestUtil.getInteger(request, "page");
        Integer count = RequestUtil.getInteger(request,"count");
        if(page != null && page<1){
            newResponse(startTime,request, response, Result.ERROR_PARAMS.toResult("page is too small."));
            return;
        }
        if(count != null && count>500){
            newResponse(startTime,request, response, Result.ERROR_PARAMS.toResult("count is too large."));
            return;
        }
        // invoke servlet
        try {
            Object res = servlet.doProcess(request,response,action.getMethod());
            if(res == null){
                res = Result.ERROR_NO_RESULT.toResult();
            }
            newResponse(startTime,request, response, res);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            newResponse(startTime,request, response, Result.ERROR_NO_METHOD.toResult());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            newResponse(startTime,request, response, Result.ERROR_ACCESS.toResult());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            newResponse(startTime,request, response, Result.ERROR_INVOKE.toResult());
        } catch (Exception e) {
            e.printStackTrace();
            newResponse(startTime,request, response, Result.ERROR_UNKNOWN.toResult());
        }
    }

    public void errorNoMethod(HttpServletRequest request,HttpServletResponse response,String error){
        logger.info(error + " is error ... ");
        newResponse(System.currentTimeMillis(),request, response, Result.ERROR_NO_METHOD.toResult());
    }

	/**
	* @Description: response data to json
	 */
	public static void newResponse(long startTime,HttpServletRequest request,HttpServletResponse response,Object obj){
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
            // Object to json by fastJson  JSON.toJSONStringZ
			String strValue = JSON.toJSONString(obj, SerializeConfig.getGlobalInstance(), SerializerFeature.WriteNullListAsEmpty);
            Long uid = RequestUtil.getLong(request,"uid", 0L);
			pw.write(strValue);
            // end time
            long endTime = System.currentTimeMillis();
            dumpResult(uid,startTime,endTime,strValue);
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

	@Override
	public void init() throws ServletException {
		logger.debug("["+this.getClass() + "] has initialized ... ");
        this.initService();
    }

    /**
     * dump params
     */
    private static void dumpParams(HttpServletRequest request){
        Map<Object,Object> map = new HashMap<Object,Object>();
        map.putAll(request.getParameterMap());
        if(map.containsKey("image")){
            map.put("image","too long to hide.");
        }
        if(map.containsKey("photo")){
            map.put("photo","too long to hide.");
        }
        if(map.containsKey("voice")){
            map.put("voice","too long to hide.");
        }
        logger.info("####### "+ DateUtil.longToDate(System.currentTimeMillis())+" >>> params:"+JSON.toJSONString(map));
    }

    /**
     * dump result
     */
    private static void dumpResult(Long uid,long startTime,long endTime,String result){
        StringBuilder sb = new StringBuilder();
        sb.append("#uid:").append(uid);
        sb.append("#startTime:").append(startTime);
        sb.append("#endTime:").append(endTime);
        sb.append("#duration:").append(endTime-startTime);
        logger.info("#######"+ DateUtil.long2Date(System.currentTimeMillis())+sb.toString()+" >>> result:"+result);
    }

    public static Object setResult(HttpServletRequest request,Object object){
        Object o = request.getAttribute("__result__");
        request.setAttribute("__result__", o);
        return o;
    }

    public static Object getResult(HttpServletRequest request){
        return request.getAttribute("__result__");
    }

    public static void removeResult(HttpServletRequest request){
        request.getAttribute("__result__");
    }

    /**
     * @Description: init
     */
    @SuppressWarnings("rawtypes")
    protected void initService() {
        Class clazz = this.getClass();
        Field fields[] = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(FstInject.class)) {
                field.setAccessible(true);
                try {
                    field.set(this, injector.getInstance(field.getType()));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @Description: inject
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FstInject {
    }

}
