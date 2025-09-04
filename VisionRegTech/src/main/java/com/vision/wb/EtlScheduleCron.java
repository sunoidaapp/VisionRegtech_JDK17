package com.vision.wb;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.dao.EtlServiceDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;

@Component
public class EtlScheduleCron extends EtlSwitch {
	@Autowired
	EtlSwitch etlSwitch;
	
	@Autowired
	EtlServiceDao etlServiceDao;
	
	@Autowired
	CommonDao commonDao;
	
	@Value("${schedule.genBuild}")
	private String genBuildFlag;
	
	//@Scheduled(fixedRate = 30000)
	public void EtlServiceCron() {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
				if("Y".equalsIgnoreCase(genBuildFlag) && etlService) {
					if(etlServRunThreadCnt < etlServMaxThreadCnt) {
						etlServRunThreadCnt++;
						//System.out.println("RA GENBUILD SERVICE RUNNING");
						exceptionCode = etlServiceDao.getEtlPostngDetailGenBuild();
						if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							HashMap<String,String> postingData = (HashMap<String,String>)exceptionCode.getResponse();
							String COUNTRY = postingData.get("COUNTRY");
							String LE_BOOK = postingData.get("LE_BOOK");
							String EXTRACTION_FEED_ID = postingData.get("EXTRACTION_FEED_ID");
							String EXTRACTION_FREQUENCY = postingData.get("EXTRACTION_FREQUENCY");
							String EXTRACTION_SEQUENCE = postingData.get("EXTRACTION_SEQUENCE");
							String POSTING_SEQUENCE = postingData.get("POSTING_SEQUENCE");
							String BUSINESS_DATE = postingData.get("BUSINESS_DATE");
							String POSTING_DATE = postingData.get("POSTING_DATE");
							String logFileName = EXTRACTION_FEED_ID+"_"+POSTING_SEQUENCE+"_"+COUNTRY+"_"+LE_BOOK+"_"+BUSINESS_DATE;
							String genBuildPassingLog = EXTRACTION_FEED_ID+"_"+POSTING_SEQUENCE;
							String postingStatus = "I";
							
							etlServiceDao.doUpdatePostings(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
									POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,postingStatus);
							
							etlServiceDao.doUpdatePostingsHistory(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
									POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,postingStatus);
							
							String execsPath =  commonDao.findVisionVariableValue("RA_EXECS_PATH");
							String logPath =  commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						     Date date=df.parse(BUSINESS_DATE);
						     df=new SimpleDateFormat("dd-MMM-yyyy");
						     String formatBusinessDate = df.format(date);
							try {
								Process proc;
								proc = Runtime.getRuntime().exec(
										"java -jar "+execsPath+genericBuildServiceJar+" "+COUNTRY+" "+LE_BOOK+" "+EXTRACTION_FEED_ID+" "+POSTING_SEQUENCE+" "+formatBusinessDate+" Y "+logPath+" 9999 ");
								proc.waitFor();
								InputStream in = proc.getInputStream();
								InputStream err = proc.getErrorStream();
								byte b[]=new byte[in.available()];
								in.read(b,0,b.length);
							    if(ValidationUtil.isValid(new String(b))) {
							    	String status = new String(b);
							    	status = status.substring(status.indexOf(":")+1);
							    	if("0".equalsIgnoreCase(status.trim())) {
							    		postingStatus  = "C";	
							    	}else {
							    		postingStatus  = "E";
							    	}
							    	
							    	//Updating Status on History	
							    	etlServiceDao.doUpdatePostingsHistory(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
											POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,postingStatus);
							    	//Delete Posting Table
							    	etlServiceDao.doDeletePostings(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
											POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,postingStatus);
							    	//Update Log File
							    	etlServiceDao.updateLogFileName(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
											POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,logFileName);
							    }
							} catch (InterruptedException e) {
								System.out.println("****************Error on RA ETL Service****************");
								System.out.println(e.getMessage());
							}
						}
					}
				}
			}catch (IOException e) {
				System.out.println("****************Error on RA ETL Service****************");
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally {
				if(etlServRunThreadCnt > 0)
					etlServRunThreadCnt = etlServRunThreadCnt-1;
			}
		}
}
