package com.kas.mq.admin;

import com.kas.appl.AppLauncher;
import com.kas.infra.utils.RunTimeUtils;

/**
 * KAS/MQ admin console main function
 * 
 * @author Pippo
 */
public class KasMqAdminMain 
{
  static public void main(String [] args)
  {
    RunTimeUtils.setProperty("kas.home", "./build/install/kas-mq-admin", true);
    
    String [] argArray = {
      "kas.class=" + KasMqAdmin.class.getName()
    };
    AppLauncher.main(argArray);
  }
}
