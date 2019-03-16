package com.kas.infra.base;

/**
 * General purpose exception used by {@link Properties}
 * 
 * @author Pippo
 */
public class PropertyException extends Exception
{
  protected static final long serialVersionUID = -4905784442984423530L;
  
  protected String mPropertyName  = null;
  
  public PropertyException(String message)
  {
    super(message);
  }
  
  public PropertyException(String message, String name)
  {
    super(message + ": " + name);
    mPropertyName = name;
  }
  
  public String getPropertyName()
  {
    return mPropertyName;
  }
}
