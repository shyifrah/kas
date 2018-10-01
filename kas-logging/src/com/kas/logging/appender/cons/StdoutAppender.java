package com.kas.logging.appender.cons;

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
  public StdoutAppender(ConsoleAppenderConfiguration cac)
  {
    super(cac, System.out);
  }
}
