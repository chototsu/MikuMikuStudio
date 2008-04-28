/**
 * SchemaTime.java
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

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


public class SchemaTime extends SchemaCalendarBase {

  // construction
  public SchemaTime() {
    super();
  }

  public SchemaTime(SchemaTime newvalue) {
    setInternalValues(1, 1, 1, newvalue.hour, newvalue.minute, newvalue.second, newvalue.partsecond, newvalue.hasTZ, newvalue.offsetTZ);
    isempty = newvalue.isempty;
  }
  
  public SchemaTime (SchemaDateTime newvalue) {
    setInternalValues(1, 1, 1, newvalue.hour, newvalue.minute, newvalue.second, newvalue.partsecond, newvalue.hasTZ, newvalue.offsetTZ);
    isempty = newvalue.isempty;
  }
    
  public SchemaTime(int newhour, int newminute, int newsecond, double newpartsecond, int newoffsetTZ) {
    setInternalValues(1, 1, 1, newhour, newminute, newsecond, newpartsecond, SchemaCalendarBase.TZ_OFFSET, newoffsetTZ);
    isempty = false;
  }

  public SchemaTime(int newhour, int newminute, int newsecond, double newpartsecond) {
    setInternalValues(1, 1, 1, newhour, newminute, newsecond, newpartsecond, SchemaCalendarBase.TZ_MISSING, 0);
    isempty = false;
  }

  public SchemaTime(Calendar newvalue) {
    setValue( newvalue );
  }

  public SchemaTime(String newvalue) {
    parse(newvalue);
  }

  public SchemaTime(SchemaType newvalue) {
    assign(newvalue);
  }

  public SchemaTime(SchemaTypeCalendar newvalue) {
    assign( (SchemaType)newvalue );
  }

  // getValue, setValue
  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public int getSecond() {
    return second;
  }

  public double getPartSecond() {
    return partsecond;
  }

  public int getMillisecond() {
    return (int)java.lang.Math.round(partsecond*1000);
  }

  public int hasTimezone() {
    return hasTZ;
  }

  public int getTimezoneOffset() {
    if( hasTZ != TZ_OFFSET )
      return 0;
    return offsetTZ;
  }

  public Calendar getValue() {
    Calendar cal = Calendar.getInstance();
    cal.set( Calendar.HOUR_OF_DAY, hour);
    cal.set( Calendar.MINUTE, minute);
    cal.set( Calendar.SECOND, second);
    cal.set( Calendar.MILLISECOND, getMillisecond() );
    hasTZ = TZ_OFFSET; // necessary, because Calendar object always has timezone.
    cal.setTimeZone( (TimeZone)new SimpleTimeZone(offsetTZ * 60000, "") );
    return cal;
  }

  public void setHour(int newhour) {
    hour = newhour;
    isempty = false;
  }

  public void setMinute(int newminute) {
    minute = newminute;
    isempty = false;
  }

  public void setSecond(int newsecond) {
    second = newsecond;
    isempty = false;
  }

  public void setPartSecond(double newpartsecond) {
    partsecond = newpartsecond;
    isempty = false;
  }

  public void setMillisecond(int newmillisecond) {
    partsecond = (double)newmillisecond / 1000;
    isempty = false;
  }

  public void setTimezone(int newhasTZ, int newoffsetTZminutes ) {
    hasTZ = newhasTZ;
    offsetTZ = newoffsetTZminutes;
    isempty = false;
  }

  public void setValue(Calendar newvalue) {
    if(newvalue == null)
      setNull();
    else {
      hour = newvalue.get( Calendar.HOUR_OF_DAY );
      minute = newvalue.get( Calendar.MINUTE );
      second = newvalue.get( Calendar.SECOND );
      setMillisecond( newvalue.get( Calendar.MILLISECOND ) );
      hasTZ = TZ_MISSING;
      isempty = false;
    }
  }

  public void parse(String s) {
	  
	String newvalue = SchemaNormalizedString.normalize(SchemaNormalizedString.WHITESPACE_COLLAPSE, s);

    if( newvalue == null ||  newvalue.length() == 0)
      setEmpty();
    else 
      if (!parseDateTime(newvalue, DateTimePart_Time))
        throw new StringParseException(newvalue + " cannot be converted to a time value", 0);
  }

  public void assign(SchemaType newvalue) {
    if( newvalue == null || newvalue.isNull() || newvalue.isEmpty() )
      setEmpty();
    else if( newvalue instanceof SchemaTime )
      setInternalValues( 1, 1, 1, ((SchemaTime)newvalue).hour, ((SchemaTime)newvalue).minute, ((SchemaTime)newvalue).second, ((SchemaTime)newvalue).partsecond, ((SchemaTime)newvalue).hasTZ, ((SchemaTime)newvalue).offsetTZ );
    else if( newvalue instanceof SchemaDateTime )
      setInternalValues( 1, 1, 1, ((SchemaDateTime)newvalue).hour, ((SchemaDateTime)newvalue).minute, ((SchemaDateTime)newvalue).second, ((SchemaDateTime)newvalue).partsecond, ((SchemaDateTime)newvalue).hasTZ, ((SchemaDateTime)newvalue).offsetTZ );
    else if( newvalue instanceof SchemaString )
      parse( newvalue.toString() );
    else
      throw new TypesIncompatibleException( newvalue, this );
  }

  // further
  public Object clone() {
    return new SchemaTime( this );
  }

  public String toString() {
    if( isempty )
      return "";
    return toTimeString();
  }

  public static SchemaTime now() {
    return new SchemaTime(Calendar.getInstance());
  }

  // ---------- interface SchemaTypeCalendar ----------
  public int calendarType() {
    return CALENDAR_VALUE_TIME;
  }

  public SchemaDateTime dateTimeValue() {
    throw new TypesIncompatibleException(this, new SchemaDateTime("2003-07-28T12:00:00") );
  }

  public SchemaDate dateValue() {
    throw new TypesIncompatibleException(this, new SchemaDate("2003-07-28") );
  }

  public SchemaTime timeValue() {
    return new SchemaTime( this );
  }
}
