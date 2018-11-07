package com.kas.comm.serializer;

import com.kas.infra.base.IObject;

/**
 * Detailing all serializable classes IDs
 * 
 * @author Pippo
 */
public enum EClassId implements IObject
{
  /**
   * place holders
   */
  cPlaceHolder00,
  cPlaceHolder01,
  cPlaceHolder02,
  cPlaceHolder03,
  cPlaceHolder04,
  cPlaceHolder05,
  cPlaceHolder06,
  cPlaceHolder07,
  cPlaceHolder08,
  cPlaceHolder09,
  
  /**
   * KAS/MQ message types
   */
  cClassMqMessage,
  cClassMqStringMessage,
  cClassMqObjectMessage,
  cClassMqBytesMessage,
  cClassMqMapMessage,
  cClassMqStreamMessage,
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
   * Get the enum detailed string representation
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