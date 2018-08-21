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
  private int mResponseCode;
  
  /**
   * The response description
   */
  private String mResponseDesc;
  
  /**
   * Construct a default response object using properties contained in {@link MqMessage} properties
   * 
   * @param message The {@link MqMessage} containing the response code and description
   */
  public MqResponse(MqMessage message)
  {
    mResponseCode = message.getIntProperty(IMqConstants.cKasPropertyResponseCode, 8);
    mResponseDesc = message.getStringProperty(IMqConstants.cKasPropertyResponseDesc, null);
  }
  
  /**
   * Get the response code
   * 
   * @return the response code
   */
  public int getCode()
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
