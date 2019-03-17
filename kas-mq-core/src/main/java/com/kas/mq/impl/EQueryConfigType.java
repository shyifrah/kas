package com.kas.mq.impl;

/**
 * Configuration types
 * 
 * @author Pippo
 */
public enum EQueryConfigType
{
  /**
   * All configuration data
   */
  ALL,
  
  /**
   * Logging configuration data
   */
  LOGGING,
  
  /**
   * MQ configuration data
   */
  MQ,
  
  /**
   * DB configuration data
   */
  DB,
  ;
  
  static final private EQueryConfigType [] cValues = EQueryConfigType.values();
  
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id The ordinal of the enum
   * @return the enum value
   */
  static public EQueryConfigType fromInt(int id)
  {
    return cValues[id];
  }
  
  /**
   * Return a more friendly string
   * 
   * @return
   *   the name of the constant in addition to the ordinal value
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder()
      .append(name())
      .append(" (")
      .append(ordinal())
      .append(')');
    return sb.toString();
  }
}
