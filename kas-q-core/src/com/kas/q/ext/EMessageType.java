package com.kas.q.ext;

public enum EMessageType
{
  cKasqMessage,
  cKasqMessageText,
  cKasqMessageObject,
  cKasqMessageBytes,
  cKasqMessageStream,
  cKasqMessageMap;
  
  private static final EMessageType [] cValues = EMessageType.values();
  
  public  static final EMessageType cMinValue = cValues[0];
  public  static final EMessageType cMaxValue = cValues[cValues.length-1];
  
  public static boolean isValid(int ord)
  {
    return ((ord >= 0) && (ord <= cValues.length-1));
  }
  
  public static EMessageType fromInt(int ord)
  {
    if (isValid(ord))
      return cValues[ord];
    
    throw new IllegalArgumentException(ord + " not a valid EMessageType ordinal");
  }
}
