package com.kas.comm.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

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
  static private ILogger sLogger = LoggerFactory.getLogger(Serializer.class);
  
  /**
   * Convert {@link Serializable} object to a byte array
   * 
   * @param object The {@link Serializable} object to convert
   * @return the byte array
   */
  static public byte [] toByteArray(Serializable object)
  {
    sLogger.diag("Serializer::toByteArray() - IN");
    
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
        sLogger.diag("Serializer::toByteArray() - Converted object to byte array. byte [] size: " + result.length);
      }
      catch (IOException e)
      {
        sLogger.diag("Serializer::toByteArray() - An exception caught while converting Serializable object to byte-array. Exception: ", e);
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
    
    sLogger.diag("Serializer::toByteArray() - OUT");
    return result;
  }
  
  /**
   * Convert a byte array to {@link Serializable} object 
   * 
   * @param bytearray The byte array to convert
   * @return the {@link Serializable} object
   */
  static public Serializable toSerializable(byte [] bytearray)
  {
    sLogger.diag("Serializer::toSerializable() - IN");
    
    Serializable result = null;
    if ((bytearray != null) && (bytearray.length > 0))
    {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytearray);
      ObjectInputStream ois = null;
      try
      {
        ois = new ObjectInputStream(bais);
        result = (Serializable)ois.readObject();
        sLogger.diag("Serializer::toSerializable() - Converted byte array to object: " + result.toString());
      }
      catch (IOException | ClassNotFoundException e)
      {
        sLogger.diag("Serializer::toSerializable() - An exception caught while converting byte-array to Serializable object. Exception: ", e);
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
    
    sLogger.diag("Serializer::toSerializable() - OUT");
    return result;
  }
}
