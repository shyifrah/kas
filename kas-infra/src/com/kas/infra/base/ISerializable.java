package com.kas.infra.base;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * A serializable {@link IObject}.
 * 
 * @author Pippo
 */
public interface ISerializable extends IObject
{
  /**
   * Serialize an object to the specified output stream
   * 
   * @param ostream {@link ObjectOutputStream} to which the object will be serialized
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract void serialize(ObjectOutputStream ostream) throws IOException;
  
  /**
   * Returns the {@link #ISerializable} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link #ISerializable}.
   * 
   * @return a replica of this {@link #ISerializable}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IObject replicate();
  
  /**
   * Returns the {@link #ISerializable} string representation.
   * 
   * @param level the required level padding
   * 
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
