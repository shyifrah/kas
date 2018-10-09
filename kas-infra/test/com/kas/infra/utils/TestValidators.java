package com.kas.infra.utils;

import org.junit.Test;
import com.kas.infra.utils.Validators;
import junit.framework.Assert;

public class TestValidators
{
  @Test
  public void testIpAddressValidator()
  {
    String [] badAddresses = {
      "0",
      "A",
      "1.2",
      "1.2.3",
      "1.2.3.4.",
      ".1.1.1.1",
      "1111",
      "1.1..1",
      "256.100.1.A",
      "256.100.@.1"
    };
    
    for (String ip : badAddresses)
      Assert.assertFalse( Validators.isIpAddress(ip) );
    
    String [] goodAddresses = {
      "0.0.0.0",
      "0.0.0.1",
      "4.100.1.3",
      "255.255.255.255"
    };
                            
    for (String ip : goodAddresses)
      Assert.assertTrue( Validators.isIpAddress(ip) );
  }
  
  @Test
  public void testHostNameValidator()
  {
    String [] badAddresses = {
      "",
      ".",
      "@",
      "aaa.",
      ".aaa"
    };
    
    for (String host : badAddresses)
      Assert.assertFalse( Validators.isHostName(host) );
    
    String [] goodAddresses = {
      "a",
      "1",
      "ab",
      "ab.c",
      "a.b.c.d.e.f.g.h"
    };
    
    for (String host : goodAddresses)
      Assert.assertTrue( Validators.isHostName(host) );
  }
  
  @Test
  public void testPortValidator()
  {
    Assert.assertFalse ( Validators.isPort(0)      );
    Assert.assertFalse ( Validators.isPort(-1)     );
    Assert.assertTrue  ( Validators.isPort(1)      );
    Assert.assertTrue  ( Validators.isPort(433)    );
    Assert.assertTrue  ( Validators.isPort(65535)  );
    Assert.assertFalse ( Validators.isPort(65536)  );
    Assert.assertFalse ( Validators.isPort(103336) );
  }
  
  @Test
  public void testQueueNameValidator()
  {
    String [] badNames = {
      "",
      "1",
      "1aaa",
      "a123456789a123456789a123456789a123456789a12345678",
      "a@aa",
      "a$aa",
      "a#aa",
      "a!aa",
      "_a1_q.d",
      ".a1_q.d",
    };
    
    for (String name : badNames)
      Assert.assertFalse( Validators.isQueueName(name) );
    
    String [] goodNames = {
      "a",
      "a1",
      "a1_q.d",
      "a123456789a123456789a123456789a123456789a1234567",
    };
    
    for (String name : goodNames)
      Assert.assertTrue( Validators.isQueueName(name) );
  }
  
  @Test
  public void testUserNameValidator()
  {
    String [] badNames = {
      "",
      "1",
      "1aaa",
      "a@aa",
      "a$aa",
      "a#aa",
      "a!aa",
      "_a1_q.d",
      ".a1_q.d",
    };
    
    for (String name : badNames)
      Assert.assertFalse( Validators.isUserName(name) );
    
    String [] goodNames = {
      "a",
      "a1",
      "a1_q.d",
      "a123456789a123456789a123456789a123456789a1234567",
    };
    
    for (String name : goodNames)
      Assert.assertTrue( Validators.isUserName(name) );
  }
}
