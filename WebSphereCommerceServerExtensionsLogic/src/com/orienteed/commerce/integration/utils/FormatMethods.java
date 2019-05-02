package com.orienteed.commerce.integration.utils;

public class FormatMethods {

	private final static int MILLIS_IN_SECOND = 1000;
	private final static int MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60;
	private final static int MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
	private final static int MILLIS_IN_DAY = MILLIS_IN_HOUR * 24; 
	public static String millisToString(long millis) {
		if (millis < 0) {
            throw new IllegalArgumentException("Duration cannot be less than zero!");
		}
		
		String toReturn = "";
		int counter = 0;
		int limit = 2;
		
		if (counter < limit && millis >= MILLIS_IN_DAY) {
			long days = millis / MILLIS_IN_DAY;
			String word = "days";
			if (days == 1l) {
				word = "day";
			}
			toReturn += String.format("%d %s ", days, word);
			millis = millis % MILLIS_IN_DAY;
			counter++;
		}
		if (counter < limit && MILLIS_IN_DAY > millis && millis >= MILLIS_IN_HOUR) {
			long hours = millis / MILLIS_IN_HOUR;
			toReturn += String.format("%d hr ", hours);
			millis = millis % MILLIS_IN_HOUR;
			counter++;
		}
		if (counter < limit && MILLIS_IN_HOUR > millis && millis >= MILLIS_IN_MINUTE) {
			long minutes = millis / MILLIS_IN_MINUTE;
			toReturn += String.format("%d min ", minutes);
			millis = millis % MILLIS_IN_MINUTE;
			counter++;
		}
		if (counter < limit && MILLIS_IN_MINUTE > millis && millis >= MILLIS_IN_SECOND) {
			long seconds = millis / MILLIS_IN_SECOND;
			toReturn += String.format("%d sec ", seconds);
			millis = millis % MILLIS_IN_SECOND;
			counter++;
		}
		if (counter < limit && MILLIS_IN_SECOND > millis && millis > 0) {
			toReturn += String.format("%d ms", millis);
			counter++;
		}
		return toReturn;
	}
}
