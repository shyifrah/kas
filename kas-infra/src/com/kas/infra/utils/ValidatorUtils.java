package com.kas.infra.utils;

import java.util.regex.Pattern;

public class ValidatorUtils
{
  static private final String cIpAddressPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
  static private Pattern cIpAddressCompiledPattern = Pattern.compile(cIpAddressPattern);
  
  static private final String cHostNamePattern = "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])\\(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
  static private Pattern cHostNameCompiledPattern = Pattern.compile(cHostNamePattern);
  
  static private final String cQueueNamePattern = "^[a-zA-Z][a-zA-Z0-9\\._]{0,47}$";
  static private Pattern cQueueNameCompiledPattern = Pattern.compile(cQueueNamePattern);
  
  static private final String cUserNamePattern =  "^[a-zA-Z][a-zA-Z0-9\\._]*$";
  static private Pattern cUserNameCompiledPattern = Pattern.compile(cUserNamePattern);
  
  static private final int cMinimumPortNumber = 1;
  static private final int cMaxomumPortNumber = (Short.MAX_VALUE + 1) * 2 - 1;
  
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
  static public boolean isPort(int port)
  {
    return (port >= cMinimumPortNumber) && (port <= cMaxomumPortNumber);
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
