package com.kas.mq.samples;

import java.util.Map;
import com.kas.infra.base.KasException;
import com.kas.infra.base.TimeStamp;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
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
    private ILogger mLogger;
    private MqContext mContext;
    private int mThreadIndex;
    private int mMaxMessages;
    private String mQueueName;
    
    ProdThread(int tix, int maxMsgs, String qname)
    {
      super(ProdThread.class.getSimpleName() + tix);
      mContext = new MqContext();
      mThreadIndex = tix;
      mMaxMessages = maxMsgs;
      mQueueName = qname;
      mLogger = LoggerFactory.getLogger(this.getClass());
    }
    
    public void run()
    {
      mLogger.debug("ProdThread::run() - IN, TIX=" + mThreadIndex + ", Queue=" + mQueueName + ", MaxMessages=" + mMaxMessages);
      
      try
      {
        mLogger.debug("ProdThread::run() - Connecting to " + sHost + ':' + sPort);
        mContext.connect(sHost, sPort, sUserName, sPassword);
      }
      catch (KasException e) {}
      
      mLogger.debug("ProdThread::run() - Starting actual work...");
      for (int i = 0; i < mMaxMessages; ++i)
      {
        String messageBody = "message number: " + (i + 1);
        MqTextMessage putMessage = MqMessageFactory.createTextMessage(messageBody);
        putMessage.setPriority(i%10);
        mContext.put(mQueueName, putMessage);
        
        if (i%100==0)
          System.out.println(String.format("[P%d] ... %d", mThreadIndex, i));
      }
      
      try
      {
        mLogger.debug("ProdThread::run() - Disconnecting from remote host");
        mContext.disconnect();
      }
      catch (KasException e) {}
      
      mLogger.debug("ProdThread::run() - OUT");
    }
  }
  
  static class ConsThread extends Thread
  {
    private ILogger mLogger;
    private MqContext mContext;
    private int mThreadIndex;
    private String mQueueName;
    
    ConsThread(int tix, String qname)
    {
      super(ConsThread.class.getSimpleName() + tix);
      mContext = new MqContext();
      mThreadIndex = tix;
      mQueueName = qname;
      mLogger = LoggerFactory.getLogger(this.getClass());
    }
    
    public void run()
    {
      mLogger.debug("ConsThread::run() - IN, TIX=" + mThreadIndex + ", Queue=" + mQueueName);
      
      try
      {
        mLogger.debug("ConsThread::run() - Connecting to " + sHost + ':' + sPort);
        mContext.connect(sHost, sPort, sUserName, sPassword);
      }
      catch (KasException e) {}
      
      mLogger.debug("ConsThread::run() - Starting actual work...");
      int total = 0;
      IMqMessage<?> getMessage = mContext.get(mQueueName, cConsumerGetTimeout, cConsumerPollingInterval);
      while (getMessage != null)
      {
        if ((total%100==0) && (total != 0))
          System.out.println(String.format("[C%d] ... %d", mThreadIndex, total));
        ++total;
        getMessage = mContext.get(mQueueName, cConsumerGetTimeout, cConsumerPollingInterval);
      }
      
      try
      {
        mLogger.debug("ConsThread::run() - Disconnecting from remote host");
        mContext.disconnect();
      }
      catch (KasException e) {}
      mLogger.debug("ConsThread::run() - OUT");
    }
  }
  
  /**
   * KAS/MQ address and credentials
   */
  static private final long cConsumerPollingInterval = 1000L;
  static private final long cConsumerGetTimeout      = 5000L;
  
  private static String sUserName;
  private static String sPassword;
  private static String sHost;
  private static int    sPort;
  
  private String mQueueName;
  private int    mMaxMessages;
  private int    mNumOfProducers;
  private int    mNumOfConsumers;
  
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
    sUserName = getStrArg("client.app.username", null);
    sPassword = getStrArg("client.app.password", null);
    sHost = getStrArg("client.app.host", "localhost");
    sPort = getIntArg("client.app.port", 14560);
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
      client.connect(sHost, sPort, sUserName, sPassword);
      
      System.out.println("Defining queue with name " + mQueueName + " and a threshold of " + (mMaxMessages * mNumOfProducers) + " messages");
      boolean defined = client.define(mQueueName, mMaxMessages);
      if (!defined)
        throw new KasException("failed to define queue with name " + mQueueName);
      
      for (int i = 0; i < mNumOfConsumers; ++i)
      {
        System.out.println("Creating consumer thread number " + (i+1));
        mConsumers[i] = new ConsThread(i, mQueueName);
      }
      
      for (int i = 0; i < mNumOfProducers; ++i)
      {
        System.out.println("Creating producer thread number " + (i+1));
        mProducers[i] = new ProdThread(i, mMaxMessages/mNumOfProducers, mQueueName);
      }
      
      System.out.println("Consumers and Producers created. starting...");
      for (int i = 0; i < mNumOfConsumers; ++i)
        mConsumers[i].start();
      
      for (int i = 0; i < mNumOfProducers; ++i)
        mProducers[i].start();
      
      System.out.println("Awaiting threads termination...");
      for (int i = 0; i < mNumOfProducers; ++i)
      {
        try
        {
          mProducers[i].join();
        }
        catch (InterruptedException e) {}
      }
      
      for (int i = 0; i < mNumOfConsumers; ++i)
      {
        try
        {
          mConsumers[i].join();
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
    System.out.println("Total traffic (msgs)......: " + mMaxMessages);
    System.out.println("Producer threads..........: " + mNumOfProducers);
    System.out.println("Consumer threads..........: " + mNumOfConsumers);
    System.out.println("   Consume interval....: " + cConsumerPollingInterval);
    System.out.println("   Consume timeout.....: " + cConsumerGetTimeout);
    System.out.println("Started at................: " + tsStart.toString());
    System.out.println("Ended at..................: " + tsEnd.toString());
    
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
