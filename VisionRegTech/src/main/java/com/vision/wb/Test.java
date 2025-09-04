package com.vision.wb;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Test {
    public static String getMACAddress(String ip) {
    	   try {
               InetAddress inetAddress = InetAddress.getByName(ip);
               NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

               if (networkInterface == null) {
                   return ""; // No network interface found
               }

               byte[] macBytes = networkInterface.getHardwareAddress();
               if (macBytes == null) {
                   return ""; // No MAC address found
               }

               // Convert bytes to readable MAC address format
               StringBuilder macAddress = new StringBuilder();
               for (byte b : macBytes) {
                   macAddress.append(String.format("%02X:", b));
               }

               // Remove trailing colon
               return macAddress.substring(0, macAddress.length() - 1);
           } catch (Exception e) {
               e.printStackTrace();
               return "";
           }
    }
    
    public static String date() {
    	 String previousDateStr = "";
    	 String inputDateStr = "09-Jun-2024";

         // Define the date format
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

         // Parse the input string to a LocalDate
         LocalDate inputDate = LocalDate.parse(inputDateStr, formatter);

         // Subtract one day
         LocalDate previousDate = inputDate.minusDays(1);

         // Format and print the previous date
          previousDateStr = previousDate.format(formatter);
         System.out.println("Previous date: " + previousDateStr);
         return previousDateStr;
     }
  
    

    public static void main(String[] args) {
        String macAddress = getMACAddress("10.16.1.120");
        System.out.println( macAddress);
        
        String datetimeStamp = new SimpleDateFormat("YYYYMMDD").format(new Date());
        System.out.println(datetimeStamp);
        System.out.println(date());
        
        
        String cntry = "AD,AE,AF,AG,AI,AL,AM,AN,AO,AQ,AR,AS,AT,AU,AW,AX,AZ,BA,BB,BD,";
        System.out.println(cntry.split(",").length);
    }
    
    
    
}


	


