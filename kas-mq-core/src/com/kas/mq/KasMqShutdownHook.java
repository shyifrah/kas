package com.kas.mq;

import com.kas.infra.base.threads.AKasThread;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * The {@link KasMqShutdownHook} is a thread that kicks in the minute a shutdown signal is sent to the application.<br>
 * 
 * This is actually a shutdown hook that is registered via a call to {@link Runtime#addShutdownHook(Thread)}.
 * 
 * @author Pippo
 * 
 * @see java.lang.Runtime#addShutdownHook(Thread)
 */
public class KasMqShutdownHook extends AKasThread
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * The MQ application that should be stopped
   */
  private AKasMqAppl mApplication;
  
  /**
   * Construct KasMqStopper
   * 
   * @param appl The MQ application
   */
  public KasMqShutdownHook(AKasMqAppl appl)
  {
    super(KasMqShutdownHook.class.getSimpleName());
    mLogger = LoggerFactory.getLogger(this.getClass());
    mApplication = appl;
  }
  
  /**
   * Running the shutdown hook.<br>
   * <br>
   * The procedure is actually quite simple - invoking the application's {@link AKasMqAppl#term()} method.
   * 
   * @see com.kas.mq.server.AKasMqAppl#term()
   * @see com.kas.infra.base.IInitializable#term()
   */
  public void run()
  {
    mLogger.warn("Shutdown hook was called. Terminating application...");
    mApplication.stop();
    mApplication.term();
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
      .append(pad).append("  Name=").append(getName()).append("\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
