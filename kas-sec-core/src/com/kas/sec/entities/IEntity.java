package com.kas.sec.entities;

import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;

/**
 * A general purpose entity interface
 * 
 * @author Pippo
 */
public interface IEntity extends IObject
{
  /**
   * Get the entity ID
   * 
   * @return the entity ID
   */
  public abstract UniqueId getId();
  
  /**
   * Get the entity name
   * 
   * @return the entity name
   */
  public abstract String getName();
}
