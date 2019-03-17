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
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
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
