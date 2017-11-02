package com.kas.q.ext;

import com.kas.infra.base.IInitializable;

public interface IDestination extends IInitializable
{
  /***************************************************************************************************************
   * Put {@code IMessage} to destination
   * 
   * @param message the {@code IMessage} to be placed in the destination
   */
  public abstract void put(IMessage message);
  
  /***************************************************************************************************************
   * Get {@code IMessage} from destination, but does not remove it
   * 
   * @return the first {@code IMessage} in the destination
   */
  public abstract IMessage get();
  
  /***************************************************************************************************************
   * Get and remove {@code IMessage} from destination
   * 
   * @return the first {@code IMessage} in the destination
   */
  public abstract IMessage remove();
  
  /***************************************************************************************************************
   * Returns the destination name
   * 
   * @return a string representing this destination
   */
  public abstract String toDestinationName();
}
