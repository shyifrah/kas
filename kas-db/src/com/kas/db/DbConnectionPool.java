package com.kas.db;

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
   * Initialize the pool. This method must be called prior to {@link #getInstance()}
   * 
   * @param config The {@link DbConfiguration} object that will initialize the pool
   * @return {@code true} if the pool was initialized successfully, {@code false} otherwise
   */
  static public boolean init(DbConfiguration config)
  {
    sInstance = new DbConnectionPool(config);
    return sInstance.getDbVersion();
  }
  
  /**
   * Get the pool
   * 
   * @return the instance previously created
   */
  static public DbConnectionPool getInstance()
  {
    if (sInstance == null)
      throw new RuntimeException("DbConnectionPool was not initialized");
    
    return sInstance;
  }
  
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Configuration object
   */
  private DbConfiguration mConfig = null;
  
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
  private DbConnectionPool(DbConfiguration config)
  {
    if (sInstance != null)
      throw new RuntimeException("Multiple instances of DbConnectionPool are prohibited");
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    
    mConfig = config;
    
    String dbType = mConfig.getDbType().toLowerCase();
    String schema = mConfig.getSchemaName().toLowerCase();
    
    mConnUrl = DbUtils.createConnUrl(
      mConfig.getDbType().toLowerCase(), 
      mConfig.getHost().toLowerCase(), 
      mConfig.getPort(), 
      mConfig.getSchemaName().toLowerCase(), 
      mConfig.getUserName().toLowerCase(), 
      mConfig.getPassword());
        
        new StringBuilder()
      .append("jdbc:").append(dbType).append("://")
      .append(mConfig.getHost()).append(':').append(mConfig.getPort()).append('/')
      .append(schema).toString();
    
    mConnMap = new ConcurrentHashMap<UniqueId, DbConnection>();
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
    
    Connection conn = dbConn.getConn();
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
  public boolean getDbVersion()
  {
    mLogger.debug("DbConnectionPool::getDbVersion() - IN");
    
    boolean success = true;
    
    String version = null;
    DbConnection dbConn = allocate();
    Connection conn = dbConn.getConn();
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
    mLogger.info("DbConnectionPool::getDbVersion() - Connection pool successfully connected to DB and got its version: " + version);
    
    
    mLogger.debug("DbConnectionPool::getDbVersion() - OUT, Returns=" + success);
    return success;
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
    sInstance = null;
    
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
      conn = DriverManager.getConnection(mConnUrl);
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
