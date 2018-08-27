package com.kas.infra.utils;

public class TestBase64Utils
{
  static public void main(String [] args)
  {
    String str = "Shy Ifrah is the best";
    byte [] plaintext = str.getBytes();
    byte [] cyphertext = Base64Utils.encode(plaintext);
    
    byte [] decodedtext =  Base64Utils.decode(cyphertext);
    String newstr = new String(decodedtext);
    
    System.out.println("Original string is....: " + str);
    System.out.println("Plain length......: " + plaintext.length);
    System.out.println("Cyphered length...: " + cyphertext.length);
    System.out.println("Resulted string is....: " + newstr);
  }
}
