package com.kas.config.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IRunnable;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.utils.StringUtils;

final public class ConfigTask extends AKasObject implements IRunnable
{
  private IMainConfiguration mMainConfig;
  private Map<String, Long>  mMonitoredFilesMap;
  
  //----------------------------------------------------------------------------------------------------
  //
  //----------------------------------------------------------------------------------------------------
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
  
  //----------------------------------------------------------------------------------------------------
  //
  //----------------------------------------------------------------------------------------------------
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
   * Returns a replica of this {@link ConfigTask}.
   * 
   * @return a replica of this {@link ConfigTask}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public ConfigTask replicate()
  {
    return new ConfigTask(mMainConfig);
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
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  MonitoredFiles=(\n")
      .append(StringUtils.asPrintableString(mMonitoredFilesMap, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
