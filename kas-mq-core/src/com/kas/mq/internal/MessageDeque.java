package com.kas.mq.internal;

import java.util.concurrent.LinkedBlockingDeque;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessage;

/**
 * {@link MessageDeque} is the actual container for {@link MqMessage}
 * 
 * @author Pippo
 */
public class MessageDeque extends LinkedBlockingDeque<IMqMessage<?>> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Returns the {@link MessageDeque} simple class name enclosed with chevrons.
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
