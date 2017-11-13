package com.kas.comm;

import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import com.kas.comm.impl.MessageHeader;

public interface IMessageFactory
{
  /***************************************************************************************************************
   * Constructs a {@code IMessage} object from {@code ObjectInputStream}
   * Each serialized {@code IMessage} is prefixed with a {@link MessageHeader}, so we read it first and
   * according to the {@code MessageType}, call the appropriate constructor.
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * @param header the {@code MessageHeader} that was previously deserialized from the input stream 
   * 
   * @throws StreamCorruptedException
   */
  public abstract IMessage createFromStream(ObjectInputStream istream, MessageHeader header) throws StreamCorruptedException;
}
