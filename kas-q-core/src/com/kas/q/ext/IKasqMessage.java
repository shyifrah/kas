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
  
  /***************************************************************************************************************
   * Returns an indication whether a message has expired
   * 
   * @return true if the message's expiration date is lower than the current timestamp, false otherwise
   */
  public abstract boolean isExpired();
  
  /***************************************************************************************************************
   * Returns an indication whether a message has expired, relatively to specified timestamp
   * 
   * @return true if the message's expiration date is lower than the specified timestamp, false otherwise
   */
  public abstract boolean isExpired(long timestamp);
}
