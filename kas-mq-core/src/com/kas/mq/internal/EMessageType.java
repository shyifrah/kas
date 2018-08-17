package com.kas.mq.internal;

import com.kas.infra.base.IObject;

/**
 * Message type
 * 
 * @author Pippo
 */
public enum EMessageType implements IObject
{
  /**
   * Base message, no body
   */
  cMqMessage,
  
  /**
   * Text message
   */
  cMqTextMessage,
  
  /**
   * Bytes message
   */
  cMqBytesMessage,
  ;
  
  /**
   * Return a more friendly string
   * 
   * @return the name of the constant in addition to the ordinal value
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder()
      .append(name().substring(1))
      .append(" (")
      .append(ordinal())
      .append(')');
    return sb.toString();
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
    return toString();
  }
}
