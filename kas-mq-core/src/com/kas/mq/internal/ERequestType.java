package com.kas.mq.internal;

import com.kas.infra.base.IObject;

/**
 * This enum is used to categorize administrative messages
 * 
 * @author Pippo
 */
public enum ERequestType implements IObject
{
  /**
   * Unknown request type. Just put the message in the designated queue
   */
  cUnknown,
  
  /**
   * Shutdown KAS/MQ server
   */
  cShutdown,
  
  /**
   * Define a queue
   */
  cDefineQueue,
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
