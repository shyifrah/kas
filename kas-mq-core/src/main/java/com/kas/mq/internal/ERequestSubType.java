package com.kas.mq.internal;

import com.kas.infra.base.IObject;

/**
 * This enum is used to categorize administrative messages
 * 
 * @author Pippo
 */
public enum ERequestSubType implements IObject
{
  /**
   * Unknown sub-request type
   */
  cUnknown,
  
  /**
   * Group operations
   */
  cGroup,
  
  /**
   * User operations
   */
  cUser,
  
  /**
   * Queue operations
   */
  cQueue,
  
  /**
   * Terminate operations
   */
  cConnection,
  cSession,
  cServer,
  
  /**
   * Notifications
   */
  cSysState,
  cRepoUpdate,
  ;
  
  static final private ERequestSubType [] cValues = ERequestSubType.values();
  
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id
   *   The ordinal of the enum
   * @return
   *   the enum value
   */
  static public ERequestSubType fromInt(int id)
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
      .append(name().substring(1))
      .append(" (")
      .append(ordinal())
      .append(')');
    return sb.toString();
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
