package com.test.demo.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: 	Result
 * @Description:action result
 * @author 		xinge imxingge@gmail.com
 * @date 		2013-3-26 上午10:04:06
 *
 */
public enum Result {
	SUCCESS(			200,"成功"),
	NEW_VERSION(		201,"有可更新版本"),
	ERROR_EXECUTE(		300,"业务处理失败"),
	ERROR_API_VERSION(	301,"接口版本过低"),
	ERROR_PARAMS(		400,"请求参数错误"),
	ERROR_ACCESS(		401,"未授权，没有访问权限"),
	ERROR_MODE(			402,"禁止访问，必须在特定方式下访问"),
	ERROR_NO_RESULT(	403,"nil"),
	ERROR_NO_METHOD(	404,"未找到，没有该接口"),
	ERROR_METHOD(		405,"方法未允许"),
	ERROR_SESSION(		406,"登录超时,请重新登录"),
	ERROR_INVOKE(		500,"系统错误,请稍后再试."),
	ERROR_UNKNOWN(		501,"未知错误");

	public static final String CODE = "code";
	public static final String MSG = "msg";
	public static final String DATA = "data";

	public final int code;
	public final String msg;

	Result(int code,String msg){
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Map<String,Object> toResult(Object obj){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE,code);
		map.put(MSG,msg);
		return map;
	}

	public Map<String,Object> toResult(){
		Object obj = new Object();
		return this.toResult(obj);
	}

	public Map<String,Object> toResult(Object obj,String newMsg){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE,code);
		map.put(MSG,newMsg);
		return map;
	}

	public Map<String,Object> toResult(String newMsg){
		Object obj = new Object();
		return this.toResult(obj, newMsg);
	}

	public <T> Map<String,Object> makeResult(T data){
		Map<String,Object> result = this.toResult();
		result.put(DATA, data);
		return result;
	}

	public <T> Map<String,Object> makeResult(T data,String newMsg){
		Map<String,Object> result = this.toResult(newMsg);
		result.put(DATA, data);
		return result;
	}

	/**
	 *
	* @Description: find result
	 */
	public static Result findResult(int code){
		Result[] r = Result.values();
		for (Result result : r) {
			if(result.code == code)
				return result;
		}
		return null;
	}

	public static <T> Object build(Result result,T data){
		return result.makeResult(data);
	}

	public static <T> Object build(Result result,T data,String newMsg){
		return result.makeResult(data,newMsg);
	}

	/*public static <T> Object build(Result result,List<T> data){
		ResultArray array = new ResultArray();
        if (data != null) {
            Iterator<T> iter = data.iterator();
            while (iter.hasNext()) {
            	array.put(new Object(iter.next()));
            }
        }
		return result.makeResult(array);
	}*/

	public boolean equal(Object o){
		if(o == null){
			return false;
		}
		if(o instanceof Result){
			return code == ((Result)o).code;
		}
		if(o instanceof Integer){
			return code == ((Integer)o).intValue();
		}
		if(o instanceof Long){
			return code == ((Long)o).longValue();
		}
		return false;
	}


}
