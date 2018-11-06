package com.kas.infra.base;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * A serializable {@link IObject}.
 * 
 * @author Pippo
 */
public interface ISerializable
{
  /**
   * Serialize an object to the specified output stream
   * 
   * @param ostream {@link ObjectOutputStream} to which the object will be serialized
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract void serialize(ObjectOutputStream ostream) throws IOException;
}
