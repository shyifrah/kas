package com.kas.sec.entities;

import com.kas.infra.base.IDao;

/**
 * Extension of {@link IDao} for {@link IGroupEntity}
 * 
 * @author Pippo
 */
public interface IGroupDao extends IDao<IGroupEntity>
{
  /**
   * Get a {@link IGroupEntity} by its name
   * 
   * @param name The name of the {@link IGroupEntity}
   * @return the {@link IGroupEntity} with the specified name or {@code null} if not found
   */
  public abstract IGroupEntity get(String name);
}
