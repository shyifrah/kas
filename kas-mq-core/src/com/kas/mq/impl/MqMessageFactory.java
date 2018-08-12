package com.kas.mq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import com.kas.comm.IPacket;
import com.kas.comm.IPacketFactory;
import com.kas.comm.impl.PacketHeader;

public class MqMessageFactory implements IPacketFactory
{
  public IPacket createFromStream(ObjectInputStream istream) throws IOException
  {
    PacketHeader header = new PacketHeader(istream);
    
    // need to verify header's validity by calling header.verify()
    
    // need to determine which object's is being built. according to the packet's header
    return new MqMessage(istream);
  }
}
