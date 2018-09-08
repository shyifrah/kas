package com.kas.mq.typedef;

import java.util.concurrent.ConcurrentLinkedQueue;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.AMqMessage;

/**
 * {@link MessageQueue} is the actual container for {@link AMqMessage}
 * 
 * @author Pippo
 */
public class MessageQueue extends ConcurrentLinkedQueue<IMqMessage<?>> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Returns the {@link MessageQueue} simple class name enclosed with chevrons.
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
   * Returns the {@link MessageQueue} string representation.
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
