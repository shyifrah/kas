package com.kas.logging.impl;

import com.kas.config.MainConfiguration;
import com.kas.infra.base.KasObject;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IMainConfiguration;

public abstract class AbstractAppenderConfiguration extends KasObject implements IListener
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected static IMainConfiguration sMainConfig = MainConfiguration.getInstance();
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected LoggingConfiguration mLoggingConfig = null;
  protected String   mName;
  protected boolean  mEnabled  = Constants.cDefaultEnabled;
  protected LogLevel mLogLevel = Constants.cDefaultLogLevel;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public AbstractAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
  {
    mName = name;
    mLoggingConfig = loggingConfig;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void refresh()
  {
    mEnabled        = sMainConfig.getBoolProperty  ( Constants.cLoggingConfigPrefix + "appender." + mName + ".enabled", mEnabled);
    String logLevel = sMainConfig.getStringProperty( Constants.cLoggingConfigPrefix + "appender." + mName + ".logLevel", mLogLevel.name());
    
    try {
      mLogLevel = LogLevel.valueOf(LogLevel.class, logLevel);
    } catch(IllegalArgumentException e) {}
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public abstract String toPrintableString(int level);
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean isEnabled()
  {
    return mEnabled && mLoggingConfig.isEnabled();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public LogLevel getLogLevel()
  {
    return mLogLevel;
  }
}
