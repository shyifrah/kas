package com.kas.mq.samples;

import java.util.Map;

public class ParamsContainer
{
  private Map<String,String> mMap;
  private String mPrefix;
  
  public String mUserName;
  public String mPassword;
  public String mHost;
  public int    mPort;
  
  public boolean mCreateResources;
  
  public ParamsContainer(Map<String,String> map, String prefix)
  {
    mMap = map;
    mPrefix = prefix;
  }
  
  /**
   * Get int argument value
   * @param key
   * @param defval
   * @return the value
   */
  protected int getIntArg(String key, int defval)
  {
    String strval = mMap.get(mPrefix + key);
    int result = defval;
    if (strval != null)
    {
      try
      {
        int temp = Integer.valueOf(strval);
        result = temp;
      }
      catch (NumberFormatException e) {}
    }
    return result;
  }
  
  /**
   * Get string argument value
   * @param key
   * @param defval
   * @return the value
   */
  protected String getStrArg(String key, String defval)
  {
    String strval = mMap.get(mPrefix + key);
    String result = defval;
    if (strval != null)
    {
      result = strval;
    }
    return result;
  }
  
  /**
   * Get boolean argument value
   * @param key
   * @param defval
   * @return the value
   */
  protected boolean getBoolArg(String key, boolean defval)
  {
    String strval = mMap.get(mPrefix + key);
    boolean result = defval;
    if (strval != null)
    {
      boolean temp = Boolean.valueOf(strval);
      result = temp;
    }
    return result;
  }
  
  public void print()
  {
    System.out.println("Printing specified parameters: ");
    System.out.println("=====================================================");
    for (Map.Entry<String, String> entry : mMap.entrySet())
      System.out.println(entry.getKey() + "=" + entry.getValue());
    System.out.println("=====================================================");
  }
}
