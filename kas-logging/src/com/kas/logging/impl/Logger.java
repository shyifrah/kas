package com.kas.logging.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.logging.ELogLevel;
import com.kas.logging.ILogger;

public class Logger extends AKasObject implements ILogger
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private String    mName;
  private IAppender mAppender;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Logger(String name, IAppender appender)
  {
    mName     = name;
    mAppender = appender;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void write(String message, Throwable ex, ELogLevel level)
  {
    if (mAppender != null) mAppender.write(mName, message, ex, level);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void diag(String message)
  {
    write(message, null, ELogLevel.DIAG);
  }
  
  public void diag(String message, Throwable ex)
  {
    write(message, ex, ELogLevel.DIAG);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void debug(String message)
  {
    write(message, null, ELogLevel.DEBUG);
  }
  
  public void debug(String message, Throwable ex)
  {
    write(message, ex, ELogLevel.DEBUG);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void trace(String message)
  {
    write(message, null, ELogLevel.TRACE);
  }
  
  public void trace(String message, Throwable ex)
  {
    write(message, ex, ELogLevel.TRACE);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void info(String message)
  {
    write(message, null, ELogLevel.INFO);
  }
  
  public void info(String message, Throwable ex)
  {
    write(message, ex, ELogLevel.INFO);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void warn(String message)
  {
    write(message, null, ELogLevel.WARN);
  }
  
  public void warn(String message, Throwable ex)
  {
    write(message, ex, ELogLevel.WARN);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void error(String message)
  {
    write(message, null, ELogLevel.ERROR);
  }
  
  public void error(String message, Throwable ex)
  {
    write(message, ex, ELogLevel.ERROR);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void fatal(String message)
  {
    write(message, null, ELogLevel.FATAL);
  }
  
  public void fatal(String message, Throwable ex)
  {
    write(message, ex, ELogLevel.FATAL);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  Appender=").append(mAppender.name()).append("\n")
      .append(pad).append(")\n");
    
    return sb.toString();
  }
}
