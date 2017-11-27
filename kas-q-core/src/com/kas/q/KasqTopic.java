package com.kas.q;

import javax.jms.JMSException;
import javax.jms.TemporaryTopic;
import com.kas.q.ext.EDestinationType;

public class KasqTopic extends KasqDestination implements TemporaryTopic
{
  private static final long serialVersionUID = 1L;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqQueue} object, specifying its name
   * 
   * @param name the name associated with this queue
   * @param managerName the name of the manager of this queue
   */
  public KasqTopic(String name, String managerName)
  {
    super(EDestinationType.cTopic, name, managerName);
  }
  
  /***************************************************************************************************************
   * 
   */
  public String getTopicName() throws JMSException
  {
    return getName();
  }
  
  /***************************************************************************************************************
   * 
   */
  public void delete() throws JMSException
  {
    internalDelete();
  }
}
