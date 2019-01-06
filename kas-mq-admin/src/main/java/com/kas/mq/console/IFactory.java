package com.kas.mq.console;

public interface IFactory
{
  public abstract void register(String verb, ICommand cmd);
  
  public abstract void init();
}