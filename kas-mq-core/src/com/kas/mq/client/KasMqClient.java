package com.kas.mq.client;

import java.util.Map;
import com.kas.infra.base.KasException;
import com.kas.infra.base.TimeStamp;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqTextMessage;

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
public class KasMqClient extends AKasMqAppl 
{
  static class ProdThread extends Thread
  {
    private MqContext mContext;
    private int mMaxMessages;
    private String mQueueName;
    
    ProdThread(int tix, int maxMsgs, String qname)
    {
      super(ProdThread.class.getSimpleName() + tix);
      mContext = new MqContext();
      mMaxMessages = maxMsgs;
      mQueueName = qname;
    }
    
    public void run()
    {
      try
      {
        mContext.connect(cHost, cPort, cUser, cPass);
      }
      catch (KasException e) {}
      
      for (int i = 0; i < mMaxMessages; ++i)
      {
        String messageBody = "message number: " + (i + 1);
        MqTextMessage putMessage = MqMessageFactory.createTextMessage(messageBody);
        putMessage.setPriority(i%10);
        mContext.put(mQueueName, putMessage);
        
        if (i%100==0)
          System.out.println("...." + i);
      }
      
      try
      {
        mContext.disconnect();
      }
      catch (KasException e) {}
    }
  }
  
  static class ConsThread extends Thread
  {
    private MqContext mContext;
    private String mQueueName;
    
    ConsThread(int tix, String qname)
    {
      super(ConsThread.class.getSimpleName() + tix);
      mContext = new MqContext();
      mQueueName = qname;
    }
    
    public void run()
    {
      try
      {
        mContext.connect(cHost, cPort, cUser, cPass);
      }
      catch (KasException e) {}
      
      long timeout = 60000L;
      long interval = 1000L;
      int total = 0;
      IMqMessage<?> getMessage = mContext.get(mQueueName, timeout, interval);
      while (getMessage != null)
      {
        getMessage = mContext.get(mQueueName, timeout, interval);
        if (getMessage == null)
          System.out.println("Failed to get message...");
        ++total;
        if (total%100==0)
          System.out.println("...." + total);
      }
      
      try
      {
        mContext.disconnect();
      }
      catch (KasException e) {}
    }
  }
  
  /**
   * KAS/MQ address and credentials
   */
  static private final String cHost = "localhost";
  static private final int    cPort = 14560;
  static private final String cUser = "admin";
  static private final String cPass = "admin";
  
  private String mQueueName = "temp.queue";
  private int    mMaxMessages = 10000;
  private int    mNumOfProducers = 1;
  private int    mNumOfConsumers = 1;
  private Thread [] mProducers;
  private Thread [] mConsumers;
  
  /**
   * Construct the application
   * 
   * @param args
   */
  public KasMqClient(Map<String, String> args)
  {
    super(args);
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
    mQueueName = getStrArg("client.app.queue", "temp.queue");
    mNumOfProducers = getIntArg("client.app.producers", 2);
    mNumOfConsumers = getIntArg("client.app.consumers", 10);
    
    mProducers = new Thread [mNumOfProducers];
    mConsumers = new Thread [mNumOfConsumers];
    
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
      client.connect(cHost, cPort, cUser, cPass);
      
      System.out.println("Defining queue with name " + mQueueName + " and a threshold of " + (mMaxMessages * mNumOfProducers) + " messages");
      boolean defined = client.define(mQueueName, mMaxMessages * mNumOfProducers);
      if (!defined)
        throw new KasException("failed to define queue with name " + mQueueName);
      
      for (int i = 0; i < mNumOfConsumers; ++i)
      {
        System.out.println("Creating consumer thread number " + (i+1));
        mConsumers[i] = new ConsThread(i, mQueueName);
        mConsumers[i].start();
      }
      
      for (int i = 0; i < mNumOfProducers; ++i)
      {
        System.out.println("Creating producer thread number " + (i+1));
        mProducers[i] = new ProdThread(i, mMaxMessages, mQueueName);
        mProducers[i].start();
      }
      
      System.out.println("Awaiting threads termination...");
      for (int i = 0; i < mNumOfConsumers; ++i)
      {
        try
        {
          mConsumers[i].join();
        }
        catch (InterruptedException e) {}
      }
      
      for (int i = 0; i < mNumOfProducers; ++i)
      {
        try
        {
          mProducers[i].join();
        }
        catch (InterruptedException e) {}
      }
      
      System.out.println("Deleting forcefully queue with name " + mQueueName);
      client.delete(mQueueName, true);
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
    System.out.println("Started at......: " + tsStart.toString());
    System.out.println("Ended at........: " + tsEnd.toString());
    
    String runTime = reportTime(tsStart, tsEnd);
    System.out.println("Total run time.....: " + runTime);
    
    return end();
  }
  
  /**
   * Get int argument value
   * @param key
   * @param defval
   * @return the value
   */
  private int getIntArg(String key, int defval)
  {
    String strval = mStartupArgs.get(key);
    int result = defval;
    if (strval != null)
    {
      try
      {
        int temp = Integer.valueOf(strval);
        result = temp;
      }
      catch (NumberFormatException e) {}
    }
    return result;
  }
  
  /**
   * Get string argument value
   * @param key
   * @param defval
   * @return the value
   */
  private String getStrArg(String key, String defval)
  {
    String strval = mStartupArgs.get(key);
    String result = defval;
    if (strval != null)
    {
      result = strval;
    }
    return result;
  }
  
  /**
   * Report execution time
   * 
   * @param start The {@link TimeStamp timestamp} of the time the launcher started
   * @param end The {@link TimeStamp timestamp} of the time the launcher ended
   */
  private String reportTime(TimeStamp start, TimeStamp end)
  {
    long diff = end.diff(start);
    long millis = diff % 1000;
    diff = diff / 1000;
    long seconds = diff % 60;
    diff = diff / 60;
    long minutes = diff % 60;
    diff = diff / 60;
    long hours = diff;
    
    String s1 = (hours > 0 ? hours + " hours, " : "");
    String s2 = (minutes > 0 ? minutes + " minutes, " : "");
    return String.format("%s%s%d.%03d seconds", s1, s2, seconds, millis);
  }
}
