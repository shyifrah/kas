package com.kas.sec.resources;

import com.kas.sec.access.AccessLevel;

public enum EResourceType
{
  APPLICATION,
  QUEUE,
  ;
  
  static private EResourceType [] cValues = EResourceType.values();
  
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id The ordinal of the enum
   * @return the enum value
   */
  static public EResourceType fromInt(int id)
  {
    EResourceType t = null;
    try
    {
      t = cValues[id];
    }
    catch (Throwable e) {}
    return t;
  }
  
  /**
   * The resource class
   */
  private ResourceClass mResourceClass;
  
  /**
   * Construct the Resource type
   */
  private EResourceType()
  {
    mResourceClass = new ResourceClass(this.ordinal(), this.name(), AccessLevel.READ);
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
