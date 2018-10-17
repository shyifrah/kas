package com.kas.sec;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;

/**
 * {@link AccessLevel} describes a level of access that is granted to a user
 * upon a specific resource.<br>
 * <br>
 * The access level is actually a bit of a byte. For example:<br>
 *   READ access:     00000001<br>
 *   WRITE access:    00000010<br>
 * If we wish to grant a user a READ and WRITE access we simply turn on both bits:<br>
 *   READ-WRITE:      00000011<br>
 * Of course, not all resource classes has a meaning for write access vs. read access.
 * In these resource classes, we would probably like to enable only the read access.
 * 
 * @author Pippo
 */
public class AccessLevel extends AKasObject
{
  /**
   * Access bits
   */
  static public final byte READ  = 1;   // 00000001
  static public final byte WRITE = 2;   // 00000010
  static public final byte ALTER = 4;   // 00000100
  static public final byte PH_01 = 8;   // 00001000
  static public final byte PH_02 = 16;  // 00010000
  static public final byte PH_03 = 32;  // 00100000
  static public final byte PH_04 = 64;  // 01000000
  static public final byte PH_05 = -1;  // 10000000
  
  /**
   * Access levels
   */
  static public final AccessLevel NONE_ACCESS  = new AccessLevel((byte)0);
  static public final AccessLevel READ_ACCESS  = new AccessLevel(READ);
  static public final AccessLevel WRITE_ACCESS = new AccessLevel(WRITE);
  static public final AccessLevel ALTER_ACCESS = new AccessLevel(ALTER);
  
  /**
   * The access level
   */
  private byte mAccessLevel;
  
  /**
   * Construct an access level with the specified byte.
   * 
   * @param accessLevel The bit-string that should be turned on 
   */
  AccessLevel(byte accessLevel)
  {
    mAccessLevel = accessLevel;
  }
  
  /**
   * Get an indication if access levels are enabled or not 
   *  
   * @param levels A bit-string of access levels
   * @return {@code true} if all bits of {@code levels} are turned on, {@code false} otherwise
   */
  public boolean isLevelEnabled(byte levels)
  {
    return (mAccessLevel & levels) != 0;
  }
  
  /**
   * Get the bit-string as a hexa-decimal character.
   * 
   * @return the bit-string as a hexa-decimal character.
   */
  public String toString()
  {
    return StringUtils.asHexString(new byte [] {mAccessLevel});
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
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