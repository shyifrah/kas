package com.kas.mq.typedef;

import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.MqQueue;

/**
 * {@link QueueMap} is the actual container for {@link com.kas.mq.impl.messages.IMqMessage}.<br>
 * <br>
 * It is actually an extension of {@link ConcurrentHashMap} that maps a name to the associated {@link MqQueue}.
 * The {@link MqQueue} object can be either {@link com.kas.mq.internal.MqLocalQueue local} or {@link com.kas.mq.internal.MqRemoteQueue remote}.
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
      .append(pad).append(")");
    return sb.toString();
  }
}
