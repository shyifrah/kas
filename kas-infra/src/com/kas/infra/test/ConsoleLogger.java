package com.kas.infra.test;

import com.kas.infra.base.IBaseLogger;
import com.kas.infra.base.TimeStamp;

public class ConsoleLogger implements IBaseLogger
{
  
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
    String.format("%s (%s) %-5s %s", ts.toString(), name(), level, message);
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
  }

  public void debug(String message, Throwable e)
  {
  }

  public void trace(String message, Throwable e)
  {
  }

  public void info(String message, Throwable e)
  {
  }

  public void warn(String message, Throwable e)
  {
  }

  public void error(String message, Throwable e)
  {
  }

  public void fatal(String message, Throwable e)
  {
  }
}
