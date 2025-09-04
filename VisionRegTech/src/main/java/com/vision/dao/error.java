package com.vision.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class error {

    public static List<Map<String, Object>> errorLst(
        List<Map<String, Object>> l1,
        List<Map<String, Object>> l2,
        String rowIdKey,
        String errorColumnName
    ) {
    	Map<Object, List<Map<String, Object>>> errorMap = l2.stream()
    		    .collect(Collectors.groupingBy(errorRow -> errorRow.get(rowIdKey)));

    		// Update l1 to include the error list
    		for (Map<String, Object> dataRow : l1) {
    		    Object rowId = dataRow.get(rowIdKey);

    		    // Add the errors as a list under the new error column directly in l1
    		    List<Map<String, Object>> errorsForRow = errorMap.getOrDefault(rowId, Collections.emptyList());
    		    dataRow.put(errorColumnName, errorsForRow);
    		}
    		return l1;
    }

    public static void main(String[] args) {
        // Example data for l1
        List<Map<String, Object>> l1 = new ArrayList<>();
        Map<String, Object> l1Row1 = new HashMap<>();
        l1Row1.put("rowid", 1);
        l1Row1.put("name", "Alice");
        l1.add(l1Row1);

        Map<String, Object> l1Row2 = new HashMap<>();
        l1Row2.put("rowid", 2);
        l1Row2.put("name", "Bob");
        l1.add(l1Row2);

        // Example data for l2
        List<Map<String, Object>> l2 = new ArrayList<>();
        Map<String, Object> l2Row1 = new HashMap<>();
        l2Row1.put("rowid", 1);
        l2Row1.put("error", "Invalid name format");
        l2.add(l2Row1);

        Map<String, Object> l2Row2 = new HashMap<>();
        l2Row2.put("rowid", 1);
        l2Row2.put("error", "Missing last name");
        l2.add(l2Row2);

        Map<String, Object> l2Row3 = new HashMap<>();
        l2Row3.put("rowid", 3);
        l2Row3.put("error", "Row not found");
        l2.add(l2Row3);

        // Combine l1 and l2
        List<Map<String, Object>> l3 = errorLst(l1, l2, "rowid", "zerrorLst");

        // Print the combined list
        for (Map<String, Object> row : l3) {
            System.out.println(row);
        }
    }
}

