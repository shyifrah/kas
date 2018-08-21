package com.kas.mq.impl;

public interface IMqConstants
{
  /**
   * Minimum and maximum priorities
   */
  static public final int cMinimumPriority = 0;
  static public final int cMaximumPriority = 9;
  
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
}
