package com.kas.mq.server.processors;

/**
 * Query types
 * 
 * @author Pippo
 */
public enum EQueryType
{
  /**
   * Unknown value
   */
  UNKNOWN,
  
  /**
   * Query configuration
   */
  QUERY_CONFIG,
  
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
  
  /**
   * Query users
   */
  QUERY_USER,
  
  /**
   * Query groups
   */
  QUERY_GROUP,
  ;
}
