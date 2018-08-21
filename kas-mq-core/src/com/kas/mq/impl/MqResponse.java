package com.kas.mq.impl;

import com.kas.infra.base.AKasObject;

/**
 * A KAS/MQ response message.<br>
 * <br>
 * This is an extension of the the base message object - {@link MqMessage} - with the additions of
 * two new data members: The response code and the response message.
 * 
 * @author Pippo
 */
public class MqResponse extends AKasObject
{
  /**
   * The response code
   */
  private EMqResponseCode mResponseCode;
  
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
  public MqResponse(EMqResponseCode code, String desc)
  {
    mResponseCode = code;
    mResponseDesc = desc;
  }
  
  /**
   * Construct a response object using properties contained in {@link MqMessage} properties
   * 
   * @param message The {@link MqMessage} containing the response code and description
   */
  public MqResponse(MqMessage message)
  {
    int code = message.getIntProperty(IMqConstants.cKasPropertyResponseCode, EMqResponseCode.cError.ordinal());
    mResponseCode = EMqResponseCode.fromInt(code);
    mResponseDesc = message.getStringProperty(IMqConstants.cKasPropertyResponseDesc, null);
  }
  
  /**
   * Get the response code
   * 
   * @return the response code
   */
  public EMqResponseCode getCode()
  {
    return mResponseCode;
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
      .append(pad).append("  ResponseCode=").append(mResponseCode).append("\n")
      .append(pad).append("  ResponseDesc=").append(mResponseDesc).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
