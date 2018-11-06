package com.kas.mq.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ISerializable;

/**
 * A KAS/MQ response.<br>
 * <br>
 * The response is composed of three fields: a {@link EMqCode code}, an integer value and a description.
 * 
 * @author Pippo
 */
public class MqResponse extends AKasObject implements ISerializable
{
  /**
   * The response code
   */
  private EMqCode mCode;
  
  /**
   * The response value
   */
  private int mValue;
  
  /**
   * The response description
   */
  private String mDesc;
  
  /**
   * Construct a response object using the specified {@link EMqCode} and description
   * 
   * @param code The {@link EMqCode response code}
   * @param val Additional integer value
   * @param desc The description of the response code
   */
  public MqResponse(EMqCode code, int val, String desc)
  {
    mCode = code;
    mValue = val;
    mDesc = desc;
  }
  
  /**
   * Constructs a {@link MqResponse} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqResponse(ObjectInputStream istream) throws IOException
  {
    try
    {
      mCode = EMqCode.fromInt(istream.readInt());
      mValue = istream.readInt();
      mDesc = (String)istream.readObject();
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
   * Serialize the {@link MqResponse} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the response will be serialized
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
   */
  public synchronized void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeInt(mCode.ordinal());
    ostream.reset();
    ostream.writeInt(mValue);
    ostream.reset();
    ostream.writeObject(mDesc);
    ostream.reset();
  }
  
  /**
   * Get the response code
   * 
   * @return the response code
   */
  public EMqCode getCode()
  {
    return mCode;
  }
  
  /**
   * Set the response code
   * 
   * @param code The response code
   */
  public void setCode(EMqCode code)
  {
    mCode = code;
  }
  
  /**
   * Get the response value
   * 
   * @return the response value
   */
  public int getValue()
  {
    return mValue;
  }
  
  /**
   * Set the response value
   * 
   * @param val The response value
   */
  public void setValue(int val)
  {
    mValue = val;
  }
  
  /**
   * Get the response description
   * 
   * @return the response description
   */
  public String getDesc()
  {
    return mDesc;
  }
    
  /**
   * Set the response description
   * 
   * @param desc The response description
   */
  public void setDesc(String desc)
  {
    mDesc = desc;
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
      .append(pad).append("  Code=").append(mCode).append("\n")
      .append(pad).append("  Value=").append(mValue).append("\n")
      .append(pad).append("  Desc=").append(mDesc).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
