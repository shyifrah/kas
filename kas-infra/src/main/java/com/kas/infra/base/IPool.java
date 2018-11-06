package com.kas.infra.base;

/**
 * A pool of objects
 * 
 * @author Pippo
 *
 * @param <T> A pooled object
 */
public interface IPool<T>
{
  /**
   * Allocate a new pooled object
   * 
   * @return the newly allocated object
   */
  public abstract T allocate();
  
  /**
   * Release the specified object
   * 
   * @param pobj The pooled object to release
   */
  public abstract void release(T pobj);
  
  /**
   * Shutdown the pool
   */
  public abstract void shutdown();
}
