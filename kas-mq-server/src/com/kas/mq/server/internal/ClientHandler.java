package com.kas.mq.server.internal;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * A {@link ClientHandler} is the object that handles the traffic in and from a remote client.
 * 
 * @author Pippo
 */
public class ClientHandler extends AKasObject implements Runnable
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
   * Messenger
   */
  private IMessenger mMessenger;
  
  /**
   * Logger
   */
  private ILogger mLogger;
  
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
    mController = controller;
    mSocket = socket;
    mSocket.setSoTimeout(mController.getConfig().getConnSocketTimeout());
    mMessenger = MessengerFactory.create(mSocket);
    
    mClientId = UniqueId.generate();
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  /**
   * Running the handler - keep reading and process packets from input stream.<br>
   * If necessary, respond on output stream.
   */
  public void run()
  {
    mLogger.debug("ClientHandler::run() - IN");
    
    boolean shouldStop = false;
    while (!shouldStop)
    {
      mLogger.debug("Waiting for messages from client...");
      try
      {
        IPacket packet = mMessenger.receive();
        if (packet != null)
        {
          mLogger.debug("Packet received: " + packet.toPrintableString(0));
          process(packet);
        }
      }
      catch (SocketTimeoutException e)
      {
        mLogger.debug("Socket Timeout occurred. Resume waiting for a new packet from client...");
      }
      catch (IOException e)
      {
        mLogger.error("An I/O error occurred while trying to receive packet from remote client. Exception: ", e);
        mLogger.error("Connection to remote host at " + new NetworkAddress(mSocket).toString() + " was dropped");
        shouldStop = true;
      }
    }
    
    mLogger.debug("ClientHandler::run() - OUT");
  }
  
  /**
   * Process received packet
   * 
   * @param packet The received packet
   */
  private void process(IPacket packet)
  {
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
