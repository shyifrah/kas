package com.kas.mq.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import com.kas.infra.base.AStoppable;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.server.internal.ClientController;

/**
 * MQ server.<br>
 * <br>
 * Note that this object inheres from {@link AStoppable} and not {@link AKasObject} like other classes.
 * This is in order to allow the shutdown hook to stop the MQ server from a different thread.
 * 
 * @author Pippo
 */
public class KasMqServer extends AKasMqAppl 
{
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqServer.class.getName());
  
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
   * - super class initialization
   * - creating client controller
   * - creating the server's listener socket
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    boolean init = super.init();
    if (init)
    {
      mLogger.info("KAS/MQ base application initialized successfully");
      mController = new ClientController(mConfig);
      
      try
      {
        mListenSocket = new ServerSocket(mConfig.getPort());
        mListenSocket.setSoTimeout(mConfig.getConnSocketTimeout());
      }
      catch (IOException e)
      {
        init = false;
        mLogger.error("An error occured while trying to bind server socket with port: " + mConfig.getPort());
        mLogger.fatal("Exception caught: ", e);
        super.term();
      }
    }
    
    String message = "KAS/MQ server V" + mVersion.toString() + (init ? " started successfully" : " failed to start");
    sStartupLogger.info(message);
    mLogger.info(message);
    return init;
  }
  
  /**
   * Terminating the KAS/MQ server.<br>
   * <br>
   * Termination consisting of:
   * - closing server's listener socket
   * - super class termination
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean term()
  {
    mLogger.info("KAS/MQ server termination in progress");
    boolean term = true;
    try
    {
      mListenSocket.close();
    }
    catch (IOException e)
    {
      mLogger.warn("An error occured while trying to close server socket", e);
    }
    
    term = super.term();
    
    return term;
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
        if (errors >= mConfig.getConnMaxErrors())
        {
          mLogger.error("Number of connection errors reached the maximum number of " + mConfig.getConnMaxErrors());
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
