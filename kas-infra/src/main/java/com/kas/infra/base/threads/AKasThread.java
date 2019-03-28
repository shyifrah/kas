package com.kas.infra.base.threads;

import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * AKasThread is an abstract Thread which allows driven classes to implement their own version
 * of the run() method that will be executed.
 * 
 * @author Pippo
 */
public abstract class AKasThread extends Thread implements IObject
{
  /**
   * Construct a {@link #KasThread} with the given name
   * 
   * @param name
   *   The Thread's name
   */
  public AKasThread(String name)
  {
    super(name);
  }
  
  /**
   * Generate a padding for members layout to be used by {@link #toPrintableString(int)}.
   * 
   * @param level
   *   The required padding level
   * @return
   *   padding string
   */
  protected String pad(int level)
  {
    return StringUtils.getPadding(level);
  }
  
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return
   *   class name enclosed with chevrons.
   */
  public String name()
  {
    return StringUtils.getClassName(getClass());
  }
  
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