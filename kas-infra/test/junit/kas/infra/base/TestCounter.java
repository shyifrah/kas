package junit.kas.infra.base;

import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.Counter;

public class TestCounter
{
  private Counter mCounter1 = new Counter("TestCounter-1");
  private Counter mCounter2 = new Counter("TestCounter-2", 10);
  
  
  @Test
  public void testCounter1()
  {
    for (int i = 0; i < 3; ++i)
      mCounter1.increment();
    
    Assert.assertEquals(mCounter1.getName(), "TestCounter-1");
    Assert.assertEquals(mCounter1.getValue(), 3);
    mCounter1.increment(2);
    Assert.assertEquals(mCounter1.getValue(), 5);
    Assert.assertEquals(mCounter1.toString(), "TestCounter-1=[5]");
  }
  
  @Test
  public void testCounter2()
  {
    for (int i = 0; i < 3; ++i)
      mCounter2.increment();
    
    Assert.assertEquals(mCounter2.getName(), "TestCounter-2");
    Assert.assertEquals(mCounter2.getValue(), 13);
    mCounter2.increment(2);
    Assert.assertEquals(mCounter2.getValue(), 15);
    Assert.assertEquals(mCounter2.toString(), "TestCounter-2=[15]");
  }
}
