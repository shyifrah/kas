package com.kas.q.ext.impl;

import javax.jms.DeliveryMode;

public class JmsUtils
{
  public static String toString(int deliveryMode)
  {
    String result;
    switch (deliveryMode)
    {
      case DeliveryMode.PERSISTENT:
        result = "PERSISTENT";
        break;
      case DeliveryMode.NON_PERSISTENT:
        result = "NON_PERSISTENT";
        break;
      default:
        result = "";
    }
    return result;
  }
}
