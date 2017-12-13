package com.kas.q.server.admin.cmd;

import java.util.Queue;
import com.kas.q.server.admin.KasqAdminConnection;

public class QueryCommand implements Runnable
{
  //private KasqAdminConnection mConnection;
  //private Queue<String>       mCommandArgs;
  
  public QueryCommand(KasqAdminConnection conn, Queue<String> args)
  {
    //mConnection  = conn;
    //mCommandArgs = args;
  }
  
  public void run()
  {
    // first argument was already verified by KasqAdmin class - it is QUERY 
    //QueryRequest queryRequest = null;
    //EDestinationType destType;
    //String destName;
    
    /*
    if (mArgs.length == 1)
    {
      throw new IllegalArgumentException("Destination type is missing");
    }
    else
    if (mArgs.length == 2)
    {
      String qArg1 = mArgs[1].toLowerCase();
      destType = EDestinationType.fromString(qArg1);
      
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
      destType = EDestinationType.fromString(qArg1);
      
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
    */
  }
}
