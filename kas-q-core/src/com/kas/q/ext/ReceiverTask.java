package com.kas.q.ext;

import javax.jms.JMSException;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.threads.KasThreads;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqConnection;
import com.kas.q.KasqSession;

public class ReceiverTask extends KasThreads.AThread
{
  private KasqConnection mConnection;
  private ILogger        mLogger;
  private IMessenger     mMessenger;
  
  /***************************************************************************************************************
   * Construct a {@code ReceiverTask} for a specific {@code KasqConnection}
   * 
   * @throws JMSException if a problem occurred while extracting connection's clientID.
   */
  public ReceiverTask(IMessenger messenger, KasqConnection conn) throws JMSException
  {
    super(conn.getClientID() + "-ReceiverTask");
    mLogger     = LoggerFactory.getLogger(this.getClass());
    mConnection = conn;
    mMessenger  = messenger;
  }
  
  /***************************************************************************************************************
   * Connection's message receiver processing:
   * 
   * The {@code MessageConsumer} is waiting for a message on a specific queue that was created upon its creation.
   * We get the consumer queue name and the session which is responsible for this consumer and put the message
   * into this queue.
   */
  public void run()
  {
    mLogger.debug("ConnectionReceiverTask::run() - IN");
    
    boolean interrupted = false;
    while ((mConnection.isStarted()) && (!interrupted))
    {
      try
      {
        // get next packet. if it's not a valid KAS/Q message, reiterate 
        IPacket packet = mMessenger.receive();
        int classId = packet.getPacketClassId();
        if (classId != PacketHeader.cClassIdKasq)
        {
          mLogger.warn("Connection receiver task got a message with an unknown classId: " + classId + ". Ignoring...");
          continue;
        }
        
        // get the origin of the request. if it's invalid, reiterate
        IKasqMessage message = (IKasqMessage)packet;
        String queueName = null;
        String sessId    = null;
        try
        {
          queueName = message.getStringProperty(IKasqConstants.cPropertyConsumerQueue);
          sessId = message.getStringProperty(IKasqConstants.cPropertyConsumerSession);
        }
        catch (Throwable e) {}
        
        if ((queueName == null) || (queueName.length() == 0) || (sessId == null))
        {
          mLogger.debug("Invalid origin: Session=[" + StringUtils.asString(sessId) + "], Queue=[" + StringUtils.asString(queueName) + "]");
          mLogger.warn("Message has invalid origin session or queue, ignoring...");
          continue;
        }
        
        // locate the consumer's session. if we fail to do so, reiterate
        KasqSession sess = mConnection.getOpenSession(sessId);
        if (sess == null)
        {
          mLogger.warn("Could not locate session with Id=" + sessId);
          continue;
        }
        
        // get the consumer's queue. if we fail to do so, reiterate
        IKasqDestination dest = sess.getOpenQueue(queueName);
        if (dest == null)
        {
          mLogger.warn("Could not locate destination with name=" + queueName);
        }
        else
        {
          dest.put(message);
        }
      }
      catch (Throwable e)
      {
        mLogger.error("Connection receiver task encountered a problem. Skipping message processing");
        mLogger.error("Exception ", e);
      }
    }
    
    mLogger.debug("ConnectionReceiverTask::run() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return name();
  }  
}
