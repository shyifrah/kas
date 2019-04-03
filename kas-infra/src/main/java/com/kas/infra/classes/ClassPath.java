package com.kas.infra.classes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * {@link ClassPath} is a tool that can be used to query your class path.
 *  
 * @author Pippo
 */
public class ClassPath
{
  /**
   * A predicate to remove from a {@link Set} all classes
   * that should not be in it. In this case, all classes that
   * are contained in subpackages of a designated package
   * are filtered out.
   */
  static class SubPackageClassFilter implements Predicate<String>
  {
    private String mPackagePrefix;
    private int    mLastPeriodIndex;
    
    SubPackageClassFilter(String pkg)
    {
      mPackagePrefix = pkg + '.';
      mLastPeriodIndex = pkg.length();
    }
    
    public boolean test(String className)
    {
      if ((className.startsWith(mPackagePrefix)) && (className.lastIndexOf('.') == mLastPeriodIndex))
        return false;
      return true;
    }
  }
  
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
   * Get a set of all class names declared in package {@code pkg}
   * and its subpackages.
   *  
   * @param pkg
   *   The name of the package
   * @return
   *   a set of all classes names in package {@code pkg}
   */
  public Set<String> getPackageClasses(String pkg)
  {
    return getPackageClasses(pkg, true);
  }
  
  /**
   * Get a set of all class names declared in package {@code pkg}.
   * Since the package can be defined in various JARs/directories,
   * we need to scan all of them.
   *  
   * @param pkg
   *   The name of the package
   * @param includeSubPackages
   *   Whether the set of class names will include classes defined
   *   in {@code pkg} subpackages
   * @return
   *   a set of all classes names in package {@code pkg}
   */
  public Set<String> getPackageClasses(String pkg, boolean includeSubPackages)
  {
    Set<String> result = new HashSet<String>();
    
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
          Set<String> thisUrlSet = getUrlClasses(f.getAbsolutePath());
          result.addAll(thisUrlSet);
        }
      }
    }
    
    if (!includeSubPackages)
    {
      Predicate<String> filter = new SubPackageClassFilter(pkg);
      result.removeIf(filter);
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
