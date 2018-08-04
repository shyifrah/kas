package com.kas.infra.base;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UniqueId extends AKasObject implements Serializable
{
  private static final long    serialVersionUID = 1L;
  private static final byte [] cZeroArray = ByteBuffer.wrap(new byte [16]).putLong(0L).putLong(0L).array();
  
  public  static final UniqueId cNullUniqueId         = fromByteArray(cZeroArray);
  public  static final String   cNullUniqueIdAsString = cNullUniqueId.toString();
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private UUID mUuid;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private UniqueId()
  {
    mUuid = UUID.randomUUID();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public UniqueId(UUID uuid)
  {
    mUuid = uuid;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public UniqueId(UniqueId other)
  {
    mUuid = other.mUuid;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public byte [] toByteArray()
  {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    
    bb.putLong(mUuid.getMostSignificantBits());
    bb.putLong(mUuid.getLeastSignificantBits());
    
    return bb.array();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public UUID getUuid()
  {
    return mUuid;
  }
  
  /**
   * 
   */
  public boolean equals(UniqueId other)
  {
    return mUuid.equals(((UniqueId)other).mUuid);
  }
  
  /**
   * 
   */
  public int hashCode()
  {
    return mUuid.hashCode();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static UniqueId generate()
  {
    return new UniqueId();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static UniqueId fromByteArray(byte [] array)
  {
    ByteBuffer bb = ByteBuffer.wrap(array);
    long high = bb.getLong();
    long low  = bb.getLong();
    UUID uuid = new UUID(high, low);
    return new UniqueId(uuid);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static UniqueId fromString(String str)
  {
    UUID uuid = UUID.fromString(str);
    return new UniqueId(uuid);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toString()
  {
    return mUuid.toString();
  }
  
  /**
   * Returns a replica of this {@link #UniqueId}.
   * 
   * @return a replica of this {@link #UniqueId}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public UniqueId replicate()
  {
    return new UniqueId(this);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    return toString();
  }
}
