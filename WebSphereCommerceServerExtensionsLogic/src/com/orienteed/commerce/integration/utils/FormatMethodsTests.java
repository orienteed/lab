package com.orienteed.commerce.integration.utils;

import java.time.Instant;

public class FormatMethodsTests {

	public static void main(String[] args) {

		System.out.println(withOneDayHour());
		System.out.println(withDays());
		System.out.println(withDaysHours());
		System.out.println(withHoursMinutes());
		System.out.println(withSecondMillis());
		System.out.println(withMillis());
		
		System.out.println(FormatMethods.millisToString(Instant.now().toEpochMilli() - 1534261955775l));
		
	}
	
	private static boolean withOneDayHour(){
		Long millis = 103680000l;
		return "1 day 4 hr ".equals(FormatMethods.millisToString(millis));
	}
	
	private static boolean withDays() {
		Long millis = 2160000000l;
		return "25 days ".equals(FormatMethods.millisToString(millis));
	}
	private static boolean withDaysHours() {
		long millis = 1554196559827l;
		String formatted = FormatMethods.millisToString(millis);
		return "17988 days 9 hr ".equals(formatted);
	}
	
	private static boolean withHoursMinutes() {
		long millis = 10234232l;
		return "2 hr 50 min ".equals(FormatMethods.millisToString(millis));
	}
	
	private static boolean withSecondMillis() {
		long millis = 4353l;
		return "4 sec 353 ms".equals(FormatMethods.millisToString(millis));
	}
	
	private static boolean withMillis() {
		long millis = 321l;
		return "321 ms".equals(FormatMethods.millisToString(millis));
	}


}
