package com.kas.mq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;

/**
 * A KAS/MQ base message.<br>
 * <br>
 * Each message has an unique ID - that differentiates it from other messages in this KAS/MQ system, a payload,
 * and a few other characteristics such as priority that determines the behavior of KAS/MQ system when processing the message.
 * 
 * @author Pippo
 */
public class MqMessage extends AKasObject
{
  public static final int cMinimumPriority = 0;
  public static final int cMaximumPriority = 9;
  
  /**
   * The message priority
   */
  private int mPriority;
  
  /**
   * The message unique identifier
   */
  private UniqueId mMessageId;
  
  /**
   * Construct a default message object
   */
  public MqMessage()
  {
    this(cMinimumPriority);
  }
  
  /**
   * Construct a Message object with specified priority
   * 
   * @param priority The message priority
   * @throws IllegalArgumentException If the message priority is larger than 9 or lower than 0 
   */
  public MqMessage(int priority)
  {
    if ((priority < cMinimumPriority) || (priority > cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    mPriority  = priority;
    mMessageId = UniqueId.generate();
  }
  
  /**
   * Constructs a {@link MqMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqMessage(ObjectInputStream istream) throws IOException
  {
    try
    {
      mPriority = istream.readInt();
      
      byte [] ba = new byte [16];
      istream.read(ba);
      mMessageId = UniqueId.fromByteArray(ba);
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Throwable e)
    {
      throw new IOException(e);
    }
  }
  
  /**
   * Get the message priority
   * 
   * @return the message priority
   */
  public int getPriority()
  {
    return mPriority;
  }
  
  /**
   * Get the message Id
   * 
   * @return the message id
   */
  public UniqueId getMessageId()
  {
    return mMessageId;
  }
  
  /**
   * Serialize the {@link MqMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeInt(mPriority);
    ostream.reset();
    
    byte [] ba = mMessageId.toByteArray();
    ostream.write(ba);
    ostream.reset();
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  MessageId=").append(mMessageId.toPrintableString()).append("\n")
      .append(pad).append("  Priority=").append(mPriority).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
