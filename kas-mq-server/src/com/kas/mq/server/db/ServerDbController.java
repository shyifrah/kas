package com.kas.mq.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IInitializable;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.server.db.dao.GroupsDao;
import com.kas.mq.server.db.dao.UsersDao;

public class ServerDbController extends AKasObject implements IInitializable
{
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * Configuration
   */
  private MqDbConfiguration mConfig = null;
  
  /**
   * Connection pool
   */
  private DbConnectionPool mPool = null;
  
  /**
   * DAO objects
   */
  private UsersDao  mUsersDao  = null;
  private GroupsDao mGroupsDao = null;
  
  /**
   * Initialize the DB controller
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise
   */
  public boolean init()
  {
    mConfig = new MqDbConfiguration();
    mConfig.init();
    if (!mConfig.isInitialized())
    {
      mLogger.error("Failed to load KAS/MQ DB configuration");
      return false;
    }
    
    mPool = new DbConnectionPool(mConfig);
    String dbVersion = getDbVersion();
    if (dbVersion == null)
    {
      mLogger.error("Failed to extract DB version");
      return false;
    }
    
    mUsersDao  = new UsersDao(this);
    mGroupsDao = new GroupsDao(this);
    
    return true;
  }
  
  /**
   * Terminate the DB controller
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise
   */
  public boolean term()
  {
    mPool.shutdown();
    mConfig.term();
    return true;
  }
  
  /**
   * Check DB connectivity and get its version
   * 
   * @return the version of the DB or {@code null} if connectivity does not work
   */
  private String getDbVersion()
  {
    String version = null;
    DbConnection dbConn = mPool.allocate();
    Connection conn = dbConn.getConnection();
    try
    {
      PreparedStatement st = conn.prepareStatement("SELECT VERSION() AS VER");
      ResultSet rs = st.executeQuery();
      if (rs.next())
        version = rs.getString("VER");
    }
    catch (SQLException ex) {}
    
    mPool.release(dbConn);
    return version;
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
    return null;
  }
}
