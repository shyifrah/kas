package com.kas.mq.typedef;

import java.util.concurrent.ConcurrentLinkedQueue;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;

/**
 * {@link MessageQueue} is the actual container for {@link IMqMessage}
 * 
 * @author Pippo
 */
public class MessageQueue extends ConcurrentLinkedQueue<IMqMessage> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return
   *   class name enclosed with chevrons.
   */
  public String name()
  {
    return StringUtils.getClassName(getClass());
  }
  
  /**
   * Returns the string representation.
   * 
   * @return
   *   the string representation.
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(Size=").append(size()).append(")");
    return sb.toString();
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
    return toString();
  }
}
