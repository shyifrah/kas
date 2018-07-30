package com.kas.infra.base;

public class TestProperties
{
  static boolean compare(UniqueId first, UniqueId second)
  {
    return first.getUuid().equals(second.getUuid());
  }
}
