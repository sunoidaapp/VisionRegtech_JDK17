package com.vision.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.util.ValidationUtil;

@Component
public class AdfScheduler {

	@Autowired
	CommonDao commonDao;
	@Value("${adfEmail.alerts}")
	private String adfEmailFlag;

	@Scheduled(cron = "0 */2 * * * *") // every 2 minutes
	public void callMailJar() {
		if (adfEmailFlag.equalsIgnoreCase("Y")) {
			runExternalJar();
		}
	}

	private void runExternalJar() {
		try {
			System.out.println("Starting external JAR...");

			// Fetch jar name from DB
			String jarName = commonDao.findVisionVariableValue("RG_ADF_SCHEDULER_NAME");
			jarName = ValidationUtil.isValid(jarName) ? jarName : "AdfMailScheduler.jar";

			// Fetch scheduler folder path
			String schedulerPath = commonDao.findVisionVariableValue("RG_ADF_SCHEDULER_PATH");

			if (!ValidationUtil.isValid(schedulerPath)) {
				System.err.println("ERROR: RG_ADF_SCHEDULER_PATH is empty or NULL");
				return;
			}

			File dir = new File(schedulerPath);

			if (!dir.exists()) {
				System.err.println("ERROR: Scheduler directory does not exist: " + schedulerPath);
				return;
			}

			File jarFile = new File(dir, jarName);
			if (!jarFile.exists()) {
				System.err.println("ERROR: JAR not found: " + jarFile.getAbsolutePath());
				return;
			}

			ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarFile.getAbsolutePath());
			pb.directory(dir);
			pb.redirectErrorStream(true);

			Process process = pb.start();

			// Read JAR output
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("JAR OUTPUT: " + line);
			}

			int exitCode = process.waitFor();
			System.out.println("External JAR completed with exit code: " + exitCode);

		} catch (Exception e) {
			System.err.println("ERROR while running external JAR:");
			e.printStackTrace();
		}
	}
}
