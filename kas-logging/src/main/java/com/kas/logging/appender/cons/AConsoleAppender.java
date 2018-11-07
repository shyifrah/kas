package com.kas.logging.appender.cons;

import java.io.PrintStream;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.ELogLevel;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.appender.AAppender;
import com.kas.logging.impl.AConsoleAppenderConfiguration;
import com.kas.logging.impl.FileAppenderConfiguration;

/**
 * A console appender.<br>
 * <br>
 * This class is abstract so it won't be instantiated, but it doesn't really have
 * an abstract methods. 
 * 
 * @author Pippo
 */
public abstract class AConsoleAppender extends AAppender
{
  /**
   * The appender's configuration
   */
  protected AConsoleAppenderConfiguration mConfig = null;
  
  /**
   * The print stream to which messages are written
   */
  private PrintStream mPrintStream = null;
  
  /**
   * Construct the console appender
   * 
   * @param cac The {@link AConsoleAppenderConfiguration}
   * @param pstream The {@link PrintStream} to which messages are directed
   */
  protected AConsoleAppender(AConsoleAppenderConfiguration cac, PrintStream pstream)
  {
    mConfig = cac;
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
        mPrintStream.print(String.format(cAppenderMessageFormat,
          TimeStamp.nowAsString(),
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