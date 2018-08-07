package com.kas.infra.config;

import java.util.Set;
import com.kas.infra.base.IInitializable;

/**
 * Main configuration object.<br>
 * <br>
 * This object is responsible for loading the properties from the configuration files as well
 * as refresh them when the files are changed.<br>
 * In addition, this object acts as a {@link IConfiguration} as well.
 * 
 * @author Pippo
 */
public interface IMainConfiguration extends IConfiguration, IInitializable
{
  /**
   * This method is invoked by a side-thread which is responsible to monitor the configuration files.<br>
   * <br>
   * When the thread detects that at least one of the configuration files was modified, it calls this method
   * in order to reload the properties from the updated configuration files.
   */
  public abstract void reload();
  
  /**
   * Get a set of all configuration files
   * 
   * @return a set of all configuration files
   */
  public abstract Set<String> getConfigFiles();
  
  /**
   * Get the fully-pathed configuration directory
   * 
   * @return the fully-pathed configuration directory
   */
  public abstract String getConfigDir();
  
  /**
   * Returns the {@link IMainConfiguration} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link IMainConfiguration}.
   * 
   * @return a replica of this {@link IMainConfiguration}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IMainConfiguration replicate();
  
  /**
   * Returns the {@link IMainConfiguration} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
