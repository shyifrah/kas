package com.kas.infra.utils;

/**
 * OS-dependent utilities
 * 
 * @author Pippo
 */
public class OsUtils
{
  static public final String cOsNameProperty = "os.name";
  
  /**
   * Enum values defining the various operating systems
   */
  static enum EOsType {
    cUnknown,
    cWindows,
    cUnix,
    cMacOs,
    cSolaris
  }
  
  /**
   * Get the type of the Operating System
   * 
   * @return one of the enum values defined in {@link EOsType}
   */
  static public EOsType getOsType()
  {
    String osn = getOsName().toLowerCase();
    
    if (osn.indexOf("win") >= 0)
      return EOsType.cWindows;
    if (osn.indexOf("mac") >= 0)
      return EOsType.cMacOs;
    if (osn.indexOf("nix") >= 0 || osn.indexOf("nux") >= 0 || osn.indexOf("aix") >= 0)
      return EOsType.cUnix;
    if (osn.indexOf("sunos") >= 0)
      return EOsType.cSolaris;
    return EOsType.cUnknown;
  }
  
  /**
   * Get the Operating System name
   * 
   * @return the Operating System name
   */
  static public String getOsName()
  {
    return System.getProperty(cOsNameProperty);
  }
}
