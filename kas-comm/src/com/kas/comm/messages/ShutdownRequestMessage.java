package com.kas.comm.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.MessageType;

public class ShutdownRequestMessage extends Message
{
  private String mMode;
  
  /***************************************************************************************************************
   * Constructs a {@code ShutdownRequestMessage} object, specifying the shutdown mode
   * 
   * @param mode the shutdown mode
   */
  public ShutdownRequestMessage(String mode)
  {
    super();
    mMode = mode;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code ShutdownRequestMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   *  
   * @throws ClassNotFoundException
   * @throws IOException
   */
  public ShutdownRequestMessage(ObjectInputStream istream) throws IOException, ClassNotFoundException
  {
    super(istream);
    mMode = (String)istream.readObject();
  }
  
  /***************************************************************************************************************
   * 
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    super.serialize(ostream);
    ostream.writeObject(mMode);
    ostream.reset();
  }

  /***************************************************************************************************************
   * 
   */
  public MessageType getMessageType()
  {
    return MessageType.cShutdownMessage;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String getMode()
  {
    return mMode;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(super.toPrintableString(level))
      .append(pad).append("  Mode=").append(mMode).append("\n")
      .append(pad).append(")\n");
    return sb.toString();
  }
}
