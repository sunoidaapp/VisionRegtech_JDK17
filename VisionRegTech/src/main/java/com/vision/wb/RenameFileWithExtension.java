package com.vision.wb;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RenameFileWithExtension {
    public static void main(String[] args) {
        // Path to the old file
        String oldFilePath = "C:\\Java_Exces\\CBK_GDI_ACCOUNT_BALANCE24oct2024.zip";

        // New file name without extension
        String newFileNameWithoutExtension = "CBK_GDI_ACCOUNT_BALANCE";
        
        String datetimeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date());
   	 System.out.println(datetimeStamp);

        // Rename the file while retaining the extension
        renameFileWithExtension(oldFilePath, newFileNameWithoutExtension+"_"+datetimeStamp);
    	
    	
    }

    public static void renameFileWithExtension(String oldFilePath, String newFileNameWithoutExtension) {
        File oldFile = new File(oldFilePath);

        if (!oldFile.exists()) {
            System.out.println("The file does not exist: " + oldFilePath);
            return;
        }

        // Extract the extension from the old file
        String extension = getFileExtension(oldFile);

        // Create the new file path
        String newFilePath = oldFile.getParent() + File.separator + newFileNameWithoutExtension + extension;

        File newFile = new File(newFilePath);

        // Attempt to rename the file
        if (oldFile.renameTo(newFile)) {
            System.out.println("Renamed successfully: " + oldFilePath + " -> " + newFilePath);
        } else {
            System.out.println("Failed to rename: " + oldFilePath);
        }
    }

    // Helper method to extract the file extension
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex); // Includes the dot, e.g., ".zip"
        }
        return ""; // No extension
    }
}
