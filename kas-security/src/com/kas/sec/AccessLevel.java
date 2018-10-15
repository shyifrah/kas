package com.kas.sec;

import com.kas.infra.base.AKasObject;

public class AccessLevel extends AKasObject
{
  static public final byte READ  = 1;   // 00000001
  static public final byte WRITE = 2;   // 00000010
  static public final byte ALTER = 4;   // 00000100
  static public final byte PH_01 = 8;   // 00001000
  static public final byte PH_02 = 16;  // 00010000
  static public final byte PH_03 = 32;  // 00100000
  static public final byte PH_04 = 64;  // 01000000
  static public final byte PH_05 = -1;  // 10000000
  
  private byte mAccessLevel;
  
  AccessLevel(byte accessLevel)
  {
    mAccessLevel = accessLevel;
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
}
