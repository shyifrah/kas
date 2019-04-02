package com.kas.infra.classes;

public class ClassPathJarUrl extends ClassPathUrl
{
  ClassPathJarUrl(String url)
  {
    super(url);
  }
  
  protected void load()
  {
    mClassMap = ClassUtils.getJarClasses(mUrl);
  }
  
  public String toString()
  {
    return new StringBuffer()
        .append("jar=")
        .append(super.toString())
        .toString();
  }
}
