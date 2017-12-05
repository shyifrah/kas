package com.kas.q.samples;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.q.ext.KasqClient;
import com.kas.q.samples.internal.AThread;
import com.kas.q.samples.internal.ProducerThread;

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
    System.out.println("Driver::run() - IN, PID=[" + RunTimeUtils.getProcessId() + "]");
    
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
      threadParams.setStringProperty(AThread.cProperty_ThreadName, "SenderThread");
      threadParams.setIntProperty(AThread.cProperty_NumOfIterations, 10);
      threadParams.setIntProperty(AThread.cProperty_PreAndPostDelay, 5);
      threadParams.setObjectProperty(AThread.cProperty_KasqSession, sess);
      threadParams.setObjectProperty(AThread.cProperty_KasqQueue, queue);
      threadParams.setIntProperty(ProducerThread.cProperty_SendDelay, 1);
      
      Thread thread = new ProducerThread(threadParams);
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

    System.out.println("Driver::run() - OUT");
  }
}
