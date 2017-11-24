package com.kas.q.ext;

import java.io.Serializable;
import javax.jms.Destination;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;

public interface IKasqDestination extends IInitializable, Serializable, Destination, IObject
{
  /***************************************************************************************************************
   * Put a {@code IMessage} to the destination.
   * 
   * @param message the {@code IMessage} to be placed in the destination.
   */
  public abstract void put(IKasqMessage message);
  
  /***************************************************************************************************************
   * Get a {@code IMessage} from the destination, but not remove it.
   * 
   * @return the first {@code IMessage} in the destination.
   */
  public abstract IKasqMessage peek();
  
  /***************************************************************************************************************
   * Get and remove a {@code IMessage} from the destination.
   * If a message is not available, this caller is blocked indefinitely.
   * 
   * @return the first {@code IMessage} in the destination.
   */
  public abstract IKasqMessage get();
  
  /***************************************************************************************************************
   * Get and remove a {@code IMessage} from the destination.
   * If a message is not available, this call will wait for {@code timeout} milliseconds.
   * 
   * @param timeout number of milliseconds to wait for a message to become available.
   * 
   * @return the first {@code IMessage} in the destination. If a message is not available and
   *   {@code timeout} milliseconds have passed, {@code null} is returned.
   */
  public abstract IKasqMessage getAndWait(long timeout);
  
  /***************************************************************************************************************
   * Get and remove a {@code IMessage} from the destination, but only if a message is immediately available.
   * 
   * @return the first {@code IMessage} in the destination. If a message is not available, {@code null}
   *   is returned.
   */
  public abstract IKasqMessage getNoWait();
  
  /***************************************************************************************************************
   * Returns the size of the destination.
   * 
   * @return number of messages in destination
   */
  public abstract int size();
  
  /***************************************************************************************************************
   * Returns the type of the destination.
   * 
   * @return "queue" if the destination is queue, "topic" if it's a topic
   */
  public abstract String getType();
  
  /***************************************************************************************************************
   * Returns the name of the destination (the name of the queue/topic).
   * 
   * @return the name of the destination
   */
  public abstract String getName();

  /***************************************************************************************************************
   * Returns the destination formatted name
   * 
   * @return a string representing the full destination path
   */
  public abstract String getFormattedName();
}
