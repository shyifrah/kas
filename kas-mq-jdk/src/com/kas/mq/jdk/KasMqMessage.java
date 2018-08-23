package com.kas.mq.jdk;

import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.internal.ERequestType;

public final class KasMqMessage implements IMqMessage
{
  private MqMessage mMessage;

  public UniqueId getMessageId()
  {
    return mMessage.getMessageId();
  }

  public void setPriority(int priority)
  {
    mMessage.setPriority(priority);
  }

  public int getPriority()
  {
    return mMessage.getPriority();
  }

  public void setRequestType(ERequestType type)
  {
    mMessage.setRequestType(type);
  }

  public ERequestType getRequestType()
  {
    return mMessage.getRequestType();
  }

  public Object getObjectProperty(String key, Object defaultValue)
  {
    return mMessage.getObjectProperty(key, defaultValue);
  }

  public void setObjectProperty(String key, Object value)
  {
    mMessage.setObjectProperty(key, value);
  }

  public boolean getBoolProperty(String key, boolean defaultValue)
  {
    return mMessage.getBoolProperty(key, defaultValue);
  }

  public void setBoolProperty(String key, boolean value)
  {
    mMessage.setBoolProperty(key, value);
  }

  public String getStringProperty(String key, String defaultValue)
  {
    return mMessage.getStringProperty(key, defaultValue);
  }

  public void setStringProperty(String key, String value)
  {
    mMessage.setStringProperty(key, value);
  }

  public byte getByteProperty(String key, byte defaultValue)
  {
    return mMessage.getByteProperty(key, defaultValue);
  }

  public void setByteProperty(String key, byte value)
  {
    mMessage.setByteProperty(key, value);
  }

  public short getShortProperty(String key, short defaultValue)
  {
    return mMessage.getShortProperty(key, defaultValue);
  }

  public void setShortProperty(String key, short value)
  {
    mMessage.setShortProperty(key, value);
  }

  public int getIntProperty(String key, int defaultValue)
  {
    return mMessage.getIntProperty(key, defaultValue);
  }

  public void setIntProperty(String key, int value)
  {
    mMessage.setIntProperty(key, value);
  }

  public long getLongProperty(String key, long defaultValue)
  {
    return mMessage.getLongProperty(key, defaultValue);
  }

  public void setLongProperty(String key, long value)
  {
    mMessage.setLongProperty(key, value);
  }

  public float getFloatProperty(String key, float defaultValue)
  {
    return mMessage.getFloatProperty(key, defaultValue);
  }

  public void setFloatProperty(String key, float value)
  {
    mMessage.setFloatProperty(key, value);
  }

  public double getDoubleProperty(String key, double defaultValue)
  {
    return mMessage.getDoubleProperty(key, defaultValue);
  }

  public void setDoubleProperty(String key, double value)
  {
    mMessage.setDoubleProperty(key, value);
  }
}
