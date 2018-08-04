package com.kas.infra.utils;

public class NumUtils
{
  /**
   * Return max value from a variable list of integer arguments
   * 
   * @param nums A variable number of arguments
   * @return The max value from the specified argument list
   * 
   * @throws IllegalArgumentException if {@code nums} is {@code null} or zero-length list.
   */
  public static int max(int ... nums)
  {
    if (nums == null)
      throw new IllegalArgumentException("Cannot return max value of null argument list");
    
    if (nums.length == 0)
      throw new IllegalArgumentException("Cannot return max value of 0 length argument list");
    
    int maxValue = Integer.MIN_VALUE;
    for (int i = 0; i < nums.length; ++i)
    {
      if (nums[i] > maxValue)
        maxValue = nums[i];
    }
    return maxValue;
  }
}
