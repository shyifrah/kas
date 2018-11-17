package com.kas.logging.appender.noop;

import com.kas.infra.logging.ELogLevel;
import com.kas.logging.appender.AAppender;
import com.kas.logging.impl.NoOpAppenderConfiguration;

/**
 * A no-operation appender.<br>
 * <br>
 * It suppresses all writings.
 * 
 * @author Pippo
 */
public class NoOpAppender extends AAppender
{
  /**
   * The appender's configuration
   */
  protected NoOpAppenderConfiguration mConfig = null;
  
  /**
   * Construct the No-operation appender
   * 
   * @param noac The {@link NoOpAppenderConfiguration}
   */
  public NoOpAppender(NoOpAppenderConfiguration noac)
  {
    mConfig = noac;
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
   * Write a message.<br>
   * <br>
   * Since this is a no-operation appender, it does nothing. 
   * 
   * @param logger The name of the logger
   * @param level the {@link ELogLevel} of the message
   * @param message The message text to write
   */
  protected void write(String logger, ELogLevel messageLevel, String message)
  {
  }
  
  /**
   * Returns the object's string representation.
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
