package com.kas.mq.server.security;

import com.kas.infra.base.IDao;

/**
 * Extension of {@link IDao} for {@link IUserEntity}
 * 
 * @author Pippo
 */
public interface IUserDao extends IDao<IUserEntity>
{
  /**
   * Get a {@link IUserEntity} by its name
   * 
   * @param name The name of the {@link IUserEntity}
   * @return the {@link IUserEntity} with the specified name or {@code null} if not found
   */
  public abstract IUserEntity get(String name);
}
