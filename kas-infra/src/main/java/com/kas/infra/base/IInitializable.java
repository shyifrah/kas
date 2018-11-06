package com.kas.infra.base;

/**
 * Implementors of this interface are usually classes that requires some sort of initialization
 * prior their usage, and optionally, requires some sort of termination as well.
 * 
 * @author Pippo
 */
public interface IInitializable
{
  /**
   * Initialize the object
   * 
   * @return {@code true} if object was initialized successfully, {@code false} otherwise
   */
  public abstract boolean init();
  
  /**
   * Terminate the object
   * 
   * @return {@code true} if object was terminated successfully, {@code false} otherwise
   */
  public abstract boolean term();
}
