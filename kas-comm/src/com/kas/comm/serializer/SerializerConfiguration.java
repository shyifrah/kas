package com.kas.comm.serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.config.impl.AConfiguration;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;

/**
 * This {@link AConfiguration} object holds class to ID mappings
 * 
 * @author Pippo
 */
public class SerializerConfiguration extends AConfiguration
{
  static private final String cSerializerConfigPrefix  = "kas.serializer.";
  static private final String cSerializerClassConfigPrefix  = cSerializerConfigPrefix + "class.";
  
  private Map<String, Integer> mClassToIdMap = new ConcurrentHashMap<String, Integer>();
  private Map<Integer, String> mIdToClassMap = new ConcurrentHashMap<Integer, String>();
  
  static private SerializerConfiguration sInstance = new SerializerConfiguration();
  
  /**
   * Get singleton instance
   * 
   * @return the singleton instance
   */
  static public SerializerConfiguration getInstance()
  {
    return sInstance;
  }
  
  /**
   * Refresh configuration - reload class-to-id map and id-to-class map
   */
  public void refresh()
  {
    mClassToIdMap.clear();
    mIdToClassMap.clear();
    
    Properties props = mMainConfig.getSubset(cSerializerClassConfigPrefix);
    for (Map.Entry<Object, Object> entry : props.entrySet())
    {
      String className = ((String)entry.getKey()).substring(cSerializerClassConfigPrefix.length());
      String strId     = (String)entry.getValue();
      
      Integer id = -999;
      try
      {
        id = Integer.valueOf(strId);
      }
      catch (NumberFormatException e) {}
      
      if (id > 0)
      {
        mClassToIdMap.put(className, id);
        mIdToClassMap.put(id, className);
      }
    }
  }
  
  /**
   * Get the class name by its ID
   * 
   * @param id The Class' ID
   * @return the class name matching the specified ID
   */
  public String getClassName(int id)
  {
    return mIdToClassMap.get(id);
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
      .append(pad).append("  ID to Class=(\n");
    sb.append(StringUtils.asPrintableString(mIdToClassMap, level+2)).append('\n');
    sb.append(pad).append("  )\n")
      .append(pad).append("  Class to ID=(\n");
    sb.append(StringUtils.asPrintableString(mClassToIdMap, level+2)).append('\n');
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
