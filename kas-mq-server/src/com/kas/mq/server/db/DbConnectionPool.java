package com.kas.mq.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IPool;
import com.kas.infra.base.UniqueId;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.server.MqDbConfiguration;

/**
 * A pool of database connections.<br>
 * <br>
 * The pool is first accessed by {@link com.kas.mq.server.KasMqServer} so it can initialize it
 * by calling {@link #init()}. Following that access, this class acts just as a regular singleton.
 * 
 * @author Pippo
 */
public class DbConnectionPool extends AKasObject implements IPool<DbConnection> 
{
  /**
   * The pool that will actually be used
   */
  static private DbConnectionPool sInstance = null;
  
  /**
   * Get the pool
   * 
   * @return the instance previously created
   */
  static public DbConnectionPool getPool()
  {
    if (sInstance == null)
      throw new RuntimeException("DbConnectionPool was not initialized");
    
    return sInstance;
  }
  
  /**
   * DB types.<br>
   * Currently, only MySQL is supported
   */
  static private final String cMySqlDbType = "mysql";
  
  /**
   * DB drivers.<br>
   * Currently, only MySQL is supported
   */
  static private final String cMySqlJdbcDriver = "com.mysql.jdbc.Driver";
  
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Configuration object
   */
  private MqDbConfiguration mConfig = null;
  
  /**
   * The connection string as computed after analyzing configuration
   */
  private String mConnUrl = null;
  
  /**
   * The name of the JDBC driver. This is driven from the DB type
   */
  private String mJdbcClassName = null;
  
  /**
   * The connections map
   */
  private Map<UniqueId, DbConnection> mConnMap;
  
  /**
   * Construct the DB pool
   * 
   * @param config The configuration object
   */
  public DbConnectionPool(MqDbConfiguration config)
  {
    if (sInstance != null)
      throw new RuntimeException("Multiple instances of DbConnectionPool are prohibited");
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    
    mConfig = config;
    
    String dbType = mConfig.getDbType().toLowerCase();
    String schema = mConfig.getSchemaName().toLowerCase();
    
    mConnUrl = new StringBuilder()
      .append("jdbc:").append(dbType).append("://)")
      .append(mConfig.getHost()).append(':').append(mConfig.getPort()).append('/')
      .append(schema).append("?useSSL=false&serverTimezone=UTC").toString();
    
    mConnMap = new ConcurrentHashMap<UniqueId, DbConnection>();
    
    switch (dbType)
    {
      case cMySqlDbType:
        mJdbcClassName = cMySqlJdbcDriver;
        break;
      default:
        throw new RuntimeException("Unknown DB type: " + dbType);
    }
    
    try
    {
      Class.forName(mJdbcClassName);
    }
    catch (ClassNotFoundException e)
    {
      throw new RuntimeException(mJdbcClassName);
    }
  }
  
  /**
   * Allocate new {@link DbConnection}
   * 
   * @return the newly allocated {@link DbConnection}
   */
  public DbConnection allocate()
  {
    mLogger.debug("DbConnectionPool::allocate() - IN");
    
    DbConnection dbConn = null;
    
    if (mConnMap.size() < mConfig.getMaxConnections())
    {
      Connection conn = createConnection();
      if (conn != null)
      {
        dbConn = new DbConnection(conn);
        mConnMap.put(dbConn.getConnId(), dbConn);
      }
    }
    
    mLogger.debug("DbConnectionPool::allocate() - OUT");
    return dbConn;
  }
  
  /**
   * Release a previously allocated {@link DbConnection}
   * 
   * @param dbConn A previously allocated {@link DbConnection}
   */
  public void release(DbConnection dbConn)
  {
    mLogger.debug("DbConnectionPool::release() - IN");
    
    mConnMap.remove(dbConn.getConnId());
    
    Connection conn = dbConn.getConnection();
    try
    {
      conn.close();
    }
    catch (SQLException e) {}
    
    mLogger.debug("DbConnectionPool::release() - OUT");
  }
  
  /**
   * Check DB connectivity and get its version
   * 
   * @return {@code true} if connectivity works fine, {@code false} otherwise
   */
  public boolean init()
  {
    mLogger.debug("DbConnectionPool::init() - IN");
    
    boolean success = true;
    
    String version = null;
    DbConnection dbConn = allocate();
    Connection conn = dbConn.getConnection();
    try
    {
      PreparedStatement st = conn.prepareStatement("SELECT VERSION() AS VER");
      ResultSet rs = st.executeQuery();
      if (rs.next())
        version = rs.getString("VER");
    }
    catch (SQLException ex)
    {
      success = false;
    }
    
    release(dbConn);
    
    if (success)
    {
      mLogger.info("DbConnectionPool::init() - Connection pool successfully connected to DB and got its version: " + version);
      sInstance = this;
    }
    
    mLogger.debug("DbConnectionPool::init() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Closing all connections and clearing the map
   * 
   * @return {@code true} 
   */
  public boolean term()
  {
    mLogger.debug("DbConnectionPool::term() - IN");
    
    Collection<DbConnection> col = mConnMap.values();
    for (Iterator<DbConnection> iter = col.iterator(); iter.hasNext();)
    {
      DbConnection conn = iter.next();
      UniqueId id = conn.getConnId();
      mLogger.debug("DbConnectionPool::term() - Closing connection ID " + id);
      conn.close();
      iter.remove();
    }
    
    mConnMap.clear();
    sInstance = null;
    
    mLogger.debug("DbConnectionPool::term() - OUT");
    return true;
  }
  
  /**
   * Create a new Connection
   * 
   * @return a new Connection
   */
  private Connection createConnection()
  {
    Connection conn = null;
    try
    {
      conn = DriverManager.getConnection(mConnUrl, mConfig.getUserName(), mConfig.getPassword());
    }
    catch (SQLException ex) {}
    return conn;
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Connection URL=").append(mConnUrl).append("\n")
      .append(pad).append("  Driver=").append(mJdbcClassName).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
