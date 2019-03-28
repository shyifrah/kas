package com.kas.appl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.IObject;
import com.kas.infra.base.threads.AKasThread;

/**
 * The {@link AppShutdownHook} is a thread that kicks in the minute a shutdown signal
 * is sent to the application.<br>
 * This is hook is registered via a call to {@link Runtime#addShutdownHook(Thread)}.
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
  private Logger mLogger;
  
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
   * @param appl
   *   The KAS application
   */
  public AppShutdownHook(AKasApp appl)
  {
    super(AppShutdownHook.class.getSimpleName());
    mLogger = LogManager.getLogger(getClass());
    mApplication = appl;
  }
  
  /**
   * Running the shutdown hook.<br>
   * The procedure is actually quite simple - invoking the application's {@link AKasApp#term()} method.
   */
  public void run()
  {
    mIsRunning = true;
    mLogger.warn("Shutdown hook was called. Terminating application...");
    mApplication.term();
  }
  
  /**
   * Get the shutdown hook running state
   * 
   * @return
   *   the shutdown hook running state
   */
  public boolean isRunning()
  {
    return mIsRunning;
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
      .append(pad).append("  Name=").append(mApplication.getAppName()).append("\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
