package com.kas.logging.impl;

import java.util.HashSet;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.config.IBaseListener;
import com.kas.infra.config.IBaseRegistrar;
/**
 * The logging configuration
 * 
 * @author Pippo
 */
public class LoggingConfiguration extends AConfiguration implements IBaseRegistrar
{
  static public final String  cLoggingConfigPrefix = "kas.logging.";
  static public final boolean cDefaultEnabled = true;
  
  /**
   * An indicator whether logging is enabled or disabled
   */
  private boolean mEnabled = cDefaultEnabled;
  
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
    mEnabled = mMainConfig.getBoolProperty ( cLoggingConfigPrefix + "enabled" , mEnabled );
    
    for (IBaseListener listener : mListeners)
      listener.refresh();
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
   * Register an object as a listener to to configuration changes
   * 
   * @param listener The listener
   * 
   * @see com.kas.infra.config.IBaseRegistrar#register(IBaseListener)
   */
  public synchronized void register(IBaseListener listener)
  {
    mListeners.add(listener);
    listener.refresh();
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
