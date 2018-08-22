package com.kas.mq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.utils.StringUtils;

/**
 * A KAS/MQ text message.<br>
 * <br>
 * Each message has an unique ID - that differentiates it from other messages in this KAS/MQ system, a payload,
 * and a few other characteristics such as priority that determines the behavior of KAS/MQ system when processing the message.
 * 
 * @author Pippo
 */
public class MqTextMessage extends MqMessage
{
  /**
   * The message body
   */
  protected String mBody;
  
  /**
   * Construct a default text message object
   */
  MqTextMessage()
  {
    super();
    mBody = null;
  }
  
  /**
   * Constructs a {@link MqTextMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqTextMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    try
    {
      mBody = (String)istream.readObject();
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
   * Serialize the {@link MqTextMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    super.serialize(ostream);
    
    ostream.writeObject(mBody);
  }
  
  /**
   * Set the message body
   * 
   * @param body The message body
   */
  public void setBody(String body)
  {
    mBody = body;
  }
  
  /**
   * Get the message body
   * 
   * @return the message body
   */
  public String getBody()
  {
    return mBody;
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link MqTextMessage}
   * 
   * @return the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqTextMessage);
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
      .append(pad).append("  Properties=(").append(mProperties.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  Body=(").append(mBody).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
