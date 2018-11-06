package com.kas.infra.config;

/**
 * A {@link IBaseRegistrar} is an object to which other objects, objects that implements the {@link IBaseListener} interface,
 * are registered and unregistered from in order to be notified about events happening in the {@link IBaseRegistrar}.
 * 
 * @author Pippo
 */
public interface IBaseRegistrar
{
  /**
   * Register a new listener
   * 
   * @param listener the listener to register with the registrar
   */
  public abstract void register(IBaseListener listener);
  
  /**
   * Unregister a listener
   * 
   * @param listener the listener to unregister with the registrar
   */
  public abstract void unregister(IBaseListener listener);
}
