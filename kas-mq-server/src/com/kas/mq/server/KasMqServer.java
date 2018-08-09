package com.kas.mq.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import com.kas.infra.base.AStoppable;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IRunnable;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.server.internal.ClientController;
import com.kas.mq.server.internal.MqConfiguration;

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
   * The Mq configuration object
   */
  private MqConfiguration mConfig = null;
  
  /**
   * Logger
   */
  private ILogger mLogger = null;
  
  /**
   * Shutdown hook
   */
  private KasMqStopper mShutdownHook = null;
  
  /**
   * Server socket
   */
  private ServerSocket mListenSocket = null;
  
  /**
   * Client controller
   */
  private ClientController mController = null;
  
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
    mConfig = new MqConfiguration();
    mConfig.init();
    if (!mConfig.isInitialized())
      return false;
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    sStartupLogger.info("KAS/MQ logging services are now active, switching to log file...");
    
    mShutdownHook = new KasMqStopper(this);
    Runtime.getRuntime().addShutdownHook(mShutdownHook);
    
    mController = new ClientController();
    
    try
    {
      mListenSocket = new ServerSocket(mConfig.getPort());
      mListenSocket.setSoTimeout(5000);
    }
    catch (IOException e)
    {
      mLogger.error("An error occured while trying to bind server socket with port: " + mConfig.getPort());
      mLogger.fatal("Exception caught: ", e);
      term();
    }
    
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
   * Run KAS/MQ server.<br>
   * <br>
   * The main logic is quite simple: keep accepting new client connections as long as the main thread
   * was not signaled to shutdown. 
   */
  public void run()
  {
    int errors = 0;
    boolean shouldStop = isStopping();
    while (!shouldStop)
    {
      try
      {
        Socket socket = mListenSocket.accept();
        mController.newClient(socket);
      }
      catch (SocketTimeoutException e)
      {
        mLogger.debug("Accept() timed out, no new connections...");
      }
      catch (IOException e)
      {
        mLogger.warn("An error occurred while trying to accept new client connection");
        ++errors;
        if (errors >= mConfig.getMaxErrors())
        {
          mLogger.error("Number of connection errors reached the maximum number of " + mConfig.getMaxErrors());
          mLogger.error("This could indicate a severe network connectivity issue. Terminating KAS/MQ server...");
          stop();
        }
      }
      
      // re-check if needs to shutdown
      shouldStop = isStopping();
      mLogger.debug("Checking if KAS/MQ server needs to shutdown... " + (shouldStop ? "yep. Terminating main loop..." : "nope..."));
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
