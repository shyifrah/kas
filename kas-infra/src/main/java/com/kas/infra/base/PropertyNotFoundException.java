package com.kas.infra.base;

/**
 * An exception thrown by {@link Properties} whenever
 * a property is not found
 * 
 * @author Pippo
 */
public class PropertyNotFoundException extends PropertyException
{
  private static final long serialVersionUID = -4905784442984423530L;
  
  public PropertyNotFoundException()
  {
    super("Property not found");
  }
  
  public PropertyNotFoundException(String name)
  {
    super("Property not found", name);
  }
}
