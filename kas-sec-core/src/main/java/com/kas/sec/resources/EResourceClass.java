package com.kas.sec.resources;

import com.kas.sec.access.AccessLevel;

/**
 * This ENUM is used to simplify the the code when requesting to check
 * permissions for a user against a specific resource.<br>
 * 
 * @author Pippo
 */
public enum EResourceClass
{
  /**
   * Unknown resource type
   */
  UNKNOWN(AccessLevel.NONE),
  
  /**
   * Protects server shutdown
   */
  SERVER(AccessLevel.READ),
  
  /**
   * Protects server shutdown
   */
  COMMAND(AccessLevel.READ),
  
  /**
   * Protects the usage of specific application
   */
  APPLICATION(AccessLevel.READ),
  
  /**
   * Protects queues
   */
  QUEUE(AccessLevel.READ | AccessLevel.WRITE | AccessLevel.ALTER),
  ;
  
  /**
   * The resource class
   */
  private ResourceClass mResourceClass;
  
  /**
   * Construct the Resource type
   */
  private EResourceClass(int enabledAccessLevels)
  {
    if (enabledAccessLevels > 0)
      mResourceClass = new ResourceClass(this.ordinal(), this.name(), enabledAccessLevels);
    else
      mResourceClass = null;
  }
  
  /**
   * Get the resource class
   * 
   * @return the resource class
   */
  public ResourceClass getResourceClass()
  {
    return mResourceClass;
  }
}
