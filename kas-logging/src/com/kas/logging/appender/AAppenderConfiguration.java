package com.kas.logging.appender;

import com.kas.config.MainConfiguration;
import com.kas.infra.base.AKasObject;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.logging.ELogLevel;
import com.kas.logging.impl.IAppender;
import com.kas.logging.impl.LoggingConfiguration;

/**
 * Abstract appender's configuration
 * 
 * @author Pippo
 */
public abstract class AAppenderConfiguration extends AKasObject implements IAppenderConfiguration
{
  static protected final String cLogConfigPrefix  = "kas.logging.";
  static protected final String cLogAppenderConfigPrefix = cLogConfigPrefix + "appender."; 
  
  static final boolean   cDefaultEnabled  = true;
  static final ELogLevel cDefaultLogLevel = ELogLevel.INFO;
   
  static protected IMainConfiguration sMainConfig = MainConfiguration.getInstance();
  
  /**
   * Logging configuration
   */
  protected LoggingConfiguration mLoggingConfig = null;
  
  /**
   * Appender's name
   */
  protected String mName;
  
  /**
   * Is appender enabled
   */
  protected boolean mEnabled = cDefaultEnabled;
  
  /**
   * The appender's log level
   */
  protected ELogLevel mLogLevel = cDefaultLogLevel;
  
  /**
   * Construct the appender's configuration specifying the appender's name and a reference to the {@link LoggingConfiguration}
   * 
   * @param name The appender's name
   * @param loggingConfig a reference to the {@link LoggingConfiguration}
   */
  public AAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
  {
    mName = name;
    mLoggingConfig = loggingConfig;
  }
  
  /**
   * Refreshing the appender's configuration.<br>
   * <br>
   * Since all appenders have some properties in common, like the {@code enabled} status, these properties
   * are held in this super class.
   */
  public void refresh()
  {
    mEnabled        = sMainConfig.getBoolProperty  ( cLogAppenderConfigPrefix + mName + ".enabled" , mEnabled         );
    String logLevel = sMainConfig.getStringProperty( cLogAppenderConfigPrefix + mName + ".logLevel", mLogLevel.name() );
    
    try
    {
      mLogLevel = ELogLevel.valueOf(logLevel);
    }
    catch(IllegalArgumentException e) {}
  }

  /**
   * Get the appender's name
   * 
   * @return the name associated with this appender's configuration
   */
  public String getName()
  {
    return mName;
  }

  /**
   * Get the appender's state
   * 
   * @return {@code true} if the appender is enabled <b>and</b> logging is enabled. {@code false} otherwise
   */
  public boolean isEnabled()
  {
    return mEnabled && mLoggingConfig.isEnabled();
  }
  
  /**
   * Get the appender's log level
   * 
   * @return the appender's log level
   */
  public ELogLevel getLogLevel()
  {
    return mLogLevel;
  }
  
  /**
   * Create an appender that uses this {@link IAppenderConfiguration} object
   * 
   * @return a new {@link IAppender} that is associated with this {@link IAppenderConfiguration}
   */
  public abstract IAppender createAppender();
  
  /**
   * Returns the {@link AAppenderConfiguration} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
