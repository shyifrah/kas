package com.kas.q.ext;

import java.io.Serializable;
import com.kas.infra.base.IInitializable;

public interface IDestination extends IInitializable, Serializable
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
   * Returns the size of the destination.
   * This method does not synchronize the destination, so the number might not be accurate.
   * 
   * @return number of messages in destination
   */
  public abstract int getSize();
  
  /***************************************************************************************************************
   * Returns the name of the destination.
   * 
   * @return the name of the destination
   */
  public abstract String getName();

  /***************************************************************************************************************
   * Returns the destination name
   * 
   * @return a string representing this destination
   */
  public abstract String toDestinationName();
}
