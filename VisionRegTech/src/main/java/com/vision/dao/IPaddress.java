package com.vision.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPaddress {
	public static String getMacAddress(String ip) {
        String macAddress = "";

        try {
            // Step 1: Check if IP is local (on the same machine)
            InetAddress inetAddress = InetAddress.getByName(ip);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

            if (networkInterface != null) {
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes != null) {
                    // Convert bytes to MAC address format
                    StringBuilder macBuilder = new StringBuilder();
                    for (byte b : macBytes) {
                        macBuilder.append(String.format("%02X:", b));
                    }
                    macAddress = macBuilder.substring(0, macBuilder.length() - 1);  // Remove the trailing colon
                    return macAddress;
                }
            }

            // Step 2: If no local MAC address found, try fetching from ARP cache for remote IPs
            macAddress = getMacAddressFromARP(ip);

            if (macAddress.isEmpty()) {
                macAddress = ip;  // Return IP if MAC address not found
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return macAddress.toUpperCase();
    }
    // Method to fetch MAC address using ARP or IP neighbor command (Linux/Windows)
    private static String getMacAddressFromARP(String ip) {
        String macAddress = "";

        try {
            // For Windows: arp -a <IP>
            // For Linux: ip neighbour show <IP> or arp -n <IP>
            String cmd = System.getProperty("os.name").toLowerCase().contains("win")
                    ? "arp -a " + ip
                    : "ip neighbour show " + ip;

            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Regex pattern to match MAC address (both standard and dot notation)
            Pattern pattern = Pattern.compile("(([0-9A-Fa-f]{2}[-:]){5}[0-9A-Fa-f]{2})|(([0-9A-Fa-f]{4}\\.){2}[0-9A-Fa-f]{4})");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    macAddress = matcher.group();
                    break;
                }
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return macAddress;
    }
}
