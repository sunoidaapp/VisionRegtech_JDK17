package com.vision.wb;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.util.ValidationUtil;

@Component
public class ExcelUploadWb {

    public static final long FIXED_RATE = 30000;
    private static final Logger logger = LoggerFactory.getLogger(ExcelUploadWb.class);

    @Value("${schedule.upload}")
    private String rgUploadFlag;

    @Autowired
    private ExcelUploadPc excelUploadPc;

    @Scheduled(fixedRate = FIXED_RATE)
    @Async
    public void doUpload() {
        if (!"Y".equalsIgnoreCase(rgUploadFlag)) {
            logger.info("Upload schedule disabled (schedule.upload != Y). Skipping execution.");
            return;
        }

        logger.info("Excel upload started at {}", new Date());

        String uploadDir = excelUploadPc.xluploadDataFilePath;
        String downloadDir = excelUploadPc.xluploadLogFilePath;

        if (!ValidationUtil.isValid(uploadDir)) {
            logger.error("Invalid Upload Filepath: {}", uploadDir);
            return;
        }
        if (!ValidationUtil.isValid(downloadDir)) {
            logger.error("Invalid Download Filepath: {}", downloadDir);
            return;
        }

        try {
            int retVal = excelUploadPc.uploadData();
            logger.info("Excel upload completed with status [{}] at {}", retVal, new Date());
        } catch (Exception e) {
            logger.error("Excel upload failed", e);
        }
    }
}
