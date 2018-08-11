package com.kas.logging.impl;

import java.io.PrintStream;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.ELogLevel;
import com.kas.infra.utils.RunTimeUtils;

/**
 * A console appender
 * 
 * @author Pippo
 */
public class ConsoleAppender extends AAppender
{
  /**
   * The appender's configuration
   */
  protected ConsoleAppenderConfiguration mConfig = null;
  
  /**
   * The print stream to which messages are written
   */
  private PrintStream mPrintStream = null;
  
  /**
   * Construct the console appender
   * 
   * @param cac The {@link ConsoleAppenderConfiguration}
   * @param pstream The {@link PrintStream} to which messages are directed
   */
  protected ConsoleAppender(ConsoleAppenderConfiguration cac, PrintStream pstream)
  {
    mConfig      = cac;
    mPrintStream = pstream;
  }
  
  /**
   * Initialize the appender.<br>
   * <br>
   * Note this is a placeholder. If any initialization is required in the specific console appender,
   * you should override this method.
   * 
   * @return always {@code true}
   * 
   * @see com.kas.infra.base.IInitializable#init()
   */
  public boolean init()
  {
    return true;
  }
  
  /**
   * Terminate the appender.<br>
   * <br>
   * Note this is a placeholder. If any termination is required in the specific console appender,
   * you should override this method.
   * 
   * @return always {@code true}
   * 
   * @see com.kas.infra.base.IInitializable#term()
   */
  public boolean term()
  {
    return true;
  }
  
  /**
   * Write a message to the console
   * 
   * @param logger The name of the logger
   * @param level the {@link ELogLevel} of the message
   * @param message The message text to write
   */
  protected void write(String logger, ELogLevel messageLevel, String message)
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
  
  /**
   * Returns the {@link FileAppenderConfiguration} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Config=").append(mConfig.toPrintableString(level+1)).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
