//package com.vision.controller;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.text.SimpleDateFormat;
//import java.util.Base64;
//import java.util.Date;
//import java.util.Map;
//
//import org.apache.hc.client5.http.classic.methods.HttpPost;
//import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
//import org.apache.hc.client5.http.entity.mime.StringBody;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.core5.http.io.entity.EntityUtils;
//import org.apache.hc.core5.http.io.entity.StringEntity;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vision.dao.CommonDao;
//import com.vision.exception.ExceptionCode;
//import com.vision.util.Constants;
//import com.vision.vb.TemplateConfigVb;
//
//public class OAuth2Client {
//
//	@Autowired
//	CommonDao CommonDao;
//	
//	public static ExceptionCode getAuthenticationCode() {
//		String TOKEN_URL = "https://gdi.centralbank.go.ke/oauth2/v1/token";
//		String CLIENT_ID = "1ba2983dc59f44b693f774a54945e706";
//		String CLIENT_SECRET = "8d6665f3-9385-4684-95f8-71b9dc6b3ab2";
//		String GRANT_TYPE = "client_credentials"; // or another grant type
//		String SCOPE = "https://8C80E3C8CFCF45EBB272C5478F767122.ke1.s7071808.oraclecloudatcustomer.com:443urn:opc:resource:consumer::all"; // optional,
//																																			// depends
//																																			// on
//																																			// your
//																																			// server
//		ExceptionCode exceptionCode = new ExceptionCode();
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//			// Original string
//			String originalString = "Bala Satya Prakash";
//
//			// Encode to Base64
//			String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
//			// System.out.println("Encoded string: " + encodedString);
//
//			// String encodedString = "QmFsYSBTYXR5YSBQcmFrYXNo";
//
//			// Decode from Base64
//			byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
//			String decodedString = new String(decodedBytes);
//			// System.out.println("Decoded string: " + decodedString);
//
//			// Create HttpPost request
//			HttpPost httpPost = new HttpPost(TOKEN_URL);
//			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//
//			// Prepare form data
//			StringBuilder formData = new StringBuilder();
//			formData.append("client_id=").append(CLIENT_ID).append("&client_secret=").append(CLIENT_SECRET)
//					.append("&grant_type=").append(GRANT_TYPE).append("&scope=").append(SCOPE);
//
//			// Set request body
//			StringEntity entity = new StringEntity(formData.toString());
//
//			httpPost.setEntity(entity);
//
//			// Execute request
//			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//				// Check response status code
//				int statusCode = response.getStatusLine().getStatusCode();
//				if (statusCode == 200) {
//					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
//					// Parse JSON response
//					String responseBody = EntityUtils.toString(response.getEntity());
//					ObjectMapper objectMapper = new ObjectMapper();
//					Map<String, Object> responseData = objectMapper.readValue(responseBody, Map.class);
//
//					String access_token = (String) responseData.get("access_token");
//					String token_type = (String) responseData.get("token_type");
//					int expires_in = (int) responseData.get("expires_in");
////					System.out.println("Access Token: " + access_token);
////					System.out.println("Token Type: " + token_type);
////					System.out.println("Expires in: " + expires_in);
//					exceptionCode.setResponse(access_token);
//					exceptionCode.setErrorMsg(responseBody);
//				} else {
//					System.out.println("Failed to get access token. Response code: " + statusCode);
//					String responseBody = EntityUtils.toString(response.getEntity());
//					System.out.println("Response body: " + responseBody);
//					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//					exceptionCode.setErrorMsg(responseBody);
//				}
//			}
//		} catch (Exception e) {
////			e.printStackTrace();
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//		}
//
//		return exceptionCode;
//	}
//
//	public static ExceptionCode postData(String institutionCode, String requestId, String isFIleAttached,
//			String reportingDate, ExceptionCode exceptionCodeNew, Object authToken) {
//		TemplateConfigVb templateConfigVb = (TemplateConfigVb) exceptionCodeNew.getOtherInfo();
//		
//		String TOKEN_URL = templateConfigVb.getApiConnectivityDetails();
//		String dataList = templateConfigVb.getDataList().trim();
//		ExceptionCode exceptionCode = new ExceptionCode();
//
//		String logPath = (String) exceptionCodeNew.getCsvPath();
//		String fileName = (String) exceptionCodeNew.getCsvFileName();
//		String fileExtension = ".log"; //
//		 String  responseBody = "";
//		 String requestNo = "";
//		 String jsonFilePath = (String) exceptionCodeNew.getCsvPath();
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//
//			// Create HttpPost request
//			HttpPost httpPost = new HttpPost(TOKEN_URL);
//			httpPost.setHeader("Content-Type", "multipart/mixed; boundary=boundary123");
//			httpPost.setHeader("Authorization", "Bearer " + authToken);
//
//			JSONArray datasArray = new JSONArray((String) exceptionCodeNew.getResponse());
//			// Prepare form data
//			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//			SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
//			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
//			Date date1 = inputFormat.parse(reportingDate);
//
//			// Format the Date object into the desired output format
//			String formattedDate = date1 != null ? outputFormat.format(date1) : "";
//			System.out.println(formattedDate);
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("INSTITUTION_CODE", institutionCode);
//			jsonObject.put("REQUEST_ID", requestId);
//			jsonObject.put("IS_ATTACHED", isFIleAttached);
//			jsonObject.put("REPORTING_DATE", formattedDate);
//			jsonObject.put(dataList, datasArray);
//			System.out.println(jsonObject.toString());
//			// Convert the JSON object to StringBody
//			StringBody jsonDataPart = new StringBody(jsonObject.toString(), ContentType.APPLICATION_JSON);
//			File jsonFile = new File(jsonFilePath+File.separator+fileName+".JSON");
//	        try (FileWriter fileWriter = new FileWriter(jsonFile)) {
//	            fileWriter.write(jsonObject.toString());
//	            System.out.println("JSON data written to file: " + jsonFilePath);
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//			String boundary = "boundary123";
//			HttpEntity multipartEntity = MultipartEntityBuilder.create().setBoundary(boundary)
//					.addPart("data", jsonDataPart).build();
//
//			httpPost.setEntity(multipartEntity);
//
//////			 Execute request
////	           try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
////	               int statusCode = response.getStatusLine().getStatusCode();
////	                 responseBody = EntityUtils.toString(response.getEntity());
////
////	               if (statusCode == 200) {
////	                   exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
////	                   exceptionCode.setErrorMsg(responseBody);
////	                   System.out.println("Response: " + responseBody);
////	                   
////	                   // Parse the response to get the RequestNo
////	                   JSONObject jsonResponse = new JSONObject(responseBody);
////	                    requestNo = jsonResponse.getString("RequestNo");
////	                   System.out.println("Request No: " + requestNo);
////	                   exceptionCode.setResponse1(requestNo);
////	               } else {
////	                   System.out.println("Failed with response code: " + statusCode);
////	                   System.out.println("Response body: " + responseBody);
////	                   exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
////	                   exceptionCode.setErrorMsg(responseBody);
////	                   exceptionCode.setResponse1(requestNo);
////	               }
////	           }
//	           if (responseBody == null) {
//	        	    responseBody = "{\"Status:\"\"No response received or an error occurred\"}";
//	        	}
//	        	// Write the responseBody to a file
//	        	File logFile = Paths.get(logPath, fileName + fileExtension).toFile();
//	        	try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
//	        	    writer.write(responseBody);
//	        	    System.out.println("Response body written to file: " + logFile.getAbsolutePath());
//	        	} catch (IOException ioe) {
//	        	    ioe.printStackTrace();
//	        	    exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//	        	    exceptionCode.setErrorMsg(ioe.getMessage());
//	        	    System.out.println("Failed to write response body to file: " + logFile.getAbsolutePath());
//	        	}
//
//	           
//		} catch (Exception e) {
//			e.printStackTrace();
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//		}
//
//		
//		return exceptionCode;
//	}
//
//	public static ExceptionCode postSampleData(Object authToken) {
////    String TOKEN_URL = "https://gdi.centralbank.go.ke/test/api/v1/flows/rest/API_CUSTOMERINFOSYNC/1.0/Customer_Info";
//		String TOKEN_URL = "https://gdi.centralbank.go.ke/test/api/v1/flows/rest/API_ACCOUNTSATTRIBUTESSYNC/1.0/Accts_Attributes";
//		ExceptionCode exceptionCode = new ExceptionCode();
//
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//
//			HttpPost httpPost = new HttpPost(TOKEN_URL);
//			httpPost.setHeader("Content-Type", "multipart/mixed; boundary=boundary123");
//			httpPost.setHeader("Authorization", "Bearer " + authToken);
//
//			// String value = "[{\"ROW ID\" :\"1\",\"BANK CODE\" :\"0000001\",\"BRANCH ID\"
//			// :\"003\",\"REPORTING DATE\" :\"03-JUN-2020\",\"CUSTOMER ID\"
//			// :\"0354678\",\"GENDER\":\"M\",\"CUSTOMER NAME\" :\"Augustine Neto
//			// Other\",\"ONBOARDING DATE\" :\"08-JAN-2015\",\"DATE OF BIRTH\"
//			// :\"14-FEB-1978\",\"NATIONALITY\" :\"003\",\"PRI ID DOC TYPE\"
//			// :\"KE\",\"NATIONAL ID\" :\"CUSTID01\",\"COMPANY REG NUMBER\"
//			// :\"21987563\",\"PASSPORT NUMBER\" :\"\",\"PERSONAL ID NUMBER\"
//			// :\"A000597819E\",\"CUSTOMER LEGAL STATUS\" :\"LSTC01\",\"CUSTOMER TYPE\"
//			// :\"CUSTTC02\",\"INSIDER CATEGORY TYPE\" :\"ITC01\",\"ECONOMIC SECTOR CODE\"
//			// :\"0017.035\",\"BUSINESS SEGMENT\" :\"BSC04\",\"RESIDENCY\" :\"US\",\"POSTAL
//			// ADDRESS\" :\"132, My Street, Kingston, New York 12401, United
//			// States\",\"TELEPHONE NUMBER\" :\"(555) 555-1234\",\"EMAIL ADDRESS\"
//			// :\"another@anywhere.com\"}]";
//
//			String value = "[{ \"ROW ID\" :\"1\", \"BANK CODE\" :\"0000010\", \"BRANCH ID\" :\"003\", \"BUSINESS REPORTING DAY, MONTH & YEAR\" :\"03-May-2020\", \"CUSTOMER ID\" :\"0354678\", "
//					+ "\"ACCOUNT/CONTRACT NUMBER\" :\"01A002035467801\", \"ACCOUNT NAME\" :\"01A002035467801\", \"ACCOUNT OWNERSHIP TYPE\" :\"AOC01\", \"BUSINESS ECONOMIC ACTIVITY CODE\" :\"A0111\", "
//					+ "\"CURRENCY CODE\" :\"GBP\", \"TYPE OF ACCOUNT\" :\"CAA\", \"CHART OF ACCOUNTS CORRESPONDING CODE\" :\"CA2\", \"GL MAPPING CODE\" :\"GL201022\", \"DATE ACCOUNT WAS OPENED\" :\"23-June-2019\", "
//					+ "\"DATE ACCOUNT WAS CLOSED\" :\"\", \"DEBIT INTEREST RATE (DR)\" :\"0.11111\", \"CREDIT INTEREST RATE (CR)\" :\"0.11111\", \"SANCTIONED ACCOUNT LIMIT IN FCY\" :\"0.12345\", "
//					+ "\"SANCTIONED ACCOUNT LIMIT IN LCY EQUIVALENT\" :\"0.11111\", \"ACCOUNT STATUS\" :\"ACT\", \"ACCOUNT STATUS CHANGE DATE\" :\"\", \"EXPIRY DATE\" :\"\" }] }";
//
//			// Create multipart entity
//			String boundary = "boundary123";
//
//			JSONArray accountsArray = new JSONArray(value);
//
//			// Create the JSON object with dynamic values
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("INSTITUTION_CODE", "0000010");
//			jsonObject.put("REQUEST_ID", "");
//			jsonObject.put("IS_ATTACHED", "N");
//			jsonObject.put("REPORTING_DATE", "2024-08-20");
//			jsonObject.put("ACCOUNTS_ATTRIBUTES", accountsArray);
//
//			// Convert the JSON object to StringBody
//			StringBody jsonDataPart = new StringBody(jsonObject.toString(), ContentType.APPLICATION_JSON);
//
//			HttpEntity multipartEntity = MultipartEntityBuilder.create().setBoundary(boundary)
//					.addPart("data", jsonDataPart).build();
//
//			httpPost.setEntity(multipartEntity);
//			// Execute request
//			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//				int statusCode = response.getStatusLine().getStatusCode();
//				String responseBody = EntityUtils.toString(response.getEntity());
//
//				if (statusCode == 200) {
//					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
//					exceptionCode.setErrorMsg(responseBody);
//					System.out.println("Response: " + responseBody);
//				} else {
//					System.out.println("Failed with response code: " + statusCode);
//					System.out.println("Response body: " + responseBody);
//					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//					exceptionCode.setErrorMsg(responseBody);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//		}
//
//		return exceptionCode;
//	}
//}
