package com.lesserhydra.util;

public class MathUtil {
	
	public static int clamp(int value, int min, int max) {
		return Math.min(Math.max(min, value), max);
	}
	
	public static long clamp(long value, long min, long max) {
		return Math.min(Math.max(min, value), max);
	}
	
	public static float clamp(float value, float min, float max) {
		return Math.min(Math.max(min, value), max);
	}
	
	public static double clamp(double value, double min, double max) {
		return Math.min(Math.max(min, value), max);
	}
	
}
