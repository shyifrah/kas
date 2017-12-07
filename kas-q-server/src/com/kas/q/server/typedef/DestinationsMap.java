package com.kas.q.server.typedef;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.q.ext.IKasqDestination;

public class DestinationsMap extends ConcurrentHashMap<String, IKasqDestination> implements IObject
{
  private static final long serialVersionUID = 1L;

  public String name()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
  }

  public String toPrintableString(int level)
  {
    String pad = StringUtils.getPadding(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Entries=(\n");
    
    for (Map.Entry<String, IKasqDestination> entry : entrySet())
    {
      String key = entry.getKey();
      IKasqDestination value = entry.getValue();
      sb.append(pad).append("    ").append(key).append('=').append(value.getFormattedName()).append('\n');
    }
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}