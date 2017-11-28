package com.kas.q.server.internal;

import com.kas.infra.base.threads.AKasThread;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.server.KasqServer;

public class ShutdownHook extends AKasThread
{
  public static final long cThreadIdNotSet = -1L;
  
  private ILogger mLogger = null;
  private long    mThreadId = cThreadIdNotSet;
  
  /***************************************************************************************************************
   * Constructs a {@code ShutdownHook} object
   */
  public ShutdownHook()
  {
    super("KAS/Q shutdown hook");
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  /***************************************************************************************************************
   * Shutdown hook execution. Call method {@code KasqServer} {@link com.kas.q.server.KasqServer#term()}. 
   */
  public void run()
  {
    mThreadId = Thread.currentThread().getId();
    mLogger.info("Signal main thread to shutdown...");
    KasqServer.getInstance().term();
  }
  
  /***************************************************************************************************************
   * Get the thread ID that runs the Shutdown hook.
   * 
   * @return the thread ID
   */
  public long getThreadId()
  {
    return mThreadId;
  }
}
