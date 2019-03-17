package com.kas.mq.internal;

/**
 * Various constants
 * 
 * @author Pippo
 */
public interface IMqConstants
{
  /**
   * Queue definition defaults
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
  
  static public final String cKasPropertyDefQueueName   = cKasPropertyPrefix + "def.q.queue";
  static public final String cKasPropertyDefQueueDesc   = cKasPropertyPrefix + "def.q.desc";
  static public final String cKasPropertyDefThreshold   = cKasPropertyPrefix + "def.q.threshold";
  static public final String cKasPropertyDefDisposition = cKasPropertyPrefix + "def.q.disposition";
  
  static public final String cKasPropertyDelQueueName = cKasPropertyPrefix + "del.q.queue";
  static public final String cKasPropertyDelForce     = cKasPropertyPrefix + "del.q.force";
  
  static public final String cKasPropertyAltQueueName = cKasPropertyPrefix + "alt.q.queue";
  static public final String cKasPropertyAltDisp      = cKasPropertyPrefix + "alt.q.opt.disposition";
  static public final String cKasPropertyAltThreshold = cKasPropertyPrefix + "atl.q.opt.threshold";
  
  static public final String cKasPropertyDefGroupName = cKasPropertyPrefix + "def.grp.group";
  static public final String cKasPropertyDefGroupDesc = cKasPropertyPrefix + "def.grp.desc";
  static public final String cKasPropertyDelGroupName = cKasPropertyPrefix + "del.grp.group";
  static public final String cKasPropertyDefUserName  = cKasPropertyPrefix + "def.usr.user";
  static public final String cKasPropertyDefUserPass  = cKasPropertyPrefix + "def.usr.pass";
  static public final String cKasPropertyDefUserGrps  = cKasPropertyPrefix + "def.usr.grps";
  static public final String cKasPropertyDefUserDesc  = cKasPropertyPrefix + "def.usr.desc";
  static public final String cKasPropertyDelUserName  = cKasPropertyPrefix + "del.usr.user";
  
  
  static public final String cKasPropertyGetTimeout   = cKasPropertyPrefix + "get.timeout";
  static public final String cKasPropertyGetInterval  = cKasPropertyPrefix + "get.interval";
  static public final String cKasPropertyGetTimeStamp = cKasPropertyPrefix + "get.timestamp";
  static public final String cKasPropertyGetUserName  = cKasPropertyPrefix + "get.username";
  static public final String cKasPropertyGetQueueName = cKasPropertyPrefix + "get.queue";
  
  static public final String cKasPropertyPutUserName  = cKasPropertyPrefix + "put.username";
  static public final String cKasPropertyPutQueueName = cKasPropertyPrefix + "put.queue";
  static public final String cKasPropertyPutTimeStamp = cKasPropertyPrefix + "put.timestamp";
  
  static public final String cKasPropertySyssQmgrName  = cKasPropertyPrefix + "sysstate.qmgr";
  static public final String cKasPropertySyssActive    = cKasPropertyPrefix + "sysstate.active";
  static public final String cKasPropertySyssQueueList = cKasPropertyPrefix + "sysstate.qlist";
  
  static public final String cKasPropertyRepoQmgrName  = cKasPropertyPrefix + "repo.qmgr";
  static public final String cKasPropertyRepoQueueName = cKasPropertyPrefix + "repo.queue";
  static public final String cKasPropertyRepoOperation = cKasPropertyPrefix + "repo.added";
  
  static public final String cKasPropertyQueryType         = cKasPropertyPrefix + "qry.type";
  static public final String cKasPropertyQueryConfigType   = cKasPropertyPrefix + "qry.conf.type";
  static public final String cKasPropertyQuerySessId       = cKasPropertyPrefix + "qry.sessid";
  static public final String cKasPropertyQueryConnId       = cKasPropertyPrefix + "qry.connid";
  static public final String cKasPropertyQueryUserName     = cKasPropertyPrefix + "qry.user";
  static public final String cKasPropertyQueryGroupName    = cKasPropertyPrefix + "qry.group";
  static public final String cKasPropertyQueryQueueName    = cKasPropertyPrefix + "qry.queue";
  static public final String cKasPropertyQueryPrefix       = cKasPropertyPrefix + "qry.prefix";
  static public final String cKasPropertyQueryAllData      = cKasPropertyPrefix + "qry.alldata";
  static public final String cKasPropertyQueryFormatOutput = cKasPropertyPrefix + "qry.format";
  static public final String cKasPropertyQueryResultPrefix = cKasPropertyPrefix + "qry.result";
  
  static public final String cKasPropertyTermConnId   = cKasPropertyPrefix + "term.connid";
  static public final String cKasPropertyTermSessId   = cKasPropertyPrefix + "term.sessid";
  static public final String cKasPropertyTermUserName = cKasPropertyPrefix + "term.username";
}
