package com.kas.q.server.admin;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import com.kas.q.ext.KasqClient;
import com.kas.q.requests.HaltRequest;

public class QueryProcessor implements Runnable
{
  private static final String cDestTypeQueue = "queue";
  private static final String cDestTypeTopic = "topic";
  private static final String cDestTypeAll   = "all";
  
  private KasqClient mClient;
  private String []  mArgs;
  
  public QueryProcessor(KasqClient client, String [] args)
  {
    mClient = client;
    mArgs = args;
  }
  
  public void run()
  {
    String userName = "admin";
    String password = "admin";
    
    try
    {
      ConnectionFactory factory = mClient.getConnectionFactory();
      Connection        conn    = factory.createConnection(userName, password);
      Session           sess    = conn.createSession();
      MessageProducer   prod    = sess.createProducer(null);
      
      HaltRequest haltRequest = new HaltRequest();
      
      prod.send(haltRequest);
    }
    catch (JMSException e) {}
  }
  
  private void writeln(String message)
  {
    System.out.println(message);
  }
}
