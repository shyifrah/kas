package com.kas.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.kas.config.impl.ConfigTask;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.config.IRegistrar;
import com.kas.infra.utils.RunTimeUtils;

final public class MainConfiguration extends AKasObject implements IMainConfiguration, IRegistrar 
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static final long cDefaultMonitoringDelay    = 10000L;
  private static final long cDefaultMonitoringInterval = 10000L;
  
  private static final String cMainConfigFileName = "kas.properties";
  private static final String cConfigPropPrefix   = "kas.config.";
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static MainConfiguration    sInstance      = new MainConfiguration();
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private boolean        mInitialized = false;
  private String         mConfigDir   = RunTimeUtils.getProductHomeDir() + File.separatorChar + "conf";
  private Set<IListener> mListeners   = new HashSet<IListener>();
  private Set<String>    mConfigFiles = new HashSet<String>();
  private Properties     mProperties  = null;
  private ConfigTask     mConfigTask  = null;
  
  private long mConfigMonitoringDelay    = cDefaultMonitoringDelay;
  private long mConfigMonitoringInterval = cDefaultMonitoringInterval;
  
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
      
      mConfigMonitoringDelay    = getLongProperty( cConfigPropPrefix + "delay"    , mConfigMonitoringDelay    );
      mConfigMonitoringInterval = getLongProperty( cConfigPropPrefix + "interval" , mConfigMonitoringInterval );
      
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
      ThreadPool.removeSchedule(mConfigTask);
      
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
  //private String getProperty(String key)
  //{
  //  String result = null;
  //  if (mProperties == null)
  //  {
  //    throw new NullPointerException("Configuration not initialized or terminated");
  //  }
  //  else
  //  {
  //    result = mProperties.getProperty(key);
  //  }
  //  return result;
  //}
  //
  ///------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getStringProperty(String key, String defaultValue)
  {
    return mProperties.getStringProperty(key, defaultValue);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getIntProperty(String key, int defaultValue)
  {
    return mProperties.getIntProperty(key, defaultValue);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public long getLongProperty(String key, long defaultValue)
  {
    return mProperties.getLongProperty(key, defaultValue);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean getBoolProperty(String key, boolean defaultValue)
  {
    return mProperties.getBoolProperty(key, defaultValue);
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
