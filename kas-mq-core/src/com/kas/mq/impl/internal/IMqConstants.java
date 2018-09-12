package com.kas.mq.impl.internal;

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
  static public final long cDefaultPollingInterval = 150000L;
  
  /**
   * Expiration
   */
  static public final long cDefaultExpiration = 604800000L; // 1 week
  
  /**
   * Get timeout
   */
  static public final long cDefaultTimeout = 0L;
  
  /**
   * Property names
   */
  static public final String cKasPropertyPrefix = "kas.";
  
  static public final String cKasPropertyResponseCode = cKasPropertyPrefix + "response.code";
  static public final String cKasPropertyResponseDesc = cKasPropertyPrefix + "response.desc";
  
  static public final String cKasPropertyUserName       = cKasPropertyPrefix + "username";
  static public final String cKasPropertyPassword       = cKasPropertyPrefix + "password";
  static public final String cKasPropertyNetworkAddress = cKasPropertyPrefix + "network.address";
  static public final String cKasPropertySessionId      = cKasPropertyPrefix + "session";
  
  static public final String cKasPropertyDefqQueueName = cKasPropertyPrefix + "defq.queue";
  static public final String cKasPropertyDefqThreshold = cKasPropertyPrefix + "defq.threshold";
  
  static public final String cKasPropertyDelqQueueName = cKasPropertyPrefix + "delq.queue";
  static public final String cKasPropertyDelqForce     = cKasPropertyPrefix + "delq.force";
  
  static public final String cKasPropertyQryqQueueName    = cKasPropertyPrefix + "qryq.queue";
  static public final String cKasPropertyQryqPrefix       = cKasPropertyPrefix + "qryq.prefix";
  static public final String cKasPropertyQryqAllData      = cKasPropertyPrefix + "qryq.alldata";
  static public final String cKasPropertyQryqResultPrefix = cKasPropertyPrefix + "qryq.result";
  
  static public final String cKasPropertyGetTimeout   = cKasPropertyPrefix + "get.timeout";
  static public final String cKasPropertyGetInterval  = cKasPropertyPrefix + "get.interval";
  static public final String cKasPropertyGetUserName  = cKasPropertyPrefix + "get.username";
  static public final String cKasPropertyGetTimeStamp = cKasPropertyPrefix + "get.timestamp";
  static public final String cKasPropertyGetQueueName = cKasPropertyPrefix + "get.queue";
  
  static public final String cKasPropertyPutUserName  = cKasPropertyPrefix + "put.username";
  static public final String cKasPropertyPutQueueName = cKasPropertyPrefix + "put.queue";
  static public final String cKasPropertyPutTimeStamp = cKasPropertyPrefix + "put.timestamp";
  
  static public final String cKasPropertySyncListPrefix = cKasPropertyPrefix + "sync.list";
}
