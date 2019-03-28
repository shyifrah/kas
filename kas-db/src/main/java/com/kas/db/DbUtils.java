package com.kas.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.utils.StringUtils;

/**
 * A set of DB-related utility functions
 * 
 * @author Pippo
 */
public class DbUtils
{
  static final public String cDbTypeMySql      = "mysql";
  static final public String cDbTypePostgreSql = "postgresql";
  
  /**
   * Logger
   */
  static private Logger sLogger = LogManager.getLogger(DbUtils.class);
  
  /**
   * Create a connection URL
   * 
   * @param dbtype
   *   The database type
   * @param host
   *   The database host name
   * @param port
   *   The database listening port
   * @param schema
   *   The schema used
   * @param user
   *   The database user's name
   * @param pswd
   *   The database user's password
   * @return
   *   the connection URL
   */
  static public String createConnUrl(String dbtype, String host, int port, String schema, String user, String pswd)
  {
    sLogger.trace("DbUtils::createConnUrl() - IN");
    
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("jdbc:%s://%s:%d/%s?user=%s&password=%s", dbtype, host, port, schema, user, pswd));
    
    switch (dbtype)
    {
      case cDbTypeMySql:
        sb.append("&serverTimezone=UTC&useSSL=false");
        break;
      case cDbTypePostgreSql:
        sb.append("&ssl=false");
        break;
    }
    
    String curl = sb.toString();
    sLogger.trace("DbUtils::createConnUrl() - OUT, Returns=[" + curl + "]");
    return curl;
  }
  
  /**
   * Initialize KAS/MQ database schema
   */
  static public void initSchema()
  {
    sLogger.trace("DbUtils::initSchema() - IN");
    
    DbConnectionPool pool = DbConnectionPool.getInstance();
    DbConnection dbConn = pool.allocate();
    
    SchemaHelper schema = new SchemaHelper(dbConn.getConn());
    boolean exists = schema.isExist();
    sLogger.debug("DbUtils::initSchema() - Check if schema exists: " + exists);
    if (!exists)
    {
      sLogger.debug("DbUtils::initSchema() - Schema does not exist, initializing it");
      schema.init();
    }
    pool.release(dbConn);
    
    sLogger.trace("DbUtils::initSchema() - OUT");
  }
  
  /**
   * Execute a SQL statement using the specified connection
   * 
   * @param conn
   *   The {@link Connection} that will be used
   * @param sql
   *   The SQL statement
   * @return
   *   a result set, if one was generated
   * @throws SQLException
   *   if thrown by java.sql.* classes
   */
  static public ResultSet execute(Connection conn, String sql) throws SQLException
  {
    return execute(conn, sql, new Object [] {});
  }
  
  /**
   * Execute a SQL statement using the specified connection
   * 
   * @param conn
   *   The {@link Connection} that will be used
   * @param fmt
   *   The SQL statement base format
   * @param args
   *   Arguments to format the SQL
   * @return
   *   a result set, if one was generated
   * @throws SQLException
   *   if thrown by java.sql.* classes
   */
  static public ResultSet execute(Connection conn, String fmt, Object ... args) throws SQLException
  {
    sLogger.trace("DbUtils::execute() - IN");
    
    String sql = String.format(fmt, args);
    ResultSet result = null;

    sql = StringUtils.clearBlanks(sql);
    sLogger.trace("DbUtils::execute() - Executing SQL: [" + sql + "]");
    PreparedStatement st = conn.prepareStatement(sql);
    boolean b = st.execute();
    if (b) result = st.getResultSet();
    sLogger.trace("DbUtils::execute() - HaveResult: [" + b + "]");
    
    sLogger.trace("DbUtils::execute() - OUT");
    return result;
  }
}
