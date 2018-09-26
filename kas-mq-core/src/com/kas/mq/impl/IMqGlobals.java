package com.kas.mq.impl;


public interface IMqGlobals
{
  /**
   * Query server command query types
   */
  static enum EQueryType
  {
    /**
     * Unknown value
     */
    cUnknown,
    
    /**
     * Query configuration
     */
    cQueryConfigAll,
    cQueryConfigLogging,
    cQueryConfigMq,
    cQueryConfigSerializer,
    
    /**
     * Query session
     */
    cQuerySession,
    
    /**
     * Query connection
     */
    cQueryConnection,
    ;
    
    static final private EQueryType [] cValues = EQueryType.values();
    
    /**
     * Get the enum value by its ordinal.
     * 
     * @param id The ordinal of the enum
     * @return the enum value
     */
    static public EQueryType fromInt(int id)
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
  };
}
