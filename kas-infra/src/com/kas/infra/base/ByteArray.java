package com.kas.infra.base;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.kas.infra.utils.StringUtils;

/**
 * A serializable byte array
 * 
 * @author Pippo
 */
public class ByteArray extends AKasObject implements ISerializable, Serializable
{
  private static final long serialVersionUID = 1L;
  
  /**
   * The actual byte array
   */
  private byte [] mByteArray;
  
  /**
   * Construct the {@link ByteArray} object, passing it the byte []
   * 
   * @param bytearray The wrapped byte array
   */
  public ByteArray(byte [] bytearray)
  {
    if (bytearray == null)
      throw new NullPointerException("Byte array cannot be null value");
    
    mByteArray = bytearray;
  }
  
  /**
   * Construct the {@link ByteArray} object from the {@link ObjectInputStream}
   * 
   * @param istream The input stream
   */
  public ByteArray(ObjectInputStream istream) throws IOException
  {
    try
    {
      int size = istream.readInt();
      mByteArray = new byte [size];
      istream.read(mByteArray);
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Throwable e)
    {
      throw new IOException(e);
    }
  }
  
  /**
   * Serialize the byte array into the output stream
   * 
   * @param ostream The {@link ObjectOutputStream}
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeInt(mByteArray.length);
    ostream.reset();
    ostream.write(mByteArray);
    ostream.reset();
  }
  
  /**
   * Get the wrapped byte array
   * 
   * @return the byte array
   */
  public byte [] getByteArray()
  {
    return mByteArray;
  }

  /**
   * Get the object's detailed string representation.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   * @see #toString()
   */
  public String toPrintableString(int level)
  {
    return StringUtils.asHexString(mByteArray);
  }
}
