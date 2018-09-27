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
  static public final long cDefaultPollingInterval = 1000L;
  
  /**
   * Expiration
   */
  static public final long cDefaultExpiration = 604800000L; // 1 week
  
  /**
   * Get timeout
   */
  static public final long cDefaultTimeout = 0L;
  
  /**
   * Admin queue name<br>
   * This queue does not actually exist
   */
  static public final String cAdminQueueName = "LOCAL.ADMIN";
  
  /**
   * System user name<br>
   * This user does not actually exist
   */
  static public final String cSystemUserName = "SYSTEM";
  static public final String cSystemPassWord = "K4S/MQ";
  
  /**
   * Property names
   */
  static public final String cKasPropertyPrefix = "kas.";
  
  static public final String cKasPropertyLoginUserName = cKasPropertyPrefix + "login.username";
  static public final String cKasPropertyLoginPassword = cKasPropertyPrefix + "login.password";
  static public final String cKasPropertyLoginSession  = cKasPropertyPrefix + "login.sessionid";
  
  static public final String cKasPropertyDefqQueueName = cKasPropertyPrefix + "defq.queue";
  static public final String cKasPropertyDefqThreshold = cKasPropertyPrefix + "defq.threshold";
  
  static public final String cKasPropertyDelqQueueName = cKasPropertyPrefix + "delq.queue";
  static public final String cKasPropertyDelqForce     = cKasPropertyPrefix + "delq.force";
  
  static public final String cKasPropertyGetTimeout   = cKasPropertyPrefix + "get.timeout";
  static public final String cKasPropertyGetInterval  = cKasPropertyPrefix + "get.interval";
  static public final String cKasPropertyGetTimeStamp = cKasPropertyPrefix + "get.timestamp";
  static public final String cKasPropertyGetUserName  = cKasPropertyPrefix + "get.username";
  static public final String cKasPropertyGetQueueName = cKasPropertyPrefix + "get.queue";
  
  static public final String cKasPropertyPutUserName  = cKasPropertyPrefix + "put.username";
  static public final String cKasPropertyPutQueueName = cKasPropertyPrefix + "put.queue";
  static public final String cKasPropertyPutTimeStamp = cKasPropertyPrefix + "put.timestamp";
  
  static public final String cKasPropertyShutUserName = cKasPropertyPrefix + "shut.username";
  
  static public final String cKasPropertySyssQmgrName      = cKasPropertyPrefix + "sysstate.qmgr";
  static public final String cKasPropertySyssActive        = cKasPropertyPrefix + "sysstate.active";
  static public final String cKasPropertySyssSessionPrefix = cKasPropertyPrefix + "sysstate.sessionid";
  
  static public final String cKasPropertyRepoQmgrName  = cKasPropertyPrefix + "repo.qmgr";
  static public final String cKasPropertyRepoQueueName = cKasPropertyPrefix + "repo.queue";
  static public final String cKasPropertyRepoOperation = cKasPropertyPrefix + "repo.added";
  
  static public final String cKasPropertyQrysQueryType    = cKasPropertyPrefix + "qrys.type";
  static public final String cKasPropertyQrysSessionId    = cKasPropertyPrefix + "qrys.sessionid";
  static public final String cKasPropertyQrysConnId       = cKasPropertyPrefix + "qrys.connid";
  static public final String cKasPropertyQryqQmgrName     = cKasPropertyPrefix + "qryq.qmgr";
  static public final String cKasPropertyQryqQueueName    = cKasPropertyPrefix + "qryq.queue";
  static public final String cKasPropertyQryqPrefix       = cKasPropertyPrefix + "qryq.prefix";
  static public final String cKasPropertyQryqAllData      = cKasPropertyPrefix + "qryq.alldata";
  static public final String cKasPropertyQryqOutOnlyProps = cKasPropertyPrefix + "qryq.output.only.props";
  static public final String cKasPropertyQryqResultPrefix = cKasPropertyPrefix + "qryq.result";
}
