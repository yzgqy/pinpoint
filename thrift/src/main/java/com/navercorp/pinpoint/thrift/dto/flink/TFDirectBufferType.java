/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.navercorp.pinpoint.thrift.dto.flink;


public enum TFDirectBufferType implements org.apache.thrift.TEnum {
  UNKNOWN(0),
  DIRECT(1),
  MAPPED(2);

  private final int value;

  private TFDirectBufferType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static TFDirectBufferType findByValue(int value) { 
    switch (value) {
      case 0:
        return UNKNOWN;
      case 1:
        return DIRECT;
      case 2:
        return MAPPED;
      default:
        return null;
    }
  }
}
