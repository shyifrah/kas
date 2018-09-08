package com.kas.mq.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.IKasMqAppl;
import com.kas.mq.MqConfiguration;
import com.kas.mq.server.internal.SessionController;
import com.kas.mq.server.internal.ServerRepository;
import com.kas.mq.server.internal.ServerHouseKeeper;

/**
 * MQ server.<br>
 * 
 * @author Pippo
 */
public class KasMqServer extends AKasMqAppl implements IMqServer
{
  /**
   * Server socket
   */
  private ServerSocket mListenSocket = null;
  
  /**
   * Server repository
   */
  private IRepository mRepository = null;
  
  /**
   * {@link Session controller}
   */
  private SessionController mController = null;
  
  /**
   * Housekeeper task
   */
  private ServerHouseKeeper mHousekeeper = null;
  
  /**
   * Stop indicator
   */
  private boolean mStop = false;
  
  /**
   * Construct the {@link KasMqServer} passing it the startup arguments
   * 
   * @param args The startup arguments
   */
  public KasMqServer(Map<String, String> args)
  {
    super(args);
  }
  
  /**
   * Initializing the KAS/MQ server.<br>
   * <br>
   * Initialization consisting of:
   * - super class initialization
   * - creating session controller
   * - creating the server's listener socket
   * - creating the server's repository
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    boolean init = super.init();
    if (!init)
    {
      mLogger.error("KAS/MQ base application failed to initialize");
    }
    else
    {
      mLogger.info("KAS/MQ base application initialized successfully");
      mRepository = new ServerRepository(mConfig);
      mController = new SessionController(this);
      
      try
      {
        mListenSocket = new ServerSocket(mConfig.getPort());
        mListenSocket.setSoTimeout(mConfig.getConnSocketTimeout());
      }
      catch (IOException e)
      {
        init = false;
        mLogger.error("An error occurred while trying to bind server socket with port: " + mConfig.getPort());
        mLogger.fatal("Exception caught: ", e);
        super.term();
      }
      
      init = mRepository.init();
      if (!init)
      {
        mLogger.fatal("Server repository failed initialization");
        try
        {
          mListenSocket.close();
        }
        catch (IOException e) {}
        super.term();
      }
      
      mHousekeeper = new ServerHouseKeeper(mController, mRepository);
      ThreadPool.scheduleAtFixedRate(mHousekeeper, 0L, mConfig.getHousekeeperInterval(), TimeUnit.MILLISECONDS);
    }
    
    String message = "KAS/MQ server V" + mVersion.toString() + (init ? " started successfully" : " failed to start");
    sStartupLogger.info(message);
    mLogger.info(message);
    return init;
  }
  
  /**
   * Terminating the KAS/MQ server.<br>
   * <br>
   * Termination is synchronized because it could be executed by the main thread (if a SHUTDOWN request has arrived)
   * or by the Shutdown Hook thread, if, for instance, the user sent a SIGTERM signal (via CTRL-C).
   * <br>
   * Termination consisting of:
   * - closing server's listener socket
   * - terminate server repository
   * - super class termination
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise 
   */
  public synchronized boolean term()
  {
    mLogger.info("KAS/MQ server termination in progress");
    boolean success = false;
    success = mRepository.term();
    if (!success)
    {
      mLogger.warn("An error occurred while shutting the server's repository");
    }
    
    try
    {
      mListenSocket.close();
    }
    catch (IOException e)
    {
      mLogger.warn("An error occurred while trying to close server socket", e);
    }
    
    success = super.term();
    if (!success)
    {
      sStartupLogger.warn("An error occurred during KAS/MQ base application termination");
    }
    
    sStartupLogger.info("KAS/MQ server shutdown complete");
    return success;
  }
  
  /**
   * Run KAS/MQ server.<br>
   * <br>
   * The main logic is quite simple: keep accepting new sessions as long as the main thread
   * was not signaled to shutdown.
   * 
   * @return {@code true} if main thread should execute the termination, {@code false} otherwise
   * 
   * @see IKasMqAppl#run()
   */
  public boolean run()
  {
    int errors = 0;
    while (!isStopping())
    {
      if (!mConfig.isEnabled())
      {
        mLogger.diag("KasMqServer::run() - KAS/MQ server is disabled");
        RunTimeUtils.sleepForMilliSeconds(mConfig.getConnSocketTimeout());
      }
      else
      {
        try
        {
          Socket socket = mListenSocket.accept();
          mController.newSession(socket);
        }
        catch (SocketTimeoutException e)
        {
          mLogger.diag("KasMqServer::run() - Accept() timed out, no new connections...");
        }
        catch (IOException e)
        {
          if (mListenSocket.isClosed())
          {
            stop();
            mLogger.debug("KasMqServer::run() - Socket was closed, Terminating KAS/MQ server...");
          }
          else
          {
            mLogger.warn("An error occurred while trying to accept new client connection");
            ++errors;
            if (errors >= mConfig.getConnMaxErrors())
            {
              stop();
              mLogger.error("Number of connection errors reached the maximum number of " + mConfig.getConnMaxErrors());
              mLogger.error("This could indicate a severe network connectivity issue. Terminating KAS/MQ server...");
            }
          }
        }
      }
      
      mLogger.diag("KasMqServer::run() - Checking if KAS/MQ server needs to shutdown... " + (mStop ? "yep. Terminating main loop..." : "nope..."));
    }
    
    return end();
  }
  
  /**
   * Stop the main loop
   */
  public synchronized void stop()
  {
    mStop = true;
  }
  
  /**
   * Return the stop-state of the server
   * 
   * @return the stop-state of the server
   */
  public synchronized boolean isStopping()
  {
    return mStop;
  }
  
  /**
   * Get the {@link ServerRepository} object
   * 
   * @return the {@link ServerRepository} object
   * 
   * @see IMqServer#getRepository()
   */
  public IRepository getRepository()
  {
    return mRepository;
  }
  
  /**
   * Get the {@link MqConfiguration} object
   * 
   * @return the {@link MqConfiguration} object
   * 
   * @see IMqServer#getConfig()
   */
  public MqConfiguration getConfig()
  {
    return mConfig;
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
      .append(pad).append("  Version=(").append(StringUtils.asPrintableString(mVersion)).append(")\n")
      .append(pad).append("  Config=(").append(StringUtils.asPrintableString(mConfig)).append(")\n")
      .append(pad).append("  ShutdownHook=(").append(StringUtils.asPrintableString(mShutdownHook)).append(")\n")
      .append(pad).append("  Controller=(").append(StringUtils.asPrintableString(mController)).append(")\n")
      .append(pad).append("  Repository=(").append(StringUtils.asPrintableString(mRepository)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
