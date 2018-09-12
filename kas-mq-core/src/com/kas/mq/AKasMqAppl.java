package com.kas.mq;

import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.ProductVersion;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.logging.IBaseLogger;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * A KAS/MQ application
 * 
 * @author Pippo
 */
public abstract class AKasMqAppl extends AKasObject implements IKasMqAppl
{
  static protected IBaseLogger sStartupLogger = new ConsoleLogger(AKasMqAppl.class.getName());
  
  /**
   * Logger
   */
  protected ILogger mLogger = null;
  
  /**
   * Shutdown hook
   */
  protected KasMqShutdownHook mShutdownHook = null;
  
  /**
   * The Mq configuration object
   */
  protected MqConfiguration mConfig = null;
  
  /**
   * The product version
   */
  protected ProductVersion mVersion = null;
  
  /**
   * Startup arguments
   */
  protected Map<String, String> mStartupArgs = null;
  
  /**
   * Construct the {@link AKasMqAppl application} passing it the startup arguments
   * 
   * @param args The startup arguments
   */
  protected AKasMqAppl(Map<String, String> args)
  {
    mStartupArgs = args;
  }
  
  /**
   * Initializing the KAS/MQ server.<br>
   * <br>
   * Initialization consisting of:
   * - configuration
   * - logger
   * - registering a shutdown hook
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    mVersion = new ProductVersion(this.getClass());
    mConfig = new MqConfiguration();
    mConfig.init();
    if (!mConfig.isInitialized())
      return false;
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    sStartupLogger.info("Logging services are now active, switching to log file...");
    mLogger.info("Loaded configuration: " + mConfig.toPrintableString());
    
    mShutdownHook = new KasMqShutdownHook(this);
    Runtime.getRuntime().addShutdownHook(mShutdownHook);
    return true;
  }

  /**
   * Terminating the KAS/MQ server.<br>
   * <br>
   * Termination consisting of:
   * - remove the shutdown hook
   * - configuration
   * - thread pool shutdown
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise 
   */
  public boolean term()
  {
    mLogger.info("Terminating configuration object and switching back to Console logging...");
    if (mConfig.isInitialized())
      mConfig.term();
    
    ThreadPool.shutdownNow();
    return true;
  }
  
  /**
   * Running the application.
   * 
   * @return {@code true} if main thread should execute the termination, {@code false} otherwise
   * 
   * @see IKasMqAppl#run()
   */
  public abstract boolean run();
  
  /**
   * Finalizing {@link #run() main function} execution.
   *  
   * @return {@code true} if shutdown hook was successfully de-registered, {@code false} otherwise.
   * The meaning of the shutdown hook removal is that the main thread should execute the {@link #term()}
   * function, or leave it for the shutdown hook.
   */
  protected boolean end()
  {
    mLogger.debug("AKasMqAppl::end() - IN");
    
    mLogger.info("Try de-registering the shutdown hook...");
    boolean okay = false;
    try
    {
      okay = Runtime.getRuntime().removeShutdownHook(mShutdownHook);
      mLogger.debug("KasMqServer::run() - Shutdown hook was successfully de-registered");
    }
    catch (IllegalStateException e)
    {
      mLogger.debug("KasMqServer::run() - Shutdown hook could not be de-registered. It is probably because JVM is alreadu shutting down");
    }
    
    mLogger.debug("AKasMqAppl::end() - OUT, Returns=" + okay);
    return okay;
  }
}
