package com.kas.infra.typedef;

import java.util.ArrayDeque;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * A {@link TokenDeque} is a means for passing several tokens as a Deque of Strings
 * 
 * @author Pippo
 */
public class TokenDeque extends ArrayDeque<String> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  private String mOriginalString;
  
  /**
   * Construct a {@link TokenDeque} with the specified string {@code str}
   * 
   * @param str The string to tokenize
   */
  public TokenDeque(String str)
  {
    mOriginalString = str;
    String [] a = str.split(" ");
    for (String word : a)
      super.offer(word);
  }
  
  /**
   * Get the original string
   * 
   * @return the original string
   */
  public String getOriginalString()
  {
    return mOriginalString;
  }
  
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
   * Returns the {@link TokenDeque} string representation.
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