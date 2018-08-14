package com.kas.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.kas.config.impl.ConfigTask;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.config.IConfiguration;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.config.IRegistrar;
import com.kas.infra.utils.RunTimeUtils;

/**
 * The {@link MainConfiguration} object class.<br>
 * <br>
 * This is a singleton class.
 * 
 * @author Pippo
 */
final public class MainConfiguration extends AKasObject implements IMainConfiguration, IRegistrar 
{
  static private final long cDefaultMonitoringDelay    = 10000L;
  static private final long cDefaultMonitoringInterval = 10000L;
  static private final String cMainConfigFileName = "kas.properties";
  static private final String cConfigPropPrefix   = "kas.config.";
  
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
   * A collection of {@link IListener} objects that are listening for configuration changes
   */
  private Set<IListener> mListeners   = new HashSet<IListener>();
  
  /**
   * A collection of configuration files
   */
  private Set<String> mConfigFiles = new HashSet<String>();
  
  /**
   * A {@link Properties} object holding all KAS properties read from configuration files
   */
  private Properties mProperties = null;
  
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
   * @return the {@link MainConfiguration} singleton instance
   */
  public static MainConfiguration getInstance()
  {
    return sInstance;
  }

  /**
   * Get the object's initialization status
   * 
   * @return {@code true} if the object was successfully initialized, {@code false} otherwise
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
   * @return the initialization value of the object. If initialization succeeded, this method will return {@code true}.
   * 
   * @see com.kas.infra.base.IInitializable#init()
   */
  public boolean init()
  {
    if (!mInitialized)
    {
      mConfigDir = RunTimeUtils.getProductHomeDir() + File.separatorChar + "conf";
      boolean loaded = load();
      if (!loaded)
      {
        return false;
      }
      
      mConfigTask  = new ConfigTask(this);
      
      mConfigMonitoringDelay    = getLongProperty( cConfigPropPrefix + "delay"    , mConfigMonitoringDelay    );
      mConfigMonitoringInterval = getLongProperty( cConfigPropPrefix + "interval" , mConfigMonitoringInterval );
      
      ThreadPool.scheduleAtFixedRate(mConfigTask, mConfigMonitoringDelay, mConfigMonitoringInterval, TimeUnit.MILLISECONDS);
      
      mInitialized = true;
    }
    
    return true;
  }
  
  /**
   * Terminate the main configuration object.<br>
   * <br>
   * Termination includes clearing all properties and canceling the configuration task
   * 
   * @return the initialization value of the object. If termination succeeded, this method will return {@code false}.
   * 
   * @see com.kas.infra.base.IInitializable#term()
   */
  public boolean term()
  {
    if (mInitialized)
    {
      ThreadPool.removeSchedule(mConfigTask);
      
      mConfigTask = null;
      mProperties = null;
      
      mInitialized = false;
    }
    return mInitialized;
  }
  
  /**
   * Load configuration properties
   * 
   * @return {@code false} if the properties are empty or no configuration files could be found, {@code true} otherwise 
   */
  private boolean load()
  {
    // reloading configuration to new object and switch. old object will be gc'ed
    mProperties = new Properties();
    mProperties.load(mConfigDir + File.separatorChar + cMainConfigFileName);
    
    if (mProperties.isEmpty())
      return false;
    
    mConfigFiles.clear();
    
    String monitoredFiles = mProperties.getStringProperty(Properties.cIncludeKey, null);
    if (monitoredFiles == null) // this should not happen, cIncludeKey should have at least one file name 
    {
      return false;
    }
    else
    {
      String [] listOfMonitoredFiles = monitoredFiles.split(",");
      for (String file : listOfMonitoredFiles)
      {
        mConfigFiles.add(file);
      }
    }
    
    return true;
  }

  /**
   * Reload configuration properties
   */
  public void reload()
  {
    // load new properties
    load();
    
    // refresh listeners
    if (mListeners.size() > 0)
    {
      synchronized (mListeners)
      {
        for (IListener listener : mListeners)
        {
          listener.refresh();
        }
      }
    }
  }
  
  /**
   * Register a configuration listener
   * 
   * @param listener A configuration listener to register in this {@link IRegistrar} object.
   * 
   * @see com.kas.infra.config.IRegistrar#register(IListener)
   */
  public synchronized void register(IListener listener)
  {
    mListeners.add(listener);
    listener.refresh();
  }
  
  /**
   * Unregister a configuration listener
   * 
   * @param listener A configuration listener to unregister from this {@link IRegistrar} object.
   * 
   * @see com.kas.infra.config.IRegistrar#unregister(IListener)
   */
  public synchronized void unregister(IListener listener)
  {
    synchronized (mListeners)
    {
      for (IListener regListener : mListeners)
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
   * Get a {@link Set} containing all configuration files composing this configuration object.<br>
   * <br>
   * This includes the main configuration file - kas.properties - as well as all configuration files
   * that are included via the {@code kas.include} statement.
   * 
   * @return a set of the configuration files
   */
  public Set<String> getConfigFiles()
  {
    return mConfigFiles;
  }

  /**
   * Get the configuration directory path
   * 
   * @return the configuration directory path
   */
  public String getConfigDir()
  {
    return mConfigDir;
  }
  
  /**
   * Returns the value of a string configuration property
   * 
   * @param key The name of the configuration property
   * @param defaultValue The default value that will be returned if the configuration property does not exist
   * @return the value of the configuration property as stored in the configuration files, or {@code defaultValue}
   * if the property does not exist.
   * 
   * @see com.kas.infra.config.IConfiguration#getStringProperty(String, String)
   */
  public String getStringProperty(String key, String defaultValue)
  {
    return mProperties.getStringProperty(key, defaultValue);
  }
  
  /**
   * Returns the value of an integer configuration property
   * 
   * @param key The name of the configuration property
   * @param defaultValue The default value that will be returned if the configuration property does not exist
   * @return the value of the configuration property as stored in the configuration files, or {@code defaultValue}
   * if the property does not exist.
   * 
   * @see com.kas.infra.config.IConfiguration#getIntProperty(String, int)
   */
  public int getIntProperty(String key, int defaultValue)
  {
    return mProperties.getIntProperty(key, defaultValue);
  }
  
  /**
   * Returns the value of a long configuration property
   * 
   * @param key The name of the configuration property
   * @param defaultValue The default value that will be returned if the configuration property does not exist
   * @return the value of the configuration property as stored in the configuration files, or {@code defaultValue}
   * if the property does not exist.
   * 
   * @see com.kas.infra.config.IConfiguration#getLongProperty(String, long)
   */
  public long getLongProperty(String key, long defaultValue)
  {
    return mProperties.getLongProperty(key, defaultValue);
  }
  
  /**
   * Returns the value of a boolean configuration property
   * 
   * @param key The name of the configuration property
   * @param defaultValue The default value that will be returned if the configuration property does not exist
   * @return the value of the configuration property as stored in the configuration files, or {@code defaultValue}
   * if the property does not exist.
   * 
   * @see com.kas.infra.config.IConfiguration#getBoolProperty(String, boolean)
   */
  public boolean getBoolProperty(String key, boolean defaultValue)
  {
    return mProperties.getBoolProperty(key, defaultValue);
  }
  
  /**
   * Get a subset of the {@link IConfiguration} object.
   * 
   * @param keyPrefix The prefix of the keys to include in the subset
   * @return a new {@link Properties} object including only keys that are prefixed with {@code keyPrefix}
   * 
   * @see com.kas.infra.config.IConfiguration#getSubset(String)
   */
  public Properties getSubset(String keyPrefix)
  {
    return mProperties.getSubset(keyPrefix);
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
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
        .append(pad).append("  Properties=(").append(mProperties.toPrintableString(level + 1)).append(")\n")
        .append(pad).append("  ConfigTask=(").append(mConfigTask.toPrintableString(level + 1)).append(")\n")
        .append(pad).append("  Listeners=(\n");
      
      for (IListener listener : mListeners)
      {
        sb.append(pad).append("    ").append(listener.name()).append("\n");
      }
      
      sb.append(pad).append("  )\n");
    }
    
    sb.append(pad).append(")\n");
    
    return sb.toString();
  }
}
