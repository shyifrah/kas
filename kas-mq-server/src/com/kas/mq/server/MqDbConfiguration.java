package com.kas.mq.server;

import java.util.HashSet;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.config.IBaseListener;
import com.kas.infra.config.IBaseRegistrar;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * This {@link AConfiguration} object holds all DB related configuration properties
 * 
 * @author Pippo
 */
public class MqDbConfiguration extends AConfiguration implements IBaseRegistrar
{
  /**
   * Configuration prefixes
   */
  static private final String  cDbConfigPrefix  = "kas.mq.db.";
  
  /**
   * Default values
   */
  static public final String  cDefaultDbType         = "mysql";
  static public final String  cDefaultHostName       = "localhost";
  static public final int     cDefaultPort           = 3306;
  static public final String  cDefaultSchemaName     = "kas";
  static public final String  cDefaultUserName       = null;
  static public final String  cDefaultPassword       = null;
  static public final int     cDefaultMaxConnections = 20;
  
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
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
    mLogger.debug("MqDbConfiguration::refresh() - IN");
    
    mDbType         = mMainConfig.getStringProperty  ( cDbConfigPrefix + "type"           , mDbType         );
    mHostName       = mMainConfig.getStringProperty  ( cDbConfigPrefix + "host"           , mHostName       );
    mPort           = mMainConfig.getIntProperty     ( cDbConfigPrefix + "port"           , mPort           );
    mSchemaName     = mMainConfig.getStringProperty  ( cDbConfigPrefix + "schema"         , mSchemaName     );
    mUserName       = mMainConfig.getStringProperty  ( cDbConfigPrefix + "username"       , mUserName       );
    mPassword       = mMainConfig.getStringProperty  ( cDbConfigPrefix + "password"       , mPassword       );
    mMaxConnections = mMainConfig.getIntProperty     ( cDbConfigPrefix + "maxConnections" , mMaxConnections );
    
    mLogger.debug("MqDbConfiguration::refresh() - Notifying listeners that configuration has been refreshed");
    for (IBaseListener listener : mListeners)
      listener.refresh();
    
    mLogger.debug("DbConfiguration::refresh() - OUT");
  }
  
  /**
   * Register an object as a listener to to configuration changes
   * 
   * @param listener The listener
   * 
   * @see com.kas.infra.config.IBaseRegistrar#register(IBaseListener)
   */
  public synchronized void register(IBaseListener listener)
  {
    mListeners.add(listener);
  }
  
  /**
   * Register an object as a listener to to configuration changes
   * 
   * @param listener The listener
   * 
   * @see com.kas.infra.config.IBaseRegistrar#unregister(IBaseListener)
   */
  public synchronized void unregister(IBaseListener listener)
  {
    mListeners.remove(listener);
  }
  
  /**
   * Gets the DB type
   * 
   * @return the DB type
   */
  public String getDbType()
  {
    return mDbType;
  }
  
  /**
   * Gets the DB host
   * 
   * @return the DB host
   */
  public String getHost()
  {
    return mHostName;
  }
  
  /**
   * Gets the port number on which the DB listens for new connections
   * 
   * @return DB listen port number
   */
  public int getPort()
  {
    return mPort;
  }
  
  /**
   * Gets the name of the schema
   * 
   * @return the name of the schema
   */
  public String getSchemaName()
  {
    return mSchemaName;
  }
  
  /**
   * Gets the DB user's name
   * 
   * @return the DB user's name
   */
  public String getUserName()
  {
    return mUserName;
  }
  
  /**
   * Gets the DB user's password
   * 
   * @return the DB user's password
   */
  public String getPassword()
  {
    return mPassword;
  }
  
  /**
   * Get the maximum number of active DB connections
   * @return
   */
  public int getMaxConnections()
  {
    return mMaxConnections;
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
      .append(pad).append("  Type=").append(mDbType).append("\n")
      .append(pad).append("  Host=").append(mHostName).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append("  Schema=").append(mSchemaName).append("\n")
      .append(pad).append("  MaxConnections=").append(mMaxConnections).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
