package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.infra.base.ISerializable;
import com.kas.infra.base.AKasObject;

/**
 * A packet header is the beginning of a message transmitted, prefixing the actual packet.
 * 
 * @author Pippo
 */
public class PacketHeader extends AKasObject implements ISerializable
{
  static public final String cEyeCatcher = "KAS";
  static public final int    cTypeUnknown    = -1;
  static public final int    cClassIdUnknown = 0;
  static public final int    cClassIdKasq    = 1;
  
  /**
   * A packet header is marked with an eye-catcher that contains the string "KAS"
   */
  private String mEyeCatcher;
  
  /**
   * The packet's class ID
   */
  private int mClassId;
  
  /**
   * The packet's type
   */
  private int mType;
  
  /**
   * Construct a {@link PacketHeader}, specifying only the class ID
   *  
   * @param id The class ID of the packet
   */
  protected PacketHeader(int id)
  {
    this(id, cTypeUnknown);
  }
  
  /**
   * Construct a {@link PacketHeader}
   *  
   * @param id The class ID of the packet
   * @param type The packet's type. this is an integer which is managed outside of the communication layer
   */
  protected PacketHeader(int id, int type)
  {
    mEyeCatcher = cEyeCatcher;
    mClassId = id;
    mType    = type;
  }
  
  /**
   * Constructs a {@link PacketHeader} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public PacketHeader(ObjectInputStream istream) throws IOException
  {
    try
    {
      mEyeCatcher = (String)istream.readObject();
      mClassId = istream.readInt();
      mType    = istream.readInt();
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
   * Serialize the {@link PacketHeader} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the header will be serialized
   * 
   * @throws IOException if an I/O error occurs
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeObject(mEyeCatcher);
    ostream.reset();
    ostream.writeInt(mClassId);
    ostream.reset();
    ostream.writeInt(mType);
    ostream.reset();
  }
  
  /**
   * Get the eye-catcher
   *  
   * @return the eye-catcher
   */
  public String getEyeCatcher()
  {
    return mEyeCatcher;
  }
  
  /**
   * Get the class ID of the appended packet
   *  
   * @return the class ID
   */
  public int getClassId()
  {
    return mClassId;
  }
  
  /**
   * Return the type of the appended packet.<br>
   * <br>
   * The type of a packet is used by various factories to determine the specific type of the packet.
   *  
   * @return the packet's type
   */
  public int getType()
  {
    return mType;
  }
  
  /**
   * Verify this {@link PacketHeader} is a valid header.<br>
   * <br>
   * Verification is done by comparing the eye-catcher to the value "KAS"
   * 
   * @return {@code true} if this header is valid, {@code false} otherwise
   */
  public boolean verify()
  {
    return mEyeCatcher.equals(cEyeCatcher);
  }
  
  /**
   * Returns a replica of this {@link PacketHeader}.
   * 
   * @return a replica of this {@link PacketHeader}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public PacketHeader replicate()
  {
    return new PacketHeader(mClassId, mType);
  }
  
  /**
   * Returns the {@link PacketHeader} detailed string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(name())
      .append('(')
      .append(mClassId)
      .append(':')
      .append(mType)
      .append(')');
    return sb.toString();
  }  
}
