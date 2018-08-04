package com.kas.infra.base;

/**
 * A KAS exception.
 * 
 * @author Pippo
 */
public class KasException extends Exception
{
  /**
   * 
   */
  private static final long   serialVersionUID = 1L;
  private static final String cDefaultMessage = "KAS Exception";

  /**
   * Construct a default {@code KasException} object.
   */
  public KasException()
  {
    this(cDefaultMessage, null);
  }
  
  /**
   * Construct a {@code KasException} object.
   * 
   * @param message the exception message
   */
  public KasException(String message)
  {
    this(cDefaultMessage + ": " + message, null);
  }
  
  /**
   * Construct a {@code KasQException} object with the associated cause
   * 
   * @param cause the causer exception
   */
  public KasException(Throwable cause)
  {
    this(cDefaultMessage, cause);
  }
  
  /**
   * Construct a {@code KasException} object with the associated message and cause
   * 
   * @param message the exception message
   * @param cause the causer exception
   */
  public KasException(String message, Throwable cause)
  {
    super(cDefaultMessage + ": " + message, cause);
  }
}
