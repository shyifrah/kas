package com.kas.mq.server;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.comm.impl.NetworkAddress;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.config.IBaseListener;
import com.kas.infra.config.IBaseRegistrar;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.IMqConstants;

/**
 * This {@link AConfiguration} object holds all KAS/MQ related configuration properties
 * 
 * @author Pippo
 */
public class MqConfiguration extends AConfiguration implements IBaseRegistrar
{
  /**
   * Configuration prefixes
   */
  static private final String  cMqConfigPrefix  = "kas.mq.";
  static private final String  cMqConnConfigPrefix        = cMqConfigPrefix + "conn.";
  static private final String  cMqHskpConfigPrefix        = cMqConfigPrefix + "hskp.";
  static private final String  cMqRemoteConfigPrefix      = cMqConfigPrefix + "remoteManager.";
  static private final String  cMqPreDefQueueConfigPrefix = cMqConfigPrefix + "defq.";
  
  /**
   * Default values
   */
  static public final boolean cDefaultEnabled           = true;
  static public final int     cDefaultPort              = 14560;
  static public final String  cDefaultManagerName       = "qmgr";
  static public final String  cDefaultDeadQueueName     = "local.dead";
  static public final int     cDefaultConnMaxErrors     = 10;
  static public final int     cDefaultConnSocketTimeout = 5000;
  static public final boolean cDefaultHskpEnabled       = true;
  static public final long    cDefaultHskpInterval      = 300000;
  
  /**
   * Logger
   */
  private Logger mLogger = LogManager.getLogger(getClass());
  
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
   * A map of remote destination managers to associated network addresses
   */
  private Map<String, NetworkAddress> mRemoteManagersMap = new ConcurrentHashMap<String, NetworkAddress>();
  
  /**
   * A map of predefined queues
   */
  private Map<String, Integer> mPredefQueuesMap = new ConcurrentHashMap<String, Integer>();
  
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
    mLogger.trace("MqConfiguration::refresh() - IN");
    
    mEnabled            = mMainConfig.getBoolProperty    ( cMqConfigPrefix + "enabled"           , mEnabled           );
    mPort               = mMainConfig.getIntProperty     ( cMqConfigPrefix + "port"              , mPort              );
    mManagerName        = mMainConfig.getStringProperty  ( cMqConfigPrefix + "managerName"       , mManagerName       );
    mDeadQueueName      = mMainConfig.getStringProperty  ( cMqConfigPrefix + "deadqName"         , mDeadQueueName     );
    mConnMaxErrors      = mMainConfig.getIntProperty     ( cMqConnConfigPrefix + "maxErrors"     , mConnMaxErrors     );
    mConnSocketTimeout  = mMainConfig.getIntProperty     ( cMqConnConfigPrefix + "socketTimeout" , mConnSocketTimeout );
    mHskpEnabled        = mMainConfig.getBoolProperty    ( cMqHskpConfigPrefix + "enabled"       , mHskpEnabled       );
    mHskpInterval       = mMainConfig.getLongProperty    ( cMqHskpConfigPrefix + "interval"      , mHskpInterval      );
    
    refreshRemoteManagersMap();
    refreshPredefQueuesMap();
    
    mLogger.trace("MqConfiguration::refresh() - Notifying listeners that configuration has been refreshed");
    for (IBaseListener listener : mListeners)
      listener.refresh();
    
    mLogger.trace("MqConfiguration::refresh() - OUT");
  }
  
  /**
   * Refresh the remote managers map
   */
  private void refreshRemoteManagersMap()
  {
    mLogger.trace("MqConfiguration::refreshRemoteManagersMap() - IN");
    
    Map<String, NetworkAddress> remoteManagersMap = new ConcurrentHashMap<String, NetworkAddress>();
    
    Properties props = mMainConfig.getSubset(cMqRemoteConfigPrefix, ".host");
    for (Map.Entry<Object, Object> oentry : props.entrySet())
    {
      String key = (String)oentry.getKey();
      int beginindex = cMqRemoteConfigPrefix.length();
      int endindex = key.lastIndexOf(".host");
      String manager = key.substring(beginindex, endindex);
      String host = (String)oentry.getValue();
      int port = mMainConfig.getIntProperty(cMqRemoteConfigPrefix + manager + ".port" , cDefaultPort );
      remoteManagersMap.put(manager, new NetworkAddress(host, port));
    }
    mRemoteManagersMap = remoteManagersMap;
    
    mLogger.trace("MqConfiguration::refreshRemoteManagersMap() - OUT");
  }
  
  /**
   * Refresh predefined queues map
   */
  private void refreshPredefQueuesMap()
  {
    mLogger.trace("MqConfiguration::refreshQueueDefinitionsMap() - IN");
    
    Map<String, Integer> queueDefsMap = new ConcurrentHashMap<String, Integer>();
    
    Properties props = mMainConfig.getSubset(cMqPreDefQueueConfigPrefix, ".threshold");
    for (Map.Entry<Object, Object> oentry : props.entrySet())
    {
      String key = (String)oentry.getKey();
      int beginindex = cMqPreDefQueueConfigPrefix.length();
      int endindex = key.lastIndexOf(".threshold");
      String queue = key.substring(beginindex, endindex);
      int threshold = props.getIntProperty(cMqPreDefQueueConfigPrefix + queue + ".threshold" , IMqConstants.cDefaultQueueThreshold);
      queueDefsMap.put(queue, threshold);
    }
    mPredefQueuesMap = queueDefsMap;
    
    mLogger.trace("MqConfiguration::refreshQueueDefinitionsMap() - OUT");
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
   * Register an object as a listener to to configuration changes
   * 
   * @param listener
   *   The listener
   */
  public synchronized void unregister(IBaseListener listener)
  {
    mListeners.remove(listener);
  }
  
  /**
   * Get whether the KAS/MQ is enabled or disabled
   * 
   * @return
   *   {@code true} if KAS/MQ is enabled, {@code false} otherwise
   */
  public boolean isEnabled()
  {
    return mEnabled;
  }
  
  /**
   * Gets the port number on which the KAS/MQ manager listens for new connections
   * 
   * @return
   *   KAS/MQ listen port number
   */
  public int getPort()
  {
    return mPort;
  }
  
  /**
   * Gets the name associated with the current manager
   * 
   * @return
   *   the manager's name
   */
  public String getManagerName()
  {
    return mManagerName;
  }
  
  /**
   * Gets the name of the dead queue
   * 
   * @return
   *   the name of the dead queue
   */
  public String getDeadQueueName()
  {
    return mDeadQueueName;
  }
  
  /**
   * Gets the maximum number of connection errors
   * 
   * @return
   *   the maximum number of connection errors
   */
  public int getConnMaxErrors()
  {
    return mConnMaxErrors;
  }
  
  /**
   * Gets the socket timeout
   * 
   * @return
   *   the socket timeout
   */
  public int getConnSocketTimeout()
  {
    return mConnSocketTimeout;
  }
  
  /**
   * Get whether the KAS/MQ housekeeping is enabled or disabled
   * 
   * @return
   *   {@code true} if KAS/MQ housekeeping is enabled, {@code false} otherwise
   */
  public boolean isHousekeeperEnabled()
  {
    return mHskpEnabled;
  }
  
  /**
   * Get the housekeeper interval length in milliseconds
   * 
   * @return
   *   the housekeeper interval length in milliseconds
   */
  public long getHousekeeperInterval()
  {
    return mHskpInterval;
  }
  
  /**
   * Get the remote managers map
   * 
   * @return
   *   the remote managers map
   */
  public Map<String, NetworkAddress> getRemoteManagers()
  {
    return mRemoteManagersMap;
  }
  
  /**
   * Get the predefined queues map
   * 
   * @return
   *   the predefined queues map
   */
  public Map<String, Integer> getQueueDefinitions()
  {
    return mPredefQueuesMap;
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
      .append(pad).append("  RemoteManagers=(\n")
      .append(StringUtils.asPrintableString(mRemoteManagersMap, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append("  PredefinedQueues=(\n")
      .append(StringUtils.asPrintableString(mPredefQueuesMap, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
