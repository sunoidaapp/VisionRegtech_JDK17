//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//
//public class Sample {
//	public static void main(String[] args) throws JsonProcessingException {
//		Connection connection = null;
//		Statement statement = null;
//		ResultSet resultSet = null;
//
//		try {
//			// Connect to the database
//			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.16.1.101:1521:VISIONBI", "devuser",
//					"vision123");
//			if (connection != null) {
//				System.out.println("DB Connected.....");
//				// Create a statement
//				statement = connection.createStatement();
//				if(statement != null) {
//				// Execute a query
//					System.out.println("Resultset ..............");
//				resultSet = statement.executeQuery("SELECT Vision_id FROM VISION_USERS");
//
//				// Convert ResultSet to JSON
//				if (resultSet != null) {
//					List<SampleEntity> ls = new ArrayList<>();
//					List <SampleEntity> dataList = new ArrayList<>();
//		            while (resultSet.next()) {
//		                // Here you can create objects or use maps to represent rows
//		                // For simplicity, let's assume we have a single column in the source table
//		            	SampleEntity sampleEntity= new SampleEntity();
//		            	sampleEntity.setName(resultSet.getString(1));
//		                dataList.add(sampleEntity); // Adjust for your actual data type
//		            }
//					System.out.println("JSON Convertion ........");
//					 Gson gson = new Gson();
//			            String jsonData = gson.toJson(dataList);
//			            List<String> d1 = new ArrayList<>();
//			            if(dataList != null) {
//			            	for(SampleEntity str: dataList) {
//			            		if(str.equals("2426")) {
//			            			resultSet = statement.executeQuery("select reference_no from AUDIT_TRAIL_DATA where maker = 2426 ");
//			            			if (resultSet != null) {
//			            				
//			            				d1.add(resultSet.getString(1));
//			            			}
//			            			if(d1 != null && d1.size() > 0) {
//			            				str.setLst(d1);
//			            			}
//			            		}
//			            	}
//			            }
//					// Print the JSON data
//			            ObjectMapper objectMapper = new ObjectMapper();
//			            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData);
//			            
//					System.out.println(jsonData);
//					System.out.println("__________________::::::::::::::_____________________");
//					System.out.println(json);
//				}
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				// Close the resources
//				if (resultSet != null)
//					resultSet.close();
//				if (statement != null)
//					statement.close();
//				if (connection != null)
//					connection.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//}
//	class SampleEntity {
//
//
//	    private String name;
//	    List<String> lst = new ArrayList<>();
//		public String getName() {
//			return name;
//		}
//		public void setName(String name) {
//			this.name = name;
//		}
//		public List<String> getLst() {
//			return lst;
//		}
//		public void setLst(List<String> lst) {
//			this.lst = lst;
//		}
//
//	}
//
//
