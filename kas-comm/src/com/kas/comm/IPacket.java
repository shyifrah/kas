package com.kas.comm;

import java.io.IOException;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.IObject;
import com.kas.infra.base.ISerializable;

/**
 * A packet is a datagram sent over the network
 * 
 * @author Pippo
 */
public interface IPacket extends ISerializable,IObject
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
   * Get the {@link IPacket}'s class ID.
   * This value determines how the packet should be deserialized.
   * 
   * @return the {@link IPacket}'s class ID
   */
  public abstract int getPacketClassId();
  
  /**
   * Create a {@link PacketHeader header} for the specified {@link IPacket}
   * 
   * @return the {@link PacketHeader header} describing the current {@link IPacket}
   */
  public abstract PacketHeader createHeader();
  
  /**
   * Returns the {@link IPacket} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link IPacket}.
   * 
   * @return a replica of this {@link IPacket}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IPacket replicate();
  
  /**
   * Returns the {@link IPacket} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
