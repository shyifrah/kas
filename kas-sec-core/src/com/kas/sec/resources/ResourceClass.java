package com.kas.sec.resources;

import com.kas.infra.base.AKasObject;
import com.kas.sec.access.AccessLevel;

/**
 * A {@link ResourceClass} is an object describing a class of resources that can be protected.<br>
 * <br>
 * A resource class consists of a name and description which identifies what are resources belonging
 * to this class and what they protect. The name has a maximum length of 32 characters and must be unique
 * among all defined resources classes.
 * The description is actually a free text, up to 256 characters.<br>
 * <br>
 * Each resource class consists of a protection flag as well. When the flag has a value of {@code false},
 * it means that resources of this class are not protected and that all access requests to them are permitted.
 * When the flag has a value of {@code true}, then access is permitted (or prohibited) based on an access-list
 * mechanism.<br>
 * <br>
 * The enabled access-levels is a list of access-levels that can be granted to a user on a resource.
 * 
 * @author Pippo
 */
public class ResourceClass extends AKasObject implements Comparable<ResourceClass>
{
  static private final int cMaxResourceNameLength = 32;
  static private final int cMaxResourceDescLength = 256;
  
  /**
   * The resource class name
   */
  private String mName;
  
  /**
   * The resource description
   */
  private String mDesc;
  
  /**
   * List of access levels
   */
  private AccessLevel mEnabledAccessLevels;
  
  /**
   * Is resource protected
   */
  private boolean mProtected;
  
  /**
   * Construct a {@link ResourceClass}
   * 
   * @param name The name of the resource
   * @param desc The description of the resource
   * @param accessLevels A list of logically-ORed access-levels
   * 
   * @throws RuntimeException if name or description are invalid
   */
  ResourceClass(String name, String desc, AccessLevel accessLevels)
  {
    if ((name == null) || (name.length() == 0) || (name.length() > cMaxResourceNameLength))
      throw new RuntimeException("Invalid resource class name. Null, empty or too long resource name: [" + name + "]");
    
    if ((desc != null) && (desc.length() > cMaxResourceDescLength))
      throw new RuntimeException("Invalid resource class description. Too long: [" + name + "]");
    
    mName = name;
    mDesc = desc;
    mEnabledAccessLevels = accessLevels;
    mProtected = true;
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
   * Get the description of the resource class
   * 
   * @return the description of the resource class
   */
  public String getDescription()
  {
    return mDesc;
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
   * Get the indication stating if this resource class is protected
   * 
   * @return the indication stating if this resource class is protected
   */
  public boolean isProtected()
  {
    return mProtected;
  }
  
  /**
   * Mark resources of this resource class as protected 
   */
  public void protect()
  {
    mProtected = true;
  }
  
  /**
   * Mark resources of this resource class as unprotected 
   */
  public void unprotect()
  {
    mProtected = false;
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
    return mName.compareTo(other.mName);
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
    return String.format("%32s %5s %s", mName, new Boolean(mProtected).toString(), mEnabledAccessLevels.toString());
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
      .append(pad).append("  Description=").append(mDesc).append("\n")
      .append(pad).append("  EnabledAccessLevels=").append(mEnabledAccessLevels.toPrintableString()).append("\n")
      .append(pad).append("  IsProtected=").append(mProtected).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
