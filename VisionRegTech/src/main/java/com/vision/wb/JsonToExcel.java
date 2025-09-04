//package com.vision.wb;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class JsonToExcel {
////	public static void main(String[] args) {
////		String jsonUploadFilePath = "C:\\Users\\Welcome\\Desktop\\users_samp-Copy.json"; 
////		File jsonFile = new File(jsonUploadFilePath);
////		String excelOutputPath = System.getProperty("java.io.tmpdir") + File.separator + "users_samp_converted.xlsx";
////
////		if (!jsonFile.exists()) {
////			System.out.println("JSON file not found.");
////			return;
////		}
////
////		try {
////			ObjectMapper mapper = new ObjectMapper();
////			JsonNode root = mapper.readTree(new FileInputStream(jsonFile));
////
////			if (!root.isArray()) {
////				System.out.println("Root JSON is not an array.");
////				return;
////			}
////
////			Workbook workbook = new XSSFWorkbook();
////			Sheet sheet = workbook.createSheet("Users");
////
////			int rowIndex = 0;
////			List<String> headers = new ArrayList<>();
////			Iterator<JsonNode> elements = root.elements();
////
////			// First object for headers
////			if (elements.hasNext()) {
////				JsonNode firstObj = elements.next();
////				Row headerRow = sheet.createRow(rowIndex++);
////				Iterator<String> fieldNames = firstObj.fieldNames();
////
////				while (fieldNames.hasNext()) {
////					String field = fieldNames.next();
////					headers.add(field);
////					headerRow.createCell(headers.indexOf(field)).setCellValue(field);
////				}
////
////				// Write first data row
////				Row firstDataRow = sheet.createRow(rowIndex++);
////				for (int i = 0; i < headers.size(); i++) {
////					JsonNode value = firstObj.get(headers.get(i));
////					firstDataRow.createCell(i).setCellValue(value == null ? "" : value.asText());
////				}
////			}
////
////			// Remaining rows
////			while (elements.hasNext()) {
////				JsonNode obj = elements.next();
////				Row row = sheet.createRow(rowIndex++);
////				for (int i = 0; i < headers.size(); i++) {
////					JsonNode value = obj.get(headers.get(i));
////					row.createCell(i).setCellValue(value == null ? "" : value.asText());
////				}
////			}
////
////			try (FileOutputStream fos = new FileOutputStream(excelOutputPath)) {
////				workbook.write(fos);
////				workbook.close();
////			}
////
////			System.out.println("✅ Excel file generated: " + excelOutputPath);
////		} catch (Exception e) {
////			e.printStackTrace();
////			System.out.println("❌ Failed to convert JSON to Excel.");
////		}
////	}
//	public static void main(String[] args) {
//		String jsonUploadFilePath = "C:\\Users\\Welcome\\Desktop\\error_codes.json";
//		File jsonFile = new File(jsonUploadFilePath);
//		String excelOutputPath = System.getProperty("java.io.tmpdir") + File.separator + "users_samp_converted.xlsx";
//
//		if (!jsonFile.exists()) {
//			System.out.println("JSON file not found.");
//			return;
//		}
//
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			JsonNode root = mapper.readTree(new FileInputStream(jsonFile));
//
//			// If root is not an array, look for a child array (e.g., "data")
//			JsonNode dataArray;
//			if (root.isArray()) {
//				dataArray = root;
//			} else {
//				// You can change this key based on your structure
//				String arrayKey = "data"; // <-- change if your JSON key is different
//				dataArray = root.get(arrayKey);
//				if (dataArray == null || !dataArray.isArray()) {
//					System.out.println(" JSON is neither an array nor contains an array under key '" + arrayKey + "'.");
//					return;
//				}
//			}
//
//			Workbook workbook = new XSSFWorkbook();
//			Sheet sheet = workbook.createSheet("Users");
//
//			int rowIndex = 0;
//			List<String> headers = new ArrayList<>();
//			Iterator<JsonNode> elements = dataArray.elements();
//
//			while (elements.hasNext()) {
//				JsonNode currentObj = elements.next();
//
//				if (headers.isEmpty()) {
//					// First non-empty object defines headers
//					Iterator<String> fieldNames = currentObj.fieldNames();
//					while (fieldNames.hasNext()) {
//						headers.add(fieldNames.next());
//					}
//
//					if (headers.isEmpty()) {
//						System.out.println("JSON objects are empty. No data to write.");
//						return;
//					}
//
//					// Create header row
//					Row headerRow = sheet.createRow(rowIndex++);
//					for (int i = 0; i < headers.size(); i++) {
//						headerRow.createCell(i).setCellValue(headers.get(i));
//					}
//				}
//
//				// Write data row
//				Row dataRow = sheet.createRow(rowIndex++);
//				for (int i = 0; i < headers.size(); i++) {
//					JsonNode value = currentObj.get(headers.get(i));
//					dataRow.createCell(i).setCellValue(value == null ? "" : value.asText());
//				}
//			}
//
//			// Remaining rows
//			while (elements.hasNext()) {
//				JsonNode obj = elements.next();
//				Row row = sheet.createRow(rowIndex++);
//				for (int i = 0; i < headers.size(); i++) {
//					JsonNode value = obj.get(headers.get(i));
////					row.createCell(i).setCellValue(value == null ? "" : value.asText());
//					String textValue = value == null ? "" : value.asText();
//					if (isDateLike(textValue)) {
//						try {
//							// Try parsing as date-time
//							LocalDateTime date = parseDate(textValue);
//							String formatted = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")); // your desired format
//							row.createCell(i).setCellValue(formatted);
//						} catch (Exception e) {
//							row.createCell(i).setCellValue(textValue); // fallback to original
//						}
//					} else {
//						row.createCell(i).setCellValue(textValue);
//					}
//
//				}
//			}
//
//			try (FileOutputStream fos = new FileOutputStream(excelOutputPath)) {
//				workbook.write(fos);
//				workbook.close();
//			}
//
//			System.out.println(" Excel file generated: " + excelOutputPath);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println(" Failed to convert JSON to Excel.");
//		}
//	}
//	private static boolean isDateLike(String value) {
//		// You can refine this pattern based on your date formats
//		return value.matches("\\d{4}-\\d{2}-\\d{2}.*") || value.matches("\\d{2}/\\d{2}/\\d{4}.*");
//	}
//
//	private static LocalDateTime parseDate(String value) {
//		// Try different patterns as per your input
//		DateTimeFormatter[] formatters = new DateTimeFormatter[] {
//			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
//			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
//			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
//			DateTimeFormatter.ofPattern("dd/MM/yyyy")
//		};
//
//		for (DateTimeFormatter formatter : formatters) {
//			try {
//				return LocalDateTime.parse(value, formatter);
//			} catch (Exception ignored) {
//				// Try next formatter
//			}
//		}
//
//		// If all fail, throw an error
//		throw new IllegalArgumentException("Unrecognized date format: " + value);
//	}
//
//}
package com.vision.wb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToExcel {

	public static void main(String[] args) {
		String jsonUploadFilePath = "C:\\Users\\Welcome\\Desktop\\error_codes.json"; // <-- Change path
		File jsonFile = new File(jsonUploadFilePath);
		String excelOutputPath = System.getProperty("java.io.tmpdir") + File.separator + "converted_excel.xlsx";

		if (!jsonFile.exists()) {
			System.out.println(" JSON file not found.");
			return;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(new FileInputStream(jsonFile));

			// If root is not an array, try getting "data" node
			JsonNode dataArray;
			if (root.isArray()) {
				dataArray = root;
			} else {
				String arrayKey = "data"; // change if your JSON uses a different key
				dataArray = root.get(arrayKey);
				if (dataArray == null || !dataArray.isArray()) {
					System.out.println(" JSON does not contain an array or 'data' field.");
					return;
				}
			}

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Sheet1");

			int rowIndex = 0;
			List<String> headers = new ArrayList<>();
			Iterator<JsonNode> elements = dataArray.elements();

			while (elements.hasNext()) {
				JsonNode currentObj = elements.next();

				// Extract headers
				if (headers.isEmpty()) {
					Iterator<String> fieldNames = currentObj.fieldNames();
					while (fieldNames.hasNext()) {
						headers.add(fieldNames.next());
					}

					// Write header row
					Row headerRow = sheet.createRow(rowIndex++);
					for (int i = 0; i < headers.size(); i++) {
						String normalized = normalizeHeader(headers.get(i));
						headerRow.createCell(i).setCellValue(normalized);
					}
				}

				// Write data row
				Row dataRow = sheet.createRow(rowIndex++);
				for (int i = 0; i < headers.size(); i++) {
					JsonNode valueNode = currentObj.get(headers.get(i));
					String textValue = valueNode == null ? "" : valueNode.asText();

					if (isDateLike(textValue)) {
						try {
							String formattedDate = formatDate(textValue);
							dataRow.createCell(i).setCellValue(formattedDate);
						} catch (Exception e) {
							dataRow.createCell(i).setCellValue(textValue); // fallback
						}
					} else {
						dataRow.createCell(i).setCellValue(textValue);
					}
				}
			}

			try (FileOutputStream fos = new FileOutputStream(excelOutputPath)) {
				workbook.write(fos);
				workbook.close();
			}

			System.out.println(" Excel file generated at: " + excelOutputPath);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" Failed to convert JSON to Excel.");
		}
	}

	// Check if the value looks like a date
	private static boolean isDateLike(String value) {
		return value.matches("\\d{4}-\\d{2}-\\d{2}([T\\s]\\d{2}:\\d{2}(:\\d{2})?)?")
				|| value.matches("\\d{2}/\\d{2}/\\d{4}([\\s]\\d{2}:\\d{2}(:\\d{2})?)?");
	}

	// Try to parse the date string and return formatted value
	private static String formatDate(String value) {
		DateTimeFormatter[] formatters = new DateTimeFormatter[] {
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd/MM/yyyy"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd")
		};

		for (DateTimeFormatter formatter : formatters) {
			try {
				if (formatter.toString().contains("H")) {
					LocalDateTime dt = LocalDateTime.parse(value, formatter);
					return dt.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
				} else {
					LocalDate date = LocalDate.parse(value, formatter);
					return date.atStartOfDay().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
				}
			} catch (DateTimeParseException ignored) {}
		}

		throw new IllegalArgumentException("Unsupported date format: " + value);
	}
	private static String normalizeHeader(String input) {
		// Replace camelCase to underscore: column1Name → column_1_Name
		String withUnderscores = input.replaceAll("([a-z])([A-Z])", "$1_$2");

		// Replace sequences like column1name → column_1_name
		withUnderscores = withUnderscores.replaceAll("(\\d+)([a-zA-Z])", "_$1_$2");
		withUnderscores = withUnderscores.replaceAll("([a-zA-Z])(\\d+)", "$1_$2");

		// Replace multiple underscores with one and trim
		withUnderscores = withUnderscores.replaceAll("_+", "_").replaceAll("^_+|_+$", "");

		// Uppercase the whole thing
		return withUnderscores.toUpperCase();
	}

}
