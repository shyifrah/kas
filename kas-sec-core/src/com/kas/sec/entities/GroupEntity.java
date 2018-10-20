package com.kas.sec.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * A group entity
 * 
 * @author Pippo
 */
public class GroupEntity extends Entity implements IGroupEntity
{
  /**
   * List of members of this group
   */
  private List<IUserEntity> mMembers;
  
  /**
   * Construct a group with the specified name
   * 
   * @param name The name of the group
   */
  GroupEntity(String name)
  {
    super(name);
    mMembers = new ArrayList<IUserEntity>();
  }
  
  /**
   * Get the list of members of this group
   * 
   * @return the list of members of this group
   */
  public List<IUserEntity> getMembers()
  {
    return mMembers;
  }
}
