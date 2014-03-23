package com.test.demo.filter;

import com.google.inject.Singleton;

import javax.servlet.*;
import java.io.IOException;

/**
 * @Description: EncodingFilter
 * @author xinge imxingge@gmail.com
 * @date 2013-3-26 上午10:50:31
 *
 */
@Singleton
public class EncodingFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		arg0.setCharacterEncoding("UTF-8");
		arg1.setCharacterEncoding("UTF-8");
		arg2.doFilter(arg0, arg1);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
