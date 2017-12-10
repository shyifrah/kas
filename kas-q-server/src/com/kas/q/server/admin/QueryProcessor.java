package com.kas.q.server.admin;

import javax.jms.JMSException;
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
      
      if (destType == EDestinationType.cAll)
      {
        queryRequest = new QueryRequest(destType);
      }
      else
      {
        throw new IllegalArgumentException("Missing destination name or ALL for QUERY QUEUE/TOPIC command"); 
      }
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
      else
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
        throw new IllegalArgumentException("Invalid destination name: " + qArg2 + ". Must be enclosed with apostrpohes");
      }
    }
    else
    if (mArgs.length == 4)
    {
      String qArg1 = mArgs[1].toLowerCase();
      String qArg2 = mArgs[2].toLowerCase();
      String qArg3 = mArgs[3].toLowerCase();
      destType = EDestinationType.valueOf(EDestinationType.class, qArg1.toLowerCase());
      destName = qArg2.substring(1, qArg2.length());
      
      if (destType == EDestinationType.cAll)
      {
        throw new IllegalArgumentException("Excessive tokens following ALL: " + qArg2 + ", " + qArg3);
      }
      
      if (qArg2.equals("all"))
      {
        throw new IllegalArgumentException("Excessive tokens following ALL: " + qArg3);
      }
      
      if ((!qArg2.startsWith("'")) || (!qArg2.startsWith("'")) || (qArg2.length() <= 2))
      {
        throw new IllegalArgumentException("Invalid destination name: " + qArg2 + ". Must be enclosed with apostrpohes");
      }
      
      if ((qArg3.length() != 2) || !qArg3.startsWith("p"))
      {
        throw new IllegalArgumentException("Invalid priority: " + qArg3 + ". Must be one of P0-P9");
      }

      destName = qArg2.substring(1, qArg2.length());
      int priority = -1;
      try
      {
        priority = Integer.valueOf(qArg3.substring(1));
      }
      catch (NumberFormatException e) {}
      
      if (priority == -1)
      {
        throw new IllegalArgumentException("Invalid priority: " + qArg3 + ". Must be one of P0-P9");
      }
      else
      {
        queryRequest = new QueryRequest(destType, destName, priority);
      }
    }
    else
    {
      throw new IllegalArgumentException("Unknown command");
    }
    
    //
    //String userName = "admin";
    //String password = "admin";
    //
    //try
    //{
    //  ConnectionFactory factory = mClient.getConnectionFactory();
    //  Connection        conn    = factory.createConnection(userName, password);
    //  Session           sess    = conn.createSession();
    //  MessageProducer   prod    = sess.createProducer(null);
    //  
    //  HaltRequest haltRequest = new HaltRequest();
    //  
    //  prod.send(haltRequest);
    //}
    //catch (JMSException e) {}
  }
  
  private void writeln(String message)
  {
    System.out.println(message);
  }
}
