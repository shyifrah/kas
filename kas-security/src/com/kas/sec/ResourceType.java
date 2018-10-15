package com.kas.sec;

import com.kas.infra.base.AKasObject;

public class ResourceType extends AKasObject
{
  private String mName;
  private String mDesc;
  private byte   mAccessLevels;
  
  ResourceType(String name, String desc, byte accessLevels)
  {
    mName = name;
    mDesc = desc;
    mAccessLevels = accessLevels;
  }
  
  public String getName()
  {
    return null;
  }
  
  public String getDescription()
  {
    return null;
  }
  
  public IAccessEntry getAccessEntry(String resource)
  {
    return null;
  }
  
  public byte getAccessLevels()
  {
    return 0;
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
}
