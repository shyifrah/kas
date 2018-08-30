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
  /**
   * KAS/MQ address and credentials
   */
  static private final String cHost = "localhost";
  static private final int    cPort = 14560;
  static private final String cUser = "admin";
  static private final String cPass = "admin";
  
  /**
   * Change this: queue name
   */
  //static private final String cQueueName = "temp.queue";
  
  /**
   * Change this: number of messages
   */
  //static private final int cNumOfMessages = 100000;
  private String mQueueName = "temp.queue";
  private int    mMaxMessages = 10000;
  
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
    mQueueName = mStartupArgs.get("client.app.queue");
    String str = mStartupArgs.get("client.app.max.messages");
    try
    {
      mMaxMessages = Integer.valueOf(str);
    }
    catch (NumberFormatException e)
    {
      mMaxMessages = 1000;
    }
    
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
      
      System.out.println("Defining queue with name " + mQueueName + " and a threshold of " + mMaxMessages + " messages");
      boolean defined = client.define(mQueueName, mMaxMessages);
      if (defined)
      {
        System.out.println("Starting putting " + mMaxMessages + " messages in queue...");
        
        // put messages
        for (int i = 0; i < mMaxMessages; ++i)
        {
          String messageBody = "message number: " + (i + 1);
          MqTextMessage putMessage = MqMessageFactory.createTextMessage(messageBody);
          putMessage.setPriority(i%10);
          client.put(mQueueName, putMessage);
          
          if (i%100==0)
            System.out.println("...." + i);
        }
        
        
        System.out.println("Starting getting back " + mMaxMessages + " messages from queue...");
        
        // get messages
        long timeout = 1000L;
        long interval = 1000L;
        int total = 0;
        IMqMessage<?> getMessage = client.get(mQueueName, timeout, interval);
        while (getMessage != null)
        {
          getMessage = client.get(mQueueName, timeout, interval);
          if (getMessage == null)
            System.out.println("Failed to get message...");
          ++total;
          if (total%100==0)
            System.out.println("...." + total);
        }
        
        client.delete(mQueueName, true);
      }
    }
    catch (KasException e)
    {
      e.printStackTrace();
    }
    finally
    {
      disconnectIfNeeded(client);
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
   * Disconnect from server if needed
   * 
   * @param client
   */
  private void disconnectIfNeeded(MqContext client)
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
