package com.kas.infra.base;

import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.TimeStamp;

public class TestTimeStamp
{
  private TimeStamp mNow;
  private TimeStamp mEarlier;
  private TimeStamp mLater;
  
  public TestTimeStamp()
  {
    mNow     = TimeStamp.now();
    mEarlier = TimeStamp.toTimeStamp(1551165242197L);  //1551165242197 = 26 Feb 2019, 9:14:02,197
    mLater   = TimeStamp.toTimeStamp(1551165242297L);  //1551165242297 = 26 Feb 2019, 9:14:02,297
  }
  
  @Test
  public void testGetters()
  {
    Assert.assertEquals( 2019 , mEarlier.getYear()    );
    Assert.assertEquals( 2    , mEarlier.getMonth()   );
    Assert.assertEquals( 26   , mEarlier.getDay()     );
    Assert.assertEquals( 9    , mEarlier.getHours()   );
    Assert.assertEquals( 14   , mEarlier.getMinutes() );
    Assert.assertEquals( 2    , mEarlier.getSeconds() );
    Assert.assertEquals( 197  , mEarlier.getMillis()  );
  }
  
  @Test
  public void testToString()
  {
    Assert.assertEquals("2019-02-26 09:14:02,197", mEarlier.toString());
  }
  
  @Test
  public void testToDiffs()
  {
    Assert.assertTrue(TimeStamp.diff(mNow, mEarlier) > 0);
    Assert.assertEquals(100, TimeStamp.diff(mLater, mEarlier));
  }
}
