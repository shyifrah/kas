package com.kas.infra.base;

public class KasException extends Exception
{
  /****
   * 
   */
  private static final long   serialVersionUID = 1L;
  private static final String cDefaultMessage = "KAS Exception";

  /****
   * Construct a default {@code KasQException} object.
   */
  public KasException()
  {
    super(cDefaultMessage);
  }
  
  /****
   * Construct a {@code KasQException} object.
   * 
   * @param message the exception message
   */
  public KasException(String message)
  {
    super(cDefaultMessage + ": " + message);
  }
  
  /****
   * Construct a {@code KasQException} object.
   * 
   * @param message the exception message
   * @param cause the causer exception
   */
  public KasException(String message, Throwable cause)
  {
    super(cDefaultMessage + ": " + message, cause);
  }
  
  /****
   * Construct a {@code KasQException} object.
   * 
   * @param cause the causer exception
   */
  public KasException(Throwable cause)
  {
    super(cause);
  }
}
