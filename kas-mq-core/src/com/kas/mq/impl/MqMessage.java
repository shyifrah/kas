package com.kas.mq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.mq.internal.ERequestType;

/**
 * A KAS/MQ base message.<br>
 * <br>
 * Each message has an unique ID - that differentiates it from other messages in this KAS/MQ system, a payload,
 * and a few other characteristics such as priority that determines the behavior of KAS/MQ system when processing the message.
 * 
 * @author Pippo
 */
public class MqMessage extends AKasObject implements IPacket
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
   * Request type
   */
  private ERequestType mRequestType = ERequestType.cUnknown;;
  
  /**
   * Credentials
   */
  private String mUserName = null;
  private String mPassword = null;
  
  /**
   * Target queue name
   */
  private String mTargetQueueName;
  
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
   * Set the credentials that will be used for authenticating the origin of this message
   * 
   * @param username The user name. This cannot be {@code null} for administrative messages.
   * @param password The user's password. This cannot be {@code null} for administrative messages.
   */
  public void setCredentials(String username, String password)
  {
    mUserName = username;
    mPassword = password;
  }
  
  /**
   * Get the user's name
   * 
   * @return the user's name
   */
  public String getUserName()
  {
    return mUserName;
  }
  
  /**
   * Get the user's password
   * 
   * @return the user's password
   */
  public String getPassword()
  {
    return mPassword;
  }
  
  /**
   * Set the target of this message.
   * 
   * @param queueName The name of queue in which this message will be placed. This should be {@code null}
   * for administrative messages
   */
  public void setTargetQueueName(String queueName)
  {
    mTargetQueueName = queueName;
  }
  
  /**
   * Get the target of this message.
   * 
   * @return the target of this message
   */
  public String getTargetQueueName()
  {
    return mTargetQueueName;
  }
  
  /**
   * Set the request type.<br>
   * <br>
   * Note that this property must be set for administrative messages.
   * 
   * @param type The {@link ERequestType} of the message
   */
  public void setRequestType(ERequestType type)
  {
    mRequestType = type;
  }
  
  /**
   * Get the administrative message's request type
   * 
   * @return the administrative message's request type, or {@link ERequestType.cUnknown} for non-administrative messages
   */
  public ERequestType getRequestType()
  {
    return mRequestType;
  }
  
  /**
   * Create the {@link MqMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqMessage);
  }
  
  /**
   * Serialize the {@link MqMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
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
