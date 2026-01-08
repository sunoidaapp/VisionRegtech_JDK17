import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

public class SimpleCsvSubmitTest {

    public static void main(String[] args) {
        String apiUrl = "https://gdi.centralbank.go.ke/test/api/v1/flows/rest/API_EXCHANGERATESSYN/1.0/Exchange_Rates"; // Replace with your API URL
        String authToken = "eyJ4NXQjUzI1NiI6IkgxOEotQXVMVHdNTGpHZXg3NXNId0dfRXpTQVUwVG5uV0FYNnVNTTFqaW8iLCJ4NXQiOiJ2aW50MjhROGpJMkFwWGxBYTVpYkVmNVJRUnMiLCJraWQiOiJTSUdOSU5HX0tFWSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiZjQwZWVkNjRiMjQ0NWI1OWViMTI2NjA0OTVmN2MyYiIsInVzZXIudGVuYW50Lm5hbWUiOiJpZGNzLTNjZGEzNDRkZGVlMzQ4ODA5NjVjZjQ2NTgxZDAwYzg1Iiwic3ViX21hcHBpbmdhdHRyIjoidXNlck5hbWUiLCJwcmltVGVuYW50Ijp0cnVlLCJpc3MiOiJodHRwczpcL1wvaWRlbnRpdHkub3JhY2xlY2xvdWQuY29tXC8iLCJ0b2tfdHlwZSI6IkFUIiwiY2xpZW50X2lkIjoiYmY0MGVlZDY0YjI0NDViNTllYjEyNjYwNDk1ZjdjMmIiLCJjYV9ndWlkIjoiY2FjY3QtOGI4YTAwODI1NDQyNDkzZDhiM2M4MDA4MDkwZDFjOWMiLCJhdWQiOlsiaHR0cHM6XC9cLzhDODBFM0M4Q0ZDRjQ1RUJCMjcyQzU0NzhGNzY3MTIyLmtlMS5zNzA3MTgwOC5vcmFjbGVjbG91ZGF0Y3VzdG9tZXIuY29tOjQ0MyIsInVybjpvcGM6bGJhYXM6bG9naWNhbGd1aWQ9OEM4MEUzQzhDRkNGNDVFQkIyNzJDNTQ3OEY3NjcxMjIiXSwic3ViX3R5cGUiOiJjbGllbnQiLCJzY29wZSI6InVybjpvcGM6cmVzb3VyY2U6Y29uc3VtZXI6OmFsbCIsImNsaWVudF90ZW5hbnRuYW1lIjoiaWRjcy0zY2RhMzQ0ZGRlZTM0ODgwOTY1Y2Y0NjU4MWQwMGM4NSIsImV4cCI6MTc0NDM4MDkwNCwiaWF0IjoxNzQ0Mzc3MzA0LCJ0ZW5hbnRfaXNzIjoiaHR0cHM6XC9cL2lkY3MtM2NkYTM0NGRkZWUzNDg4MDk2NWNmNDY1ODFkMDBjODUuaWRlbnRpdHkuczcwNzE4MDgub3JhY2xlY2xvdWRhdGN1c3RvbWVyLmNvbSIsImNsaWVudF9ndWlkIjoiZWIzNDkxNTIxMWQ2NDYxMjkwYjA5NDdiYjczYzcxNmEiLCJjbGllbnRfbmFtZSI6IkkgJiBNIEJhbmsiLCJ0ZW5hbnQiOiJpZGNzLTNjZGEzNDRkZGVlMzQ4ODA5NjVjZjQ2NTgxZDAwYzg1IiwianRpIjoiNjI0NjdiZmEtNTBhNS00MmYxLWJkNTQtMjc5MjI3NWVkMzI1In0.PXEzOMy2PpwSeFBDcXG06iJRqIYJZus7C94P05QMO4raVZy8GEZgFLcHeRaYcqW-yFSUeNJeN-Mp7ibWo3-r81fmDKVwJnZDQZWAGDLLLDIhm3YEIxlbnrwvzVevJCQPAfARm1JsxhN-FTX22aHpGKm_giMvKvPxWWsfBYAxi_UOxrfwxSGmeyPOebTsTlv0qpAOPzHpVt77GfJK0-qoaqbV0rIdgV102IvWfQ8PW719eTnwk_TmcMlVcpmkgHt1yo6cmclOhqijCflo6u_tck_f3_F5Np1k-LqMZBnyZQKx1Z3tCiuGvcRRs3Pw69ewqDWfoEsNxOWV-Iyg0DMNTQ";                 // Hardcoded token
        String institutionCode = "0000057";
        String requestId = "";
        String isFileAttached = "Y";
        String reportingDate = "13-Dec-2025";               // Hardcoded reporting date
        String csvFilePath = "C:\\temp\\testfile.csv";      // CSV location

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // Prepare POST request
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Authorization", "Bearer " + authToken);

            // Format reporting date
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = outputFormat.format(inputFormat.parse(reportingDate));

            // Prepare JSON metadata
            JSONObject jsonData = new JSONObject();
            jsonData.put("INSTITUTION_CODE", institutionCode);
            jsonData.put("REQUEST_ID", requestId);
            jsonData.put("IS_ATTACHED", isFileAttached);
            jsonData.put("REPORTING_DATE", formattedDate);
            jsonData.put("DATA_LIST", "[]"); // empty data list for CSV submission

            // Prepare CSV file
            File csvFile = new File(csvFilePath);
            if (!csvFile.exists()) {
                System.out.println("CSV file does not exist at: " + csvFilePath);
                return;
            }

            // Build multipart request
            FileBody fileBody = new FileBody(csvFile, ContentType.DEFAULT_BINARY);
            StringBody jsonPart = new StringBody(jsonData.toString(), ContentType.APPLICATION_JSON);
            String boundary = "boundary123";
            httpPost.setEntity(MultipartEntityBuilder.create().setBoundary(boundary)
                    .addPart("data", jsonPart)
                    .addPart("file", fileBody)
                    .build());
            httpPost.setHeader("Content-Type", "multipart/mixed; boundary=boundary123");
            // Execute request
            System.out.println("Submission Start ");
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                System.out.println("Response Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
