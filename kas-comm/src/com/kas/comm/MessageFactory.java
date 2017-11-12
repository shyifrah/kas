package com.kas.comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import com.kas.comm.impl.MessageHeader;
import com.kas.comm.messages.AuthenticateRequestMessage;
import com.kas.comm.messages.Message;
import com.kas.comm.messages.ResponseMessage;

public class MessageFactory
{
  /***************************************************************************************************************
   * Constructs a {@code IMessage} object from {@code ObjectInputStream}
   * Each serialized {@code IMessage} is prefixed with a {@link MessageHeader}, so we read it first and
   * according to the {@code MessageType}, call the appropriate constructor.
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws StreamCorruptedException
   */
  public static IMessage createFromStream(ObjectInputStream istream) throws StreamCorruptedException
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
