package com.kas.appl;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ProductVersion;
import com.kas.infra.base.threads.ThreadPool;

/**
 * A KAS/MQ application
 * 
 * @author Pippo
 */
public abstract class AKasApp extends AKasObject implements IKasApp
{
  static protected Logger sStdout = LogManager.getLogger("stdout");
  
  /**
   * Map of arguments
   */
  protected Map<String, String> mArgMap;
  
  /**
   * Logger
   */
  protected Logger mLogger = null;
  
  /**
   * Indicator for termination
   */
  private boolean mTerminated = false;
  
  /**
   * Shutdown hook
   */
  protected AppShutdownHook mShutdownHook = null;
  
  /**
   * The product version
   */
  protected ProductVersion mVersion = null;
  
  /**
   * Construct the {@link AKasApp application}
   */
  protected AKasApp(Map<String, String> args)
  {
    mArgMap = args;
  }
  
  /**
   * Initializing the application.<br>
   * <br>
   * Most application doesn't require real initialization, so we provide default initialization.
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean appInit()
  {
    return true;
  }
  
  /**
   * Initializing the base KAS application.<br>
   * Initialization consisting of:
   * - application version
   * - register a shutdown hook
   * - get a logger
   * - invoking the application initialization method
   * 
   * @return
   *   {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    sStdout.info("KAS base application startup in progress...");
    
    mVersion = new ProductVersion(getClass());
    mLogger = LogManager.getLogger(getClass());
    
    mShutdownHook = new AppShutdownHook(this);
    Runtime.getRuntime().addShutdownHook(mShutdownHook);
    sStdout.info("Logging services are now active, switching to log file");
    
    boolean init = appInit();    
    String message = getAppName() + " V" + mVersion.toString() + (init ? " started successfully" : " failed to start");
    sStdout.info(message);
    mLogger.info(message);
    
    return init;
  }
  
  /**
   * Running the application. 
   */
  public abstract void appExec();
  
  /**
   * Running the application.
   * 
   * @see IKasApp#run()
   */
  public void run()
  {
    appExec();
    
    if (!mShutdownHook.isRunning())
    {
      mLogger.info("Try de-registering the shutdown hook...");
      try
      {
        boolean okay = Runtime.getRuntime().removeShutdownHook(mShutdownHook);
        mLogger.debug("AKasAppl::run() - Shutdown hook deregistration: {}", okay);
      }
      catch (IllegalStateException e)
      {
        mLogger.debug("AKasAppl::run() - Shutdown hook could not be de-registered. JVM is already shutting down");
      }
    }
  }
  
  /**
   * Terminating the application.<br>
   * Most application doesn't require real termination, so we provide default initialization.
   * 
   * @return
   *   {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean appTerm()
  {
    return true;
  }

  /**
   * Terminating the base KAS application.<br>
   * Termination consisting of:
   * - invoking the application termination method
   * - thread pool shutdown
   * 
   * @return
   *   {@code true} if termination completed successfully, {@code false} otherwise 
   */
  public synchronized boolean term()
  {
    sStdout.info("KAS base application termination in progress...");
    mTerminated = true;
    
    boolean term = appTerm();
    if (!term)
    {
      sStdout.warn("An error occurred during KAS base application termination");
    }
    
    ThreadPool.shutdownNow();
    sStdout.info("{} shutdown complete", getAppName());
    return term;
  }
  
  /**
   * Return indication whether the application was terminated
   * 
   * @return
   *   {@code true} if application was terminated, {@code false} otherwise
   */
  public boolean isTerminated()
  {
    return mTerminated;
  }
  
  /**
   * Refreshing the configuration
   */
  public void refresh()
  {
  }
}
