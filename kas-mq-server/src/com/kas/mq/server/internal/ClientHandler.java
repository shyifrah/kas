package com.kas.mq.server.internal;

import java.net.Socket;
import com.kas.infra.base.AStoppable;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.RunTimeUtils;

public class ClientHandler extends AStoppable implements Runnable
{
  /**
   * The client's unique ID
   */
  private UniqueId mClientId;
  
  /**
   * The client socket
   */
  private Socket mSocket;
  
  /**
   * Construct a {@link ClientHandler} to handle all incoming and outgoing traffic of the client.<br>
   * <br>
   * Client's transmits messages and received by this handler over the specified {@code socket}.
   *  
   * @param socket The client's socket
   */
  ClientHandler(Socket socket)
  {
    mSocket = socket;
    mClientId = UniqueId.generate();
  }
  
  public void run()
  {
    boolean shouldStop = isStopping();
    while (!shouldStop)
    {
      RunTimeUtils.sleepForSeconds(5);
      
      // re-check if needs to shutdown
      shouldStop = isStopping();
    }
  }
  
  /**
   * Get the client unique ID
   * 
   * @return the client unique ID
   */
  public UniqueId getClientId()
  {
    return mClientId;
  }
  
  public AStoppable replicate()
  {
    return null;
  }

  public String toPrintableString(int level)
  {
    return null;
  }
}
