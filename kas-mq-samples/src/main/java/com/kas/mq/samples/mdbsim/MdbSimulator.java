package com.kas.mq.samples.mdbsim;

import java.util.HashMap;
import java.util.Map;
import com.kas.appl.AKasApp;
import com.kas.appl.AppLauncher;
import com.kas.infra.base.KasException;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqBytesMessage;
import com.kas.mq.impl.messages.MqMapMessage;
import com.kas.mq.impl.messages.MqMessageFactory;
import com.kas.mq.impl.messages.MqObjectMessage;
import com.kas.mq.impl.messages.MqStreamMessage;
import com.kas.mq.impl.messages.MqStringMessage;
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
public class MdbSimulator extends AKasApp
{
  static final String cKasHome      = "./build/install/kas-mq-samples";
  static final String cAppName      = "MdbSimSample";
  static final String cConfigPrefix = "mdb.sim.";
  
  static final long cConsumerPollingInterval = 1000L;
  static final long cConsumerGetTimeout      = 60000L;

  static public void main(String [] args)
  {
    Map<String, String> defaults = new HashMap<String, String>();
    String kasHome = RunTimeUtils.getProperty(RunTimeUtils.cProductHomeDirProperty, System.getProperty("user.dir") + cKasHome);
    defaults.put(RunTimeUtils.cProductHomeDirProperty, kasHome);
    defaults.put(cConfigPrefix + "username", "guest");
    defaults.put(cConfigPrefix + "password", "guest");
    
    AppLauncher launcher = new AppLauncher(args, defaults);
    Map<String, String> settings = launcher.getSettings();
    
    MdbSimulator app = new MdbSimulator(settings);
    launcher.launch(app);
  }
  
  /**
   * MDB simulator data members
   */
  private MdbSimulatorParams mParams;
  
  /**
   * Construct the application
   * 
   * @param args
   */
  public MdbSimulator(Map<String, String> args)
  {
    mParams = new MdbSimulatorParams(args);
  }

  /**
   * Ignore this
   */
  public String toPrintableString(int level)
  {
    return null;
  }
  
  /**
   * Get the application name
   * 
   * @return the application name
   */
  public String getAppName()
  {
    return cAppName;
  }
  
  /**
   * Initialization
   */
  public boolean appInit()
  {
    return true;
  }
  
  /**
   * Termination
   */
  public boolean appTerm()
  {
    return true;
  }
  
  /**
   * Main logic
   */
  public void appExec()
  {
    int total = 0;
    TimeStamp tsStart = TimeStamp.now();
    MqContext client = new MqContext(cAppName);
    
    mParams.print();
    
    try
    {
      client.connect(mParams.mHost, mParams.mPort, mParams.mUserName, mParams.mPassword);
      System.out.println("Response: " + client.getResponse());
      
      //===========================================================================================
      // defining queues which will be used by mdb
      //===========================================================================================
      System.out.println("Creating resources..." + (mParams.mCreateResources ? "" : " skipped"));
      if (mParams.mCreateResources)
      {
        System.out.println("Creating queue: " + mParams.mRequestsQueue);
        try
        {
          Utils.createQueue(client, mParams.mRequestsQueue, 10000);
        }
        catch (KasException e) {}
        
        System.out.println("Creating queue: " + mParams.mRepliesQueue);
        try
        {
          Utils.createQueue(client, mParams.mRepliesQueue, 10000);
        }
        catch (KasException e) {}
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
        else if (message instanceof MqBytesMessage)
          body = new String(((MqBytesMessage)message).getBody());
        else if (message instanceof MqMapMessage)
         body = ((MqMapMessage)message).getString("client.app.author");
        else if (message instanceof MqStreamMessage)
        {
          int n1 = ((MqStreamMessage)message).readInt();
          String str = ((MqStreamMessage)message).readString();
          int n2 = ((MqStreamMessage)message).readInt();
          body = n1 + ' ' + str + ' ' + n2;
        }
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
  }
}
