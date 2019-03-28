package com.kas.infra.base;

/**
 * Exception thrown by {@link Properties} whenever a property value
 * is referenced in an invalid manner
 * 
 * @author Pippo
 */
public class InvalidPropertyValueException extends PropertyException
{
  private static final long serialVersionUID = -3295674607847232087L;
  
  /**
   * The value of the property causing the exception
   */
  private Object mPropertyValue = null;
  
  /**
   * Construct the exception
   */
  public InvalidPropertyValueException()
  {
    super("Invalid property value");
  }
  
  /**
   * Construct the exception using the specified name and value
   * 
   * @param name
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public InvalidPropertyValueException(String name, Object value)
  {
    super("Invalid property value", name);
    mPropertyValue = value;
  }

  /**
   * Get the value of the property
   * 
   * @return
   *   the value of the property
   */
  public Object getPropertyValue()
  {
    return mPropertyValue;
  }
}
