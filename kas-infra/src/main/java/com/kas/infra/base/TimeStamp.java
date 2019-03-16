package com.kas.infra.base;

import java.util.Calendar;

/**
 * A timestamp object
 * 
 * @author Pippo
 */
public class TimeStamp extends AKasObject
{
  static private Calendar sCalendar = Calendar.getInstance();
  
  /**
   * Get current timestamp
   * 
   * @return
   *   current timestamp
   */
  static public TimeStamp now()
  {
    return new TimeStamp();
  }
  
  /**
   * Get timestamp of specified long value
   * 
   * @return
   *   specified timestamp
   */
  static public TimeStamp toTimeStamp(long timeInMillis)
  {
    return new TimeStamp(timeInMillis);
  }
  
  /**
   * Get the difference, in milliseconds, between two timestamps
   * 
   * @return
   *   the difference, in milliseconds
   */
  static public long diff(TimeStamp first, TimeStamp second)
  {
    return first.mTimeInMillis - second.mTimeInMillis;
  }
  
  /**
   * The timestamp represented as milliseconds since January 1st, 1970
   */
  private long mTimeInMillis;
  
  /**
   * Portions of the converted timestamp
   */
  private long mMillis;
  private long mSeconds;
  private long mMinutes;
  private long mHours;
  private long mDay;
  private long mMonth;
  private long mYear;
  
  /**
   * Construct a {@link TestTimeStamp} object using the current timestamp in milliseconds.
   * 
   * @see System#currentTimeMillis()
   */
  private TimeStamp()
  {
    mTimeInMillis = System.currentTimeMillis();
    init();
  }
  
  /**
   * Construct a {@link TestTimeStamp} object using a specified milliseconds value
   * 
   * @param milliseconds
   *   A timestamp represented as milliseconds since January 1st, 1970.
   * 
   * @see System#currentTimeMillis()
   */
  private TimeStamp(long milliseconds)
  {
    mTimeInMillis = milliseconds;
    init();
  }
  
  /**
   * Initialize the timestamp data members
   */
  private void init()
  {
    sCalendar.setTimeInMillis(mTimeInMillis);
    mMillis  = sCalendar.get(Calendar.MILLISECOND);
    mSeconds = sCalendar.get(Calendar.SECOND);
    mMinutes = sCalendar.get(Calendar.MINUTE);
    mHours   = sCalendar.get(Calendar.HOUR_OF_DAY);
    mDay     = sCalendar.get(Calendar.DAY_OF_MONTH);
    mMonth   = sCalendar.get(Calendar.MONTH) + 1;
    mYear    = sCalendar.get(Calendar.YEAR);
  }
  
  /**
   * Get the timestamp in milliseconds
   * 
   * @return
   *   {@link System#currentTimeMillis()} format timestamp
   */
  public long getTimeInMillis()
  {
    return mTimeInMillis;
  }
  
  /**
   * Get the milliseconds portion of the timestamp
   * 
   * @return
   *   the milliseconds portion of the timestamp
   */
  public long getMillis()
  {
    return mMillis;
  }
  
  /**
   * Get the seconds portion of the timestamp
   * 
   * @return
   *   the seconds portion of the timestamp
   */
  public long getSeconds()
  {
    return mSeconds;
  }
  
  /**
   * Get the minutes portion of the timestamp
   * 
   * @return
   *   the minutes portion of the timestamp
   */
  public long getMinutes()
  {
    return mMinutes;
  }
  
  /**
   * Get the hours portion of the timestamp
   * 
   * @return
   *   the hours portion of the timestamp
   */
  public long getHours()
  {
    return mHours;
  }
  
  /**
   * Get the day (of the month) portion of the timestamp
   * 
   * @return
   *   the day (of the month) portion of the timestamp
   */
  public long getDay()
  {
    return mDay;
  }
  
  /**
   * Get the month portion of the timestamp
   * 
   * @return
   *   the month portion of the timestamp
   */
  public long getMonth()
  {
    return mMonth;
  }
  
  /**
   * Get the year portion of the timestamp
   * 
   * @return
   *   the year portion of the timestamp
   */
  public long getYear()
  {
    return mYear;
  }
  
  /**
   * Return a formatted timestamp string.<br>
   * The string is in "YYYY-MM-DD hh:mm:ss,nnn" format
   * 
   * @return
   *   the timestamp in string format
   */
  public String toString()
  {
    return String.format("%04d-%02d-%02d %02d:%02d:%02d,%03d", mYear, mMonth, mDay, mHours, mMinutes, mSeconds, mMillis);
  }
  
  /**
   * Get the object's detailed string representation.
   * 
   * @param level
   *   The string padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append('(')
      .append(getTimeInMillis())
      .append("),(")
      .append(toString())
      .append(')');
    return sb.toString();
  }
}
