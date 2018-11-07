package com.kas.infra.base;

import com.kas.infra.utils.StringUtils;

/**
 * A KAS managed object.<br>
 * <br>
 * This class implements some of the methods that described in {@link IObject} 
 * 
 * @author Pippo
 * 
 * @see com.kas.infra.base.IObject
 */
public abstract class AKasObject implements IObject
{
  /**
   * Override Object's toString() method with the {@link #name()} method.
   * 
   * @return Object's string representation.
   */
  public String toString()
  {
    return name();
  }
  
  /**
   * Generate a padding for members layout to be used by {@link #toPrintableString(int)}.
   * 
   * @return padding string
   */
  protected String pad(int level)
  {
    return StringUtils.getPadding(level);
  }
  
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public String name()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
  }
  
  /**
   * Returns the {@link IObject} string representation with 0-padding level
   * 
   * @return the string representation with 0-padding level
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString()
  {
    return toPrintableString(0);
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level the required level padding
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}