package com.kas.mq.internal;

import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.classes.ClassPath;

/**
 * A package registrar is a class that registers classes that reside
 * in the specified package. For each class, the method {@link #initClass(Class)}
 * is called
 * 
 * @author Pippo
 */
public abstract class PackageRegistrar<K, V>
{
  /**
   * Logger
   */
  static private Logger sLogger = LogManager.getLogger(PackageRegistrar.class);
  
  /**
   * A {@link TreeMap} to map verbs with the specific T object
   */
  protected Map<K, V> mRegistration = new TreeMap<K, V>();
  
  /**
   * The name of the package
   */
  protected String mPackageName;
  
  /**
   * Class path data extractor
   */
  protected ClassPath mClassPath;
  
  /**
   * Construct the registrar
   */
  protected PackageRegistrar(String pkg)
  {
    mPackageName = pkg;
    mClassPath = ClassPath.getInstance();
  }
  
  /**
   * This method is called for each class to determine if it should
   * be registered and if so, it should call {@link #register(Object, Object)}
   * 
   * @param cls
   *   The class that should be registered
   * @return
   *   {@code true} if {@code cls} should be registered, {@code false} otherwise
   */
  protected abstract void register(Class<?> cls);
  
  /**
   * This method maps {@code key} to {@code value}.
   * 
   * @param key
   *   The key
   * @param val
   *   The value
   */
  protected void register(K key, V val)
  {
    sLogger.debug("PackageRegistrar::register() - Key=[{}], Value=[{}]", key, val);
    synchronized (mRegistration)
    {
      mRegistration.put(key, val);
    }
  }
  
  /**
   * Initialize the factory
   */
  public void init()
  {
    sLogger.trace("PackageRegistrar::init() - IN");
    
    sLogger.trace("PackageRegistrar::init() - Getting list of classes in current package: {}", mPackageName);
    Map<String, Class<?>> classes = mClassPath.getPackageClasses(mPackageName, false);
    
    for (Map.Entry<String, Class<?>> entry : classes.entrySet())
    {
      String cn = entry.getKey();
      Class<?> cls = entry.getValue();
      
      sLogger.debug("PackageRegistrar::init() - Processing class [{}]", cn);
      register(cls);
    }
    
    sLogger.trace("PackageRegistrar::init() - OUT");
  }
}
