package com.kas.infra.base.threads;

import com.kas.infra.base.IObject;

/**
 * AKasThread is an abstract Thread which allows driven classes to implement their own version
 * of the run() method that will be executed.
 */
public abstract class AKasThread extends Thread implements IObject
{
  /**
   * Construct a {@link #KasThread} with the given name
   * 
   * @param name The {@code Thread} name
   */
  public AKasThread(String name)
  {
    super(name);
  }
  
  /**
   * Returns a replica of this {@link #AKasThread}.
   * 
   * @return a replica of this {@link #AKasThread}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract AKasThread replicate();
  
  /**
   * Returns the {@link #KasThread} simple class name enclosed with chevrons.
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
   * Get the thread's detailed string representation.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}