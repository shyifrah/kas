package com.kas.mq.impl;


public enum EQueryType
{
  /**
   * Unknown value
   */
  UNKNOWN,
  
  /**
   * Query configuration
   */
  QUERY_CONFIG_ALL,
  QUERY_CONFIG_LOGGING,
  QUERY_CONFIG_MQ,
  QUERY_CONFIG_SERIALIZER,
  
  /**
   * Query session
   */
  QUERY_SESSION,
  
  /**
   * Query connection
   */
  QUERY_CONNECTION,
  
  /**
   * Query queues
   */
  QUERY_QUEUE,
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
}
