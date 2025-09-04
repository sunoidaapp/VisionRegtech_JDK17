//package com.vision.examples;
//
//import java.io.File;
//import java.util.Map;
//
//import org.apache.hc.client5.http.classic.methods.HttpPost;
//import org.apache.hc.client5.http.entity.mime.FileBody;
//import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
//import org.apache.hc.client5.http.entity.mime.StringBody;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.core5.http.io.entity.EntityUtils;
//import org.apache.hc.core5.http.io.entity.StringEntity;
//import org.json.JSONObject;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class auth {
//	public static void main(String[] args) {
//		String authToken = getAuthenticationCode();
//		if (!authToken.isEmpty()) {
////		postSampleData(authToken);
//			System.out.println(authToken);
//		}
//	}
//
//	public static String getAuthenticationCode() {
//		String TOKEN_URL = "https://gdi.centralbank.go.ke/oauth2/v1/token";
//		String CLIENT_ID = "a5bc3279a5d040d2b9a7573f82c7dc30";
//		String CLIENT_SECRET = "180d8a20-6c1c-4440-9d8a-e3c3176a78ca";
//		String GRANT_TYPE = "client_credentials"; // or another grant type
//		String SCOPE = "https://8C80E3C8CFCF45EBB272C5478F767122.ke1.s7071808.oraclecloudatcustomer.com:443urn:opc:resource:consumer::all"; // optional,
//																																			// depends
//																																			// on
//		String access_token = ""; // your;
//									// server
//
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//			// Original string
//			System.out.println(" CLIENT_ID -->" + CLIENT_ID);
//			System.out.println(" CLIENT_SECRET -->" + CLIENT_SECRET);
//			System.out.println(" TOKEN_URL -->" + TOKEN_URL);
//			System.out.println(" SCOPE -->" + SCOPE);
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
//
//					// Parse JSON response
//					String responseBody = EntityUtils.toString(response.getEntity());
//					ObjectMapper objectMapper = new ObjectMapper();
//					Map<String, Object> responseData = objectMapper.readValue(responseBody, Map.class);
//
//					access_token = (String) responseData.get("access_token");
//					String token_type = (String) responseData.get("token_type");
//					int expires_in = (int) responseData.get("expires_in");
//					System.out.println("Access Token: " + access_token);
//					System.out.println("Token Type: " + token_type);
//					System.out.println("Expires in: " + expires_in);
//
//				} else {
//					System.out.println("Failed to get access token. Response code: " + statusCode);
//					String responseBody = EntityUtils.toString(response.getEntity());
//					System.out.println("Response body: " + responseBody);
//
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//		return access_token;
//	}
//
//	public static void postSampleData(Object authToken) {
////  String TOKEN_URL = "https://gdi.centralbank.go.ke/test/api/v1/flows/rest/API_CUSTOMERINFOSYNC/1.0/Customer_Info";
//		String TOKEN_URL = "https://gdi.centralbank.go.ke/submitAccountBalance";
//
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//
//			HttpPost httpPost = new HttpPost(TOKEN_URL);
//			httpPost.setHeader("Content-Type", "multipart/mixed; boundary=boundary123");
//			httpPost.setHeader("Authorization", "Bearer " + authToken);
//
//			String boundary = "boundary123";
//			JSONObject jsonObject1 = new JSONObject();
//			jsonObject1.put("INSTITUTION_CODE", "0000057");
//			jsonObject1.put("REQUEST_ID", "");
//			jsonObject1.put("IS_ATTACHED", "Y");
//			jsonObject1.put("REPORTING_DATE", "2023-11-04");
//			jsonObject1.put("ACCOUNTS_BALANCES", "[{}]");
//			String zipFilePath = "/gdi/app/CSV_Path/CBK_GDI_ACCOUNT_BALANCE.zip";
//			File zipFile = new File(zipFilePath);
//			System.out.println("PAth" + zipFilePath);
//
//			if (zipFile.exists()) {
//				FileBody zipFileBody = new FileBody(zipFile, ContentType.MULTIPART_FORM_DATA);
//				StringBody jsonDataPart = new StringBody(jsonObject1.toString(), ContentType.APPLICATION_JSON);
//				System.out.println("zipfile Body" + zipFileBody.getFile().getAbsolutePath());
//				/// gdi/app/CSV_Path/CBK_GDI_ACCOUNT_BALANCE.zip
//				HttpEntity multipartEntity = MultipartEntityBuilder.create().setBoundary(boundary)
//						.addPart("data", jsonDataPart).addPart("file", zipFileBody).build();
//
//				httpPost.setEntity(multipartEntity);
//				// Execute request
//				try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//					int statusCode = response.getStatusLine().getStatusCode();
//					String responseBody = EntityUtils.toString(response.getEntity());
//
//					if (statusCode == 200) {
//						System.out.println("Response: " + responseBody);
//					} else {
//						System.out.println("Failed with response code: " + statusCode);
//						System.out.println("Response body: " + responseBody);
//					}
//				}
//			} else {
//				System.out.println("out of block");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//}