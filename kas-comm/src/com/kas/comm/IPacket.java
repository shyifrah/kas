package com.kas.comm;

import java.io.IOException;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.ISerializable;

/**
 * A packet is a datagram sent over the network
 * 
 * @author Pippo
 */
public interface IPacket extends ISerializable
{
  /**
   * Serialize a packet to the specified output stream
   * 
   * @param ostream {@link ObjectOutputStream} to which the object will be serialized
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract void serialize(ObjectOutputStream ostream) throws IOException;

  /**
   * Create a header describing the packet
   * 
   * @return the header describing the packet 
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract PacketHeader createHeader();
}
