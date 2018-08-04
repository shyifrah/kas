package com.kas.infra.base.threads;

import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * A KasRunnableThread is a Thread which accepts the Runnable object that will be executed
 * upon calling to run() method
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
   * @param name The {@code Thread} name
   * @param cmd The {@code Runnable} object
   */
  public KasRunnableThread(String name, Runnable cmd)
  {
    super(cmd, name);
    mCommand = cmd;
  }
  
  /**
   * Returns a replica of this {@link #KasRunnableThread}.
   * 
   * @return a replica of this {@link #KasRunnableThread}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public KasRunnableThread replicate()
  {
    return new KasRunnableThread(getName(), mCommand);
  }
  
  /**
   * Returns the {@link #KasRunnableThread} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public String name()
  {
    StringBuffer sb = new StringBuffer();
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
