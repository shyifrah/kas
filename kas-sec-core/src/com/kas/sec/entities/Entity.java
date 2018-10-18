package com.kas.sec.entities;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;

/**
 * An {@link Entity} is an object that can be permitted to a resource
 * 
 * @author Pippo
 */
public class Entity extends AKasObject
{
  /**
   * The entity unique ID
   */
  protected UniqueId mEntityId;
  
  /**
   * The name of the entity
   */
  protected String mName;
  
  /**
   * Construct an entity using the specified name
   * 
   * @param name The name of the entity
   */
  protected Entity(String name)
  {
    mEntityId = UniqueId.generate();
    mName = name;
  }
  
  /**
   * Get the entity ID
   * 
   * @return the entity ID
   */
  public UniqueId getId()
  {
    return mEntityId;
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
   * Get the entity's string representation
   * 
   * @return the entity's string representation
   */
  public String toString()
  {
    return String.format("%s (%s)", mName, mEntityId.toString());
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
      .append(pad).append("  Id=").append(mEntityId.toString()).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
