package com.kas.mq.types;

import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.AMqMessage;

/**
 * {@link QueueMap} is the actual container for {@link AMqMessage}
 * 
 * @author Pippo
 */
public class QueueMap extends ConcurrentHashMap<String, MqQueue> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Returns the {@link QueueMap} simple class name enclosed with chevrons.
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
   * Returns the {@link QueueMap} string representation.
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
      .append(pad).append("  Queues=(\n")
      .append(pad).append(StringUtils.asPrintableString(this, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
