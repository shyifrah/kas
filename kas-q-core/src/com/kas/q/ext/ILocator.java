package com.kas.q.ext;

import javax.jms.Queue;
import javax.jms.Topic;

public interface ILocator
{
  /***************************************************************************************************************
   * Locate a queue Destination by its name
   * 
   * @param name the name of the {@code Queue} to be located
   * 
   * @return the located {@code Queue}, or null if it was not found 
   */
  public abstract Queue locateQueue(String name);
  
  /***************************************************************************************************************
   * Locate a topic Destination by its name
   * 
   * @param name the name of the {@code Topic} to be located
   * 
   * @return the located {@code Topic}, or null if it was not found 
   */
  public abstract Topic locateTopic(String name);
}
