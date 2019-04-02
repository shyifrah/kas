package com.kas.infra.classes;

public class ClassPathDirUrl extends ClassPathUrl
{
  ClassPathDirUrl(String url)
  {
    super(url);
  }
  
  protected void load()
  {
    mClassMap = ClassUtils.getDirClasses(mUrl, mUrl);
  }
  
  public String toString()
  {
    return new StringBuffer()
        .append("dir=")
        .append(super.toString())
        .toString();
  }
}
