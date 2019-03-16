package com.kas.infra.typedef;

import java.util.ArrayList;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * A {@link StringList} is simply a list of strings implementing the {@link IObject} interface.
 * 
 * @author Pippo
 */
public class StringList extends ArrayList<String> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Create a StringList object from a comma-separated list which is enclosed with brackets
   *  
   * @param string
   *   The comma-separated list
   * @return
   *   the StringList object
   */
  static public StringList fromString(String string)
  {
    return fromString(string, true);
  }

  /**
   * Create a StringList object from a comma-separated list which is enclosed with brackets
   *  
   * @param string
   *   The comma-separated list
   * @param enclosed
   *   Whether {@code string} is enclosed with brackets
   * @return
   *   the StringList object
   */
  static public StringList fromString(String string, boolean enclosed)
  {
    StringList result = new StringList();
    if (string == null)
      return result;
    
    String [] array;
    if (!enclosed)
      array = string.split(",");
    else if (!string.startsWith("["))
      return result;
    else if (!string.endsWith("]"))
        return result;
    else
      array = string.substring(1, string.length()-1).split(",");
    
    if (array.length == 0)
      return result;
    
    for (String str : array)
    {
      if (str.length() > 0)
        result.add(str.trim());
    }
    return result;
  }

  /**
   * Returns the {@link StringList} simple class name enclosed with chevrons.
   * 
   * @return
   *   class name enclosed with chevrons.
   */
  public String name()
  {
    return StringUtils.getClassName(getClass());
  }

  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = StringUtils.getPadding(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Size=").append(size()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
