package com.kas.q.server.admin;

public class DefineProcessor implements Runnable
{
  private String [] mArgs;
  
  public DefineProcessor(String [] args)
  {
    mArgs = args;
  }
  
  public void run()
  {
    
  }
  
  private void write(String message)
  {
    System.out.println(message);
  }
}
