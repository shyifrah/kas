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
   * Indicator whether application should stop
   */
  protected boolean mStop = false;
  
  
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
    sStartupLogger.info("KAS/MQ logging services are now active, switching to log file...");
    
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
    if (mShutdownHook != null)
    {
      mLogger.info("Shutdown hook is registered, will try to remove it...");
      if (Thread.currentThread().getName().equals(KasMqShutdownHook.class.getSimpleName()))
      {
        mLogger.info("Skipping removal of shutdown hook as termination process is executed under it...");
      }
      else
      {
        Runtime.getRuntime().removeShutdownHook(mShutdownHook);
        mLogger.info("Shutdown hook was successfully removed");
      }
    }
    
    mLogger.info("Terminating configuration object and switching back to Console logging...");
    if (mConfig.isInitialized())
      mConfig.term();
    
    ThreadPool.shutdownNow();
    return true;
  }
  
  /**
   * Running the application.
   */
  public abstract void run();
  
  /**
   * Indicate that the caller wants the object to change its state to "stopping" 
   */
  public synchronized void stop()
  {
    mStop = true;
  }
  
  /**
   * Get indication if the object is in "stopping" state
   * 
   * @return indication if the object is in "stopping" state
   */
  public synchronized boolean isStopping()
  {
    return mStop;
  }
}
