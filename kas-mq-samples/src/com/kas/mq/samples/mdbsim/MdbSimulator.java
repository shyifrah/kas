package com.kas.mq.samples.mdbsim;

import java.util.Map;
import com.kas.infra.base.KasException;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqObjectMessage;
import com.kas.mq.impl.MqStringMessage;
import com.kas.mq.samples.Utils;

/**
 * This is a sample {@link AKasMqAppl KAS/MQ application} that is intended
 * to demonstrate the use of the KAS/MQ API.<br>
 * <br>
 * The program reads messages from a designated queue and replies to a different one. 
 * <br><br>
 * For information regarding the applicable arguments for this application,
 * see class {@link MdbSimulatorParams}
 * 
 * @author Pippo
 */
public class MdbSimulator extends AKasMqAppl
{
  static final long cConsumerPollingInterval = 1000L;
  static final long cConsumerGetTimeout      = 60000L;
  
  private MdbSimulatorParams mParams;
  
  /**
   * Construct the application
   * 
   * @param args
   */
  public MdbSimulator(Map<String, String> args)
  {
    super(args);
    mParams = new MdbSimulatorParams(args);
  }

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
    int total = 0;
    TimeStamp tsStart = TimeStamp.now();
    MqContext client = new MqContext();
    
    mParams.print();
    
    try
    {
      client.connect(mParams.mHost, mParams.mPort, mParams.mUserName, mParams.mPassword);
      
      //===========================================================================================
      // defining queues which will be used by mdb
      //===========================================================================================
      System.out.println("Creating resources..." + (mParams.mCreateResources ? "" : " skipped"));
      if (mParams.mCreateResources)
      {
        System.out.println("Creating queue: " + mParams.mRequestsQueue);
        Utils.createQueue(client, mParams.mRequestsQueue, 10000);
        if (!mParams.mRequestsQueue.equals(mParams.mRepliesQueue))
        {
          System.out.println("Creating queue: " + mParams.mRepliesQueue);
          Utils.createQueue(client, mParams.mRepliesQueue , 10000);
        }
      }
      
      //===========================================================================================
      // get requests from requests' queue, and reply to replies' queue
      //===========================================================================================
      System.out.println("Start reading messages and replying to messages...");
      IMqMessage message = client.get(mParams.mRequestsQueue, cConsumerGetTimeout, cConsumerPollingInterval);
      while (message != null)
      {
        ++total;
        UniqueId requestId = message.getMessageId();
        String body = null;
        
        if (message instanceof MqStringMessage)
          body = ((MqStringMessage)message).getBody();
        else if (message instanceof MqObjectMessage)
          body = StringUtils.asString(((MqObjectMessage)message).getBody());
        else
          body = message.getStringProperty("client.app.author", "the king");

        String replyBody = "Reply to " + body;
        MqStringMessage reply = MqMessageFactory.createStringMessage(replyBody);
        reply.setReferenceId(requestId);
        client.put(mParams.mRepliesQueue, reply);
        
        if (total % 100 == 0) System.out.println(String.format("Number of messages processed by MDB: %d", total));
        
        message = client.get(mParams.mRequestsQueue, cConsumerGetTimeout, cConsumerPollingInterval);
      }
      
      //===========================================================================================
      // deleting queues which were used by mdb
      //===========================================================================================
      System.out.println("Deleting created queues (if necessary)...");
      if (mParams.mCreateResources)
      {
        Utils.deleteQueue(client, mParams.mRequestsQueue);
        Utils.deleteQueue(client, mParams.mRepliesQueue);
      }
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
    System.out.println("Total traffic (msgs)......: " + total);
    System.out.println("Started at................: " + tsStart.toString());
    System.out.println("Ended at..................: " + tsEnd.toString());
    
    String runTime = Utils.reportTime(tsStart, tsEnd);
    System.out.println("Total run time.....: " + runTime);
    
    return end();
  }
}
