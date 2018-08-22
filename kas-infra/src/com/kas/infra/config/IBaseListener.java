package com.kas.infra.config;

/**
 * A configuration listener.
 *   
 * @author Pippo
 */
public interface IBaseListener
{
  /**
   * Refresh configuration.<br>
   * <br>
   * When called, the object should update its configuration
   */
  public abstract void refresh();
}
