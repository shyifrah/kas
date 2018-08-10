package com.kas.mq;

import com.kas.infra.base.AStoppable;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IRunnable;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.logging.IBaseLogger;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * A KAS/MQ application
 * 
 * @author Pippo
 */
public abstract class AKasMqAppl extends AStoppable implements IInitializable, IRunnable
{
  static IBaseLogger sStartupLogger = new ConsoleLogger(AKasMqAppl.class.getName());
  
  /**
   * Logger
   */
  protected ILogger mLogger = null;
  
  /**
   * Shutdown hook
   */
  protected KasMqStopper mShutdownHook = null;
  
  /**
   * The Mq configuration object
   */
  protected MqConfiguration mConfig = null;
  
  /**
   * Initializing the KAS/MQ server.<br>
   * <br>
   * Initialization consisting of:
   * - configuration
   * - logger
   * - registering a shutdown hook
   * 
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    mConfig = new MqConfiguration();
    mConfig.init();
    if (!mConfig.isInitialized())
      return false;
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    sStartupLogger.info("KAS/MQ logging services are now active, switching to log file...");
    
    mShutdownHook = new KasMqStopper(this);
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
      if (Thread.currentThread().getName().equals(KasMqStopper.class.getSimpleName()))
      {
        mLogger.warn("Cannot remove Shutdown hook as termination process is executed under it...");
      }
      else
      {
        Runtime.getRuntime().removeShutdownHook(mShutdownHook);
        mLogger.info("Shutdown hook was successfully removed");
      }
    }
    
    mLogger.info("Terminating configuration object and switching back to Console loggging...");
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
   * Returns a replica of this {@link AKasMqAppl}.
   * 
   * @return a replica of this {@link AKasMqAppl}
   * 
   * @throws RuntimeException always. Cannot replicate class
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public AKasMqAppl replicate()
  {
    throw new RuntimeException("Cannot replicate objects of type AKasMqAppl");
  }

  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}