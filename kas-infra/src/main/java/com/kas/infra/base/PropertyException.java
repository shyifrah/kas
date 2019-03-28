package com.kas.infra.base;

/**
 * General purpose exception used by {@link Properties}
 * 
 * @author Pippo
 */
public class PropertyException extends KasException
{
  protected static final long serialVersionUID = -4905784442984423530L;
  
  /**
   * The property causing the exception
   */
  protected String mPropertyName  = null;
  
  /**
   * Construct the exception using the specified message
   * 
   * @param message
   *   The error message
   */
  public PropertyException(String message)
  {
    super(message);
  }
  
  /**
   * Construct the exception using the specified message and property name
   * 
   * @param message
   *   The error message
   * @param name
   *   The name of the property
   */
  public PropertyException(String message, String name)
  {
    super(message + ": " + name);
    mPropertyName = name;
  }
  
  /**
   * Get the property causing the exception
   * 
   * @return
   *   the property causing the exception
   */
  public String getPropertyName()
  {
    return mPropertyName;
  }
}
