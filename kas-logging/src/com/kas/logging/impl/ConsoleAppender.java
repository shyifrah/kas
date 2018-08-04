package com.kas.logging.impl;

import java.io.PrintStream;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.ELogLevel;
import com.kas.infra.utils.RunTimeUtils;

public class ConsoleAppender extends AAppender
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private ConsoleAppenderConfiguration mConfig = null;
  private PrintStream                  mPrintStream = null;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected ConsoleAppender(ConsoleAppenderConfiguration cac, PrintStream pstream)
  {
    mConfig      = cac;
    mPrintStream = pstream;
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
  protected void write(String logger, String message, ELogLevel messageLevel)
  {
    if (mConfig.isEnabled())
    {
      if (mConfig.getLogLevel().isGreaterOrEqual(messageLevel))
      {
        TimeStamp ts = new TimeStamp();
        mPrintStream.print(String.format(cAppenderMessageFormat,
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
