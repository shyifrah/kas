package com.kas.mq.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.infra.base.IObject;

/**
 * A KAS/MQ base message, with a byte array payload.<br>
 * <br>
 * Some of the messages that are exposed to the user make use of a byte array
 * as their payload. To simplify things and code reuse purposes I created this class.
 * 
 * @author Pippo
 */
public abstract class ABaseBytesMessage extends ABaseMessage
{
  /**
   * The payload (byte array) 
   */
  protected byte [] mBody;
  
  /**
   * Construct a default string message object
   */
  protected ABaseBytesMessage()
  {
    super();
    mBody = null;
  }
  
  /**
   * Constructs a {@link ABaseBytesMessage} object from {@link ObjectInputStream}
   * 
   * @param istream
   *   The {@link ObjectInputStream}
   * @throws IOException
   *   if I/O error occurs
   */
  public ABaseBytesMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    try
    {
      int len = istream.readInt();
      if (len > 0)
      {
        mBody = new byte [len];
        istream.read(mBody);
      }
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
   * Serialize the {@link ABaseBytesMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream
   *   The {@link ObjectOutputStream} to which the message will be serialized
   * @throws IOException
   *   if an I/O error occurs
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    super.serialize(ostream);
    
    int len = (mBody == null ? 0 : mBody.length);
    ostream.writeInt(len);
    ostream.reset();
    
    if (len > 0)
    {
      ostream.write(mBody);
      ostream.reset();
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
    return super.toPrintableString(level);
  }
}
