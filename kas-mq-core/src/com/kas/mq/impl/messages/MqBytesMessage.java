package com.kas.mq.impl.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.ABaseBytesMessage;

/**
 * A KAS/MQ message with a byte array payload.<br>
 * <br>
 * The message body is a byte []
 * 
 * @author Pippo
 */
public final class MqBytesMessage extends ABaseBytesMessage
{
  /**
   * Construct a default text message object
   */
  MqBytesMessage()
  {
    super();
    mBody = null;
  }
  
  /**
   * Constructs a {@link MqBytesMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqBytesMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
  }
  
  /**
   * Serialize the {@link MqBytesMessage} to the specified {@link ObjectOutputStream}
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
  }
  
  /**
   * Set the message body
   * 
   * @param body The message body
   */
  public void setBody(byte [] body)
  {
    mBody = body;
  }
  
  /**
   * Get the message body
   * 
   * @return the message body
   */
  public byte [] getBody()
  {
    return mBody;
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link MqBytesMessage}
   * 
   * @return the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqBytesMessage);
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
