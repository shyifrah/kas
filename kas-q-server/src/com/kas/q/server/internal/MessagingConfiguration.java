package com.kas.q.server.internal;

import java.util.HashSet;
import com.kas.config.impl.AbstractConfiguration;
import com.kas.config.impl.Constants;
import com.kas.infra.base.WeakRef;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IRegistrar;
import com.kas.infra.utils.StringUtils;

public class MessagingConfiguration extends AbstractConfiguration implements IRegistrar
{
  /***************************************************************************************************************
   * 
   */
  private boolean mEnabled          = Constants.cDefaultEnabled;
  private int     mPort             = Constants.cDefaultListenPort;
  private String  mManagerName      = Constants.cDefaultManagerName;
  private String  mDeadQueue        = Constants.cDefaultDeadQueueName;
  private String  mAdminQueue       = Constants.cDefaultAdminQueueName;
  private long    mDispatchDelay    = Constants.cDefaultDispatchDelay;
  private long    mDispatchInterval = Constants.cDefaultDispatchInterval;
  private int     mLocatorPort      = Constants.cDefaultLocatorListenPort;
      
  private HashSet<WeakRef<IListener>> mRemoteManagers = new HashSet<WeakRef<IListener>>();
  
  /***************************************************************************************************************
   * 
   */
  public void refresh()
  {
    mEnabled          = mMainConfig.getBoolProperty   ( Constants.cMessagingConfigPrefix + "enabled"     , mEnabled     );
    mPort             = mMainConfig.getIntProperty    ( Constants.cMessagingConfigPrefix + "port"        , mPort        );
    mManagerName      = mMainConfig.getStringProperty ( Constants.cMessagingConfigPrefix + "managerName" , mManagerName );
    mDeadQueue        = mMainConfig.getStringProperty ( Constants.cMessagingConfigPrefix + "deadq"       , mDeadQueue   );
    mAdminQueue       = mMainConfig.getStringProperty ( Constants.cMessagingConfigPrefix + "adminq"      , mAdminQueue  );
    mDispatchDelay    = mMainConfig.getLongProperty   ( Constants.cMessagingDispatchConfigPrefix + "delay"    , mDispatchDelay    ); 
    mDispatchInterval = mMainConfig.getLongProperty   ( Constants.cMessagingDispatchConfigPrefix + "interval" , mDispatchInterval );
    mLocatorPort      = mMainConfig.getIntProperty    ( Constants.cMessagingLocatorConfigPrefix + "port" , mLocatorPort );
    
    synchronized (mRemoteManagers)
    {
      for (WeakRef<IListener> ref : mRemoteManagers)
      {
        IListener listener = ref.get();
        if (listener != null)
        {
          listener.refresh();
        }
      }
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized void register(IListener listener)
  {
    WeakRef<IListener> ref = new WeakRef<IListener>(listener);
    mRemoteManagers.add(ref);
    listener.refresh();
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized void unregister(IListener listener)
  {
    for (WeakRef<IListener> ref : mRemoteManagers)
    {
      if ((ref != null) && listener.equals(ref.get()))
      {
        mRemoteManagers.remove(ref);
        break;
      }
    }
  }
  
  /***************************************************************************************************************
   * Whether the KAS/Q is enabled or not
   * 
   * @return true if KAS/Q is enabled, false otherwise
   */
  public boolean isEnabled()
  {
    return mEnabled;
  }
  
  /***************************************************************************************************************
   * Gets the port number on which the KAS/Q manager listens for new client connections
   * 
   * @return KAS/Q port number
   */
  public int getPort()
  {
    return mPort;
  }
  
  /***************************************************************************************************************
   * Gets the name associated with the current manager
   * 
   * @return the manager's name
   */
  public String getManagerName()
  {
    return mManagerName;
  }
  
  /***************************************************************************************************************
   * Gets the name of the dead queue
   * 
   * @return the name of the dead queue
   */
  public String getDeadQueue()
  {
    return mDeadQueue;
  }
  
  /***************************************************************************************************************
   * Gets the name of the administration queue
   * 
   * @return the name of the administration queue
   */
  public String getAdminQueue()
  {
    return mAdminQueue;
  }
  
  /***************************************************************************************************************
   * Number of milliseconds since KAS/Q startup till the manager will start dispatch queues
   * 
   * @return dispatching delay in milliseconds
   */
  public long getDispatchDelay()
  {
    return mDispatchDelay;
  }
  
  /***************************************************************************************************************
   * Number of milliseconds between each dispatch task execution
   * 
   * @return dispatching interval in milliseconds
   */
  public long getDispatchInterval()
  {
    return mDispatchInterval;
  }
  
  /***************************************************************************************************************
   * The port number on which the Repository will listen for new location requests
   * 
   * @return locator's port number
   */
  public int getLocatorPort()
  {
    return mLocatorPort;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Enabled=").append(mEnabled).append("\n")
      .append(pad).append("  Name=").append(mManagerName).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append("  DeadQueue=").append(mDeadQueue).append("\n")
      .append(pad).append("  AdminQueue=").append(mAdminQueue).append("\n")
      .append(pad).append("  QueueDispatch.Delay=").append(mDispatchDelay).append(" MilliSeconds\n")
      .append(pad).append("  QueueDispatch.Interval=").append(mDispatchInterval).append(" MilliSeconds\n")
      .append(pad).append("  Locator.Port=").append(mLocatorPort).append("\n")
      .append(pad).append("  RemoteManagers=(\n")
      .append(StringUtils.asPrintableString(mRemoteManagers, level + 2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
