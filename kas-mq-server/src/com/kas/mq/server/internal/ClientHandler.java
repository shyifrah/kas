package com.kas.mq.server.internal;

import java.io.IOException;
import java.net.Socket;
import com.kas.infra.base.AStoppable;
import com.kas.infra.base.UniqueId;

/**
 * A {@link ClientHandler} is the object that handles the traffic in and from a remote client.
 * 
 * @author Pippo
 */
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
   * The client controller
   */
  private IController mController;
  
  /**
   * Construct a {@link ClientHandler} to handle all incoming and outgoing traffic of the client.<br>
   * <br>
   * Client's transmits messages and received by this handler over the specified {@code socket}.
   *  
   * @param socket The client's socket
   * 
   * @throws IOException if {@link Socket#setSoTimeout()} throws
   */
  ClientHandler(Socket socket, IController controller) throws IOException
  {
    mSocket = socket;
    mController = controller;
    mSocket.setSoTimeout(mController.getConfig().getConnSocketTimeout());
    mClientId = UniqueId.generate();
  }
  
  public void run()
  {
    boolean shouldStop = isStopping();
    while (!shouldStop)
    {
//      try
//      {
//        // do socket read call
//        // process the message
//      }
//      catch (SocketTimeoutException e)
//      {
//        // just notify on timeout and re-iterate
//      }
//      catch (IOException e)
//      {
//        
//      }
      
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
      .append(pad).append("  Client Id=").append(mClientId.toString()).append("\n")
      .append(pad).append("  Socket=").append(mSocket.toString()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
