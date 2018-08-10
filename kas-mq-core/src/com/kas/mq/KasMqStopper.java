package com.kas.mq;

import com.kas.infra.base.IStoppable;
import com.kas.infra.base.threads.AKasThread;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * The {@link KasMqStopper} is a thread that kicks in the minute a shutdown signal is sent to the server.<br>
 * 
 * This is actually a shutdown hook that is registered via a call to {@link Runtime#addShutdownHook(Thread)}.
 * 
 * @author Pippo
 * 
 * @see java.lang.Runtime#addShutdownHook(Thread)
 */
public class KasMqStopper extends AKasThread
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * The MQ server that should be stopped
   */
  private IStoppable mServer;
  
  /**
   * Construct KasMqTerminator
   * 
   * @param server The owner MQ server
   */
  public KasMqStopper(IStoppable server)
  {
    super(KasMqStopper.class.getSimpleName());
    mLogger = LoggerFactory.getLogger(this.getClass());
    mServer = server;
  }
  
  /**
   * Running the shutdown hook.<br>
   * <br>
   * The procedure is actually quite simple - invoking the server's {@link KasMqServer#stop() stop()} method.
   * 
   * @see com.kas.mq.server.KasMqServer#stop()
   * @see com.kas.infra.base.IStoppable#stop()
   */
  public void run()
  {
    mLogger.warn("Shutdown hook was called. Signaling KAS/MQ server to shutdown...");
    mServer.stop();
  }
  
  /**
   * Returns a replica of this {@link KasMqStopper}.
   * 
   * @return a replica of this {@link KasMqStopper}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public KasMqStopper replicate()
  {
    return new KasMqStopper(mServer);
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
