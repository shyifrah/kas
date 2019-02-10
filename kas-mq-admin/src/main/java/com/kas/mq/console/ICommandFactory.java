package com.kas.mq.console;

public interface ICommandFactory
{
  public abstract void register(String verb, ICommand cmd);
  
  public abstract void init();
  
  public abstract ICommand newCommand(String cmdText);
}