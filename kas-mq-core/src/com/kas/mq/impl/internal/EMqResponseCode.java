package com.kas.mq.impl.internal;

import com.kas.infra.base.IObject;

/**
 * This enum is used to categorize administrative messages
 * 
 * @author Pippo
 */
public enum EMqResponseCode implements IObject
{
  /**
   * Operation ended okay
   */
  cOkay,
  
  /**
   * Operation ended with a warning
   */
  cWarn,
  
  /**
   * Operation ended with a failure
   */
  cFail,
  
  /**
   * Operation ended with a severe error
   */
  cError,
    
  ;
  
  static final private EMqResponseCode [] cValues = EMqResponseCode.values();
  
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id The ordinal of the enum
   * @return the enum value
   */
  static public EMqResponseCode fromInt(int id)
  {
    return cValues[id];
  }
  
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
