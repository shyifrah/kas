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
   * Get the user's password
   * 
   * @return the user's password
   */
  public abstract byte [] getPassword();
  
  /**
   * Get the groups in which a user is member
   * 
   * @return list of {@link IGroupEntity}
   */
  public abstract List<IGroupEntity> getGroups();
}
