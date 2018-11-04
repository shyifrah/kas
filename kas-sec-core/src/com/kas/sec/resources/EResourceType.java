package com.kas.sec.resources;

import com.kas.sec.access.AccessLevel;

public enum EResourceType
{
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
