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
  /**
   * Construct a group with the specified parameters
   * 
   * @param id The entity ID
   * @param name The entity Name
   * @param desc The entity description
   */
  GroupEntity(int id, String name, String desc)
  {
    super(id, name, desc);
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
