package com.kas.q.server.internal;

import java.io.IOException;
import java.net.Socket;
import javax.jms.JMSException;
import com.kas.comm.IPacket;
import com.kas.comm.IMessenger;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.KasqMessageFactory;
import com.kas.q.server.KasqServer;
import com.kas.q.server.req.AuthenticateRequest;
import com.kas.q.server.req.GetRequest;
import com.kas.q.server.req.IRequest;
import com.kas.q.server.req.PutRequest;
import com.kas.q.server.req.RequestFactory;
import com.kas.q.server.req.RequestProcessor;
import com.kas.q.server.req.ShutdownRequest;

public class ClientHandler extends AKasObject implements Runnable
{
  private static ILogger sLogger = LoggerFactory.getLogger(ClientHandler.class);
  
  /***************************************************************************************************************
   * 
   */
  private IMessenger      mMessenger;
  private IController     mController;
  private boolean         mAuthenticated;
  //private Queue<IRequest> mRequestQueue;
  
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
    //mRequestQueue = new ArrayDeque<IRequest>();
    
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
          IRequest request = RequestFactory.createRequest((IKasqMessage)packet);
          sLogger.debug("ClientHandler::run() - Got request " + request.toString() + " calling RequestPocessor");
          process(request);
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
   * Process an incoming {@code IPacket}.
   * 
   * Just like every {@link javax.jms.Message} object, the {@code IPacket} object carries a {@code Destination}
   * object in the JMSDestination header.
   * If this object is <b>not</b> an instance of {@code IDestination}, it means it is managed by some other provider,
   * and we have nothing to do with this message, so we send it to our Dead queue.
   * If this object is an instance of {@code IDestination}, then we try to locate it in our repository. If it was
   * located, we put the message in that destination. If we didn't locate it, we define it and then put it there. 
   * 
   * @param request the {@code IRequest} to be processed
   * 
   * @throws JMSException if for some reason we fail to get the Destination from the message. 
   * @throws IOException if the Messenger throws an exception 
   */
  private void process(IRequest request) throws JMSException, IOException
  {
    sLogger.debug("ClientHandler::process() - IN");
    
    switch (request.getRequestType())
    {
      case cShutdown:
        sLogger.debug("ClientHandler::process() - Processing a Shutdown request");
        if (mAuthenticated)
          RequestProcessor.handleShutdownRequest((ShutdownRequest)request);
        break;
      case cAuthenticate:
        sLogger.debug("ClientHandler::process() - Processing an Authentication request");
        mAuthenticated = RequestProcessor.handleAuthenticateRequest(this, (AuthenticateRequest)request);
        break;
      case cGet:
        sLogger.debug("ClientHandler::process() - Processing an Get request");
        if (mAuthenticated)
          RequestProcessor.handleGetRequest(this, (GetRequest)request);
        break;
      case cPut:
        sLogger.debug("ClientHandler::process() - Processing an Put request");
        if (mAuthenticated)
          RequestProcessor.handlePutRequest((PutRequest)request);
        break;
    }
    
    sLogger.debug("ClientHandler::process() - OUT");
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
  
  ///***************************************************************************************************************
  // * Deferring a request means simply means to put a request back to the queue
  // * 
  // * @param request the {@code IRequest} to be processed.
  // */
  //public void deferRequest(IRequest request)
  //{
  //  mRequestQueue.offer(request);
  //}
  //
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
      .append(pad).append("  Requests Queue=(\n")
    //  .append(StringUtils.asPrintableString(mRequestQueue, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}

