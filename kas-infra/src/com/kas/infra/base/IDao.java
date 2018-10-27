package com.kas.infra.base;

import java.util.List;
import java.util.Map;

/**
 * A DAO interface for managing object in a data layer
 * 
 * @author Pippo
 *
 * @param <T>
 */
public interface IDao<T> extends IObject
{
  /**
   * Get a specific object
   * 
   * @param name The name associated with the object to be retrieved
   * @return the specific object, if found, or {@code null} if not
   */
  public abstract T get(int id);
  
  /**
   * Get a list of all objects managed by this DAO
   * 
   * @return a list of all objects
   */
  public abstract List<T> getAll();
   
  /**
   * Save specified object to the data layer
   * 
   * @param t The object to be saved
   */
  public abstract void save(T t);
   
  /**
   * Update a specific object
   * 
   * @param t The object to be updated
   * @param params A map of fields-to-values to be updated within the object
   */
  public abstract void update(T t, Map<String, String> params);
   
  /**
   * Delete the specified object
   * 
   * @param t The object to be deleted
   */
  public abstract void delete(T t);
}
