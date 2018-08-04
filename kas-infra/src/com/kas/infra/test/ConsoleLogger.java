package com.kas.infra.test;

import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.IBaseLogger;

public class ConsoleLogger implements IBaseLogger
{
  private String mLoggerName;
  
  public ConsoleLogger(String name)
  {
    mLoggerName = name;
  }
  
  public String getLoggerName()
  {
    return mLoggerName;
  }
  
  public ConsoleLogger replicate()
  {
    return new ConsoleLogger(mLoggerName);
  }
  
  public String name()
  {
    return this.getClass().getSimpleName();
  }

  public String toPrintableString(int level)
  {
    return name();
  }
  
  private void stdout(String level, String message)
  {
    TimeStamp ts = new TimeStamp();
    String msg = String.format("%s (%s) %-5s %s", ts.toString(), mLoggerName, level, message);
    System.out.println(msg);
  }

  public void diag(String message)
  {
    stdout("DIAG", message);
  }

  public void debug(String message)
  {
    stdout("DEBUG", message);
  }

  public void trace(String message)
  {
    stdout("TRACE", message);
  }

  public void info(String message)
  {
    stdout("INFO", message);
  }

  public void warn(String message)
  {
    stdout("WARN", message);
  }

  public void error(String message)
  {
    stdout("ERROR", message);
  }

  public void fatal(String message)
  {
    stdout("FATAL", message);
  }

  public void diag(String message, Throwable e)
  {
    stdout("DIAG", message);
    e.printStackTrace();
  }

  public void debug(String message, Throwable e)
  {
    stdout("DEBUG", message);
    e.printStackTrace();
  }

  public void trace(String message, Throwable e)
  {
    stdout("TRACE", message);
    e.printStackTrace();
  }

  public void info(String message, Throwable e)
  {
    stdout("INFO", message);
    e.printStackTrace();
  }

  public void warn(String message, Throwable e)
  {
    stdout("WARN", message);
    e.printStackTrace();
  }

  public void error(String message, Throwable e)
  {
    stdout("ERROR", message);
    e.printStackTrace();
  }

  public void fatal(String message, Throwable e)
  {
    stdout("FATAL", message);
    e.printStackTrace();
  }
}
