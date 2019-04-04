package com.kas.mq.server;

import java.util.Map;
import com.kas.appl.AKasApp;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;

/**
 * MQ server stopper.<br>
 * 
 * @author Pippo
 */
public class KasMqStopper extends AKasApp 
{
  static private final String cAppName = "KAS/MQ server-stopper";
  static private final String cKasUser = "kas.user";
  static private final String cKasPass = "kas.pass";
    
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
   * Construct main KAS/MQ server's stopper
   * 
   * @param args
   *   Map of arguments passed via launcher's main function
   */
  public KasMqStopper(Map<String, String> args)
  {
    super(args);
    mUserName = args.get(cKasUser);
    mPassword = args.get(cKasPass);
  }
  
  /**
   * Get the application name
   * 
   * @return
   *   the application name
   */
  public String getAppName()
  {
    return cAppName;
  }
  
  /**
   * Initializing the KAS/MQ stopper
   * 
   * @return
   *   {@code true} if initialization completed successfully, {@code false} otherwise 
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
   * @return
   *   {@code true} if termination completed successfully, {@code false} otherwise 
   */
  public boolean appTerm()
  {
    mConfig.term();
    return true;
  }
  
  /**
   * Run KAS/MQ stopper.<br>
   * The main logic is quite simple: open a new session to the KAS/MQ server and send it
   * a shutdown request. 
   */
  public void appExec()
  {
    MqServerConnectionPool connPool = MqServerConnectionPool.getInstance();
    MqServerConnection conn = connPool.allocate();
    
    boolean shouldContinue = true;
    try
    {
      conn.connect("localhost", mConfig.getPort());
    }
    catch (Throwable e)
    {
      mStdout.error("Exception caught: ", e);
      shouldContinue = false;
    }
    
    if (shouldContinue)
    {
      try
      {
        boolean authed = conn.login(mUserName, mPassword);
        if (!authed)
        {
          mStdout.error(conn.getResponse());
          shouldContinue = false;
        }
      }
      catch (Throwable e)
      {
        mStdout.error("Exception caught: ", e);
        shouldContinue = false;
      }
    }
    
    if (shouldContinue)
    {
      try
      {
        boolean termed = conn.termServer();
        if (!termed)
        {
          mStdout.error(conn.getResponse());
          shouldContinue = false;
        }
        else
        {
          mStdout.info(conn.getResponse());
        }
      }
      catch (Throwable e)
      {
        mStdout.error("Exception caught: ", e);
        shouldContinue = false;
      }
    }
    
    connPool.release(conn);
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
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
