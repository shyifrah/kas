package com.kas.q.ext;

import java.io.IOException;
import java.io.ObjectOutputStream;
import com.kas.infra.base.ISerializable;
import com.kas.infra.base.KasObject;

public class MessageHeader extends KasObject implements ISerializable
{
  private MessageType mType;
  
  /***************************************************************************************************************
   * Construct a {@code MessageHeader}
   *  
   * @param type {@code MessageType} of the accompanied message
   */
  public MessageHeader(MessageType type)
  {
    mType = type;
  }
  
  /***************************************************************************************************************
   * Return the {@code MessageType}
   *  
   * @return {@code MessageType} of the accompanied message
   */
  public MessageType getType()
  {
    return mType;
  }
  
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeObject(mType);
    ostream.reset();
  }
  
  public String toPrintableString(int level)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append('[')
      .append(mType.toString())
      .append(']');
    return sb.toString();
  }
}
