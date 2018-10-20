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
   * Initializing the base KAS application.<br>
   * <br>
   * Initialization consisting of:
   * - application version
   * - register a shutdown hook
   * - get a logger
   * - invoking the application initialization method
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    sStartupLogger.info("KAS base application startup in progress...");
    
    mVersion = new ProductVersion(this.getClass());
    mShutdownHook = new KasApplShutdownHook(this);
    Runtime.getRuntime().addShutdownHook(mShutdownHook);
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    sStartupLogger.info("Logging services are now active, switching to log file");
    
    boolean init = appInit();    
    String message = getAppName() + " V" + mVersion.toString() + (init ? " started successfully" : " failed to start");
    sStartupLogger.info(message);
    mLogger.info(message);
    
    return init;
  }

  /**
   * Terminating the base KAS application.<br>
   * <br>
   * Termination consisting of:
   * - invoking the application termination method
   * - thread pool shutdown
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise 
   */
  public boolean term()
  {
    sStartupLogger.info("KAS base application termination in progress...");
    
    boolean term = appTerm();
    if (!term)
    {
      sStartupLogger.warn("An error occurred during KAS base application termination");
    }
    
    ThreadPool.shutdownNow();
    sStartupLogger.info(getAppName() + " shutdown complete");
    return term;
  }
  
  /**
   * Running the application.
   * 
   * @return {@code true} if main thread should execute the termination, {@code false} otherwise
   * 
   * @see IKasAppl#run()
   */
  public boolean run()
  {
    appExec();
    return end();
  }
  
  /**
   * Refreshing the configuration
   * 
   * @see IBaseListener#refresh()
   */
  public void refresh()
  {
  }
  
  /**
   * Finalizing {@link #run() main function} execution.
   *  
   * @return {@code true} if shutdown hook was successfully de-registered, {@code false} otherwise.
   * The meaning of the shutdown hook removal is that the main thread should execute the {@link #term()}
   * function, or leave it for the shutdown hook.
   */
  protected boolean end()
  {
    mLogger.debug("AKasAppl::end() - IN");
    
    mLogger.info("Try de-registering the shutdown hook...");
    boolean okay = false;
    try
    {
      okay = Runtime.getRuntime().removeShutdownHook(mShutdownHook);
      mLogger.debug("AKasAppl::run() - Shutdown hook was successfully de-registered");
    }
    catch (IllegalStateException e)
    {
      mLogger.debug("AKasAppl::run() - Shutdown hook could not be de-registered. It is probably because JVM is alreadu shutting down");
    }
    
    mLogger.debug("AKasAppl::end() - OUT, Returns=" + okay);
    return okay;
  }
}
