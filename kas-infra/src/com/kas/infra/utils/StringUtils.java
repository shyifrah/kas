package com.kas.infra.utils;

import java.util.Collection;
import java.util.Map;
import com.kas.infra.base.IObject;

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
   * Return IObject's toPrintableString() value, or "null" if object is null
   * 
   * @param obj the IObject
   * 
   * @return IObject's toPrintableString() value, or "null"
   */
  public static String asPrintableString(IObject obj)
  {
    return (obj == null ? "null" : obj.toPrintableString(0));
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
   * Return the printable String value for a Map object, just as if it had the 
   * {@link com.kas.infra.base.IObject#toPrintableString(int) toPrintable(int)} method as part of it.
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
      sb.append(pad)
        .append(asString(entry.getKey(), entry.getValue()));
      
      if (i + 1 <= size)
        sb.append("\n");
    }
      
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * Return the printable String value for a Collection object, just as if it had the 
   * {@link com.kas.infra.base.IObject#toPrintableString(int) toPrintable(int)} method as part of it.
   * 
   * @param collection the {@code Collection}
   * @param level padding level
   * 
   * @return the object's printable string representation 
   */
  public static String asPrintableString(Collection<?> collection, int level)
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
  
  /**
   * Duplicate <code>str</code> a specified <code>number</code> of times.<br>
   * <br>
   * If <code>number</code> has a negative value, a <code>null</code> string is returned.<br>
   * If <code>number</code> is 0, an empty string is returned.<br>
   * If <code>number</code> is 1, no duplication takes place, and <code>str</code> is returned as is.<br>
   * For all other <code>number</code> values, <code>str</code> is duplicated <code>number</code> of times and returned.<br>
   * 
   * @param str The string to be duplicated
   * @param number The number of times to duplicate it
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
  
  /**
   * Frame a text with equal signs.<br> 
   * <br>
   * @param str The string to be framed
   * @return the duplicated string 
   */
  public static String title(String str)
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
}
