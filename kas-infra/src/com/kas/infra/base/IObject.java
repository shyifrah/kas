package com.kas.infra.base;

public interface IObject
{
  /**
   * Returns the {@link #IObject} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public String name();
  
  /**
   * Returns a replica of this {@link #IObject}.
   * 
   * @return a replica of this {@link #IObject}
   */
  public IObject replicate();
  
  /**
   * Returns the {@link #IObject} string representation.
   * 
   * @param level the required level padding
   * 
   * @return the object's printable string representation
   */
  public String toPrintableString(int level);
}
