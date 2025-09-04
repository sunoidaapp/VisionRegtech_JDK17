package com.vision.dao;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class sqlQueryBuilder {

    public static String buildSelectQuery(String tableName, Map<String, Object> columns) {
        StringBuilder query = new StringBuilder("SELECT ");
        
        if (columns != null && !columns.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            for (String column : columns.keySet()) {
                joiner.add(column);
            }
            query.append(joiner.toString());
        } else {
            query.append("*");
        }
        
        query.append(" FROM ").append(tableName);

        return query.toString();
    }

//    public static String buildInsertQuery(String tableName, Map<String, Object> columns) {
//        StringBuilder query = new StringBuilder("INSERT INTO ");
//        query.append(tableName).append(" (");
//
//        StringJoiner columnNames = new StringJoiner(", ");
//        StringJoiner columnValues = new StringJoiner(", ");
//
//        for (Map.Entry<String, Object> entry : columns.entrySet()) {
//            columnNames.add(entry.getKey());
//            columnValues.add("'" + entry.getValue().toString() + "'");
//        }
//
//        query.append(columnNames.toString()).append(") VALUES (");
//        query.append(columnValues.toString()).append(")");
//
//        return query.toString();
//    }

    
    public static String buildInsertQuery(String tableName, Map<String, Object> columns) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName).append(" (");

        StringJoiner columnNames = new StringJoiner(", ");

        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            columnNames.add(entry.getKey());
        }

        query.append(columnNames.toString()).append(") VALUES (");

        return query.toString();
    }

    
    public static void main(String[] args) {
        // Example map
        Map<String, Object> columns = new HashMap<>();
        columns.put("ACCOUNT_NAME", "SAMUEL");
        columns.put("ACCOUNT_OWNERSHIP_TYPE", "I3");
        columns.put("ACCOUNT_STATUS", "0");
        columns.put("ACCOUNT_TYPE", "CAA");
        columns.put("ACC_CLOSING_DATE", "09-Feb-2021");
        columns.put("ACC_CONTRACT_NUMBER", "01A002035467801");
        columns.put("ACC_OPENING_DATE", "09-Feb-2018");
        columns.put("ACC_STATUS_CHANGE_DATE", "10-Aug-2019");
        columns.put("ANTICIPATED_MATURITY_PERIOD", "30D");
        columns.put("BANK_CODE", "0000001");
        columns.put("BRANCH_ID", "003");
        columns.put("BUSINESS_ACTIVITY_CODE", "CH001");
        columns.put("CHART_OF_ACCOUNTS_CODE", "AC1");
        columns.put("CREDIT_INTEREST_RATE", 6);
        columns.put("CURRENCY_CODE", "KES");
        columns.put("CUSTOMER_ID", "0354678");
        columns.put("DATE_CREATION", "03-Jun-2020");
        columns.put("DATE_LAST_MODIFIED", "03-Jun-2020");
        columns.put("DEBIT_INTEREST_RATE", 7);
        columns.put("EXPIRY_DATE", "10-Aug-2019");
        columns.put("GL_CODE", "120009");
        columns.put("INTERNAL_STATUS", 0);
        columns.put("MAKER", 2426);
        columns.put("NUM", 1);
        columns.put("PRODUCT_TYPE", "CA002");
        columns.put("RECORD_INDICATOR", 0);
        columns.put("RECORD_INDICATOR_NT", 7);
        columns.put("REPORTING_DATE", "03-Jun-2020");
        columns.put("RN", 1);
        columns.put("ROW_ID", 1);
        columns.put("SANCTIONED_ACC_LIMIT_FCY", 100000);
        columns.put("SANCTIONED_ACC_LIMIT_LCY", 80000);
        columns.put("VERIFIER", 1005);
        columns.put("VERSION_NO", 1);

        String tableName = "your_table_name"; // Replace with your actual table name
        String selectQuery = buildSelectQuery(tableName, columns);
        String insertQuery = buildInsertQuery(tableName, columns);

        System.out.println("SELECT Query:");
        System.out.println(selectQuery);
        System.out.println("\nINSERT Query:");
        System.out.println(insertQuery);
    }
}

