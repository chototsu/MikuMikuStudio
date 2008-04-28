/**
 * SchemaBoolean.java
 *
 * This file was generated by XMLSpy 2007sp2 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSpy Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package com.jmex.xml.types;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SchemaBoolean implements SchemaTypeNumber {
  protected boolean value;
  protected boolean isempty;
  protected boolean isnull;

  // construction
  public SchemaBoolean() {
    setEmpty();
  }

  public SchemaBoolean(SchemaBoolean newvalue) {
    value = newvalue.value;
    isempty = newvalue.isempty;
    isnull = newvalue.isnull;
  }

  public SchemaBoolean(boolean newvalue) {
    setValue( newvalue );
  }

  public SchemaBoolean(String newvalue) {
    parse( newvalue );
  }

  public SchemaBoolean(SchemaType newvalue) {
    assign( newvalue );
  }

  public SchemaBoolean(SchemaTypeNumber newvalue) {
    assign( (SchemaType)newvalue );
  }

  // getValue, setValue
  public boolean getValue() {
    return value;
  }

  public void setValue(boolean newvalue) {
    isnull = false;
    isempty = false;
    value = newvalue;
  }

 public void parse(String s) {
	  
	String newvalue = SchemaNormalizedString.normalize(SchemaNormalizedString.WHITESPACE_COLLAPSE, s);

    if( newvalue == null )
      setNull();
    else if( newvalue.length() == 0)
      setEmpty();
    else
      setValue( new SchemaString(newvalue).booleanValue() );
  }

  public void assign(SchemaType newvalue) {
    if( newvalue == null || newvalue.isNull() )
      setNull();
    else if( newvalue.isEmpty() )
      setEmpty();
    else
      parse( newvalue.toString() );
  }

  public void setNull() {
    isnull = true;
    isempty = true;
    value = false;
  }

  public void setEmpty() {
    isnull = false;
    isempty = true;
    value = false;
  }

  // further
  public int hashCode() {
    return value ? 1231 : 1237;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof SchemaBoolean))
      return false;
    return value == ( (SchemaBoolean) obj).value;
  }

  public Object clone() {
    return new SchemaBoolean(this);
  }

  public String toString() {
    if( isempty || isnull )
      return "";
    return value ? "true" : "false"; // when converting to an string-value, than XML-Schema would expect "" for false.
  }

  public int length() {
    return 1;	// length of the number (0 or 1)
  }

  public boolean booleanValue() {
    return value;
  }

  public boolean isEmpty() {
    return isempty;
  }

  public boolean isNull() {
    return isnull;
  }

  public int compareTo(Object obj) {
    return compareTo( (SchemaBoolean) obj);
  }

  public int compareTo(SchemaBoolean obj) {
    if (value == obj.value)
      return 0;
    else if (value == false)
      return -1;
    else
      return 1;
  }

  // interface SchemaTypeNumber
  public int numericType() {
    return NUMERIC_VALUE_INT;
  }

  public void setValue(int newvalue) {
    isnull = false;
    isempty = false;
    value = ! (newvalue == 0);
  }

  public void setValue(long newvalue) {
    isnull = false;
    isempty = false;
    value = ! (newvalue == 0);
  }

  public void setValue(BigInteger newvalue) {
    isnull = false;
    isempty = false;
    value = newvalue.compareTo(BigInteger.valueOf(0)) != 0;
  }

  public void setValue(float newvalue) {
    isnull = false;
    isempty = false;
    value = ! (newvalue == 0);
  }

  public void setValue(double newvalue) {
    isnull = false;
    isempty = false;
    value = ! (newvalue == 0);
  }

  public void setValue(BigDecimal newvalue) {
    isnull = false;
    isempty = false;
    value = newvalue.compareTo(BigDecimal.valueOf(0)) != 0;
  }

  public int intValue() {
    if (value)
      return 1;
    return 0;
  }

  public long longValue() {
    if (value)
      return 1L;
    return 0L;
  }

  public BigInteger bigIntegerValue() {
    if (value)
      return BigInteger.valueOf(1L);
    return BigInteger.valueOf(0L);
  }

  public float floatValue() {
    if (value)
      return 1;
    return 0;
  }

  public double doubleValue() {
    if (value)
      return 1.0;
    return 0.0;
  }

  public BigDecimal bigDecimalValue() {
    if (value)
      return BigDecimal.valueOf(1L);
    return BigDecimal.valueOf(0L);
  }
}
