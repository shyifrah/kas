package com.kas.infra.base;

import java.util.List;

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
   * @param id The object ID that is associated with the object to be retrieved
   * @return the specific object, if found, or {@code null} if not
   */
  public abstract T get(UniqueId id);
  
  /**
   * Get a list of all objects managed by this DAO
   * 
   * @return a list of all objects
   */
  public abstract List<T> getAll();
   
  /**
   * Save current object to the data layer
   * 
   * @param t The object to be saved
   */
  public abstract void save(T t);
   
  /**
   * Update a specific object
   * 
   * @param t The object to be updated
   * @param params A list of parameters to be updated within the object
   */
  public abstract void update(T t, String[] params);
   
  /**
   * Delete the specified object
   * 
   * @param t The object to be deleted
   */
  public abstract void delete(T t);
}