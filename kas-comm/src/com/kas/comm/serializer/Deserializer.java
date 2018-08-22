package com.kas.comm.serializer;

import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;
import com.kas.infra.base.KasException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * An object that is responsible for deserialization
 * 
 * @author Pippo
 */
public class Deserializer extends AKasObject implements IInitializable
{
  /**
   * Singleton instance
   */
  static private Deserializer sInstance = new Deserializer();
  
  /**
   * Get the singleton instance
   * 
   * @return the singleton instance
   */
  static public Deserializer getInstance()
  {
    return sInstance;
  }
  
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Serialization configuration
   */
  private SerializerConfiguration mConfig;
  
  /**
   * Serialization configuration
   */
  private boolean mIsInitialized;
  
  /**
   * Construct the {@link Deserializer}
   */
  private Deserializer()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = new SerializerConfiguration();
    mIsInitialized = false;
  }
  
  /**
   * Initializing the {@link Deserializer}
   * 
   * @return {@code true} always
   * 
   * @see com.kas.infra.base.IInitializable#init()
   */
  public boolean init()
  {
    if (!mIsInitialized)
    {
      mConfig.init();
      mIsInitialized = true;
    }
    return true;
  }
  
  /**
   * Terminating the {@link Deserializer}
   * 
   * @return {@code true} always
   * 
   * @see com.kas.infra.base.IInitializable#term()
   */
  public boolean term()
  {
    if (mIsInitialized)
    {
      mConfig.term();
      mIsInitialized = false;
    }
    return true;
  }
  
  /**
   * Deserialize an object with class id {@code id} from {@code istream}.
   * 
   * @param id The class ID as defined in configuration
   * @param istream The {@link ObjectInputStream} which holds the object to be deserialized
   * @return a {@link IObject} that was dynamically reconstructed from the stream
   * 
   * @throws KasException if some sort of reflection error occurred
   */
  public IObject deserializeObjectWithId(int id, ObjectInputStream istream) throws KasException
  {
    mLogger.debug("Deserializer::deserialize() - IN");
    
    String className = mConfig.getClassName(id);
    mLogger.debug("Deserializer::deserialize() - Deserializing object with class ID=" + id + ", ClassName=[" + className + "]");
    
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
    
    mLogger.debug("Deserializer::deserialize() - OUT");
    return (IObject)object;
  }
  
  /**
   * Deserialize {@link IObject} with class ID {@code id} from input stream {@code istream}
   * 
   * @param id The object's class ID
   * @param istream The input stream
   * @return the {@link IObject} that was deserialized
   */
  static public IObject deserialize(int id, ObjectInputStream istream) throws KasException
  {
    Deserializer deserializer = Deserializer.getInstance();
    deserializer.init();
    
    return deserializer.deserializeObjectWithId(id, istream);
  }

  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Config=(").append(mConfig.toPrintableString()).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
