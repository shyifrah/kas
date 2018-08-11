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
}
