package com.kas.mq.jdk;

import com.kas.mq.client.IClient;
import com.kas.mq.client.MqClientImpl;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqMessageFactory;

public final class KasMqClient implements IClient
{
  private MqClientImpl mImpl = new MqClientImpl();
  
  public void connect(String host, int port, String user, String pwd)
  {
    mImpl.connect(host, port, user, pwd);
  }

  public void disconnect()
  {
    mImpl.disconnect();
  }

  public boolean open(String queue)
  {
    return mImpl.open(queue);
  }

  public void close()
  {
    mImpl.close();
  }

  public boolean define(String queue)
  {
    return mImpl.define(queue);
  }

  public boolean delete(String queue)
  {
    return mImpl.delete(queue);
  }

  public void show()
  {
    mImpl.show();
  }

  public MqMessage get(int priority, long timeout, long interval)
  {
    return mImpl.get(priority, timeout, interval);
  }

  public void put(MqMessage message)
  {
    mImpl.put(message);
  }
  
  public KasMqMessage createMqTextMessage(String text)
  {
    return new KasMqMessage(MqMessageFactory.createTextMessage(text));
  }

  public String getResponse()
  {
    return mImpl.getResponse();
  }

  public void setResponse(String response)
  {
    mImpl.setResponse(response);
  }
}
