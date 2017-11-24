package com.kas.q.samples;

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
    
    System.out.println("Driver::run() - client created");
    
    client.init();
    
    try
    {
      Properties threadParams = new Properties();
      threadParams.setStringProperty(AThread.cProperty_ThreadName, "SenderThread");
      threadParams.setIntProperty(AThread.cProperty_NumOfMessages, 5);
      threadParams.setIntProperty(AThread.cProperty_PreAndPostDelay, 5);
      threadParams.setObjectProperty(AThread.cProperty_KasqClient, client);
      threadParams.setStringProperty(AThread.cProperty_QueueName, cQueueName);
      threadParams.setStringProperty(AThread.cProperty_UserName, userName);
      threadParams.setStringProperty(AThread.cProperty_Password, password);
      
      Thread thread = new SenderThread(threadParams);
      thread.start();
      thread.join();
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
