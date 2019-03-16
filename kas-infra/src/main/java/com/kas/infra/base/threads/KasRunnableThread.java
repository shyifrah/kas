package com.kas.infra.base.threads;

import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * A KasRunnableThread is a Thread which accepts a {@link Runnable} object to be executed
 * upon calling to run() method
 * 
 * @author Pippo
 */
public class KasRunnableThread extends Thread implements IObject
{
  /**
   * The {@code Runnable} object to be handled by this {@code Thread}
   */
  private Runnable mCommand;
  
  /**
   * Construct a {@link #KasRunnableThread} with the given name and {@code Runnable}
   * 
   * @param name
   *   The Thread's name
   * @param cmd
   *   The {@code Runnable} object
   */
  public KasRunnableThread(String name, Runnable cmd)
  {
    super(cmd, name);
    mCommand = cmd;
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
  public String toPrintableString(int level)
  {
    String pad = StringUtils.getPadding(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  ").append("Name=").append(getName()).append("\n")
      .append(pad).append("  ").append("Runnable=[").append(mCommand.toString()).append("]\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
