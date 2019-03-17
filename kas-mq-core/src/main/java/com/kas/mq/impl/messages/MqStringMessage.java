package com.kas.mq.impl.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.ABaseMessage;

/**
 * A KAS/MQ message with a String payload.<br>
 * The message body is a single {@link String} object
 * 
 * @author Pippo
 */
public final class MqStringMessage extends ABaseMessage
{
  /**
   * The message body
   */
  protected String mBody;
  
  /**
   * Construct a default string message object
   */
  MqStringMessage()
  {
    super();
    mBody = null;
  }
  
  /**
   * Constructs a {@link MqStringMessage} object from {@link ObjectInputStream}
   * 
   * @param istream
   *   The {@link ObjectInputStream}
   * @throws IOException
   *   if I/O error occurs
   */
  public MqStringMessage(ObjectInputStream istream) throws IOException
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
   * Serialize the {@link MqStringMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream
   *   The {@link ObjectOutputStream} to which the message will be serialized
   * @throws IOException
   *   if an I/O error occurs
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    super.serialize(ostream);
    
    ostream.writeObject(mBody);
  }
  
  /**
   * Set the message body
   * 
   * @param body
   *   The message body
   */
  public void setBody(String body)
  {
    mBody = body;
  }
  
  /**
   * Get the message body
   * 
   * @return
   *   the message body
   */
  public String getBody()
  {
    return mBody;
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link MqStringMessage}
   * 
   * @return
   *   the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqStringMessage);
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
