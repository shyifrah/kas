package com.kas.infra.base;

public interface IInitializable
{
  /***************************************************************************************************************
   * Initialize an object
   * 
   * @return true if object was initialized
   */
  public abstract boolean init();
  
  /***************************************************************************************************************
   * Terminate an object
   * 
   * @return true if object was terminated
   */
  public abstract boolean term();
}
