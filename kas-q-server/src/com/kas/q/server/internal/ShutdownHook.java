package com.kas.q.server.internal;

import java.net.ServerSocket;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class ShutdownHook extends Thread
{
  private ILogger      mLogger = null;
  private ServerSocket mServerSocket = null;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public ShutdownHook(ServerSocket socket)
  {
    mServerSocket = socket;
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void run()
  {
    try
    {
      mLogger.info("Signal main thread to shutdown...");
      mServerSocket.close();
    }
    catch (Throwable e) {}
  }
}
