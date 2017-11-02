package com.kas.logging;

import com.kas.infra.base.IObject;

public interface ILogger extends IObject
{
  public void diag (String message);
  public void debug(String message);
  public void trace(String message);
  public void info (String message);
  public void warn (String message);
  public void error(String message);
  public void fatal(String message);
  
  public void diag (String message, Throwable e);
  public void debug(String message, Throwable e);
  public void trace(String message, Throwable e);
  public void info (String message, Throwable e);
  public void warn (String message, Throwable e);
  public void error(String message, Throwable e);
  public void fatal(String message, Throwable e);
}
