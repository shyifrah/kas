package com.kas.q.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.KasObject;
import com.kas.infra.base.ProductVersion;
import com.kas.infra.base.ThreadPool;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.server.internal.MessagingConfiguration;
import com.kas.q.server.internal.ShutdownHook;

public class KasqRunner extends KasObject implements IInitializable
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
      
      KasqRunner queueManager = new KasqRunner();
      boolean initialized = queueManager.init();
      
      if (initialized)
      {
        queueManager.run();
      }
      
      queueManager.term();
      
      mainConfig.term();
    }
    catch (Throwable e)
    {
      System.out.println("Exception caught...");
      e.printStackTrace();
    }
    
    System.out.println("KAS/Q terminated");
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private MessagingConfiguration mConfig;
  private ILogger                mLogger;
  private ILogger                mConsole;
  private boolean                mShouldStop;
  private ClientHandlerManager   mHandlerManager;
  private KasqRepository         mRepository;
  private ServerSocket           mListenSocket;
  private ShutdownHook           mShutdownHook;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  KasqRunner() throws IOException
  {
    mLogger  = LoggerFactory.getLogger(this.getClass());
    mConsole = LoggerFactory.getConsole(this.getClass());
    mConfig  = new MessagingConfiguration();
    
    mShouldStop    = false;
    
    mHandlerManager = new ClientHandlerManager();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean init() 
  {
    mLogger.debug("KasqRunner::init() - IN");
    
    mLogger.info("KAS/Q initialization in progress...");
    boolean success = true;
    
    mConfig.init();
    
    // register shutdown hook
    mShutdownHook  = new ShutdownHook(mListenSocket);
    Runtime.getRuntime().addShutdownHook(mShutdownHook);
    
    // initialize the repository
    mRepository = new KasqRepository(mConfig);
    success = mRepository.init();
    
    // establish server socket
    try
    {
      mListenSocket  = new ServerSocket(mConfig.getPort());
    }
    catch (IOException e)
    {
      success = false;
    }
    
    mLogger.debug("KasqRunner::init() - OUT, Returns=" + success);
    return success;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean term()
  {
    mLogger.debug("KasqRunner::term() - IN");
    
    mConsole.info("KAS/Q termination in progress...");
    
    // close the server socket
    try
    {
      mListenSocket.close();
    }
    catch (Throwable e) {}

    // terminate the repository
    mRepository.term();
    
    // unregister the shutdown hook
    Runtime.getRuntime().removeShutdownHook(mShutdownHook);
    
    // shutdown the thread pool
    ThreadPool.shutdownNow();
    
    mLogger.debug("KasqRunner::term() - OUT");
    return true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  void run() 
  {
    mLogger.debug("KasqRunner::run() - IN");
    
    TimeStamp tsStarted = new TimeStamp();
    ProductVersion version = new ProductVersion(this.getClass());
    
    String msg = "KAS/Q " + mConfig.getManagerName() + " V" + version.toPrintableString() + " started on " + tsStarted.getDateString("-");
    logInfoBoth(msg);
    logInfoBoth(StringUtils.duplicate("=", msg.length()));
    
    mLogger.trace(MainConfiguration.getInstance().toPrintableString(0));
    
    while (!mShouldStop)
    {
      mLogger.trace("Awaiting client connections...");
      try
      {
        Socket socket = mListenSocket.accept();
        logInfoBoth("New connection accepted: " + socket.toString());
        
        mHandlerManager.newClient(socket);
      }
      catch (Throwable e)
      {
        mShouldStop = true;
      }
    }
    
    mLogger.debug("KasqRunner::run() - OUT");
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void logInfoBoth(String msg)
  {
    mLogger.info(msg);
    mConsole.info(msg);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}