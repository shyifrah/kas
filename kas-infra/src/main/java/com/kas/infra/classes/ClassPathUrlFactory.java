package com.kas.infra.classes;

import java.io.File;

public class ClassPathUrlFactory
{
  static public ClassPathUrl createClassPathUrl(String url)
  {
    File f = new File(url);
    if (f.isDirectory())
      return new ClassPathDirUrl(url);
    else
      return new ClassPathJarUrl(url);
  }
}
