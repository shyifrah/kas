package com.kas.mq.server;

import java.util.Map;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.KasException;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.MqMessageFactory;

/**
 * MQ server stopper.<br>
 * 
 * @author Pippo
 */
public class KasMqStopper extends AKasMqAppl 
{
  static private final String cKasUserSystemProperty = "kas.user";
  static private final String cKasPassSystemProperty = "kas.pass";
  
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqStopper.class.getName());
  
  /**
   * Construct the {@link KasMqStopper} passing it the startup arguments
   * 
   * @param args The startup arguments
   */
  public KasMqStopper(Map<String, String> args)
  {
    super(args);
  }
  
  /**
   * Initializing the KAS/MQ stopper
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    boolean init = super.init();
    if (!init)
    {
      mLogger.error("KAS/MQ base application failed to initialize");
    }
    
    String message = "KAS/MQ server V" + mVersion.toString() + (init ? " started successfully" : " failed to start");
    sStartupLogger.info(message);
    mLogger.info(message);
    return init;
  }
  
  /**
   * Terminating the KAS/MQ stopper
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise 
   */
  public boolean term()
  {
    boolean term = super.term();
    if (!term)
    {
      mLogger.warn("An error occurred during KAS/MQ base application termination");
    }
    
    return term;
  }
  
  /**
   * Run KAS/MQ stopper.<br>
   * <br>
   * The main logic is quite simple: open a new session to the KAS/MQ server and send it
   * a shutdown request. 
   */
  public void run()
  {
    MqContext context = new MqContext();
    int port = mConfig.getPort();
    String user = mStartupArgs.get(cKasUserSystemProperty);
    String pass = mStartupArgs.get(cKasPassSystemProperty);
    String deadq = mConfig.getDeadQueueName();
    
    
    boolean shouldContinue = true;
    boolean connected = false;
    boolean opened = false;
    
    if (shouldContinue)
    {
      try
      {
        sStartupLogger.info("Connecting to KAS/MQ server on localhost...");
        context.connect("localhost", port, user, pass);
        connected = true;
        RunTimeUtils.sleepForMilliSeconds(500);
      }
      catch (KasException e)
      {
        shouldContinue = false;
      }
    }
    
    if (shouldContinue)
    {
      try
      {
        sStartupLogger.info("Openning dead-queue (" + deadq + ")...");
        context.open(deadq);
        opened = true;
        RunTimeUtils.sleepForMilliSeconds(500);
      }
      catch (KasException e)
      {
        shouldContinue = false;
      }
    }
    
    if (shouldContinue)
    {
      IMqMessage<?> request = MqMessageFactory.createShutdownRequest();
      try
      {
        sStartupLogger.info("Putting shutdown request...");
        context.put(request);
        RunTimeUtils.sleepForMilliSeconds(500);
      }
      catch (Exception e)
      {
        shouldContinue = false;
      }
    }
    
    if (shouldContinue || opened)
    {
      try
      {
        sStartupLogger.info("Closing dead-queue...");
        context.close();
        RunTimeUtils.sleepForMilliSeconds(500);
      }
      catch (KasException e)
      {
        shouldContinue = false;
      }
    }
    
    if (shouldContinue || connected)
    {
      try
      {
        sStartupLogger.info("Disconnecting from KAS/MQ server...");
        context.disconnect();
        RunTimeUtils.sleepForMilliSeconds(500);
      }
      catch (KasException e)
      {
        shouldContinue = false;
      }
    }
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Version=(").append(StringUtils.asPrintableString(mVersion)).append(")\n")
      .append(pad).append("  Config=(").append(StringUtils.asPrintableString(mConfig)).append(")\n")
      .append(pad).append("  ShutdownHook=(").append(StringUtils.asPrintableString(mShutdownHook)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
