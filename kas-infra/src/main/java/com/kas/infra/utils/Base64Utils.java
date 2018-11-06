package com.kas.infra.utils;

import java.util.Base64;

/**
 * A simplifying interface to the {@link java.util.Base64} classes
 *  
 * @author Pippo
 */
public class Base64Utils
{
  /**
   * Encode {@code plaintext} data to BASE64-encoded data
   * 
   * @param plaintext The input data to encrypt
   * @return BASE64-encoded data
   */
  static public byte [] encode(byte [] plaintext)
  {
    return Base64.getEncoder().encode(plaintext);
  }
  
  /**
   * Decode {@code cyphertext} - a BASE64-decoded data - back to plain text
   * 
   * @param cyphertext The input data to decrypt
   * @return plain text
   */
  static public byte [] decode(byte [] cyphertext)
  {
    return Base64.getDecoder().decode(cyphertext);
  }
}
