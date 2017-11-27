package com.kas.infra.base.threads;

import com.kas.infra.base.IObject;

/***************************************************************************************************************
 * AKasThread is an abstract Thread which allows driven classes to implement their own version
 * of the run() method that will be executed.
 */
public abstract class AKasThread extends Thread implements IObject
{
  public AKasThread(String name)
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