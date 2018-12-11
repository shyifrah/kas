package com.kas.mq.internal;

public interface IMqConstants
{
  /**
   * Queue definition defaults
   */
  static public final int cDefaultQueueThreshold = 1000;
  static public final boolean cDefaultQueuePermanent = true;
  
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
  static public final String cSystemUserName = "system";
  static public final String cSystemPassWord = "system";
  
  /**
   * Property names
   */
  static public final String cKasPropertyPrefix = "kas.";
  
  static public final String cKasPropertyLoginUserName = cKasPropertyPrefix + "login.username";
  static public final String cKasPropertyLoginPassword = cKasPropertyPrefix + "login.password";
  static public final String cKasPropertyLoginAppName  = cKasPropertyPrefix + "login.appname";
  static public final String cKasPropertyLoginSession  = cKasPropertyPrefix + "login.sessionid";
  
  static public final String cKasPropertyDefQueueName = cKasPropertyPrefix + "def.q.queue";
  static public final String cKasPropertyDefThreshold = cKasPropertyPrefix + "def.q.threshold";
  static public final String cKasPropertyDefPermanent = cKasPropertyPrefix + "def.q.permanent";
  
  static public final String cKasPropertyDelQueueName = cKasPropertyPrefix + "del.q.queue";
  static public final String cKasPropertyDelForce     = cKasPropertyPrefix + "del.q.force";
  
  static public final String cKasPropertyGetTimeout   = cKasPropertyPrefix + "get.timeout";
  static public final String cKasPropertyGetInterval  = cKasPropertyPrefix + "get.interval";
  static public final String cKasPropertyGetTimeStamp = cKasPropertyPrefix + "get.timestamp";
  static public final String cKasPropertyGetUserName  = cKasPropertyPrefix + "get.username";
  static public final String cKasPropertyGetQueueName = cKasPropertyPrefix + "get.queue";
  
  static public final String cKasPropertyPutUserName  = cKasPropertyPrefix + "put.username";
  static public final String cKasPropertyPutQueueName = cKasPropertyPrefix + "put.queue";
  static public final String cKasPropertyPutTimeStamp = cKasPropertyPrefix + "put.timestamp";
  
  static public final String cKasPropertySyssQmgrName      = cKasPropertyPrefix + "sysstate.qmgr";
  static public final String cKasPropertySyssActive        = cKasPropertyPrefix + "sysstate.active";
  static public final String cKasPropertySyssQueueList     = cKasPropertyPrefix + "sysstate.qlist";
  
  static public final String cKasPropertyRepoQmgrName  = cKasPropertyPrefix + "repo.qmgr";
  static public final String cKasPropertyRepoQueueName = cKasPropertyPrefix + "repo.queue";
  static public final String cKasPropertyRepoOperation = cKasPropertyPrefix + "repo.added";
  
  static public final String cKasPropertyQueryType         = cKasPropertyPrefix + "qry.type";
  static public final String cKasPropertyQueryConfigType   = cKasPropertyPrefix + "qry.conf.type";
  
  static public final String cKasPropertyQuerySessId       = cKasPropertyPrefix + "qry.sessid";
  
  static public final String cKasPropertyQueryConnId       = cKasPropertyPrefix + "qry.connid";
  
  static public final String cKasPropertyQueryQueueName    = cKasPropertyPrefix + "qry.queue";
  static public final String cKasPropertyQueryPrefix       = cKasPropertyPrefix + "qry.prefix";
  static public final String cKasPropertyQueryAllData      = cKasPropertyPrefix + "qry.alldata";
  static public final String cKasPropertyQueryFormatOutput = cKasPropertyPrefix + "qry.format";
  static public final String cKasPropertyQueryResultPrefix = cKasPropertyPrefix + "qry.result";
  
  static public final String cKasPropertyTermConnId   = cKasPropertyPrefix + "term.connid";
  static public final String cKasPropertyTermSessId   = cKasPropertyPrefix + "term.sessid";
  static public final String cKasPropertyTermUserName = cKasPropertyPrefix + "term.username";
}
