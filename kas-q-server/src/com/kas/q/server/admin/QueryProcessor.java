package com.kas.q.server.admin;

public class QueryProcessor implements Runnable
{
  private String [] mArgs;
  
  public QueryProcessor(String [] args)
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
