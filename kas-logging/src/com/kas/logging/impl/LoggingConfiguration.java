package com.kas.logging.impl;

import java.util.HashSet;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.base.WeakRef;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IRegistrar;

/**
 * The logging configuration
 * 
 * @author Pippo
 */
public class LoggingConfiguration extends AConfiguration implements IRegistrar
{
  static public final String  cLoggingConfigPrefix = "kas.logging.";
  static public final boolean cDefaultEnabled = true;
  
  /**
   * An indicator whether logging is enabled or disabled
   */
  private boolean mEnabled = cDefaultEnabled;
  
  /**
   * A set of weak-references to all appenders' configuration objects.<br>
   * When configuration changes, all appender's configurations are refreshed
   */
  private HashSet<WeakRef<IListener>> mAppenderConfigsSet = new HashSet<WeakRef<IListener>>();
  
  /**
   * Refresh configuration
   */
  public void refresh()
  {
    mEnabled = mMainConfig.getBoolProperty ( cLoggingConfigPrefix + "enabled" , mEnabled);
    
    for (WeakRef<IListener> appenderConfigRef : mAppenderConfigsSet)
    {
      IListener appenderConfig = appenderConfigRef.get();
      if (appenderConfig == null)
      {
        mAppenderConfigsSet.remove(appenderConfigRef);
      }
      else
      {
        appenderConfig.refresh();
      }
    }
  }
  
  /**
   * Get appender's name by requestor class name
   * 
   * @param requestorClassName The name of the class that created the logger
   * @return the name of the appenders associated with this class/package, or {@code null} if none could be found
   */
  public String getAppenderName(String requestorClassName)
  {
    String loggerName = requestorClassName;
    String appenderName = null;
    boolean found = false;
    
    // looking for best match
    do
    {
      String key = cLoggingConfigPrefix + "logger." + loggerName + ".appender";
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
   * Get logging configuration status
   * 
   * @return {@code true} if logging is enabled, {@code false} otherwise
   */
  public boolean isEnabled()
  {
    return mEnabled;
  }
  
  /**
   * Register a {@link AAppenderConfiguration} object as a configuration listener
   * 
   * @param listener The {@link AAppenderConfiguration} listening for configuration changes
   * 
   * @see com.kas.infra.config.IRegistrar#register(IListener)
   */
  public synchronized void register(IListener listener)
  {
    WeakRef<IListener> appenderConfigRef = new WeakRef<IListener>(listener);
    mAppenderConfigsSet.add(appenderConfigRef);
    listener.refresh();
  }
  
  /**
   * Unregister a {@link AAppenderConfiguration} object as a configuration listener
   * 
   * @param listener The {@link AAppenderConfiguration} listening for configuration changes
   * 
   * @see com.kas.infra.config.IRegistrar#unregister(IListener)
   */
  public synchronized void unregister(IListener listener)
  {
    for (WeakRef<IListener> ref : mAppenderConfigsSet)
    {
      if ((ref != null) && listener.equals(ref.get()))
      {
        mAppenderConfigsSet.remove(ref);
        break;
      }
    }
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

    for (WeakRef<IListener> ref : mAppenderConfigsSet)
    {
      IListener listener = ref.get();
      sb.append(pad).append("    ").append(listener.toPrintableString(level + 2)).append("\n");
    }
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
