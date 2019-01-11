package com.kas.infra.utils;

import java.util.regex.Pattern;

public class Validators
{
  static private final String cIpAddressPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
  static private Pattern cIpAddressCompiledPattern = Pattern.compile(cIpAddressPattern);
  
  static private final String cHostNamePattern = "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
  static private Pattern cHostNameCompiledPattern = Pattern.compile(cHostNamePattern);
  
  static private final String cQueueNamePattern = "^[a-zA-Z][a-zA-Z0-9\\._]{0,47}$";
  static private Pattern cQueueNameCompiledPattern = Pattern.compile(cQueueNamePattern);
  
  static private final String cUserNamePattern =  "^[a-zA-Z][a-zA-Z0-9\\._]*$";
  static private Pattern cUserNameCompiledPattern = Pattern.compile(cUserNamePattern);
  
  static private final int cMinimumPortNumber = 1;
  static private final int cMaximumPortNumber = (Short.MAX_VALUE + 1) * 2 - 1;
  
  static private final int cMinimumThreshold = 1;
  static private final int cMaximumThreshold = 100000;
  
  static private final int cMaximumQueueDescriptionLength = 256;
  
  static private final String cNumericPattern = "[+-]?\\d+";
  static private Pattern cNumericCompiledPattern = Pattern.compile(cNumericPattern);
  
  /**
   * Validate {@code string} can be converted into a numeric value
   * 
   * @param string The string to validate
   * @return {@code true} if {@link Integer#valueOf(String)} can be called for {@code string}
   * without throwing an exception, {@code false} otherwise
   */
  static public boolean isNumeric(String string)
  {
    if ((string == null) || (string.trim().length() == 0)) return false;
    String numstr = string.trim();
    return cNumericCompiledPattern.matcher(numstr).matches();
  }
  
  /**
   * Validate {@code threshold} can be set for queue 
   * 
   * @param threshold The Threshold to validate
   * @return {@code true} if {@code threshold} is valid, {@code false} otherwise
   */
  static public boolean isThreshold(String threshold)
  {
    if (isNumeric(threshold))
    {
      int th = Integer.parseInt(threshold);
      return isThreshold(th);
    }
    return false;
  }
  
  /**
   * Validate {@code threshold} can be set for queue 
   * 
   * @param threshold The Threshold to validate
   * @return {@code true} if {@code threshold} is valid, {@code false} otherwise
   */
  static public boolean isThreshold(int threshold)
  {
    if ((threshold >= cMinimumThreshold) && (threshold <= cMaximumThreshold))
      return true;
    return false;
  }
  
  /**
   * Validate IP address with regular expression
   * 
   * @param ip The IP address for validation
   * @return {@code true} if {@code ip} is a valid IP address, {@code false} otherwise
   */
  static public boolean isIpAddress(String ip)
  {
    if ((ip == null) || (ip.trim().length() == 0)) return false;
    return cIpAddressCompiledPattern.matcher(ip).matches();
  }
  
  /**
   * Validate Host name with regular expression
   * 
   * @param host The host name for validation
   * @return {@code true} if {@code host} is a valid host name, {@code false} otherwise
   */
  static public boolean isHostName(String host)
  {
    if ((host == null) || (host.trim().length() == 0)) return false;
    return cHostNameCompiledPattern.matcher(host).matches();
  }
  
  /**
   * Validate port number
   * 
   * @param port The port number for validation
   * @return {@code true} if {@code port} is a valid port number, {@code false} otherwise
   */
  static public boolean isPort(String port)
  {
    if (isNumeric(port))
    {
      int p = Integer.parseInt(port);
      return (p >= cMinimumPortNumber) && (p <= cMaximumPortNumber);
    }
    return false;
  }
  
  /**
   * Validate port number
   * 
   * @param port The port number for validation
   * @return {@code true} if {@code port} is a valid port number, {@code false} otherwise
   */
  static public boolean isPort(int port)
  {
    return (port >= cMinimumPortNumber) && (port <= cMaximumPortNumber);
  }
  
  /**
   * Validate queue name with regular expression
   * 
   * @param queue The queue name for validation
   * @return {@code true} if {@code queue} is a valid queue name, {@code false} otherwise
   */
  static public boolean isQueueName(String queue)
  {
    if ((queue == null) || (queue.trim().length() == 0)) return false;
    return cQueueNameCompiledPattern.matcher(queue).matches(); 
  }
  
  /**
   * Validate queue description
   * 
   * @param desc The queue description for validation
   * @return {@code true} if {@code desc} is a valid queue description, {@code false} otherwise
   */
  static public boolean isQueueDesc(String desc)
  {
    if (desc == null) return false;
    if (desc.trim().length() > cMaximumQueueDescriptionLength) return false;
    return true;
  }
  
  /**
   * Validate user name with regular expression
   * 
   * @param user The user name for validation
   * @return {@code true} if {@code user} is a valid user name, {@code false} otherwise
   */
  static public boolean isUserName(String user)
  {
    if ((user == null) || (user.trim().length() == 0)) return false;
    return cUserNameCompiledPattern.matcher(user).matches();
  }
}
