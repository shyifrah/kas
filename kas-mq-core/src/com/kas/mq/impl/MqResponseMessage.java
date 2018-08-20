package com.kas.mq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.utils.StringUtils;

/**
 * A KAS/MQ response message.<br>
 * <br>
 * This is an extension of the the base message object - {@link MqMessage} - with the additions of
 * two new data members: The response code and the response message.
 * 
 * @author Pippo
 */
public class MqResponseMessage extends MqMessage
{
  /**
   * The response code
   */
  private int mResponseCode;
  
  /**
   * The response message
   */
  private String mResponseMessage;
  
  /**
   * Construct a default message response object
   */
  public MqResponseMessage()
  {
    super();
  }
  
  /**
   * Constructs a {@link MqResponseMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqResponseMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    
    try
    {
      mResponseCode = istream.readInt();
      
      mResponseMessage = (String)istream.readObject();
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
   * Serialize the {@link MqResponseMessage} to the specified {@link ObjectOutputStream}
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
    
    ostream.writeInt(mResponseCode);
    ostream.reset();
    
    ostream.writeObject(mResponseMessage);
    ostream.reset();
  }
  
  /**
   * Set the response
   * 
   * @param code The response code
   * @param message The response message
   */
  public void setResponse(int code, String message)
  {
    mResponseCode = code;
    mResponseMessage = message;
  }
  
  /**
   * Get the response's code
   * 
   * @return the response's code
   */
  public int getResponseCode()
  {
    return mResponseCode;
  }
  
  /**
   * Get the response's message
   * 
   * @return the response's message
   */
  public String getResponseMessage()
  {
    return mResponseMessage;
  }
  
  /**
   * Create the {@link MqResponseMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqResponseMessage);
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
      .append(pad).append("  Response=(\n")
      .append(pad).append("    Code=").append(mResponseCode).append("\n")
      .append(pad).append("    Message=").append(mResponseMessage).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
