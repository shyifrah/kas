package com.kas.comm.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.MessageClass;
import com.kas.comm.impl.MessageType;

public class AuthenticateRequestMessage extends Message
{
  private String mUserName;
  private String mPassword;
  
  /***************************************************************************************************************
   * Constructs a {@code AuthenticateRequestMessage} object, specifying the user name and password
   * 
   * @param userName the caller's user's name
   * @param password the caller's user's password
   */
  public AuthenticateRequestMessage(String userName, String password)
  {
    super();
    mUserName = userName;
    mPassword = password;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code AuthenticateRequestMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   *  
   * @throws ClassNotFoundException
   * @throws IOException
   */
  public AuthenticateRequestMessage(ObjectInputStream istream) throws IOException, ClassNotFoundException
  {
    super(istream);
    mUserName = (String)istream.readObject();
    mPassword = (String)istream.readObject();
  }
  
  /***************************************************************************************************************
   * 
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    super.serialize(ostream);
    ostream.writeObject(mUserName);
    ostream.reset();
    ostream.writeObject(mPassword);
    ostream.reset();
  }

  /***************************************************************************************************************
   * 
   */
  public MessageType getMessageType()
  {
    return MessageType.cAuthenticateRequestMessage;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String getUserName()
  {
    return mUserName;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String getPassword()
  {
    return mPassword;
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
      .append(pad).append("  UserName=").append(mUserName).append("\n")
      .append(pad).append("  Password=").append(mPassword).append("\n")
      .append(pad).append(")\n");
    return sb.toString();
  }
}
