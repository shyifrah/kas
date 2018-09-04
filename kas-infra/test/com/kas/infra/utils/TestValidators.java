package com.kas.infra.utils;


public class TestValidators
{
  static public void main(String [] args)
  {
    String [] cQueueNames = {
      "ABC",
      "A.B.C",
      ".A.B.C",
      "AB*"
    };
    
    for (int i = 0; i < cQueueNames.length; ++i)
    {
      String qn = cQueueNames[i];
      System.out.println("Queue Name........................: " + qn);
      System.out.println("   Valid queue name..........: " + Validators.isQueueName(qn));
    }
  }
}
