package com.kas.db;

import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.base.IObject;
import com.kas.infra.config.IBaseListener;
import com.kas.infra.config.IBaseRegistrar;

/**
 * This {@link AConfiguration} object holds all DB related configuration properties
 * 
 * @author Pippo
 */
public class DbConfiguration extends AConfiguration implements IBaseRegistrar
{
  /**
   * Configuration prefixes
   */
  static public final String  cDbConfigPrefix  = "kas.db.";
  
  /**
   * Default values
   */
  static public final String  cDefaultDbType         = DbUtils.cDbTypeMySql;
  static public final String  cDefaultHostName       = "localhost";
  static public final int     cDefaultPort           = 3306;
  static public final String  cDefaultSchemaName     = "kas";
  static public final String  cDefaultUserName       = "kas";
  static public final String  cDefaultPassword       = "kas";
  static public final int     cDefaultMaxConnections = 20;
  
  /**
   * Logger
   */
  private Logger mLogger = LogManager.getLogger(this.getClass());
  
  /**
   * The DB type
   */
  private String mDbType = cDefaultDbType;
  
  /**
   * The DB host name 
   */
  private String mHostName = cDefaultHostName;
  
  /**
   * The DB port number
   */
  private int mPort = cDefaultPort;
  
  /**
   * The name of KAS schema
   */
  private String mSchemaName = cDefaultSchemaName;
  
  /**
   * The DB user name 
   */
  private String mUserName = cDefaultUserName;
  
  /**
   * The DB user's password
   */
  private String mPassword = cDefaultPassword;
  
  /**
   * The maximum number of active DB connections
   */
  private int mMaxConnections = cDefaultMaxConnections;
  
  /**
   * A set of configuration listener objects.<br>
   * When configuration changes, all listeners are notified
   */
  private HashSet<IBaseListener> mListeners = new HashSet<IBaseListener>();
  
  /**
   * Refresh configuration - reload values of all properties
   */
  public void refresh()
  {
    mLogger.trace("DbConfiguration::refresh() - IN");
    
    mDbType         = mMainConfig.getStringProperty  ( cDbConfigPrefix + "type"           , mDbType         );
    mHostName       = mMainConfig.getStringProperty  ( cDbConfigPrefix + "host"           , mHostName       );
    mPort           = mMainConfig.getIntProperty     ( cDbConfigPrefix + "port"           , mPort           );
    mSchemaName     = mMainConfig.getStringProperty  ( cDbConfigPrefix + "schema"         , mSchemaName     );
    mUserName       = mMainConfig.getStringProperty  ( cDbConfigPrefix + "username"       , mUserName       );
    mPassword       = mMainConfig.getStringProperty  ( cDbConfigPrefix + "password"       , mPassword       );
    mMaxConnections = mMainConfig.getIntProperty     ( cDbConfigPrefix + "maxConnections" , mMaxConnections );
    
    mLogger.trace("DbConfiguration::refresh() - Notifying listeners that configuration has been refreshed");
    for (IBaseListener listener : mListeners)
      listener.refresh();
    
    mLogger.trace("DbConfiguration::refresh() - OUT");
  }
  
  /**
   * Register an object as a listener to to configuration changes
   * 
   * @param listener
   *   The listener
   */
  public synchronized void register(IBaseListener listener)
  {
    mListeners.add(listener);
  }
  
  /**
   * Unregister an object as a listener to to configuration changes
   * 
   * @param listener
   *   The listener
   */
  public synchronized void unregister(IBaseListener listener)
  {
    mListeners.remove(listener);
  }
  
  /**
   * Gets the DB type
   * 
   * @return
   *   the DB type
   */
  public String getDbType()
  {
    return mDbType;
  }
  
  /**
   * Gets the DB host
   * 
   * @return
   *   the DB host
   */
  public String getHost()
  {
    return mHostName;
  }
  
  /**
   * Gets the port number on which the DB listens for new connections
   * 
   * @return
   *   DB listen port number
   */
  public int getPort()
  {
    return mPort;
  }
  
  /**
   * Gets the name of the schema
   * 
   * @return
   *   the name of the schema
   */
  public String getSchemaName()
  {
    return mSchemaName;
  }
  
  /**
   * Gets the DB user's name
   * 
   * @return
   *   the DB user's name
   */
  public String getUserName()
  {
    return mUserName;
  }
  
  /**
   * Gets the DB user's password
   * 
   * @return
   *   the DB user's password
   */
  public String getPassword()
  {
    return mPassword;
  }
  
  /**
   * Get the maximum number of active DB connections
   * 
   * @return
   *   the maximum number of active DB connections
   */
  public int getMaxConnections()
  {
    return mMaxConnections;
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Type=").append(mDbType).append("\n")
      .append(pad).append("  Host=").append(mHostName).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append("  Schema=").append(mSchemaName).append("\n")
      .append(pad).append("  cDefaultUserName=").append(cDefaultUserName).append("\n")
      .append(pad).append("  cDefaultPassword=").append(cDefaultPassword).append("\n")
      .append(pad).append("  MaxConnections=").append(mMaxConnections).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
