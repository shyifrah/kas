package com.kas.q.server.internal;

import java.io.IOException;
import java.net.Socket;
import com.kas.comm.IPacket;
import com.kas.comm.IMessenger;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.KasqMessageFactory;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.IController;
import com.kas.q.server.KasqServer;
import com.kas.q.server.req.ERequestType;
import com.kas.q.server.req.IRequestProcessor;
import com.kas.q.server.req.RequestFactory;

public class ClientHandler extends AKasObject implements IClientHandler
{
  private static ILogger sLogger = LoggerFactory.getLogger(ClientHandler.class);
  
  /***************************************************************************************************************
   * 
   */
  private IMessenger  mMessenger;
  private IController mController;
  private boolean     mAuthenticated;
  
  /***************************************************************************************************************
   * Constructs a {@code ClientHandler} object, specifying the socket and start/stop callback.
   * 
   * @param socket the client socket
   * @param controller a callback that is invoked upon {@code ClientHandler} start and stop.
   * 
   * @throws IOException if for some reason we fail to wrap the client socket with a {@code Messenger} object 
   */
  public ClientHandler(Socket socket) throws IOException
  {
    mMessenger  = MessengerFactory.create(socket, new KasqMessageFactory());
    mAuthenticated = false;
    mController = KasqServer.getInstance().getController();
    
    if (mController != null) mController.onHandlerStart(this);
  }
  
  /***************************************************************************************************************
   * Terminate this handler
   */
  public void term()
  {
    try
    {
      mMessenger.cleanup();
    }
    catch (Throwable e) {}
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean isAuthenticated()
  {
    return mAuthenticated;
  }
  
  /***************************************************************************************************************
   * Main function of {@code ClientHandler}.
   * It consisting of a while-loop in which the {@code ClientHandler} awaits for new messages from the client
   * until the communication is broken.
   * Each received message is then processed by calling the {@code process(IMessage)} method.
   */
  public void run()
  {
    sLogger.debug("ClientHandler::run() - IN");
    sLogger.trace("Handling client connection from: " + mMessenger.toString());
    
    try
    {
      sLogger.trace("Start processing client requests...");
      
      while (true)
      {
        sLogger.debug("ClientHandler::run() - Waiting for client packets...");
        IPacket packet = mMessenger.receive();
        if (packet == null)
        {
          sLogger.debug("ClientHandler::run() - Got a null packet, going back to wait for a different one...");
        }
        else
        if  (packet.getPacketClassId() != PacketHeader.cClassIdKasq)
        {
          sLogger.debug("ClientHandler::run() - Got a non-KAS/Q packet, ignoring it and going back to wait for a different one...");
        }
        else
        {
          IRequestProcessor request = RequestFactory.createRequest((IKasqMessage)packet);
          sLogger.debug("ClientHandler::run() - Got request: " + request.toString());
          
          boolean success = request.process(this);
          if (request.getRequestType() == ERequestType.cAuthenticate)
            mAuthenticated = success;
          
          sLogger.debug("ClientHandler::run() - Processing of request [" + request.toString() + "] ended " + (success ? "successfully" : "with an error"));
        }
      }
    }
    catch (IOException e)
    {
      sLogger.trace("I/O Exception caught. Message: " + e.getMessage());
    }
    catch (Throwable e)
    {
      sLogger.trace("Exception caught while trying to process message from client: ", e);
    }
    
    mMessenger.cleanup();
    
    if (mController != null) mController.onHandlerStop(this);
    sLogger.debug("ClientHandler::run() - OUT");
  }
  
  /***************************************************************************************************************
   * Send a message to client 
   * 
   * @param message the {@code IKasqMessage} to be processed.
   * 
   * @throws IOException if the Messenger throws an exception 
   */
  public void send(IKasqMessage message) throws IOException
  {
    mMessenger.send(message);
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toString()
  {
    return mMessenger.toString();
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Authenticated=(").append(mAuthenticated).append(")\n")
      .append(pad).append("  Messenger=(").append(mMessenger.toPrintableString(level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}

