package com.kas.q.server.internal;

import com.kas.infra.base.Properties;
import com.kas.q.ext.IDestination;
import com.kas.q.ext.IMessage;
import com.kas.q.impl.messages.KasqTextMessage;
import com.kas.q.server.KasqRepository;
import com.kas.q.server.KasqServer;

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
  private static final String cDestinationAttributeKey = "Dest";
  private static final String cTypeAttributeKey        = "Type";
  
  private static final String cAuthCommandValue = "Auth";
  private static final String cRecvCommandValue = "Recv";
  
  private static final String cOkayResponseValue = "Okay";
  private static final String cFailResponseValue = "Fail";
  
  /***************************************************************************************************************
   * Process command
   *  
   * @param command the full command text
   * 
   * @return a generated message in response for the command
   */
  public static IMessage process(String command)
  {
    IMessage response;
    
    Properties props = parseCommand(command);
    if ((props == null) || (props.isEmpty()))
    {
      response = new KasqTextMessage(cCommandUnknown);
    }
    else
    {
      String verb = props.getStringProperty(cCommandAttributeKey, "");
      if ((verb == null) || (verb.length() == 0))
      {
        response = new KasqTextMessage(cCommandUnknown);
      }
      else if (cAuthCommandValue.equals(verb))
      {
        response = handleAuthCommand(props);
      }
      else if (cRecvCommandValue.equals(verb))
      {
        response = handleRecvCommand(props);
      }
      else
      {
        response = new KasqTextMessage(cCommandUnknown);
      }
    }
    
    return response;
  }
  
  /***************************************************************************************************************
   * Parse command text
   *  
   * @param command the full command text
   * 
   * @return a {@code Properties} object consisting of all command attributes and their values
   */
  private static Properties parseCommand(String command)
  {
    // command format: "Cmd:<cmd_type>,<attr1_key>:<attr1_val>,<attr2_key>:<attr2_val>,...,<attrN_key>:<attrN_val>";
    Properties props = new Properties();
    String [] tokens = command.split(",");
    
    for (String token : tokens)
    {
      String [] array = token.split(":");
      if (array.length != 2)
      {
        props = null;
        break;
      }
      else
      {
        String key = array[0];
        String val = array[1];
        props.setProperty(key, val);
      }
    }
    return props;
  }
  
  /***************************************************************************************************************
   * Handle authentication request 
   *  
   * @param props the {@code Properties} object containing all attributes describing the command 
   * 
   * @return the response message
   */
  private static IMessage handleAuthCommand(Properties props)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(cCommandAttributeKey)
      .append(':')
      .append(cAuthCommandValue)
      .append(',')
      .append(cResponseAttributeKey)
      .append(':');
    
    String userName = props.getStringProperty(cUserNameAttributeKey, "");
    String password = props.getStringProperty(cPasswordAttributeKey, "");
    
    if ((userName == null) || (userName.length() == 0) || (password == null) || (password.length() == 0))
    {
      sb.append(cFailResponseValue);
    }
    else
    {
      // TODO: query the security manager and generate the appropriate response
      sb.append(cOkayResponseValue);
    }
    
    IMessage response = new KasqTextMessage(sb.toString());
    
    return response;
  }
  
  /***************************************************************************************************************
   * Handle receive request 
   *  
   * @param props the {@code Properties} object containing all attributes describing the command
   * 
   * @return the response message
   */
  private static IMessage handleRecvCommand(Properties props)
  {
    String dest = props.getStringProperty(cDestinationAttributeKey, "");
    String type = props.getStringProperty(cTypeAttributeKey, "");
    
    IMessage response = null;
    if ((dest == null) || (dest.length() == 0) || (type == null) || (type.length() == 0))
    {
      StringBuffer sb = new StringBuffer();
      sb.append(cCommandAttributeKey)
        .append(':')
        .append(cRecvCommandValue)
        .append(',')
        .append(cResponseAttributeKey)
        .append(':')
        .append(cFailResponseValue);
      response = new KasqTextMessage(sb.toString());
    }
    else
    {
      KasqRepository repo = KasqServer.getInstance().getRepository();
      IDestination target = null;
      if (type.equals("T"))
      {
        target = repo.locateTopic(dest);
      }
      else
      if (type.equals("Q"))
      {
        target = repo.locateQueue(dest);
      }
      
      if (target != null)
      {
        response = target.get();
      }
      else
      {
        StringBuffer sb = new StringBuffer();
        sb.append(cCommandAttributeKey)
          .append(':')
          .append(cRecvCommandValue)
          .append(',')
          .append(cResponseAttributeKey)
          .append(':')
          .append(cFailResponseValue);
        response = new KasqTextMessage(sb.toString());
      }
    }
    
    return response;
  }
}
