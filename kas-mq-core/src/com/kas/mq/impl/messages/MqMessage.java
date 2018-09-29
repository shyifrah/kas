package com.kas.mq.impl.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.mq.internal.ABaseMessage;

/**
 * A KAS/MQ message, without a payload.
 * 
 * @author Pippo
 * 
 * @see com.kas.mq.internal.ABaseMessage
 */
public final class MqMessage extends ABaseMessage implements IPacket
{
  /**
   * Construct a default message
   */
  MqMessage()
  {
    super();
  }
  
  /**
   * Constructs a {@link MqMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link ABaseMessage}
   * 
   * @return the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqMessage);
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(super.toPrintableString(level))
      .append(pad).append(')');
    return sb.toString();
  }
}
