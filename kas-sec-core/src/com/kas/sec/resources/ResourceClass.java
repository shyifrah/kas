package com.kas.sec.resources;

import java.util.Enumeration;
import com.kas.infra.base.AKasObject;
import com.kas.sec.access.AccessEntry;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.access.AccessList;
import com.kas.sec.entities.UserEntity;

/**
 * An implementation of {@link IResourceClass}
 * 
 * @author Pippo
 */
public class ResourceClass extends AKasObject
{
  static private final int cMaxResourceNameLength = 32;
  
  /**
   * The resource class ID
   */
  private int mId;
  
  /**
   * The resource class name
   */
  private String mName;
  
  /**
   * List of access levels
   */
  private AccessLevel mEnabledAccessLevels;
  
  /**
   * The access list attached to this resource class
   */
  private AccessList mAccessList;
  
  /**
   * Construct a {@link ResourceClass}
   * 
   * @param id The Id of the resource class
   * @param name The name of the resource class
   * @param accessLevels A list of logically-ORed access-levels (integers)
   * that are supported by this resource class
   * 
   * @throws RuntimeException if name or description are invalid
   */
  public ResourceClass(int id, String name, int accessLevels)
  {
    if ((name == null) || (name.length() == 0) || (name.length() > cMaxResourceNameLength))
      throw new IllegalArgumentException("Invalid resource class name. Null, empty or too long resource name: [" + name + "]");
    
    if (AccessLevel.NONE == accessLevels)
      throw new IllegalArgumentException("Invalid enabled access levels. At least one bit should be turned on");
    
    mId = id;
    mName = name;
    mEnabledAccessLevels = new AccessLevel(accessLevels);
  }
  
  /**
   * Get the ID of the resource class
   * 
   * @return the ID of the resource class
   */
  public int getId()
  {
    return mId;
  }
  
  /**
   * Get the name of the resource class
   * 
   * @return the name of the resource class
   */
  public String getName()
  {
    return mName;
  }
  
  /**
   * Get the access levels that are enabled for this resource class
   * 
   * @return the access levels that are enabled for this resource class
   */
  public AccessLevel getEnabledAccessLevels()
  {
    return mEnabledAccessLevels;
  }
  
  /**
   * Compare to another resource class
   * 
   * @return the value returned by {@link String#compareTo(String)}
   * 
   * @see Comparable#compareTo(Object)
   */
  public int compareTo(ResourceClass other)
  {
    return mName.compareTo(other.getName());
  }
  
  /**
   * Compare two Objects for equality
   * 
   * @return the value returned by {@link String#equals(Object)}
   * 
   * @see Comparable#equals(Object)
   */
  public boolean equals(Object other)
  {
    return mName.equals(((ResourceClass)other).mName);
  }
  
  /**
   * Get the hash code of the resource class
   * 
   * @return the value returned by {@link String#hashCode()}
   * 
   * @see Comparable#hashCode()}
   */
  public int hashCode()
  {
    return mName.hashCode();
  }
  
  /**
   * Get a one-line string representation of this resource class
   * 
   * @return a one-line string representation of this resource class
   */
  public String toString()
  {
    return String.format("%s (%d)", mName, mId);
  }
  
  /**
   * Get the access level in which {@code user} can access the {@code resName}
   * 
   * @param resName The name of the resource
   * @param user The {@link UserEntity}
   * @return the {@link AccessLevel} allowed for the specified user
   */
  public AccessLevel getAccessLevelFor(String resName, UserEntity user)
  {
    AccessLevel level = null;
    Enumeration<AccessEntry> aces = mAccessList.getAccessEntries(resName);
    while ((aces.hasMoreElements()) && (level == null))
    {
      AccessEntry ace = aces.nextElement();
      level = ace.getAccessLevelFor(user);
    }
    
    if (level == null) level = mAccessList.getDefaultAccessLevel();
    return level;
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
      .append(pad).append("  Id=").append(mId).append("\n")
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  EnabledAccessLevels=").append(mEnabledAccessLevels.toPrintableString()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
