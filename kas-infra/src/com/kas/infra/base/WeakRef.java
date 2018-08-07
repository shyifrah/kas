package com.kas.infra.base;

import java.lang.ref.WeakReference;

/**
 * A weak reference to an object
 * 
 * @author Pippo
 */
public class WeakRef<T>
{
  /**
   * The weak reference held
   */
  private WeakReference<T> mRef;
  
  /**
   * Construct a weak reference
   * 
   * @param referent The object to which a reference is held
   */
  public WeakRef(T referent)
  {
    mRef = new WeakReference<T>(referent);
  }
  
  /**
   * Return the object's string representation
   * 
   * @return {@code "null"} if the referent is {@code null} or the object's {@link #toString()} value
   */
  public String toString()
  {
    return mRef == null ? "null" : mRef.get() == null ? "null" : mRef.get().toString();
  }

  /**
   * Get the referent
   * 
   * @return the referent
   */
  public T get()
  {
    return mRef.get();
  }
}
