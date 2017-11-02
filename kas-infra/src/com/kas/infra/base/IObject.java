package com.kas.infra.base;

public interface IObject
{
  /****
   * Returns the {@link #IObject} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public String name();
  
  /****
   * Returns the {@link #IObject} string representation.
   * 
   * @param level the required level padding
   */
  public String toPrintableString(int level);
}
