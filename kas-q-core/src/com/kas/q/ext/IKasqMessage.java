package com.kas.q.ext;

import javax.jms.Message;
import com.kas.comm.IPacket;

public interface IKasqMessage extends IPacket, Message
{
  /***************************************************************************************************************
   * Get the message's type
   * 
   * @return the message's type
   */
  public abstract EMessageType getType();
}
