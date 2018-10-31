package com.kas.sec.resources;

import com.kas.infra.base.IDao;

/**
 * Extension of {@link IDao} for {@link IResourceClass}
 * 
 * @author Pippo
 */
public interface IResourceClassDao extends IDao<IResourceClass>
{
  /**
   * Get a {@link IResourceClass} by its name
   * 
   * @param name The name of the {@link IUserEntity}
   * @return the {@link IUserEntity} with the specified name or {@code null} if not found
   */
  public abstract IResourceClass get(String name);
}
