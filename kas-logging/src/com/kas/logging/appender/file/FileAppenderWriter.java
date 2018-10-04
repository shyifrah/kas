package com.kas.logging.appender.file;

import java.util.Queue;
import com.kas.infra.base.AKasObject;
import com.kas.logging.impl.FileAppenderConfiguration;

/**
 * Writer thread.<br>
 * <br>
 * This is a {@link Runnable} object that is scheduled for execution under a different Thread.<br>
 * It is responsible to call {@link IMessageDataWriter#write(MessageData)} method whenever it awakens.
 * 
 * @author Pippo
 */
public class FileAppenderWriter extends AKasObject implements Runnable
{
  /**
   * The writer.<br>
   * It is an object that implements {@link IMessageDataWriter#write(MessageData)} method
   */
  private IMessageDataWriter mWriter;
  
  /**
   * A queue of messages awaiting for write
   */
  private Queue<MessageData> mMessages;
  
  /**
   * Construct the writer thread
   * 
   * @param writer The writer
   * @param messages The queue of messages
   */
  FileAppenderWriter(IMessageDataWriter writer, Queue<MessageData> messages)
  {
    mWriter = writer;
    mMessages = messages;
  }
  
  /**
   * Keep writing messages while the queue is not empty.
   */
  public void run()
  {
    MessageData md = mMessages.poll();
    while (md != null)
    {
      mWriter.write(md);
      md = mMessages.poll();
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
      .append(pad).append("  Writer=").append(mWriter.name()).append("\n")
      .append(pad).append("  MessageQueue Size=").append(mMessages.size()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
