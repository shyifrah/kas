package com.kas.sec.entities;

import java.util.List;

/**
 * A group entity interface
 * 
 * @author Pippo
 */
public interface IGroupEntity extends IEntity
{
  /**
   * Get the list of members of this group
   * 
   * @return the list of members of this group
   */
  public abstract List<IUserEntity> getMembers();
}
