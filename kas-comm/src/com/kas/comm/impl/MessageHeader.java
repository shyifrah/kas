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
  private MessageType    mType;
  private MessageSubType mSubType;
  private String         mEyeCatcher;
  
  /***************************************************************************************************************
   * Construct a {@code MessageHeader}
   *  
   * @param type {@code MessageType} of the accompanied message
   */
  public MessageHeader(MessageType type)
  {
    this(type, MessageSubType.cUnknownMessage);
  }
  
  /***************************************************************************************************************
   * Construct a {@code MessageHeader}
   *  
   * @param type {@code tMessageSubType} of the accompanied message
   * @param mclass {@code tMessageSubType} used to identify the MessageFactory to be used for deserializing messages
   *    from the specified type.
   */
  public MessageHeader(MessageType type, MessageSubType subtype)
  {
    mType = type;
    mSubType = subtype;
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
    mSubType = MessageSubType.fromInt(istream.readInt());
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
   * Return the {@code tMessageSubType}
   *  
   * @return {@code tMessageSubType} of the accompanied message
   */
  public MessageSubType getMessageClass()
  {
    return mSubType;
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
    ostream.writeInt(mSubType.ordinal());
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
      .append(mSubType.toString())
      .append(')');
    return sb.toString();
  }
}
