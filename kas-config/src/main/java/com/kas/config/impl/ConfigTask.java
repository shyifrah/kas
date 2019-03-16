package com.kas.config.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.IObject;
import com.kas.infra.base.threads.AKasRunnable;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.utils.StringUtils;

/**
 * Configuration monitoring task.<br>
 * <br>
 * This task is scheduled for repeated execution.
 * 
 * @author Pippo
 */
final public class ConfigTask extends AKasRunnable
{
  static private Logger sLogger = LogManager.getLogger(ConfigTask.class);
  
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
   * @param config
   *   The main configuration object
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
   * Each time the task is executed, it scans the map of configuration files
   * and compares the last modification timestamp of the file against the value
   * previously saved. If the value has changed, it means a user updated this
   * configuration file and it should be reloaded. In that case, the saved
   * timestamp is also updated.
   */
  public void run()
  {
    sLogger.trace("ConfigTask::run() - IN");
    
    boolean reload = false;
    
    for (Map.Entry<String, Long> pair : mMonitoredFilesMap.entrySet())
    {
      String fileName = pair.getKey();
      Long prevModTs  = pair.getValue();
      sLogger.debug("ConfigTask::run() - Checking File={}, LastModified={}", fileName, prevModTs);
      
      // if the file was removed from monitored list, remove it from modification map
      if (!mMainConfig.getConfigFiles().contains(fileName))
      {
        sLogger.debug("ConfigTask::run() - File {} does not exist in config map, remove it", fileName);
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
            sLogger.debug("ConfigTask::run() - File {} was updated", fileName);
            mMonitoredFilesMap.put(fileName, currModTs);
            reload = true;
          }
        }
      }
    }
    
    if (reload) mMainConfig.refresh();
    
    sLogger.trace("ConfigTask::run() - OUT");
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
      .append(pad).append("  MonitoredFiles=(\n")
      .append(StringUtils.asPrintableString(mMonitoredFilesMap, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
