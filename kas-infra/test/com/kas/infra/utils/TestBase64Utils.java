package com.kas.infra.utils;

import org.junit.Test;
import com.kas.infra.utils.Base64Utils;
import junit.framework.Assert;

public class TestBase64Utils
{
  @Test
  public void testEncodeAndDecode()
  {
    String str = "There's No Death, There's Only The Force";
    byte [] plaintext = str.getBytes();
    byte [] cyphertext = Base64Utils.encode(plaintext);
    
    byte [] decodedtext =  Base64Utils.decode(cyphertext);
    String newstr = new String(decodedtext);
    
    Assert.assertEquals(str,  newstr);
  }
}
