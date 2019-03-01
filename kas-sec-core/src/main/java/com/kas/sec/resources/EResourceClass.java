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
   * Protects server shutdown.
   * READ:  allows to shutdown server.
   */
  SERVER(AccessLevel.READ),
  
  /**
   * Protects from commands issued
   * READ:  allows to issue the specified command.
   */
  COMMAND(AccessLevel.READ),
  
  /**
   * Protects the usage of specific application
   * READ:  allows to login from the specified application.
   */
  APPLICATION(AccessLevel.READ),
  
  /**
   * Protects queues
   * READ:  allows to read messages from specified queue.
   * WRITE: allows to write messages to specified queue, but not read.
   * ALTER: allows to define new queues, delete existing queues, or alter queues properties.
   */
  QUEUE(AccessLevel.READ | AccessLevel.WRITE | AccessLevel.ALTER),
  
  /**
   * Protects users
   * READ:  allows to query user entities.
   * WRITE: allows to create, alter or delete, but not query user entities.
   */
  USER(AccessLevel.READ | AccessLevel.WRITE),
  
  /**
   * Protects queues
   * READ:  allows to query group entities.
   * WRITE: allows to create, alter or delete, but not query group entities.
   */
  GROUP(AccessLevel.READ | AccessLevel.WRITE),
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
