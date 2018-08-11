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
  public synchronized void init()
  {
    mMainConfig = MainConfiguration.getInstance();
    if (!mMainConfig.isInitialized())
    {
      mMainConfig.init();
    }
    mMainConfig.register(this);
    refresh();
    
    mInitialized = true;
  }
  
  /**
   * Terminate the configuration object
   */
  public synchronized void term()
  {
    mMainConfig.unregister(this);
    mInitialized = false;
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
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
