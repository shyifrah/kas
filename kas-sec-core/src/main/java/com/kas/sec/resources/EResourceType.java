package com.kas.sec.resources;

import com.kas.sec.access.AccessLevel;

/**
 * This ENUM is used to simplify the the code when requesting to check
 * permissions for a user against a specific resource.<br>
 * 
 * The values of this ENUM <b>must</b> match the contents of {@code table kas_mq_resource_types}.
 * @author Pippo
 */
public enum EResourceType
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
  private EResourceType(int enabledAccessLevels)
  {
    mResourceClass = new ResourceClass(this.ordinal(), this.name(), enabledAccessLevels);
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
