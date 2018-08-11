package com.kas.infra.base;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * A class wrapping the {@link UUID}
 * 
 * @author Pippo
 */
public class UniqueId extends AKasObject implements Serializable
{
  static private final long    serialVersionUID = 1L;
  static private final byte [] cZeroArray = ByteBuffer.wrap(new byte [16]).putLong(0L).putLong(0L).array();
  
  static public  final UniqueId cNullUniqueId         = fromByteArray(cZeroArray);
  static public  final String   cNullUniqueIdAsString = cNullUniqueId.toString();
  
  /**
   * Generate a random {@link UniqueId}
   * 
   * @return the generated {@link UniqueId}
   */
  static public UniqueId generate()
  {
    return new UniqueId();
  }
  
  /**
   * Create a {@link UniqueId} from a byte array
   * 
   * @param array The UUID represented as array of bytes
   * @return the generated {@link UniqueId}
   */
  static public UniqueId fromByteArray(byte [] array)
  {
    ByteBuffer bb = ByteBuffer.wrap(array);
    long high = bb.getLong();
    long low  = bb.getLong();
    UUID uuid = new UUID(high, low);
    return new UniqueId(uuid);
  }
  
  /**
   * Create a {@link UniqueId} from a string
   * 
   * @param str The UUID as a string
   * @return the generated {@link UniqueId}
   * 
   * @see java.util.UUID#fromString(String)
   */
  static public UniqueId fromString(String str)
  {
    UUID uuid = UUID.fromString(str);
    return new UniqueId(uuid);
  }

  /**
   * The {@link UUID}
   */
  private UUID mUuid;
  
  /**
   * Create a random {@link UniqueId}
   * 
   * @see java.util.UUID#randomUUID()
   */
  private UniqueId()
  {
    mUuid = UUID.randomUUID();
  }
  
  /**
   * Create a {@link UniqueId} based on a different {@link UUID}
   * 
   * @param uuid a {@link UUID}
   */
  public UniqueId(UUID uuid)
  {
    mUuid = uuid;
  }
  
  /**
   * Create a {@link UniqueId} based on a different {@link UniqueId}
   * 
   * @param other a {@link UniqueId}
   */
  public UniqueId(UniqueId other)
  {
    mUuid = other.mUuid;
  }
  
  /**
   * Get the {@link UUID} as array of bytes
   * 
   * @return a 16-bytes array
   */
  public byte [] toByteArray()
  {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    
    bb.putLong(mUuid.getMostSignificantBits());
    bb.putLong(mUuid.getLeastSignificantBits());
    
    return bb.array();
  }
  
  /**
   * Get the {@link UUID}
   * 
   * @return the {@link UUID}
   */
  public UUID getUuid()
  {
    return mUuid;
  }
  
  /**
   * Compares two {@link UniqueId}
   * 
   * @return the value of {@link java.util.UUID#equals(Object)}
   * 
   * @see java.util.UUID#equals(Object)
   */
  public boolean equals(UniqueId other)
  {
    return mUuid.equals(((UniqueId)other).mUuid);
  }
  
  /**
   * Returns a Hash Code value of the object
   * 
   * @return the value of {@link java.util.UUID#hashCode()}
   * 
   * @see java.util.UUID#hashCode()
   */
  public int hashCode()
  {
    return mUuid.hashCode();
  }
  
  /**
   * Return the UUID as a string
   * 
   * @return the UUID string
   * 
   * @see java.util.UUID#toString()
   */
  public String toString()
  {
    return mUuid.toString();
  }
  
  /**
   * Get the object's detailed string representation.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   * @see #toString()
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
