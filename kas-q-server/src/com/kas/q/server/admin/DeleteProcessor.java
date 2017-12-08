package com.kas.q.server.admin;

public class DeleteProcessor implements Runnable
{
  private String [] mArgs;
  
  public DeleteProcessor(String [] args)
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
