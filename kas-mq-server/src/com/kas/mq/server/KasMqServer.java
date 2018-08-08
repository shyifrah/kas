package com.kas.mq.server;

import com.kas.config.MainConfiguration;
import com.kas.infra.base.AStoppable;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IRunnable;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * MQ server.<br>
 * <br>
 * Note that this object inheres from {@link AStoppable} and not {@link AKasObject} like other classes.
 * This is in order to allow the shutdown hook to stop the MQ server from a different thread.
 * 
 * @author Pippo
 */
public class KasMqServer extends AStoppable implements IInitializable, IRunnable
{
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqServer.class.getName());
  
  /**
   * The main configuration object
   */
  private MainConfiguration mConfig = null;
  
  /**
   * Logger
   */
  private ILogger mLogger = null;
  
  /**
   * Shutdown hook
   */
  private KasMqStopper mShutdownHook = null;
  
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
    mConfig = MainConfiguration.getInstance();
    boolean init = mConfig.init();
    if (!init)
      return false;
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    
    mShutdownHook = new KasMqStopper(this);
    Runtime.getRuntime().addShutdownHook(mShutdownHook);
    
    sStartupLogger.info("KAS/MQ server initialization complete. Logger is active");
    mLogger.info("KAS/MQ server initialization complete. Logger is active");
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
    mLogger.info("KAS/MQ server termination in progress");
    
    Runtime.getRuntime().removeShutdownHook(mShutdownHook);
    
    mConfig.term();
    
    ThreadPool.shutdownNow();
    return true;
  }
  
  /**
   * Run KAS/MQ server
   */
  public void run()
  {
    while (!isStopping())
    {
      // ... do what you need to do - for example, accept() new client sockets...
    }
  }
  
  /**
   * Returns a replica of this {@link KasMqServer}.
   * 
   * @return a replica of this {@link KasMqServer}
   * 
   * @throws RuntimeException Always. Cannot replicate KasMqServer
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public KasMqServer replicate()
  {
    throw new RuntimeException("Cannot replicate KasMqServer object as it is internal system object");
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
      .append(pad).append("  Config=(").append(StringUtils.asPrintableString(mConfig)).append(")\n")
      .append(pad).append("  ShutdownHook=(").append(StringUtils.asPrintableString(mShutdownHook)).append(")\n");;
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
