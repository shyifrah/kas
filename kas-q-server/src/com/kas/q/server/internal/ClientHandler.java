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
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.KasqMessageFactory;
import com.kas.q.server.KasqRepository;
import com.kas.q.server.KasqServer;
import com.kas.q.server.req.AuthenticateRequest;
import com.kas.q.server.req.GetRequest;
import com.kas.q.server.req.IRequest;
import com.kas.q.server.req.PutRequest;
import com.kas.q.server.req.RequestFactory;
import com.kas.q.server.req.ShutdownRequest;

public class ClientHandler extends AKasObject implements Runnable
{
  private static ILogger sLogger = LoggerFactory.getLogger(ClientHandler.class);
  
  /***************************************************************************************************************
   * 
   */
  private IMessenger       mMessenger;
  private IController      mController;
  private KasqRepository   mRepository;
  private boolean          mAuthenticated;
  //private boolean          mAdminHandler;
  
  /***************************************************************************************************************
   * Constructs a {@code ClientHandler} object, specifying the socket and start/stop callback.
   * 
   * @param socket the client socket
   * @param controller a callback that is invoked upon {@code ClientHandler} start and stop.
   * 
   * @throws IOException if for some reason we fail to wrap the client socket with a {@code Messenger} object 
   */
  public ClientHandler(Socket socket, IController controller) throws IOException
  {
    mMessenger  = MessengerFactory.create(socket, new KasqMessageFactory());
    mController = controller;
    mRepository = KasqServer.getInstance().getRepository();
    mAuthenticated = false;
    //mAdminHandler = false;
    
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
    sLogger.trace("Handling client connection from: " + mMessenger.toPrintableString(0));
    
    try
    {
      sLogger.trace("Awaiting client to send messages..");
      
      IPacket message = mMessenger.receive();
      while (message != null)
      {
        sLogger.trace("Received from client message: " + message.toPrintableString(0));
        process(message);
      
        message = mMessenger.receive();
      }
    }
    catch (IOException e)
    {
      sLogger.trace("I/O Exception caught. Message: " + e.getMessage());
    }
    catch (Throwable e)
    {
      sLogger.trace("Exception caught while trying to process message from client. ", e);
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
   * @param message the {@code IPacket} to be processed.
   * 
   * @throws JMSException if for some reason we fail to get the Destination from the message. 
   * @throws IOException if the Messenger throws an exception 
   */
  private void process(IPacket packet) throws JMSException, IOException
  {
    sLogger.debug("ClientHandler::process() - IN");
    
    if (packet.getPacketClassId() != PacketHeader.cClassIdKasq)
    {
      sLogger.warn("Unknown packet class ID=" + packet.getPacketClassId() + ", Ignoring message");
    }
    else
    {
      IKasqMessage requestMessage = (IKasqMessage)packet;
      IRequest request = RequestFactory.createRequest(requestMessage);
      
      switch (request.getRequestType())
      {
        case cShutdown:
          if (mAuthenticated)
            shutdown((ShutdownRequest)request);
          break;
        case cAuthenticate:
          mAuthenticated = authenticate((AuthenticateRequest)request);
          break;
        case cGet:
          if (mAuthenticated)
            get((GetRequest)request);
          break;
        case cPut:
          if (mAuthenticated)
            put((PutRequest)request);
          break;
      }
    }
    
    sLogger.debug("ClientHandler::process() - OUT");
  }
  
  /***************************************************************************************************************
   * Process a shutdown request
   * 
   * If the current {@code ClientHandler} is an administrative handler, we call the controller to shutdown
   * the KAS/Q server. Otherwise, this is an un-authorized request and we deny it. 
   */
  private void shutdown(ShutdownRequest request)
  {
    sLogger.debug("ClientHandler::shutdown() - IN");
    
    if (request.isAdmin())
    {
      mController.onShutdownRequest();
    }
    else
    {
      sLogger.warn("Received shutdown request from non-authorized client. Ignoring...");
    }
    
    sLogger.debug("ClientHandler::shutdown() - OUT");
  }
  
  /***************************************************************************************************************
   * Authenticate client
   *  
   * @return true if client authenticated successfully, false otherwise
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  private boolean authenticate(AuthenticateRequest request) throws JMSException, IOException
  {
    sLogger.debug("ClientHandler::authenticate() - IN");
    boolean authenticated = false;
    
    //String userName = request.getUserName();
    //String password = request.getPassword();
    // TODO: address some security manager and find out if the credentials are okay
    //       if they are okay, set authenticated to "true" and code to "Fail"
    //
    authenticated = true;
    String msg = "";
    int code = IKasqConstants.cPropertyResponseCode_Okay;
    
    IKasqMessage response = new KasqMessage();
    response.setJMSCorrelationID(request.getJmsMessageId());
    response.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
    response.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
    
    sLogger.debug("ClientHandler::authenticate() - Send message: " + response.toPrintableString(0));
    mMessenger.send(response);
    
    sLogger.debug("ClientHandler::authenticate() - OUT, Result=" + authenticated);
    return authenticated;
  }
  
  /***************************************************************************************************************
   * Process a GetRequest
   * 
   * @param request the request message 
   */
  private void get(GetRequest request)
  {
    sLogger.debug("ClientHandler::get() - IN");
    
    //
    // TODO: use the following message criteria to select the consumed message
    //
    // get the filtering criteria
    //String selector = "";
    //boolean noLocal = false;
    //try
    //{
    //  selector = request.getStringProperty(IKasqConstants.cPropertyMessageSelector);
    //  noLocal = request.getBooleanProperty(IKasqConstants.cPropertyNoLocal);
    //}
    //catch (Throwable e) {}
    
    IKasqMessage message;
    
    // now we address the repository and locate the destination
    if (request.getDestinationType() == IKasqConstants.cPropertyDestinationType_Queue)
    {
      IKasqDestination dest = mRepository.locateQueue(request.getDestinationName());
      message = dest.getNoWait();
    }
    else
    {
      IKasqDestination dest = mRepository.locateTopic(request.getDestinationName());
      message = dest.getNoWait();
    }
    
    if (message == null)
    {
      deferRequest(request);
    }
    else
    {
      // set consumer location
      try
      {
        message.setStringProperty(IKasqConstants.cPropertyConsumerQueue, request.getOriginQueueName());
        message.setStringProperty(IKasqConstants.cPropertyConsumerSession, request.getOriginSessionId());
      }
      catch (Throwable e) {}
      
      try
      {
        sLogger.debug("ClientHandler::get() - Sending to origin consumed message: " + message.toPrintableString(0));
        mMessenger.send(message);
      }
      catch (Throwable e)
      {
        sLogger.warn("Failed to send message " + message.toString() + " to consumer at session: " + request.getOriginSessionId() + ". Exception: ", e);
      }
    }
  
    sLogger.debug("ClientHandler::get() - OUT");
  }
  
  /***************************************************************************************************************
   * Put a {@code IKasqMessagee} into a {@code IKasqDestination}
   * 
   * @param destination message target
   * @param message message to put
   * 
   * @throws JMSException if for some reason we fail to get the Destination from the message. 
   */
  private void put(PutRequest request)
  {
    sLogger.debug("ClientHandler::put() - IN");
    
    IKasqDestination kqDestination = request.getDestination();
    IKasqMessage     kqMessage     = request.getMessage();

    sLogger.debug("ClientHandler::put() - message destination is managed by KAS/Q. Name=[" + kqDestination.getFormattedName() + "]");
    
    String destinationName = kqDestination.getName();
    IKasqDestination destinationFromRepo = mRepository.locate(destinationName);
    if (destinationFromRepo != null)
    {
      destinationFromRepo.put(kqMessage);
    }
    else
    {
      sLogger.debug("ClientHandler::put() - Destination is not defined, define it now...");
      boolean defined = mRepository.defineQueue(destinationName);
      if (defined)
      {
        destinationFromRepo = mRepository.locate(destinationName);
        destinationFromRepo.put(kqMessage);
      }
      else
      {
        IKasqDestination deadq = mRepository.getDeadQueue();
        deadq.put(kqMessage);
        sLogger.warn("Destination " + destinationName + " failed definition; message sent to deadq");
      }
    }
    
    sLogger.debug("ClientHandler::put() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  private void deferRequest(GetRequest request)
  {
    
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Messenger=(").append(mMessenger.toPrintableString(level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}

