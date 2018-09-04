package com.kas.mq.impl;

import com.kas.infra.base.AKasObject;

/**
 * A KAS/MQ response message.<br>
 * <br>
 * This is an extension of the the base message object - {@link AMqMessage} - with the additions of
 * two new data members: The response code and the response message.
 * 
 * @author Pippo
 */
public class MqResponse extends AKasObject
{
  /**
   * The response code integer value
   */
  private int mIntResponseCode;
  
  /**
   * The response description
   */
  private String mResponseDesc;
  
  /**
   * Construct a response object using the specified {@link EMqResponseCode} and description
   * 
   * @param code The {@link EMqResponseCode response code}
   * @param desc The description of the response code
   */
  public MqResponse(int code, String desc)
  {
    mIntResponseCode = code;
    mResponseDesc = desc;
  }
  
  /**
   * Construct a response object using properties contained in {@link AMqMessage} properties
   * 
   * @param message The {@link AMqMessage} containing the response code and description
   */
  public MqResponse(IMqMessage<?> message)
  {
    mIntResponseCode = message.getIntProperty(IMqConstants.cKasPropertyResponseCode, -1);
    mResponseDesc = message.getStringProperty(IMqConstants.cKasPropertyResponseDesc, null);
  }
  
  /**
   * Get the response code as integer value
   * 
   * @return the response code as integer value
   */
  public int getIntCode()
  {
    return mIntResponseCode;
  }
  
  /**
   * Get the response code as Enum value
   * 
   * @return the response code as Enum value
   */
  public EMqResponseCode getCode()
  {
    return EMqResponseCode.fromInt(mIntResponseCode);
  }
  
  /**
   * Get the response description
   * 
   * @return the response description
   */
  public String getDesc()
  {
    return mResponseDesc;
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
      .append(pad).append("  ResponseCode=").append(mIntResponseCode).append("\n")
      .append(pad).append("  ResponseDesc=").append(mResponseDesc).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
