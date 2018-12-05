package com.kas.comm.serializer;

import java.util.concurrent.ConcurrentHashMap;
import com.kas.comm.IPacket;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * A {@link ClassIdMap} is simply a concurrent map of class IDs to classes
 * 
 * @author Pippo
 */
public class ClassIdMap extends ConcurrentHashMap<EClassId, Class<? extends IPacket>> implements IObject
{
  private static final long serialVersionUID = 1L;

  /**
   * Returns the {@link ClassIdMap} simple class name enclosed with chevrons.
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
   * Returns the {@link ClassIdMap} string representation.
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
