package com.kas.infra.classes;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility methods to get a bunch of classes, all reside
 * in the same JAR or the same directory.
 * 
 * @author Pippo
 */
public class ClassUtils
{
  /**
   * @see #getDirClasses(String, String)
   */
  static public Map<String, Class<?>> getDirClasses(String path)
  {
    return getDirClasses(path, path);
  }
  
  /**
   * Get, recursively, a map of class names to classes located in a specific directory.
   * Note that this method does not make sure that {@code path} is actually a directory.
   * It does require, however, that {@code initPath} to be the root directory of the
   * package. Otherwise, an empty map will be returned
   * 
   * @param initPath
   *   The initial path used to locate classes
   * @param path
   *   The current invocation's path
   * @return
   *   An empty map if no classes found, or a map of class names to classes of all
   *   classes that were located in {@code initPath}
   */
  static public Map<String, Class<?>> getDirClasses(String initPath, String path)
  {
    Map<String, Class<?>> result = new HashMap<String, Class<?>>();
    
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    
    File dir = new File(path);
    File [] files = dir.listFiles();
    
    for (File file : files)
    {
      if (file.isDirectory())
      {
        Map<String, Class<?>> subDirMap = getDirClasses(initPath, file.getPath());
        result.putAll(subDirMap);
      }
      else if (file.getPath().endsWith(".class"))
      {
        String className = file.getPath().substring(initPath.length() + 1);
        className = className.substring(0, className.lastIndexOf('.')).replace(File.separatorChar, '.');
        
        Class<?> thisClass = ClassUtils.loadClass(loader, className);
        if (thisClass != null)
          result.put(className, thisClass);
      }
    }
    
    return result;
  }
  
  /**
   * Get a map of class names to classes packed in a JAR file.
   * Note that this method does not make sure {@code path} is actually a JAR
   * 
   * @param path
   *   The full path to the JAR file
   * @return
   *   An empty map if no classes found, or a map of class names to classes of all
   *   classes that were located in {@code path}
   */
  static public Map<String, Class<?>> getJarClasses(String path)
  {
    Map<String, Class<?>> result = new HashMap<String, Class<?>>();
    
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    
    JarFile jar = null;
    try
    {
      jar = new JarFile(path);
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements())
      {
        JarEntry entry = entries.nextElement();
        String name = entry.getName();
        if (name.endsWith(".class"))
        {
          String className = name.substring(0, name.lastIndexOf('.')).replace('/', '.');
          Class<?> thisClass = ClassUtils.loadClass(loader, className);
          if (thisClass != null)
            result.put(className, thisClass);
        }
      }
    }
    catch (IOException | SecurityException e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (jar != null)
          jar.close();
      }
      catch (Throwable e) {}
    }
    
    return result;
  }
  
  /**
   * Load a class using ClassPath's class loader
   * 
   * @param className
   *   The name of the class to load
   * @return
   *   The class loaded or null if failed to load
   */
  static public Class<?> loadClass(ClassLoader loader, String className)
  {
    Class<?> thisClass = null;
    try
    {
      thisClass = Class.forName(className, false, loader);
    }
    catch (Throwable e) {}
    return thisClass;
  }
  
  static public String getUrlPath(URL url)
  {
    if (url == null)
      return null;
    
    String urlPath = url.getPath();
    String urlProtocol = url.getProtocol();
    if ("jar".equals(urlProtocol))
      urlPath = urlPath.substring(0, urlPath.lastIndexOf('!'));
    try
    {
      URL newUrl = new URL(urlPath);
      return getUrlPath(newUrl);
    }
    catch (MalformedURLException e) {}
    
    try
    {
      urlPath = URLDecoder.decode(urlPath, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {}
    return urlPath;
  }
}
