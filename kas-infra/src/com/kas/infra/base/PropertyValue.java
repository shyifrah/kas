package com.kas.infra.base;

import com.kas.infra.utils.RunTimeUtils;

class PropertyValue extends KasObject
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private String  mRawValue;
  private String  mActualValue;
  private boolean mResolved = false;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public PropertyValue(String rawValue)
  {
    mRawValue = rawValue;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private String resolve()
  {
    return resolve(mRawValue);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private String resolve(String rawValue)
  {
    String result = rawValue;
    
    if (mResolved) return mActualValue;

    int startIdx = rawValue.indexOf("${");
    int endIdx   = rawValue.indexOf('}');
    if ((startIdx == -1) || (endIdx == -1) || (startIdx >= endIdx))
    {
      mResolved = true;
      return rawValue;
    }
    
    String var = rawValue.substring(startIdx+2, endIdx);
    String val = RunTimeUtils.getProperty(var);
    
    StringBuffer sb = new StringBuffer();
    sb.append(rawValue.substring(0, startIdx));
    sb.append(val);
    sb.append(rawValue.substring(endIdx+1));
    result = sb.toString();
    
    return resolve(result);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getActual()
  {
    if (!mResolved)
      mActualValue = resolve();
    
    return mActualValue;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    StringBuffer sb = new StringBuffer();
    
    // [Raw=(...)] = [Actual=(..)]
    sb.append(name()).append("(")
      .append("[Raw=(").append(mRawValue).append(")]")
      .append(" = [Actual=(").append(getActual()).append(")]")
      .append(")");
    
    return sb.toString();
  }
}
