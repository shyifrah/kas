package com.kas.sec.entities;

/**
 * A group entity
 * 
 * @author Pippo
 */
public class GroupEntity extends Entity implements IGroupEntity
{
  /**
   * Construct a group with the specified parameters
   * 
   * @param name The entity Name
   * @param desc The entity description
   */
  public GroupEntity(String name, String desc)
  {
    super(name, desc);
  }
}
