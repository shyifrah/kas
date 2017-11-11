package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import com.kas.comm.IMessage;
import com.kas.comm.MessageFactory;

public class MessageSerializer
{
  /***************************************************************************************************************
   * Serialize a {@code IMessage} object to {@code ObjectOutputStream}.
   * To serialize a {@code IMessage} object we first create a MessageHeader with the appropriate
   * {@code MessageType}, serialize it to the {@code ObjectOutputStream}, and then serialize the {@code IMessage}.
   * 
   * @param ostream the {@code ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException
   */
  public static void serialize(ObjectOutputStream ostream, IMessage message) throws IOException
  {
    MessageHeader header = new MessageHeader(message.getMessageType());
    header.serialize(ostream);
    message.serialize(ostream);
  }
  
  /***************************************************************************************************************
   * Deserialize a {@code IMessage} object from {@code ObjectInputStream}.
   * To deserialize a {@code IMessage} object we actually need to call the {@code MessageFactory}. 
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws StreamCorruptedException
   */
  public static IMessage deserialize(ObjectInputStream istream) throws StreamCorruptedException
  {
    return MessageFactory.createFromStream(istream);
  }
}
