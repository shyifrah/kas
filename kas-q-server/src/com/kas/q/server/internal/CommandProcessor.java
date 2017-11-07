package com.kas.q.server.internal;

import java.util.HashMap;
import java.util.Map;

public class CommandProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static final String cCommandUnknown = "Cmd:Unkn";
  
  private static final String cCommandAttributeKey  = "Cmd";
  private static final String cResponseAttributeKey = "Resp";
  private static final String cUserNameAttributeKey = "User";
  private static final String cPasswordAttributeKey = "Pass";
  
  private static final String cAuthCommandValue = "Auth";
  
  private static final String cOkayResponseValue = "Okay";
  private static final String cFailResponseValue = "Fail";
  
  /***************************************************************************************************************
   * Process command
   *  
   * @param command the full command text
   * 
   * @return a generated message body in response for the command
   */
  public static String process(String command)
  {
    String response;
    
    Map<String, String> attrs = parseCommand(command);
    if ((attrs == null) || (attrs.isEmpty()))
    {
      response = cCommandUnknown;
    }
    else
    {
      String verb = attrs.get(cCommandAttributeKey);
      if (verb == null)
      {
        response = cCommandUnknown;
      }
      else if (cAuthCommandValue.equals(verb))
      {
        response = handleAuthCommand(attrs);
      }
      else
      {
        response = cCommandUnknown;
      }
    }
    
    return response;
  }
  
  /***************************************************************************************************************
   * Parse command text
   *  
   * @param command the full command text
   * 
   * @return a map consisting of all command attributes and their values
   */
  private static Map<String, String> parseCommand(String command)
  {
    // command format: "Cmd:<cmd_type>,<attr1_key>:<attr1_val>,<attr2_key>:<attr2_val>,...,<attrN_key>:<attrN_val>";
    Map<String, String> map = new HashMap<String, String>();
    String [] tokens = command.split(",");
    
    for (String token : tokens)
    {
      String [] array = token.split(":");
      if (array.length != 2)
      {
        map = null;
        break;
      }
      else
      {
        String key = array[0];
        String val = array[1];
        map.put(key, val);
      }
    }
    return map;
  }
  
  /***************************************************************************************************************
   * Handle authentication request 
   *  
   * @param attributes the map containing all attributes describing the command 
   * 
   * @return the command response
   */
  private static String handleAuthCommand(Map<String, String> attributes)
  {
    StringBuffer response = new StringBuffer();
    response.append(cCommandAttributeKey)
      .append(':')
      .append(cAuthCommandValue)
      .append(',')
      .append(cResponseAttributeKey)
      .append(':');
    
    String userName = attributes.get(cUserNameAttributeKey);
    String password = attributes.get(cPasswordAttributeKey);
    
    if ((userName == null) || (userName.length() == 0) || (password == null) || (password.length() == 0))
    {
      response.append(cFailResponseValue);
    }
    else
    {
      // TODO: query the security manager and generate the appropriate response
      response.append(cOkayResponseValue);
    }
    
    return response.toString();
  }
}
