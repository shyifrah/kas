package com.kas.logging.impl;

import java.util.HashMap;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.impl.appenders.ConsoleAppender;
import com.kas.logging.impl.appenders.ConsoleAppenderConfiguration;
import com.kas.logging.impl.appenders.FileAppender;
import com.kas.logging.impl.appenders.FileAppenderConfiguration;
import com.kas.logging.impl.appenders.StderrAppender;
import com.kas.logging.impl.appenders.StderrAppenderConfiguration;
import com.kas.logging.impl.appenders.StdoutAppender;
import com.kas.logging.impl.appenders.StdoutAppenderConfiguration;

/**
 * A singleton class for managing all appenders
 * 
 * @author Pippo
 */
public class AppenderManager extends AKasObject
{
  static public final String cFileAppenderName   = "file";
  static public final String cStdoutAppenderName = "stdout";
  static public final String cStderrAppenderName = "stderr";
  
  /**
   * The {@link MainConfiguration} singleton instance
   */
  static private AppenderManager sInstance = new AppenderManager();
  
  /**
   * The {@link LoggingConfiguration} singleton instance
   */
  static private LoggingConfiguration sConfig = new LoggingConfiguration();

  /**
   * Name to {@link IAppender} map
   */
  private HashMap<String, IAppender> mAppenders = new HashMap<String, IAppender>();
  
  /**
   * An indicator whether appenders were loaded or not
   */
  private boolean mAppendersLoaded = false;
  
  /**
   * Get the instance of the {@link AppenderManager}
   */
  static public AppenderManager getInstance()
  {
    return sInstance;
  }
  
  /**
   * Get the {@link LoggingConfiguration}
   * 
   * @return the {@link LoggingConfiguration}
   */
  public LoggingConfiguration getConfig()
  {
    return sConfig;
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
    if (!sConfig.isInitialized())
    {
      sConfig.init();
    }
    
    if (!mAppendersLoaded)
    {
      load();
    }
    
    String name = sConfig.getAppenderName(requestorClass.getName());
    return mAppenders.get(name);
  }
  
  /**
   * Get an appender by name.<br>
   * <br>
   * If this is the first time the {@link LoggingConfiguration} is accessed, it is first initialized.<br>
   * If appender's weren't loaded yet, first load them.
   * 
   * @param name The name of the appender
   * @return the {@link IAppender} assigned to the class' package or {@code null} if no such assignment exists
   */
  public IAppender getAppender(String name)
  {
    if (!sConfig.isInitialized())
    {
      sConfig.init();
    }
    
    if (!mAppendersLoaded)
    {
      load();
    }
    
    return mAppenders.get(name);
  }

  /**
   * Load all available appenders and place them in the map.
   */
  private synchronized void load()
  {
    // File appender
    FileAppenderConfiguration fac = new FileAppenderConfiguration(sConfig);
    sConfig.register(fac);
    FileAppender fa = new FileAppender(fac);
    fa.init();
    mAppenders.put(cFileAppenderName, fa);
    
    // stdout
    ConsoleAppenderConfiguration soac = new StdoutAppenderConfiguration(sConfig);
    sConfig.register(soac);
    ConsoleAppender stdout = new StdoutAppender(soac);
    stdout.init();
    mAppenders.put(cStdoutAppenderName, stdout);
    
    // stderr
    ConsoleAppenderConfiguration seac = new StderrAppenderConfiguration(sConfig);
    sConfig.register(seac);
    ConsoleAppender stderr = new StderrAppender(seac);
    stderr.init();
    mAppenders.put(cStderrAppenderName, stderr);
    
    mAppendersLoaded = true;
  }
  
  /**
   * Returns the {@link AppenderManager} string representation.
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
      .append(StringUtils.asPrintableString(mAppenders, level + 2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
