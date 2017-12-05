package com.kas.q.requests;

public enum ERequestType
{
  cAuthenticate,
  cMetaData,
  cLocate,
  cDefine,
  cHalt,
  cGet,
  cPut, 
  ;
  
  private static final ERequestType [] cValues = ERequestType.values();
  
  public  static final ERequestType cMinValue = cValues[0];
  public  static final ERequestType cMaxValue = cValues[cValues.length-1];
  
  public static boolean isValid(int ord)
  {
    return ((ord >= 0) && (ord <= cValues.length-1));
  }
  
  public static ERequestType fromInt(int ord)
  {
    if (isValid(ord))
      return cValues[ord];
    
    throw new IllegalArgumentException(ord + " not a valid RequestType ordinal");
  }
}
