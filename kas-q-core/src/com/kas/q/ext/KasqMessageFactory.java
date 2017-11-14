package com.kas.q.ext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import com.kas.comm.IPacket;
import com.kas.comm.IPacketFactory;
import com.kas.comm.impl.PacketHeader;
import com.kas.q.KasqBytesMessage;
import com.kas.q.KasqMapMessage;
import com.kas.q.KasqMessage;
import com.kas.q.KasqObjectMessage;
import com.kas.q.KasqStreamMessage;
import com.kas.q.KasqTextMessage;

public class KasqMessageFactory implements IPacketFactory
{
  public IPacket createFromStream(ObjectInputStream istream) throws StreamCorruptedException
  {
    IPacket message = null;
    try
    {
      PacketHeader header = new PacketHeader(istream);
      
      int id = header.getClassId();
      if (id == PacketHeader.cClassIdKasq)
      {
        int type = header.getType();
        EMessageType messageType = EMessageType.fromInt(type);
        
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
      throw new StreamCorruptedException("ClassNotFoundException caught, Message: " + e.getMessage());
    }
    catch (IOException e)
    {
      throw new StreamCorruptedException("IOException caught, Message: " + e.getMessage());
    }
     
    return message;
  }
}
