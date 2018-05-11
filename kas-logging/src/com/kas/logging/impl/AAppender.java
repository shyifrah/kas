package com.kas.logging.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ThrowableFormatter;

public abstract class AAppender extends AKasObject implements IAppender
{
  protected static final String cAppenderMessageFormat = "%s %d:%d %-5s [%s] %s%n";
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized void write(String logger, String message, Throwable ex, ELogLevel messageLevel)
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
  protected abstract void write(String logger, String message, ELogLevel level);
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public abstract String toPrintableString(int level);
}
