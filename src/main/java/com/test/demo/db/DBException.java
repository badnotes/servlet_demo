package com.test.demo.db;

import java.sql.SQLException;

/**
 * @Description:DBException
 * @author 		xinge imxingge@gmail.com
 * @date 		2013-3-27 下午4:31:32
 *
 */
public class DBException extends RuntimeException{

	/**
	 * serialVersionUID:
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -2145127894221710462L;

	public DBException(SQLException e){
		super(e);
	}
	public DBException(Exception e){
		super(e);
	}


}
