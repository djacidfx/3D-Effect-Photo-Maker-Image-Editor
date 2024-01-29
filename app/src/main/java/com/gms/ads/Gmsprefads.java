package com.gms.ads;

import android.content.Context;

public class Gmsprefads {

	private static String SPLASHCOUNT = "splashcount";

	public static void setsplashcount(Context Context, int Int) {
		Context.getSharedPreferences(Context.getPackageName(), 0).edit()
				.putInt(SPLASHCOUNT, Int).commit();
	}

	public static int getsplashcount(Context Context) {
		return Context.getSharedPreferences(Context.getPackageName(), 0)
				.getInt(SPLASHCOUNT, 0);
	}

	private static String COUNT = "count";

	public static void setcount(Context Context, int Int) {
		Context.getSharedPreferences(Context.getPackageName(), 0).edit()
				.putInt(COUNT, Int).commit();
	}

	public static int getcount(Context Context) {
		return Context.getSharedPreferences(Context.getPackageName(), 0)
				.getInt(COUNT, 1);
	}

	private static String BANNER = "banner";

	public static void setbanner(Context Context, int Int) {
		Context.getSharedPreferences(Context.getPackageName(), 0).edit()
				.putInt(BANNER, Int).commit();
	}

	public static int getbanner(Context Context) {
		return Context.getSharedPreferences(Context.getPackageName(), 0)
				.getInt(BANNER, 0);
	}

	private static String INTER = "inter";

	public static void setinter(Context Context, int Int) {
		Context.getSharedPreferences(Context.getPackageName(), 0).edit()
				.putInt(INTER, Int).commit();
	}

	public static int getinter(Context Context) {
		return Context.getSharedPreferences(Context.getPackageName(), 0)
				.getInt(INTER, 0);
	}

}