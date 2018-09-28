package com.kas.mq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.utils.StringUtils;

/**
 * A KAS/MQ object message.<br>
 * <br>
 * The message body is a single {@link Object} object
 * 
 * @author Pippo
 */
public final class MqObjectMessage extends MqMessage
{
  /**
   * The message body
   */
  protected Object mBody;
  
  /**
   * Construct a default text message object
   */
  MqObjectMessage()
  {
    super();
    mBody = null;
  }
  
  /**
   * Constructs a {@link MqObjectMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqObjectMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    try
    {
      mBody = istream.readObject();
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
   * Serialize the {@link MqObjectMessage} to the specified {@link ObjectOutputStream}
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
   * 
   * @see com.kas.mq.impl.IMqMessage#setBody(Object)
   */
  public void setBody(Object body)
  {
    mBody = body;
  }
  
  /**
   * Get the message body
   * 
   * @return the message body
   * 
   * @see com.kas.mq.impl.IMqMessage#getBody()
   */
  public Object getBody()
  {
    return mBody;
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link MqObjectMessage}
   * 
   * @return the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqObjectMessage);
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
      .append(super.toPrintableString(level))
      .append(pad).append("  Body=(").append(StringUtils.asString(mBody)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
