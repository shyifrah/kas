package com.kas.infra.base;

/**
 * Invalid property value exception thrown by {@link Properties}
 * 
 * @author Pippo
 */
public class InvalidPropertyValueException extends PropertyException
{
  private static final long serialVersionUID = -3295674607847232087L;
  
  private Object mPropertyValue = null;
  
  public InvalidPropertyValueException()
  {
    super("Invalid property value");
  }
  
  public InvalidPropertyValueException(String name, Object value)
  {
    super("Invalid property value", name);
    mPropertyValue = value;
  }
  
  public Object getPropertyValue()
  {
    return mPropertyValue;
  }
}
