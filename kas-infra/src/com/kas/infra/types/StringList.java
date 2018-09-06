package com.kas.infra.types;

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
   * Returns the {@link TokenDeque} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public String name()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
  }

  /**
   * Returns the {@link StringList} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
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
