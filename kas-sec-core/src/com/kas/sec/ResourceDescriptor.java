package com.kas.sec;

import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.AKasObject;

public class ResourceDescriptor extends AKasObject
{
  private String mName;
  private String mDesc;
  private AccessLevel mAccessLevels;
  private boolean mProtected;
  private List<AccessEntry> mAccessList;
  private AccessEntry mDefaultAccessEntry;
  
  ResourceDescriptor(String name, String desc, byte accessLevels)
  {
    this(name, desc, new AccessLevel(accessLevels));
  }
  
  ResourceDescriptor(String name, String desc, AccessLevel accessLevels)
  {
    this(name, desc, accessLevels, null);
  }
  
  ResourceDescriptor(String name, String desc, AccessLevel accessLevels, AccessEntry defaultAccessEntry)
  {
    mName = name;
    mDesc = desc;
    mAccessLevels = accessLevels;
    mDefaultAccessEntry = defaultAccessEntry;
    mProtected = true;
    mAccessList = new ArrayList<AccessEntry>();
  }
  
  public String getName()
  {
    return mName;
  }
  
  public String getDescription()
  {
    return mDesc;
  }
  
  public AccessLevel getAccessLevels()
  {
    return mAccessLevels;
  }
  
  public boolean isProtected()
  {
    return mProtected;
  }
  
  public void protect()
  {
    mProtected = true;
  }
  
  public void unprotect()
  {
    mProtected = false;
  }
  
  public AccessEntry getAccessEntry(String resource)
  {
    for (AccessEntry ace : mAccessList)
    {
      if (ace.isMached(resource))
        return ace;
    }
    return mDefaultAccessEntry;
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
}
