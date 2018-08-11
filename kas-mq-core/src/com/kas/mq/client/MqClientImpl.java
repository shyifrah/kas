package com.kas.mq.client;

import com.kas.infra.base.AKasObject;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqMessage;

public class MqClientImpl extends AKasObject implements IClient
{
  public void connect(String host, int port)
  {
  }

  public MqQueue open(String queue)
  {
    return null;
  }

  public void close()
  {
  }

  public void disconnect()
  {
  }

  public MqMessage createMessage()
  {
    return null;
  }

  public MqMessage get()
  {
    return null;
  }

  public MqMessage getAndWait()
  {
    return null;
  }

  public MqMessage getAndWaitWithTimeout(long timeout)
  {
    return null;
  }

  public void put(MqMessage message)
  {
  }

  public String toPrintableString(int level)
  {
    return null;
  }
}
