package com.kas.infra.base;

import java.util.UUID;
import com.kas.infra.test.ObjectTest;

public class TestUniqueId
{
  static public void main(String [] args)
  {
    ObjectTest oTest;
    
    UniqueId allZeroes = UniqueId.cNullUniqueId;
    UniqueId copyOfAllZeroes = new UniqueId(allZeroes);
    
    oTest = new ObjectTest(copyOfAllZeroes);
    oTest.add("equals", true, allZeroes);
    oTest.add("toString", "00000000-0000-0000-0000-000000000000");
    oTest.run();
    
    
    UUID randomUuid = UUID.randomUUID();
    UniqueId copyOfRandomUuid = new UniqueId(randomUuid);
    
    oTest = new ObjectTest(copyOfRandomUuid);
    oTest.add("getUuid", randomUuid);
    oTest.run();
    
    byte [] byteArrayUuid = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };
    String  stringUuid    = "01020304-0506-0708-0900-0a0b0c0d0e0f";
    UniqueId fromByteArray = UniqueId.fromByteArray(byteArrayUuid);
    UniqueId fromString    = UniqueId.fromString(stringUuid);
    oTest = new ObjectTest(fromByteArray);
    oTest.add("toString", stringUuid);
    oTest.add("equals", true, fromString);
    oTest.run();
  }
}
