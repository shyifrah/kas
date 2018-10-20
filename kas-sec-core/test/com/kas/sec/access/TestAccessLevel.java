package com.kas.sec.access;

import org.junit.Assert;
import org.junit.Test;

public class TestAccessLevel
{
  private AccessLevel mReadOnly = new AccessLevel(AccessLevel.READ);
  private AccessLevel mWriteOnly = new AccessLevel(AccessLevel.WRITE);;
  private AccessLevel mReadWrite = new AccessLevel((byte)(AccessLevel.READ | AccessLevel.WRITE));
  private AccessLevel mReadAlter = new AccessLevel((byte)(AccessLevel.READ | AccessLevel.ALTER));;
  
  
  @Test
  public void testReadOnly()
  {
    Assert.assertTrue  ( mReadOnly.isLevelEnabled(AccessLevel.READ)  );
    Assert.assertFalse ( mReadOnly.isLevelEnabled(AccessLevel.WRITE) );
    Assert.assertFalse ( mReadOnly.isLevelEnabled(AccessLevel.ALTER) );
    
    Assert.assertTrue  ( "01".equals(mReadOnly.toString()) );
    
    Assert.assertEquals( "READ" , mReadOnly.toPrintableString() );
  }
  
  @Test
  public void testWriteOnly()
  {
    Assert.assertFalse ( mWriteOnly.isLevelEnabled(AccessLevel.READ)  );
    Assert.assertTrue  ( mWriteOnly.isLevelEnabled(AccessLevel.WRITE) );
    Assert.assertFalse ( mWriteOnly.isLevelEnabled(AccessLevel.ALTER) );
    
    Assert.assertTrue  ( "02".equals(mWriteOnly.toString()) );
    
    Assert.assertEquals( "WRITE" , mWriteOnly.toPrintableString() );
  }
  
  @Test
  public void testReadWrite()
  {
    Assert.assertTrue  ( mReadWrite.isLevelEnabled(AccessLevel.READ)  );
    Assert.assertTrue  ( mReadWrite.isLevelEnabled(AccessLevel.WRITE) );
    Assert.assertFalse ( mReadWrite.isLevelEnabled(AccessLevel.ALTER) );
    
    Assert.assertTrue  ( "03".equals(mReadWrite.toString()) );
    
    Assert.assertEquals( "READ,WRITE" , mReadWrite.toPrintableString() );
  }
  
  @Test
  public void testReadAlter()
  {
    Assert.assertTrue  ( mReadAlter.isLevelEnabled(AccessLevel.READ)  );
    Assert.assertFalse ( mReadAlter.isLevelEnabled(AccessLevel.WRITE) );
    Assert.assertTrue  ( mReadAlter.isLevelEnabled(AccessLevel.ALTER) );
    
    Assert.assertTrue  ( "05".equals(mReadAlter.toString()) );
    
    Assert.assertEquals( "READ,ALTER", mReadAlter.toPrintableString() );
  }
}
