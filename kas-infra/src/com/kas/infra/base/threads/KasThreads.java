package com.kas.infra.base.threads;

import com.kas.infra.base.IObject;

public class KasThreads
{
  /***************************************************************************************************************
   * AThread is an abstract Thread which allows subclasses to implement their own version of the run() method
   */
  public static abstract class AThread extends Thread implements IObject
  {
    public AThread(String name)
    {
      super(name);
    }
    
    public abstract void run();
    
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
      return name();
    }
  }
  
  /***************************************************************************************************************
   * RunThread i an Thread which accepts the Runnable object that will be executed upon calling to run() method
   */
  public static class RunnableThread extends Thread implements IObject
  {
    public RunnableThread(String name, Runnable cmd)
    {
      super(cmd, name);
    }
    
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
      return name();
    }
  }
}
