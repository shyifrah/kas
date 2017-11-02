package com.kas.config;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.kas.config.impl.ConfigTask;
import com.kas.config.impl.Constants;
import com.kas.infra.base.KasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.ThreadPool;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.config.IRegistrar;
import com.kas.infra.utils.RunTimeUtils;

final public class MainConfiguration extends KasObject implements IMainConfiguration, IRegistrar 
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static MainConfiguration    sInstance      = new MainConfiguration();
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private boolean           mInitialized = false;
  private String            mConfigDir   = RunTimeUtils.getProductHomeDir() + "/conf";
  private Set<IListener>    mListeners   = new HashSet<IListener>();
  private Set<String>       mConfigFiles = new HashSet<String>();
  private Properties        mProperties  = null;
  private ConfigTask        mConfigTask  = null;
  
  private long mConfigMonitoringDelay    = Constants.cDefaultMonitoringDelay;
  private long mConfigMonitoringInterval = Constants.cDefaultMonitoringInterval;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static MainConfiguration getInstance()
  {
    return sInstance;
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean isInitialized()
  {
    return mInitialized;
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean init()
  {
    if (!mInitialized)
    {
      boolean loaded = load();
      if (!loaded)
      {
        return false;
      }
      
      mConfigTask  = new ConfigTask(this);
      
      mConfigMonitoringDelay    = getLongProperty( Constants.cConfigPropPrefix + "delay"    , mConfigMonitoringDelay    );
      mConfigMonitoringInterval = getLongProperty( Constants.cConfigPropPrefix + "interval" , mConfigMonitoringInterval );
      
      ThreadPool.scheduleAtFixedRate(mConfigTask, mConfigMonitoringDelay, mConfigMonitoringInterval, TimeUnit.MILLISECONDS);
      
      mInitialized = true;
    }
    
    return true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean term()
  {
    if (mInitialized)
    {
      ThreadPool.remove(mConfigTask);
      
      mConfigTask = null;
      mProperties = null;
      
      mInitialized = false;
    }
    return mInitialized;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private boolean load()
  {
    // reloading configuration to new object and switch. old object will be gc'ed
    //PropertiesLoader newLoader = new PropertiesLoader(mConfigDir, Constants.cIncludeKey);
    mProperties = new Properties();
    mProperties.load(RunTimeUtils.getProductHomeDir() + "/conf/" + Constants.cMainConfigFileName);
    
    if (mProperties.isEmpty())
    {
      return false;
    }
    
    mConfigFiles.clear();
    
    String monitoredFiles = mProperties.getProperty(Constants.cIncludeKey);
    String [] listOfMonitoredFiles = monitoredFiles.split(",");
    for (String file : listOfMonitoredFiles)
    {
      mConfigFiles.add(file);
    }
    
    return true;
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized void register(IListener listener)
  {
    mListeners.add(listener);
    listener.refresh();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Set<String> getConfigFiles()
  {
    return mConfigFiles;
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getConfigDir()
  {
    return mConfigDir;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private String getProperty(String key)
  {
    String result = null;
    if (mProperties == null)
    {
      throw new NullPointerException("Configuration not initialized or terminated");
    }
    else
    {
      result = mProperties.getProperty(key);
    }
    return result;
  }
  
  ///------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getStringProperty(String key, String defaultValue)
  {
    String result = getProperty(key);
    if (result == null)
    {
      return defaultValue;
    }
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getIntProperty(String key, int defaultValue)
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      return defaultValue;
    }
    
    int result;
    try
    {
      result = Integer.valueOf(strResult);
    }
    catch (NumberFormatException e)
    {
      result = defaultValue;
    }
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public long getLongProperty(String key, long defaultValue)
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      return defaultValue;
    }
    
    long result;
    try
    {
      result = Long.valueOf(strResult);
    }
    catch (NumberFormatException e)
    {
      result = defaultValue;
    }
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean getBoolProperty(String key, boolean defaultValue)
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      return defaultValue;
    }
    
    if (!("false".equalsIgnoreCase(strResult)) && !("true".equalsIgnoreCase(strResult)))
    {
      return defaultValue;
    }
    return Boolean.valueOf(strResult);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Properties getSubset(String keyPrefix)
  {
    return mProperties.getSubset(keyPrefix);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
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
