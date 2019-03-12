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
   * Define a group
   */
  cDefineGroup,
  
  /**
   * Delete a group
   */
  cDeleteGroup,
  
  /**
   * Alter a group
   */
  cAlterGroup,
  
  /**
   * Define a user
   */
  cDefineUser,
  
  /**
   * Delete a user
   */
  cDeleteUser,
  
  /**
   * Alter a user
   */
  cAlterUser,
  
  /**
   * Define a queue
   */
  cDefineQueue,
  
  /**
   * Delete a queue
   */
  cDeleteQueue,
  
  /**
   * Alter a queue
   */
  cAlterQueue,
  
  /**
   * Query server info
   */
  cQueryServer,
  
  /**
   * Terminate active connection
   */
  cTermConn,
  
  /**
   * Terminate active session
   */
  cTermSess,
  
  /**
   * Shutdown KAS/MQ server
   */
  cTermServer,
  
  /**
   * Notify system state changed
   */
  cSysState,
  
  /**
   * Notify repository changed
   */
  cRepoUpdate,
  ;
  
  static final private ERequestType [] cValues = ERequestType.values();
  
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id The ordinal of the enum
   * @return the enum value
   */
  static public ERequestType fromInt(int id)
  {
    return cValues[id];
  }
  
  /**
   * Return a more friendly string
   * 
   * @return the name of the constant in addition to the ordinal value
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
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
