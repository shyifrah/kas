package com.kas.mq;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;

/**
 * A KAS/MQ base message.<br>
 * <br>
 * Each message has an unique ID - that differentiates it from other messages in this KAS/MQ system, a payload,
 * and a few other characteristics such as priority that determines the behavior of KAS/MQ system when processing the message.
 */
public class KasMessage extends AKasObject
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
  public KasMessage()
  {
    this(cMinimumPriority);
  }
  
  /**
   * Construct a Message object with specified priority
   * 
   * @param priority The message priority
   * @throws IllegalArgumentException If the message priority is larger than 9 or lower than 0 
   */
  public KasMessage(int priority)
  {
    if ((priority < cMinimumPriority) || (priority > cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    mPriority  = priority;
    mMessageId = UniqueId.generate();
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
   * Returns a replica of this {@link KasMessage}.<br>
   * <br>
   * The replica will have a different {@link UniqueId}.
   * 
   * @return a replica of this {@link KasMessage}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public KasMessage replicate()
  {
    return new KasMessage(mPriority);
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
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  MessageId=").append(mMessageId.toPrintableString()).append("\n")
      .append(pad).append("  Priority=").append(mPriority).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
