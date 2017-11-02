package com.kas.infra.config;

import com.kas.infra.base.IObject;

public interface IConfiguration extends IObject
{
  public abstract String  getStringProperty(String key, String  defaultValue);
  public abstract int     getIntProperty   (String key, int     defaultValue);
  public abstract long    getLongProperty  (String key, long    defaultValue);
  public abstract boolean getBoolProperty  (String key, boolean defaultValue);
  
  public abstract IConfiguration getSubset(String key);
}
