package com.kas.infra.base;

/**
 * A KAS exception.
 * 
 * @author Pippo
 */
public class KasException extends Exception
{
  static private final long   serialVersionUID = 1L;
  static private final String cDefaultMessage = "KAS Exception";

  /**
   * Construct a default {@link KasException} object.
   */
  public KasException()
  {
    this(cDefaultMessage, null);
  }
  
  /**
   * Construct a {@link KasException} object.
   * 
   * @param message the exception message
   */
  public KasException(String message)
  {
    this(message, null);
  }
  
  /**
   * Construct a {@link KasQException} object with the associated cause
   * 
   * @param cause the causer exception
   */
  public KasException(Throwable cause)
  {
    this(cDefaultMessage, cause);
  }
  
  /**
   * Construct a {@link KasException} object with the associated message and cause
   * 
   * @param message the exception message
   * @param cause the causer exception
   */
  public KasException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
