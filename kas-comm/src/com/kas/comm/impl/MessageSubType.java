package com.kas.comm.impl;

public enum MessageSubType
{
  // unknown sub-type
  cUnknownMessage,
  
  // sub-types that are created by KasqMessageFactory
  cKasqMessage,
  cKasqTextMessage,
  cKasqObjectMessage,
  cKasqBytesMessage,
  cKasqStreamMessage,
  cKasqMapMessage,
  ;
 
  /***************************************************************************************************************
   *  
   */
  private static MessageSubType [] cMessageSubTypeValues = MessageSubType.values();
  
  /***************************************************************************************************************
   * Get a {@code MessageSubType} by its ordinal value
   * 
   * @param ord the ordinal value of the {@code MessageSubType}
   * 
   * @return the respectable {@code MessageSubType}
   */
  public static MessageSubType fromInt(int idx)
  {
    return cMessageSubTypeValues[idx];
  }
}
