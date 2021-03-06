package com.kas.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.config.impl.ConfigProperties;
import com.kas.config.impl.ConfigTask;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.config.IBaseListener;
import com.kas.infra.config.IBaseRegistrar;
import com.kas.infra.config.IConfiguration;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.typedef.StringList;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;

/**
 * The {@link MainConfiguration} object class.<br>
 * <br>
 * This is a singleton class.
 * 
 * @author Pippo
 */
final public class MainConfiguration extends AKasObject implements IMainConfiguration, IBaseRegistrar 
{
  static private final long   cDefaultMonitoringDelay    = 10000L;
  static private final long   cDefaultMonitoringInterval = 10000L;
  
  static private final String cMainConfigFileName = "kas.properties";
  static private final String cConfigPropPrefix   = "kas.config.";
  
  static private Logger sLogger = LogManager.getLogger(MainConfiguration.class);
  
  /**
   * The {@link MainConfiguration} singleton instance
   */
  static private MainConfiguration sInstance = new MainConfiguration();
  
  /**
   * A boolean stating if this configuration object was initialized
   */
  private boolean mInitialized = false;
  
  /**
   * The configuration directory
   */
  private String mConfigDir = null;
  
  /**
   * A collection of {@link IBaseListener} objects that are listening for configuration changes
   */
  private Set<IBaseListener> mListeners = new HashSet<IBaseListener>();
  
  /**
   * A {@link ConfigProperties} object holding all KAS properties read from configuration files
   */
  private ConfigProperties mProperties = null;
  
  /**
   * A {@link ConfigTask} which is responsible for monitoring configuration files for changes
   */
  private ConfigTask mConfigTask = null;
  
  /**
   * How many milliseconds to delay configuration changes monitoring
   */
  private long mConfigMonitoringDelay = cDefaultMonitoringDelay;
  
  /**
   * Interval to wait before subsequent monitoring operation
   */
  private long mConfigMonitoringInterval = cDefaultMonitoringInterval;
  
  /**
   * Get the singleton
   * 
   * @return
   *   the {@link MainConfiguration} singleton instance
   */
  public static MainConfiguration getInstance()
  {
    return sInstance;
  }

  /**
   * Get the object's initialization status
   * 
   * @return
   *   {@code true} if the object was successfully initialized, {@code false} otherwise
   */
  public boolean isInitialized()
  {
    return mInitialized;
  }

  /**
   * Initialize the main configuration object.<br>
   * <br>
   * Initialization includes loading the properties and starting the configuration monitor task.
   * 
   * @return
   *   the initialization value of the object. If initialization succeeded, this method will return {@code true}.
   */
  public boolean init()
  {
    sLogger.trace("MainConfiguration::init() - IN");
    
    if (!mInitialized)
    {
      mConfigDir = RunTimeUtils.getProductHomeDir() + File.separatorChar + "conf";
      sLogger.trace("MainConfiguration::init() - ConfigDir is {}", mConfigDir);
      
      boolean loaded = load();
      if (loaded)
      {
        mConfigTask  = new ConfigTask(this);
        
        mConfigMonitoringDelay    = getLongProperty( cConfigPropPrefix + "delay"    , mConfigMonitoringDelay    );
        mConfigMonitoringInterval = getLongProperty( cConfigPropPrefix + "interval" , mConfigMonitoringInterval );
        
        ThreadPool.scheduleAtFixedRate(mConfigTask, mConfigMonitoringDelay, mConfigMonitoringInterval, TimeUnit.MILLISECONDS);
        
        mInitialized = true;
      }
    }
    
    sLogger.trace("MainConfiguration::init() - OUT, Result={}", mInitialized);
    return true;
  }
  
  /**
   * Terminate the main configuration object.<br>
   * <br>
   * Termination includes clearing all properties and canceling the configuration task
   * 
   * @return
   *   the initialization value of the object. If termination succeeded, this method will return {@code false}.
   */
  public boolean term()
  {
    sLogger.trace("MainConfiguration::term() - IN");
    
    if (mInitialized)
    {
      ThreadPool.removeSchedule(mConfigTask);
      
      mConfigTask = null;
      mProperties = null;
      
      mInitialized = false;
    }
    
    sLogger.trace("MainConfiguration::term() - OUT, Result={}", mInitialized);
    return mInitialized;
  }
  
  /**
   * Load configuration properties
   * 
   * @return
   *   {@code false} if the properties are empty or no configuration files could be found, {@code true} otherwise 
   */
  private boolean load()
  {
    sLogger.trace("MainConfiguration::load() - IN");
    
    mProperties = new ConfigProperties();
    mProperties.load(mConfigDir + File.separatorChar + cMainConfigFileName);
    
    boolean result = false;
    if (!mProperties.isEmpty())
      result = true;
    
    sLogger.trace("MainConfiguration::load() - OUT, Result={}", result);
    return result;
  }

  /**
   * Reload configuration properties
   */
  public void refresh()
  {
    sLogger.trace("MainConfiguration::refresh() - IN");
    
    // load new properties
    load();
    
    // refresh listeners
    if (mListeners.size() > 0)
    {
      synchronized (mListeners)
      {
        for (IBaseListener listener : mListeners)
          listener.refresh();
      }
    }
    
    sLogger.trace("MainConfiguration::refresh() - OUT");
  }
  
  /**
   * Register a configuration listener
   * 
   * @param listener
   *   A configuration listener to register in this {@link IBaseRegistrar} object.
   */
  public synchronized void register(IBaseListener listener)
  {
    mListeners.add(listener);
  }
  
  /**
   * Unregister a configuration listener
   * 
   * @param listener
   *   A configuration listener to unregister from this {@link IBaseRegistrar} object.
   */
  public synchronized void unregister(IBaseListener listener)
  {
    synchronized (mListeners)
    {
      for (IBaseListener regListener : mListeners)
      {
        if (regListener.equals(listener))
        {
          mListeners.remove(listener);
          break;
        }
      }
    }
  }

  /**
   * Get a {@link StringList} containing all configuration files composing this configuration object.<br>
   * <br>
   * This includes the main configuration file - kas.properties - as well as all configuration files
   * that are included via the {@code kas.include} statement.
   * 
   * @return
   *   a list of the configuration files
   */
  public StringList getConfigFiles()
  {
    return mProperties.getConfigFiles();
  }

  /**
   * Get the configuration directory path
   * 
   * @return
   *   the configuration directory path
   */
  public String getConfigDir()
  {
    return mConfigDir;
  }
  
  /**
   * Returns the value of a string configuration property
   * 
   * @param key
   *   The name of the configuration property
   * @param defaultValue
   *   The default value that will be returned if the configuration property does not exist
   * @return
   *   the value of the configuration property as stored in the configuration files,
   *   or {@code defaultValue} if the property does not exist.
   */
  public String getStringProperty(String key, String defaultValue)
  {
    return mProperties.getStringProperty(key, defaultValue);
  }
  
  /**
   * Returns the value of an integer configuration property
   * 
   * @param key
   *   The name of the configuration property
   * @param defaultValue
   *   The default value that will be returned if the configuration property does not exist
   * @return
   *   the value of the configuration property as stored in the configuration files,
   *   or {@code defaultValue} if the property does not exist.
   */
  public int getIntProperty(String key, int defaultValue)
  {
    return mProperties.getIntProperty(key, defaultValue);
  }
  
  /**
   * Returns the value of a long configuration property
   * 
   * @param key
   *   The name of the configuration property
   * @param defaultValue
   *   The default value that will be returned if the configuration property does not exist
   * @return
   *   the value of the configuration property as stored in the configuration files,
   *   or {@code defaultValue} if the property does not exist.
   */
  public long getLongProperty(String key, long defaultValue)
  {
    return mProperties.getLongProperty(key, defaultValue);
  }
  
  /**
   * Returns the value of a boolean configuration property
   * 
   * @param key
   *   The name of the configuration property
   * @param defaultValue
   *   The default value that will be returned if the configuration property does not exist
   * @return
   *   the value of the configuration property as stored in the configuration files,
   *   or {@code defaultValue} if the property does not exist.
   */
  public boolean getBoolProperty(String key, boolean defaultValue)
  {
    return mProperties.getBoolProperty(key, defaultValue);
  }
  
  /**
   * Get a subset of the {@link IConfiguration} object.
   * 
   * @param keyPrefix
   *   The prefix of the keys to include in the subset
   * @return
   *   a new {@link Properties} object including only keys that are prefixed with {@code keyPrefix}
   */
  public Properties getSubset(String keyPrefix)
  {
    return mProperties.getSubset(keyPrefix);
  }
  
  /**
   * Get a subset of the {@link IConfiguration} object.
   * 
   * @param keyPrefix
   *   The prefix of the keys to include in the subset
   * @param keySuffix
   *   The suffix of the keys to include in the subset
   * @return
   *   a new {@link Properties} object including only keys that are
   *   prefixed with {@code keyPrefix} <b>AND</b> suffixed with {@code keySuffix}
   */
  public Properties getSubset(String keyPrefix, String keySuffix)
  {
    return mProperties.getSubset(keyPrefix, keySuffix);
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Initialized=").append(mInitialized).append("\n")
      .append(pad).append("  ConfigDir=").append(mConfigDir).append("\n");
      
    if (mInitialized)
    {
      sb.append(pad).append("  MonitoringDelay=").append(mConfigMonitoringDelay).append(" MilliSeconds\n")
        .append(pad).append("  MonitoringInterval=").append(mConfigMonitoringInterval).append(" MilliSeconds\n")
        .append(pad).append("  Properties=(").append(mProperties.toPrintableString(level+1)).append(")\n")
        .append(pad).append("  ConfigTask=(").append(mConfigTask.toPrintableString(level+1)).append(")\n")
        .append(pad).append("  Listeners=(\n");
      sb.append(StringUtils.asPrintableString(mListeners, level+2, true)).append(")\n");
      sb.append(pad).append("  )");
    }
    
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
