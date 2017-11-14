package com.kas.logging.impl;

import java.util.HashSet;
import com.kas.config.impl.AConfiguration;
import com.kas.config.impl.Constants;
import com.kas.infra.base.WeakRef;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IRegistrar;

public class LoggingConfiguration extends AConfiguration implements IRegistrar
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private boolean mEnabled = Constants.cDefaultEnabled;
  
  private HashSet<WeakRef<IListener>> mAppenderConfigsSet = new HashSet<WeakRef<IListener>>();
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void refresh()
  {
    mEnabled = mMainConfig.getBoolProperty ( Constants.cLoggingConfigPrefix + "enabled" , mEnabled);
    
    for (WeakRef<IListener> appenderConfigRef : mAppenderConfigsSet)
    {
      IListener appenderConfig = appenderConfigRef.get();
      if (appenderConfig == null)
      {
        mAppenderConfigsSet.remove(appenderConfigRef);
      }
      else
      {
        appenderConfig.refresh();
      }
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getAppenderName(String requestorClassName)
  {
    String loggerName = requestorClassName;
    String appenderName = null;
    boolean found = false;
    
    // looking for best match
    do
    {
      String key = Constants.cLoggingConfigPrefix + "logger." + loggerName + ".appender";
      String val = mMainConfig.getStringProperty(key, "");
      
      if (val.length() > 0)
      {
        found = true;
        appenderName = val;
      }
      else
      {
        // remove last qualifier of logger name
        int endIdx = loggerName.lastIndexOf('.');
        if (endIdx == -1)
          break;
        loggerName = loggerName.substring(0, endIdx);
      }
    }
    while (!found);

    return appenderName;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean isEnabled()
  {
    return mEnabled;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized void register(IListener listener)
  {
    WeakRef<IListener> appenderConfigRef = new WeakRef<IListener>(listener);
    mAppenderConfigsSet.add(appenderConfigRef);
    listener.refresh();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized void unregister(IListener listener)
  {
    for (WeakRef<IListener> ref : mAppenderConfigsSet)
    {
      if ((ref != null) && listener.equals(ref.get()))
      {
        mAppenderConfigsSet.remove(ref);
        break;
      }
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Enabled=").append(mEnabled).append("\n")
      .append(pad).append("  AppendersConfigurations=(\n");

    for (WeakRef<IListener> ref : mAppenderConfigsSet)
    {
      IListener listener = ref.get();
      sb.append(pad).append("    ").append(listener.toPrintableString(level + 2)).append("\n");
    }
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
