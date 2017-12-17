package com.kas.q.server.typedef;

import java.util.ArrayDeque;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

public class CommandQueue extends ArrayDeque<String> implements IObject
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
      .append(pad).append("  Queue=(\n");
    
    for (String str : this)
      sb.append(str).append("\n");
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
