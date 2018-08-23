package com.kas.infra.base;

import java.util.Calendar;

/**
 * A timestamp object
 * 
 * @author Pippo
 */
public class TimeStamp extends AKasObject
{
  /**
   * A {@link Calendar} object used in coversions
   */
  static private Calendar sCalendar = Calendar.getInstance();
  
  /**
   * Get current timestamp in string format
   * 
   * @return current timestamp in string format
   */
  static public String nowAsString()
  {
    return now().toString();
  }
  
  /**
   * Get current timestamp
   * 
   * @return current timestamp
   */
  static public TimeStamp now()
  {
    return new TimeStamp();
  }
  
  /**
   * The timestamp represented as milliseconds since January 1st, 1970
   */
  private long mTimeInMilliSeconds;
  
  /**
   * The milliseconds portion of the timestamp
   */
  private long mMilliseconds;
  
  /**
   * The seconds portion of the timestamp
   */
  private long mSecond;
  
  /**
   * The minutes portion of the timestamp
   */
  private long mMinute;
  
  /**
   * The hours portion of the timestamp
   */
  private long mHour;
  
  /**
   * The days portion of the timestamp
   */
  private long mDay;
  
  /**
   * The months portion of the timestamp
   */
  private long mMonth;
  
  /**
   * The years portion of the timestamp
   */
  private long mYear;
  
  /**
   * Construct a {@link TimeStamp} object using the current timestamp in milliseconds.
   * 
   * @see java.lang.System#currentTimeMillis()
   */
  public TimeStamp()
  {
    mTimeInMilliSeconds = System.currentTimeMillis();
    init();
  }
  
  /**
   * Construct a {@link TimeStamp} object using a specified milliseconds value
   * 
   * @param milliseconds A timestamp represented as milliseconds since January 1st, 1970.
   * 
   * @see java.lang.System#currentTimeMillis()
   */
  public TimeStamp(long milliseconds)
  {
    mTimeInMilliSeconds = milliseconds;
    init();
  }
  
  /**
   * Initialize the timestamp data members
   */
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
  
  /**
   * Get the timestamp milliseconds in string format
   * 
   * @return the timestamp string
   */
  private String toRawString()
  {
    return Long.toString(mTimeInMilliSeconds);
  }
  
  /**
   * Get only the date portion of the timestamp in string format.
   * 
   * @return the date string
   */
  public String getDateString()
  {
    return getDateString("");
  }
  
  /**
   * Get only the date portion of the timestamp in string format.
   * 
   * @param seperator A character used to separate between years, months and days 
   * @return the date string
   */
  public String getDateString(String seperator)
  {
    return String.format("%04d" + seperator + "%02d" + seperator + "%02d", mYear, mMonth, mDay);
  }
  
  /**
   * Get only the time portion of the timestamp in string format.
   * 
   * @return the time string
   */
  public String getTimeString()
  {
    return getTimeString("");
  }
  
  /**
   * Get only the time portion of the timestamp in string format.
   * 
   * @param seperator A character used to separate between hours, minutes and seconds 
   * @return the time string
   */
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
    return mTimeInMilliSeconds-other;
  }
  
  /**
   * Return the difference, in milliseconds, between this TimeStamp object and other one
   * 
   * @param other A second timestamp represented by {@link TimeStamp}
   * @return The difference, expressed in milliseconds
   */
  public long diff(TimeStamp other)
  {
    return diff(other.mTimeInMilliSeconds);
  }
  
  /**
   * Return a formatted timestamp string.<br>
   * <br>
   * The string is in "YYYY-MM-DD hh:mm:ss,nnn" format
   * 
   * @return the timestamp in string format
   */
  public String toString()
  {
    return String.format("%04d-%02d-%02d %02d:%02d:%02d,%03d", mYear, mMonth, mDay, mHour, mMinute, mSecond, mMilliseconds);
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
    StringBuilder sb = new StringBuilder();
    sb.append(name())
      .append('(')
      .append(toRawString())
      .append(')')
      .append(",(")
      .append(toString())
      .append(')');
    return sb.toString();
  }
}
