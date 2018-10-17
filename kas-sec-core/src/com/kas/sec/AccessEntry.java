package com.kas.sec;

import java.util.regex.Pattern;
import com.kas.infra.base.AKasObject;

/**
 * An access entry is a an entry in an access list that protects a resource(s).<br>
 * <br>
 * The resources protected by this entry are designated by the regular expression {@code mResourceRegEx}
 * and the permitted access level by the {@code mPermittedAccess}.
 * 
 * @author Pippo
 */
public class AccessEntry extends AKasObject
{
  /**
   * Resource regular expression
   */
  private Pattern mResourceRegEx;
  
  /**
   * Permitted access level
   */
  private AccessLevel mPermittedAccess;
  
  /**
   * Construct an access-entry
   * 
   * @param resource A regular expression that identifies the resources protected by this entry
   * @param accessLevel The permitted level of access
   */
  AccessEntry(String resource, AccessLevel accessLevel)
  {
    mResourceRegEx = Pattern.compile(resource);
    mPermittedAccess = accessLevel;
  }
  
  /**
   * Test if {@code resource} is protected by this {@link AccessEntry}.
   * 
   * @param resource The resource tested
   * @return {@code true} if this {@link AccessEntry} protects {@code resource}.
   */
  public boolean isMatched(String resource)
  {
    return mResourceRegEx.matcher(resource).matches();
  }
  
  /**
   * Get the access level allowed for the specified resource
   * 
   * @param resource The resource tested
   * @return the allowed {@link AccessLevel}
   */
  public AccessLevel getAllowedAccessLevel(String resource)
  {
    if (isMatched(resource))
      return mPermittedAccess;
    return AccessLevel.NONE_ACCESS;
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  RegEx=").append(mResourceRegEx.toString()).append("\n")
      .append(pad).append("  Access=").append(mPermittedAccess.toPrintableString(level+1)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
