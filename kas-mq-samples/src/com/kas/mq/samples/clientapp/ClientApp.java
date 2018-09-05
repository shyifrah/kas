package com.kas.mq.samples.clientapp;

import java.util.Map;
import com.kas.infra.base.KasException;
import com.kas.infra.base.TimeStamp;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.impl.MqContext;
import com.kas.mq.samples.Utils;

/**
 * This is a sample {@link AKasMqAppl KAS/MQ application} that is intended
 * to demonstrate the use of the KAS/MQ API.<br>
 * <br>
 * The program writes {@code cNumOfMessages} messages to the queue named
 * {@code cQueueName} and then reads them back.<br>
 * At the end of the execution, the program will print how long its execution took. 
 * 
 * @author Pippo
 *
 */
public class ClientApp extends AKasMqAppl 
{
  private Thread [] mProducers;
  private Thread [] mConsumers;
  private ClientAppParams mParams;
  
  /**
   * Construct the application
   * 
   * @param args
   */
  public ClientApp(Map<String, String> args)
  {
    super(args);
    mParams = new ClientAppParams(args);
  }
  
  /**
   * Ignore this
   */
  public String toPrintableString(int level)
  {
    return null;
  }
  
  /**
   * Initialization
   */
  public boolean init()
  {
    super.init();
    
    mProducers = new Thread [mParams.mTotalProducers];
    mConsumers = new Thread [mParams.mTotalConsumers];
    
    return true;
  }
  
  /**
   * Termination
   */
  public boolean term()
  {
    super.term();
    return true;
  }
  
  /**
   * Main logic
   */
  public boolean run()
  {
    TimeStamp tsStart = TimeStamp.now();
    MqContext client = new MqContext();
    try
    {
      client.connect(mParams.mHost, mParams.mPort, mParams.mUserName, mParams.mPassword);
      
      //===========================================================================================
      // defining queues which are used by producers and consumers
      //===========================================================================================
      if (mParams.mCreateResources)
      {
        Utils.createQueue(client, mParams.mProdQueueName, mParams.mTotalMessages);
        if (!mParams.mProdQueueName.equals(mParams.mConsQueueName))
        {
          Utils.createQueue(client, mParams.mConsQueueName, mParams.mTotalMessages);
        }
      }
      
      //===========================================================================================
      // creating consumers and producers
      //===========================================================================================
      for (int i = 0; i < mParams.mTotalConsumers; ++i)
      {
        System.out.println("Creating consumer thread number " + (i+1));
        mConsumers[i] = new ConsumerThread(i, mParams);
      }
      
      for (int i = 0; i < mParams.mTotalProducers; ++i)
      {
        System.out.println("Creating producer thread number " + (i+1));
        mProducers[i] = new ProducerThread(i, mParams);
      }
      
      //===========================================================================================
      // starting consumers and producers
      //===========================================================================================
      System.out.println("Consumers and Producers created. starting...");
      for (int i = 0; i < mParams.mTotalConsumers; ++i)
        mConsumers[i].start();
      
      for (int i = 0; i < mParams.mTotalProducers; ++i)
        mProducers[i].start();
      
      //===========================================================================================
      // awaiting consumers and producers termination
      //===========================================================================================
      System.out.println("Awaiting threads termination...");
      for (int i = 0; i < mParams.mTotalProducers; ++i)
      {
        try
        {
          mProducers[i].join();
        }
        catch (InterruptedException e) {}
      }
      
      for (int i = 0; i < mParams.mTotalConsumers; ++i)
      {
        try
        {
          mConsumers[i].join();
        }
        catch (InterruptedException e) {}
      }
      
      //===========================================================================================
      // deleting queues which were used by producers and consumers
      //===========================================================================================
      Utils.deleteQueue(client, mParams.mProdQueueName);
      Utils.deleteQueue(client, mParams.mConsQueueName);
    }
    catch (KasException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (client != null)
      {
        if (client.isConnected())
        {
          try
          { client.disconnect(); }
          catch (KasException e) {}
        }
      }
    }
    
    /**
     * Print execution stats
     */
    TimeStamp tsEnd = TimeStamp.now();
    System.out.println("Total traffic (msgs)......: " + mParams.mTotalMessages);
    System.out.println("Producer threads..........: " + mParams.mTotalProducers);
    System.out.println("Consumer threads..........: " + mParams.mTotalConsumers);
    System.out.println("   Consume interval....: " + ConsumerThread.cConsumerPollingInterval);
    System.out.println("   Consume timeout.....: " + ConsumerThread.cConsumerGetTimeout);
    System.out.println("Started at................: " + tsStart.toString());
    System.out.println("Ended at..................: " + tsEnd.toString());
    
    String runTime = Utils.reportTime(tsStart, tsEnd);
    System.out.println("Total run time.....: " + runTime);
    
    return end();
  }
}