package com.kas.sec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AKasObject;

public class ResourceTable extends AKasObject
{
  private Map<String, IResourceType> mTable;
  
  public ResourceTable()
  {
    mTable = new ConcurrentHashMap<String, IResourceType>();
  }
  
  public String toPrintableString(int level)
  {
    
    return null;
  }
}
