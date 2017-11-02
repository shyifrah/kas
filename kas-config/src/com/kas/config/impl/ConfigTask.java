package com.kas.config.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.KasObject;
import com.kas.infra.config.IMainConfiguration;
import com.kas.infra.utils.StringUtils;

final public class ConfigTask extends KasObject implements Runnable
{
  private IMainConfiguration mMainConfig;
  private Map<String, Long>  mMonitoredFilesLastModTimeStampMap;
  
  //----------------------------------------------------------------------------------------------------
  //
  //----------------------------------------------------------------------------------------------------
  public ConfigTask(IMainConfiguration config)
  {
    mMainConfig  = config;
    mMonitoredFilesLastModTimeStampMap = new HashMap<String, Long>();
    
    for (String monitoredFile : mMainConfig.getConfigFiles())
    {
      File file = new File(monitoredFile);
      if (file.exists())
      {
        mMonitoredFilesLastModTimeStampMap.put(monitoredFile,  -1L);
      }
    }
  }
  
  //----------------------------------------------------------------------------------------------------
  //
  //----------------------------------------------------------------------------------------------------
  public void run()
  {
    boolean reload = false;
    
    for (Map.Entry<String, Long> pair : mMonitoredFilesLastModTimeStampMap.entrySet())
    {
      String fileName = pair.getKey();
      Long prevModTs  = pair.getValue();
      
      // if the file was removed from monitored list, remove it from modification map
      if (!mMainConfig.getConfigFiles().contains(fileName))
      {
        mMonitoredFilesLastModTimeStampMap.remove(fileName);
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
            mMonitoredFilesLastModTimeStampMap.put(fileName, currModTs);
            reload = true;
          }
        }
      }
    }
    
    if (reload) mMainConfig.reload();
  }
  
  //----------------------------------------------------------------------------------------------------
  //
  //----------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  MonitoredFiles=(\n")
      .append(StringUtils.asPrintableString(mMonitoredFilesLastModTimeStampMap, level + 2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
