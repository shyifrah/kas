package com.kas.q.ext;

import javax.jms.Message;
import com.kas.infra.base.ISerializable;

public interface IMessage extends ISerializable, Message
{
  /***************************************************************************************************************
   * Get {@code MessageType} of the object
   * 
   * @return {@code MessageType} of the object
   */
  public abstract MessageType getMessageType();
}
