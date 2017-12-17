package com.kas.q.server.admin;

import javax.jms.JMSException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqConnection;
import com.kas.q.KasqTextMessage;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.HaltRequest;
import com.kas.q.requests.QueryRequest;

final public class KasqAdminConnection extends KasqConnection
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
    sLogger.debug("KasqAdminConnection::shutdown() - IN");
    
    try
    {
      HaltRequest request = new HaltRequest();
      sLogger.debug("KasqAdminConnection::shutdown() - Sending shutdown request via message: " + request.toPrintableString(0));
      
      internalSend(request);
    }
    catch (JMSException e)
    {
      sLogger.info("Failed to shutdown KAS/Q server at " + mHost + ':' + mPort + ". Exception caught: ", e);
    }
    
    sLogger.debug("KasqAdminConnection::shutdown() - OUT");
  }
  
  public String query(QueryRequest request)
  {
    sLogger.debug("KasqAdminConnection::query() - IN");
    
    String output = "";
    try
    {
      sLogger.debug("KasqAdminConnection::query() - Sending shutdown query via message: " + request.toPrintableString(0));
      IKasqMessage response = internalSendAndReceive(request);
      sLogger.debug("KasqAdminConnection::query() - Got response: " + response.toPrintableString(0));
      
      output = ((KasqTextMessage)response).getText();
    }
    catch (JMSException e)
    {
      sLogger.info("Failed to query KAS/Q server at " + mHost + ':' + mPort + ". Exception caught: ", e);
    }
    
    sLogger.debug("KasqAdminConnection::query() - OUT, Output length=" + output.length());
    return output;
  }
}
