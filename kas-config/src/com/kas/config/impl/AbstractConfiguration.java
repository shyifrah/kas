package com.kas.config.impl;

import com.kas.config.MainConfiguration;
import com.kas.infra.base.KasObject;
import com.kas.infra.config.IListener;

public abstract class AbstractConfiguration extends KasObject implements IListener
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected MainConfiguration mMainConfig = null;
  private   boolean           mInitialized = false;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void init()
  {
    mMainConfig = MainConfiguration.getInstance();
    mMainConfig.register(this);
    refresh();
    
    mInitialized = true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean isInitialized()
  {
    return mInitialized;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public abstract void refresh();
  public abstract String toPrintableString(int level);
}
