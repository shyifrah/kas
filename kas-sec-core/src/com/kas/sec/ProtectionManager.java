package com.kas.mq.server.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AKasObject;
import com.kas.sec.IProtectionManager;
import com.kas.sec.entities.IUserEntity;
import com.kas.sec.resources.ResourceClass;

public class ProtectionManager extends AKasObject implements IProtectionManager
{
  static private ProtectionManager sInstance = null;
  
  static public boolean init()
  {
    sInstance = new ProtectionManager();
    return true;
  }
  
  static public IProtectionManager getInstance()
  {
    if (sInstance == null)
      throw new RuntimeException("ProtectionManager was not initialized");
    
    return sInstance;
  }
  
  private Map<String, ResourceClass> mResourceTable;
  private UserDao mUsers;
  private GroupDao mGroups;
  
  private ProtectionManager()
  {
    mResourceTable = new ConcurrentHashMap<String, ResourceClass>();
    mUsers = new UserDao();
    mGroups = new GroupDao();
  }
  
  public IUserEntity getUserByName(String name)
  {
    return mUsers.get(name);
  }

  public IUserEntity getUserById(int id)
  {
    return mUsers.get(id);
  }

  public String toPrintableString(int level)
  {
    return null;
  }
}
