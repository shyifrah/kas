package com.kas.mq.internal;

import com.kas.infra.base.IObject;

/**
 * This enum is used to categorize administrative messages
 * 
 * @author Pippo
 */
public enum ERequestType implements IObject
{
  /**
   * Unknown request type. Just put the message in the designated queue
   */
  cUnknown,
  
  /**
   * Login
   */
  cLogin,
  
  /**
   * Get message
   */
  cGet,
  
  /**
   * Define
   */
  cDefineGroup,
  cDefineUser,
  cDefineQueue,
  
  /**
   * Delete
   */
  cDeleteGroup,
  cDeleteUser,
  cDeleteQueue,
  
  /**
   * Alter
   */
  cAlterGroup,
  cAlterUser,
  cAlterQueue,
  
  /**
   * Query
   */
  cQueryGroup,
  cQueryUser,
  cQueryQueue,
  cQueryConnection,
  cQuerySession,
  cQueryConfig,
  
  /**
   * Terminate
   */
  cTerminateServer,
  cTerminateConnection,
  cTerminateSession,
  
  /**
   * Notifications
   */
  cNotifySysState,
  cNotifyRepoUpdate,
  ;
  
  static final private ERequestType [] cValues = ERequestType.values();
  
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id
   *   The ordinal of the enum
   * @return
   *   the enum value
   */
  static public ERequestType fromInt(int id)
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
