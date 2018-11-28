package com.kas.appl;

import com.kas.infra.base.threads.AKasThread;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * The {@link AppShutdownHook} is a thread that kicks in the minute a shutdown signal is sent to the application.<br>
 * 
 * This is actually a shutdown hook that is registered via a call to {@link Runtime#addShutdownHook(Thread)}.
 * 
 * @author Pippo
 * 
 * @see java.lang.Runtime#addShutdownHook(Thread)
 */
public class AppShutdownHook extends AKasThread
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * The KAS application that should be stopped
   */
  private AKasApp mApplication;
  
  /**
   * Indicator if the hook is running
   */
  private boolean mIsRunning = false;
  
  /**
   * Construct {@link AppShutdownHook}
   * 
   * @param appl The KAS application
   */
  public AppShutdownHook(AKasApp appl)
  {
    super(AppShutdownHook.class.getSimpleName());
    mLogger = LoggerFactory.getLogger(this.getClass());
    mApplication = appl;
  }
  
  /**
   * Running the shutdown hook.<br>
   * <br>
   * The procedure is actually quite simple - invoking the application's {@link AKasApp#term()} method.
   * 
   * @see com.kas.infra.base.IInitializable#term()
   */
  public void run()
  {
    mLogger.warn("Shutdown hook was called. Terminating application...");
    setRunning(true);
    mApplication.term();
  }
  
  /**
   * Mark the shutdown hook is running or not
   * 
   * @param b The running state of the shutdown hook
   */
  public synchronized void setRunning(boolean b)
  {
    mIsRunning = b;
  }
  
  /**
   * Get the shutdown hook running state
   * 
   * @return the shutdown hook running state
   */
  public synchronized boolean isRunning()
  {
    return mIsRunning;
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
      .append(pad).append("  Name=").append(mApplication.getAppName()).append("\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
