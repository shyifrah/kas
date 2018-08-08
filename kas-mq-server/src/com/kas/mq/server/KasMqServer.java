package com.kas.mq.server;

import com.kas.config.MainConfiguration;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IRunnable;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * MQ server
 * 
 * @author Pippo
 */
public class KasMqServer extends AKasObject implements IInitializable, IRunnable
{
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqServer.class.getSimpleName());
  
  /**
   * KAS/MQ server home directory
   */
  private String mHomeDirectory = null;
  
  /**
   * The main configuration object
   */
  private MainConfiguration mConfig = MainConfiguration.getInstance();
  
  /**
   * Logger
   */
  private ILogger mLogger = null;
  
  /**
   * Construct MQ server, specifying the home directory
   * 
   * @param home The home directory of KAS/MQ server
   */
  KasMqServer(String home)
  {
    mHomeDirectory = home;
  }
  
  /**
   * Initializing the KAS/MQ server.<br>
   * <br>
   * 
   */
  public boolean init()
  {
    mConfig = MainConfiguration.getInstance();
    boolean init = mConfig.init();
    if (!init)
      return false;
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    sStartupLogger.info("KAS/MQ server initialization complete. Logger is active");
    mLogger.info("KAS/MQ server initialization complete. Logger is active");
    return true;
  }
  
  public boolean term()
  {
    mLogger.info("KAS/MQ server termination in progress");
    mConfig.term();
    return true;
  }
  
  public void run()
  {
    mLogger.info("KAS/MQ server started...");
    
    for (int i = 0; i < 100; ++i)
    {
      mLogger.info("Doing some stuff...");
      RunTimeUtils.sleepForSeconds(3);
    }
  }
  
  public KasMqServer replicate()
  {
    return null;
  }

  public String toPrintableString(int level)
  {
    return null;
  }
}
