package com.kas.q.ext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import com.kas.q.KasqBytesMessage;
import com.kas.q.KasqMapMessage;
import com.kas.q.KasqMessage;
import com.kas.q.KasqObjectMessage;
import com.kas.q.KasqStreamMessage;
import com.kas.q.KasqTextMessage;

public class KasqMessageFactory
{
  /***************************************************************************************************************
   * Constructs a {@code IKasqMessage} object from {@code ObjectInputStream}
   * Each serialized {@code IKasqMessage} is prefixed with a {@link KasqMessageHeader}, so we read it first and
   * according to the {@code MessageClass}, call the appropriate constructor.
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws StreamCorruptedException
   */
  public static IKasqMessage createFromStream(ObjectInputStream istream) throws StreamCorruptedException
  {
    IKasqMessage message = null;
    try
    {
      KasqMessageHeader header = new KasqMessageHeader(istream);
      if (!header.verify())
        throw new StreamCorruptedException("Invalid header. Expected: [" + KasqMessageHeader.cEyeCatcher + "]; Got: [" + header.getEyeCatcher() + "]");
      
      switch (header.getMessageClass())
      {
        case cMessage:
          message = new KasqMessage(istream);
          break;
        case cTextMessage:
          message = new KasqTextMessage(istream);
          break;
        case cObjectMessage:
          message = new KasqObjectMessage(istream);
          break;
        case cBytesMessage:
          message = new KasqBytesMessage(istream);
          break;
        case cStreamMessage:
          message = new KasqStreamMessage(istream);
          break;
        case cMapMessage:
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
