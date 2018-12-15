package com.kas.mq.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.kas.appl.AKasApp;
import com.kas.appl.AppLauncher;
import com.kas.db.DbConfiguration;
import com.kas.db.DbConnectionPool;
import com.kas.db.DbUtils;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.server.internal.SessionController;
import com.kas.mq.server.repo.ServerRepository;
import com.kas.mq.server.internal.ServerHouseKeeper;
import com.kas.mq.server.internal.ServerNotifier;

/**
 * MQ server.<br>
 * 
 * @author Pippo
 */
public class KasMqServer extends AKasApp implements IMqServer
{
  static private final String cKasHome = "/build/install/kas-mq-server";
  static private final String cAppName = "KAS/MQ server";
  
  static public void main(String [] args)
  {
    Map<String, String> defaults = new HashMap<String, String>();
    String kasHome = RunTimeUtils.getProperty(RunTimeUtils.cProductHomeDirProperty, System.getProperty("user.dir") + cKasHome);
    defaults.put(RunTimeUtils.cProductHomeDirProperty, kasHome);
    
    AppLauncher launcher = new AppLauncher(args, defaults);
    Map<String, String> settings = launcher.getSettings();
    
    KasMqServer app = new KasMqServer(settings);
    launcher.launch(app);
  }
  
  /**
   * Server socket
   */
  private ServerSocket mListenSocket = null;
  
  /**
   * {@link ServerRepository}
   */
  private IRepository mRepository = null;
  
  /**
   * {@link SessionController}
   */
  private SessionController mController = null;
  
  /**
   * {@link ServerHouseKeeper housekeeping task}
   */
  private ServerHouseKeeper mHousekeeper = null;
  
  /**
   * {@link ServerNotifier}
   */
  private ServerNotifier mNotifier = null;
  
  /**
   * KAS/MQ server's configuration
   */
  private MqConfiguration mConfig = null;
  
  /**
   * DB configuration
   */
  private DbConfiguration mDbConfig = null;
  
  /**
   * DB connection pool
   */
  private DbConnectionPool mDbConnPool = null;
  
  /**
   * Stop indicator
   */
  private boolean mStop = false;
  
  /**
   * Construct the {@link KasMqServer} passing it the startup arguments
   * 
   * @param settings The startup arguments
   */
  protected KasMqServer(Map<String, String> settings)
  {
  }
  
  /**
   * Get the application name
   * 
   * @return the application name
   */
  public String getAppName()
  {
    return cAppName;
  }
  
  /**
   * Initializing the KAS/MQ server.<br>
   * <br>
   * Initialization consisting of:
   * - creating and initializing configuration object
   * - creating and initializing the db connection pool, and initialize schema if necessary
   * - creating the server's repository
   * - start the housekeeper
   * - creating session controller
   * - creating the server's notifier
   * - creating the server's listener socket
   * - notifying remote servers of activation
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean appInit()
  {
    mDbConfig = new DbConfiguration();
    sStartupLogger.info("KAS/MQ server will use " + mDbConfig.getDbType() + " DB on " + mDbConfig.getHost() + ":" + mDbConfig.getPort());
    mDbConfig.init();
    
    mConfig = new MqConfiguration();
    mConfig.init();
    
    mConfig.register(this);
    
    boolean init = DbConnectionPool.init(mDbConfig);
    if (!init)
    {
      mLogger.fatal("Server DB connection pool failed initialization");
      return false;
    }
    sStartupLogger.info("DB additional information: Version=" + DbConnectionPool.getInstance().getDbVersion() + ", Schema=" + mDbConfig.getSchemaName() + ", User=" + mDbConfig.getUserName());
    DbUtils.initSchema();

    mRepository = new ServerRepository(mConfig);
    mHousekeeper = new ServerHouseKeeper(mRepository);
    mController = new SessionController(this);
    mNotifier = new ServerNotifier(mRepository);

    try
    {
      mListenSocket = new ServerSocket(mConfig.getPort());
      mListenSocket.setSoTimeout(mConfig.getConnSocketTimeout());
    }
    catch (IOException e)
    {
    	sStartupLogger.error("An error occurred while trying to bind server socket with port: " + mConfig.getPort());
    	sStartupLogger.error("Exception caught: " +  e.getMessage());
      return false;
    }
    
    init = mRepository.init();
    if (!init)
    {
    	mLogger.fatal("Server repository failed initialization");
      return false;
    }
    
    startHouseKeeper();  
    mNotifier.notifyServerActivated();    
    return true;
  }
  
  /**
   * Terminating the KAS/MQ server.<br>
   * <br>
   * Termination is synchronized because it could be executed by the main thread (if a SHUTDOWN request has arrived)
   * or by the Shutdown Hook thread, if, for instance, the user sent a SIGTERM signal (via CTRL-C).
   * <br>
   * Termination consisting of:
   * - notify remote servers of inactivation
   * - stop the housekeeper
   * - terminate server repository
   * - closing server's listener socket
   * - shutdown the db connection pool
   * - terminate configuration object
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise 
   */
  public synchronized boolean appTerm()
  {	  
    mNotifier.notifyServerDeactivated();	

    stopHouseKeeper();
	
    boolean term = mRepository.term();
    if (!term)
    {
      mLogger.warn("An error occurred while shutting the server's repository");
    }
    
    try
    {
      if (mListenSocket != null)
        mListenSocket.close();
    }
    catch (IOException e)
    {
      mLogger.warn("An error occurred while trying to close server socket", e);
    }	  

    try
    {
    	DbConnectionPool.getInstance().shutdown();
    }
    catch (RuntimeException e)
    {
    	mLogger.error("An error occured while trying to close DBConnectionPool");
    }
    
    mConfig.term();
    return true;
  }
  
  /**
   * Configuration has been refreshed.<br>
   * <br>
   * If KAS/MQ server's configuration has been refreshed, it means that the server
   * should restart (if necessary) the house-keeper task
   */
  public void refresh()
  {
    mLogger.debug("KasMqServer::refresh() - IN");
    
    /*
     * need to update housekeeper task state according to this scheme:
     * 
     * if it's not running & disabled - stop
     * if it's not running & enabled - start
     * --if running & disabled - stop
     * if running & enabled - 
     *   if interval changed - stop and start
     *   else - do nothing
     */
    
    mLogger.debug("KasMqServer::refresh() - OUT");
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
  public void appExec()
  {
    int errors = 0;
    sStartupLogger.info("KAS/MQ server " + mConfig.getManagerName() + " available on port " +  mConfig.getPort ());
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
   * Get the {@link DbConnectionPool} object
   * 
   * @return the {@link DbConnectionPool} object
   * 
   * @see IMqServer#getDbConnectionPool()
   */
  public DbConnectionPool getDbConnectionPool()
  {
    return mDbConnPool;
  }
  
  /**
   * Stop the house keeper task
   */
  private void stopHouseKeeper()
  {
    mHousekeeper.stop();
    ThreadPool.removeSchedule(mHousekeeper);
  }
  
  /**
   * Start the house keeper task
   */
  private void startHouseKeeper()
  {
    if ((mConfig.isEnabled()) && (mConfig.isHousekeeperEnabled()))
    {
      ThreadPool.scheduleAtFixedRate(mHousekeeper, 0L, mConfig.getHousekeeperInterval(), TimeUnit.MILLISECONDS);
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
      .append(pad).append("  Version=(").append(StringUtils.asPrintableString(mVersion)).append(")\n")
      .append(pad).append("  Config=(").append(StringUtils.asPrintableString(mConfig)).append(")\n")
      .append(pad).append("  ShutdownHook=(").append(StringUtils.asPrintableString(mShutdownHook)).append(")\n")
      .append(pad).append("  Controller=(").append(StringUtils.asPrintableString(mController)).append(")\n")
      .append(pad).append("  Repository=(").append(StringUtils.asPrintableString(mRepository)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
