package com.kas.infra.config;

import com.kas.infra.base.IObject;

/**
 * A configuration listener.
 *   
 * @author Pippo
 */
public interface IListener extends IObject
{
  /**
   * Refresh configuration.<br>
   * <br>
   * When called, the object should update its configuration
   */
  public void refresh();
}
