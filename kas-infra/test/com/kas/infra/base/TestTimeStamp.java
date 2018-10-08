package com.kas.infra.base;

import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.TimeStamp;

public class TestTimeStamp
{
  @Test
  public void testNowAsString()
  {
    long now = TimeStamp.nowAsLong();
    TimeStamp ts = new TimeStamp(now);
    Assert.assertEquals(now, ts.getAsLong());
  }
  
  @Test
  public void testDiff()
  {
    long now = TimeStamp.nowAsLong();
    TimeStamp ts = new TimeStamp(now);
    Assert.assertEquals(ts.diff(now), 0);
  }
  
  @Test
  public void testCtor() throws InterruptedException
  {
    long now = TimeStamp.nowAsLong();
    Thread.sleep(300);
    TimeStamp ts = TimeStamp.now();
    Assert.assertTrue(ts.getAsLong() > now);
  }
  
  @Test
  public void testName()
  {
    TimeStamp ts = TimeStamp.now();
    Assert.assertTrue(ts.name().equals("<TimeStamp>"));
  }
}
