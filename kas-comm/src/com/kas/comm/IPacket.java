package com.kas.comm;

import java.io.IOException;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.ISerializable;

public interface IPacket extends ISerializable
{
  /***************************************************************************************************************
   * 
   */
  public abstract void serialize(ObjectOutputStream ostream) throws IOException;
  
  /***************************************************************************************************************
   * Get the {@code Packet}'s class ID.
   * This value determines how the packet should be deserialized.
   * 
   * @return the {@code Packet}'s class ID
   */
  public abstract int getPacketClassId();
  
  /***************************************************************************************************************
   * Create a {@code PacketHeader} for the specified {@code IPacket}
   * 
   * @return the {@code PacketHeader} describing the current IPacket
   */
  public abstract PacketHeader createHeader();
}
