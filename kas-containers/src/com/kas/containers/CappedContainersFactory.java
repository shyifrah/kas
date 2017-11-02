package com.kas.containers;

import java.util.Map;
import java.util.Set;
import com.kas.containers.internal.Constants;
import com.kas.containers.internal.ContainersConfiguration;

@SuppressWarnings("rawtypes")
public class CappedContainersFactory
{
  private static ContainersConfiguration sConfig = new ContainersConfiguration();
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static Map<?, ?> createMap(String name, ICappedContainerListener listener)
  {
    int threshold = getThreshold(name);
    if (threshold == Constants.cDefaultNotFoundSize)
      return null;
    
    return new CappedHashMap(name, threshold, listener);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static Map<?, ?> createMap(CappedContainerProxy proxy)
  {
    String name = proxy.getName();
    int threshold = getThreshold(name);
    if (threshold == Constants.cDefaultNotFoundSize)
      return null;
    
    return new CappedHashMap(name, threshold, proxy);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static Set<?> createSet(String name, ICappedContainerListener listener)
  {
    int threshold = getThreshold(name);
    if (threshold == Constants.cDefaultNotFoundSize)
      return null;
    
    return new CappedHashSet(name, threshold, listener);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static Set<?> createSet(CappedContainerProxy proxy)
  {
    String name = proxy.getName();
    int threshold = getThreshold(name);
    if (threshold == Constants.cDefaultNotFoundSize)
      return null;
    
    return new CappedHashSet(name, threshold, proxy);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static int getThreshold(String name)
  {
    if (!sConfig.isInitialized())
    {
      sConfig.init();
    }

    int threshold = sConfig.getContainerCap(name);
    
    return threshold;
  }
}
