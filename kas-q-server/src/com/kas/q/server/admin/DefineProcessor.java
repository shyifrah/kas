package com.kas.q.server.admin;

import com.kas.q.ext.KasqClient;

public class DefineProcessor implements Runnable
{
  private KasqClient mClient;
  private String []  mArgs;
  
  public DefineProcessor(KasqClient client, String [] args)
  {
    mClient = client;
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
