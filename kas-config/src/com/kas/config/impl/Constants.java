package com.kas.config.impl;

public interface Constants
{
  static final String cIncludeKey         = "kas.include";
  static final String cMainConfigFileName = "kas.properties";
  static final String cConfigPropPrefix   = "kas.config.";
  
  static final long cDefaultMonitoringDelay    = 10000L;
  static final long cDefaultMonitoringInterval = 10000L;
  
  static final String cLoggingConfigPrefix    = "kas.logging.";
  static final String cContainersConfigPrefix = "kas.container.";
  static final String cMessagingConfigPrefix  = "kas.q.";
  
  static final int cDefaultContainerNotFoundSize = -1;
  
  static final boolean  cDefaultEnabled        = true;
  static final String   cDefaultAppender       = "console";
  static final int      cDefaultListenPort     = 14560;
  static final String   cDefaultManagerName    = "qmgr";
  static final String   cDefaultDeadQueueName  = "local.dead";
  static final String   cDefaultAdminQueueName = "local.admin";
  
  static final String   cMessagingDispatchConfigPrefix = cMessagingConfigPrefix + "queueDispatch.";
  
  static final long     cDefaultDispatchDelay    = 10000L;
  static final long     cDefaultDispatchInterval = 10000L;
  
  static final String   cMessagingLocatorConfigPrefix = cMessagingConfigPrefix + "locator.";
  
  static final int      cDefaultLocatorListenPort = 14580;
}
