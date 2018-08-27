package com.kas.infra.utils;

import com.kas.infra.base.UniqueId;

public class TestStringUtils
{
  static public void main(String [] args)
  {
    UniqueId uid = UniqueId.generate();
    byte [] ba = uid.toByteArray();
    
    System.out.println("UniqueId.toString()............................: " + uid.toString());
    System.out.println("StringUtils.asHexString( uid.toByteArray() )...: " + StringUtils.asHexString(ba));
  }
}
