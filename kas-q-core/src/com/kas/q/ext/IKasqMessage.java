package com.kas.q.ext;

import javax.jms.Message;
import com.kas.comm.IMessage;
import com.kas.comm.impl.MessageSubType;
import com.kas.infra.base.ISerializable;

public interface IKasqMessage extends ISerializable, IMessage, Message
{
  /***************************************************************************************************************
   * Get {@code MessageClass} of the object
   * 
   * @return {@code MessageClass} of the object
   */
  public abstract MessageSubType getMessageSubType();
}
