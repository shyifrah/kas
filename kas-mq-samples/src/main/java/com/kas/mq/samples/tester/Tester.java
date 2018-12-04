package com.kas.mq.samples.tester;

import com.kas.infra.base.KasException;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqMessageFactory;

/**
 * 
 * @author Pippo
 */
public class Tester
{
  static public void main(String [] args)
  {
    Tester tester = new Tester();
    tester.run();
  }
  
  private Tester()
  {
  }
  
  private void run()
  {
    boolean success = true;
    String name = this.getClass().getName();
    MqContext client = new MqContext(name);

    //====================================================================================================================
    //
    //====================================================================================================================
    System.out.println("============================================================");
    System.out.println("Connect to KAS/MQ server");
    System.out.println("============================================================");
    try
    {
      client.connect("localhost", 14560, "root", "root");
    }
    catch (KasException e)
    {
      e.printStackTrace();
      success = false;
    }
    
    //====================================================================================================================
    //
    //====================================================================================================================
    if (success)
    {
      System.out.println("============================================================");
      System.out.print("Define queue \"" + name + "\": ");
      success = client.defineQueue(name, 1000, true);
      if (success)
      {
        System.out.println("defined!");
      }
      else
      {
        System.out.println("failed.");
        System.out.println(client.getResponse());
      }
      System.out.println("============================================================");
    }
    
    //====================================================================================================================
    //
    //====================================================================================================================
    if (success)
    {
      System.out.println("============================================================");
      System.out.println("Put 100 messages into queue: ");
      for (int i = 0; i < 100; ++i)
      {
        IMqMessage message = MqMessageFactory.createStringMessage("Message number: " + i);
        System.out.println(String.format("%02d >> ID=[%s]", i, message.getMessageId().toString()));
        client.put(name, message);
      }
      System.out.println("============================================================");
    }
    
    //====================================================================================================================
    //
    //====================================================================================================================
    if (success)
    {
      System.out.println("============================================================");
      System.out.println("Get all messages from queue: ");
      IMqMessage message = client.get(name, 10000L, 500L);
      int i = 0;
      while (message != null)
      {
        System.out.println(String.format("%02d << ID=[%s]", i, message.getMessageId().toString()));
        ++i;
        message = client.get(name, 10000L, 500L);
      }
      System.out.println("============================================================");
    }
    
    //====================================================================================================================
    //
    //====================================================================================================================
    if (success)
    {
      System.out.println("============================================================");
      System.out.print("Delete queue \"" + name + "\": ");
      success = client.deleteQueue(name, true);
      if (success)
      {
        System.out.println("deleted!");
      }
      else
      {
        System.out.println("failed.");
        System.out.println(client.getResponse());
      }
      System.out.println("============================================================");
    }
    
    //====================================================================================================================
    //
    //====================================================================================================================
    System.out.println("============================================================");
    System.out.println("Disconnect from KAS/MQ server");
    System.out.println("============================================================");
    try
    {
      client.disconnect();
    }
    catch (KasException e)
    {
      e.printStackTrace();
    }
  }
}
