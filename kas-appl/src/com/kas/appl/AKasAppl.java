package com.kas.appl;

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
public abstract class AKasAppl extends AKasObject implements IKasAppl
{
  static protected IBaseLogger sStartupLogger = new ConsoleLogger(AKasAppl.class.getName());
  
  /**
   * Logger
   */
  protected ILogger mLogger = null;
  
  /**
   * Shutdown hook
   */
  protected KasApplShutdownHook mShutdownHook = null;
  
  /**
   * The product version
   */
  protected ProductVersion mVersion = null;
  
  /**
   * Startup arguments
   */
  protected Map<String, String> mStartupArgs = null;
  
  /**
   * Construct the {@link AKasAppl application} passing it the startup arguments
   * 
   * @param args The startup arguments
   */
  protected AKasAppl(Map<String, String> args)
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
    
    mShutdownHook = new KasApplShutdownHook(this);
    Runtime.getRuntime().addShutdownHook(mShutdownHook);
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    sStartupLogger.info("Logging services are now active, switching to log file...");
    
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
    ThreadPool.shutdownNow();
    return true;
  }
  
  /**
   * KAS/MQ server's configuration has been refreshed.<br>
   * <br>
   * Most {@link AKasAppl} objects have nothing to do with this, so supply a default implementation
   */
  public void refresh()
  {
  }
  
  /**
   * Running the application.
   * 
   * @return {@code true} if main thread should execute the termination, {@code false} otherwise
   * 
   * @see IKasAppl#run()
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
