package com.kas.logging.impl;

import com.kas.infra.base.KasObject;
import com.kas.infra.base.ThrowableFormatter;

public abstract class AbstractAppender extends KasObject implements IAppender
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized void write(String logger, String message, Throwable ex, LogLevel messageLevel)
  {
    String msgText = message;
    if (ex != null)
    {
      StringBuffer sb = new StringBuffer().append(message);
      ThrowableFormatter formatter = new ThrowableFormatter(ex);
      sb.append(formatter.toString());
      msgText = sb.toString();
    }
    
    write(logger, msgText, messageLevel);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean init()
  {
    return true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean term()
  {
    return true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected abstract void write(String logger, String message, LogLevel level);
  public abstract String toPrintableString(int level);
}
