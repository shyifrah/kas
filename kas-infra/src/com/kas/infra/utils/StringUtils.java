package com.kas.infra.utils;

import java.util.Map;
import java.util.Set;

public class StringUtils
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String asString(Map.Entry<?, ?> entry)
  {
    StringBuilder sb = new StringBuilder();
    
    Object key   = entry.getKey();
    Object value = entry.getValue();
    sb.append(key == null ? "null" : key.toString());
    sb.append("=");
    sb.append(value == null ? "null" : value.toString());
    
    return sb.toString();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String asPrintableString(Map<?, ?> map, int level)
  {
    String pad = getPadding(level);
    StringBuilder sb = new StringBuilder();
    
    int size = map.size();
    int i    = 0;
    
    for (Map.Entry<?, ?> entry : map.entrySet())
    {
      ++i;
      sb.append(pad);
      sb.append(asString(entry));
      
      if (i + 1 <= size)
        sb.append("\n");
    }
      
    return sb.toString();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String asPrintableString(Set<?> set, int level)
  {
    String pad = getPadding(level);
    StringBuilder sb = new StringBuilder();
    
    int size = set.size();
    int i    = 0;
    
    for (Object entry : set)
    {
      ++i;
      sb.append(pad);
      
      
      String val; // = entry == null ? "null" : entry.toString();
      if (entry == null)
      {
        val = "null";
      }
      else
      {
        val = entry.toString();
      }
      
      sb.append(val);
      
      if (i + 1 <= size)
        sb.append("\n");
    }
      
    return sb.toString();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String getPadding(int level)
  {
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < level; ++i)
      sb.append("  ");
    
    return sb.toString();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String duplicate(String str, int number)
  {
    if (number < 0)
      return null;
    
    if (number == 0)
      return "";
    
    if (number == 1)
      return str;
    
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < number; ++i)
      sb.append(str);
      
    return sb.toString();
  }
}
