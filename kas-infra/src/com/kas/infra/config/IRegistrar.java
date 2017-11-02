package com.kas.infra.config;

public interface IRegistrar
{
  public void register(IListener listener);
  public void unregister(IListener listener);
}
