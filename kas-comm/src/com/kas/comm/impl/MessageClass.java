package com.kas.comm.impl;

public enum MessageClass
{
  // unknown classId
  cUnknownMessage,
  
  // classIds that are created by KasqMessageFactory
  cKasqMessage,
  cKasqTextMessage,
  cKasqObjectMessage,
  cKasqBytesMessage,
  cKasqStreamMessage,
  cKasqMapMessage,
  ;
  
  private static MessageClass [] cMessageClassValues = MessageClass.values();
  
  public static MessageClass fromInt(int idx)
  {
    return cMessageClassValues[idx];
  }
}
