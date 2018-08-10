package com.kas.mq.impl;

import java.util.concurrent.LinkedBlockingDeque;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * {@link KasMessageDeque} is the actual container for {@link KasMessage}
 */
public class KasMessageDeque extends LinkedBlockingDeque<KasMessage> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Returns the {@link KasMessageDeque} simple class name enclosed with chevrons.
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
   * Returns a replica of this {@link KasMessageDeque}.<br>
   * <br>
   * All messages in this {@link KasMessageDeque} are replicated as well and put in the replica.
   * 
   * @return a replica of this {@link KasMessageDeque}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public KasMessageDeque replicate()
  {
    KasMessageDeque md = new KasMessageDeque();
    for (KasMessage msg : this)
    {
      md.offer(msg.replicate());
    }
    return md;
  }
  
  /**
   * Returns the {@link KasMessageDeque} string representation.
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
