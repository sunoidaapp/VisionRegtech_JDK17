package com.vision.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);
		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}

	static public void addFileToZip(String csvFilePath, ZipOutputStream zip) throws Exception {
		File csvFile = new File(csvFilePath);
		byte[] buf = new byte[1024];
		int len;

		// Open the CSV file to be added to the ZIP
		try (FileInputStream in = new FileInputStream(csvFile)) {
			// Create a new ZIP entry with the CSV file name
			zip.putNextEntry(new ZipEntry(csvFile.getName()));

			// Write the content of the CSV file to the ZIP
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	public static void renameFileWithExtension(String oldFilePath, String newFileNameWithoutExtension) {
		File oldFile = new File(oldFilePath);

		if (!oldFile.exists()) {
//	            System.out.println("The file does not exist: " + oldFilePath);
			return;
		}

		// Extract the extension from the old file
		String extension = getFileExtension(oldFile);

		// Create the new file path
		String newFilePath = oldFile.getParent() + File.separator + newFileNameWithoutExtension + extension;

		File newFile = new File(newFilePath);

		// Attempt to rename the file
		if (oldFile.renameTo(newFile)) {
//	            System.out.println("Renamed successfully: ");
		} else {
//	            System.out.println("Failed to rename: ");
		}
	}

	public static String getFileExtension(File file) {
		String name = file.getName();
		int lastDotIndex = name.lastIndexOf('.');
		if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
			return name.substring(lastDotIndex); // Includes the dot, e.g., ".zip"
		}
		return ""; // No extension
	}

	private ZipUtils() {
	}

	// Create a ZIP containing a single file (XML or Excel)
	public static void zipSingleFile(Path inputFile, Path zipOut) throws Exception {
		Files.createDirectories(zipOut.getParent());
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipOut))) {
			ZipEntry entry = new ZipEntry(inputFile.getFileName().toString());
			zos.putNextEntry(entry);
			Files.copy(inputFile, zos);
			zos.closeEntry();
		}
	}

	// Extract ZIP into output directory
	public static void unzipToDirectory(Path zipFile, Path outDir) throws Exception {
		Files.createDirectories(outDir);
		try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				Path outPath = outDir.resolve(entry.getName()).normalize();
				if (!outPath.startsWith(outDir)) {
					throw new SecurityException("Zip Slip detected: " + entry.getName());
				}
				Files.copy(zis, outPath, StandardCopyOption.REPLACE_EXISTING);
				zis.closeEntry();
			}
		}
	}

	public static void zipSingleFileWithEntryName(Path inputFile, Path zipOut, String entryName) throws Exception {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipOut))) {
			zos.putNextEntry(new ZipEntry(entryName));
			Files.copy(inputFile, zos);
			zos.closeEntry();
		}
	}

}
