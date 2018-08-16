package com.kas.mq.appl;

import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.AKasMqAppl;

/**
 * MQ administration CLI.
 * 
 * @author Pippo
 */
public class KasMqAdmin extends AKasMqAppl 
{
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqAdmin.class.getName());
  
  /**
   * Initializing the KAS/MQ admin CLI.<br>
   * <br>
   * Initialization consisting of:
   * - super class initialization
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    boolean init = super.init();
    if (init)
    {
      mLogger.info("KAS/MQ base application initialized successfully");
    }
    
    String message = "KAS/MQ admin CLI V" + mVersion.toString() + (init ? " started successfully" : " failed to start");
    sStartupLogger.info(message);
    mLogger.info(message);
    return init;
  }
  
  /**
   * Terminating the KAS/MQ admin CLI.<br>
   * <br>
   * Termination consisting of:
   * - super class termination
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean term()
  {
    mLogger.info("KAS/MQ admin CLI termination in progress");
    boolean term = true;
    
    // ============>>>>  actual admin CLI termination goes here  <<<<==============
    
    term = super.term();
    return term;
  }
  
  /**
   * Run KAS/MQ admin CLI.<br>
   * <br>
   * The main logic is quite simple: keep reading commands from the command line until
   * it is terminated via the "exit" or SIGTERM signal.
   */
  public void run()
  {
    MqAdminProcessor processor = new MqAdminProcessor();
    processor.run();
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
