package com.kas.q.ext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.SocketTimeoutException;
import com.kas.comm.IPacket;
import com.kas.comm.IPacketFactory;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqBytesMessage;
import com.kas.q.KasqMapMessage;
import com.kas.q.KasqMessage;
import com.kas.q.KasqObjectMessage;
import com.kas.q.KasqStreamMessage;
import com.kas.q.KasqTextMessage;

public class KasqMessageFactory implements IPacketFactory
{
  private static ILogger sLogger = LoggerFactory.getLogger(KasqMessageFactory.class);
  
  /***************************************************************************************************************
   * Create a {@code KasqMessage} object from {@code ObjectInputStream}
   * 
   * First, we try to create a {@link PacketHeader} from the stream.
   * Then, if is's a valid header, we create the proper message.
   * 
   * @param istream the {@code ObjectInputStream} that will be deserialized 
   * 
   * @throws StreamCorruptedException if deserialization failed or IOException occur 
   */
  public IPacket createFromStream(ObjectInputStream istream) throws StreamCorruptedException, SocketTimeoutException
  {
    sLogger.debug("KasqMessageFactory::createFromStream() - IN");
    
    IPacket message = null;
    try
    {
      PacketHeader header = new PacketHeader(istream);
      if ((header != null) && (header.getClassId() == PacketHeader.cClassIdKasq))
      { 
        int type = header.getType();
        EMessageType messageType = EMessageType.fromInt(type);
        sLogger.debug("KasqMessageFactory::createFromStream() - MessageType=" + messageType.toString());
        
        switch (messageType)
        {
          case cKasqMessage:
            message = new KasqMessage(istream);
            break;
          case cKasqMessageText:
            message = new KasqTextMessage(istream);
            break;
          case cKasqMessageObject:  
            message = new KasqObjectMessage(istream);
            break;
          case cKasqMessageBytes:
            message = new KasqBytesMessage(istream);
            break;
          case cKasqMessageStream:
            message = new KasqStreamMessage(istream);
            break;
          case cKasqMessageMap:
            message = new KasqMapMessage(istream);
            break;
          default:
            break;
        }
      }
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
      sLogger.debug("KasqMessageFactory::createFromStream() - Exception caught: ", e);
      throw new StreamCorruptedException("ClassNotFoundException caught, Message: " + e.getMessage());
    }
    catch (SocketTimeoutException e)
    {
      throw e;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      sLogger.debug("KasqMessageFactory::createFromStream() - Exception caught: ", e);
      throw new StreamCorruptedException("IOException caught, Message: " + e.getMessage());
    }
    
    sLogger.debug("KasqMessageFactory::createFromStream() - OUT, Result=" + StringUtils.asString(message));
    return message;
  }
}
