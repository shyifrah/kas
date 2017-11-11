package com.kas.q.ext;

public enum ReadWriteMode
{
  /***************************************************************************************************************
   *  
   */
  cReadOnly("read only"),
  cWriteOnly("write only"),
  cReadWrite("read/write");
  
  /***************************************************************************************************************
   *  
   */
  protected String mDesc;
  
  /***************************************************************************************************************
   * Constructs an enumeration value with a specific description
   *  
   */
  private ReadWriteMode(String desc)
  {
    mDesc = desc;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toString()
  {
    return mDesc;
  }
}
