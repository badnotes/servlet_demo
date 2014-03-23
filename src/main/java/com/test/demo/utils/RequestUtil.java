package com.test.demo.utils;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @Description:request util
 * @author 		xinge imxingge@gmail.com
 * @date 		2013-3-28 下午5:37:03
 *
 */
public class RequestUtil {

	public static String getString(HttpServletRequest request,String name){
		String value = request.getParameter(name);
		if(value == null || "".equals(value.trim())){
			return null;
		}
		return value;
	}
	public static String getString(HttpServletRequest request,String name,String defVal){
		String value = request.getParameter(name);
		if(value == null){
			return defVal;
		}
		return value;
	}
	public static Integer getInteger(HttpServletRequest request,String name){
		String value = request.getParameter(name);
		if(value == null|| "".equals(value.trim())){
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Integer getInteger(HttpServletRequest request,String name,Integer defVal){
		String value = request.getParameter(name);
		if(value == null || "".equals(value.trim())){
			return defVal;
		}try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return defVal;
	}
	public static Long getLong(HttpServletRequest request,String name){
		String value = request.getParameter(name);
		if(value == null || "".equals(value.trim())){
			return null;
		}
		try {
			return Long.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Long getLong(HttpServletRequest request,String name,Long defVal){
		String value = request.getParameter(name);
		if(value == null|| "".equals(value.trim())){
			return defVal;
		}try {
			return Long.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return defVal;
	}
	public static Boolean getBoolean(HttpServletRequest request,String name){
		String value = request.getParameter(name);
		if(value == null|| "".equals(value.trim())){
			return null;
		}
		try {
			return Boolean.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Boolean getBoolean(HttpServletRequest request,String name,Boolean defVal){
		String value = request.getParameter(name);
		if(value == null|| "".equals(value.trim())){
			return defVal;
		}try {
			return Boolean.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return defVal;
	}
	public static Float getFloat(HttpServletRequest request,String name){
		String value = request.getParameter(name);
		if(value == null|| "".equals(value.trim())){
			return null;
		}
		try {
			return Float.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Float getFloat(HttpServletRequest request,String name,Float defVal){
		String value = request.getParameter(name);
		if(value == null|| "".equals(value.trim())){
			return defVal;
		}try {
			return Float.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return defVal;
	}
	public static Double getDouble(HttpServletRequest request,String name){
		String value = request.getParameter(name);
		if(value == null|| "".equals(value.trim())){
			return null;
		}
		try {
			return Double.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Double getDouble(HttpServletRequest request,String name,Double defVal){
		String value = request.getParameter(name);
		if(value == null|| "".equals(value.trim())){
			return defVal;
		}try {
			return Double.valueOf(value.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return defVal;
	}
	public static <T> T populate(T object, HttpServletRequest request){
		@SuppressWarnings("rawtypes")
		Map params = request.getParameterMap();
		try {
			BeanUtils.populate(object, params);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return object;
	}
	public static <T> T populate(Class<T> clazz,HttpServletRequest request){
		try {
			T obj = clazz.newInstance();
			return populate(obj,request);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
