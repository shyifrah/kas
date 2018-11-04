package com.kas.sec.entities;

import com.kas.infra.base.AKasObject;

/**
 * An {@link Entity} is an object that can be permitted to a resource
 * 
 * @author Pippo
 */
public class Entity extends AKasObject
{
  /**
   * The entity ID
   */
  protected int mId;
  
  /**
   * The name of the entity
   */
  protected String mName;
  
  /**
   * Free text description
   */
  protected String mDescription;
  
  /**
   * Construct an entity using the specified name
   * 
   * @param id The entity ID
   * @param name Name of the entity
   * @param desc Description
   */
  protected Entity(int id, String name, String desc)
  {
    mId = id;
    mName = name;
    mDescription = desc;
  }
  
  /**
   * Get the entity ID
   * 
   * @return the entity ID
   */
  public int getId()
  {
    return mId;
  }
  
  /**
   * Get the entity name
   * 
   * @return the entity name
   */
  public String getName()
  {
    return mName;
  }
  
  /**
   * Get the entity description
   * 
   * @return the entity description
   */
  public String getDescription()
  {
    return mDescription;
  }
  
  /**
   * Get the entity's string representation
   * 
   * @return the entity's string representation
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(mName).append(" (").append(mId).append(')');
    return sb.toString();
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  Description=").append(mDescription).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
