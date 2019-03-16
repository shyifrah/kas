package com.kas.infra.config;

import com.kas.infra.base.IObject;

/**
 * A configuration listener.
 *   
 * @author Pippo
 */
public interface IBaseListener extends IObject
{
  /**
   * Refresh configuration.<br>
   * When called, the object should update its configuration
   */
  public abstract void refresh();
}
