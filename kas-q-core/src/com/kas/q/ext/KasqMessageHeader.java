package com.kas.q.ext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.infra.base.ISerializable;
import com.kas.infra.base.KasObject;

public class KasqMessageHeader extends KasObject implements ISerializable
{
  /***************************************************************************************************************
   * 
   */
  public static final String cEyeCatcher = "KASQ";
  
  /***************************************************************************************************************
   * 
   */
  private MessageClass mClass;
  private String       mEyeCatcher;
  
  /***************************************************************************************************************
   * Construct a {@code KasqMessageHeader}
   *  
   * @param cls {@code MessageClass} of the accompanied message
   */
  public KasqMessageHeader(MessageClass cls)
  {
    mClass = cls;
    mEyeCatcher = cEyeCatcher;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessageHeader} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream}
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public KasqMessageHeader(ObjectInputStream istream) throws ClassNotFoundException, IOException
  {
    mClass = (MessageClass)istream.readObject();
    mEyeCatcher = (String)istream.readObject();
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
   * Verify this {@code KasqMessageHeader} is a valid header.
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
    ostream.writeObject(mClass);
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
      .append(mClass.toString())
      .append(')');
    return sb.toString();
  }
}
