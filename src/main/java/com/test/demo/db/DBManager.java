package com.test.demo.db;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * database manager
 * @author wanjun
 */
public class DBManager {

	private final static Log log = LogFactory.getLog(DBManager.class);
    private final static Map<Integer,DataSource> dataSourceMap = new HashMap<Integer,DataSource>();
    private final static Map<Integer,ThreadLocal<Connection>> connMap = new HashMap<Integer,ThreadLocal<Connection>>();
    private static boolean show_sql = false;

	static {
        connMap.put(1, new ThreadLocal<Connection>());
        connMap.put(2, new ThreadLocal<Connection>());
        connMap.put(3, new ThreadLocal<Connection>());
        //initDataSource(1, "/db.properties");
        //initDataSource(2, "/db2.properties");
        //initDataSource(3, "/db3.properties");
        initDataSource(1, "/druid1.properties");
        initDataSource(2, "/druid2.properties");
	}

	/**
	 * init database
	 * @param props
	 * @param show_sql
	 */
	private static void initDataSource(int db,String dbConfigFile) {
		log.info(" init << "+db+" >> the database ... ");
		try {
            Properties dbProperties = new Properties();
            dbProperties.load(DBManager.class.getResourceAsStream(dbConfigFile));
			Properties cp_props = new Properties();
			for (Object key : dbProperties.keySet()) {
				String sKey = (String) key;
				if (sKey.startsWith("jdbc.")) {
					String name = sKey.substring(5);
					cp_props.put(name, dbProperties.getProperty(sKey));
					if ("show_sql".equalsIgnoreCase(name)) {
						show_sql = "true".equalsIgnoreCase(dbProperties.getProperty(sKey));
					}
				}
			}
            DataSource ds = (DataSource) Class.forName(cp_props.getProperty("datasource")).newInstance();
            if (ds.getClass().getName().indexOf("c3p0") > 0) {
                // Disable JMX in C3P0
                System.setProperty(
                        "com.mchange.v2.c3p0.management.ManagementCoordinator",
                        "com.mchange.v2.c3p0.management.NullManagementCoordinator");
            }
            /*Class clazz = Class.forName(cp_props.getProperty("datasource"));
            Method method = clazz.getMethod("init", new Class[]{});
            method.invoke(ds);*/
            BeanUtils.populate(ds, cp_props);
            log.info("Using DataSource : " + ds.getClass().getName());
            dataSourceMap.put(db, ds);
            Connection conn = getConnection(db);
            DatabaseMetaData mdm = conn.getMetaData();
            log.info("Connected to " + mdm.getDatabaseProductName() + " " + mdm.getDatabaseProductVersion());
            closeConnection(db);
        } catch (Exception e) {
			log.error(" Database << "+db+" >> initial error ...  ", e);
			throw new DBException(e);
		}
	}

    public static void closeAllDataSource(){
        closeDataSource(1);
        closeDataSource(2);
        //closeDataSource(3);
    }

	/**
	 * close dataSource
	 */
	public static void closeDataSource(int db) {
		try {
            DataSource dataSource = dataSourceMap.get(db);
            if(dataSource != null){
                dataSource.getClass().getMethod("close").invoke(dataSource);
            }
            else {
                log.error("datasource <<"+db+">> is null.");
            }
		} catch (NoSuchMethodException e) {
            e.printStackTrace();
		} catch (Exception e) {
			log.error("Unabled to destroy DataSource!!! ", e);
		}
	}

	/**
	 * get connection
	 */
	public static Connection getConnection() throws SQLException {
		return getConnection(1);
	}

	/**
	 * get connection
	 */
	public static Connection getConnection(Integer db) throws SQLException {
        DataSource dataSource = dataSourceMap.get(db);
        ThreadLocal<Connection> conns = connMap.get(db);
        Connection conn = conns.get();
        if (conn == null || conn.isClosed()) {
            conn = dataSource.getConnection();
            conns.set(conn);
        }
        return (show_sql && !Proxy.isProxyClass(conn.getClass())) ? new _DebugConnection(
                conn).getConnection() : conn;
	}

	/**
	 * close connection
	 */
	public static void closeConnection() throws SQLException{
		closeConnection(1);
	}

    /**
     * close connection
     */
    public static void closeConnection(Integer db) throws SQLException{
        ThreadLocal<Connection> conns = connMap.get(db);
        Connection conn = conns.get();
		try {
			if (conn != null && !conn.isClosed()) {
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (SQLException e) {
			log.error("Unabled to close connection!!! ", e);
			throw new SQLException(e);
		}
        conns.set(null);
	}

	/**
	 * debug
	 */
    private	static class _DebugConnection implements InvocationHandler {

		private final static Log log = LogFactory.getLog(_DebugConnection.class);

		private Connection conn = null;

		public _DebugConnection(Connection conn) {
			this.conn = conn;
		}

		/**
		 * Returns the conn.
		 * @return Connection
		 */
		public Connection getConnection() {
			return (Connection) Proxy.newProxyInstance(conn.getClass()
					.getClassLoader(), conn.getClass().getInterfaces(), this);
		}
		public Object invoke(Object proxy, Method m, Object[] args)
				throws Throwable {
			try {
				String method = m.getName();
				if ("prepareStatement".equals(method) || "createStatement".equals(method)){
					log.info("[SQL] >>> " + args[0]);
                }
				return m.invoke(conn, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
	}

}
