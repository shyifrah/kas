package com.kas.mq.client;

import java.io.IOException;
import java.net.Socket;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqMessage;

public class MqClientImpl extends AKasObject implements IClient
{
  private Socket mSocket;
  private ILogger mLogger;
  
  public MqClientImpl()
  {
    mSocket = new Socket();
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  public void connect(String host, int port)
  {
    if (isConnected())
    {
      disconnect();
    }
    else
    {
      try
      {
        mSocket = new Socket(host, port);
        mLogger.info("Connection established with host at [" + host + ':' + port + "]");
      }
      catch (IOException e)
      {       
        mLogger.error("Exception occurred while trying to connect to [" + host + ':' + port + "]. Exception: ", e);
      }
    }
  }

  public void disconnect()
  {
    try
    {
      mSocket.close();
    }
    catch (IOException e)
    {
      mLogger.error("Exception occurred while trying to close socket [" + mSocket.toString() + "]", e);
    }
    mSocket = new Socket();
  }

  public boolean isConnected()
  {
    return mSocket.isConnected() && !mSocket.isClosed();
  }
  
  public MqQueue open(String queue)
  {
    return null;
  }

  public void close()
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
