package com.kas.q.server.admin;

import com.kas.q.ext.KasqClient;

public class DeleteProcessor implements Runnable
{
  private KasqClient mClient;
  private String []  mArgs;
  
  public DeleteProcessor(KasqClient client, String [] args)
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
