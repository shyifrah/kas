package com.kas.q.ext;

import java.io.IOException;
import java.util.Map;
import javax.jms.JMSException;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.threads.AKasThread;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqQueue;

public class KasqReceiverTask extends AKasThread
{
  /***************************************************************************************************************
   * 
   */
  private ILogger    mLogger;
  private IMessenger mMessenger;
  private Map<String, KasqQueue> mTempQueues;
  
  /***************************************************************************************************************
   * Construct a {@code KasqReceiverTask} object, using the specified {@code IMessenger} and temporary queues map.
   * 
   * @param messenger the {@code KasqConnection}'s Messenger
   * @param tempQueuesMap this map contains all {@code MessageConsumer}'s queues
   */
  public KasqReceiverTask(IMessenger messenger, Map<String, KasqQueue> tempQueuesMap)
  {
    super(KasqReceiverTask.class.getSimpleName());
    mLogger     = LoggerFactory.getLogger(this.getClass());
    mMessenger  = messenger;
    mTempQueues = tempQueuesMap;
  }
  
  /***************************************************************************************************************
   * Main receiver task loop.
   * The receiver task runs as long as it wasn't interrupted for some reason. It reads messages from
   * the connection's messenger and place them in the appropriate consumer queues (temporary queues which
   * are created upon {@code MessageConsumer} creation.
   */
  public void run()
  {
    mLogger.debug("KasqReceiverTask::run() - IN");
    
    boolean shouldStop = false;
    while (!shouldStop)
    {
      try
      {
        IPacket packet = mMessenger.receive();
        if (packet.getPacketClassId() != PacketHeader.cClassIdKasq)
        {
          mLogger.debug("KasqReceiverTask::run() - Got a non-KAS/Q packet. Ignoring it...");
        }
        else
        {
          IKasqMessage message = (IKasqMessage)packet;
          String tempq = null;
          Integer rc = null;
          String requestId = null;
          try
          {
            rc = message.getIntProperty(IKasqConstants.cPropertyResponseCode);
            tempq = message.getStringProperty(IKasqConstants.cPropertyConsumerQueue);
            requestId = message.getJMSCorrelationID();
          }
          catch (JMSException e) {}
          
          if (rc == null)
          {
            mLogger.debug("KasqReceiverTask::run() - Got a message without response code. Ignoring it...");
          }
          else
          if (rc == IKasqConstants.cPropertyResponseCode_Fail)
          {
            mLogger.debug("KasqReceiverTask::run() - Request with ID=" + requestId + " ended with failure. Continuing...");
          }
          else
          if (tempq == null)
          {
            mLogger.debug("KasqReceiverTask::run() - Got a message without origin queue. Ignoring it...");
          }
          else
          {
            mLogger.debug("KasqReceiverTask::run() - Got a message to be sent to origin: " + StringUtils.asString(tempq));
            KasqQueue q = mTempQueues.get(tempq);
            if (q == null)
              mLogger.debug("KasqReceiverTask::run() - Origin queue does not exist, or was deleted");
            else
              q.put(message);
          }
        }
      }
      catch (IOException e)
      {
        shouldStop = true;
      }
      catch (Throwable e)
      {
        shouldStop = true;
        mLogger.error("KasqReceiverTask caught exception: ", e);
      }
    }
    
    mLogger.debug("KasqReceiverTask::run() - OUT");
  }
}
