package com.kas.mq.server;

import java.util.HashMap;
import java.util.Map;
import com.kas.appl.AKasApp;
import com.kas.appl.AppLauncher;
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
public class KasMqStopper extends AKasApp 
{
  static private final String cKasHome = "./build/install/kas-mq-server";
  static private final String cAppName = "KAS/MQ server-stopper";
  static private final String cKasUser = "kas.user";
  static private final String cKasPass = "kas.pass";
  
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqStopper.class.getName());
  
  static public void main(String [] args)
  {
    Map<String, String> defaults = new HashMap<String, String>();
    defaults.put(RunTimeUtils.cProductHomeDirProperty, cKasHome);
    defaults.put(cKasUser, "root");
    defaults.put(cKasPass, "root");
    
    AppLauncher launcher = new AppLauncher(args, defaults);
    Map<String, String> settings = launcher.getSettings();
    
    KasMqStopper app = new KasMqStopper(settings);
    launcher.launch(app);
  }
  
  /**
   * KAS/MQ server's configuration
   */
  private MqConfiguration mConfig = null;
  
  /**
   * Credentials for the stopper
   */
  private String mUserName;
  private String mPassword;
  
  /**
   * Construct the {@link KasMqStopper} passing it the startup arguments
   * 
   * @param settings The startup arguments
   */
  protected KasMqStopper(Map<String, String> settings)
  {
    mUserName = settings.get(cKasUser);
    mPassword = settings.get(cKasPass);
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
    String deadq = mConfig.getDeadQueueName();
    
    boolean shouldContinue = true;
    boolean connected = false;
    
    if (shouldContinue)
    {
      try
      {
        sStartupLogger.info("Connecting to KAS/MQ server on localhost...");
        context.connect("localhost", port, mUserName, mPassword);
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
      IMqMessage request = MqRequestFactory.createTermServerRequest(mUserName);
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
