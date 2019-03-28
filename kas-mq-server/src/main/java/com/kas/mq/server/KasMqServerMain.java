package com.kas.mq.server;

import com.kas.appl.AppLauncher;
import com.kas.infra.utils.RunTimeUtils;

/**
 * MQ server main function
 * 
 * @author Pippo
 */
public class KasMqServerMain
{
  static public void main(String [] args)
  {
    RunTimeUtils.setProperty("kas.home", "./build/install/kas-mq-server", true);
    
    String [] argArray = {
      "kas.class=" + KasMqServer.class.getName()                    
    };
    AppLauncher.main(argArray);
  }
}
