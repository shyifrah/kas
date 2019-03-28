package com.kas.mq.impl.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.comm.serializer.Serializer;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.ABaseBytesMessage;

/**
 * A KAS/MQ message with a Serializable object payload.<br>
 * The message body is a single {@link Serializable} object
 * 
 * @author Pippo
 */
public final class MqObjectMessage extends ABaseBytesMessage
{
  /**
   * A cached message body
   */
  private transient Serializable mCachedBody;
  
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
   * @param istream
   *   The {@link ObjectInputStream}
   * @throws IOException
   *   if I/O error occurs
   */
  public MqObjectMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
  }
  
  /**
   * Serialize the {@link MqObjectMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream
   *   The {@link ObjectOutputStream} to which the message will be serialized
   * @throws IOException
   *   if an I/O error occurs
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    super.serialize(ostream);
  }
  
  /**
   * Set the message body
   * 
   * @param body
   *   The message body
   */
  public void setBody(Serializable body)
  {
    mCachedBody = body;
    mBody = Serializer.toByteArray(mCachedBody);
  }
  
  /**
   * Get the message body
   * 
   * @return
   *   the message body
   */
  public Serializable getBody()
  {
    if (mBody == null) return null;
    if (mCachedBody == null)
      mCachedBody = Serializer.toSerializable(mBody);
    
    return mCachedBody;
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link MqObjectMessage}
   * 
   * @return
   *   the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqObjectMessage);
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
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
