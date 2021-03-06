package com.kas.infra.config;

import com.kas.infra.base.Properties;

/**
 * A configuration object is one that you can query for various types of properties values
 * 
 * @author Pippo
 */
public interface IConfiguration
{
  /**
   * Get the value of a property named {@code key} which its value should be of type {@link String}.<br>
   * <br>
   * If the property does not exist, the {@code defaultValue} value is returned.
   * 
   * @param key
   *   The property name
   * @param defaultValue
   *   The default value which will be returned in case the property does not exist
   * @return
   *   the property's value
   */
  public abstract String getStringProperty(String key, String defaultValue);
  
  /**
   * Get the value of a property named {@code key} which its value should be of type {@code int}.<br>
   * <br>
   * If the property does not exist, the {@code defaultValue} value is returned.
   * 
   * @param key
   *   The property name
   * @param defaultValue
   *   The default value which will be returned in case the property does not exist
   * @return
   *   the property's value
   */
  public abstract int getIntProperty(String key, int defaultValue);
  
  /**
   * Get the value of a property named {@code key} which its value should be of type {@code long}.<br>
   * <br>
   * If the property does not exist, the {@code defaultValue} value is returned.
   * 
   * @param key
   *   The property name
   * @param defaultValue
   *   The default value which will be returned in case the property does not exist
   * @return
   *   the property's value
   */
  public abstract long getLongProperty(String key, long defaultValue);
  
  /**
   * Get the value of a property named {@code key} which its value should be of type {@code boolean}.<br>
   * <br>
   * If the property does not exist, the {@code defaultValue} value is returned.
   * 
   * @param key
   *   The property name
   * @param defaultValue
   *   The default value which will be returned in case the property does not exist
   * @return
   *   the property's value
   */
  public abstract boolean getBoolProperty(String key, boolean defaultValue);
  
  /**
   * Get a subset of the {@link IConfiguration} object.
   * 
   * @param keyPrefix
   *   The prefix of the keys to include in the subset
   * @return
   *   a new {@link Properties} object including only keys that are prefixed with {@code keyPrefix}
   */
  public abstract Properties getSubset(String keyPrefix);
  
  /**
   * Get a subset of the {@link IConfiguration} object.
   * 
   * @param keyPrefix
   *   The prefix of the keys to include in the subset
   * @param keySuffix
   *   The suffix of the keys to include in the subset
   * @return
   *   a new {@link Properties} object including only keys that are prefixed
   *   with {@code keyPrefix} <b>AND</b> suffixed with {@code keySuffix}
   */
  public abstract Properties getSubset(String keyPrefix, String keySuffix);
}
