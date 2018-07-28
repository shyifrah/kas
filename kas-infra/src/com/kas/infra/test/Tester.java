package com.kas.infra.test;

import com.kas.infra.base.IBaseLogger;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.StringUtils;

public class Tester
{
  private IBaseLogger mLogger;
  private String      mTitle;
  private TimeStamp   mStartTimestamp;
  private TimeStamp   mEndTimestamp;
  private Object      mTestedObject;
  
  public Tester(IBaseLogger logger, String title, Object object)
  {
    mLogger = logger;
    mTitle  = title;
    mTestedObject = object;
  }
  
  public void execute(String method, Object [] arguments)
  {
    
  }
  
  public void start()
  {
    mStartTimestamp = new TimeStamp();
    String msg = StringUtils.title("Test set " + mTitle);
    mLogger.trace(msg);
  }
  
  public void end()
  {
    mEndTimestamp = new TimeStamp();
  }
  
  public void equals(short num1, short num2)
  {
  }
  
  public void equals(int num1, int num2)
  {
  }
  
  public void equals(long num1, long num2)
  {
  }
  
  public void equals(char ch1, char ch2)
  {
  }
  
  public void equals(boolean bool1, boolean bool2)
  {
  }
  
  public void equals(String str1, String str2)
  {
  }
}
