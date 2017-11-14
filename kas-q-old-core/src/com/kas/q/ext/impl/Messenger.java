package com.kas.q.ext.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import com.kas.infra.base.ISerializable;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class Messenger extends AKasObject
{
  public static class Factory
  {
    public static Messenger create(String host, int port) throws UnknownHostException, IOException
    {
      return new Messenger(new Socket(host, port), host, port);
    }
    
    public static Messenger create(String host, int port, String userName, String password) throws UnknownHostException, IOException
    {
      return new Messenger(new Socket(host, port), host, port);
    }
    
    public static Messenger create(Socket socket) throws UnknownHostException, IOException
    {
      return new Messenger(socket, socket.getInetAddress().getHostName(), socket.getPort());
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected ILogger  mLogger;
  protected Socket   mSocket;
  protected ObjectOutputStream mOutputStream;
  protected ObjectInputStream  mInputStream;
  protected boolean  mConnected;
  protected String   mHost;
  protected int      mPort;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private Messenger(Socket socket, String host, int port) throws IOException
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mHost = host;
    mPort = port;
    mSocket = socket;
    mConnected = true;
    mOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
    mInputStream = new ObjectInputStream(mSocket.getInputStream());
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean sendObject(Object object)
  {
    boolean result = false;
    mLogger.debug("Messenger::send() - IN, Message=" + object.toString());
    
    try
    {
      mOutputStream.writeObject(object);
      mOutputStream.flush();
      result = true;
    }
    catch (Throwable e)
    {
      mLogger.error("Messenger failed to send message=[" + object.toString() + "]. Exception caught: ", e);
    }
    
    mLogger.debug("Messenger::send() - OUT, Result=" + result);
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean sendSerializable(ISerializable object)
  {
    boolean result = false;
    mLogger.debug("Messenger::send() - IN, Message=" + object.toString());
    
    try
    {
      object.serialize(mOutputStream);
      mOutputStream.flush();
      result = true;
    }
    catch (Throwable e)
    {
      mLogger.error("Messenger failed to send message=[" + object.toString() + "]. Exception caught: ", e);
    }
    
    mLogger.debug("Messenger::send() - OUT, Result=" + result);
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Object recv()
  {
    Object result = null;
    mLogger.debug("Messenger::recv() - IN");
    
    try
    {
      result = mInputStream.readObject();
    }
    catch (EOFException e)
    {
      mLogger.info("Client disconnected...");
      mConnected = false;
    }
    catch (Throwable e) {}
    
    mLogger.debug("Messenger::recv() - OUT, Result=" + result.toString());
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void cleanup()
  {
    mLogger.debug("Messenger::cleanup() - IN");
    
    try
    {
      mOutputStream.flush();
      mOutputStream.close();
      mInputStream.close();
      mSocket.close();
      mConnected = false;
    }
    catch (IOException e) {}
    
    mLogger.debug("Messenger::cleanup() - OUT");
  }
  
  public ObjectOutputStream getOutputStream()
  {
    return mOutputStream;
  }
  
  public ObjectInputStream getInputStream()
  {
    return mInputStream;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean isConnected()
  {
    return mConnected;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Host=").append(mHost).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append("  Connected=").append(mConnected).append("\n")
      .append(pad).append(")\n");
    
    return sb.toString();
  }
}
