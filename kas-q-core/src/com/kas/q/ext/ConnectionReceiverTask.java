package com.kas.q.ext;

import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqConnection;
import com.kas.q.KasqSession;

public class ConnectionReceiverTask implements Runnable
{
  private KasqConnection mOwner;
  private ILogger        mLogger;
  private IMessenger     mMessenger;
  
  public ConnectionReceiverTask(IMessenger messenger, KasqConnection conn)
  {
    mLogger    = LoggerFactory.getLogger(this.getClass());
    mOwner     = conn;
    mMessenger = messenger;
  }
  
  public void run()
  {
    mLogger.debug("ConnectionReceiverTask::run() - IN");
    
    while (mOwner.isStarted())
    {
      try
      {
        IPacket packet = mMessenger.receive();
        int classId = packet.getPacketClassId();
        if (classId != PacketHeader.cClassIdKasq)
        {
          mLogger.warn("Connection receiver task got a message with an unknown classId: " + classId + ". Ignoring...");
        }
        else
        {
          IKasqMessage message = (IKasqMessage)packet;
          
          String queueName = null;
          UniqueId sessId  = null;
          try
          {
            queueName = message.getStringProperty(IKasqConstants.cPropertyConsumerQueue);
            String session = message.getStringProperty(IKasqConstants.cPropertyConsumerSession);
            sessId = UniqueId.fromString(session);
          }
          catch (Throwable e) {}
          
          if ((queueName == null) || (queueName.length() == 0) || (sessId == null))
          {
            mLogger.debug("Invalid origin: Session=[" + StringUtils.asString(sessId) + "], Queue=[" + StringUtils.asString(queueName) + "]");
            mLogger.warn("Message has invalid origin session or queue, ignoring...");
          }
          else
          {
            KasqSession sess = mOwner.getOpenSession(sessId);
            if (sess == null)
            {
              mLogger.warn("Could not locate session with Id=" + sessId);
            }
            else
            {
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
          }
        }
      }
      catch (Throwable e)
      {
        mLogger.warn("Connection receiver task encountered a problem: ", e);
      }
    }
    
    mLogger.debug("ConnectionReceiverTask::run() - OUT");
  }
}
