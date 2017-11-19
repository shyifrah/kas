package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import com.kas.infra.base.ISerializable;
import com.kas.infra.base.AKasObject;

public class PacketHeader extends AKasObject implements ISerializable
{
  /***************************************************************************************************************
   * 
   */
  public static final String cEyeCatcher = "KAS";
  
  public static final int cTypeUnknown    = -1;
  
  public static final int cClassIdUnknown = 0;
  public static final int cClassIdKasq    = 1;
  
  /***************************************************************************************************************
   * 
   */
  private String  mEyeCatcher;
  private int     mClassId;
  private int     mType;
  
  /***************************************************************************************************************
   * Construct a {@code PacketHeader}, specifying only the class ID
   *  
   * @param id the class ID of the packet
   */
  protected PacketHeader(int id)
  {
    this(id, cTypeUnknown);
  }
  
  /***************************************************************************************************************
   * Construct a {@code PacketHeader}
   *  
   * @param id the class ID of the packet
   * @param type the message type. this is an integer which is managed outside of the messenger
   */
  protected PacketHeader(int id, int type)
  {
    mEyeCatcher = cEyeCatcher;
    mClassId = id;
    mType    = type;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code PacketHeader} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream}
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public PacketHeader(ObjectInputStream istream) throws ClassNotFoundException, SocketTimeoutException, IOException
  {
    mEyeCatcher = (String)istream.readObject();
    mClassId = istream.readInt();
    mType    = istream.readInt();
  }
  
  /***************************************************************************************************************
   * 
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeObject(mEyeCatcher);
    ostream.reset();
    ostream.writeInt(mClassId);
    ostream.reset();
    ostream.writeInt(mType);
    ostream.reset();
    ostream.flush();
  }
  
  /***************************************************************************************************************
   * Return the eye-catcher
   *  
   * @return the eye-catcher
   */
  public String getEyeCatcher()
  {
    return mEyeCatcher;
  }
  
  /***************************************************************************************************************
   * Return the class ID of the appended packet
   *  
   * @return the class ID
   */
  public int getClassId()
  {
    return mClassId;
  }
  
  /***************************************************************************************************************
   * Return the type of the appended packet
   * The type of a packet is used by various factories to determine the specific type of the packet.
   *  
   * @return the type
   */
  public int getType()
  {
    return mType;
  }
  
  /***************************************************************************************************************
   * Verify this {@code PacketHeader} is a valid header.
   * Verification is done by comparing the eye-catcher.
   * 
   * @return true if this header is valid, false otherwise
   */
  public boolean verify()
  {
    return mEyeCatcher.equals(cEyeCatcher);
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append('(')
      .append(mClassId)
      .append(':')
      .append(mType)
      .append(')');
    return sb.toString();
  }
}
