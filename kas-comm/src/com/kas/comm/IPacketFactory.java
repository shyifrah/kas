package com.kas.comm;

import java.io.IOException;
import java.io.ObjectInputStream;

public interface IPacketFactory
{
  /***************************************************************************************************************
   * Constructs a {@code IPacket} object from {@code ObjectInputStream}. 
   * Each serialized {@code IPacket} is prefixed with a {@link PacketHeader}, so we read it first and
   * according to the class ID, call the appropriate constructor.
   * 
   * @param istream the {@code ObjectInputStream} from which the packet will be deserialized
   * 
   * @throws IOException
   */
  public abstract IPacket createFromStream(ObjectInputStream istream) throws IOException;
}
