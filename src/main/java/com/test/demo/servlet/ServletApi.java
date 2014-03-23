package com.test.demo.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: 	ServletApi
 * @Description:api
 * @author 		xinge imxingge@gmail.com
 * @date 		2013-3-26 上午11:41:08
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ServletApi {

	boolean login() default true;

	MethodType method() default MethodType.ALL;

	float minVersion() default 0.0f;

}
