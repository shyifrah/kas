package com.kas.infra.base;

import java.io.IOException;
import java.io.ObjectOutputStream;

public interface ISerializable extends IObject
{
  /***************************************************************************************************************
   * Serialize an object to the specified output stream
   * 
   * @param ostream OutputStream to which the object will be serialized
   * 
   * @throws IOException 
   */
  public abstract void serialize(ObjectOutputStream ostream) throws IOException;
}
