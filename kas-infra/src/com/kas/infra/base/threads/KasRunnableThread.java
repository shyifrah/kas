package com.kas.infra.base.threads;

import com.kas.infra.base.IObject;

/***************************************************************************************************************
 * A KasRunnableThread is a Thread which accepts the Runnable object that will be executed
 * upon calling to run() method
 */
public class KasRunnableThread extends Thread implements IObject
{
  public KasRunnableThread(String name, Runnable cmd)
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
