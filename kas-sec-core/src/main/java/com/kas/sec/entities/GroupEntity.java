package com.kas.sec.entities;

/**
 * A group entity
 * 
 * @author Pippo
 */
public class GroupEntity extends Entity
{
  /**
   * Construct a group with the specified parameters
   * 
   * @param id
   *   The entity ID
   * @param name
   *   The entity Name
   * @param desc
   *   The entity description
   */
  public GroupEntity(int id, String name, String desc)
  {
    super(id, name, desc);
  }
}
