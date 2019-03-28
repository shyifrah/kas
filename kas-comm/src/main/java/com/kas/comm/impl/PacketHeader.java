package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.ISerializable;
import com.kas.infra.base.KasException;
import com.kas.comm.IPacket;
import com.kas.comm.serializer.Deserializer;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;

/**
 * A packet header is the beginning of a message transmitted, prefixing the actual packet.
 * 
 * @author Pippo
 */
public class PacketHeader extends AKasObject implements ISerializable
{
  static public final String cEyeCatcher = "KAS";
  
  static private Logger sLogger = LogManager.getLogger(PacketHeader.class);
  
  /**
   * A packet header is marked with an eye-catcher that contains the string "KAS"
   */
  private String mEyeCatcher;
  
  /**
   * The packet's class ID
   */
  private EClassId mClassId;
  
  /**
   * An indicator that shows this packet header was verified
   */
  private boolean mVerified;
  
  /**
   * Construct a {@link PacketHeader}, specifying only the class ID
   *  
   * @param id
   *   The class ID of the packet
   */
  public PacketHeader(EClassId id)
  {
    mEyeCatcher = cEyeCatcher;
    mClassId = id;
  }
  
  /**
   * Constructs a {@link PacketHeader} object from {@link ObjectInputStream}
   * 
   * @param istream
   *   The {@link ObjectInputStream}
   * @throws IOException
   *   if I/O error occurs
   */
  public PacketHeader(ObjectInputStream istream) throws IOException
  {
    try
    {
      mEyeCatcher = (String)istream.readObject();
      int id = istream.readInt();
      mClassId = EClassId.fromInt(id);
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
   * @param ostream
   *   The {@link ObjectOutputStream} to which the header will be serialized
   * @throws IOException
   *   if an I/O error occurs
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    sLogger.trace("PacketHeader::serialize() - IN");
    
    ostream.writeObject(mEyeCatcher);
    ostream.reset();
    ostream.writeInt(mClassId.ordinal());
    ostream.reset();
    
    sLogger.trace("PacketHeader::serialize() - OUT");
  }
  
  /**
   * Read a packet from {@code istream}.<br>
   * The {@link PacketHeader packet header} contains a class ID. This class ID is defined
   * inside the {@link SerializerConfiguration} with an assigned class name. We dynamically
   * (via reflection) call this class' constructor, passing it the input stream.
   * 
   * @param istream
   *   The {@link ObjectInputStream} from which the packet will be deserialized
   * @return
   *   the created {@link IPacket}
   * @throws KasException
   *   if any reflection error occurs. The original exception is set as causer. 
   */
  public IPacket read(ObjectInputStream istream) throws KasException
  {
    sLogger.trace("PacketHeader::read() - IN");
    
    if (!mVerified) verify();
    
    sLogger.trace("PacketHeader::read() - De-serializing object from input stream...");
    IObject iObject = Deserializer.deserialize(mClassId.ordinal(), istream);
    
    IPacket iPacket = null;
    try
    {
      iPacket = (IPacket)iObject;
    }
    catch (ClassCastException e)
    {
      throw new KasException("Created object is not a valid IPacket");
    }
    
    sLogger.trace("PacketHeader::read() - OUT");
    return iPacket;
  }
  
  /**
   * Get the eye-catcher
   *  
   * @return
   *   the eye-catcher
   */
  public String getEyeCatcher()
  {
    return mEyeCatcher;
  }
  
  /**
   * Get the class ID of the appended packet
   *  
   * @return
   *   the class ID
   */
  public EClassId getClassId()
  {
    return mClassId;
  }
  
  /**
   * Verify this {@link PacketHeader} is a valid header.<br>
   * Verification is done by comparing the eye-catcher to the value "KAS"
   * 
   * @throws KasException
   *   if this header is invalid
   */
  public void verify() throws KasException
  {
    if (!mEyeCatcher.equals(cEyeCatcher))
    {
      throw new KasException("Packet header failed verification. EyeCatcher=[" + mEyeCatcher + "]");
    }
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
    StringBuilder sb = new StringBuilder();
    sb.append(name())
      .append("ClassId=(")
      .append(mClassId)
      .append(')');
    return sb.toString();
  }  
}
