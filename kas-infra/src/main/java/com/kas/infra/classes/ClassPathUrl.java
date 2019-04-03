package com.kas.infra.classes;

import java.util.Map;

public abstract class ClassPathUrl implements IUrl
{
  protected String mUrl;
  protected Map<String, Class<?>> mClassMap;
  
  ClassPathUrl(String url)
  {
    int jarPackagePos = url.lastIndexOf(".jar!");
    if (jarPackagePos > 0)
      mUrl = url.substring(0, jarPackagePos+4); // cut everything follows the ! including
    else
      mUrl = url;
    
    load();
  }
  
  protected abstract void load();
  
  public String getUrl()
  {
    return mUrl;
  }
  
  public Map<String, Class<?>> getClassMap()
  {
    return mClassMap;
  }
  
  public boolean equals(Object other)
  {
    if (other instanceof ClassPathUrl)
    {
      ClassPathUrl cpurl = (ClassPathUrl)other;
      return mUrl.equals(cpurl.getUrl());
    }
    return false;
  }
  
  public String toString()
  {
    return mUrl;
  }
}
