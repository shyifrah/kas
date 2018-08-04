package com.kas.infra.config;

/**
 * A {@code IRegistrar} is an object to which other objects, objects that implements the {@link IListener} interface,
 * are registered and unregistered from in order to be notified about events happening in the {@code IRegistrar}.
 * 
 * @author Pippo
 * 
 */
public interface IRegistrar
{
  /**
   * Register a new listener
   * 
   * @param listener the listener to register with the registrar
   */
  public void register(IListener listener);
  
  /**
   * Unregister a listener
   * 
   * @param listener the listener to unregister with the registrar
   */
  public void unregister(IListener listener);
}
