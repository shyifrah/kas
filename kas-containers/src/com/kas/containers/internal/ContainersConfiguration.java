package com.kas.containers.internal;

import com.kas.config.impl.AbstractConfiguration;
import com.kas.config.impl.Constants;
import com.kas.infra.config.IConfiguration;

public class ContainersConfiguration extends AbstractConfiguration
{
  private IConfiguration mConfig = null;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void refresh()
  {
    mConfig = mMainConfig.getSubset(Constants.cContainersConfigPrefix);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getContainerCap(String name)
  {
    String sizeKey = Constants.cContainersConfigPrefix + name + ".size";
    return mConfig.getIntProperty(sizeKey, Constants.cDefaultContainerNotFoundSize);
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
