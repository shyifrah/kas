package com.kas.config.impl;

import com.kas.config.MainConfiguration;
import com.kas.infra.base.AKasObject;
import com.kas.infra.config.IListener;

public abstract class AConfiguration extends AKasObject implements IListener
{
  protected MainConfiguration mMainConfig = null;
  private   boolean           mInitialized = false;
  
  /***************************************************************************************************************
   * Terminate the configuration object
   * 
   */
  public void init()
  {
    mMainConfig = MainConfiguration.getInstance();
    mMainConfig.register(this);
    refresh();
    
    mInitialized = true;
  }
  
  /***************************************************************************************************************
   * Returns the configuration object's state.
   * 
   * @return true if the configuration object was initialized
   */
  public boolean isInitialized()
  {
    return mInitialized;
  }
  
  /***************************************************************************************************************
   * Refreshes the configuration object
   * 
   */
  public abstract void refresh();
  
  /***************************************************************************************************************
   * 
   */
  public abstract String toPrintableString(int level);
}
