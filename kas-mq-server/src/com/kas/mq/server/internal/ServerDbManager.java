package com.kas.mq.server.internal;

import com.kas.infra.base.AKasObject;
import com.kas.mq.server.MqDbConfiguration;

public class ServerDbManager extends AKasObject
{
  private MqDbConfiguration mConfig;
  
  public ServerDbManager(MqDbConfiguration config)
  {
    mConfig = config;
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
}
