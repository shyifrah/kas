package com.kas.mq.samples.mdbsim;

import java.util.Map;
import com.kas.infra.base.KasException;
import com.kas.infra.base.TimeStamp;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqTextMessage;
import com.kas.mq.samples.Utils;

public class MdbSimulator extends AKasMqAppl
{
  static final long cConsumerPollingInterval = 1000L;
  static final long cConsumerGetTimeout      = 60000L;
  
  private MdbSimulatorParams mParams;
  
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
    try
    {
      client.connect(mParams.mHost, mParams.mPort, mParams.mUserName, mParams.mPassword);
      
      //===========================================================================================
      // defining queues which will be used by mdb
      //===========================================================================================
      if (mParams.mCreateResources)
      {
        Utils.createQueue(client, mParams.mRequestsQueue, 10000);
        if (!mParams.mRequestsQueue.equals(mParams.mRepliesQueue))
        {
          Utils.createQueue(client, mParams.mRepliesQueue , 10000);
        }
      }
      
      //===========================================================================================
      // get requests from requests' queue, and reply to replies' queue
      //===========================================================================================
      IMqMessage<?> message = client.get(mParams.mRequestsQueue, cConsumerGetTimeout, cConsumerPollingInterval);
      while (message != null)
      {
        ++total;
        MqTextMessage request = (MqTextMessage)message;
        String replyBody = "reply to: " + request.getBody();
        MqTextMessage reply = MqMessageFactory.createTextMessage(replyBody);
        reply.setResponseId(request.getMessageId());
        client.put(mParams.mRepliesQueue, reply);
            
        message = client.get(mParams.mRequestsQueue, cConsumerGetTimeout, cConsumerPollingInterval);
      }
      
      //===========================================================================================
      // deleting queues which were used by mdb
      //===========================================================================================
      Utils.deleteQueue(client, mParams.mRequestsQueue);
      Utils.deleteQueue(client, mParams.mRepliesQueue);
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
