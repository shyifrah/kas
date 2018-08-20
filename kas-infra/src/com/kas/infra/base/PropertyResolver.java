package com.kas.infra.base;

import com.kas.infra.utils.RunTimeUtils;

/**
 * A {@link PropertyResolver} is an object that holds a string value, but if this string has the following format
 * "...${....}...", then the value between the opening "${" and closing "}" can actually represent a variable -
 * a system property. In that case, the value of this system property will take place in the value
 * of the PropertyValue.<br>
 * <br>
 * <b>Example:</b><br>
 *   If the system property "user.name" has the value of "shy1", and a PropertyValue has the raw value of "I_am_${user.name}2345"
 *   a call to {@link #getActual()} will return the value of "I_am_shy12345".
 * 
 * @author Pippo
 */
class PropertyResolver extends AKasObject
{
  /**
   * The raw value of this {@link PropertyResolver}
   */
  private String  mRawValue;
  
  /**
   * The actual value of this {@link PropertyResolver}
   */
  private String  mActualValue = null;
  
  /**
   * An indicator whether this {@link PropertyResolver} has already been resolved
   */
  private boolean mResolved = false;
  
  /**
   * Construct a {@link PropertyResolver} object with the specified raw value.
   * 
   * @param rawValue The raw value of this {@link PropertyResolver}.
   */
  public PropertyResolver(String rawValue)
  {
    mRawValue = rawValue;
  }
  
  /**
   * Resolve the raw value of this object.
   * 
   * @return the actual value.
   */
  private String resolve()
  {
    return resolve(mRawValue);
  }
  
  /**
   * Resolve a raw value.<br>
   * <br>
   * A raw value is the string value as-is. The resolved value is one that replaces all occurrences of
   * "${var}" to their actual value.
   * 
   * @param rawValue The raw value
   * @return the actual value.
   */
  private String resolve(String rawValue)
  {
    String result = rawValue;
    
    if (mResolved) return mActualValue;

    int startIdx = rawValue.indexOf("${");
    int endIdx   = rawValue.indexOf('}');
    if ((startIdx == -1) || (endIdx == -1) || (startIdx >= endIdx))
    {
      mResolved = true;
      return rawValue;
    }
    
    String var = rawValue.substring(startIdx+2, endIdx);
    String val = RunTimeUtils.getProperty(var);
    
    StringBuilder sb = new StringBuilder();
    sb.append(rawValue.substring(0, startIdx));
    sb.append(val);
    sb.append(rawValue.substring(endIdx+1));
    result = sb.toString();
    
    return resolve(result);
  }
  
  /**
   * Get the actual property's value.<br>
   * <br>
   * If the property's value hasn't been resolved yet, resolve it and then return the actual value.
   * 
   * @return the actual value.
   */
  public String getActual()
  {
    if (!mResolved)
      mActualValue = resolve();
    
    return mActualValue;
  }
  
  /**
   * Get the object's detailed string representation.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    StringBuilder sb = new StringBuilder(); // [Raw=(...)] = [Actual=(..)]
    sb.append(name()).append("(")
      .append("[Raw=(").append(mRawValue).append(")]")
      .append(" = [Actual=(").append(getActual()).append(")]")
      .append(")");
    return sb.toString();
  }
}
