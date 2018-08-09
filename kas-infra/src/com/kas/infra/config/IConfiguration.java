package com.kas.infra.config;

import com.kas.infra.base.IObject;

/**
 * A configuration object is one that you can query for various types of properties values
 * 
 * @author Pippo
 */
public interface IConfiguration extends IObject
{
  /**
   * Get the value of a property named {@code key} which its value should be of type {@link String}.<br>
   * <br>
   * If the property does not exist, the {@code defaultValue} value is returned.
   * 
   * @param key The property name
   * @param defaultValue The default value which will be returned in case the property does not exist
   * @return the property's value
   */
  public abstract String getStringProperty(String key, String defaultValue);
  
  /**
   * Get the value of a property named {@code key} which its value should be of type {@code int}.<br>
   * <br>
   * If the property does not exist, the {@code defaultValue} value is returned.
   * 
   * @param key The property name
   * @param defaultValue The default value which will be returned in case the property does not exist
   * @return the property's value
   */
  public abstract int getIntProperty(String key, int defaultValue);
  
  /**
   * Get the value of a property named {@code key} which its value should be of type {@code long}.<br>
   * <br>
   * If the property does not exist, the {@code defaultValue} value is returned.
   * 
   * @param key The property name
   * @param defaultValue The default value which will be returned in case the property does not exist
   * @return the property's value
   */
  public abstract long getLongProperty(String key, long defaultValue);
  
  /**
   * Get the value of a property named {@code key} which its value should be of type {@code boolean}.<br>
   * <br>
   * If the property does not exist, the {@code defaultValue} value is returned.
   * 
   * @param key The property name
   * @param defaultValue The default value which will be returned in case the property does not exist
   * @return the property's value
   */
  public abstract boolean getBoolProperty(String key, boolean defaultValue);
  
  /**
   * Returns the {@link IConfiguration} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link IConfiguration}.
   * 
   * @return a replica of this {@link IConfiguration}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IConfiguration replicate();
  
  /**
   * Returns the {@link IConfiguration} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
