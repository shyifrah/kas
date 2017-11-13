package com.kas.comm.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.MessageClass;
import com.kas.comm.impl.MessageType;
import com.kas.infra.base.UniqueId;

public class ResponseMessage extends Message
{
  /***************************************************************************************************************
   * 
   */
  private static final int cOkayCode = 0;
  private static final int cFailCode = -1;
  
  /***************************************************************************************************************
   * 
   */
  private int    mCode;
  private String mReason;
  
  /***************************************************************************************************************
   * Generate a success response
   * 
   * @param request the request {@code Message}
   */
  public static ResponseMessage generateSuccessResponse(Message request)
  {
    return generateResponse(request, cOkayCode, null);
  }
  
  /***************************************************************************************************************
   * Generate a failure response
   * 
   * @param request the request {@code Message}
   * @param error an error message in case of failure
   */
  public static ResponseMessage generateFailureResponse(Message request, String error)
  {
    return generateResponse(request, cFailCode, error);
  }
  
  /***************************************************************************************************************
   * Generate a response for a specific message
   * 
   * @param request the request {@code Message}
   * @param code the return code which indicates success or failure
   * @param error an error message in case of failure
   */
  private static ResponseMessage generateResponse(Message request, int code, String error)
  {
    return new ResponseMessage(request.mMessageId, code, error);
  }
  
  /***************************************************************************************************************
   * Constructs a default {@code ResponseMessage} object, specifying the response code and the reason message
   * 
   * @param requestId the request message's UniqueId
   * @param code a code which interprets to success or failure
   * @param reason a string message which explains the reason for failure
   */
  public ResponseMessage(UniqueId requestId, int code, String reason)
  {
    super(requestId);
    mCode   = code;
    mReason = reason;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code ResponseMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws ClassNotFoundException
   * @throws IOException
   */
  public ResponseMessage(ObjectInputStream istream) throws IOException, ClassNotFoundException
  {
    super(istream);
    mCode   = istream.readInt();
    mReason = (String)istream.readObject();
  }
  
  /***************************************************************************************************************
   * 
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    super.serialize(ostream);
    ostream.writeInt(mCode);
    ostream.reset();
    ostream.writeObject(mReason);
    ostream.reset();
  }

  /***************************************************************************************************************
   * 
   */
  public MessageType getMessageType()
  {
    return MessageType.cResponseMessage;
  }
  
  /***************************************************************************************************************
   * Analyze the response code
   * 
   * @return true if the request was successful, false otherwise
   */
  public boolean succeeded()
  {
    return mCode == cOkayCode;
  }
  
  /***************************************************************************************************************
   * Get the response code
   * 
   * @return the response code
   */
  public int getCode()
  {
    return mCode;
  }
  
  /***************************************************************************************************************
   * Get the reason string
   * 
   * @return the reason string
   */
  public String getReason()
  {
    return mReason;
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
      .append(pad).append("  Code=").append(mCode).append("\n")
      .append(pad).append("  Reason=[").append(mReason).append("]\n")
      .append(pad).append(")\n");
    return sb.toString();
  }
}
