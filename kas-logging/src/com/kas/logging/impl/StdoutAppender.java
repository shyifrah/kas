package com.kas.logging.impl;

/**
 * STDOUT appender
 * 
 * @author Pippo
 */
public class StdoutAppender extends ConsoleAppender
{
  /**
   * Construct STDOUT appender specifying the {@link ConsoleAppenderConfiguration} object
   * 
   * @param cac The {@link ConsoleAppenderConfiguration}
   */
  protected StdoutAppender(ConsoleAppenderConfiguration cac)
  {
    super(cac, System.out);
  }
  
  /**
   * Returns a replica of this {@link StdoutAppender}.
   * 
   * @return a replica of this {@link StdoutAppender}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public StdoutAppender replicate()
  {
    return new StdoutAppender(mConfig);
  }
}
