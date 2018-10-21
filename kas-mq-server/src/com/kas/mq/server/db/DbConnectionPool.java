package com.kas.mq.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
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

/**
 * A pool of database connections
 * 
 * @author Pippo
 */
public class DbConnectionPool extends AKasObject implements IPool<DbConnection> 
{
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
  private String mConnectionString = null;
  
  /**
   * The user name and password that will be used to access the DB
   */
  private String mUserName = null;
  private String mPassword = null;
  
  /**
   * The name of the JDBC driver. This is driven from the DB type
   */
  private String mJdbcClassName = null;
  
  /**
   * Maximum number of connections
   */
  private int mMaxConnections;
  
  /**
   * The connections map
   */
  private Map<UniqueId, DbConnection> mConnMap;
  
  /**
   * Construct the DB pool
   * 
   * @param config The configuration object
   */
  DbConnectionPool(MqDbConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    
    mConfig = config;
    
    String dbType = mConfig.getDbType().toLowerCase();
    String schema = mConfig.getSchemaName().toLowerCase();
    
    mConnectionString = new StringBuilder()
      .append("jdbc:").append(dbType).append("://)")
      .append(mConfig.getHost()).append(':').append(mConfig.getPort()).append('/')
      .append(schema).append("?useSSL=false&serverTimezone=UTC").toString();
    
    mUserName = mConfig.getUserName();
    mPassword = mConfig.getPassword();
    mMaxConnections = mConfig.getMaxConnections();
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
    
    if (mConnMap.size() < mMaxConnections)
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
   * Closing all connections and clearing the map
   */
  public void shutdown()
  {
    mLogger.debug("DbConnectionPool::shutdown() - IN");
    
    Collection<DbConnection> col = mConnMap.values();
    for (Iterator<DbConnection> iter = col.iterator(); iter.hasNext();)
    {
      DbConnection conn = iter.next();
      UniqueId id = conn.getConnId();
      mLogger.debug("DbConnectionPool::shutdown() - Closing connection ID " + id);
      conn.close();
      iter.remove();
    }
    
    mConnMap.clear();
    
    mLogger.debug("DbConnectionPool::shutdown() - OUT");
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
      conn = DriverManager.getConnection(mConnectionString, mUserName, mPassword);
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
      .append(pad).append("  Connection URL=").append(mConnectionString).append("\n")
      .append(pad).append("  Driver=").append(mJdbcClassName).append("\n")
      .append(pad).append("  Limit=").append(mMaxConnections).append(" connections\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
