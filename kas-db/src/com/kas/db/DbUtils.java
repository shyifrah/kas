package com.kas.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.Properties;
import com.kas.infra.base.PropertyResolver;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * A set of DB-related utility functions
 * 
 * @author Pippo
 */
public class DbUtils
{
  static private ILogger sLogger = LoggerFactory.getLogger(DbUtils.class);
  
  /**
   * Create a connection URL
   * 
   * @param dbtype The database type
   * @param host The database host name
   * @param port The database listening port
   * @param schema The schema used
   * @param user The database user's name
   * @param pswd The database user's password
   * @return the connection URL
   */
  static public String createConnUrl(String dbtype, String host, int port, String schema, String user, String pswd)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("jdbc:%s://%s:%d/%s?user=%s&password=%s", dbtype, host, port, schema, user, pswd));
    
    switch (dbtype)
    {
      case "mysql":
        sb.append("&serverTimezone=UTC&useSSL=false");
        break;
      case "postgresql":
        sb.append("&ssl=false");
        break;
    }
    
    return sb.toString();
  }
  
  /**
   * Check if the schema was initialized
   * 
   * @return {@code true} if the schema was initialized, {@code false} otherwise
   */
  static public boolean isSchemaInitialized()
  {
    sLogger.debug("DbUtils::isSchemaInitialized() - IN");
    boolean init = false;
    
    DbConnectionPool pool = DbConnectionPool.getInstance();
    DbConnection dbConn = pool.allocate();
    Connection conn = dbConn.getConn();
    
    String sql = "select column_name, data_type from information_schema.columns where table_name = 'kas_mq_users';";
    try
    {
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs =  ps.executeQuery();
      if (rs.next()) init = true;
    }
    catch (SQLException e)
    {
      init = false;
    }
    
    pool.release(dbConn);
    sLogger.debug("DbUtils::isSchemaInitialized() - OUT, Returns=" + init);
    return init;
  }
  
  /**
   * Initialize KAS/MQ database schema
   */
  static public void initSchema()
  {
    sLogger.debug("DbUtils::initSchema() - IN");
    
    Properties props = MainConfiguration.getInstance().getSubset(DbConfiguration.cDbConfigPrefix);
    String dbtype = props.getStringProperty(DbConfiguration.cDbConfigPrefix + "dbtype", DbConfiguration.cDefaultDbType);
    
    sLogger.debug("DbUtils::initSchema() - Database type is " + dbtype);
    
    File dbInitFile = new File(RunTimeUtils.getProductHomeDir() + File.separator + "conf" + File.separator + "db-init-" + dbtype + ".sql");
    List<String> input = FileUtils.load(dbInitFile, "--");
    
    boolean newcmd = true;
    StringBuilder sb = null;
    for (String line : input)
    {
      line = line.trim();
      sLogger.diag("DbUtils::initSchema() - Current line: [" + line + "]");
      
      if (newcmd)
      {
        sLogger.diag("DbUtils::initSchema() - Current line is the start of a new command");
        sb = new StringBuilder(line);
        newcmd = false;
      }
      else
      {
        sLogger.diag("DbUtils::initSchema() - Current line is a continuation of the previous command, concatenating...");
        sb.append(' ').append(line);
      }
      
      if (line.endsWith(";"))
      {
        sLogger.diag("DbUtils::initSchema() - End of command detected. Execute it...");
        String cmd = PropertyResolver.resolve(sb.toString(), props);
        try
        {
          execute(cmd);
        }
        catch (SQLException e) {}
        newcmd = true;
      }
    }
    sLogger.debug("DbUtils::initSchema() - OUT");
  }
  
  /**
   * Execute a SQL statement
   * 
   * @param sql The SQL statement
   * @return A result set, if one was generated
   * @throws SQLException if thrown by java.sql.* classes
   */
  static public ResultSet execute(String sql) throws SQLException
  {
    return execute(sql, new Object [] {});
  }
  
  /**
   * Execute a SQL statement
   * 
   * @param fmt The SQL statement base format
   * @param args Arguments to format the SQL
   * @return A result set, if one was generated
   * @throws SQLException if thrown by java.sql.* classes
   */
  static public ResultSet execute(String fmt, Object... args) throws SQLException
  {
    sLogger.debug("DbUtils::execute() - IN");
    
    DbConnectionPool pool = DbConnectionPool.getInstance();
    DbConnection dbConn = pool.allocate();
    Connection conn = dbConn.getConn();
    
    String sql = String.format(fmt, args);
    ResultSet result = null;

    sql = StringUtils.clearBlanks(sql);
    sLogger.trace("DbUtils::execute() - Executing SQL: [" + sql + "]");
    PreparedStatement st = conn.prepareStatement(sql);
    boolean b = st.execute();
    if (b) result = st.getResultSet();
    
    sLogger.trace("DbUtils::execute() - HaveResult: [" + b + "]");
    
    pool.release(dbConn);
    
    sLogger.debug("DbUtils::execute() - OUT");
    return result;
  }
}
