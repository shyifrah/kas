package com.kas.q;

import javax.jms.JMSException;
import javax.jms.TemporaryQueue;
import com.kas.q.ext.AKasqDestination;
import com.kas.q.ext.IKasqMessage;

public class KasqQueue extends AKasqDestination implements TemporaryQueue
{
  private static final long serialVersionUID = 1L;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqQueue} object, specifying its name
   * 
   * @param name the name associated with this queue
   * @param managerName the name of the manager of this queue
   */
  public KasqQueue(String name, String managerName)
  {
    super(name, managerName);
  }
  
  /***************************************************************************************************************
   * 
   */
  public String getQueueName() throws JMSException
  {
    return getName();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String getType()
  {
    return "queue";
  }

  /***************************************************************************************************************
   * 
   */
  public void delete() throws JMSException
  {
    internalDelete();
  }
  
  /***************************************************************************************************************
   * 
   */
  protected IKasqMessage requestReply(IKasqMessage request) throws JMSException
  {
    return mSession.internalSendAndReceive(request);
  }
}
