package com.kas.mq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
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
  protected int mPriority;
  
  /**
   * The message unique identifier
   */
  protected UniqueId mMessageId;
  
  /**
   * Request type
   */
  protected ERequestType mRequestType = ERequestType.cUnknown;;
  
  /**
   * Credentials
   */
  protected String mUserName = null;
  protected String mPassword = null;
  
  /**
   * Target queue name
   */
  protected String mQueueName;
  
  /**
   * Construct a default message object
   */
  MqMessage()
  {
    mPriority  = cMinimumPriority;
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
      
      int reqType = istream.readInt();
      mRequestType = ERequestType.fromInt(reqType);
      
      mUserName = (String)istream.readObject();
      mPassword = (String)istream.readObject();
      
      mQueueName = (String)istream.readObject();
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
   * Get the message Id
   * 
   * @return the message id
   */
  public UniqueId getMessageId()
  {
    return mMessageId;
  }
  
  /**
   * Set the message priority
   * 
   * @param priority The message priority
   * 
   * @throws IllegalArgumentException if the new priority is invalid
   */
  public void setPriority(int priority)
  {
    if ((priority < cMinimumPriority) || (priority > cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    mPriority = priority;
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
  public void setQueueName(String queueName)
  {
    mQueueName = queueName;
  }
  
  /**
   * Get the target of this message.
   * 
   * @return the target of this message
   */
  public String getQueueName()
  {
    return mQueueName;
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
    
    ostream.writeInt(mRequestType.ordinal());
    ostream.reset();
    
    ostream.writeObject(mUserName);
    ostream.reset();
    ostream.writeObject(mPassword);
    ostream.reset();
    
    ostream.writeObject(mQueueName);
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
      .append(pad).append("  Message Id=").append(mMessageId.toPrintableString()).append("\n")
      .append(pad).append("  Priority=").append(mPriority).append("\n")
      .append(pad).append("  Request Type=").append(StringUtils.asPrintableString(mRequestType)).append("\n")
      .append(pad).append("  User Name=").append(mUserName).append("\n")
      .append(pad).append("  Password=").append(mPassword).append("\n")
      .append(pad).append("  Target Queue=").append(mQueueName).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
