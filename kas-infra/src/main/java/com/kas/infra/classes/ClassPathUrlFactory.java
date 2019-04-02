package com.kas.infra.classes;

import java.io.File;

public class ClassPathUrlFactory
{
  static public ClassPathUrl createClassPathUrl(String url)
  {
    File f = new File(url);
    if (f.isFile())
      return new ClassPathJarUrl(url);
    else
      return new ClassPathDirUrl(url);
  }
}
