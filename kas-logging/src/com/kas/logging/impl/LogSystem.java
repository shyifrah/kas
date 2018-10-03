package com.kas.logging.impl;

import java.util.HashMap;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;

/**
 * A singleton class for managing logging
 * 
 * @author Pippo
 */
public class LogSystem extends AKasObject
{
  static public final String cFileAppenderName   = "file";
  static public final String cStdoutAppenderName = "stdout";
  static public final String cStderrAppenderName = "stderr";
  
  /**
   * The {@link MainConfiguration} singleton instance
   */
  static private LogSystem sInstance = new LogSystem();
  
  /**
   * The {@link LoggingConfiguration}
   */
  private LoggingConfiguration mConfig;

  /**
   * Name to {@link IAppender} map
   */
  private HashMap<String, IAppender> mAppenders;
  
  /**
   * Get the instance of the {@link LogSystem}
   */
  static public LogSystem getInstance()
  {
    return sInstance;
  }
  
  /**
   * Private constructor
   */
  private LogSystem()
  {
   mConfig = new LoggingConfiguration();
  }
  
  /**
   * Get the {@link LoggingConfiguration}
   * 
   * @return the {@link LoggingConfiguration}
   */
  public LoggingConfiguration getConfig()
  {
    return mConfig;
  }

  /**
   * Get an appender by requestor class.<br>
   * <br>
   * If this is the first time the {@link LoggingConfiguration} is accessed, it is first initialized.<br>
   * If appender's weren't loaded yet, first load them.
   * 
   * @param requestorClass The name of the class that created the {@link ILogger} object
   * @return the {@link IAppender} assigned to the class' package or {@code null} if no such assignment exists
   */
  public IAppender getAppender(Class<?> requestorClass)
  {
    if (!mConfig.isInitialized())
      mConfig.init();
    
    String name = mConfig.getAppenderName(requestorClass.getName());
    IAppender appender = null;
    if (name != null)
    {
      appender = mAppenders.get(name);
      if (appender == null)
      {
         
      }
    }
    return appender;
  }
  
  /**
   * Returns the {@link LogSystem} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Appenders=(\n")
      .append(StringUtils.asPrintableString(mAppenders, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
