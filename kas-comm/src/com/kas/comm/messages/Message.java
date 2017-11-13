package com.kas.comm.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.IMessage;
import com.kas.comm.impl.MessageClass;
import com.kas.comm.impl.MessageType;
import com.kas.infra.base.KasObject;
import com.kas.infra.base.UniqueId;

public class Message extends KasObject implements IMessage
{
  /***************************************************************************************************************
   * 
   */
  protected UniqueId mMessageId;
  protected UniqueId mCorrelationId;
  
  /***************************************************************************************************************
   * Constructs a default {@code Message} object
   */
  public Message()
  {
    mMessageId     = UniqueId.generate();
    mCorrelationId = UniqueId.cNullUniqueId;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code Message} object that is used as reply for a different message
   * This constructor is package-protected because it can be called only from other message.
   * 
   * @param replyToMessageId the message Id of a request message (this message is the reply message)
   */
  Message(UniqueId replyToMessageId)
  {
    mMessageId     = UniqueId.generate();
    mCorrelationId = replyToMessageId;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code Message} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws ClassNotFoundException
   * @throws IOException
   */
  public Message(ObjectInputStream istream) throws IOException, ClassNotFoundException
  {
    byte [] idBytes = new byte [16];
    istream.read(idBytes);
    mMessageId = UniqueId.fromByteArray(idBytes);
    
    istream.read(idBytes);
    mCorrelationId = UniqueId.fromByteArray(idBytes);
  }
  
  /***************************************************************************************************************
   * 
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.write(mMessageId.toByteArray());
    ostream.reset();
    ostream.write(mCorrelationId.toByteArray());
    ostream.reset();
  }

  /***************************************************************************************************************
   * 
   */
  public MessageType getMessageType()
  {
    return MessageType.cMessage;
  }
  
  /***************************************************************************************************************
   * 
   */
  public MessageClass getMessageClass()
  {
    return MessageClass.cUnknownMessage;
  }

  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(pad).append("  MessageId=[").append(mMessageId.toString()).append("]\n")
      .append(pad).append("  CorrelationId=[").append(mCorrelationId.toString()).append("]\n");
    return sb.toString();
  }
}
