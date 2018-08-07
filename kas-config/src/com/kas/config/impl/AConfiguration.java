package com.kas.config.impl;

import com.kas.config.MainConfiguration;
import com.kas.infra.base.AKasObject;
import com.kas.infra.config.IListener;

/**
 * An abstract configuration object.
 * 
 * @author Pippo
 */
public abstract class AConfiguration extends AKasObject implements IListener
{
  /**
   * The {@link MainConfiguration} object
   */
  protected MainConfiguration mMainConfig = null;
  
  /**
   * A boolean stating if this {@link AConfiguration} object was initialized or not
   */
  private boolean mInitialized = false;
  
  /**
   * Initialize the configuration object
   */
  public void init()
  {
    mMainConfig = MainConfiguration.getInstance();
    mMainConfig.register(this);
    refresh();
    
    mInitialized = true;
  }
  
  /**
   * Returns the configuration object's state.
   * 
   * @return true if the configuration object was initialized
   */
  public boolean isInitialized()
  {
    return mInitialized;
  }
  
  /**
   * Refreshes the configuration object
   * 
   * @see com.kas.infra.config.IListener#refresh()
   */
  public abstract void refresh();
  
  /**
   * Returns a replica of this {@link AConfiguration}.
   * 
   * @return a replica of this {@link AConfiguration}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract AConfiguration replicate();
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
