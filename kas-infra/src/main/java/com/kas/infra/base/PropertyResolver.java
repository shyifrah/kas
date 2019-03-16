package com.kas.infra.base;

import com.kas.infra.utils.RunTimeUtils;

/**
 * A {@link PropertyResolver} is an object that resolves string values, in the case these values
 * has the following format:
 *    "...${....}..."
 * The value between the opening "${" and closing "}" can actually represent a variable.
 * A variable is a name that is associated with a value - a system property, environment variable
 * or other property inside a set of {@link Properties} object.
 * In that case, the variable will be substituted with its value.<br>
 * <br>
 * <b>Example:</b><br>
 *   If the system property "user.name" has the value of "shy1", and the {@link PropertyResolver}
 *   got the raw value of "I_am_${user.name}2345", a call to {@link #resolve(String, Properties)}
 *   will generate the return value of "I_am_shy12345".
 * 
 * @author Pippo
 */
public class PropertyResolver
{
  /**
   * Resolve the value of {@code rawValue}. 
   * 
   * @param rawValue
   *   The value that needs resolving
   * @return
   *   the resolved value
   */
  static public String resolve(String rawValue)
  {
    return resolve(rawValue, null);
  }
  
  /**
   * Resolve the value of {@code rawValue}.<br>
   * Resolving means to look for variables inside {@code rawValue}, and substitute these
   * occurrences with their associated values. To get a variable's value this method
   * calls {@link #getVarValue(String)} method. 
   * 
   * @param rawValue
   *   The value that needs resolving
   * @param props
   *   A set of {@link Properties} that is used to determine a variable's value
   * @return
   *   the resolved value
   */
  static public String resolve(String rawValue, Properties props)
  {
    StringBuilder sb = new StringBuilder();
    int startIndex = rawValue.indexOf("${");
    int endIndex = rawValue.indexOf("}");
    if (startIndex == -1 || endIndex == -1 || endIndex < startIndex)
    {
      return rawValue;
    }
    else
    {
      String prefix = rawValue.substring(0, startIndex);         // part of val before variable
      String suffix = rawValue.substring(endIndex+1);            // part of val after variable
      String var = rawValue.substring(startIndex, endIndex+1);   // the variable (including the enclosing ${...}
      sb.append(prefix);
      sb.append(getVarValue(var, props));
      sb.append(suffix);
      return resolve(sb.toString(), props);
    }
  }
  
  /**
   * Remove the enclosing "${" and "}"
   * 
   * @param var
   *   The enclosed variable name
   * @return
   *   strip the enclosing "${" and "}"
   */
  static String strip(String var)
  {
    return var.substring(2, var.length()-1);
  }
  
  /**
   * Get a value of a variable.<br>
   * The variable is treated as if it has enclosing ${...} so we first strip it.
   * First we try getting the value from system properties and environment variables.
   * If it's still {@code null}, the resolver got a set of properties upon construction,
   * we try getting its value from there.
   * 
   * @param var
   *   The variable. e.g. ${kas.user.name}
   * @param props
   *   A set of {@link Properties} which will be used in resolving
   * @return
   *   the value associated with the variable.
   */
  static String getVarValue(String var, Properties props)
  {
    String strippedvar = strip(var);
    String val = RunTimeUtils.getProperty(strippedvar);
    if ((val == null) && (props != null))
      val = props.getStringProperty(strippedvar, null);
    
    if (val == null) val = "";
    return val;
  }
}
