package com.kas.mq.server.resp;

import com.kas.infra.base.AKasObject;

/**
 * A response object is a response from the {@link SessionResponder} following a request.<br>
 * <br>
 * It is initialized with a positive response (no text message, code=0 and continue=true).
 * 
 * @author Pippo
 */
public class Response extends AKasObject
{
  /**
   * Response message
   */
  private String mMessage = "";
  
  /**
   * Response code
   */
  private int mCode = 0;
  
  /**
   * General indication if response is good (continue to next request)
   */
  private boolean mContinue = true;
  
  /**
   * Construct a response with the default values
   */
  public Response()
  {
  }
  
  /**
   * Construct a response with the specified values.
   * 
   * @param message The response message
   * @param code The response code
   * @param cont The response indicator
   */
  public Response(String message, int code, boolean cont)
  {
    mMessage = message;
    mCode = code;
    mContinue = cont;
  }
  
  /**
   * Get the response message
   * 
   * @return the response message
   */
  public String getMessage()
  {
    return mMessage;
  }
  
  /**
   * Get the response code
   * 
   * @return the response code
   */
  public int getCode()
  {
    return mCode;
  }
  
  /**
   * Should continue following this {@link Response} is processed
   * 
   * @return {@code true} if should process next request, {@code false} otherwise
   */
  public boolean shouldContinue()
  {
    return mContinue;
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Continue=").append(mContinue).append("\n")
      .append(pad).append("  Code=").append(mCode).append("\n")
      .append(pad).append("  Message=").append(mMessage).append("\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
