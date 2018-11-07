package com.kas.logging.appender.file;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.ELogLevel;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.appender.AAppender;
import com.kas.logging.impl.FileAppenderConfiguration;

/**
 * A message descriptor
 * 
 * @author Pippo
 */
public class MessageData extends AKasObject
{
  /**
   * Message's timestamp
   */
  private TimeStamp mTimeStamp;
  
  /**
   * Writing Thread ID
   */
  private long mThreadId;
  
  /**
   * Message's log-level
   */
  private ELogLevel mLevel;
  
  /**
   * The writing logger's name
   */
  private String mLogger;
  
  /**
   * The message text
   */
  private String mText;
  
  /**
   * Construct a descriptor
   * 
   * @param ts Message's {@link TimeStamp}
   * @param tid Writing Thread ID
   * @param level Message's log-level
   * @param logger The writing logger's name
   * @param text The message text
   */
  public MessageData(TimeStamp ts, long tid, ELogLevel level, String logger, String text)
  {
    mTimeStamp = ts;
    mThreadId = tid;
    mLevel = level;
    mLogger = logger;
    mText = text;
  }
  
  /**
   * Formatting the descriptor's data into a writable text
   * 
   * @return The formatted message's text
   */
  public String format()
  {
    return String.format(AAppender.cAppenderMessageFormat, 
      mTimeStamp.toString(),
      RunTimeUtils.getProcessId(),
      mThreadId,
      mLevel.toString(), 
      mLogger,
      mText);
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
      .append(pad).append("  TimeStamp=(").append(mTimeStamp.toString()).append(")\n")
      .append(pad).append("  ThreadId=").append(mThreadId).append("\n")
      .append(pad).append("  mLevel=").append(mLevel.toString()).append("\n")
      .append(pad).append("  mLoggerName=").append(mLogger).append("\n")
      .append(pad).append("  mText=(").append(mText).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
