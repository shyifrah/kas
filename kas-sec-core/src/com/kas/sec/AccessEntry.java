package com.kas.sec;

import java.util.regex.Pattern;
import com.kas.infra.base.AKasObject;

public class AccessEntry extends AKasObject
{
  private Pattern mResourceRegEx;
  private AccessLevel mPermittedAccess;
  
  AccessEntry(String resource, AccessLevel accessLevel)
  {
    mResourceRegEx = Pattern.compile(resource);
    mPermittedAccess = accessLevel;
  }
  
  public boolean isMatched(String resource)
  {
    return mResourceRegEx.matcher(resource).matches();
  }
  
  public AccessLevel getAllowedAccessLevel(String resource)
  {
    if (mResourceRegEx.matcher(resource).matches())
      return mPermittedAccess;
    return AccessLevel.NONE_ACCESS;
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
}
