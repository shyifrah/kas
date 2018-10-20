package com.kas.sec.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * A user entity
 * 
 * @author Pippo
 */
public class UserEntity extends Entity implements IUserEntity
{
  /**
   * List of groups the user is attached to
   */
  private List<IGroupEntity> mGroups;
  
  /**
   * Construct a user entity with the specified name
   * 
   * @param name The name of the entity
   */
  UserEntity(String name)
  {
    super(name);
    mGroups = new ArrayList<IGroupEntity>();
  }
  
  /**
   * Get the list of groups this user is member of
   * 
   * @return the list of groups this user is member of
   */
  public List<IGroupEntity> getGroups()
  {
    return mGroups;
  }
}
