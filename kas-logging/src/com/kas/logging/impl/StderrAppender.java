package com.kas.logging.impl;

/**
 * STDERR appender
 * 
 * @author Pippo
 */
public class StderrAppender extends ConsoleAppender
{
  /**
   * Construct STDERR appender specifying the {@link ConsoleAppenderConfiguration} object
   * 
   * @param cac The {@link ConsoleAppenderConfiguration}
   */
  protected StderrAppender(ConsoleAppenderConfiguration cac)
  {
    super(cac, System.err);
  }
  
  /**
   * Returns a replica of this {@link StderrAppender}.
   * 
   * @return a replica of this {@link StderrAppender}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public StderrAppender replicate()
  {
    return new StderrAppender(mConfig);
  }
}
