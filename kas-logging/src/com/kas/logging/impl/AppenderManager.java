package com.kas.logging.impl;

import java.util.HashMap;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;

public class AppenderManager extends AKasObject
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static final String cFileAppenderName   = "file";
  public static final String cStdoutAppenderName = "stdout";
  public static final String cStderrAppenderName = "stderr";
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static AppenderManager      sInstance = new AppenderManager();
  private static LoggingConfiguration sConfig   = new LoggingConfiguration();

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private HashMap<String, IAppender> mAppenders = new HashMap<String, IAppender>();
  private boolean  mAppendersLoaded = false;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static AppenderManager getInstance()
  {
    return sInstance;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Appenders=(\n")
      .append(StringUtils.asPrintableString(mAppenders, level + 2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
