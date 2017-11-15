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

public class ClientHandler extends AKasObject implements Runnable
{
  private static ILogger sLogger = LoggerFactory.getLogger(ClientHandler.class);
  
  /***************************************************************************************************************
   * 
   */
  private IMessenger       mMessenger;
  private IHandlerCallback mCallback;
  private KasqRepository   mRepository;
  private boolean          mAdminHandler;
  
  /***************************************************************************************************************
   * Constructs a {@code ClientHandler} object, specifying the socket and start/stop callback.
   * 
   * @param socket the client socket
   * @param callback a callback that is invoked upon {@code ClientHandler} start and stop.
   * 
   * @throws IOException if for some reason we fail to wrap the client socket with a {@code Messenger} object 
   */
  public ClientHandler(Socket socket, IHandlerCallback callback) throws IOException
  {
    mMessenger  = MessengerFactory.create(socket, new KasqMessageFactory());
    mCallback   = callback;
    mRepository = KasqServer.getInstance().getRepository();
    mAdminHandler = false;
    
    if (mCallback != null) mCallback.onHandlerStart(this);
  }
  
  /***************************************************************************************************************
   * Gets the {@code ClientHandler}'s {@code Messenger}
   * 
   * @return this handler's {@code Messenger} 
   */
  public IMessenger getMessenger()
  {
    return mMessenger;
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
      boolean auth = authenticate();
      if (!auth)
      {
        sLogger.trace("Client failed authentication...");
      }
      else
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
    
    if (mCallback != null) mCallback.onHandlerStop(this);
    sLogger.debug("ClientHandler::run() - OUT");
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
    sLogger.debug("ClientHandler::authenticate() - IN");
    boolean authenticated = false;
    
    IPacket packet = mMessenger.receive();
    sLogger.debug("ClientHandler::authenticate() - Got a message: " + packet.toPrintableString(0));
    
    if (packet.getPacketClassId() == PacketHeader.cClassIdKasq)
    {
      IKasqMessage request = (IKasqMessage)packet;
      if (request.getIntProperty(IKasqConstants.cPropertyRequestType) == IKasqConstants.cPropertyRequestType_Authenticate)
      {
        String userName = null, password = null;
        try
        {
          userName = request.getStringProperty(IKasqConstants.cPropertyUserName);
          password = request.getStringProperty(IKasqConstants.cPropertyPassword);
          mAdminHandler = request.getBooleanProperty(IKasqConstants.cPropertyAdminMessage);
        }
        catch (Throwable e) {}
        
        IKasqMessage response = new KasqMessage();
        response.setJMSCorrelationID(request.getJMSMessageID());
        
        String msg = "";
        int   code;
        if ((userName != null) && (userName.length() > 0) && (password != null))
        {
          //
          // TODO: address some security manager and find out if the credentials are okay
          //
          authenticated = true;
          code = IKasqConstants.cPropertyResponseCode_Okay; 
        }
        else
        {
          code = IKasqConstants.cPropertyResponseCode_Fail;
          msg  = "User name or password are incorrect";
        }
        
        response.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
        response.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
        sLogger.debug("ClientHandler::authenticate() - Send message: " + response.toPrintableString(0));
        mMessenger.send(response);
      }
    }
    
    sLogger.debug("ClientHandler::authenticate() - OUT, Result=" + authenticated);
    return authenticated;
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
   */
  private void process(IPacket packet) throws JMSException
  {
    sLogger.debug("ClientHandler::process() - IN");
    
    if (packet.getPacketClassId() == PacketHeader.cClassIdKasq)
    {
      IKasqMessage request = (IKasqMessage)packet;
      int requestType = 0;
      try
      {
        requestType = request.getIntProperty(IKasqConstants.cPropertyRequestType);
      }
      catch (Throwable e) {}
      
      if (requestType == IKasqConstants.cPropertyRequestType_Shutdown)
      {
        if (mAdminHandler)
        {
          mCallback.onShutdownRequest();
        }
        else
        {
          sLogger.warn("Received shutdown request from non-authorized client. Ignoring...");
        }
      }
      else
      {
        IKasqDestination kqDestination;
        if (!(request.getJMSDestination() instanceof IKasqDestination))
        {
          sLogger.debug("ClientHandler::process() - message destination is NOT managed by KAS/Q. send to deadq");
          kqDestination = mRepository.getDeadQueue();
        }
        else
        {
          kqDestination = (IKasqDestination)request.getJMSDestination();
        }
        
        put(kqDestination, request);
      }
    }
    
    sLogger.debug("ClientHandler::process() - OUT");
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
    sLogger.debug("ClientHandler::process() - message destination is managed by KAS/Q. Name=[" + destination.getFormattedName() + "]");
    
    String destinationName = destination.getName();
    IKasqDestination destinationFromRepo = mRepository.locate(destinationName);
    if (destinationFromRepo != null)
    {
      destinationFromRepo.put(message);
    }
    else
    {
      sLogger.debug("ClientHandler::process() - Destination is not defined, define it now...");
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
        sLogger.warn("Destination " + destinationName + " failed definition; message sent to deadq");
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

