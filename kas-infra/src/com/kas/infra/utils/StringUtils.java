package com.kas.infra.utils;

import java.util.Collection;
import java.util.Map;
import com.kas.infra.base.IObject;

/**
 * Utility functions
 * 
 * @author Pippo
 */
public class StringUtils
{
  /**
   * Return {@link Object#toString()} value, or "null" if object is null
   * 
   * @param obj the object
   * @return Object's toString() value, or "null"
   */
  static public String asString(Object obj)
  {
    return (obj == null ? "null" : obj.toString());
  }
  
  /**
   * Return {@link IObject#toPrintableString(int)} value, or "null" if object is null
   * 
   * @param obj The IObject
   * @return IObject's toPrintableString() value, or "null"
   */
  static public String asPrintableString(IObject obj)
  {
    return (obj == null ? "null" : obj.toPrintableString(0));
  }
  
  /**
   * Return a "key=value" string for a map entry
   * 
   * @param key Entry's key
   * @param value Entry's value
   * @return "key=value" string
   */
  static public String asString(Object key, Object value)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(asString(key));
    sb.append("=");
    sb.append(asString(value));
    return sb.toString();
  }
  
  /**
   * Return the printable String value for a Map object, just as if it had the 
   * {@link com.kas.infra.base.IObject#toPrintableString(int) toPrintable(int)} method as part of it.
   * 
   * @param map The {@code Map}
   * @param level Padding level
   * @return the object's printable string representation 
   */
  static public String asPrintableString(Map<?, ?> map, int level)
  {
    String pad = getPadding(level);
    StringBuilder sb = new StringBuilder();
    
    int size = map.size();
    int i    = 0;
    
    for (Map.Entry<?, ?> entry : map.entrySet())
    {
      ++i;
      sb.append(pad)
        .append(asString(entry.getKey(), entry.getValue()));
      
      if (i + 1 <= size)
        sb.append("\n");
    }
      
    return sb.toString();
  }
  
  /**
   * Return the printable String value for a Collection object, just as if it had the 
   * {@link com.kas.infra.base.IObject#toPrintableString(int)} method as part of it.
   * 
   * @param collection The {@code Collection}
   * @param level Padding level
   * @return the object's printable string representation 
   */
  static public String asPrintableString(Collection<?> collection, int level)
  {
    String pad = getPadding(level);
    StringBuilder sb = new StringBuilder();
    
    int size = collection.size();
    int i    = 0;
    
    for (Object entry : collection)
    {
      ++i;
      sb.append(pad)
        .append(asString(entry));
      
      if (i + 1 <= size)
        sb.append("\n");
    }
      
    return sb.toString();
  }
  
  /**
   * Calculate the padding String base on the specified level
   * 
   * @param level padding level
   * @return the padding string 
   */
  static public String getPadding(int level)
  {
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < level; ++i)
      sb.append("  ");
    
    return sb.toString();
  }
  
  /**
   * Duplicate {@code str} a specified {@code number} of times.<br>
   * <br>
   * If {@code number} has a negative value, a {@code null} string is returned.<br>
   * If {@code number} is 0, an empty string is returned.<br>
   * If {@code number} is 1, no duplication takes place, and {@code str} is returned as is.<br>
   * For all other {@code number} values, {@code str} is duplicated {@code number} of times and returned.
   * 
   * @param str The string to be duplicated
   * @param number The number of times to duplicate it
   * @return the duplicated string 
   */
  static public String duplicate(String str, int number)
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
  
  /**
   * Frame a text with equal signs
   * 
   * @param str The string to be framed
   * @return the duplicated string 
   */
  static public String title(String str)
  {
    if (str == null)
      return null;
    
    StringBuilder sb = new StringBuilder();
    
    int len = str.length();
    String banner = StringUtils.duplicate("=", len + 16);
    sb.append(banner).append('\n')                                     // =========================
      .append("===     ").append(str).append("     ===").append('\n')  // ===     Shy Ifrah     ===
      .append(banner);                                                 // =========================
    
    return sb.toString();
  }
  
  /**
   * Truncate a string to a specified length.<br>
   * <br>
   * If the given string is longer than the requested length, it is truncated, and if it is shorter, it is
   * padded with the specified padding character. If one is not specified, space is used.<br>
   * 
   * @param str The string to be truncated/padded
   * @param len The requested length of the new string
   * @param pad The padding character
   * @return the truncated string
   * 
   * @throws IllegalArgumentException if {@code len} is negative or if {@code str} is null
   */
  static public String trunc(String str, int len)
  {
    return trunc(str, len, ' ');
  }
  
  static public String trunc(String str, int len, char pad)
  {
    if (str == null)
      throw new IllegalArgumentException("Cannot truncate a null string");
    if (len < 0)
      throw new IllegalArgumentException("Cannot truncate a string to a negative value");
    
    // requested length is str length
    if (len == str.length())
      return str;
    
    // requested length is lower than str length
    if (len < str.length())
      return str.substring(0, len);
    
    // requested length is larger than str length
    StringBuilder sb = new StringBuilder(str);
    for (int i = 0; i < len - str.length(); ++i)
      sb.append(pad);
    
    return sb.toString();
  }
}
