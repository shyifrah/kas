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
  cUnknown,
  cPlaceHolder00,
  cPlaceHolder01,
  cPlaceHolder02,
  cPlaceHolder03,
  cPlaceHolder04,
  cPlaceHolder05,
  cPlaceHolder06,
  cPlaceHolder07,
  cPlaceHolder08,
  
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
   * @param id
   *   The ordinal of the enum
   * @return
   *   the enum value
   */
  static public EClassId fromInt(int id)
  {
    EClassId classId = cUnknown;
    try
    {
      classId = cValues[id];
    }
    catch (Throwable e) {}
    return classId;
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
