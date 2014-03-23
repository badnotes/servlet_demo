package com.test.demo.db;

import com.fst.cache.CacheManager;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: 	QueryHelper
 * @Description:
 * @author 		xinge imxingge@gmail.com
 * @date 		2013-3-27 下午3:23:06
 */

public class DBUtil {

    private final static Log log = LogFactory.getLog(DBUtil.class);
	private final static QueryRunner _g_runner = new QueryRunner();
	/**
	 *  ColumnListHandler：将结果集中某一列的数据存放到List中
	 */
	private final static ColumnListHandler<?> _g_columnListHandler = new ColumnListHandler<Object>(){
		@Override
		protected Object handleRow(ResultSet rs) throws SQLException {
			Object obj = super.handleRow(rs);
			if(obj instanceof BigInteger)
				return ((BigInteger)obj).longValue();
			return obj;
		}

	};
	/**
	 *  ScalarHandler：将结果集中某一条记录的其中某一列的数据存成Object
	 */
	private final static ScalarHandler<?> _g_scaleHandler = new ScalarHandler<Object>(){
		@Override
		public Object handle(ResultSet rs) throws SQLException {
			Object obj = super.handle(rs);
			if(obj instanceof BigInteger)
				return ((BigInteger)obj).longValue();
            if(obj instanceof BigDecimal){
                return ((BigDecimal)obj).longValue(); //
            }
			return obj;
		}
	};

	@SuppressWarnings("serial")
	private final static List<Class<?>> PrimitiveClasses = new ArrayList<Class<?>>(){{
		add(Long.class);
		add(Integer.class);
		add(String.class);
		add(java.util.Date.class);
		add(java.sql.Date.class);
		add(java.sql.Timestamp.class);
	}};

	private static boolean _IsPrimitive(Class<?> cls) {
		return cls.isPrimitive() || PrimitiveClasses.contains(cls) ;
	}

	/**
	 * 获取数据库连接
	 * @return
	 */
	public static Connection getConnection() {
        return getConnection(1);
    }
    public static Connection getConnection(Integer db) {
        try {
            return DBManager.getConnection(db);
        } catch (SQLException e) {
            throw  new DBException(e);
        }
    }

    /**
     * close dataSource
     */
    public static void closeAllDataSource(){
        DBManager.closeAllDataSource();
    }

	/**
	 * 关闭链接
	 */
    public static void closeConnection(){
        closeConnection(1);
    }
    public static void closeConnection(int db){
        try {
            DBManager.closeConnection(db);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

	/**
	 * get Object
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T get(Class<T> beanClass, String sql, Object...params)  {
        try {
            return (T)_g_runner.query(getConnection(), sql, _IsPrimitive(beanClass)?_g_scaleHandler:new BeanHandler(beanClass), params);
        } catch (SQLException e) {
            throw new DBException(e);
        } finally{
            closeConnection();
        }
    }

    /**
	 * get Object
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T get_cache(Class<T> beanClass, String cache, Serializable key, String sql, Object...params)  {
        T obj = (T)CacheManager.get(cache, key);
        if(obj == null){
            obj = get(beanClass, sql, params);
            CacheManager.set(cache, key, (Serializable)obj);
        }
        return obj;
    }

    /**
	 * get Object
	 * @param sql
	 * @param params
	 * @return
	 */
	public static <T> T get(ResultSetHandler<T> rsh, String sql, Object...params)  {
        try {
            return (T)_g_runner.query(getConnection(), sql,rsh, params);
        } catch (SQLException e) {
            throw new DBException(e);
        } finally{
            closeConnection();
        }
    }

	/**
	 * get list
	 * @param <T>
	 * @param beanClass
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> list(Class<T> beanClass, String sql, Object...params) {
        try {
            return (List<T>)_g_runner.query(getConnection(), sql, _IsPrimitive(beanClass)?_g_columnListHandler:new BeanListHandler(beanClass), params);
        } catch (SQLException e) {
            throw new DBException(e);
        } finally{
            closeConnection();
        }
    }

	/**
	 * get list
	 * @param <T>
	 * @param beanClass
	 * @param sql
	 * @param params
	 * @return
	 */
	public static <T> List<T> list(ResultSetHandler<List<T>> rsh, String sql, Object...params) {
        try {
            return (List<T>)_g_runner.query(getConnection(), sql,rsh, params);
        } catch (SQLException e) {
            throw new DBException(e);
        } finally{
            closeConnection();
        }
    }

	/**
	 * 分页查询
	 * @param <T>
	 * @param beanClass
	 * @param sql
	 * @param page
	 * @param count
	 * @param params
	 * @return
	 */
	public static <T> List<T> list(Class<T> beanClass, int page, int count, String sql, Object...params){
		if(page < 0 || count < 0)
			throw new IllegalArgumentException("Illegal parameter of 'page' or 'count', Must be positive.");
		int from = (page - 1) * count;
		count = (count > 0) ? count : Integer.MAX_VALUE;
		count = (count>100)?100:count;
		return list(beanClass, sql + " LIMIT ?,?", ArrayUtils.addAll(params, new Integer[]{from,count}));
	}

    /**
     * get Object from cache
     * @param sql
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> list_cache(Class<T> beanClass,String cache, Serializable cache_key, String sql, Object...params)  {
        List<T> list = (List<T>)CacheManager.get(cache, cache_key);
        if(list == null){
            list = list(beanClass, sql, params);
            CacheManager.set(cache, cache_key, (Serializable)list);
        }
        return list;
    }

    /**
     * get Object from cache
     * @param sql
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> list_cache(ResultSetHandler<List<T>> rsh, String cache, Serializable cache_key, String sql, Object...params)  {
        List<T> list = (List<T>)CacheManager.get(cache, cache_key);
        if(list == null) {
            list = list(rsh, sql, params);
            CacheManager.set(cache, cache_key, (Serializable) list);
        }
        return list;
    }

	/**
	 * 分页查询缓存
	 * @param <T>
	 * @param beanClass
	 * @param sql
	 * @param page
	 * @param count
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> list_cache(Class<T> beanClass,String cache, Serializable cache_key, int cache_obj_count,int page, int count, String sql, Object...params){
		List<T> list = (List<T>)CacheManager.get(cache, cache_key);
		if(list == null){
			list = list(beanClass,1, cache_obj_count, sql,  params);
			CacheManager.set(cache, cache_key, (Serializable)list);
		}
		if(list == null || list.size()==0)
			return list;
		int from = (page - 1) * count;
		if(from < 0)
			return null;
		if((from+count) > cache_obj_count)//超出缓存的范围
			return list(beanClass, page, count,sql,  params);
		int end = Math.min(from + count, list.size());
		if(from >= end)
			return null;
		return list.subList(from, end);
	}

	/**
	 * 分页查询
	 * @param <T>
	 * @param beanClass
	 * @param sql
	 * @param page
	 * @param count
	 * @param params
	 * @return
	 */
	public static <T> List<T> list(ResultSetHandler<List<T>> rsh, int page, int count, String sql, Object...params){
		if(page < 0 || count < 0)
			throw new IllegalArgumentException("Illegal parameter of 'page' or 'count', Must be positive.");
		int from = (page - 1) * count;
		count = (count > 0) ? count : Integer.MAX_VALUE;
		count = (count>100)?100:count;
		return list(rsh, sql + " LIMIT ?,?", ArrayUtils.addAll(params, new Integer[]{from,count}));
	}

	/**
	 * 分页查询缓存
	 * @param <T>
	 * @param beanClass
	 * @param sql
	 * @param page
	 * @param count
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> list_cache(ResultSetHandler<List<T>> rsh,String cache, Serializable cache_key,  int cache_obj_count, int page, int count, String sql, Object...params){
		List<T> list = (List<T>)CacheManager.get(cache, cache_key);
		if(list == null){
			list = list(rsh, 1, cache_obj_count, sql, params);
			CacheManager.set(cache, cache_key, (Serializable)list);
		}
		if(list == null || list.size()==0)
			return list;
		int from = (page - 1) * count;
		if(from < 0)
			return null;
		if((from+count) > cache_obj_count)//超出缓存的范围
			return list(rsh, page, count, sql, params);
		int end = Math.min(from + count, list.size());
		if(from >= end)
			return null;
		return list.subList(from, end);
	}

	/**
	 * 执行统计查询语句，语句的执行结果必须只返回一个数值
	 * @param sql
	 * @param params
	 * @return
	 */
	public static long stat(String sql, Object...params) {
        try {
            Number num  = (Number)_g_runner.query(getConnection(), sql, _g_scaleHandler, params);
            return (num!=null)?num.longValue():-1;
        } catch (SQLException e) {
            throw new DBException(e);
        } finally{
            closeConnection();
        }
	}

    public static long stat(boolean close, String sql, Object...params) {
        try {
            Number num  = (Number)_g_runner.query(getConnection(), sql, _g_scaleHandler, params);
            return (num!=null)?num.longValue():-1;
        } catch (SQLException e) {
            throw new DBException(e);
        } finally{
            if(close){
                closeConnection();
            }
        }
	}

	/**
	 * 保存返回主键
	 * @param sql
	 * @param params
	 * @return Long
	 */
	public static Long storeInfoAndGetGeneratedKey(String sql,Object...params){
		Long pk = 0L;
		Connection conn = getConnection();
		try {
			_g_runner.update(conn,sql,params);
			pk = (Long) _g_runner.query(conn,"SELECT LAST_INSERT_ID()", _g_scaleHandler);
		} catch (SQLException e) {
			throw new DBException(e);
		} finally{
            closeConnection();
        }
		return pk;
	}

    public static Long storeInfoAndGetGeneratedKey(boolean close, String sql,Object...params){
		Long pk = 0L;
		Connection conn = getConnection();
		try {
			_g_runner.update(conn,sql,params);
			pk = (Long) _g_runner.query(conn,"SELECT LAST_INSERT_ID()", _g_scaleHandler);
		} catch (SQLException e) {
			throw new DBException(e);
		} finally{
            if(close){
                closeConnection();
            }
        }
		return pk;
	}

	/**
	 * 执行INSERT/UPDATE/DELETE语句
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int update(String sql, Object...params) {
        return update(1,sql,params);
    }
    public static int update(Integer db, String sql, Object...params) {
        try {
            return _g_runner.update(getConnection(db), sql, params);
        } catch (SQLException e) {
            throw  new DBException(e);
        } finally{
            closeConnection(db);
        }
    }

    public static int update(boolean close, String sql, Object...params) {
        return update(close,1,sql,params);
    }
    public static int update(boolean close, Integer db, String sql, Object...params) {
        try {
            return _g_runner.update(getConnection(db), sql, params);
        } catch (SQLException e) {
            throw  new DBException(e);
        } finally{
            if(close){
                closeConnection(db);
            }
        }
    }

	/**
	 * 执行过程
	 * @param
	 * @param params
	 * @return
	 */
	public static Object call(String procedure,ResultSetHandler<?> rsh, Object...params) {
        try {
			return _g_runner.query(getConnection(), procedure, rsh, params);
        } catch (SQLException e) {
            throw  new DBException(e);
        } finally{
            closeConnection();
        }
    }

	/**
	 * 批量执行指定的SQL语句
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int[] batch(String sql, Object[][] params)  {
        try {
            return _g_runner.batch(getConnection(), sql, params);
        } catch (SQLException e) {
            throw  new DBException(e);
        } finally{
            closeConnection();
        }
    }

    public static int[] batch(boolean close,String sql, Object[][] params)  {
        try {
            return _g_runner.batch(getConnection(), sql, params);
        } catch (SQLException e) {
            throw  new DBException(e);
        } finally{
            if(close){
                closeConnection();
            }
        }
    }

    public static void beginTransaction() {
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw  new DBException(e);
        } finally{
            //
        }
    }

    public static void endTransaction() throws DBException{
        try {
            getConnection().commit();
            getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            throw new DBException(e);
        } finally{
            closeConnection();
        }
    }

    public static void rollback(Exception se){
        try {
            log.error("transaction rollback error ", se);
            getConnection().rollback();
            getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            log.error("rollback error ", e);
            throw new DBException(e);
        } finally{
            closeConnection();
        }
    }

}