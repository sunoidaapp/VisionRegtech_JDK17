package com.vision.wb;

import java.util.HashMap;
import java.util.Map;

public class Mapformation {
	public static void main(String[] args) {
		// Example maps
		Map<String, String> map1 = new HashMap<>();
		map1.put("key1", "value1_map1");
		map1.put("key2", "value2_map1");
		map1.put("key3", "value3_map1");

		Map<String, String> map2 = new HashMap<>();
		map2.put("key2", "value2_map2");
		map2.put("key3", "value3_map2");
		map2.put("key4", "value4_map2");

		// Find and print common keys and their values
		for (String key : map1.keySet()) {
			if (map2.containsKey(key)) {
				System.out.println("Common key: " + key);
				System.out.println("Value in map1: " + map1.get(key));
				System.out.println("Value in map2: " + map2.get(key));
				System.out.println();
			}
		}
	}
}
