package com.kas.sec;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;

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
  
  static public final AccessLevel NONE_ACCESS  = new AccessLevel((byte)0);
  static public final AccessLevel READ_ACCESS  = new AccessLevel(READ);
  static public final AccessLevel WRITE_ACCESS = new AccessLevel(WRITE);
  static public final AccessLevel ALTER_ACCESS = new AccessLevel(ALTER);
  
  private byte mAccessLevel;
  
  AccessLevel(byte accessLevel)
  {
    mAccessLevel = accessLevel;
  }
  
  public boolean isLevelEnabled(byte level)
  {
    return (mAccessLevel & level) != 0;
  }
  
  public String toString()
  {
    return StringUtils.asHexString(new byte [] {mAccessLevel});
  }
  
  public String toPrintableString(int level)
  {
    StringBuilder sb = new StringBuilder().append("AccessLevels=");
    if (isLevelEnabled(READ)) sb.append("READ,");
    if (isLevelEnabled(WRITE)) sb.append("WRITE,");
    if (isLevelEnabled(ALTER)) sb.append("ALTER,");
    
    String result = sb.toString();
    if (result.endsWith(",")) result = result.substring(0,result.length()-1);
    return result;
  }
}
