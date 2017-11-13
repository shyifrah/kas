package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.infra.base.ISerializable;
import com.kas.infra.base.KasObject;

public class MessageHeader extends KasObject implements ISerializable
{
  /***************************************************************************************************************
   * 
   */
  public static final String cEyeCatcher = "KAS";
  
  /***************************************************************************************************************
   * 
   */
  private MessageType  mType;
  private MessageClass mClass;
  private String       mEyeCatcher;
  
  /***************************************************************************************************************
   * Construct a {@code MessageHeader}
   *  
   * @param type {@code MessageType} of the accompanied message
   */
  public MessageHeader(MessageType type)
  {
    this(type, MessageClass.cUnknownMessage);
  }
  
  /***************************************************************************************************************
   * Construct a {@code MessageHeader}
   *  
   * @param type {@code MessageType} of the accompanied message
   * @param mclass {@code MessageClass} used to identify the MessageFactory to be used for deserializing messages
   *    from the specified type.
   */
  public MessageHeader(MessageType type, MessageClass mclass)
  {
    mType = type;
    mClass = mclass;
    mEyeCatcher = cEyeCatcher;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code MessageHeader} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream}
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public MessageHeader(ObjectInputStream istream) throws ClassNotFoundException, IOException
  {
    mType = (MessageType)istream.readObject();
    mClass = MessageClass.fromInt(istream.readInt());
    mEyeCatcher = (String)istream.readObject();
  }
  
  /***************************************************************************************************************
   * Return the {@code MessageType}
   *  
   * @return {@code MessageType} of the accompanied message
   */
  public MessageType getMessageType()
  {
    return mType;
  }
  
  /***************************************************************************************************************
   * Return the {@code MessageClass}
   *  
   * @return {@code MessageClass} of the accompanied message
   */
  public MessageClass getMessageClass()
  {
    return mClass;
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
   * Verify this {@code MessageHeader} is a valid header.
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
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeObject(mType);
    ostream.reset();
    ostream.writeInt(mClass.ordinal());
    ostream.reset();
    ostream.writeObject(mEyeCatcher);
    ostream.reset();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append('(')
      .append(mType.toString())
      .append(':')
      .append(mClass.toString())
      .append(')');
    return sb.toString();
  }
}
