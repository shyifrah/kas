package com.kas.containers.internal;

import com.kas.config.impl.AConfiguration;
import com.kas.infra.config.IConfiguration;

public class ContainersConfiguration extends AConfiguration
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static final String cContainersConfigPrefix = "kas.container.";
  
  public static final int    cDefaultContainerNotFoundSize = -1;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private IConfiguration mConfig = null;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void refresh()
  {
    mConfig = mMainConfig.getSubset(cContainersConfigPrefix);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getContainerCap(String name)
  {
    String sizeKey = cContainersConfigPrefix + name + ".size";
    return mConfig.getIntProperty(sizeKey, cDefaultContainerNotFoundSize);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Properties=(").append(mConfig.toPrintableString(level + 1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
