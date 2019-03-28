package com.kas.mq.server;

import com.kas.appl.AppLauncher;
import com.kas.infra.utils.RunTimeUtils;

/**
 * MQ server stopper main function.
 * 
 * @author Pippo
 */
public class KasMqStopperMain 
{
  static private final String cKasUser = "kas.user";
  static private final String cKasPass = "kas.pass";
  
  static public void main(String [] args)
  {
    RunTimeUtils.setProperty("kas.home", "./build/install/kas-mq-server", true);
    
    String [] argArray = {
      "kas.class=" + KasMqStopper.class.getName(),
      cKasUser + "=" + "system",
      cKasPass + "=" + "system"
    };
    AppLauncher.main(argArray);
  }
}
