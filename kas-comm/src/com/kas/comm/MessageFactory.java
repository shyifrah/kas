package com.kas.comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import com.kas.comm.impl.MessageClass;
import com.kas.comm.impl.MessageHeader;
import com.kas.comm.messages.AuthenticateRequestMessage;
import com.kas.comm.messages.Message;
import com.kas.comm.messages.ResponseMessage;

public class MessageFactory implements IMessageFactory
{
  private static MessageFactory sInstance = new MessageFactory();
  
  private IMessageFactory [] mSecondaryFactories;
  
  private MessageFactory()
  {
    mSecondaryFactories = new IMessageFactory [10];
  }
  
  public static MessageFactory getInstance()
  {
    return sInstance;
  }
  
  public void registerSecondaryFactory(IMessageFactory factory, int classId)
  {
    synchronized(mSecondaryFactories)
    {
      if (classId > mSecondaryFactories.length)
      {
        IMessageFactory [] temp = new IMessageFactory [classId];
        System.arraycopy(mSecondaryFactories, 0, temp, 0, mSecondaryFactories.length);
        mSecondaryFactories = temp;
      }
      
      mSecondaryFactories[classId] = factory;
    }
  }
  
  public IMessage createFromStream(ObjectInputStream istream) throws StreamCorruptedException
  {
    IMessage message = null;
    try
    {
      MessageHeader header = new MessageHeader(istream);
      if (!header.verify())
        throw new StreamCorruptedException("Invalid header. Expected: [" + MessageHeader.cEyeCatcher + "]; Got: [" + header.getEyeCatcher() + "]");
      
      switch (header.getMessageType())
      {
        case cMessage:
          message = new Message(istream);
          break;
        case cResponseMessage:
          message = new ResponseMessage(istream);
          break;
        case cAuthenticateRequestMessage:
          message = new AuthenticateRequestMessage(istream);
          break;
        case cDataMessage:
          message = createFromStream(istream, header);
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
  
  public IMessage createFromStream(ObjectInputStream istream, MessageHeader header) throws StreamCorruptedException
  {
    IMessage message = null;
    MessageClass msgClass = header.getMessageClass();
    int ord = msgClass.ordinal();
    if (ord > MessageClass.cUnknownMessage.ordinal())
    {
      IMessageFactory factory = null;
      synchronized (mSecondaryFactories)
      {
        factory = mSecondaryFactories[ord];
      }
      
      if (factory != null)
      {
        message = factory.createFromStream(istream, header);
      }
    }
    return message;
  }
}
