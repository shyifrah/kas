package com.kas.infra.config;

import com.kas.infra.base.IObject;

/**
 * A {@link IRegistrar} is an object to which other objects, objects that implements the {@link IListener} interface,
 * are registered and unregistered from in order to be notified about events happening in the {@link IRegistrar}.
 * 
 * @author Pippo
 */
public interface IRegistrar extends IObject
{
  /**
   * Register a new listener
   * 
   * @param listener the listener to register with the registrar
   */
  public abstract void register(IListener listener);
  
  /**
   * Unregister a listener
   * 
   * @param listener the listener to unregister with the registrar
   */
  public abstract void unregister(IListener listener);
  
  /**
   * Returns the {@link #IRegistrar} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link IRegistrar}.
   * 
   * @return a replica of this {@link IRegistrar}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IRegistrar replicate();
  
  /**
   * Returns the {@link IRegistrar} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
