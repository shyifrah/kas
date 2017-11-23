package com.kas.q.samples;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.q.ext.KasqClient;
import com.kas.q.samples.internal.AThread;
import com.kas.q.samples.internal.SenderThread;

public class MessageProducerDriver
{
  private final static String cQueueName = "shy.local.queue";
  private final static String cHostname  = "localhost";
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
      MessageProducerDriver driver = new MessageProducerDriver();
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
        
        Properties threadParams = new Properties();
        threadParams.setStringProperty(AThread.cProperty_ThreadName, "SenderThread");
        threadParams.setIntProperty(AThread.cProperty_NumOfMessages, 5);
        threadParams.setIntProperty(AThread.cProperty_PreAndPostDelay, 5);
        threadParams.setObjectProperty(AThread.cProperty_KasqConnection, conn);
        threadParams.setStringProperty(AThread.cProperty_QueueName, cQueueName);
        
        Thread thread = new SenderThread(threadParams);
        thread.start();
        thread.join();
      }
      catch (JMSException e)
      {
        System.out.println("Driver::run() - JMSException caught");
        e.printStackTrace();
      }
      catch (InterruptedException e)
      {
        System.out.println("Driver::run() - InterruptedException caught");
        e.printStackTrace();
      }
      
      client.term();
    }

    System.out.println("Driver::run() - OUT");
  }
}
