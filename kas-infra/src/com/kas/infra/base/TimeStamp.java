package com.kas.infra.base;

import java.util.Calendar;

public class TimeStamp extends AKasObject
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static Calendar sCalendar = Calendar.getInstance();
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private long mTimeInMilliSeconds;
  private long mMilliseconds;
  private long mSecond;
  private long mMinute;
  private long mHour;
  private long mDay;
  private long mMonth;
  private long mYear;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public TimeStamp()
  {
    mTimeInMilliSeconds = System.currentTimeMillis();
    init();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public TimeStamp(long milliseconds)
  {
    mTimeInMilliSeconds = milliseconds;
    init();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void init()
  {
    sCalendar.setTimeInMillis(mTimeInMilliSeconds);
    mMilliseconds = sCalendar.get(Calendar.MILLISECOND);
    mSecond       = sCalendar.get(Calendar.SECOND);
    mMinute       = sCalendar.get(Calendar.MINUTE);
    mHour         = sCalendar.get(Calendar.HOUR_OF_DAY);
    mDay          = sCalendar.get(Calendar.DAY_OF_MONTH);
    mMonth        = sCalendar.get(Calendar.MONTH) + 1;
    mYear         = sCalendar.get(Calendar.YEAR);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private String toRawString()
  {
    return Long.toString(mTimeInMilliSeconds);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getDateString()
  {
    return getDateString("");
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getDateString(String seperator)
  {
    return String.format("%04d" + seperator + "%02d" + seperator + "%02d", mYear, mMonth, mDay);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getTimeString()
  {
    return getTimeString("");
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getTimeString(String seperator)
  {
    return String.format("%02d" + seperator + "%02d" + seperator + "%02d", mHour, mMinute, mSecond);
  }
  
  /**
   * Return the difference, in milliseconds, between this TimeStamp object and other one
   * 
   * @param other A second timestamp represented by milliseconds
   * @return The difference, expressed in milliseconds
   */
  public long diff(long other)
  {
    return (other > mTimeInMilliSeconds ? other - mTimeInMilliSeconds : mTimeInMilliSeconds - other);
  }
  
  public long diff(TimeStamp other)
  {
    return diff(other.mTimeInMilliSeconds);
  }
  
  static public long diff(TimeStamp first, TimeStamp second)
  {
    return (first.mTimeInMilliSeconds > second.mTimeInMilliSeconds ? first.mTimeInMilliSeconds - second.mTimeInMilliSeconds : second.mTimeInMilliSeconds - first.mTimeInMilliSeconds);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toString()
  {
    return String.format("%04d-%02d-%02d %02d:%02d:%02d,%03d", mYear, mMonth, mDay, mHour, mMinute, mSecond, mMilliseconds);
  }
  
  /**
   * Returns a replica of this {@link #TimeStamp}.
   * 
   * @return a replica of this {@link #TimeStamp}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public TimeStamp replicate()
  {
    return new TimeStamp(mTimeInMilliSeconds);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(").append(toRawString()).append(")");
    return sb.toString();
  }
}
