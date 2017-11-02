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

public class KasqManager extends KasObject implements IInitializable
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static void main(String [] args) throws IOException
  {
    System.out.println("KAS/Q Manager initialization in progress...");
    
    try
    {
      MainConfiguration mainConfig = MainConfiguration.getInstance();
      mainConfig.init();
      if (!mainConfig.isInitialized())
      {
        System.out.println("Configuration initialization failed. Quitting...");
        return;
      }
      
      KasqManager queueManager = new KasqManager();
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
    
    System.out.println("KAS/Q Manager terminated");
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private MessagingConfiguration mConfig;
  private ILogger                mLogger;
  private ILogger                mConsole;
  private boolean                mShouldStop;
  private ClientHandlerManager   mHandlerManager;
  private ServerSocket           mListenSocket;
  //private AdminQueueListener mAdminListener;
  private ShutdownHook           mShutdownHook;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  KasqManager() throws IOException
  {
    mConfig  = new MessagingConfiguration();
    mLogger  = LoggerFactory.getLogger(this.getClass());
    mConsole = LoggerFactory.getConsole(this.getClass());
    
    mShouldStop    = false;
    mListenSocket  = new ServerSocket(mConfig.getPort());
    //mAdminListener = new AdminQueueListener(this);
    mShutdownHook  = new ShutdownHook(mListenSocket);
    mHandlerManager = new ClientHandlerManager();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean init() 
  {
    mLogger.debug("KasqManager::init() - IN");
    
    mLogger.info("KAS/Q initialization in progress...");
    boolean success = true;
    
    mConfig.init();                                       // initialize configuration
    Runtime.getRuntime().addShutdownHook(mShutdownHook);  // registering a shutdown hook
    
    //
    // initialize queue repository
    // QueueRepository.getInstance().init();
    // mLogger.info("Initialization completed");
    
    // initialize admin queue listener
    // QueueRepository.getInstance().getLocalQueue(mConfig.getAdminQueue()).listen(mAdminListener);
    
    mLogger.debug("KasqManager::init() - OUT, Returns=" + success);
    return success;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean term()
  {
    mLogger.debug("KasqManager::term() - IN");
    
    mConsole.info("KAS/Q termination in progress...");
    
    try
    {
      mListenSocket.close();
    }
    catch (Throwable e) {}
    
    Runtime.getRuntime().removeShutdownHook(mShutdownHook);  // remote shutdown hook
    ThreadPool.shutdownNow();                                // shutdown ThreadPool
    /// QueueRepository.getInstance().term();                    // shutdown queue repository
    
    mLogger.debug("KasqManager::term() - OUT");
    return true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  void run() 
  {
    mLogger.debug("KasqManager::run() - IN");
    
    TimeStamp tsStarted = new TimeStamp();
    ProductVersion version = new ProductVersion(this.getClass());
    
    String msg1 = "KAS/Q Manager " + mConfig.getManagerName() + " V" + version.toPrintableString() + " started on " + tsStarted.getDateString("-");
    mLogger.info(msg1);
    mConsole.info(msg1);
    
    String msg2 = StringUtils.duplicate("=", msg1.length());
    mLogger.info(msg2);
    mConsole.info(msg2);
    
    mLogger.trace(MainConfiguration.getInstance().toPrintableString(0));
    
    while (!mShouldStop)
    {
      try
      {
        Socket socket = mListenSocket.accept();
        mLogger.trace("New connection accepted: " + socket.toString());
        mHandlerManager.newClient(socket);
      }
      catch (Throwable e)
      {
        mShouldStop = true;
      }
    }
    
    mLogger.debug("KasqManager::run() - OUT");
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
