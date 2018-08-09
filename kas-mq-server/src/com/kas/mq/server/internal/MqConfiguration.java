package com.kas.mq.server.internal;

import com.kas.config.impl.AConfiguration;

/**
 * This {@link AConfiguration} object holds all KAS/MQ related configuration properties
 * 
 * @author Pippo
 */
public class MqConfiguration extends AConfiguration
{
  static private final String  cMqConfigPrefix  = "kas.mq.";
  
  static public final boolean cDefaultEnabled        = true;
  static public final int     cDefaultPort           = 14560;
  static public final String  cDefaultManagerName    = "qmgr";
  static public final String  cDefaultDeadQueueName  = "local.dead";
  static public final String  cDefaultAdminQueueName = "local.admin";
  static public final int     cDefaultMaxErrors      = 10;
  
  
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
   * The name of the administration queue
   */
  private String mAdminQueueName = cDefaultAdminQueueName;
  
  /**
   * The maximum number of errors the KAS/MQ server will tolerate before shutting down
   */
  private int mMaxErrors = cDefaultMaxErrors; 
  
  /**
   * Refresh configuration - reload values of all properties
   */
  public void refresh()
  {
    mEnabled            = mMainConfig.getBoolProperty    ( cMqConfigPrefix + "enabled"            , mEnabled           );
    mPort               = mMainConfig.getIntProperty     ( cMqConfigPrefix + "port"               , mPort              );
    mManagerName        = mMainConfig.getStringProperty  ( cMqConfigPrefix + "managerName"        , mManagerName       );
    mDeadQueueName      = mMainConfig.getStringProperty  ( cMqConfigPrefix + "deadqName"          , mDeadQueueName     );
    mAdminQueueName     = mMainConfig.getStringProperty  ( cMqConfigPrefix + "adminqName"         , mAdminQueueName    );
    mMaxErrors          = mMainConfig.getIntProperty     ( cMqConfigPrefix + "maxTolerableErrors" , mMaxErrors         );
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
   * Gets the name of the administration queue
   * 
   * @return the name of the administration queue
   */
  public String getAdminQueueName()
  {
    return mAdminQueueName;
  }
  
  /**
   * Gets the maximum number of tolerable errors
   * 
   * @return the maximum number of tolerable errors
   */
  public int getMaxErrors()
  {
    return mMaxErrors;
  }
  
  /**
   * Returns a replica of this {@link MqConfiguration}.
   * 
   * @return a replica of this {@link MqConfiguration}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public MqConfiguration replicate()
  {
    MqConfiguration config = new MqConfiguration();
    config.mMainConfig      = mMainConfig;
    config.mEnabled         = mEnabled;
    config.mPort            = mPort;
    config.mManagerName     = mManagerName;
    config.mDeadQueueName   = mDeadQueueName;
    config.mAdminQueueName  = mAdminQueueName;
    config.mMaxErrors       = mMaxErrors;
    return config;
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
      .append(pad).append("  AdminQueueName=").append(mAdminQueueName).append("\n")
      .append(pad).append("  MaxErrors=").append(mMaxErrors).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
