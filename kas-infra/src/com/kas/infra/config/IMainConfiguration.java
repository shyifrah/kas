package com.kas.infra.config;

import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;
import com.kas.infra.typedef.StringList;

/**
 * Main configuration object.<br>
 * <br>
 * This object is responsible for loading the properties from the configuration files as well
 * as refresh them when the files are changed.<br>
 * In addition, this object acts as a {@link IConfiguration} as well.
 * 
 * @author Pippo
 */
public interface IMainConfiguration extends IConfiguration, IInitializable, IObject
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
  public abstract StringList getConfigFiles();
  
  /**
   * Get the fully-pathed configuration directory
   * 
   * @return the fully-pathed configuration directory
   */
  public abstract String getConfigDir();
}
