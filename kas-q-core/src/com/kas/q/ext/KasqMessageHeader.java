package com.kas.q.ext;

import java.io.IOException;
import java.io.ObjectInputStream;
import com.kas.comm.impl.PacketHeader;

public class KasqMessageHeader extends PacketHeader
{
  public static final int cTypeUnknown    = -1;
  
  public static final int cClassIdUnknown = 0;
  public static final int cClassIdKasq    = 1;
  
  /***************************************************************************************************************
   * Construct a {@code KasqMessageHeader}, specifying only the class ID
   *  
   * @param id the class ID of the packet
   */
  public KasqMessageHeader(int type)
  {
    super(cClassIdKasq, type);
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
    super(istream);
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append('(')
      .append(getClassId())
      .append(':')
      .append(getType())
      .append(')');
    return sb.toString();
  }
}
