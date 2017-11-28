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
import com.kas.q.samples.internal.ReceiverThread;
import com.kas.q.samples.internal.SenderThread;

public class MessageProducerAndConsumerDriver
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
      MessageProducerAndConsumerDriver driver = new MessageProducerAndConsumerDriver();
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
      
      try
      {
        ConnectionFactory factory = client.getFactory();
        Connection conn = factory.createConnection(userName, password);
        Session sess = conn.createSession();
        Queue queue = client.locateQueue(cQueueName);
        if (queue == null) queue = sess.createQueue(cQueueName);
        
        Properties senderParams = new Properties();
        senderParams.setStringProperty(AThread.cProperty_ThreadName, "SenderThread");
        senderParams.setIntProperty(AThread.cProperty_NumOfIterations, 5);
        senderParams.setIntProperty(AThread.cProperty_PreAndPostDelay, 10);
        senderParams.setObjectProperty(AThread.cProperty_KasqSession, sess);
        senderParams.setObjectProperty(AThread.cProperty_KasqQueue, queue);
        Thread sender = new SenderThread(senderParams);
        sender.start();
        
        Properties receiverParams = new Properties(senderParams);
        receiverParams.setStringProperty(AThread.cProperty_ThreadName, "ReceiverThread");
        senderParams.setIntProperty(AThread.cProperty_PreAndPostDelay, 5);
        receiverParams.setStringProperty(ReceiverThread.cProperty_ReceiveMode, ReceiverThread.cProperty_ReceiveMode_InfiniteWait);
        Thread receiver = new ReceiverThread(receiverParams);
        receiver.start();
        
        RunTimeUtils.sleep(60);
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
}
