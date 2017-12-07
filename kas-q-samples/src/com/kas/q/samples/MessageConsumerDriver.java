package com.kas.q.samples;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.q.ext.KasqClient;
import com.kas.q.samples.internal.AThread;
import com.kas.q.samples.internal.ConsumerThread;

public class MessageConsumerDriver
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
      MessageConsumerDriver driver = new MessageConsumerDriver();
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
    
    System.out.println("Driver::run() - client created");
    
    client.init();
    
    try
    {
      ConnectionFactory factory = client.getConnectionFactory();
      Connection conn = factory.createConnection(userName, password);
      Session sess = conn.createSession();
      Queue queue = client.locateQueue(cQueueName);
      if (queue == null) queue = sess.createQueue(cQueueName);
      
      Properties threadParams = new Properties();
      threadParams.setStringProperty(AThread.cProperty_ThreadName, "ReceiverThread");
      threadParams.setIntProperty(AThread.cProperty_NumOfIterations, 100);
      threadParams.setIntProperty(AThread.cProperty_PreAndPostDelay, 0);
      threadParams.setObjectProperty(AThread.cProperty_KasqSession, sess);
      threadParams.setObjectProperty(AThread.cProperty_KasqQueue, queue);
      threadParams.setStringProperty(ConsumerThread.cProperty_ReceiveMode, ConsumerThread.cProperty_ReceiveMode_InfiniteWait);
      threadParams.setLongProperty(ConsumerThread.cProperty_ReceiveTimeout, 1000);
      
      Thread thread = new ConsumerThread(threadParams);
      thread.start();
      conn.start();
      thread.join();
      
      conn.stop();
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

    System.out.println("Driver::run() - OUT");
  }
}
