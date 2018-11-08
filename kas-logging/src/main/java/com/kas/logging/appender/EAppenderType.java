package com.kas.logging.appender;

import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.IObject;

/**
 * This enum is used to categorize appenders' types
 * 
 * @author Pippo
 * 
 * @deprecated
 */
public enum EAppenderType implements IObject
{
  /**
   * Unknown appender type
   */
  Unknown,
  
  /**
   * File appender type
   */
  File,
  
  /**
   * STDOUT appender
   */
  Stdout,
  
  /**
   * STDERR appender
   */
  Stderr,
    
  ;
  
  static final private EAppenderType [] cValues = EAppenderType.values();
  static final Map<String, EAppenderType> cTypesByNameMap = new HashMap<String, EAppenderType>();  
  
  static
  {
    for (EAppenderType at : cValues)
      cTypesByNameMap.put(at.name().toLowerCase(), at);
  };
  
  /**
   * Get the enum value by its ordinal.
   * 
   * @param id The ordinal of the enum
   * @return the enum value
   */
  static public EAppenderType fromInt(int id)
  {
    EAppenderType type = Unknown;
    try
    {
      type = cValues[id];
    }
    catch (Throwable e) {}
    return type;
  }
  
  /**
   * Get the enum value by its name
   * 
   * @param str The ordinal of the enum
   * @return the enum value
   */
  static public EAppenderType fromString(String str)
  {
    EAppenderType type = Unknown;
    if (str == null)
      return type;
    
    type = cTypesByNameMap.get(str.toLowerCase());
    if (type == null)
      type = Unknown;
    return type;
  }
  
  /**
   * Return a more friendly string
   * 
   * @return the name of the constant in addition to the ordinal value
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder()
      .append(name())
      .append(" (")
      .append(ordinal())
      .append(')');
    return sb.toString();
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
    return toString();
  }
}
