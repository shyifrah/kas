package com.kas.sec.resources;

import com.kas.sec.access.AccessLevel;

/**
 * A {@link IResourceClass} is an object describing a class of resources that can be protected.<br>
 * <br>
 * A resource class consists of a name which describes what are resources belonging
 * to this class and what they protect. The name has a maximum length of 32 characters and must be unique
 * among all defined resources classes.<br>
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
public interface IResourceClass extends Comparable<IResourceClass>
{
  /**
   * Get the ID of the resource class
   * 
   * @return the ID of the resource class
   */
  public abstract int getId();
  
  /**
   * Get the name of the resource class
   * 
   * @return the name of the resource class
   */
  public abstract String getName();
  
  /**
   * Get the access levels that are enabled for this resource class
   * 
   * @return the access levels that are enabled for this resource class
   */
  public abstract AccessLevel getEnabledAccessLevels();
  
  /**
   * Compare to another resource class
   * 
   * @return the value returned by {@link String#compareTo(String)}
   * 
   * @see Comparable#compareTo(Object)
   */
  public abstract int compareTo(IResourceClass other);
  
  /**
   * Compare two Objects for equality
   * 
   * @return the value returned by {@link String#equals(Object)}
   * 
   * @see Comparable#equals(Object)
   */
  public abstract boolean equals(Object other);
  
  /**
   * Get the hash code of the resource class
   * 
   * @return the value returned by {@link String#hashCode()}
   * 
   * @see Comparable#hashCode()}
   */
  public abstract int hashCode();
}
