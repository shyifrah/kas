package com.kas.q.server.admin;

import javax.jms.JMSException;
import com.kas.q.KasqConnection;

public class KasqAdminConnection extends KasqConnection
{
  KasqAdminConnection(String host, int port) throws JMSException
  {
    super(host, port);
  }
  
  public void shutdown()
  {
    //HaltRequest request = new HaltRequest();
    
  }
}
