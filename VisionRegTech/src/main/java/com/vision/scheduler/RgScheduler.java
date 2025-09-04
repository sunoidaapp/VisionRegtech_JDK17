package com.vision.scheduler;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.dao.RgBuildProcessDao;
import com.vision.dao.RgSchedulerDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.RgBuildsVb;

@Component
public class RgScheduler {
	
	@Autowired
	RgSchedulerDao rgSchedulerDao;
	
	@Autowired
	CommonDao commonDao;
	
	@Autowired 
	RgBuildProcessDao rgBuildProcessDao;
	
	@Value("${mdm.scheduler}")
	private String schedulerFlag;
	
	public static Logger logger = LoggerFactory.getLogger(RgScheduler.class);
	
	@Scheduled(fixedRate = 3000)
	public void rgBuildSchedules() {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if("true".equalsIgnoreCase(schedulerFlag) && "Y".equalsIgnoreCase(rgBuildProcessDao.getRgBuildStatus())) {
				List<RgBuildsVb> BuildHeaderlst = rgSchedulerDao.getRgBuildsSchedulesHeader();
				RgBuildsVb rgBuildsVb = new RgBuildsVb();
				if(BuildHeaderlst != null && BuildHeaderlst.size() > 0) {
					rgBuildsVb = BuildHeaderlst.get(0);
					int retVal = rgSchedulerDao.updateRgBuildSchedulesHeader(rgBuildsVb,"I");
					
					rgBuildsVb = BuildHeaderlst.get(0);
					List<RgBuildsVb> Buildlst = rgSchedulerDao.getRgBuildsSchedules(rgBuildsVb);
					if(Buildlst != null && Buildlst.size() > 0) {
						String execsPath = commonDao.findVisionVariableValue("RG_BUILD_PATH");
						String jarName = commonDao.findVisionVariableValue("RG_BUILD_JAR_NAME");
						String logPath = commonDao.findVisionVariableValue("RG_BUILDLOG_PATH");
						
						if(!ValidationUtil.isValid(execsPath)) 
							logger.error("RG Build Executable path not configured in Vision_Variables[RG_BUILD_PATH]");
						
						if(!ValidationUtil.isValid(jarName)) 
							logger.error("RG Build Jar Name not configured in Vision_Variables[RG_BUILD_JAR_NAME]");
						
						if(!ValidationUtil.isValid(logPath)) 
							logger.error("RG Build Log path not configured in Vision_Variables[RG_BUILDLOG_PATH]");
						
						LocalDate today = LocalDate.now();
						
						for(RgBuildsVb vObject : Buildlst) {
							String logFileName = vObject.getBuildId()+"_"+vObject.getCountry()+vObject.getLeBook()+vObject.getYear()+vObject.getDataSource()+rgBuildsVb.getSequence()+"_"+today+".log";
							String logFile = logPath+logFileName;
							vObject.setLogFile(logFileName);
							
							retVal = rgSchedulerDao.updateRgBuildSchedules(vObject,"I"); 
							logger.info("Proces start for the Build ["+vObject.getProgram()+"] Country["+vObject.getCountry()+"] "
									+ "LE Book["+vObject.getLeBook()+"] Year["+vObject.getYear()+"]Data Source["+vObject.getDataSource()+"]");
							
							if(ValidationUtil.isValid(vObject.getDependentProgram())) {
								logger.info("Checking Dependency");
								int depCompCount = rgSchedulerDao.getDependencyStatus(vObject);
								if(depCompCount == 0) {
									logger.info("Dependency Errored");
									logger.info("Dependent Build is not completed");
									retVal = rgSchedulerDao.updateRgBuildSchedules(vObject,"E"); 
									continue;
								}
							}
							
							Process proc;
							
							String jarExecCmd = "java -jar " + execsPath + jarName + " "+vObject.getCountry()+" "+vObject.getLeBook()+" "+logFile+" "+vObject.getBuildId()+" ";
							logger.info(jarExecCmd);
							
							proc = Runtime.getRuntime().exec(jarExecCmd);
							
							//KE 01 2017 B FC_CALC_STG C:\\App\\Rg_BuildLogs\\FC_CALC_STG_KE012017B
							proc.waitFor();
							
							logger.info("Proces End for the Build ["+vObject.getBuildId()+"] Country["+vObject.getCountry()+"] "
									+ "LE Book["+vObject.getLeBook()+"] Year["+vObject.getYear()+"]Data Source["+vObject.getDataSource()+"]");
							
							InputStream in = proc.getInputStream();
							InputStream err = proc.getErrorStream();
							int exitVal = proc.exitValue();
							
							logger.info("Exit Value from Jar["+exitVal+"]");
							
							String returnStatus = "";
							if(exitVal == Constants.SUCCESSFUL_OPERATION)
								returnStatus = "C";
							else
								returnStatus = "E";
							
							logger.info("Updating Rg Build Schedule status");
							retVal = rgSchedulerDao.updateRgBuildSchedules(vObject,returnStatus); 
							if(retVal != Constants.SUCCESSFUL_OPERATION) {
								logger.error("Error while updating Status on completion/Errored");
							}
							
							logger.info("Updating Rg Program Last Run Date");
							retVal = rgSchedulerDao.updateLastRunStatus(vObject,returnStatus); 
							if(retVal != Constants.SUCCESSFUL_OPERATION) {
								logger.error("Error while updating BNf Programs Table on completion/Errored");
							}
							
							logger.info("move Rg build schedules to history table");
							retVal = rgSchedulerDao.moveScheduleToHistory(vObject);
							if(retVal != Constants.SUCCESSFUL_OPERATION) {
								logger.error("Error while moving Rg schedules to History");
							}
						}
					}
					logger.info("Removing record from Rg Build Schedule Header ");
					retVal = rgSchedulerDao.deleteScheduleHeader(rgBuildsVb);
					if(retVal != Constants.SUCCESSFUL_OPERATION) {
						logger.error("Error deleting Rg Schedule Header record");
					}
				}
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
	}
	
}