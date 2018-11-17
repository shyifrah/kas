package com.kas.mq.server;

import java.util.Map;
import com.kas.appl.AKasAppl;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.KasException;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.MqRequestFactory;

/**
 * MQ server stopper.<br>
 * 
 * @author Pippo
 */
public class KasMqStopper extends AKasAppl 
{
  static final String cAppName = "KAS/MQ server-stopper";
  
  static private final String cKasUserSystemProperty = "kas.user";
  static private final String cKasPassSystemProperty = "kas.pass";
  
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqStopper.class.getName());
  
  /**
   * KAS/MQ server's configuration
   */
  private MqConfiguration mConfig = null;
  
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
   * Get the application name
   * 
   * @return the application name
   */
  public String getAppName()
  {
    return cAppName;
  }
  
  /**
   * Initializing the KAS/MQ stopper
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean appInit()
  {
    mConfig = new MqConfiguration();
    mConfig.init();
    if (!mConfig.isInitialized())
      return false;
    
    mConfig.register(this);
    return true;
  }
  
  /**
   * Terminating the KAS/MQ stopper
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise 
   */
  public boolean appTerm()
  {
    mConfig.term();
    return true;
  }
  
  /**
   * Run KAS/MQ stopper.<br>
   * <br>
   * The main logic is quite simple: open a new session to the KAS/MQ server and send it
   * a shutdown request. 
   */
  public void appExec()
  {
    MqContext context = new MqContext(cAppName);
    int port = mConfig.getPort();
    String user = mStartupArgs.get(cKasUserSystemProperty);
    String pass = mStartupArgs.get(cKasPassSystemProperty);
    String deadq = mConfig.getDeadQueueName();
    
    boolean shouldContinue = true;
    boolean connected = false;
    
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
      IMqMessage request = MqRequestFactory.createTermServerRequest(user);
      try
      {
        sStartupLogger.info("Putting shutdown request...");
        context.put(deadq, request);
        RunTimeUtils.sleepForMilliSeconds(500);
      }
      catch (Exception e)
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
