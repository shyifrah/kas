package com.kas.mq.server.mgr;

import com.kas.infra.base.KasException;
import com.kas.mq.impl.internal.IClient;
import com.kas.mq.impl.internal.MqClientImpl;
import com.kas.mq.impl.internal.MqManager;

public class RemoteManager extends MqManager
{
  private IClient mClient;
  
  public RemoteManager(String name, String host, int port) throws KasException
  {
    super(name, host, port);
    mClient = new MqClientImpl();
    mClient.connect(host, port);
  }
  
  public void sendSyncRequest()
  {
    
  }
}
