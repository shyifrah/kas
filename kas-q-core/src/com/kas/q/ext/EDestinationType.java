package com.kas.q.ext;

public enum EDestinationType
{
  cQueue ("queue"),
  cTopic ("topic"),
  cAll ("all"),
  ;
  
  private String mName;
  private EDestinationType(String name)
  {
    mName = name;
  }
  
  public String toString()
  {
    return mName;
  }
  
  private static final EDestinationType [] cValues = EDestinationType.values();
  
  public  static final EDestinationType cMinValue = cValues[0];
  public  static final EDestinationType cMaxValue = cValues[cValues.length-1];
  
  public static boolean isValid(int ord)
  {
    return ((ord >= 0) && (ord <= cValues.length-1));
  }
  
  public static EDestinationType fromInt(int ord)
  {
    if (isValid(ord))
      return cValues[ord];
    
    throw new IllegalArgumentException(ord + " not a valid EDestinationType ordinal");
  }
}
