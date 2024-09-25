package com.bot.theechoesbot.core;

import java.util.HashMap;
import java.util.Map;

public class Cache{

	public static class Event{

		private final static Map<String, String> data = new HashMap<>();

		public static void put(String key, String value){
			data.put(key, value);
		}

		public static String get(String key){
			return data.get(key);
		}

		public static String getAndRemove(String key){
			return data.remove(key);
		}

	}

}
