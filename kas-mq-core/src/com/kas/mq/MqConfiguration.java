package com.kas.mq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.Base64Utils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.DestinationManager;

/**
 * This {@link AConfiguration} object holds all KAS/MQ related configuration properties
 * 
 * @author Pippo
 */
public class MqConfiguration extends AConfiguration
{
  static private final String  cMqConfigPrefix  = "kas.mq.";
  static private final String  cMqUserConfigPrefix    = cMqConfigPrefix + "user.";
  static private final String  cMqConnConfigPrefix    = cMqConfigPrefix + "conn.";
  static private final String  cMqHskpConfigPrefix    = cMqConfigPrefix + "hskp.";
  static private final String  cMqRemoteConfigPrefix  = cMqConfigPrefix + "remote.";
  
  static public final boolean cDefaultEnabled           = true;
  static public final int     cDefaultPort              = 14560;
  static public final String  cDefaultManagerName       = "qmgr";
  static public final String  cDefaultDeadQueueName     = "local.dead";
  static public final int     cDefaultConnMaxErrors     = 10;
  static public final int     cDefaultConnSocketTimeout = 5000;
  static public final boolean cDefaultHskpEnabled       = true;
  static public final long    cDefaultHskpInterval      = 300000;
  
  
  /**
   * Indicator whether KAS/MQ is enabled
   */
  private boolean mEnabled = cDefaultEnabled;
  
  /**
   * The port number on which KAS/MQ will listen for new connections
   */
  private int mPort = cDefaultPort;
  
  /**
   * The name of this KAS/MQ manager
   */
  private String mManagerName = cDefaultManagerName;
  
  /**
   * The name of the dead queue 
   */
  private String mDeadQueueName = cDefaultDeadQueueName;
  
  /**
   * The maximum number of errors the KAS/MQ server will tolerate before shutting down
   */
  private int mConnMaxErrors = cDefaultConnMaxErrors; 
  
  /**
   * The timeout, in milliseconds, before socket operations like {@link java.net.ServerSocket#accept() accept()} call will timeout
   */
  private int mConnSocketTimeout = cDefaultConnSocketTimeout; 
  
  /**
   * Indicator whether KAS/MQ housekeeping is enabled
   */
  private boolean mHskpEnabled = cDefaultHskpEnabled; 
  
  /**
   * The interval, in milliseconds, between housekeeping executions
   */
  private long mHskpInterval = cDefaultHskpInterval; 
  
  /**
   * A map of users and passwords (base64 encrypted)
   */
  private Map<String, DestinationManager> mRemoteDestinationMap = new ConcurrentHashMap<String, DestinationManager>();
  
  /**
   * A map of users and passwords (base64 encrypted)
   */
  private Map<String, byte []> mUserMap = new ConcurrentHashMap<String, byte []>();
  
  /**
   * Refresh configuration - reload values of all properties
   */
  public void refresh()
  {
    mEnabled            = mMainConfig.getBoolProperty    ( cMqConfigPrefix + "enabled"           , mEnabled           );
    mPort               = mMainConfig.getIntProperty     ( cMqConfigPrefix + "port"              , mPort              );
    mManagerName        = mMainConfig.getStringProperty  ( cMqConfigPrefix + "managerName"       , mManagerName       );
    mDeadQueueName      = mMainConfig.getStringProperty  ( cMqConfigPrefix + "deadqName"         , mDeadQueueName     );
    mConnMaxErrors      = mMainConfig.getIntProperty     ( cMqConnConfigPrefix + "maxErrors"     , mConnMaxErrors     );
    mConnSocketTimeout  = mMainConfig.getIntProperty     ( cMqConnConfigPrefix + "socketTimeout" , mConnSocketTimeout );
    mHskpEnabled        = mMainConfig.getBoolProperty    ( cMqHskpConfigPrefix + "enabled"       , mHskpEnabled       );
    mHskpInterval       = mMainConfig.getLongProperty    ( cMqHskpConfigPrefix + "interval"      , mHskpInterval      );
    
    mUserMap.clear();
    
    Properties props = mMainConfig.getSubset(cMqUserConfigPrefix);
    for (Map.Entry<Object, Object> entry : props.entrySet())
    {
      String user = ((String)entry.getKey()).substring(cMqUserConfigPrefix.length());
      user = user.toUpperCase();
      String pass = (String)entry.getValue();
      byte [] encpass = Base64Utils.encode(pass.getBytes());
      mUserMap.put(user, encpass);
    }
    
    mRemoteDestinationMap.clear();
  }
  
  /**
   * Get whether the KAS/MQ is enabled or disabled
   * 
   * @return {@code true} if KAS/MQ is enabled, {@code false} otherwise
   */
  public boolean isEnabled()
  {
    return mEnabled;
  }
  
  /**
   * Gets the port number on which the KAS/MQ manager listens for new connections
   * 
   * @return KAS/MQ listen port number
   */
  public int getPort()
  {
    return mPort;
  }
  
  /**
   * Gets the name associated with the current manager
   * 
   * @return the manager's name
   */
  public String getManagerName()
  {
    return mManagerName;
  }
  
  /**
   * Gets the name of the dead queue
   * 
   * @return the name of the dead queue
   */
  public String getDeadQueueName()
  {
    return mDeadQueueName;
  }
  
  /**
   * Gets the maximum number of connection errors
   * 
   * @return the maximum number of connection errors
   */
  public int getConnMaxErrors()
  {
    return mConnMaxErrors;
  }
  
  /**
   * Gets the socket timeout
   * 
   * @return the socket timeout
   */
  public int getConnSocketTimeout()
  {
    return mConnSocketTimeout;
  }
  
  /**
   * Get whether the KAS/MQ housekeeping is enabled or disabled
   * 
   * @return {@code true} if KAS/MQ housekeeping is enabled, {@code false} otherwise
   */
  public boolean isHousekeeperEnabled()
  {
    return mHskpEnabled;
  }
  
  /**
   * Get the housekeeper interval length in milliseconds
   * 
   * @return the housekeeper interval length in milliseconds
   */
  public long getHousekeeperInterval()
  {
    return mHskpInterval;
  }
  
  /**
   * Gets a user's password
   * 
   * @param user The user's name
   * @return the user's password or {@code null} if user's name isn't defined
   */
  public byte [] getUserPassword(String user)
  {
    if (user == null)
      return null;
    
    String u = user.toUpperCase();
    
    return mUserMap.get(u);
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
      .append(pad).append("  Enabled=").append(mEnabled).append("\n")
      .append(pad).append("  Name=").append(mManagerName).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append("  DeadQueueName=").append(mDeadQueueName).append("\n")
      .append(pad).append("  Connection Settings=(\n")
      .append(pad).append("    MaxErrors=").append(mConnMaxErrors).append("\n")
      .append(pad).append("    Timeout=").append(mConnSocketTimeout).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append("  Housekeeper=(\n")
      .append(pad).append("    Enabled=").append(mHskpEnabled).append("\n")
      .append(pad).append("    Interval=").append(mHskpInterval).append(" milliseconds\n")
      .append(pad).append("  )\n")
      .append(pad).append("  Users=(\n")
      .append(pad).append(StringUtils.asPrintableString(mUserMap, level+2))
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
