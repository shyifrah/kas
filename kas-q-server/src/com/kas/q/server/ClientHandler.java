package com.kas.q.server;

import java.io.IOException;
import java.net.Socket;
import javax.jms.Destination;
import javax.jms.JMSException;
import com.kas.infra.base.KasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IDestination;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.impl.MessageSerializer;
import com.kas.q.ext.impl.Messenger;
import com.kas.q.server.internal.IHandlerCallback;

public class ClientHandler extends KasObject implements Runnable
{
  private ILogger                mLogger;
  private Messenger              mMessenger;
  private IHandlerCallback       mCallback;
  private KasqRepository         mRepository;
  
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
    mMessenger  = Messenger.Factory.create(socket);
    mCallback   = callback;
    mRepository = KasqServer.getInstance().getRepository();
    
    if (mCallback != null) mCallback.onHandlerStart(this);
  }
  
  public void run()
  {
    mLogger.debug("ClientHandler::run() - IN");
    mLogger.trace("Handling client connection from: " + mMessenger.toPrintableString(0));
    
    try
    {
      mLogger.trace("Awaiting client to send messages..");
      IMessage message = MessageSerializer.deserialize(mMessenger.getInputStream());
      while (message != null)
      {
        mLogger.trace("Received from client message: " + message.toPrintableString(0));
        process(message);
        
        message = MessageSerializer.deserialize(mMessenger.getInputStream());
      }
      
      mMessenger.cleanup();
    }
    catch (IOException e)
    {
      mLogger.trace("I/O Exception caught. Message: "+ e.getMessage());
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
    
    Destination jmsDestination = message.getJMSDestination();
    IDestination iDest;
    if (!(jmsDestination instanceof IDestination))
    {
      mLogger.debug("ClientHandler::process() - message destination is managed by external provider, sending to dead queue");
      iDest = mRepository.getDeadQueue();
      iDest.put(message);
    }
    else
    {
      iDest = (IDestination)jmsDestination;
      mLogger.debug("ClientHandler::process() - message destination is managed by KAS/Q. Name=[" + iDest.toDestinationName() + "]");
      
      String destName = iDest.getName();
      IDestination iDestFromRepo = mRepository.locate(destName);
      if (iDestFromRepo == null)
      {
        mLogger.debug("ClientHandler::process() - Destination is not defined, define it now...");
        boolean defined = mRepository.defineQueue(destName);
        if (defined)
        {
          iDestFromRepo = mRepository.locate(destName);
          iDestFromRepo.put(message);
        }
      }
    }
    
    mLogger.debug("ClientHandler::process() - OUT");
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    return null;
  }
}

