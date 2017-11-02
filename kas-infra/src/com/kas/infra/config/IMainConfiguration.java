package com.kas.infra.config;

import java.util.Set;
import com.kas.infra.base.IInitializable;

public interface IMainConfiguration extends IConfiguration, IInitializable
{
  public void reload();
  
  public Set<String> getConfigFiles();
  public String      getConfigDir();
}
