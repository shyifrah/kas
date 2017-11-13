package com.kas.comm;

import java.io.IOException;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.MessageClass;
import com.kas.comm.impl.MessageType;
import com.kas.infra.base.IObject;

public interface IMessage extends IObject
{
  /***************************************************************************************************************
   * Serialize a {@code IMessage} object to {@code ObjectOutputStream}.
   * 
   * @param ostream the {@code ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException
   */
  public abstract void serialize(ObjectOutputStream ostream) throws IOException;
  
  /***************************************************************************************************************
   * Get the {@code MessageType}
   * 
   * @return the message's {@code MessageType}
   */
  public abstract MessageType getMessageType();
  
  /***************************************************************************************************************
   * Get the {@code MessageClass}
   * 
   * @return the message's {@code MessageClass}
   */
  public abstract MessageClass getMessageClass();
}
