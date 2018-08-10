package com.kas.mq.appl.cli;

import com.kas.infra.base.IObject;

/**
 * A general purpose command processed by the {@link MqAdminProcessor}
 * 
 * @author Pippo
 */
public interface ICliCommand extends IObject
{
  /**
   * Running the command.
   * 
   * @return {@code true} if {@link MqAdminProcessor} should stop further processing following
   * the processing of this command, {@code false} otherwise
   */
  public abstract boolean run();
  
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link IObject}.
   * 
   * @return a replica of this {@link IObject}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IObject replicate();
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
