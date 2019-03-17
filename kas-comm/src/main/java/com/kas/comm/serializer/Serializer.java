package com.kas.comm.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An object that helps in serialization
 * 
 * @author Pippo
 */
public class Serializer
{
  /**
   * Logger
   */
  static private Logger sLogger = LogManager.getLogger(Serializer.class);
  
  /**
   * Convert {@link Serializable} object to a byte array
   * 
   * @param object
   *   The {@link Serializable} object to convert
   * @return
   *   the byte array
   */
  static public byte [] toByteArray(Serializable object)
  {
    sLogger.trace("Serializer::toByteArray() - IN");
    
    byte [] result = null;
    if (object != null)
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = null;
      try
      {
        oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.flush();
        baos.flush();
        result = baos.toByteArray();
        sLogger.trace("Serializer::toByteArray() - Converted object to byte array. byte [] size: {}", result.length);
      }
      catch (IOException e)
      {
        sLogger.trace("Serializer::toByteArray() - An exception caught while converting Serializable object to byte-array. Exception: ", e);
      }
      finally
      {
        try
        {
          if (oos != null)
          {
            oos.flush();
            oos.close();
          }
          baos.flush();
          baos.close();
        }
        catch (IOException e) {}
      }
    }
    
    sLogger.trace("Serializer::toByteArray() - OUT");
    return result;
  }
  
  /**
   * Convert a byte array to {@link Serializable} object 
   * 
   * @param bytearray
   *   The byte array to convert
   * @return
   *   the {@link Serializable} object
   */
  static public Serializable toSerializable(byte [] bytearray)
  {
    sLogger.trace("Serializer::toSerializable() - IN");
    
    Serializable result = null;
    if ((bytearray != null) && (bytearray.length > 0))
    {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytearray);
      ObjectInputStream ois = null;
      try
      {
        ois = new ObjectInputStream(bais);
        result = (Serializable)ois.readObject();
        sLogger.trace("Serializer::toSerializable() - Converted byte array to object: {}", result);
      }
      catch (IOException | ClassNotFoundException e)
      {
        sLogger.trace("Serializer::toSerializable() - An exception caught while converting byte-array to Serializable object. Exception: ", e);
      }
      finally
      {
        try
        {
          if (ois != null) ois.close();
          bais.close();
        }
        catch (IOException ex) { }
      }
    }
    
    sLogger.trace("Serializer::toSerializable() - OUT");
    return result;
  }
}
