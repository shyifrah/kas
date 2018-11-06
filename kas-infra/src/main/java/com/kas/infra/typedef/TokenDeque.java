package com.kas.infra.typedef;

import java.util.ArrayDeque;

/**
 * A {@link TokenDeque} is a means for passing several tokens as a Deque of Strings
 * 
 * @author Pippo
 */
public class TokenDeque extends ArrayDeque<String>
{
  private static final long serialVersionUID = 1L;
  
  private String mOriginalString;
  
  /**
   * Construct a {@link TokenDeque} with the specified string {@code str}
   * 
   * @param str The string to tokenize
   */
  public TokenDeque(String str)
  {
    mOriginalString = str;
    String [] a = str.split(" ");
    for (String word : a)
      super.offer(word);
  }
  
  /**
   * Get the original string
   * 
   * @return the original string
   */
  public String getOriginalString()
  {
    return mOriginalString;
  }
}