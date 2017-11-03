package com.kas.q.ext.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.MessageHeader;
import com.kas.q.impl.KasqMessage;
import com.kas.q.impl.KasqTextMessage;
import com.kas.q.impl.KasqWrappedMessage;

public class MessageSerializer
{
  public static void serialize(ObjectOutputStream ostream, IMessage message) throws IOException
  {
    MessageHeader header = new MessageHeader(message.getMessageType());
    header.serialize(ostream);
    message.serialize(ostream);
  }
  
  public static IMessage deserialize(ObjectInputStream istream) throws IOException, ClassNotFoundException
  {
    IMessage message = null;
    MessageHeader header = new MessageHeader(istream);
    switch (header.getType())
    {
      case cMessage:
        message = new KasqMessage(istream);
        break;
      case cTextMessage:
        message = new KasqTextMessage(istream);
        break;
      case cObjectMessage:
        //message = new KasqObjectMessage(istream);
        break;
      case cBytesMessage:
        //message = new KasqBytesMessage(istream);
        break;
      case cStreamMessage:
        //message = new KasqStreamMessage(istream);
        break;
      case cMapMessage:
        //message = new KasqMapMessage(istream);
        break;
      case cJavaxJmsMessage:
        message = new KasqWrappedMessage(istream);
        break;
    }
    
    return message;
  }
}
