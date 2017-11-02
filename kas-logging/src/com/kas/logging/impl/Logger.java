package com.kas.logging.impl;

import com.kas.infra.base.KasObject;
import com.kas.logging.ILogger;

public class Logger extends KasObject implements ILogger
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
  private void write(String message, Throwable ex, LogLevel level)
  {
    if (mAppender != null) mAppender.write(mName, message, ex, level);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void diag(String message)
  {
    write(message, null, LogLevel.DIAG);
  }
  
  public void diag(String message, Throwable ex)
  {
    write(message, ex, LogLevel.DIAG);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void debug(String message)
  {
    write(message, null, LogLevel.DEBUG);
  }
  
  public void debug(String message, Throwable ex)
  {
    write(message, ex, LogLevel.DEBUG);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void trace(String message)
  {
    write(message, null, LogLevel.TRACE);
  }
  
  public void trace(String message, Throwable ex)
  {
    write(message, ex, LogLevel.TRACE);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void info(String message)
  {
    write(message, null, LogLevel.INFO);
  }
  
  public void info(String message, Throwable ex)
  {
    write(message, ex, LogLevel.INFO);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void warn(String message)
  {
    write(message, null, LogLevel.WARN);
  }
  
  public void warn(String message, Throwable ex)
  {
    write(message, ex, LogLevel.WARN);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void error(String message)
  {
    write(message, null, LogLevel.ERROR);
  }
  
  public void error(String message, Throwable ex)
  {
    write(message, ex, LogLevel.ERROR);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void fatal(String message)
  {
    write(message, null, LogLevel.FATAL);
  }
  
  public void fatal(String message, Throwable ex)
  {
    write(message, ex, LogLevel.FATAL);
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
