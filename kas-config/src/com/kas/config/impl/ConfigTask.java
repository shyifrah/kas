package com.kas.config.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.utils.StringUtils;

/**
 * Configuration monitoring task.<br>
 * <br>
 * This task is scheduled for repeated execution.
 * 
 * @author Pippo
 */
final public class ConfigTask extends AKasObject implements Runnable
{
  /**
   * The main configuration
   */
  private IMainConfiguration mMainConfig;
  
  /**
   * A map of monitored files with the associated modification timestamp (long value) 
   */
  private Map<String, Long>  mMonitoredFilesMap;
  

  /**
   * Construct the configuration monitoring task
   * 
   * @param config The main configuration object
   */
  public ConfigTask(IMainConfiguration config)
  {
    mMainConfig  = config;
    mMonitoredFilesMap = new HashMap<String, Long>();
    
    for (String monitoredFile : mMainConfig.getConfigFiles())
    {
      File file = new File(monitoredFile);
      if (file.exists())
      {
        mMonitoredFilesMap.put(monitoredFile,  -1L);
      }
    }
  }
  
  /**
   * Running the monitoring task.<br>
   * <br>
   * Each time the task is executed, it scans the map of configuration files and compares the
   * last modification timestamp of the file against the value previously saved. If the value has changed,
   * it means a user updated this configuration file and it should be reloaded. In that case, the saved
   * timestamp is also updated.
   */
  public void run()
  {
    boolean reload = false;
    
    for (Map.Entry<String, Long> pair : mMonitoredFilesMap.entrySet())
    {
      String fileName = pair.getKey();
      Long prevModTs  = pair.getValue();
      
      // if the file was removed from monitored list, remove it from modification map
      if (!mMainConfig.getConfigFiles().contains(fileName))
      {
        mMonitoredFilesMap.remove(fileName);
      }
      else
      {
        Long currModTs = 0L;
        File file = new File(fileName);
        if (file.exists())
        {
          currModTs = file.lastModified();
          if (currModTs > prevModTs)
          {
            mMonitoredFilesMap.put(fileName, currModTs);
            reload = true;
          }
        }
      }
    }
    
    if (reload) mMainConfig.reload();
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
      .append(pad).append("  MonitoredFiles=(\n")
      .append(StringUtils.asPrintableString(mMonitoredFilesMap, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
