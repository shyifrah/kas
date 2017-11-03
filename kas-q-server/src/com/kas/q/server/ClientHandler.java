package com.kas.q.server;

import java.io.IOException;
import java.net.Socket;
import com.kas.infra.base.KasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.impl.MessageSerializer;
import com.kas.q.ext.impl.Messenger;
import com.kas.q.server.internal.IHandlerCallback;

public class ClientHandler extends KasObject implements Runnable
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private ILogger          mLogger;
  private Messenger        mMessenger;
  private IHandlerCallback mCallback;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  ClientHandler(Socket socket, IHandlerCallback callback) throws IOException
  {
    mLogger    = LoggerFactory.getLogger(this.getClass());
    mMessenger = Messenger.Factory.create(socket);
    mCallback  = callback;
    
    if (mCallback != null) mCallback.onHandlerStart(this);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  //IMessenger getMessenger()
  //{
  //  return mMessenger;
  //}
  //
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void run()
  {
    mLogger.trace("Handling client connection from: " + mMessenger.toPrintableString(0));
    
    try
    {
      mLogger.trace("Awaiting client to send messages..");
      IMessage message = MessageSerializer.deserialize(mMessenger.getInputStream());
      while (message != null)
      {
        mLogger.trace("Received from client message: " + message.toPrintableString(0));
        //MessageProcessor.enqueue(message);
        
        message = null;
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
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    return null;
  }
}

