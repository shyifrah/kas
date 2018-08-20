package com.kas.comm.serializer;

import com.kas.infra.base.IObject;
import com.kas.infra.logging.ELogLevel;

/**
 * Detailing all serializable classes IDs
 * 
 * @author Pippo
 */
public enum EClassId implements IObject
{
  /**
   * Basic data types
   */
  cClassObject,     // 0
  cClassBoolean,    // 1
  cClassString,     // 2
  cClassByte,       // 3
  cClassShort,      // 4
  cClassInteger,    // 5
  cClassLong,       // 6
  cClassFloat,      // 7
  cClassDouble,     // 8
  
  cPlaceHolder1,    // 9
  cPlaceHolder2,    // 10
  
  /**
   * MqMessage
   */
  cClassMqMessage,
  
  /**
   * MqResponseMessage
   */
  cClassMqResponseMessage,
  
  /**
   * Propeties
   */
  cClassProperties,
  
  ;
  
  static final private EClassId [] cValues = EClassId.values();
 
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id The ordinal of the enum
   * @return the enum value
   */
  static public EClassId fromInt(int id)
  {
    return cValues[id];
  }
  
  /**
   * Get the enum detailed string representation. For {@link ELogLevel} this is the same as calling
   * {@link #toString()} method.
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
