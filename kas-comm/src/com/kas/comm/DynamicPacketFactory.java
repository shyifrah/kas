package com.kas.comm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicPacketFactory
{
  static private DynamicPacketFactory sInstance = new DynamicPacketFactory();
  
  private final Map<Integer, IPacketFactory> mPacketFactoriesMap = new ConcurrentHashMap<Integer, IPacketFactory>();
  
  static public void register(int classId, IPacketFactory factory)
  {
    sInstance.mPacketFactoriesMap.put(classId, factory);
  }
  
  static public void unregister(int classId)
  {
    sInstance.mPacketFactoriesMap.remove(classId);
  }
  
  static public IPacketFactory get(int classId)
  {
    return sInstance.mPacketFactoriesMap.get(classId);
  }
}
