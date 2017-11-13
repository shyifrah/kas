package com.kas.q.server.internal;

import java.io.IOException;
import java.net.Socket;
import javax.jms.JMSException;
import com.kas.comm.IMessage;
import com.kas.comm.impl.MessageType;
import com.kas.comm.impl.Messenger;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.messages.AuthenticateRequestMessage;
import com.kas.comm.messages.ResponseMessage;
import com.kas.infra.base.KasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.server.KasqRepository;
import com.kas.q.server.KasqServer;

public class ClientHandler extends KasObject implements Runnable
{
  /***************************************************************************************************************
   * 
   */
  private ILogger          mLogger;
  private Messenger        mMessenger;
  private IHandlerCallback mCallback;
  private KasqRepository   mRepository;
  
  /***************************************************************************************************************
   * Constructs a {@code ClientHandler} object, specifying the socket and start/stop callback.
   * 
   * @param socket the client socket
   * @param callback a callback that is invoked upon {@code ClientHandler} start and stop.
   * 
   * @throws IOException if for some reason we fail to wrap the client socket with a {@code Messenger} object 
   */
  ClientHandler(Socket socket, IHandlerCallback callback) throws IOException
  {
    mLogger     = LoggerFactory.getLogger(this.getClass());
    mMessenger  = MessengerFactory.create(socket);
    mCallback   = callback;
    mRepository = KasqServer.getInstance().getRepository();
    
    if (mCallback != null) mCallback.onHandlerStart(this);
  }
  
  /***************************************************************************************************************
   * Main function of {@code ClientHandler}.
   * It consisting of a while-loop in which the {@code ClientHandler} awaits for new messages from the client
   * until the communication is broken.
   * Each received message is then processed by calling the {@code process(IMessage)} method.
   */
  public void run()
  {
    mLogger.debug("ClientHandler::run() - IN");
    mLogger.trace("Handling client connection from: " + mMessenger.toPrintableString(0));
    
    try
    {
      boolean auth = authenticate();
      if (!auth)
      {
        mLogger.trace("Client failed authentication...");
      }
      else
      {
        mLogger.trace("Awaiting client to send messages..");
        
        IMessage message = mMessenger.receive();
        while (message != null)
        {
          mLogger.trace("Received from client message: " + message.toPrintableString(0));
          process(message);
        
          message = mMessenger.receive();
        }
      }
    }
    catch (IOException e)
    {
      mLogger.trace("I/O Exception caught. Message: " + e.getMessage());
    }
    catch (Throwable e)
    {
      mLogger.trace("Exception caught while trying to process message from client. ", e);
    }
    mMessenger.cleanup();
    
    if (mCallback != null) mCallback.onHandlerStop(this);
    mLogger.debug("ClientHandler::run() - OUT");
  }
  
  /***************************************************************************************************************
   * Authenticate client.
   *  
   * @return true if client authenticated successfully, false otherwise
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  private boolean authenticate() throws JMSException, ClassNotFoundException, IOException
  {
    mLogger.debug("ClientHandler::authenticate() - IN");
    boolean authenticated = false;
    
    IMessage message = mMessenger.receive();
    mLogger.debug("ClientHandler::authenticate() - Got a message: " + message.toPrintableString(0));
    
    if (message.getMessageType() == MessageType.cAuthenticateRequestMessage)
    {
      AuthenticateRequestMessage request = (AuthenticateRequestMessage)message;
      String userName = request.getUserName();
      String password = request.getPassword();
      
      String error = null;
      ResponseMessage response;
      if ((userName != null) && (userName.length() > 0) && (password != null))
      {
        //
        // TODO: address some security manager and find out if the credentials are okay
        //
        authenticated = true;
        response = ResponseMessage.generateSuccessResponse(request);
      }
      else
      {
        error = "User name or password are incorrect";
        response = ResponseMessage.generateFailureResponse(request, error);
      }
      
      mLogger.debug("ClientHandler::authenticate() - Send message: " + response.toPrintableString(0));
      mMessenger.send(response);
    }
    
    mLogger.debug("ClientHandler::authenticate() - OUT, Result=" + authenticated);
    return authenticated;
  }
  
  /***************************************************************************************************************
   * Process an incoming {@code IMessage}.
   * 
   * Just like every {@link javax.jms.Message} object, the {@code IMessage} object carries a {@code Destination}
   * object in the JMSDestination header.
   * If this object is <b>not</b> an instance of {@code IDestination}, it means it is managed by some other provider,
   * and we have nothing to do with this message, so we send it to our Dead queue.
   * If this object is an instance of {@code IDestination}, then we try to locate it in our repository. If it was
   * located, we put the message in that destination. If we didn't locate it, we define it and then put it there. 
   * 
   * @param message the {@code IMessage} to be processed.
   * 
   * @throws JMSException if for some reason we fail to get the Destination from the message. 
   */
  private void process(IMessage message) throws JMSException
  {
    mLogger.debug("ClientHandler::process() - IN");
    
    if (message.getMessageType() == MessageType.cDataMessage)
    {
      IKasqMessage kqMessage = (IKasqMessage)message;
      IKasqDestination kqDestination;
      if (!(kqMessage.getJMSDestination() instanceof IKasqDestination))
      {
        mLogger.debug("ClientHandler::process() - message destination is NOT managed by KAS/Q. send to deadq");
        kqDestination = mRepository.getDeadQueue();
      }
      else
      {
        kqDestination = (IKasqDestination)kqMessage.getJMSDestination();
      }
      
      put(kqDestination, kqMessage);
    }
    
    mLogger.debug("ClientHandler::process() - OUT");
  }
  
  /***************************************************************************************************************
   * Put a {@code IKasqMessagee} into a {@code IKasqDestination}
   * 
   * @param destination message target
   * @param message message to put
   * 
   * @throws JMSException if for some reason we fail to get the Destination from the message. 
   */
  private void put(IKasqDestination destination, IKasqMessage message)
  {
    mLogger.debug("ClientHandler::process() - message destination is managed by KAS/Q. Name=[" + destination.getFormattedName() + "]");
    
    String destinationName = destination.getName();
    IKasqDestination destinationFromRepo = mRepository.locate(destinationName);
    if (destinationFromRepo != null)
    {
      destinationFromRepo.put(message);
    }
    else
    {
      mLogger.debug("ClientHandler::process() - Destination is not defined, define it now...");
      boolean defined = mRepository.defineQueue(destinationName);
      if (defined)
      {
        destinationFromRepo = mRepository.locate(destinationName);
        destinationFromRepo.put(message);
      }
      else
      {
        IKasqDestination deadq = mRepository.getDeadQueue();
        deadq.put(message);
        mLogger.warn("Destination " + destinationName + " failed definition; message sent to deadq");
      }
    }
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Messenger=(").append(mMessenger.toPrintableString(level + 1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}

