package com.kas.q.samples;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import com.kas.infra.base.KasException;
import com.kas.q.ext.KasqClient;

public class ClientSenderDriver
{
  private final static String cQueueName = "shy.local.queue";
  private final static String cHostname  = "localhost";
  
  private final static int    cNumberOfMessages = 5;
  
  private final static int    cPort      = 14560;
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  public static void main(String[] args)
  {
    System.out.println("main() - IN");
    
    try
    {
      ClientSenderDriver driver = new ClientSenderDriver();
      driver.run(args);
    }
    catch (Throwable e)
    {
      System.err.println("main() - Exception caught:");
      e.printStackTrace();
    }
    System.out.println("main() - OUT");
  }
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  private void run(String [] args) throws KasException
  {
    System.out.println("Driver::run() - IN");
    
    String userName = "kas";
    String password = "kas";
    
    KasqClient client = new KasqClient(cHostname, cPort);
    
    if (client != null)
    {
      System.out.println("Driver::run() - client created");
      
      client.init();
      
      ConnectionFactory factory = client.getFactory();
      
      try
      {
        Connection conn = factory.createConnection(userName, password);
        System.out.println("connection created...: " + conn.toString());
        
        Session sess = conn.createSession();
        System.out.println("session created......: " + sess.toString());
        
        Queue queue = sess.createQueue(cQueueName);
        
        System.out.println("Driver::run() - Sending messages");
        sendMessages(sess, queue);
        
        System.out.println("Driver::run() - Waiting 10 seconds before continuing...");
        sleepForSeconds(5);
      }
      catch (JMSException e)
      {
        System.out.println("Driver::run() - Exception caught");
        e.printStackTrace();
      }
      
      client.term();
    }

    System.out.println("Driver::run() - OUT");
  }
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  private void sendMessages(Session session, Queue queue) throws JMSException
  {
    MessageProducer producer = session.createProducer(queue);
    
    for (int i = 0; i < cNumberOfMessages; i++)
    {
      String text = "shyifrah-" + Integer.toString(i);
      TextMessage msg = session.createTextMessage(text);
      producer.send(queue, msg);
    }
  }
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  private void sleepForSeconds(int seconds)
  {
    try
    {
      Thread.sleep((long)(seconds * 1000));
    }
    catch (Throwable e) {}
  }
}
