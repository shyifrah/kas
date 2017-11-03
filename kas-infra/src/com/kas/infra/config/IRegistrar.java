package com.kas.infra.config;

public interface IRegistrar
{
  /***************************************************************************************************************
   * Register a new listener
   * 
   * @param listener the listener to register with the registrar
   */
  public void register(IListener listener);
  
  /***************************************************************************************************************
   * Unregister a listener
   * 
   * @param listener the listener to unregister with the registrar
   */
  public void unregister(IListener listener);
}
