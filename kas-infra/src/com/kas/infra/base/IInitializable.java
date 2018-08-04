package com.kas.infra.base;

/**
 * Implementors of this interface are usually classes that requires some sort of initialization
 * prior their usage, and optionally, requires some sort of termination as well.
 * 
 * @author Pippo
 */
public interface IInitializable extends IObject
{
  /**
   * Initialize the object
   * 
   * @return {@code true} if object was initialized successfully, {@code false} otherwise
   */
  public abstract boolean init();
  
  /**
   * Terminate the object
   * 
   * @return {@code true} if object was terminated successfully, {@code false} otherwise
   */
  public abstract boolean term();
  
  /**
   * Returns the {@link #IInitializable} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link #IInitializable}.
   * 
   * @return a replica of this {@link #IInitializable}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IObject replicate();
  
  /**
   * Returns the {@link #IInitializable} string representation.
   * 
   * @param level the required level padding
   * 
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
