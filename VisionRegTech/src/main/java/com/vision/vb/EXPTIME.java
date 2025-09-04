package com.vision.vb;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Calendar;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EXPTIME {

	public static void main(String[] args) {
		String token = "eyJ4NXQjUzI1NiI6IkgxOEotQXVMVHdNTGpHZXg3NXNId0dfRXpTQVUwVG5uV0FYNnVNTTFqaW8iLCJ4NXQiOiJ2aW50MjhROGpJMkFwWGxBYTVpYkVmNVJRUnMiLCJraWQiOiJTSUdOSU5HX0tFWSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiZjQwZWVkNjRiMjQ0NWI1OWViMTI2NjA0OTVmN2MyYiIsInVzZXIudGVuYW50Lm5hbWUiOiJpZGNzLTNjZGEzNDRkZGVlMzQ4ODA5NjVjZjQ2NTgxZDAwYzg1Iiwic3ViX21hcHBpbmdhdHRyIjoidXNlck5hbWUiLCJwcmltVGVuYW50Ijp0cnVlLCJpc3MiOiJodHRwczpcL1wvaWRlbnRpdHkub3JhY2xlY2xvdWQuY29tXC8iLCJ0b2tfdHlwZSI6IkFUIiwiY2xpZW50X2lkIjoiYmY0MGVlZDY0YjI0NDViNTllYjEyNjYwNDk1ZjdjMmIiLCJjYV9ndWlkIjoiY2FjY3QtOGI4YTAwODI1NDQyNDkzZDhiM2M4MDA4MDkwZDFjOWMiLCJhdWQiOlsiaHR0cHM6XC9cLzhDODBFM0M4Q0ZDRjQ1RUJCMjcyQzU0NzhGNzY3MTIyLmtlMS5zNzA3MTgwOC5vcmFjbGVjbG91ZGF0Y3VzdG9tZXIuY29tOjQ0MyIsInVybjpvcGM6bGJhYXM6bG9naWNhbGd1aWQ9OEM4MEUzQzhDRkNGNDVFQkIyNzJDNTQ3OEY3NjcxMjIiXSwic3ViX3R5cGUiOiJjbGllbnQiLCJzY29wZSI6InVybjpvcGM6cmVzb3VyY2U6Y29uc3VtZXI6OmFsbCIsImNsaWVudF90ZW5hbnRuYW1lIjoiaWRjcy0zY2RhMzQ0ZGRlZTM0ODgwOTY1Y2Y0NjU4MWQwMGM4NSIsImV4cCI6MTc1MDgzODU1MywiaWF0IjoxNzUwODM0OTUzLCJ0ZW5hbnRfaXNzIjoiaHR0cHM6XC9cL2lkY3MtM2NkYTM0NGRkZWUzNDg4MDk2NWNmNDY1ODFkMDBjODUuaWRlbnRpdHkuczcwNzE4MDgub3JhY2xlY2xvdWRhdGN1c3RvbWVyLmNvbSIsImNsaWVudF9ndWlkIjoiZWIzNDkxNTIxMWQ2NDYxMjkwYjA5NDdiYjczYzcxNmEiLCJjbGllbnRfbmFtZSI6IkkgJiBNIEJhbmsiLCJ0ZW5hbnQiOiJpZGNzLTNjZGEzNDRkZGVlMzQ4ODA5NjVjZjQ2NTgxZDAwYzg1IiwianRpIjoiNDEwZmMwN2YtNmNjOC00NGFjLTlhYTItMmYyYjIzYjE0MzNmIn0.UvpnczET37tzyyrWEL8tzmyfdXgTnZ5L97bN9ig_6qiIopvw36Wf1wcURZC3Q6q9RtK9zfy4ESlYPR6j5hsgBD-9qwRMzarg5YCOyICBZDPPDN0un1GWSsEf_C8UwGC7ykDMQbZCmicaKnFA-74usRP453bRXfX8I6J6ntijMj2jRTECDg38-yUKnQrcTbiIdGBP7JnV56DqguypVij-DeWiKE3GE0uDJxW8wymxMkdd7LPaIoj9Z4Majw3hU4crmXifGwm_Ih9KzId_HjHEwAipexGLl4bGPQVaJBRqzzkx1CjioSyVxjgPwmCt5uKYKgv5Iug_ASfOnCoe0vKPBQ";
		try {
			LocalDateTime expTime = Instant.ofEpochSecond(extractExpFromToken(token)).atZone(ZoneId.systemDefault())
					.toLocalDateTime();
			Timestamp dbTimestamp = Timestamp.valueOf(expTime);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dbTimestamp);
			calendar.add(Calendar.MINUTE, -5); // subtract 5 minutes

			Timestamp newTimestamp = new Timestamp(calendar.getTimeInMillis());

			System.out.println("Original Exp: " + dbTimestamp);
			System.out.println("Minus 5 Min:  " + newTimestamp);

			System.out.println(dbTimestamp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Long extractExpFromToken(String token) throws Exception {
		String[] parts = token.split("\\.");
		if (parts.length < 2)
			throw new IllegalArgumentException("Invalid token");
		String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

		Map<String, Object> claims = new ObjectMapper().readValue(payload, Map.class);
		return ((Number) claims.get("exp")).longValue(); // epoch seconds
	}

}
