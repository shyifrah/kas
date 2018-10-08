package junit.kas.infra.base;

import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.UniqueId;

public class TestUniqueId
{
  private UniqueId mAllZeroes = UniqueId.cNullUniqueId;
  private UUID mRandomUUID    = UUID.randomUUID();
  
  @Test
  public void testCopyCtor()
  {
    UniqueId copyOfAllZeroes = new UniqueId(mAllZeroes);
    
    Assert.assertTrue( mAllZeroes.equals(copyOfAllZeroes) );
    Assert.assertEquals( "00000000-0000-0000-0000-000000000000" , copyOfAllZeroes.toString() );
    Assert.assertEquals( mAllZeroes.hashCode() , copyOfAllZeroes.hashCode() );
    Assert.assertEquals( mAllZeroes.compareTo(copyOfAllZeroes) , 0 );
  }
  
  @Test
  public void testCtorFromUuid()
  {
    UniqueId uuid = new UniqueId(mRandomUUID);
    
    Assert.assertEquals(uuid.getUuid(), mRandomUUID);
    Assert.assertEquals(uuid.toString(), mRandomUUID.toString());
  }
  
  @Test
  public void testFromString()
  {
    UniqueId uuid = UniqueId.fromString(mRandomUUID.toString());
    
    Assert.assertEquals(uuid.getUuid(), mRandomUUID);
    Assert.assertEquals(uuid.toString(), mRandomUUID.toString());
  }
  
  @Test
  public void testFromByteArrayAndFromString()
  {
    byte [] byteArrayUuid = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };
    String strUuid    = "01020304-0506-0708-0900-0a0b0c0d0e0f";
    
    UniqueId uuid1 = UniqueId.fromByteArray(byteArrayUuid);
    UniqueId uuid2 = UniqueId.fromString(strUuid);
    
    Assert.assertEquals(uuid1 , uuid2);
    Assert.assertEquals(uuid1.hashCode() , uuid2.hashCode());
  }
}
