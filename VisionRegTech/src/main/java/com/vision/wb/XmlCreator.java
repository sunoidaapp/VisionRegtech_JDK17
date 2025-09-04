package com.vision.wb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringJoiner;

public class XmlCreator {
public static void main(String[] args) {
	  
			try{
				//step1 load the driver class  
//				Class.forName("oracle.jdbc.driver.OracleDriver");  
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				
				//step2 create  the connection object  
//				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.16.1.106:1521:VISDB", "VISIONGDI","vision123");
				
				Connection con = DriverManager.getConnection("jdbc:sqlserver://10.16.1.38;instance=VISIONBISQL2019;port=52866;DatabaseName=VISION_GDI;encrypt=false;trustServerCertificate=false",
						"Vision","Vision_RA");
				  

				//step3 create the statement object  
				System.out.println("1");
				Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				System.out.println("2");
				String tableName = "CB_PARTICULAR_DIR_INFO";
				
				System.out.println( "TableName : "+tableName);
				
				   DatabaseMetaData metaData = con.getMetaData();
//					String query = "Select COLUMN_NAME   from USER_IND_COLUMNS Where UPPER(TABLE_NAME) =UPPER('"+tableName+"')"
//							+ "					 And INDEX_NAME = (Select RTrim(INDEX_NAME) INDEX_NAME From USER_INDEXES Where UPPER(TABLE_NAME) =UPPER('"+tableName+"')"
//							+ "                     And (INDEX_NAME Like '%_PK' ) AND ROWNUM = 1 )";
					
					String query =" SELECT COLUMN_NAME"
					+" FROM sys.index_columns ic"
					+" JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id"
					+" JOIN sys.indexes i ON ic.object_id = i.object_id AND ic.index_id = i.index_id"
					+" WHERE OBJECT_NAME(ic.object_id) = "+tableName
					+"   AND i.name = ("
					+"       SELECT TOP 1 LTRIM(RTRIM(name))"
					+"       FROM sys.indexes"
					+"       WHERE OBJECT_NAME(object_id) = "+tableName
					+"         AND name LIKE '%_PK'";
					  
					ResultSet rs = stmt.executeQuery(query);
					  ArrayList<String> primaryKeys = new ArrayList<>();
			            
			            while (rs.next()) {
			            	
			            	String dbColumnName = rs.getString("COLUMN_NAME");
			                primaryKeys.add(rs.getString("COLUMN_NAME"));
			            }
			      rs = metaData.getColumns(null, null, tableName,null);
				StringJoiner stringJoiner = new StringJoiner(" \n");
				StringJoiner promptJoiner = new StringJoiner(" \n");
				stringJoiner.add("<COLUMNS>");
				stringJoiner.add("<COLUMN_COUNT>#</COLUMN_COUNT>");
				int i =1;
				int j= 0;
				int precision = 0;
				boolean flag=true;
				while (rs.next()) {
					j++;
					String dbColumnName = rs.getString("COLUMN_NAME");
					String columnType = rs.getString("TYPE_NAME");
					 int columnSize = rs.getInt("COLUMN_SIZE");
					 if ("NUMBER".equalsIgnoreCase(columnType)) {
		                    precision = rs.getInt("DECIMAL_DIGITS");
		                    //System.out.print(" " + "Precision:"+ precision);// Fetch precision
		                }
					if(columnType.equalsIgnoreCase("VARCHAR2")||columnType.equalsIgnoreCase("VARCHAR"))
						columnType ="T";
					else if(columnType.equalsIgnoreCase("NUMERIC")
							||columnType.equalsIgnoreCase("NUMBER")
							||columnType.equalsIgnoreCase("INT"))
					columnType ="N";
					else if(columnType.equalsIgnoreCase("DATE")
							||columnType.equalsIgnoreCase("DATETIME")
							||columnType.equalsIgnoreCase("DATETIME2"))
					columnType ="D";
						stringJoiner.add("\t<COLUMN"+j+">");
						stringJoiner.add("\t\t<LABLE_ROW_NUM>"+i+"</LABLE_ROW_NUM>");
						stringJoiner.add("\t\t<LABLE_COL_NUM>"+j+"</LABLE_COL_NUM>");
						stringJoiner.add("\t\t<CAPTION>"+convertToReadableFormat(dbColumnName)+"</CAPTION>");
						stringJoiner.add("\t\t<COL_TYPE>"+columnType+"</COL_TYPE>");
						stringJoiner.add("\t\t<ROW_SPAN>"+"0"+"</ROW_SPAN>");
						stringJoiner.add("\t\t<COL_SPAN>"+"0"+"</COL_SPAN>");
						stringJoiner.add("\t\t<SOURCE_COLUMN>"+dbColumnName+"</SOURCE_COLUMN>");
						stringJoiner.add("\t\t<DRILLDOWN_LABEL_FLAG>"+"N"+"</DRILLDOWN_LABEL_FLAG>");
						stringJoiner.add("\t\t<SCALING>"+"</SCALING>");
						stringJoiner.add("\t\t<DECIMALCNT>"+precision+"</DECIMALCNT>");
						stringJoiner.add("\t\t<COLUMN_WIDTH>"+columnSize+"</COLUMN_WIDTH>");
						stringJoiner.add("\t\t<GROUPING_FLAG>"+"N"+"</GROUPING_FLAG>");
						if(("'RECORD_INDICATOR_NT', 'RECORD_INDICATOR', 'MAKER', 'VERIFIER', 'INTERNAL_STATUS', 'DATE_LAST_MODIFIED','DATE_CREATION'".contains(dbColumnName))
								|| primaryKeys.contains(dbColumnName)){
						stringJoiner.add("\t\t<READ_ONLY>"+"Y"+"</READ_ONLY>");
						if(!primaryKeys.contains(dbColumnName))
						stringJoiner.add("\t\t<SHOW_VALUES>"+"N"+"</SHOW_VALUES>");
						else
							stringJoiner.add("\t\t<SHOW_VALUES>"+"Y"+"</SHOW_VALUES>");
						}else {
							stringJoiner.add("\t\t<READ_ONLY>"+"N"+"</READ_ONLY>");
							stringJoiner.add("\t\t<SHOW_VALUES>"+"Y"+"</SHOW_VALUES>");
						}
						stringJoiner.add("\t</COLUMN"+j+">");
					
				}
				stringJoiner.add("</COLUMNS>");
				String f= stringJoiner.toString().replaceAll("#", Integer.toString(j));
				System.out.println(f);
				rs.close();
//				tableName = tableName+"_PK";
				System.out.println("\n\nTable NAme:"+ tableName);
				System.out.println("Prompts Start");
				i=1;
				j=0;
//				rs = metaData.getPrimaryKeys(null, null, tableName);
				
//				rs =metaData.getIndexInfo(null, null,tableName , true, false);
			
				 promptJoiner.add("<Prompts>");
					promptJoiner.add("<PromptCount>#</PromptCount>");
	          
	            primaryKeys.add("ERROR_CODE");
	            if(!primaryKeys.isEmpty()) {
	            	for(String column: primaryKeys) {
	            		j++;
	            		 promptJoiner.add("\t<Prompt"+j+">");
	 	            	promptJoiner.add("\t\t<Sequence>"+j+"</Sequence>");
	 					promptJoiner.add("\t\t<Label>"+convertToReadableFormat(column)+"</Label>");
	 					promptJoiner.add("\t\t<SourceId>SOURCE_FILTER</SourceId>");
	 					promptJoiner.add("\t\t<Type>COMBO</Type>");
	 					promptJoiner.add("\t\t<DefaultValue></DefaultValue>");
	 					promptJoiner.add("\t\t<DependencyFlag>N</DependencyFlag>");
	 					promptJoiner.add("\t\t<DependentPrompt />");
	 					promptJoiner.add("\t\t</DateFormat>");
	 					promptJoiner.add("\t\t<FilterRow>"+j+"</FilterRow>");
	 					promptJoiner.add("\t\t<MultiSelect />");
	 					promptJoiner.add("\t\t<ExceptionFilter>"+column+"</ExceptionFilter>");
	 					promptJoiner.add("\t\t</DateRestrict>");
	 					promptJoiner.add("\t\t<isMandatory>N</isMandatory>");
	 				    promptJoiner.add("\t</Prompt"+j+">");
	            	}
	            }
	            promptJoiner.add("</Prompts>");
				String f1= promptJoiner.toString().replaceAll("#", Integer.toString(j));
				System.out.println(f1);
	            primaryKeys.stream().forEach(System.out::println);
				
	           
			
				//rsIndex.close();
				con.close();  
				  
			}catch(Exception e){ 
				System.out.println(e);
			}  
		
}
	private static String convertToReadableFormat(String columnName) {
        StringBuilder readableName = new StringBuilder();
        boolean nextUpperCase = true;

        for (char c : columnName.toCharArray()) {
            if (c == '_') {
                readableName.append(' ');
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    readableName.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    readableName.append(Character.toLowerCase(c));
                }
            }
        }

        return readableName.toString();
    }
	
}
//public class XmlCreator {
//    public static Map<String, Object> processResultSet(ResultSet rs) throws SQLException {
//        ResultSetMetaData metaData = rs.getMetaData();
//        int columnCount = metaData.getColumnCount();
//        Map<String, Object> row = new HashMap<>(columnCount);
//
//        for (int i = 1; i <= columnCount; i++) {
//            String columnName = metaData.getColumnName(i);
//            Object columnValue = rs.getObject(i);
//            int columnType = metaData.getColumnType(i);
//
//            if (columnValue instanceof Timestamp) {
//                Timestamp timestamp = (Timestamp) columnValue;
//                String timestampString = timestamp.toString();
//
//                // Check if the timestamp contains time information
//                if (timestampString.contains(" ")) {
//                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
//                    try {
//                        Date date = inputFormat.parse(timestampString);
//                        // Format the Date to the desired output format
//                        columnValue = outputFormat.format(date);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else if (columnType == java.sql.Types.NUMERIC) {
//                if (columnValue != null) {
//                    BigDecimal bigDecimalValue = new BigDecimal(columnValue.toString());
//                    columnValue = bigDecimalValue.toPlainString();
//                }
//                
//                // Check precision and scale for NUMERIC type
//                int precision = metaData.getPrecision(i);
//                int scale = metaData.getScale(i);
//
//                System.out.println("Column: " + columnName);
//                System.out.println("Data Type: NUMERIC");
//                System.out.println("Precision: " + precision);
//                System.out.println("Scale: " + scale);
//            } else if (columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.BIGINT
//                    || columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE
//                    || columnType == java.sql.Types.DECIMAL) {
//                if (columnValue != null) {
//                    columnValue = columnValue.toString();
//                }
//            }
//
//            row.put(columnName, columnValue);
//        }
//
//        return row;
//    }
//    public static void main(String[] args) {
//        // Example usage
//        // Ensure you have a valid connection and ResultSet
//        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@10.16.1.101:1521:VISIONBI", "VISIONGDI","vision123");
//             Statement statement = connection.createStatement();
//             ResultSet rs = statement.executeQuery("SELECT * FROM CB_WEEKLY_CASH_HOLDING")) {
//
//            while (rs.next()) {
//                Map<String, Object> row = processResultSet(rs);
//                // Do something with the row
//                System.out.println(row);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

