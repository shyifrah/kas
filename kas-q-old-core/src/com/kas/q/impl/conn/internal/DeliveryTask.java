package com.kas.q.impl.conn.internal;

import java.util.ArrayList;
import java.util.List;
import javax.jms.Destination;
import javax.jms.JMSException;
import com.kas.containers.CappedHashMap;
import com.kas.infra.base.KasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IDestination;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.impl.MessageSerializer;
import com.kas.q.ext.impl.Messenger;

public class DeliveryTask extends KasObject implements Runnable
{
  /***************************************************************************************************************
   * 
   */
  private ILogger     mLogger    = null;
  private Messenger   mMessenger = null;
  private CappedHashMap<String, List<IMessage>> mMessagesMap = null;
  
  /***************************************************************************************************************
   * Construct the {@code DeliveryTask}, passing it a {@code Messenger} object and a map.
   * 
   * @param messenger The messenger used for receiving messages
   * @param messagesMap A map of destination names to list of messages awaiting to be consumed 
   */
  public DeliveryTask(Messenger messenger, CappedHashMap<String, List<IMessage>> messagesMap)
  {
    mLogger      = LoggerFactory.getLogger(this.getClass());
    mMessenger   = messenger;
    mMessagesMap = messagesMap;
  }
  
  /***************************************************************************************************************
   * This method is used to receive messages from the KasqServer and deliver them. 
   */
  public void run()
  {
    mLogger.debug("DeliveryTask::run() - IN");
    
    try
    {
      IMessage message = MessageSerializer.deserialize(mMessenger.getInputStream());
      while (message != null)
      {
        deliver(message);
        message = MessageSerializer.deserialize(mMessenger.getInputStream());
      }
    }
    catch (Throwable e) {}
    
    mLogger.debug("DeliveryTask::run() - OUT");
  }
  
  /***************************************************************************************************************
   * Delivering messages
   * 
   * @param message the message to be delivered 
   */
  public void deliver(IMessage message) throws JMSException
  {
    mLogger.debug("DeliveryTask::deliver() - IN");
    
    Destination jmsDest = message.getJMSDestination();
    if (!(jmsDest instanceof IDestination))
    {
      mLogger.warn("Unknown destination [" + jmsDest.toString() + "]. Cannot deliver message: " + message.toPrintableString(0));
    }
    else
    {
      IDestination iDest = (IDestination)jmsDest;
      String destName = iDest.getName();
      
      synchronized (mMessagesMap)
      {
        List<IMessage> messageList = mMessagesMap.get(destName);
        if (messageList == null)
        {
          messageList = new ArrayList<IMessage>();
          mMessagesMap.put(destName, messageList);
        }
        messageList.add(message);
      }
    }
    
    mLogger.debug("DeliveryTask::deliver() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
