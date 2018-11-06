package com.kas.comm;

import java.io.IOException;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.IObject;
import com.kas.infra.base.ISerializable;

/**
 * A packet is a datagram sent over the network
 * 
 * @author Pippo
 */
public interface IPacket extends ISerializable, IObject
{
  /**
   * Create a header describing the packet
   * 
   * @return the header describing the packet 
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract PacketHeader createHeader();
}
