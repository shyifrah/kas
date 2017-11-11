package com.kas.q.ext;

import javax.jms.Message;
import com.kas.comm.IMessage;
import com.kas.infra.base.ISerializable;

public interface IKasqMessage extends ISerializable, IMessage, Message
{
  /***************************************************************************************************************
   * Get {@code MessageType} of the object
   * 
   * @return {@code MessageType} of the object
   */
  public abstract MessageClass getMessageClass();
}
