package com.enezaeducation.mwalimu;

public class Constants {
	public final static boolean DEBUG = true;
	
	public final static boolean SKIP_LOGIN = false;
	
	public static final int HTTP_TIMEOUT = 30 * 1000; // 30 seconds
	
	public static final String BASE_URL = "http://54.220.201.194/api/";
	
	public static final String LOGIN_URL = BASE_URL + "authenticate";
	
	public static final String REGISTRATION_URL = BASE_URL + "users";
	
	public static final String SCHOOLS_URL = BASE_URL + "schools";
	
	public static final String SCHOOLCHART_URL = BASE_URL + "schools/averagescores";
	
	public static final String STUDENTCHART_URL = BASE_URL + "users/averagegrades";
}
