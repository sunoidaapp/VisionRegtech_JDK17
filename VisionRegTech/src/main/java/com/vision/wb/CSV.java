/*
 * package com.vision.wb;
 * 
 * import java.io.BufferedWriter; import java.io.FileWriter; import
 * java.io.IOException; import java.util.ArrayList; import java.util.HashMap;
 * import java.util.List; import java.util.Map; import java.util.StringJoiner;
 * 
 * public class CSV {
 * 
 * public static void main(String[] args) { // Example ArrayList of Map<String,
 * String>
 * 
 * List<Map<String, String>> list = new ArrayList<>();
 * list.add(createMap("name", "Alice", "age", "30", "city", "Wonderland"));
 * list.add(createMap("name", "Bob", "age", "25", "city", "Springfield"));
 * 
 * // Specify the file paths where you want to save the CSV data String
 * filePathWithDelimiter = "C:\\\\Reg_tech\\\\outputwith_delimiter.csv"; String
 * filePathWithoutDelimiter = "C:\\\\Reg_tech\\\\outputwithout_delimiter.csv";
 * 
 * // Convert and write the CSV data to files try { // With delimiter String
 * delimiter ="";
 * 
 * if(delimiter.equalsIgnoreCase("|")) { String csvStringWithDelimiter =
 * formCsvFromArrayListMap(list, "|"); writeCsvToFile(csvStringWithDelimiter,
 * filePathWithDelimiter); System.out.
 * println("CSV data (with delimiter) has been successfully written to " +
 * filePathWithDelimiter); }else { // Without delimiter String
 * csvStringWithoutDelimiter = formCsvFromArrayListMap(list, ",");
 * writeCsvToFile(csvStringWithoutDelimiter, filePathWithoutDelimiter);
 * System.out.
 * println("CSV data (without delimiter) has been successfully written to " +
 * filePathWithoutDelimiter); } } catch (IOException e) {
 * System.err.println("Error writing CSV to file: " + e.getMessage()); } }
 * 
 * // Utility method for creating a Map
 * 
 * public static Map<String, String> createMap(String... keyValuePairs) { if
 * (keyValuePairs.length % 2 != 0) { throw new
 * IllegalArgumentException("Key-value pairs must be even."); }
 * 
 * Map<String, String> map = new HashMap<>(); for (int i = 0; i <
 * keyValuePairs.length; i += 2) { map.put(keyValuePairs[i], keyValuePairs[i +
 * 1]); } return map; }
 * 
 * 
 * // Utility method for forming CSV from ArrayList of Map with a specified
 * delimiter public static String formCsvFromArrayListMap(List<Map<String,
 * String>> list, String delimiter) { StringJoiner csvJoiner = new
 * StringJoiner("\n");
 * 
 * // Header (assuming all maps have the same keys) if (!list.isEmpty()) {
 * Map<String, String> firstMap = list.get(0); String header =
 * formCsvWithDelimiter(firstMap.keySet(), delimiter); csvJoiner.add(header); }
 * 
 * // Data for (Map<String, String> map : list) { String dataRow =
 * formCsvWithDelimiter(map.values(), delimiter); csvJoiner.add(dataRow); }
 * 
 * return csvJoiner.toString(); }
 * 
 * // Utility method for forming CSV with a specified delimiter public static
 * String formCsvWithDelimiter(Iterable<String> values, String delimiter) {
 * StringJoiner joiner = new StringJoiner(delimiter); for (String value :
 * values) { joiner.add(escapeCsvValue(value)); } return joiner.toString(); }
 * 
 * // Utility method for escaping CSV values private static String
 * escapeCsvValue(String value) { // Implement the logic to escape special
 * characters in the CSV value // For simplicity, this placeholder returns the
 * value as is return value; }
 * 
 * // Utility method for writing CSV data to a file public static void
 * writeCsvToFile(String csvData, String filePath) throws IOException { try
 * (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
 * writer.write(csvData); } }}
 */