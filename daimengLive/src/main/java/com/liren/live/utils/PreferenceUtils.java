package com.liren.live.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * SharedPreferences 工具类
 * 
 * @author rancq
 *
 * @time  2015年3月20日
 *
 */
public class PreferenceUtils {

	private static String PREFERENCE_NAME = "USER_INFO";

	private static PreferenceUtils preferenceUtil;

	private static SharedPreferences sp;

	private static Editor ed;

	private PreferenceUtils(Context context) {
		init(context);
	}

	public static void init(Context context) {
		if (sp == null || ed == null) {
			try {
				sp = context.getSharedPreferences(PREFERENCE_NAME, 0);
				ed = sp.edit();
			} catch (Exception e) {
			}
		}
	}

	public static PreferenceUtils getInstance(Context context) {
		if (preferenceUtil == null) {
			preferenceUtil = new PreferenceUtils(context);
		}
		return preferenceUtil;
	}

	public void saveLong(String key, long l) {
		ed.putLong(key, l);
		ed.commit();
	}

	public long getLong(String key, long defaultlong) {
		return sp.getLong(key, defaultlong);
	}
	
	public long getLong(String key) {
		return sp.getLong(key, -1l);
	}

	public void saveFloat(String key, float f){
		ed.putFloat(key, f);
		ed.commit();
	}
	
	public float getFloat(String key, float f){
		return sp.getFloat(key, f);
	}
	
	public float getFloat(String key){
		return sp.getFloat(key, -1f);
	}
	
	public void saveBoolean(String key, boolean value) {
		ed.putBoolean(key, value);
		ed.commit();
	}

	public boolean getBoolean(String key, boolean defaultboolean) {
		return sp.getBoolean(key, defaultboolean);
	}
	
	public boolean getBoolean(String key) {
		return sp.getBoolean(key, false);
	}

	public void saveInt(String key, int value) {
		if (ed != null) {
			ed.putInt(key, value);
			ed.commit();
		}
	}

	public int getInt(String key, int defaultInt) {
		return sp.getInt(key, defaultInt);
	}
	
	public int getInt(String key) {
		return sp.getInt(key, -1);
	}

	public String getString(String key, String defaultString) {
		return sp.getString(key, defaultString);
	}
	
	public String getString(String key){
		return sp.getString(key, null);
	}

	/**error*/
	public String getString(Context context, String key, String defaultValue) {
		if (sp == null || ed == null) {
			sp = context.getSharedPreferences(PREFERENCE_NAME, 0);
			ed = sp.edit();
		}
		if (sp != null) {
			return sp.getString(key, defaultValue);
		}
		return defaultValue;
	}

	public void saveString(String key, String value) {
		ed.putString(key, value);
		ed.commit();
	}

	public void remove(String key) {
		ed.remove(key);
		ed.commit();
	}
	
	public boolean hasKey(String key){
		return sp.contains(key);
	}

	public boolean clear(Context context, String pr_name){
		File file= new File("/data/data/"+context.getPackageName().toString()+"/shared_prefs",pr_name+".xml");
		if(file.exists()){
			return file.delete();
		}
		return false;
	}
	
	public void destroy() {
		sp = null;
		ed = null;
		preferenceUtil = null;
	}
	public void saveSet(String key,Set<String> set){
		ed.putStringSet(key,set);
		ed.commit();
	}

	public Set<String> getSet(String key){
		return sp.getStringSet(key,new HashSet<String>());
	}

}
