package com.kas.logging.impl;

import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.RunTimeUtils;

public class ConsoleAppender extends AbstractAppender
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private ConsoleAppenderConfiguration mConfig = null;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public ConsoleAppender(ConsoleAppenderConfiguration cac)
  {
    mConfig = cac;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized boolean init()
  {
    return true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized boolean term()
  {
    return true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected void write(String logger, String message, LogLevel messageLevel)
  {
    if (mConfig.isEnabled())
    {
      if (mConfig.getLogLevel().isGreaterOrEqual(messageLevel))
      {
        TimeStamp ts = new TimeStamp();
        System.out.print(String.format(Constants.cAppenderMessageFormat,
          ts.toString(),
          RunTimeUtils.getProcessId(),
          RunTimeUtils.getThreadId(),
          messageLevel.toString(),
          logger,
          message));
      }
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Config=").append(mConfig.toPrintableString(level+1)).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
