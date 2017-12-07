package com.kas.q.server.internal;

import java.util.HashSet;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.base.WeakRef;
import com.kas.infra.config.IListener;
import com.kas.infra.config.IRegistrar;
import com.kas.infra.utils.StringUtils;

public class MessagingConfiguration extends AConfiguration implements IRegistrar
{
  /***************************************************************************************************************
   * 
   */
  private static final String  cMessagingConfigPrefix  = "kas.q.";
  private static final String  cMessagingHouseKeeperConfigPrefix  = "kas.q.housekeeper.";
  
  public  static final boolean cDefaultEnabled        = true;
  public  static final int     cDefaultListenPort     = 14560;
  public  static final String  cDefaultManagerName    = "qmgr";
  public  static final String  cDefaultDeadQueueName  = "local.dead";
  public  static final String  cDefaultAdminQueueName = "local.admin";
  private static final long    cDefaultHouseKeeperDelay    = 60000L;
  private static final long    cDefaultHouseKeeperInterval = 3600000L;
  
  
  /***************************************************************************************************************
   * 
   */
  private boolean mEnabled     = cDefaultEnabled;
  private int     mPort        = cDefaultListenPort;
  private String  mManagerName = cDefaultManagerName;
  private String  mDeadQueue   = cDefaultDeadQueueName;
  private String  mAdminQueue  = cDefaultAdminQueueName;
  private long    mHouseKeeperDelay    = cDefaultHouseKeeperDelay;
  private long    mHouseKeeperInterval = cDefaultHouseKeeperInterval;
      
  private HashSet<WeakRef<IListener>> mRemoteManagers = new HashSet<WeakRef<IListener>>();
  
  /***************************************************************************************************************
   * 
   */
  public void refresh()
  {
    mEnabled          = mMainConfig.getBoolProperty    ( cMessagingConfigPrefix + "enabled"     , mEnabled     );
    mPort             = mMainConfig.getIntProperty     ( cMessagingConfigPrefix + "port"        , mPort        );
    mManagerName      = mMainConfig.getStringProperty  ( cMessagingConfigPrefix + "managerName" , mManagerName );
    mDeadQueue        = mMainConfig.getStringProperty  ( cMessagingConfigPrefix + "deadq"       , mDeadQueue   );
    mAdminQueue       = mMainConfig.getStringProperty  ( cMessagingConfigPrefix + "adminq"      , mAdminQueue  );
    
    mHouseKeeperDelay    = mMainConfig.getLongProperty ( cMessagingHouseKeeperConfigPrefix + "delay"    , mHouseKeeperDelay    );
    mHouseKeeperInterval = mMainConfig.getLongProperty ( cMessagingHouseKeeperConfigPrefix + "interval" , mHouseKeeperInterval );
    
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
   * Gets the number of milliseconds since {@code KasqRepository} startup to start performing housekeeping
   * 
   * @return the housekeeper task's delay in milliseconds
   */
  public long getHouseKeeperDelay()
  {
    return mHouseKeeperDelay;
  }
  
  /***************************************************************************************************************
   * Gets number of milliseconds between housekeeping executions
   * 
   * @return the housekeeper task's interval in milliseconds
   */
  public long getHouseKeeperInterval()
  {
    return mHouseKeeperInterval;
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
      .append(pad).append("  HouseKeeping=(\n")
      .append(pad).append("    Delay=").append(mHouseKeeperDelay).append(" minutes\n")
      .append(pad).append("    Interval=").append(mHouseKeeperInterval).append(" minutes\n")
      .append(pad).append("  )\n")
      .append(pad).append("  RemoteManagers=(\n")
      .append(StringUtils.asPrintableString(mRemoteManagers, level + 2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
