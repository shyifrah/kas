package com.kas.infra.utils;

import java.util.Map;
import java.util.Set;

public class StringUtils
{
  /***************************************************************************************************************
   * Return object's toString() value, or "null" if object is null
   * 
   * @param obj the object
   * 
   * @return Object's toString() value, or "null"
   */
  public static String asString(Object obj)
  {
    return (obj == null ? "null" : obj.toString());
  }
  
  /***************************************************************************************************************
   * Return a "key=value" string for a map entry
   * 
   * @param key entry's key
   * @param value entry's value
   * 
   * @return "key=value" string
   */
  public static String asString(Object key, Object value)
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append(asString(key));
    sb.append("=");
    sb.append(asString(value));
    
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * Return the String value for a Map, just as if it had the {@link com.kas.infra.base.IObject#toPrintableString(int) toPrintable(int)}
   * method as part of it.
   * 
   * @param map the {@code Map}
   * @param level padding level
   * 
   * @return the object's printable string representation 
   */
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
      sb.append(asString(entry.getKey(), entry.getValue()));
      
      if (i + 1 <= size)
        sb.append("\n");
    }
      
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * Return the String value for a Set, just as if it had the {@link com.kas.infra.base.IObject#toPrintableString(int) toPrintable(int)}
   * method as part of it.
   * 
   * @param set the {@code Set}
   * @param level padding level
   * 
   * @return the object's printable string representation 
   */
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
  
  /***************************************************************************************************************
   * Calculate the padding String base on the specified level
   * 
   * @param level padding level
   * 
   * @return the padding string 
   */
  public static String getPadding(int level)
  {
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < level; ++i)
      sb.append("  ");
    
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * Duplicate a specified string, a specified number of times
   * 
   * If {@code number} has a negative value, a {@code null} string is returned.
   * If {@code number} is 0, an empty string is returned.
   * If {@code number} is 1, no duplication takes place, and {@code str} is returned as is.
   * for all other {@code number} values, {@code str} is duplicated {@code number} of times and returned.
   * 
   * @param str the string to be duplicated
   * @param number the number of times to duplicate it
   * 
   * @return the duplicated string 
   */
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
