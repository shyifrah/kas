package com.kas.infra.base;

/**
 * Base KAS object
 * 
 * @author Pippo
 */
public interface IObject
{
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return
   *   class name enclosed with chevrons.
   */
  public abstract String name();
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public abstract String toPrintableString(int level);
}
