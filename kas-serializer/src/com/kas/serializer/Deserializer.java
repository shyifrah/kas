package com.kas.serializer;

import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.kas.infra.base.IObject;
import com.kas.infra.base.KasException;

/**
 * An object that is responsible for deserialization
 * 
 * @author Pippo
 */
public class Deserializer
{
  static private SerializerConfiguration sConfig = new SerializerConfiguration();
  
  /**
   * Deserialize an object with class id {@code id} from {@code istream}.
   * 
   * @param id The class ID as defined in configuration
   * @param istream The {@link ObjectInputStream} which holds the object to be deserialized
   * @return a {@link IObject} that was dynamically reconstructed from the stream
   * 
   * @throws KasException if some sort of reflection error occurred
   */
  static public IObject deserialize(int id, ObjectInputStream istream) throws KasException
  {
    String className = sConfig.getClassName(id);
    Object object;
    Class<?> cls;
    try
    {
      cls = Class.forName(className);
      Constructor<?> ctor = cls.getConstructor(ObjectInputStream.class);
      
      boolean accessible = ctor.isAccessible();
      ctor.setAccessible(true);
      object = ctor.newInstance(istream);
      ctor.setAccessible(accessible);
    }
    catch (ClassNotFoundException e)
    {
      throw new KasException("Failed to find class " + className, e);
    }
    catch (NoSuchMethodException | SecurityException e)
    {
      throw new KasException("Failed to find or access constructor " + className + "(ObjectInputStream)", e);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
    {
      throw new KasException("Failed to instantiate " + className, e);
    }
    
    return (IObject)object;
  }
}
