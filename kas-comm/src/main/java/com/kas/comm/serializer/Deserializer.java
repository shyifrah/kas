package com.kas.comm.serializer;

import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.comm.IPacket;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.KasException;

/**
 * An object that is responsible for deserialization
 * 
 * @author Pippo
 */
public class Deserializer extends AKasObject
{
  /**
   * Singleton instance
   */
  static private Deserializer sInstance = new Deserializer();
  
  /**
   * Get the singleton instance
   * 
   * @return
   *   the singleton instance
   */
  static public Deserializer getInstance()
  {
    return sInstance;
  }
  
  /**
   * Logger
   */
  private Logger mLogger;
  
  /**
   * Map of IDs and classes
   */
  private ClassIdMap mClassesMap;
  
  /**
   * Construct the {@link Deserializer}
   */
  private Deserializer()
  {
    mLogger = LogManager.getLogger(getClass());
    mClassesMap = new ClassIdMap();
  }
  
  /**
   * Deserialize an object with class id {@code id} from {@code istream}.
   * 
   * @param id
   *   The class ID as defined in configuration
   * @param istream
   *   The {@link ObjectInputStream} which holds the object to be deserialized
   * @return
   *   a {@link IObject} that was dynamically reconstructed from the stream
   * @throws KasException
   *   if some sort of reflection error occurred
   */
  private IObject deserializeObjectWithId(EClassId id, ObjectInputStream istream) throws KasException
  {
    mLogger.trace("Deserializer::deserialize() - IN, ID={}", id);
    
    Object object = null;
    
    Class<? extends IPacket> cls = mClassesMap.get(id);
    if (cls == null)
    {
      mLogger.trace("Deserializer::deserialize() - Unknown class ID={}", id);
    }
    else
    {
      String className = cls.getName();
      mLogger.trace("Deserializer::deserialize() - Deserializing object with class ID={}, ClassName=[{}]", id, className);
      
      try
      {
        Constructor<?> ctor = cls.getConstructor(ObjectInputStream.class);
        object = ctor.newInstance(istream);
      }
      catch (NoSuchMethodException | SecurityException e)
      {
        throw new KasException("Failed to find or access constructor " + className + "(ObjectInputStream)", e);
      }
      catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
      {
        throw new KasException("Failed to instantiate " + className, e);
      }
      catch (Throwable e)
      {
        throw new KasException("Exception caught during deserialization of class with ID: (" + id + ")", e);
      }
    }
    
    mLogger.trace("Deserializer::deserialize() - OUT");
    return (IObject)object;
  }
  
  /**
   * Register an {@link IPacket} class with associated ID
   * 
   * @param newClass
   *   The class
   * @param newId
   *   The associated ID
   * @return
   *   the old class that was associated with the specified ID
   */
  public void register(Class<? extends IPacket> newClass, EClassId newId)
  {
    mLogger.trace("Deserializer::register() - IN, ID={}, Class=[{}]", newId, newClass.getName());
    
    if (!IPacket.class.isAssignableFrom(newClass))
    {
      mLogger.warn("Class [{}] is not valid for registration with Serializing mechanism as it is not a valid IPacket", newClass.getName());
    }
    else
    {
      mClassesMap.put(newId, newClass);
    }
    
    mLogger.trace("Deserializer::register() - OUT");
  }
  
  /**
   * Deserialize {@link IObject} with class ID {@code id} from input stream {@code istream}
   * 
   * @param id
   *   The object's class ID
   * @param istream
   *   The input stream
   * @return
   *   the {@link IObject} that was deserialized
   */
  static public IObject deserialize(int id, ObjectInputStream istream) throws KasException
  {
    Deserializer deserializer = Deserializer.getInstance();
    
    EClassId clsid = EClassId.fromInt(id);
    if (clsid == EClassId.cUnknown)
      return null;
    
    return deserializer.deserializeObjectWithId(clsid, istream);
  }

  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  ClassesMap=(").append(mClassesMap.toPrintableString(level+2)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
