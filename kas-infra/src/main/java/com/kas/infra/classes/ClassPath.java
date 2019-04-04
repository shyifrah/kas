package com.kas.infra.classes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link ClassPath} is a tool that can be used to query your class path.
 *  
 * @author Pippo
 */
public class ClassPath
{
  /**
   * Logger
   */
  static private Logger sLogger = LogManager.getLogger(ClassPath.class);
  
  /**
   * The singleton instance
   */
  static private ClassPath sInstance = new ClassPath();
  
  /**
   * Get the singleton instance
   * 
   * @return
   *   the singleton instance
   */
  static public ClassPath getInstance()
  {
    return sInstance;
  }
  
  /**
   * Map of URLs to {@link ClassPathUrl} objects
   */
  private Map<String, ClassPathUrl> mUrls = null;
  
  /**
   * Private constructor
   */
  private ClassPath()
  {
  }
  
  /**
   * Initialize the ClassPath object
   */
  private void init()
  {
    sLogger.trace("ClassPath::init() - IN");
    
    mUrls = new HashMap<String, ClassPathUrl>();
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    URL [] urls = ((URLClassLoader)cl).getURLs();
    for (URL url : urls)
    {
      sLogger.trace("ClassPath::init() - Processing URL=[{}]", url);
      
      String path = ClassUtils.getUrlPath(url);
      
      File f = new File(path);
      if (!f.exists())
      {
        sLogger.trace("ClassPath::init() - URL points to a non-existent resource, skipping");
      }
      else
      {
        String absPath = f.getAbsolutePath();
        sLogger.trace("ClassPath::init() - URL points to {}", absPath);
        
        mUrls.put(absPath, ClassPathUrlFactory.createClassPathUrl(absPath));
      }
    }
    
    sLogger.trace("ClassPath::init() - OUT");
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
    sLogger.trace("ClassPath::getClasses() - IN");
    
    if (mUrls == null) init();
    
    Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    for (Map.Entry<String, ClassPathUrl> entry : mUrls.entrySet())
      classes.putAll(entry.getValue().getClassMap());
    
    sLogger.trace("ClassPath::getClasses() - OUT, loaded {} classes", classes.size());
    return classes;
  }
  
  /**
   * Get a map of all classes found in {@code path}.
   * <br>
   * If the path is a valid key to the URLs map, then that ClassPathUrl's map is returned.
   * <br>
   * If the path is not a valid key, it's possible that one of the ancestors of the path
   * is a key, so we start trimming qualifiers from the path until we find a valid key.
   * We take the ClassPathUrl's class map mapped to that key, and filtering all classes
   * not belong to {@code path}.
   * <br> 
   * If we still can't find a valid entry in the URLs map, we simply create a brand new
   * ClassPathUrl and return its map.
   *  
   * @return
   *   a map of all class names to classes located in {@code path} or one of its
   *   parent directories.
   */
  public Map<String, Class<?>> getPathClasses(String path)
  {
    sLogger.trace("ClassPath::getPathClasses() - IN, Path=[{}]", path);
    if (mUrls == null) init();
    
    Map<String, Class<?>> map;
    
    String currPath = path;
    ClassPathUrl cpurl = mUrls.get(currPath);
    if (cpurl != null)
    {
      map = cpurl.getClassMap();
    }
    else
    {
      while (cpurl == null)
      {
        int dirSepIndex = currPath.lastIndexOf(File.separatorChar);
        if (dirSepIndex == -1)
          break;
        currPath = currPath.substring(0, dirSepIndex);
        cpurl = mUrls.get(currPath);
      }
      
      if (cpurl == null)
        cpurl = ClassPathUrlFactory.createClassPathUrl(path);
      
      map = cpurl.getClassMap();
    }
    
    sLogger.trace("ClassPath::getPathClasses() - OUT, Total of {} classes", map.size());
    return map;
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
    if (mUrls == null) init();
    
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
    sLogger.trace("ClassPath::getPackageClasses() - IN, Pkg=[{}], IncludeSub=[{}]", pkg, includeSubPackages);
    if (mUrls == null) init();
    
    Map<String, Class<?>> result = new HashMap<String, Class<?>>();
    
    String path = pkg.replace('.', '/');
    sLogger.trace("ClassPath::getPackageClasses() - Pkg=[{}] mapped to Resource Path=[{}]", pkg, path);
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
        
        File f = new File(urlPath);
        String urlAbsPath = f.getAbsolutePath();
        sLogger.trace("ClassPath::getPackageClasses() - URL=[{}] absolute path [{}]", url, urlAbsPath);
        
        Map<String, Class<?>> thisUrlMap = getPathClasses(urlAbsPath);
        
        for (Map.Entry<String, Class<?>> entry : thisUrlMap.entrySet())
        {
          String key = entry.getKey();
          Class<?> val = entry.getValue();
          
          if (includeSubPackages)
          {
            if ((key.lastIndexOf('.') >= pkg.length()) && (key.startsWith(pkg)))
              result.put(key, val);
          }
          else
          {
            if ((key.lastIndexOf('.') == pkg.length()) && (key.startsWith(pkg)))
              result.put(key, val);
          }
        }
      }
    }
    
    sLogger.trace("ClassPath::getPackageClasses() - OUT, loaded {} classes", result.size());
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
    if (mUrls == null) init();
    
    StringBuffer sb = new StringBuffer();
    sb.append("ClassPath:\n");
    for (ClassPathUrl cpurl : mUrls.values())
      sb.append("  ").append(cpurl.toString()).append('\n');
    
    return sb.toString();
  }
}
