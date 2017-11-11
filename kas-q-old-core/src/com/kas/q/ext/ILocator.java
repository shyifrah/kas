package com.kas.q.ext;

import com.kas.q.KasqQueue;
import com.kas.q.KasqTopic;

public interface ILocator
{
  /***************************************************************************************************************
   * Locate a {@code KasqQueue} by its name
   * 
   * @param name the name of the {@code KasqQueue} to be located
   * 
   * @return the located {@code KasqQueue}, or null if it was not found 
   */
  public abstract KasqQueue locateQueue(String name);
  
  /***************************************************************************************************************
   * Locate a topic Destination by its name
   * 
   * @param name the name of the {@code Topic} to be located
   * 
   * @return the located {@code Topic}, or null if it was not found 
   */
  public abstract KasqTopic locateTopic(String name);
}
