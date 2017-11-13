package com.kas.q.ext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import com.kas.comm.IMessage;
import com.kas.comm.IMessageFactory;
import com.kas.comm.MessageFactory;
import com.kas.comm.impl.MessageClass;
import com.kas.comm.impl.MessageHeader;
import com.kas.q.KasqBytesMessage;
import com.kas.q.KasqMapMessage;
import com.kas.q.KasqMessage;
import com.kas.q.KasqObjectMessage;
import com.kas.q.KasqStreamMessage;
import com.kas.q.KasqTextMessage;

public class KasqMessageFactory implements IMessageFactory
{
  public KasqMessageFactory()
  {
    MessageFactory.getInstance().registerSecondaryFactory(this, MessageClass.cUnknownMessage.ordinal());
    MessageFactory.getInstance().registerSecondaryFactory(this, MessageClass.cKasqTextMessage.ordinal());
    MessageFactory.getInstance().registerSecondaryFactory(this, MessageClass.cKasqObjectMessage.ordinal());
    MessageFactory.getInstance().registerSecondaryFactory(this, MessageClass.cKasqBytesMessage.ordinal());
    MessageFactory.getInstance().registerSecondaryFactory(this, MessageClass.cKasqStreamMessage.ordinal());
    MessageFactory.getInstance().registerSecondaryFactory(this, MessageClass.cKasqMapMessage.ordinal());
  }
  
  public IMessage createFromStream(ObjectInputStream istream, MessageHeader header) throws StreamCorruptedException
  {
    IMessage message = null;
    try
    {
      switch (header.getMessageClass())
      {
        case cKasqMessage:
          message = new KasqMessage(istream);
          break;
        case cKasqTextMessage:
          message = new KasqTextMessage(istream);
          break;
        case cKasqObjectMessage:
          message = new KasqObjectMessage(istream);
          break;
        case cKasqBytesMessage:
          message = new KasqBytesMessage(istream);
          break;
        case cKasqStreamMessage:
          message = new KasqStreamMessage(istream);
          break;
        case cKasqMapMessage:
          message = new KasqMapMessage(istream);
          break;
        default:
          break;
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
