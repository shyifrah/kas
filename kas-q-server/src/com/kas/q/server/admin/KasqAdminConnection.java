package com.kas.q.server.admin;

import javax.jms.JMSException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqConnection;
import com.kas.q.KasqTextMessage;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.HaltRequest;
import com.kas.q.requests.QueryRequest;

public class KasqAdminConnection extends KasqConnection
{
  private static ILogger sLogger = LoggerFactory.getLogger(KasqAdminConnection.class);
  
  KasqAdminConnection(String host, int port) throws JMSException
  {
    super(host, port);
    mHost = host;
    mPort = port;
  }
  
  public void shutdown()
  {
    try
    {
      HaltRequest request = new HaltRequest();
      internalSend(request);
    }
    catch (JMSException e)
    {
      sLogger.info("Failed to shutdown KAS/Q server at " + mHost + ':' + mPort + ". Exception caught: ", e);
    }
  }
  
  public String query(QueryRequest request)
  {
    String output = null;
    try
    {
      IKasqMessage response = internalSendAndReceive(request);
      output = ((KasqTextMessage)response).getText();
    }
    catch (JMSException e)
    {
      sLogger.info("Failed to query KAS/Q server at " + mHost + ':' + mPort + ". Exception caught: ", e);
    }
    return output;
  }
}
