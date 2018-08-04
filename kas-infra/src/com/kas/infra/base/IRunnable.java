package com.kas.infra.base;

/**
 * A Runnable {@link IObject}
 * 
 * @author Pippo
 *
 */
public interface IRunnable extends IObject, Runnable
{
  public abstract IRunnable replicate();
}
