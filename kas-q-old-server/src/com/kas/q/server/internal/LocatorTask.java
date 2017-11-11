package com.kas.q.server.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import com.kas.infra.base.KasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.ILocator;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.MessageType;
import com.kas.q.ext.impl.MessageSerializer;
import com.kas.q.ext.impl.Messenger;
import com.kas.q.impl.messages.KasqTextMessage;

public class LocatorTask extends KasObject implements Runnable
{
  private static final String cLocateCommandPrefix = "locate:";
  private static final int    cQueueId = 1;
  private static final int    cTopicId = 2;
  
  private static final Map<String, Integer> cDestinationTypeSet = new HashMap<String, Integer>();
  
  static {
    cDestinationTypeSet.put("queue", cQueueId);
    cDestinationTypeSet.put("topic", cTopicId);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private ILogger          mLogger;
  //private ILocator         mLocator;
  private ServerSocket     mSocket;
  private boolean          mShouldStop;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  LocatorTask(ILocator locator, int port) throws IOException
  {
    mLogger  = LoggerFactory.getLogger(this.getClass());
    //mLocator = locator;
    mSocket  = new ServerSocket(port);
    mShouldStop = false;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void run()
  {
    mLogger.debug("LocatorTask::run() - IN");
    
    try
    {
      while (!mShouldStop)
      {
        Socket requestor = mSocket.accept();
        Messenger messenger = Messenger.Factory.create(requestor);
        
        handleRequest(messenger);
      }
    }
    catch (Throwable e)
    {
      mLogger.trace("Exception caught while trying to process locate requests. ", e);
    }
    
    mLogger.debug("LocatorTask::run() - OUT");
  }
  
  private void handleRequest(Messenger messenger) throws IOException, ClassNotFoundException, JMSException
  {
    mLogger.debug("LocatorTask::handleRequest() - IN");
    
    IMessage message = MessageSerializer.deserialize(messenger.getInputStream());
    if (message.getMessageType() == MessageType.cTextMessage)
    {
      KasqTextMessage textMessage = (KasqTextMessage)message;
      String text = textMessage.getText();
      
      // text is in the following format: "locate:<destination_type>:<name_of_destination>"
      if (text.startsWith(cLocateCommandPrefix))
      {
        int firstColonStart  = text.indexOf(':');
        int secondColonStart = text.indexOf(':', firstColonStart+1);
        if ((firstColonStart > 0) && (secondColonStart > 0))
        {
          String destinationType = text.substring(firstColonStart+1, secondColonStart);
          String destinationName = text.substring(secondColonStart+1);
          
          Integer id = cDestinationTypeSet.get(destinationType);
          if (id == null)
          {
            mLogger.warn("Request to locate an unknown destination type: " + destinationType);
          }
          else
          {
            mLogger.debug("LocatorTask::handleRequest() - Locating Type=[" + destinationType + "], Name=[" + destinationName + "]");
            /*Destination result = null;
            switch (id)
            {
              case cQueueId:
                result = mLocator.locateQueue(destinationName);
                break;
              case cTopicId:
                result = mLocator.locateTopic(destinationName);
                break;
            }*/
            
            // need to send the Destination object back to the client
            //IMessage response = new KasqObjectMessage(destination);
            //MessageSerializer.serialize(response);
          }
        }
      }
    }
    
    mLogger.debug("LocatorTask::handleRequest() - OUT");
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    return null;
  }
}

