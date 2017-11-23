package com.kas.q.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ProductVersion;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.KasqMessageFactory;
import com.kas.q.server.internal.ClientController;
import com.kas.q.server.internal.MessagingConfiguration;
import com.kas.q.server.internal.ShutdownHook;

public class KasqServer extends AKasObject implements IInitializable
{
  /***************************************************************************************************************
   * Main function.
   * 
   * @param args arguments passed to the main function 
   */
  public static void main(String [] args) throws IOException
  {
    System.out.println("KAS/Q initialization in progress...");
    
    try
    {
      MainConfiguration mainConfig = MainConfiguration.getInstance();
      mainConfig.init();
      if (!mainConfig.isInitialized())
      {
        System.out.println("Configuration initialization failed. Quitting...");
        return;
      }
      
      KasqServer server = getInstance();
      boolean initialized = server.init();
      if (initialized)
      {
        server.run();
      }
      
      server.term();
      mainConfig.term();
    }
    catch (Throwable e)
    {
      System.out.println("Exception caught...");
      e.printStackTrace();
    }
    
    System.out.println("KAS/Q terminated");
  }
  
  /***************************************************************************************************************
   * 
   */
  private static KasqServer sInstance = null;
  
  /***************************************************************************************************************
   * 
   */
  private MessagingConfiguration mConfig;
  private ILogger                mLogger;
  private ILogger                mConsole;
  private boolean                mShouldStop;
  private ClientController       mController;
  private KasqRepository         mRepository;
  private ServerSocket           mListenSocket;
  private ShutdownHook           mShutdownHook;
  
  /***************************************************************************************************************
   * Gets the singleton instance of {@code KasqServer}
   * 
   * @return the single instance of the {@code KasqServer}
   */
  public static KasqServer getInstance()
  {
    if (sInstance == null)
      sInstance = new KasqServer();
    
    return sInstance;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqServer} object
   */
  private KasqServer()
  {
    mLogger  = LoggerFactory.getLogger(this.getClass());
    mConsole = LoggerFactory.getStdout(this.getClass());
    
    mConfig = new MessagingConfiguration();
    mShouldStop = false;
    
    new KasqMessageFactory();
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean init() 
  {
    mLogger.debug("KasqServer::init() - IN");
    
    mLogger.info("KAS/Q initialization in progress...");
    boolean success = true;
    
    mConfig.init();
    
    // register shutdown hook
    mShutdownHook  = new ShutdownHook(mListenSocket);
    Runtime.getRuntime().addShutdownHook(mShutdownHook);
    
    // initialize the repository
    mRepository = new KasqRepository();
    success = mRepository.init();
    
    // establish server socket
    try
    {
      mListenSocket = new ServerSocket(mConfig.getPort());
    }
    catch (IOException e)
    {
      success = false;
    }
    
    mController = new ClientController(mListenSocket);
    
    mLogger.debug("KasqServer::init() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean term()
  {
    mLogger.debug("KasqServer::term() - IN");
    
    mConsole.info("KAS/Q termination in progress...");
    
    // close the server socket
    try
    {
      mListenSocket.close();
    }
    catch (Throwable e) {}
    
    mController.closeAll();

    // terminate the repository
    mRepository.term();
    
    // unregister the shutdown hook
    Runtime.getRuntime().removeShutdownHook(mShutdownHook);
    
    // shutdown the thread pool
    ThreadPool.shutdownNow();
    
    mLogger.debug("KasqServer::term() - OUT");
    return true;
  }
  
  /***************************************************************************************************************
   * Main function of the {@code KasqServer}.
   * Essentially, this function prints the startup messages, accepts new connections from clients
   * and hands them over to the {@code ClientHandlerManager} to create a {@code ClientHandler} to handle the
   * connection.
   */
  void run() 
  {
    mLogger.debug("KasqServer::run() - IN");
    
    TimeStamp tsStarted = new TimeStamp();
    ProductVersion version = new ProductVersion(this.getClass());
    
    String msg = "KAS/Q " + mConfig.getManagerName() + " V" + version.toPrintableString() + " started on " + tsStarted.getDateString("-");
    logInfoBoth(msg);
    logInfoBoth(StringUtils.duplicate("=", msg.length()));
    
    mLogger.trace(MainConfiguration.getInstance().toPrintableString(0));
    mLogger.trace(toPrintableString(0));
    
    while (!mShouldStop)
    {
      mLogger.trace("Awaiting client connections...");
      try
      {
        Socket socket = mListenSocket.accept();
        mController.startClient(socket);
      }
      catch (Throwable e)
      {
        logInfoBoth("Server socket was closed. Terminating...");
        mShouldStop = true;
      }
    }
    
    mLogger.debug("KasqServer::run() - OUT");
  }
  
  /***************************************************************************************************************
   * Gets the configuration object associated with this {@code KasqServer}
   * 
   * @return the {@code MessagingConfiguration}
   */
  public MessagingConfiguration getConfiguration()
  {
    return mConfig;
  }
  
  /***************************************************************************************************************
   * Gets the repository object associated with this {@code KasqServer}
   * 
   * @return the {@code KasqRepository}
   */
  public KasqRepository getRepository()
  {
    return mRepository;
  }
  
  /***************************************************************************************************************
   * Gets the client controller
   * 
   * @return the {@code ClientRepository}
   */
  public ClientController getController()
  {
    return mController;
  }
  
  /***************************************************************************************************************
   * Log a message to both Console and Log file.
   * 
   * @param the message to log
   */
  private void logInfoBoth(String msg)
  {
    mLogger.info(msg);
    mConsole.info(msg);
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Configuration=(").append(mConfig.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  Repository=(").append(mRepository.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  Controller=(").append(mController.toPrintableString(level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
