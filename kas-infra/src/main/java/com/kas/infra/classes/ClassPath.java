package com.kas.infra.classes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
      String path = ClassUtils.getUrlPath(url);
      
      File f = new File(path);
      String absPath = f.getAbsolutePath();
      mUrls.put(absPath, ClassPathUrlFactory.createClassPathUrl(absPath));
    }
  }
  
  /**
   * Get a map of all classes found in all the URLs.<br>
   * A call to this method will basically return the list of all classes in
   * the class path.
   *  
   * @return
   *   a map of all class names to respective classes
   */
  public Map<String, Class<?>> getClasses()
  {
    Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    for (Map.Entry<String, ClassPathUrl> entry : mUrls.entrySet())
      classes.putAll(entry.getValue().getClassMap());
    
    return classes;
  }
  
  /**
   * Get a map of all classes found in the URL designated by the argument.
   *  
   * @return
   *   a map of all class names to classes located in {@code url}
   */
  public Map<String, Class<?>> getUrlClasses(String url)
  {
    ClassPathUrl cpurl = mUrls.get(url);
    if (cpurl == null)
      cpurl = ClassPathUrlFactory.createClassPathUrl(url);
    
    return cpurl.getClassMap();
  }
  
  /**
   * Get a map of all classes declared in package {@code pkg} and its subpackages.
   *  
   * @param pkg
   *   The name of the package
   * @return
   *   a map of all class names to classes declared in package {@code pkg} or its subpackages
   *   
   * @see #getPackageClasses(String, boolean)
   */
  public Map<String, Class<?>> getPackageClasses(String pkg)
  {
    return getPackageClasses(pkg, true);
  }
  
  /**
   * Get a map of all classes declared in package {@code pkg}.<br>
   * Since the package can be defined in various JARs/directories, we need to scan all of them.
   *  
   * @param pkg
   *   The name of the package
   * @param includeSubPackages
   *   Whether the map of class names should include classes declared
   *   in {@code pkg} subpackages
   * @return
   *   a map of all class names to classes declared in package {@code pkg}
   */
  public Map<String, Class<?>> getPackageClasses(String pkg, boolean includeSubPackages)
  {
    Map<String, Class<?>> result = new HashMap<String, Class<?>>();
    
    String path = pkg.replace('.', '/');
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Enumeration<URL> enumurls = null;
    
    try
    {
      enumurls = loader.getResources(path);
    }
    catch (IOException e) {}
    
    if (enumurls != null)
    {
      while (enumurls.hasMoreElements())
      {
        URL url = enumurls.nextElement();
        String urlPath = ClassUtils.getUrlPath(url);
        if (urlPath != null)
        {
          File f = new File(urlPath);
          Map<String, Class<?>> thisUrlMap = getUrlClasses(f.getAbsolutePath());
          result.putAll(thisUrlMap);
        }
      }
    }
    
    if (!includeSubPackages)
    {
      Iterator<Map.Entry<String, Class<?>>> iter = result.entrySet().iterator();
      while (iter.hasNext())
      {
          Map.Entry<String, Class<?>> entry = iter.next();
          String cn = entry.getKey();
          if ((cn.startsWith(pkg)) && (cn.lastIndexOf('.') == pkg.length()))
              iter.remove();
      }
    }
    return result;
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
