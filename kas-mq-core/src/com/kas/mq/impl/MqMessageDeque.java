package com.kas.mq.impl;

import java.util.concurrent.LinkedBlockingDeque;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * {@link MqMessageDeque} is the actual container for {@link MqMessage}
 * 
 * @author Pippo
 */
public class MqMessageDeque extends LinkedBlockingDeque<MqMessage> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Returns the {@link MqMessageDeque} simple class name enclosed with chevrons.
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
   * Returns a replica of this {@link MqMessageDeque}.<br>
   * <br>
   * All messages in this {@link MqMessageDeque} are replicated as well and put in the replica.
   * 
   * @return a replica of this {@link MqMessageDeque}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public MqMessageDeque replicate()
  {
    MqMessageDeque md = new MqMessageDeque();
    for (MqMessage msg : this)
    {
      md.offer(msg.replicate());
    }
    return md;
  }
  
  /**
   * Returns the {@link MqMessageDeque} string representation.
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
