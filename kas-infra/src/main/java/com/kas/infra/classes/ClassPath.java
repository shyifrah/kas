package com.kas.infra.classes;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link ClassPath} is a tool that can be used to query your class path.
 *  
 * @author Pippo
 */
public class ClassPath
{
  /**
   * Map of URLs to {@link ClassPathUrl} objects
   */
  private Map<String, ClassPathUrl> mUrls;
  
  /**
   * Construct the {@link ClassPath}.<br>
   * Note that the current implementation won't work on JDK9 and above
   * as {@link ClassLoader#getSystemClassLoader()} does not return a
   * {@link URLClassLoader} in these implementation of JDK.
   */
  public ClassPath()
  {
    mUrls = new HashMap<String, ClassPathUrl>();
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    URL [] urls = ((URLClassLoader)cl).getURLs();
    for (URL url : urls)
    {
      String file = url.getFile();
      try
      {
        file = URLDecoder.decode(url.getFile(), "UTF-8");
      }
      catch (UnsupportedEncodingException e) {}
      mUrls.put(file, ClassPathUrlFactory.createClassPathUrl(file));
    }
  }
  
  /**
   * Get a set of all classes names found in all the URLs.<br>
   * A call to this method will basically return the list of all classes in
   * the class path.
   *  
   * @return
   *   a set of all classes names
   */
  public Set<String> getClasses()
  {
    Set<String> classes = new HashSet<String>();
    for (Map.Entry<String, ClassPathUrl> entry : mUrls.entrySet())
      classes.addAll(entry.getValue().getClassMap().keySet());
    
    return classes;
  }
  
  /**
   * Get a set of all class names found in the url designated by the argument.
   *  
   * @return
   *   a set of all classes names in {@code url}
   */
  public Set<String> getUrlClasses(String url)
  {
    ClassPathUrl cpurl = mUrls.get(url);
    if (cpurl == null)
      cpurl = ClassPathUrlFactory.createClassPathUrl(url);
    
    return cpurl.getClassMap().keySet();
  }
  
  /**
   * Get a set of all class names found declared in package {@code pkg}.
   *  
   * @return
   *   a set of all classes names in package {@code pkg}
   */
  public Set<String> getPackageClasses(String pkg)
  {
    String path = pkg.replace('.', File.separatorChar);
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(path);
    return getUrlClasses(url.getFile());
  }
  
  /**
   * Get the object's string representation
   * 
   * @return
   *   the object's string representation
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("ClassPath:\n");
    for (ClassPathUrl cpurl : mUrls.values())
      sb.append("  ").append(cpurl.toString()).append('\n');
    
    return sb.toString();
  }
}
