package com.kas.logging.impl;

import com.kas.config.MainConfiguration;
import com.kas.infra.base.AKasObject;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IMainConfiguration;

public abstract class AAppenderConfiguration extends AKasObject implements IListener
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
  protected ELogLevel mLogLevel = Constants.cDefaultLogLevel;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public AAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
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
      mLogLevel = ELogLevel.valueOf(ELogLevel.class, logLevel);
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
  public ELogLevel getLogLevel()
  {
    return mLogLevel;
  }
}
