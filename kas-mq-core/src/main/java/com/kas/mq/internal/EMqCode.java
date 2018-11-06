package com.kas.mq.internal;

import com.kas.infra.base.IObject;

/**
 * This enum is used to differentiate between the different response codes
 * 
 * @author Pippo
 */
public enum EMqCode implements IObject
{
  /**
   * Unknown state
   */
  cUnknown,
  
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
  
  static final private EMqCode [] cValues = EMqCode.values();
  
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id The ordinal of the enum
   * @return the enum value
   */
  static public EMqCode fromInt(int id)
  {
    EMqCode code = cUnknown;
    try
    {
      code = cValues[id];
    }
    catch (Throwable e) {}
    return code;
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
