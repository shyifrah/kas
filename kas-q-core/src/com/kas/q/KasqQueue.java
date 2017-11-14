package com.kas.q;

import javax.jms.JMSException;
import javax.jms.TemporaryQueue;

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
}
