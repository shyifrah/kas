package com.kas.mq.impl;

public interface IMqConstants
{
  /**
   * Queue threshold
   */
  static public final int cDefaultQueueThreshold = 1000;
  
  /**
   * Message priorities
   */
  static public final int cMinimumPriority = 0;
  static public final int cMaximumPriority = 9;
  static public final int cDefaultPriority = 5;
  
  /**
   * Get polling interval length
   */
  static public final long cDefaultPollingInterval = 1000L;
  
  /**
   * Get timeout
   */
  static public final long cDefaultTimeout = 0L;
  
  /**
   * Property names
   */
  static public final String cKasPropertyPrefix = "kas.";
  
  static public final String cKasPropertyResponseCode   = cKasPropertyPrefix + "response.code";
  static public final String cKasPropertyResponseDesc   = cKasPropertyPrefix + "response.desc";
  
  static public final String cKasPropertyUserName       = cKasPropertyPrefix + "username";
  static public final String cKasPropertyPassword       = cKasPropertyPrefix + "password";
  static public final String cKasPropertyQueueName      = cKasPropertyPrefix + "target.queue";
  static public final String cKasPropertyNetworkAddress = cKasPropertyPrefix + "network.address";
  static public final String cKasPropertySessionId      = cKasPropertyPrefix + "session";
  
  static public final String cKasPropertyGetTimeout     = cKasPropertyPrefix + "get.timeout";
  static public final String cKasPropertyGetInterval    = cKasPropertyPrefix + "get.interval";
  static public final String cKasPropertyGetUserName    = cKasPropertyPrefix + "get.username";
  static public final String cKasPropertyGetTimeStamp   = cKasPropertyPrefix + "get.timestamp";
  static public final String cKasPropertyGetQueueName   = cKasPropertyPrefix + "get.queue";
  
  static public final String cKasPropertyPutUserName    = cKasPropertyPrefix + "put.username";
  static public final String cKasPropertyPutQueueName   = cKasPropertyPrefix + "put.queue";
  static public final String cKasPropertyPutTimeStamp   = cKasPropertyPrefix + "put.timestamp";
}
