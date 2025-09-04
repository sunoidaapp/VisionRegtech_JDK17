package com.vision.wb;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.util.ValidationUtil;

@Component
public class ExcelUploadWb {
	public static final long fixedRate = 30000;
	public static Logger logger = LoggerFactory.getLogger(ExcelUploadPc.class);
	@Value("${schedule.upload}")
	private  String rgUploadFlag;
	@Scheduled(fixedRate=fixedRate)
	@Async
	public  void doUpload() {
		if(ValidationUtil.isValid(rgUploadFlag) && "Y".equalsIgnoreCase(rgUploadFlag)) {
			ExcelUploadPc excelUploadPc = new ExcelUploadPc();
			System.out.println("start " +new Date());
			String downloadDir = excelUploadPc.xluploadLogFilePath;
			String	uploadDir = excelUploadPc.xluploadDataFilePath;
			if(!ValidationUtil.isValid(uploadDir)) {
				logger.error("Invalid Upload Filepath "+uploadDir);
			}
			if(!ValidationUtil.isValid(downloadDir)) {
				logger.error("Invalid Download Filepath "+downloadDir);
			}
			int retVal = excelUploadPc.uploadData();
			String uploadLogFileName = "";
			System.out.println("Status["+retVal+"]");
			System.out.println("End " +new Date());
		}
		
		//System.exit(retVal);
	}
}
