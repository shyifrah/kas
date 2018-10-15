package com.kas.sec;

import com.kas.infra.base.IObject;

public interface IResourceType extends IObject
{
  public abstract String getName();
  public abstract String getDescription();
  public abstract IAccessEntry getAccessEntry(String resource);
  public abstract byte getAccessLevels();
}
