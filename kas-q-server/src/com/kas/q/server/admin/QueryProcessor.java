package com.kas.q.server.admin;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.KasqClient;
import com.kas.q.requests.QueryRequest;

public class QueryProcessor
{
  private KasqClient mClient;
  private String []  mArgs;
  
  public QueryProcessor(KasqClient client, String [] args)
  {
    mClient = client;
    mArgs = args;
  }
  
  public void run() throws IllegalArgumentException, JMSException
  {
    // first argument was already verified by KasqAdmin class - it is QUERY 
    QueryRequest queryRequest = null;
    EDestinationType destType;
    String destName;
    
    if (mArgs.length == 1)
    {
      throw new IllegalArgumentException("Destination type is missing");
    }
    else
    if (mArgs.length == 2)
    {
      String qArg1 = mArgs[1].toLowerCase();
      destType = EDestinationType.valueOf(EDestinationType.class, qArg1);
      
      if (destType != EDestinationType.cAll)
      {
        throw new IllegalArgumentException("Missing destination name or ALL for QUERY QUEUE/TOPIC command");
      }
      
      queryRequest = new QueryRequest(destType);
    }
    else
    if (mArgs.length == 3)
    {
      String qArg1 = mArgs[1].toLowerCase();
      String qArg2 = mArgs[2].toLowerCase();
      destType = EDestinationType.valueOf(EDestinationType.class, qArg1.toLowerCase());
      
      if (destType == EDestinationType.cAll)
      {
        throw new IllegalArgumentException("Excessive tokens following ALL: " + qArg2);
      }
      
      if (qArg2.equals("all"))
      {
        queryRequest = new QueryRequest(destType);
      }
      else
      if ((qArg2.startsWith("'")) && (qArg2.endsWith("'")) && (qArg2.length() > 2))
      {
        destName = qArg2.substring(1, qArg2.length());
        queryRequest = new QueryRequest(destType, destName);
      }
      else
      {
        throw new IllegalArgumentException("Invalid destination name: " + qArg2 + ". Must be ALL or name enclosed with apostrpohes");
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid QUERY request. Too many tokens");
    }
    
    String userName = "admin";
    String password = "admin";
    
    QueueConnectionFactory factory = mClient.getQueueConnectionFactory();
    QueueConnection   conn    = factory.createQueueConnection(userName, password);
    QueueSession      sess    = conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    Queue             queue   = mClient.getAdminQueue();
    QueueRequestor    req     = new QueueRequestor(sess, queue);
    TextMessage response = (TextMessage)req.request(queryRequest);
    String text = response.getText();
    writeln(text);
  }
  
  private static void writeln(String message)
  {
    System.out.println(message);
  }
}
