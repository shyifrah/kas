package com.kas.sec.entities;

import java.util.List;

/**
 * A user entity interface
 * 
 * @author Pippo
 */
public interface IUserEntity extends IEntity
{
  /**
   * Get the list of groups this user is member of
   * 
   * @return the list of groups this user is member of
   */
  public abstract List<IGroupEntity> getGroups();
}
