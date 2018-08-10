package com.kas.mq.typedef;

import java.util.ArrayDeque;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

public class TokenQueue extends ArrayDeque<String> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Returns the {@link TokenQueue} simple class name enclosed with chevrons.
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
   * Returns a replica of this {@link TokenQueue}.
   * 
   * @return a replica of this {@link TokenQueue}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public TokenQueue replicate()
  {
    TokenQueue q = new TokenQueue();
    for (String token : this)
    {
      q.offer(token);
    }
    return q;
  }
  
  /**
   * Returns the {@link MessageDeque} string representation.
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