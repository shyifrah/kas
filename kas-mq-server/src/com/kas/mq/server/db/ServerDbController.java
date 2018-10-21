package com.kas.mq.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
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
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  private MqDbConfiguration mConfig = null;
  
  private UsersDao  mUsersDao  = null;
  private GroupsDao mGroupsDao = null;
  
  public boolean init()
  {
    mLogger.debug("ServerDbController::init() - IN");
    
    boolean success = true;
    mConfig = new MqDbConfiguration();
    mConfig.init();
    if (!mConfig.isInitialized())
      success = false;
    
    String dbVersion = getDbVersion();
    if (dbVersion == null)
      success = false;
    else
      mLogger.info("Server successfully established connection to MySQL V" + dbVersion);
    
    if (success)
    {
      mUsersDao  = new UsersDao(this);
      mGroupsDao = new GroupsDao(this);
    }
    
    mLogger.debug("ServerDbController::init() - OUT, Returns=" + success);
    return success;
  }
  
  public boolean term()
  {
    mLogger.debug("ServerDbController::term() - IN");
    
    boolean success = true;
    
    mConfig.term();
    
    mLogger.debug("ServerDbController::term() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Check DB connectivity and get its version
   * 
   * @return the version of the DB or {@code null} if connectivity does not work
   */
  private String getDbVersion()
  {
    String version = null;
    String url = new StringBuilder()
        .append("jdbc:")
        .append(mConfig.getDbType())
        .append("://)")
        .append(mConfig.getHost())
        .append(':')
        .append(mConfig.getPort())
        .append('/')
        .append(mConfig.getSchemaName()).toString();
    
    String user = mConfig.getUserName();
    String password = mConfig.getPassword();
    String query = "SELECT VERSION() AS VER";

    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      Connection con = DriverManager.getConnection(url, user, password);
      PreparedStatement st = con.prepareStatement(query);
      ResultSet rs = st.executeQuery();
      if (rs.next())
      {
        version = rs.getString("VER");
      }
    }
    catch (ClassNotFoundException | SQLException ex) {}
    return version;
  }
  
  @Override
  public String toPrintableString(int level)
  {
    // TODO Auto-generated method stub
    return null;
  }
}
