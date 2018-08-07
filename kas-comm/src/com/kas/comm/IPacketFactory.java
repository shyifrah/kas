package com.kas.comm;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A factory for {@link IPacket packets} creation
 * 
 * @author Pippo
 */
public interface IPacketFactory
{
  /**
   * Constructs a {@link IPacket} object from {@link ObjectInputStream}.<br>
   * <br>
   * Each serialized {@link IPacket} is prefixed with a {@link PacketHeader}, so we read it first and
   * according to the class ID, call the appropriate constructor.
   * 
   * @param istream the {@link ObjectInputStream} from which the packet will be deserialized
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract IPacket createFromStream(ObjectInputStream istream) throws IOException;
}
