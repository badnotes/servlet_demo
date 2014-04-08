package com.test.demo.servlet.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.test.demo.utils.DateUtil;
import com.test.demo.utils.RequestUtil;
import com.test.demo.utils.Result;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

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
	 * @since 1.0.0
	 */
	private static final long serialVersionUID = -2696929348482708541L;
    private static final String PACKAGE_NAME = "com.test.demo.servlet";
    private final Map<String,BaseServlet> servletMap;
    private final Map<String, java.lang.reflect.Method> methodMap;
    private static final Logger logger = Logger.getLogger(DispatchServlet.class);
    @Inject
    private Injector injector;

    DispatchServlet() {
        servletMap = new HashMap<String, BaseServlet>();
        methodMap = new HashMap<String, Method>();
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(PACKAGE_NAME.replace(".", "/"));
            while ( urls.hasMoreElements()){
                URL url = urls.nextElement();
                if(url != null){
                    String protocol = url.getProtocol();
                    if("file".equals(protocol)){
                        String uPath = url.getPath();
                        loadClazzList(clazzList, uPath, PACKAGE_NAME);
                    }
                }
            }
            for(Class<?> clazz: clazzList){
                BaseServlet instance = (BaseServlet)clazz.newInstance();
                Path annotation = clazz.getAnnotation(Path.class);
                if (annotation != null && annotation.value() != null){
                    Method[] methods = clazz.getMethods();
                    for (Method method: methods){
                        Path anno = method.getAnnotation(Path.class);
                        if(anno != null && anno.value() != null){
                            String s = trimPath(annotation.value());
                            String m = trimPath(anno.value());
                            servletMap.put(s + m,instance);
                            methodMap.put(s + m,method);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private static String trimPath(String path){
        String p = path.replaceAll("/+", "/");
        if(p.length() > 1 && p.endsWith("/")) {
            return p.substring(0, p.length() - 1);
        }
        return p;
    }

    private static void loadClazzList(List<Class<?>> clazzList, String uPath, String packageName){
        File packageDir = new File(uPath);
        if(packageDir.isDirectory()){
            getSubpackageClasses(clazzList,packageDir,packageName);
        }
    }

    private static void getSubpackageClasses(List<Class<?>> clazzList, File packageDir, String loadPackage) {
        File[] files = packageDir.listFiles();
        if(files == null){
            return;
        }
        for( File file: files){ // 只处理 packageName 当前目录
            if(file.isDirectory()){
                //String subPackage = loadPackage + "." + file;
                //getSubpackageClasses(clazzList, packageDir, subPackage);
            }else if(file.isFile() && file.getName().endsWith(".class")) {
                String fileName = file.getName();
                String className = loadPackage + "." + fileName.substring(0,fileName.indexOf("."));
                try {
                    Class<?> clazz = Class.forName(className);
                    if(clazz.getSuperclass().equals(BaseServlet.class)){ // instance of BaseServlet
                        clazzList.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
	public void doProcess(HttpServletRequest request,HttpServletResponse response,boolean isPost){

        // start time
        long startTime = System.currentTimeMillis();

        dumpParams(request);

        String uri = request.getRequestURI();
        String path = trimPath(uri);

        BaseServlet baseServlet = null;
        if(servletMap.containsKey(path)){
            baseServlet = servletMap.get(path);
        }
        Method method = null;
        if(methodMap.containsKey(path)){
            method = methodMap.get(path);
        }
        if(baseServlet == null || method == null){
            errorNoMethod(request,response,"no such method");
            return;
        }
        // invoke servlet
        try {
            Object res = method.invoke(baseServlet,request);
            if(res == null){
                res = Result.ERROR_NO_RESULT.toResult();
            }
            newResponse(startTime,request, response, res);
        }catch (Exception e) {
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
            Long uid = RequestUtil.getLong(request, "uid", 0L);
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
        logger.info("#######" + DateUtil.long2Date(System.currentTimeMillis()) + sb.toString() + " >>> result:" + result);
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
