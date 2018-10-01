package com.kas.logging.impl.appenders;

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
  public StderrAppender(ConsoleAppenderConfiguration cac)
  {
    super(cac, System.err);
  }
}
