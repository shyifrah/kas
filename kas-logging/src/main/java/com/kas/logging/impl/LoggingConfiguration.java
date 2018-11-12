package com.kas.logging.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.base.Properties;
import com.kas.infra.config.IBaseListener;
import com.kas.infra.config.IBaseRegistrar;
import com.kas.logging.appender.AAppenderConfiguration;
import com.kas.logging.appender.IAppenderConfiguration;
/**
 * The logging configuration
 * 
 * @author Pippo
 */
public class LoggingConfiguration extends AConfiguration implements IBaseRegistrar
{
  static public final String  cLogConfigPrefix         = "kas.logging.";
  static public final String  cLogAppenderConfigPrefix = cLogConfigPrefix + "appender.";
  static private final String cNoOpAppenderName = "noop";
  
  static public final boolean cDefaultEnabled = true;
  
  /**
   * An indicator whether logging is enabled or disabled
   */
  private boolean mEnabled = cDefaultEnabled;
  
  /**
   * Configured appenders
   */
  private Map<String, IAppenderConfiguration> mAppenderConfigs = new ConcurrentHashMap<String, IAppenderConfiguration>();
  
  /**
   * A set of configuration listener objects.<br>
   * When configuration changes, all listeners are notified
   */
  private HashSet<IBaseListener> mListeners;
  
  /**
   * Construct the configuration
   */
  LoggingConfiguration()
  {
    mListeners = new HashSet<IBaseListener>();
  }
  
  /**
   * Configuration has been refreshed.<br>
   * <br>
   * Notify to all listeners that configuration has been changed.
   */
  public void refresh()
  {
    mEnabled = mMainConfig.getBoolProperty ( cLogConfigPrefix + "enabled" , mEnabled );
    
    refreshAppenderConfigs();
    
    for (IBaseListener listener : mListeners)
      listener.refresh();
  }
  
  /**
   * Refresh the appenders' configurations
   */
  private void refreshAppenderConfigs()
  {
    Map<String, IAppenderConfiguration> appenderConfigs = new ConcurrentHashMap<String, IAppenderConfiguration>();
    
    appenderConfigs.put(cNoOpAppenderName, new NoOpAppenderConfiguration(cNoOpAppenderName, this));
    
    Properties props = mMainConfig.getSubset(cLogAppenderConfigPrefix, ".type");
    for (Map.Entry<Object, Object> oentry : props.entrySet())
    {
      String key = (String)oentry.getKey();
      int beginindex = cLogAppenderConfigPrefix.length();
      int endindex = key.lastIndexOf(".type");
      String name = key.substring(beginindex, endindex);
      
      AAppenderConfiguration config = null;
      
      String type = (String)oentry.getValue();
      switch (type.toLowerCase())
      {
        case "file":
          config = new FileAppenderConfiguration(name, this);
          break;
        case "stdout":
          config = new StdoutAppenderConfiguration(name, this);
          break;
        case "stderr":
          config = new StderrAppenderConfiguration(name, this);
          break;
        case "noop":
          config = new StderrAppenderConfiguration(name, this);
          break;
        default:
          break;
      }
      
      if (config != null) appenderConfigs.put(name, config);
    }
    
    mAppenderConfigs = appenderConfigs;
  }
  
  /**
   * Get appender's name by requestor class name
   * 
   * @param requestorClassName The name of the class that created the logger
   * @return the name of the appenders associated with this class/package, or {@code null} if none could be found
   */
  private String getAppenderName(String requestorClassName)
  {
    String loggerName = requestorClassName;
    String appenderName = null;
    boolean found = false;
    
    // looking for best match
    do
    {
      String key = cLogConfigPrefix + "logger." + loggerName + ".appender";
      String val = mMainConfig.getStringProperty(key, "");
      
      if (val.length() > 0)
      {
        found = true;
        appenderName = val;
      }
      else
      {
        // remove last qualifier of logger name
        int endIdx = loggerName.lastIndexOf('.');
        if (endIdx == -1)
          break;
        loggerName = loggerName.substring(0, endIdx);
      }
    }
    while (!found);

    return appenderName;
  }
  
  /**
   * Get appender's configuration by requestor class name
   * 
   * @param requestorClassName The name of the class that created the logger
   * @return the {@link AAppenderConfiguration} that should be used for the appender 
   */
  public IAppenderConfiguration getAppenderConfig(String requestorClassName)
  {
    String appenderName = getAppenderName(requestorClassName);
    if (appenderName == null)
      appenderName = cNoOpAppenderName;
    return mAppenderConfigs.get(appenderName);
  }
  
  /**
   * Get logging configuration status
   * 
   * @return {@code true} if logging is enabled, {@code false} otherwise
   */
  public boolean isEnabled()
  {
    return mEnabled;
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
   * Returns the {@link LoggingConfiguration} string representation.
   * 
   * @param level the required level padding
   * 
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Enabled=").append(mEnabled).append("\n")
      .append(pad).append("  AppendersConfigurations=(\n");

    for (IBaseListener listener : mListeners)
      sb.append(pad).append("    ").append(listener.toPrintableString(level+2)).append("\n");
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
