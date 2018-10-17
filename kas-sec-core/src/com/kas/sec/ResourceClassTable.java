package com.kas.sec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AKasObject;

public class ResourceClassTable extends AKasObject
{
  private Map<String, ResourceClass> mTable;
  
  public ResourceClassTable()
  {
    mTable = new ConcurrentHashMap<String, ResourceClass>();
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
}
