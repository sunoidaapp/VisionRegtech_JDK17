package com.vision.wb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.AdfControlsDao;
import com.vision.dao.AdfSchedulesDao;
import com.vision.dao.AdfSchedulesDetailsDao;
import com.vision.dao.BuildSchedulesDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ReportWriterDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.util.ZipUtils;
import com.vision.vb.AdfSchedulesDetailsVb;
import com.vision.vb.AdfSchedulesVb;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.BuildSchedulesVb;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.CronStatusVb;
import com.vision.vb.FileInfoVb;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.ReportsWriterVb;

@Component
public class AdfSchedulesWb extends AbstractDynaWorkerBean<AdfSchedulesVb> {

	@Autowired
	private AdfSchedulesDao adfSchedulesDao;

	@Autowired
	private AdfSchedulesDetailsDao adfSchedulesDetailsDao;

	@Autowired
	private AdfControlsDao adfControlsDao;

	@Autowired
	private ReportWriterDao reportWriterDao;
	@Autowired
	private BuildSchedulesDao buildSchedulesDao;

	// public static Logger logger = LoggerFactory.getLogger(AdfSchedulesWb.class);
	// public static Logger logger =Logger.getLoggerFactory(AdfSchedulesWb.class)

	@Value("${ftp.serverType}")
	private String serverType;

	@Value("${ftp.hostName}")
	private String hostName;

	@Value("${ftp.prompt}")
	private char prompt;

	@Value("${ftp.userName}")
	private String userName;

	@Value("${ftp.password}")
	private String password;

	@Value("${ftp.securedFtp}")
	private boolean securedFtp;

	@Value("${ftp.knownHostsFileName}")
	private String knownHostsFileName;

	@Value("${ftp.processDir}")
	private String processDir;

	@Value("${ftp.scriptDir}")
	private String scriptsProcessDir = "/home/vision/scripts";

	private String cbDir;
	private String uploadDir;
	private String downloadDir;
	private String buildLogsDir;
	private String timezoneId;
	private String scriptDir;
	private int uploadFileChkIntervel = 30;
	private static final int DIR_TYPE_DOWNLOAD = 1;
	private static final String SERVICE_NAME = "Error Log";

	// variables for upload &download using ftp and sftp
	private String currentNode;

	@Autowired
	private CommonDao commonDao;

	@Override
	protected AbstractDao<AdfSchedulesVb> getScreenDao() {
		return adfSchedulesDao;
	}

	@Override
	protected void setAtNtValues(AdfSchedulesVb object) {
		object.setBuildScheduleStatusAt(206);
		object.setRecurringFrequencyAt(213);
	}

	@Override
	protected void setVerifReqDeleteType(AdfSchedulesVb object) {
		object.setVerificationRequired(false);
	}

	public ArrayList getPageLoadValues(AdfSchedulesVb lAdfSchedulesVb) {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(206);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(213);
			arrListLocal.add(collTemp);
			setVerifReqDeleteType(lAdfSchedulesVb);
			ExceptionCode exception = getQueryResults(lAdfSchedulesVb);
			arrListLocal.add(exception);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(211);
			arrListLocal.add(collTemp);
//			arrListLocal = getProgramsDao().getMajorBuildList();
			collTemp = getAdfSchedulesDetailsDao().getMajorBuildList();
			arrListLocal.add(collTemp);
			collTemp = getAdfSchedulesDetailsDao().findActiveAlphaSubTabsByAlphaTab(1084);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1077);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1079);
			arrListLocal.add(collTemp);
			collTemp = getAdfSchedulesDetailsDao().findActiveAlphaSubTabsByAlphaTabOrderBy(1204);
			arrListLocal.add(collTemp);
			String logDataStatus = getCommonDao().findVisionVariableValue("ADF_SCHEDULE_ALLOW_LOG_DL_IF");
			arrListLocal.add(logDataStatus);
			String reportDataStatus = getCommonDao().findVisionVariableValue("ADF_SCHEDULE_ALLOW_REPORT_IF");
			arrListLocal.add(reportDataStatus);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1074);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1084);
			arrListLocal.add(collTemp);
			collTemp = getAdfSchedulesDetailsDao().findActiveAlphaSubTabsByAlphaTab(1084);
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	public ExceptionCode getQueryResults(AdfSchedulesVb vObject) {
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<AdfSchedulesVb> collTemp = adfSchedulesDao.getQueryResults(vObject, intStatus);
		List<AdfSchedulesVb> collTemp1 = adfSchedulesDao.getQueryResultsForStatusBars(vObject, intStatus);
		String environmentParam = "UAT";
//				System.getenv("VISION_SERVER_ENVIRONMENT");
		if (!ValidationUtil.isValid(environmentParam))
			environmentParam = "UAT";
		String cronNameDelimited = "";
		String cronStatusDelimited = "";
		List<AdfSchedulesVb> CronName = adfSchedulesDao.findCronName(vObject, environmentParam);
		List<AdfSchedulesVb> CronStatus = adfSchedulesDao.findCronStatus(vObject, environmentParam);
		if (CronName != null) {
			for (int i = 0; i < CronName.size(); i++) {
				if (ValidationUtil.isValid(cronNameDelimited))
					cronNameDelimited = cronNameDelimited + "," + CronName.get(i).getCronName();
				else
					cronNameDelimited = cronNameDelimited + CronName.get(i).getCronName();
				if (ValidationUtil.isValid(cronStatusDelimited))
					cronStatusDelimited = cronStatusDelimited + "," + CronStatus.get(i).getCronStatus();
				else
					cronStatusDelimited = cronStatusDelimited + CronStatus.get(i).getCronStatus();
			}
		}
		String cronStatusFromDB = getVisionVariableValue("VISION_BUILD_CRON");
		if (cronStatusFromDB == null || cronStatusFromDB.isEmpty()) {
			cronStatusFromDB = "Stopped";
		}
		if (!("STOPPED".equalsIgnoreCase(cronStatusFromDB) || "Stopping".equalsIgnoreCase(cronStatusFromDB))) {
			if (adfSchedulesDao.getLastFetchIntervel() > 2) {
				cronStatusFromDB = "<font color='#5F04B4'><b>Not Responding</b></font>";
			} else {
				cronStatusFromDB = "<font color='green'><b>Running</b></font>";
			}
		} else if ("STOPPED".equalsIgnoreCase(cronStatusFromDB)) {
			cronStatusFromDB = "<font color='red'><b>Stopped</b></font>";
		} else if ("Stopping".equalsIgnoreCase(cronStatusFromDB)) {
			cronStatusFromDB = "<font color='#FF8000'><b>Stopping</b></font>";
		}
		String dbDateTime = getAdfSchedulesDetailsDao().getSystemDate();
		dbDateTime = dbDateTime.replace('/', '-');
		vObject.setDbDateTime(dbDateTime);
		vObject.setCronName(cronNameDelimited);
		vObject.setCronStatus(cronStatusDelimited);
		vObject.setCronCount(adfSchedulesDao.findCronCount(environmentParam));
		vObject.setSubmitterId(SessionContextHolder.getContext().getVisionId());
		// setLogInformation(collTemp);
		ExceptionCode exceptionCode = CommonUtils.getResultObject(adfSchedulesDao.getServiceName(),
				Constants.SUCCESSFUL_OPERATION, "Query", "");
		exceptionCode.setOtherInfo(vObject);
		exceptionCode.setResponse(collTemp);
		List<AlphaSubTabVb> collTempDesc = getAdfSchedulesDetailsDao().findActiveAlphaSubTabsByAlphaTabOrderBy(1204);
		List<AdfSchedulesVb> collTempStatus = new ArrayList<AdfSchedulesVb>();
		if (collTemp1 != null && collTempDesc != null) {
			for (AlphaSubTabVb vObj : collTempDesc) {
				AdfSchedulesVb adfSchedulesVb = new AdfSchedulesVb();
				boolean avail = false;
				for (AdfSchedulesVb vObj1 : collTemp1) {
					if (vObj1.getStatusDesc().equalsIgnoreCase(vObj.getDescription())) {
						avail = true;
						adfSchedulesVb.setParallelProcsCount(vObj1.getParallelProcsCount());
					}
				}
				if (avail == false) {
					adfSchedulesVb.setParallelProcsCount(0);
				}
				adfSchedulesVb.setAdfScheduleStatus(vObj.getAlphaSubTab());
				adfSchedulesVb.setStatusDesc(vObj.getDescription());
				collTempStatus.add(adfSchedulesVb);
			}
		}
		exceptionCode.setResponse1(collTempStatus);
		return exceptionCode;
	}

	public ExceptionCode getBuildScheduleDetails(AdfSchedulesVb vObject) {
		ExceptionCode exceptionCode = null;
		AdfSchedulesVb vObjectTemp;
		vObjectTemp = adfSchedulesDao.getQueryResultsForDetails(vObject.getAdfNumber());
		if (vObjectTemp == null) {
			exceptionCode = CommonUtils.getResultObject("ADF Schedules", Constants.WE_HAVE_WARNING_DESCRIPTION, "Query",
					"Build has completed successfully.");
			exceptionCode.setOtherInfo(vObject);
		} else {
			List<AdfSchedulesDetailsVb> collTemp = getAdfSchedulesDetailsDao()
					.getQueryDisplayResults(vObject.getAdfNumber());
			/*
			 * ProgramsVb programsVb =
			 * getProgramsDao().getSupportContactDetails(vObjectTemp.getMajorBuild());
			 */
			vObjectTemp.setSupportContactNumber("0123456789");
			exceptionCode = CommonUtils.getResultObject(adfSchedulesDao.getServiceName(),
					Constants.SUCCESSFUL_OPERATION, "Query", "");
			vObjectTemp.setAdfSchedulesDetailsList(collTemp);
			exceptionCode.setOtherInfo(vObjectTemp);
		}
		return exceptionCode;
	}

	public ExceptionCode insertBuildSchedules(AdfSchedulesVb pAdfSchedulesVb, String operation) {

		ExceptionCode exceptionCode = null;
		try {
			long adfNumber = adfSchedulesDao.getMaxAdfNumber();
			adfNumber++;
			if ("Add".equalsIgnoreCase(operation)) {
				pAdfSchedulesVb.setAdfNumber(String.valueOf(adfNumber));
				pAdfSchedulesVb.setParallelProcsCount(1);
				pAdfSchedulesVb.setNotify("Y");
				pAdfSchedulesVb.setFrequencyProcess(pAdfSchedulesVb.getFrequencyProcess());
				pAdfSchedulesVb.setRecurringFrequency("N");
				pAdfSchedulesVb.setSupportContact("vision.support@kdic.go.ke");
				pAdfSchedulesVb.setAdfScheduleStatus("P");
				exceptionCode = adfSchedulesDao.doInsertApprRecord(pAdfSchedulesVb);
			} else {
				exceptionCode = adfSchedulesDao.doUpdateApprRecord(pAdfSchedulesVb);
			}
			exceptionCode.setResponse(pAdfSchedulesVb.getAdfControlsList());
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			if ("Add".equalsIgnoreCase(operation)) {
				adfSchedulesDao.doDeleteAppr(pAdfSchedulesVb);
			}
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(pAdfSchedulesVb.getAdfControlsList());
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		}
	}

	public ExceptionCode transfer(ExceptionCode pExceptionCode, List<AdfSchedulesVb> pBuildSchedules,
			String updateNode) {
		ExceptionCode exceptionCode = null;
		AdfSchedulesVb pAdfSchedulesVb = (AdfSchedulesVb) pExceptionCode.getOtherInfo();
		try {
			exceptionCode = adfSchedulesDao.transfer(pBuildSchedules, updateNode);
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		}
	}

	public ExceptionCode reInitiate(ExceptionCode pExceptionCode, List<AdfSchedulesVb> pBuildSchedules) {
		ExceptionCode exceptionCode = null;
		AdfSchedulesVb pAdfSchedulesVb = (AdfSchedulesVb) pExceptionCode.getOtherInfo();
		try {
			exceptionCode = adfSchedulesDao.reInitiate(pBuildSchedules);
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		}
	}

	public ExceptionCode reInitiateForParticularStatus(ExceptionCode pExceptionCode,
			List<AdfSchedulesVb> pBuildSchedules, String reInitiateStatus) {
		ExceptionCode exceptionCode = null;
		AdfSchedulesVb pAdfSchedulesVb = (AdfSchedulesVb) pExceptionCode.getOtherInfo();
		try {
			exceptionCode = adfSchedulesDao.reInitiateForParticularStatus(pBuildSchedules, reInitiateStatus);
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		}
	}

	public ExceptionCode terminateBuild(ExceptionCode pExceptionCode, List<AdfSchedulesVb> pBuildSchedules) {
		ExceptionCode exceptionCode = null;
		TelnetConnection telnetConnection = null;
		AdfSchedulesVb pAdfSchedulesVb = (AdfSchedulesVb) pExceptionCode.getOtherInfo();
		try {
			setProcessDirFromDB();
			if (isSecuredFtp()) {
				return terminateSecuredAdf(pExceptionCode, pBuildSchedules);
			}
			String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if (!ValidationUtil.isValid(environmentParam))
				environmentParam = "UAT";
			List<AdfSchedulesVb> lBuildSchList = new ArrayList<AdfSchedulesVb>(pBuildSchedules);
			for (AdfSchedulesVb lAdfSchedulesVb : lBuildSchList) {
				if (lAdfSchedulesVb.isChecked()) {
					String node = lAdfSchedulesVb.getNode();
					String accHostName = adfSchedulesDao.getServerCredentials(environmentParam, node, "NODE_IP");
					telnetConnection = new TelnetConnection(accHostName, userName, password, processDir, prompt);
					telnetConnection.connect();
					if ("ADF".equalsIgnoreCase(lAdfSchedulesVb.getAcquisitionProcessType())) {
						telnetConnection.killAdf(lAdfSchedulesVb.getAdfNumber());
					} else if ("XLS".equalsIgnoreCase(lAdfSchedulesVb.getAcquisitionProcessType())) {
						telnetConnection.killNonAdf(lAdfSchedulesVb.getAdfNumber());
					} else if ("RTT".equalsIgnoreCase(lAdfSchedulesVb.getAcquisitionProcessType())) {
						telnetConnection.killRealTime(lAdfSchedulesVb.getAdfNumber());
					}
				}
			}
			Thread.sleep(5000);
		} catch (ConnectException E) {
			E.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("ADF Schedules", Constants.ERRONEOUS_OPERATION,
					"ADF Killbuild bild", E.getMessage());
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		} catch (Exception rex) {
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("ADF Schedules", Constants.SUCCESSFUL_OPERATION,
					"ADF Kill bild", "");
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		} finally {
			if (telnetConnection != null && telnetConnection.isConnected()) {
				telnetConnection.disconnect();
				telnetConnection = null;
			}
		}
		return getQueryResults(pAdfSchedulesVb);
	}

	private ExceptionCode terminateSecuredAdf(ExceptionCode pExceptionCode, List<AdfSchedulesVb> pBuildSchedules) {
		ExceptionCode exceptionCode = null;
		AdfSchedulesVb pAdfSchedulesVb = (AdfSchedulesVb) pExceptionCode.getOtherInfo();
		Session session = null;
		Channel channel = null;
		try {
			JSch jsch = new JSch();
			String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if (!ValidationUtil.isValid(environmentParam))
				environmentParam = "UAT";
			List<AdfSchedulesVb> lBuildSchList = new ArrayList<AdfSchedulesVb>(pBuildSchedules);
			for (AdfSchedulesVb lAdfSchedulesVb : lBuildSchList) {
				/*
				 * if(lAdfSchedulesVb.isChecked()){ String node=lAdfSchedulesVb.getNode();
				 * String accHostName =adfSchedulesDao.getServerCredentials( environmentParam,
				 * node, "NODE_IP"); String userName =adfSchedulesDao.getServerCredentials(
				 * environmentParam, node, "NODE_USER"); String passWord
				 * =adfSchedulesDao.getServerCredentials( environmentParam, node, "NODE_PWD");
				 * session = jsch.getSession(userName, accHostName, 22); java.util.Properties
				 * config = new java.util.Properties(); config.put("StrictHostKeyChecking",
				 * "no"); session.setConfig(config); session.setPassword(passWord);
				 * session.connect(); channel = session.openChannel("shell"); OutputStream
				 * inputstream_for_the_channel = channel.getOutputStream(); PrintStream
				 * commander = new PrintStream(inputstream_for_the_channel, true);
				 * channel.connect(); commander.println("cd "+processDir);
				 * if("ADF".equalsIgnoreCase(lAdfSchedulesVb.getAcquisitionProcessType()))
				 * commander.println("adfKillbuild "+lAdfSchedulesVb.getAdfNumber()+";"); else
				 * if("XLS".equalsIgnoreCase(lAdfSchedulesVb.getAcquisitionProcessType()))
				 * commander.println("nonADFKillbuild "+lAdfSchedulesVb.getAdfNumber()+";");
				 * else if("RTT".equalsIgnoreCase(lAdfSchedulesVb.getAcquisitionProcessType()))
				 * commander.println("realTimeFTKillbuild "+lAdfSchedulesVb.getAdfNumber()+";");
				 * 
				 * commander.println("exit"); commander.close(); do {Thread.sleep(1000); }
				 * while(!channel.isEOF()); }
				 */
				if (lAdfSchedulesVb.isChecked()) {
					String node = lAdfSchedulesVb.getNode();
					String accHostName = adfSchedulesDao.getServerCredentials(environmentParam, node, "NODE_IP");
					String userName = adfSchedulesDao.getServerCredentials(environmentParam, node, "NODE_USER");
					String passWord = adfSchedulesDao.getServerCredentials(environmentParam, node, "NODE_PWD");
					String serverName = adfSchedulesDao.getServerCredentials(environmentParam, node, "SERVER_NAME");
					CronStatusVb cronStatusVb = new CronStatusVb();
					cronStatusVb.setServerEnvironment(environmentParam);
					cronStatusVb.setNodeName(node);
					cronStatusVb.setNodeIp(accHostName);
					cronStatusVb.setNodeUser(userName);
					cronStatusVb.setNodePwd(passWord);
					cronStatusVb.setServerName(serverName);
					cronStatusVb.setCronName("TERMINATE");
					int retVal=adfSchedulesDao.doInsertionAudit(cronStatusVb, "TERMINATE");
					if(retVal != Constants.ERRONEOUS_OPERATION)
					adfSchedulesDao.doUpdateScheduleStatus(lAdfSchedulesVb);
				}
				/* do {Thread.sleep(1000); } while(!channel.isEOF()); */
			}
		}
		/*
		 * catch(ConnectException E){ E.printStackTrace(); exceptionCode =
		 * CommonUtils.getResultObject("ADF Schedules", Constants.ERRONEOUS_OPERATION,
		 * "ADF Kill bild", E.getMessage()); exceptionCode.setResponse(pBuildSchedules);
		 * exceptionCode.setOtherInfo(pAdfSchedulesVb); return exceptionCode; }
		 * 
		 */
		catch (Exception rex) {
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("ADF Schedules", Constants.ERRONEOUS_OPERATION, "ADF Kill bild",
					rex.getMessage());
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
				channel = null;
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
				session = null;
			}
		}
		return getQueryResults(pAdfSchedulesVb);
	}

	private ExceptionCode startSecuredCron(AdfSchedulesVb vObject) {
		ExceptionCode exceptionCode = null;
		Session session = null;
		Channel channel = null;
		try {
			JSch jsch = new JSch();
			String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if (!ValidationUtil.isValid(environmentParam))
				environmentParam = "UAT";
			String accHostName = adfSchedulesDao.getServerCredentials(environmentParam, vObject.getStartCronNode(),
					"NODE_IP");
			session = jsch.getSession(getUserName(), accHostName, 22);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(getPassword());
			session.connect();
			channel = session.openChannel("shell");
			OutputStream inputstream_for_the_channel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			channel.connect();
			commander.println("cd " + scriptsProcessDir);
			commander.println("startADFCron.sh");
			commander.println("exit");
			commander.close();
			do {
				Thread.sleep(1000);
			} while (!channel.isEOF());
			Thread.sleep(1000);
		} catch (Exception rex) {
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.ERRONEOUS_OPERATION, "Start Cron",
					"");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
				channel = null;
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
				session = null;
			}
		}
		return getQueryResults(vObject);
	}

	public ExceptionCode startCron(AdfSchedulesVb vObject) {
		setProcessDirFromDB();
		ExceptionCode exceptionCode = null;
		TelnetConnection telnetConnection = null;
		if (isSecuredFtp()) {
			return startSecuredCron(vObject);
		} else {
			try {
				String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
				if (!ValidationUtil.isValid(environmentParam))
					environmentParam = "UAT";
				String accHostName = adfSchedulesDao.getServerCredentials(environmentParam, vObject.getStartCronNode(),
						"NODE_IP");
				telnetConnection = new TelnetConnection(accHostName, userName, password, scriptsProcessDir, prompt);
				telnetConnection.connect();
				telnetConnection.startCronAdf();
				Thread.sleep(5000);
			} catch (Exception rex) {
				rex.printStackTrace();
				exceptionCode = CommonUtils.getResultObject("Adf Schedules", Constants.ERRONEOUS_OPERATION,
						"Start Cron", rex.getMessage());
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			} finally {
				if (telnetConnection != null && telnetConnection.isConnected()) {
					telnetConnection.disconnect();
					telnetConnection = null;
				}
			}
		}
		return getQueryResults(vObject);
	}

	private void setProcessDirFromDB() {
		String uploadLogFilePathFromDB = getVisionVariableValue("BUILDCRON_EXECUTABLES_PATH");
		if (uploadLogFilePathFromDB != null && !uploadLogFilePathFromDB.isEmpty()) {
			processDir = uploadLogFilePathFromDB;
		}
	}

	public AdfSchedulesDetailsDao getAdfSchedulesDetailsDao() {
		return adfSchedulesDetailsDao;
	}

	public void setAdfSchedulesDetailsDao(AdfSchedulesDetailsDao adfSchedulesDetailsDao) {
		this.adfSchedulesDetailsDao = adfSchedulesDetailsDao;
	}

	public AdfControlsDao getAdfControlsDao() {
		return adfControlsDao;
	}

	public void setAdfControlsDao(AdfControlsDao adfControlsDao) {
		this.adfControlsDao = adfControlsDao;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSecuredFtp() {
		return securedFtp;
	}

	public void setSecuredFtp(boolean securedFtp) {
		this.securedFtp = securedFtp;
	}

	public String getProcessDir() {
		return processDir;
	}

	public void setProcessDir(String processDir) {
		this.processDir = processDir;
	}

	public char getPrompt() {
		return prompt;
	}

	public void setPrompt(char prompt) {
		this.prompt = prompt;
	}

	public ExceptionCode listFilesFromFtpServer(int dirType, String orderBy, String currentNodeName, String stkHolder) {
		ExceptionCode exceptionCode = null;
		TelnetConnection telnetConnection = null;
		String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
		if (!ValidationUtil.isValid(environmentParam))
			environmentParam = "UAT";
		try {

			String accHostName = adfSchedulesDao.getServerCredentials(environmentParam, currentNodeName, "NODE_IP");
			setUploadDownloadDirFromDB();
			if ("WINDOWS".equalsIgnoreCase(serverType)) {
				return listFilesFromFtpWindowServer(dirType, orderBy);
			}
			if (isSecuredFtp()) {
				return listFilesFromSFtpServer(dirType, orderBy, stkHolder, currentNodeName);
			}
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			telnetConnection = new TelnetConnection(accHostName, userName, password, prompt);
			// telnetConnection.connect(getServerType());
			telnetConnection.connect();
			if (dirType == DIR_TYPE_DOWNLOAD)
				telnetConnection.sendCommand("cd " + downloadDir);
			else
				telnetConnection.sendCommand("cd " + buildLogsDir);
			String responseStr = telnetConnection.sendCommand("ls -ltc ");
			String[] fileEntryArray = responseStr.split("\r\n");
			FTPClientConfig conf = new FTPClientConfig(getServerType());
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			List<String> fileEntryList = unixFtpEntryParser
					.preParse(new LinkedList<String>(Arrays.asList(fileEntryArray)));
			List<FTPFile> lfiles = new ArrayList<FTPFile>(fileEntryList.size());
			for (String fileEntry : fileEntryList) {
				FTPFile ftpFile = unixFtpEntryParser.parseFTPEntry(fileEntry);
				if (ftpFile != null)
					lfiles.add(ftpFile);
			}
			for (FTPFile fileName : lfiles) {
				if (fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar"))
					continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
				if (dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD")
						&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "_") > 0)) {
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
							+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
					lFileList.add(fileInfoVb);
				} else if (dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON")
						&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") <= 0)) {
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
							+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
					lFileList.add(fileInfoVb);
				} else if ((dirType == 2 || dirType == 1)
						&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") > 0)) {
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
			exceptionCode.setResponse(createParentChildRelations(lFileList, orderBy, dirType, stkHolder));
		} catch (FileNotFoundException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			;
			throw new RuntimeCustomException(exceptionCode);
		} catch (IOException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			;
			throw new RuntimeCustomException(exceptionCode);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			;
			throw new RuntimeCustomException(exceptionCode);
		} finally {
			if (telnetConnection != null && telnetConnection.isConnected()) {
				telnetConnection.disconnect();
				telnetConnection = null;
			}
		}
		return exceptionCode;
	}

	private String formatFileSize(long numSize) {
		String strReturn;
		BigDecimal lSize = BigDecimal.valueOf((numSize)).divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
		if (lSize.floatValue() <= 0) {
			lSize = BigDecimal.valueOf(1);
		}
		strReturn = lSize + " KB";
		if (lSize.floatValue() > 1024) {
			lSize = lSize.divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
			strReturn = lSize + " MB";
			if (lSize.floatValue() > 1024) {
				lSize = lSize.divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
				strReturn = lSize + " GB";
			}
		}
		return strReturn;
	}

	public ExceptionCode listFilesFromSFtpServer(int dirType, String orderBy, String stkHolder,
			String currentNodeName) {
		ExceptionCode exceptionCode = null;
		try {
			setUploadDownloadDirFromDB();
			String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if (!ValidationUtil.isValid(environmentParam))
				environmentParam = "UAT";
			String accHostName = adfSchedulesDao.getServerCredentials(environmentParam, currentNodeName, "NODE_IP");
			String userName = adfSchedulesDao.getServerCredentials(environmentParam, currentNodeName, "NODE_USER");
			String password = adfSchedulesDao.getServerCredentials(environmentParam, currentNodeName, "NODE_PWD");

			if (!ValidationUtil.isValid(accHostName)) {
				accHostName = getHostName();
			}
			if (!ValidationUtil.isValid(userName)) {
				userName = getUserName();
			}
			if (!ValidationUtil.isValid(password)) {
				password = getPassword();
			}
			System.out.println("Host : " + accHostName);
			System.out.println("User Name : " + userName);
//			System.out.println("Pwd : "+password);

			System.out.println("downloadDir : " + downloadDir + " buildLogsDir : " + buildLogsDir
					+ " DIR_TYPE_DOWNLOAD : " + DIR_TYPE_DOWNLOAD);
			JSch jsch = new JSch();
			// jsch.setKnownHosts( knownHostsFileName );
			Session session = jsch.getSession(userName, accHostName);
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(password);
			}
//			session.setPassword(password);
			System.out.println("knownHostsFileName : " + knownHostsFileName);
			System.out.println("Checking Connection ...................... ");
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			// sftpChannel.cd(downloadDir);
			if (dirType == DIR_TYPE_DOWNLOAD) {
				sftpChannel.cd(downloadDir);
			} else {
				sftpChannel.cd(buildLogsDir);
			}

			Vector<ChannelSftp.LsEntry> vtc = sftpChannel.ls("*.*");
			System.out.println("File Size : " + vtc.size());
			sftpChannel.disconnect();
			session.disconnect();
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			FTPClientConfig conf = new FTPClientConfig(getServerType());
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			// List<FTPFile> lfiles = new ArrayList<FTPFile>(vtc.size());
			List<String> lfiles = new ArrayList<String>(vtc.size());
			/*
			 * for(ChannelSftp.LsEntry lsEntry : vtc) { FTPFile file=
			 * unixFtpEntryParser.parseFTPEntry(lsEntry.getLongname()); if(file != null)
			 * lfiles.add(file); }
			 */
			for (ChannelSftp.LsEntry lsEntry : vtc) {
//				System.out.println("lsEntry.getFilename() : "+lsEntry.getFilename());
				// FTPFile file= unixFtpEntryParser.parseFTPEntry(lsEntry.getLongname());
//				System.out.println("file : "+file);
//				if(file != null)
				lfiles.add(lsEntry.getFilename());
			}

			System.out.println("lfiles.size " + lfiles.size());
			for (String fileName : lfiles) {
				// for(FTPFile fileName:lfiles){

				if (fileName.endsWith(".zip") || fileName.endsWith(".tar"))
					continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName);
//				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
				/*
				 * if(dirType == 1){ fileInfoVb.setDate(formatDate(fileName));
				 * lFileList.add(fileInfoVb); }
				 */
				if (dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD")
						&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "_") > 0)) {
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
							+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
					lFileList.add(fileInfoVb);
				} else if (dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON")
						&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") <= 0)) {
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
							+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
					lFileList.add(fileInfoVb);
				} else if ((dirType == 2 || dirType == 1)
						&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") > 0)) {
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}

			}
			System.out.println("vtc.size() " + vtc.size());
			System.out.println("lFileList " + lFileList.size());

			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
			exceptionCode.setResponse(createParentChildRelations(lFileList, orderBy, dirType, stkHolder));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			;
			throw new RuntimeCustomException(exceptionCode);
		} catch (IOException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			;
			throw new RuntimeCustomException(exceptionCode);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			;
			throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	private void setUploadDownloadDirFromDB() {
		String uploadLogFilePathFromDB = getVisionVariableValue("VISION_XLUPL_LOG_FILE_PATH");
		if (uploadLogFilePathFromDB != null && !uploadLogFilePathFromDB.isEmpty()) {
			downloadDir = uploadLogFilePathFromDB;
		}
		String uploadDataFilePathFromDB = getVisionVariableValue("VISION_XLUPL_DATA_FILE_PATH");
		if (uploadDataFilePathFromDB != null && !uploadDataFilePathFromDB.isEmpty()) {
			uploadDir = uploadDataFilePathFromDB;
		}
		String buildLogsFilePathFromDB = getVisionVariableValue("JAVA_BUILDCRON_LOG_FILE_PATH");
		if (buildLogsFilePathFromDB != null && !buildLogsFilePathFromDB.isEmpty()) {
			buildLogsDir = buildLogsFilePathFromDB;
		}
		String scriptPathFromDB = getVisionVariableValue("BUILDCRON_SCRIPTS_PATH");
		if (scriptPathFromDB != null && !scriptPathFromDB.isEmpty()) {
			scriptDir = scriptPathFromDB;
		}
		String uploadFileChkIntervelFromDB = getVisionVariableValue("VISION_XLUPL_FILE_CHK_INTVL");
		if (uploadFileChkIntervelFromDB != null && !uploadFileChkIntervelFromDB.isEmpty()) {
			if (ValidationUtil.isValidId(uploadFileChkIntervelFromDB))
				uploadFileChkIntervel = Integer.valueOf(uploadFileChkIntervelFromDB);
		}
		String uploadDataFilePathFromCB = getVisionVariableValue("VISION_XLCB_DATA_FILE_PATH");
		if (uploadDataFilePathFromCB != null && !uploadDataFilePathFromCB.isEmpty()) {
			cbDir = uploadDataFilePathFromCB;
		}

	}

	private String formatDate1(FTPFile fileName) {
		String fileName1 = fileName.getName();
		String year = fileName1.substring(fileName1.length() - 14, fileName1.length() - 10);
		String month = fileName1.substring(fileName1.length() - 9, fileName1.length() - 7);
		String day = fileName1.substring(fileName1.length() - 6, fileName1.length() - 4);
		return CommonUtils.getFixedLength(day, "0", 2) + "-" + CommonUtils.getFixedLength(month, "0", 2) + "-" + year;
	}


private static String formatDate1(String fileName) {
		String fileName1 = fileName;
		String year = fileName1.substring(fileName1.length()-8 , fileName1.length() - 4);
		String month = fileName1.substring(fileName1.length() - 11, fileName1.length() - 9);
		String day = fileName1.substring(fileName1.length() - 14, fileName1.length() - 12);
		return CommonUtils.getFixedLength(day, "0", 2) + "-" + CommonUtils.getFixedLength(month, "0", 2) + "-" + year;
	}

	private String formatDate(String fileName) {
		String fileName1 = fileName;
		String year = fileName1.substring(fileName1.length() - 8, fileName1.length() - 4);
		String month = fileName1.substring(fileName1.length() - 11, fileName1.length() - 9);
		String day = fileName1.substring(fileName1.length() - 14, fileName1.length() - 12);
		return CommonUtils.getFixedLength(day, "0", 2) + "-" + CommonUtils.getFixedLength(month, "0", 2) + "-" + year;
	}

	private List<FileInfoVb> createParentChildRelations(List<FileInfoVb> legalTreeList, String orderBy, int dirType,
			String stkHolder) throws IOException {
		List<FileInfoVb> lResult = new ArrayList<FileInfoVb>(0);
		Set set = new HashSet<FileInfoVb>();
		// Top Roots are added.
		for (FileInfoVb fileVb : legalTreeList) {
			if ("Date".equalsIgnoreCase(orderBy)) {
				// String date = fileVb.getName().substring(fileVb.getName().length()-14,
				// fileVb.getName().length()-4);
				if (fileVb.getDate() != null && fileVb.getDate() != "") {
					set.add(fileVb.getDate());
				}

			} else {
				String fileNeme = "";
				if (dirType == 2) {
					int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
					if (cout == 0) {
						int cout1 = StringUtils.countOccurrencesOf(fileVb.getName(), "-");
						if (cout1 == 0)
							fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
						else
							fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 15);
					} else if (cout > 1)
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 21);
				} else {
					int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
					if (cout == 1) {
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
					} else if (cout > 1) {
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 20);
					}
				}
				if (fileNeme != null && fileNeme.length() > 0) {
					set.add(fileNeme);
				}
			}
		}
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			String date = (String) iterator.next();
			FileInfoVb fileVb = new FileInfoVb();
			fileVb.setDescription(date);
			lResult.add(fileVb);
		}
		// For each top node add all child's and to that child's add sub child's
		// recursively.
		for (FileInfoVb legalVb : lResult) {
			addChilds(legalVb, legalTreeList, orderBy, dirType, stkHolder);
		}
		if ("Date".equalsIgnoreCase(orderBy)) {
			final SimpleDateFormat dtF = new SimpleDateFormat("dd-MM-yyyy");
			// set the empty lists to null. this is required for UI to display the leaf
			// nodes properly.
			Collections.sort(lResult, new Comparator<FileInfoVb>() {
				public int compare(FileInfoVb m1, FileInfoVb m2) {
					try {
						return dtF.parse(m1.getDescription()).compareTo(dtF.parse(m2.getDescription()));
					} catch (ParseException e) {
						return 0;
					}
				}
			});
			Collections.reverse(lResult);
		} else {
			Collections.sort(lResult, new Comparator<FileInfoVb>() {
				public int compare(FileInfoVb m1, FileInfoVb m2) {
					return m1.getDescription().compareTo(m2.getDescription());
				}
			});
		}
		return lResult;
	}

	public void addChilds(FileInfoVb vObject, List<FileInfoVb> legalTreeListCopy, String orderBy, int dirType,
			String stkHolder) {
		for (FileInfoVb fileTreeVb : legalTreeListCopy) {
			if ("Date".equalsIgnoreCase(orderBy)) {
				// String date =
				// fileTreeVb.getName().substring(fileTreeVb.getName().length()-14,
				// fileTreeVb.getName().length()-4);
				if (vObject.getDescription().equalsIgnoreCase(fileTreeVb.getDate())) {
					String StName = fileTreeVb.getName();
					if (StName.contains(stkHolder)) {
						if (vObject.getChildren() == null) {
							vObject.setChildren(new ArrayList<FileInfoVb>(0));
						}
						fileTreeVb.setDescription(fileTreeVb.getName());
						vObject.getChildren().add(fileTreeVb);
					}
				}
			} else {
				String fileNeme = "";
				if (dirType == 2) {
					int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
					if (cout == 0) {
						int cout1 = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "-");
						if (cout1 == 0)
							fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
						else
							fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 15);
					} else if (cout > 1)
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 21);
				} else {
					int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
					if (cout == 1) {
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
					} else if (cout > 1) {
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 20);
					}
				}
				if (vObject.getDescription().equalsIgnoreCase(fileNeme)) {
					String StName = fileTreeVb.getName();
					if (StName.contains(stkHolder)) {
						if (vObject.getChildren() == null) {
							vObject.setChildren(new ArrayList<FileInfoVb>(0));
						}
						fileTreeVb.setDescription(fileTreeVb.getName());
						vObject.getChildren().add(fileTreeVb);
					}
				}
			}
		}
	}

	public class MyUserInfo implements UserInfo {
		public String getPassword() {
			return password;
		}

		public boolean promptYesNo(String str) {
			return false;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return false;
		}

		public void showMessage(String message) {
			return;
		}
	}

	public String getCbDir() {
		return cbDir;
	}

	public void setCbDir(String cbDir) {
		this.cbDir = cbDir;
	}

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	public String getBuildLogsDir() {
		return buildLogsDir;
	}

	public void setBuildLogsDir(String buildLogsDir) {
		this.buildLogsDir = buildLogsDir;
	}

	public String getTimezoneId() {
		return timezoneId;
	}

	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}

	public String getScriptDir() {
		return scriptDir;
	}

	public void setScriptDir(String scriptDir) {
		this.scriptDir = scriptDir;
	}

	public int getUploadFileChkIntervel() {
		return uploadFileChkIntervel;
	}

	public void setUploadFileChkIntervel(int uploadFileChkIntervel) {
		this.uploadFileChkIntervel = uploadFileChkIntervel;
	}

	public String getScriptsProcessDir() {
		return scriptsProcessDir;
	}

	public void setScriptsProcessDir(String scriptsProcessDir) {
		this.scriptsProcessDir = scriptsProcessDir;
	}

	public ReportWriterDao getReportWriterDao() {
		return reportWriterDao;
	}

	public void setReportWriterDao(ReportWriterDao reportWriterDao) {
		this.reportWriterDao = reportWriterDao;
	}

	public ExceptionCode getAdfListReportData(ReportsWriterVb reportsWriterVb) {
		ExceptionCode exceptionCode = null;
		List<ColumnHeadersVb> columnHeaders = null;
		String reportsStg = "";
		try {
			String sessionId = String.valueOf(System.currentTimeMillis());
			reportsWriterVb.setSessionId(sessionId);
			reportsWriterVb.setReportTitle("Acquisition Status Report");
			System.out.println("List Report Start at: " + reportsWriterVb.getReportId() + " : "
					+ reportsWriterVb.getReportTitle());
			PromptTreeVb promptTree = adfSchedulesDao.callProcToPopulateAdfReportData(reportsWriterVb);
			System.out.println(
					"List Report End at: " + reportsWriterVb.getReportId() + " : " + reportsWriterVb.getReportTitle());
			if (promptTree == null || "1".equalsIgnoreCase(promptTree.getStatus())) {
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceName(), Constants.NO_RECORDS_FOUND,
						"Query", "");
				exceptionCode.setOtherInfo(reportsWriterVb);
				return exceptionCode;
			}
			if (ValidationUtil.isValid(reportsWriterVb.getDrillDownId())) {
				List<ReportsWriterVb> list = getReportWriterDao()
						.getReportSuiteDataByDDReportId(reportsWriterVb.getDrillDownId());
				if (list.size() > 0) {
					reportsWriterVb.setDrilDownList(list);
					reportsWriterVb.setDrillFlag(true);
				} else {
					reportsWriterVb.setDrillFlag(false);
				}
			} else {
				reportsWriterVb.setDrillFlag(false);
			}
			int captoinColumnCount = getReportWriterDao().getCaptionColumnCount(reportsWriterVb);
			reportsWriterVb.setCaptionLabelColCount(captoinColumnCount);
			columnHeaders = getReportWriterDao().getColumnHeaders(promptTree);
			List<String> coloumNames = new ArrayList<String>(columnHeaders.size());
			List<Integer> coloumNumbers = new ArrayList<Integer>(columnHeaders.size());
			for (ColumnHeadersVb colH : columnHeaders) {
				if (colH.getColSpanNum() == 0) {
					if (ValidationUtil.isValid(colH.getDbColumnName())) {
						coloumNumbers.add(colH.getLabelColNum());
					} else {
						coloumNumbers.add(colH.getLabelColNum());
					}
				}
			}
			Collections.sort(coloumNumbers);
			for (int colNumber : coloumNumbers) {
				for (ColumnHeadersVb colH : columnHeaders) {
					if (colNumber == colH.getLabelColNum()) {
						if (ValidationUtil.isValid(colH.getDbColumnName())) {
							if (colH.getColSpanNum() == 0)
								coloumNames.add(colH.getDbColumnName());
						} else {
							if (colH.getColSpanNum() == 0)
								coloumNames.add(colH.getCaption());
						}
					}
				}
			}
			int maxOfRowLevels = getReportWriterDao().getMaxOfRowsInHeader(promptTree);
			int maxOfColumnCount = getReportWriterDao().getMaxOfColumCountInHeader(promptTree);
			reportsWriterVb.setLabelRowCount(maxOfRowLevels);
			reportsWriterVb.setLabelColCount(maxOfColumnCount);
			reportsStg = getReportWriterDao().getListReportDataAsXMLString(promptTree, coloumNames);
			reportsWriterVb.setTotalRows(promptTree.getTotalRows());
			getReportWriterDao().callProcToCleanUpTables(reportsWriterVb);
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceName(), Constants.SUCCESSFUL_OPERATION,
					"Query", "");
			exceptionCode.setRequest(columnHeaders);
			exceptionCode.setOtherInfo(reportsWriterVb);
			exceptionCode.setResponse(reportsStg);
			/*
			 * int retVal =
			 * getReportWriterDao().InsetAuditTrialDataForReports(reportsWriterVb,"");
			 * if(retVal!=Constants.SUCCESSFUL_OPERATION){
			 * logger.error("Error  inserting into rs_Schedule Audit"); }
			 */
			logger.info("List Report Response at: " + reportsWriterVb.getReportId() + " : "
					+ reportsWriterVb.getReportTitle());
		} catch (RuntimeCustomException rex) {
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			logger.error("List Report Exception at: " + reportsWriterVb.getReportId() + " : "
					+ reportsWriterVb.getReportTitle());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(reportsWriterVb);
			exceptionCode.setRequest(columnHeaders);
			exceptionCode.setResponse(reportsStg);
			return exceptionCode;
		}
		return exceptionCode;
	}

	public ExceptionCode downloadFilesFromFTP(String pFileNames, int dirType) {
		ExceptionCode exceptionCode = new ExceptionCode();
		BufferedInputStream bufferedInputStream = null;
		TelnetConnection telnetConnection = null;
		String[] fileNames = pFileNames.split(",");
		setUploadDownloadDirFromDB();
		FTPClient ftpClient = null;
		try {
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
			boolean response = ftpClient.login(getUserName(), getPassword());
			if (!response) {
				ftpClient.disconnect();
				throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (dirType == DIR_TYPE_DOWNLOAD)
				response = ftpClient.changeWorkingDirectory(downloadDir);
			else
				response = ftpClient.changeWorkingDirectory(buildLogsDir);
			if (!response) {
				ftpClient.disconnect();
				throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
			}
			if (fileNames.length == 1) {
				bufferedInputStream = new BufferedInputStream(ftpClient.retrieveFileStream(fileNames[0]));
			} else if (fileNames.length > 1) {
				telnetConnection = new TelnetConnection(hostName, userName, password, prompt);
				telnetConnection.connect();
				if (dirType == DIR_TYPE_DOWNLOAD)
					telnetConnection.sendCommand("cd " + downloadDir);
				else
					telnetConnection.sendCommand("cd " + buildLogsDir);
				// for(int i=0;i<fileNames.length;i++){
				for (int i = 0; i < fileNames.length; i++) {
					telnetConnection.sendCommand("echo " + fileNames[i] + " >> example.txt");
				}
				telnetConnection.sendCommand("tar cvf logs.tar `cat example.txt`");
				telnetConnection.sendCommand("rm example.txt");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				if (dirType == DIR_TYPE_DOWNLOAD)
					response = ftpClient.changeWorkingDirectory(downloadDir);
				else
					response = ftpClient.changeWorkingDirectory(buildLogsDir);
				if (!response) {
					ftpClient.disconnect();
					throw new FTPConnectionClosedException();
				}
				bufferedInputStream = new BufferedInputStream(ftpClient.retrieveFileStream("logs.tar"));
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(bufferedInputStream);
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("File not found on the Server.");
				// throw new FileNotFoundException("File not found on the Server.");
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			e.printStackTrace();
		} finally {
			if (ftpClient != null && ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ftpClient = null;
			}
			if (telnetConnection != null && telnetConnection.isConnected()) {
				telnetConnection.disconnect();
			}
		}
		return exceptionCode;
	}

	public ExceptionCode downloadFilesFromSFTP(String pFileNames, int dirType) {
		ExceptionCode exceptionCode = new ExceptionCode();
		BufferedInputStream bufferedInputStream = null;
		String[] fileNames = pFileNames.split(",");
		System.out.println("pFileNames : " + pFileNames);
		setUploadDownloadDirFromDB();
		Session session = null;
		try {
			JSch jsch = new JSch();
			// jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession(getUserName(), getHostName());
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(getPassword());
			}
			System.out.println("getUserName() " + getUserName());
			System.out.println("getHostName() " + getHostName());
//			System.out.println("getPassword() "+getPassword());

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			System.out.println("session connected");
			Channel channel = session.openChannel("sftp");
			channel.connect();
//			System.out.println("channel connected");
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			if (dirType == DIR_TYPE_DOWNLOAD)
				sftpChannel.cd(downloadDir);
			else
				sftpChannel.cd(buildLogsDir);
//			System.out.println("DIR_TYPE_DOWNLOAD "+DIR_TYPE_DOWNLOAD);

//			System.out.println("buildLogsDir "+buildLogsDir);
//			System.out.println("fileNames[0] : "+fileNames[0]);
			if (fileNames.length == 1) {
				try {
					InputStream ins = sftpChannel.get(fileNames[0]);
					bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (fileNames.length > 1) {
				/*
				 * channel = session.openChannel("shell"); OutputStream
				 * inputstream_for_the_channel = channel.getOutputStream(); PrintStream
				 * commander = new PrintStream(inputstream_for_the_channel, true);
				 * channel.connect(); if (dirType == DIR_TYPE_DOWNLOAD) commander.println("cd "
				 * + downloadDir + "\n"); else commander.println("cd " + buildLogsDir + "\n");
				 * // for(int i=0;i<fileNames.length;i++){ for (int i = 0; i < fileNames.length;
				 * i++) { commander.println("echo " + fileNames[i] + " >> example.txt" + "\n");
				 * } commander.println("tar cvf logs.tar `cat example.txt`" + "\n");
				 * commander.println("rm example.txt"); commander.println("exit");
				 * commander.close(); do { Thread.sleep(1000); } while (!channel.isEOF()); try {
				 * InputStream ins = sftpChannel.get("logs.tar"); bufferedInputStream = new
				 * BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins))); }
				 * catch (Exception ex) { ex.printStackTrace(); }
				 */

				String filePath = System.getProperty("java.io.tmpdir");
				if (!ValidationUtil.isValid(filePath)) {
					filePath = System.getenv("TMP");
				}
				if (ValidationUtil.isValid(filePath)) {
					filePath = filePath + File.separator;
				}
				String filePathZip = filePath;
				filePath = filePath + File.separator + "logs" + File.separator;
				File destinationFiles = new File(filePath);

				if (destinationFiles.exists()) {
					deleteFolder(destinationFiles);
				}

				if (!destinationFiles.exists()) {
					destinationFiles.mkdir();
				}
				Map<String, BufferedInputStream> map = new HashMap<String, BufferedInputStream>();
				try {
					for (int i = 0; i < fileNames.length; i++) {
//						System.out.println("File name : "+fileNames[i]);
						InputStream ins = sftpChannel.get(fileNames[i]);
						bufferedInputStream = new BufferedInputStream(
								new ByteArrayInputStream(IOUtils.toByteArray(ins)));
						map.put(fileNames[i], bufferedInputStream);
						File file = new File(filePath + fileNames[i]);
						FileOutputStream fos = new FileOutputStream(file);
						int bit = 4096;
						while ((bit) >= 0) {
							bit = bufferedInputStream.read();
							fos.write(bit);
						}
						fos.close();
					}
//		    		System.out.println(destinationFiles.getAbsolutePath());
					ZipUtils.zipFolder(destinationFiles.getAbsolutePath(), destinationFiles.getAbsolutePath() + ".zip");
					File zipFile = new File(filePathZip + File.separator + "logs.zip");
					FileInputStream fis = new FileInputStream(zipFile);
					bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(fis)));
					if (zipFile.exists()) {
						zipFile.delete();
					}
					/*
					 * File destinationFiles1 = new File(filePath); if (destinationFiles1.exists())
					 * { deleteFolder(destinationFiles1); }
					 */
					fis.close();
				} catch (SftpException e) {
				}

			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("File not found on the Server.");
				// throw new FileNotFoundException("File not found on the Server.");
			}
			if (bufferedInputStream != null) {
				System.out.println("bufferedInputStream is not null");
			}
			exceptionCode.setResponse(bufferedInputStream);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);

		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			e.printStackTrace();
		} finally {
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		return exceptionCode;
	}

	public void deleteFolder(File file) {
		try {
			for (File subFile : file.listFiles()) {
				if (subFile.isDirectory()) {
					deleteFolder(subFile);
				} else {
					subFile.delete();
				}
			}
		} catch (Exception e) {
		}
	}

	private FTPClient getConnection() throws IOException {
		FTPClient ftpClient = new FTPClient();
		FTPClientConfig conf = new FTPClientConfig(serverType);
		conf.setServerTimeZoneId(getTimezoneId());
		ftpClient.configure(conf);
		return ftpClient;
	}

	public ExceptionCode fileDownload(int dirType, FileInfoVb fileInfoTempVb, String strBuildNumber, String strBuild) {
		ExceptionCode exceptionCode = null;
		try {
			String filePath = System.getProperty("java.io.tmpdir");
			if (!ValidationUtil.isValid(filePath)) {
				filePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(filePath)) {
				filePath = filePath + File.separator;
			}
			setCurrentNode(fileInfoTempVb.getNodeName());
			System.out.println("File Path:" + filePath);
			if (dirType == 3) {
				BufferedInputStream in = null;
				File file = null;
				FileOutputStream fos = null;
				String extension = "";
				FileInfoVb fileInfoVb = new FileInfoVb();
				BuildSchedulesVb buildSchedulesVb = buildSchedulesDao.getQueryResultsForDetails(strBuildNumber,
						strBuild);
				String startTime = buildSchedulesVb.getStartTime();
				String status = buildSchedulesVb.getBuildScheduleStatus();
				if (startTime != null && !startTime.isEmpty()
						&& ("E".equalsIgnoreCase(status) || "I".equalsIgnoreCase(status))) {
					if (startTime.indexOf(" ") > 0) {
						startTime = startTime.substring(0, startTime.indexOf(" "));
					}
					startTime = startTime.substring(6) + "-" + startTime.substring(3, 5) + "-"
							+ startTime.substring(0, 2);
					if ("ZZ".equalsIgnoreCase(buildSchedulesVb.getCountry())
							&& buildSchedulesDao.checkExpandFlagFor(buildSchedulesVb) > 0) {
						StringBuffer fileName = new StringBuffer();
						fileName.append(buildSchedulesVb.getBuild()).append("_").append(buildSchedulesVb.getCountry())
								.append("_").append(buildSchedulesVb.getLeBook()).append("_").append(startTime)
								.append(".zip");
						StringBuffer command = new StringBuffer();
						command.append(buildSchedulesVb.getBuild()).append("_").append("*").append("_")
								.append(startTime).append(".log");
						fileInfoVb = new FileInfoVb();
						fileInfoVb.setName(fileName.toString());
						in = getLogFiles(fileInfoVb, command.toString());
						file = new File(filePath + fileInfoVb.getName());
						fos = new FileOutputStream(file);
					} else {
						StringBuffer fileName = new StringBuffer();
						fileName.append(buildSchedulesVb.getBuild()).append("_").append(buildSchedulesVb.getCountry())
								.append("_").append(buildSchedulesVb.getLeBook()).append("_").append(startTime)
								.append(".log");
						extension = ".txt";
						fileInfoVb.setName(fileName.toString());
						in = getLogFile(fileInfoVb);
						file = new File(filePath + fileInfoVb.getName() + extension);
						fos = new FileOutputStream(file);
					}
					int bit = 4096;
					while ((bit) >= 0) {
						bit = in.read();
						fos.write(bit);
					}
					fos.close();
					exceptionCode = CommonUtils.getResultObject("", 1, "", "");
//        			exceptionCode.setResponse(1);
					exceptionCode.setResponse(filePath);
					exceptionCode.setRequest(fileInfoVb.getName() + extension);
					in.close();
				} else {
					exceptionCode = CommonUtils.getResultObject("", Constants.ERRONEOUS_OPERATION, "", "");
					// exceptionCode.setResponse(1);
					exceptionCode.setResponse(filePath);
				}
			} else {
				BufferedInputStream in = null;
				File file = null;
				FileOutputStream fos = null;
				String fName = "";
				int number = 0;
				// fileNames = fileNames.substring(0,fileNames.length()-4);
				String fileNames = fileInfoTempVb.getName();
				String[] arrFileNames = fileNames.split(",");
				System.out.println("arrFileNames.length : " + arrFileNames.length);
				if("WINDOWS".equalsIgnoreCase(serverType)){
					exceptionCode =  downloadFileFromWindowServer(fileNames, dirType);
					
	        	}else {
				if (arrFileNames.length == 1) {
					fName = arrFileNames[0] ;
					file = new File(filePath + fName);
					fos = new FileOutputStream(file);
					if (securedFtp)
						exceptionCode = downloadFilesFromSFTP(fileNames, dirType);
					else
						exceptionCode = downloadFilesFromFTP(fileNames, dirType);
					number = 1;

					if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
						fos.close();
						return exceptionCode;
					}
				} else if (arrFileNames.length != 1) {
					if (securedFtp)
						exceptionCode = downloadFilesFromSFTP(fileNames, dirType);
					else
						exceptionCode = downloadFilesFromFTP(fileNames, dirType);
					file = new File(filePath + "logs.tar");
					fos = new FileOutputStream(file);
					fName = "logs.tar";
					number = 2;

					if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
						fos.close();
						return exceptionCode;
					}
				} else {
					exceptionCode = new ExceptionCode();
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("File not found on the Server.");
					return exceptionCode;
					// throw new FileNotFoundException("File not found on the Server.");
				}
				if (exceptionCode.getResponse() != null) {
					System.out.println("response is not null ");
					in = (BufferedInputStream) exceptionCode.getResponse();
				}
				// prakashika end 26-10-2022
				int bit = 4096;
				if (in != null) {
					while ((bit) >= 0) {
						bit = in.read();
						fos.write(bit);
					}
				}
				in.close();
				fos.close();
				exceptionCode = CommonUtils.getResultObject("", 1, "", "");
				exceptionCode.setRequest(fName);
//    			exceptionCode.setResponse(number);
				exceptionCode.setResponse(filePath);
	        	}
				// prakashika start 26-10-2022
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exceptionCode;
	}

	public String getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	public BufferedInputStream getLogFile(FileInfoVb fileInfoVb) {
		FTPClient ftpClient = null;
		BufferedInputStream input = null;
		try {
			setUploadDownloadDirFromDB();
			if (securedFtp) {
				return getLogFileFromSFTP(fileInfoVb);
			}
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
			boolean response = ftpClient.login(getUserName(), getPassword());
			if (!response) {
				logger.error("Unable to login to the FTP Server.");
				return null;
			}
			response = ftpClient.changeWorkingDirectory(buildLogsDir);
			if (!response) {
				logger.error("Unable to login to the FTP Server.");
				return null;
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			input = new BufferedInputStream(ftpClient.retrieveFileStream(fileInfoVb.getName()));
			if (input == null) {
				input = new BufferedInputStream(ftpClient.retrieveFileStream("BUILDCRON.log"));
				fileInfoVb.setName("BUILDCRON.log");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to login to the FTP Server.");
			return null;
		} finally {
			if (ftpClient != null)
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return input;
	}

	public BufferedInputStream getLogFileFromSFTP(FileInfoVb fileInfoVb) {
		BufferedInputStream input = null;
		Session session = null;
		try {
			setUploadDownloadDirFromDB();
			JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession(getUserName(), getHostName());
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(getPassword());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(buildLogsDir);
			try {
				InputStream ins = sftpChannel.get(fileInfoVb.getName());
				input = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
			} catch (SftpException ex) {
				try {
					if (input == null) {
						InputStream ins = sftpChannel.get("BUILDCRON.log");
						input = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
						fileInfoVb.setName("BUILDCRON.log");
					}
				} catch (SftpException ex1) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to login to the FTP Server.");
			return null;
		} finally {
			if (session != null)
				session.disconnect();
		}
		return input;
	}

	public BufferedInputStream getLogFilesFromSFTP(FileInfoVb fileInfoVb, String command) {
		BufferedInputStream input = null;
		Session session = null;
		Channel shellChannel = null;
		try {
			setUploadDownloadDirFromDB();
			JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession(getUserName(), getHostName());
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(getPassword());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			shellChannel = session.openChannel("shell");
			OutputStream inputstream_for_the_channel = shellChannel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			shellChannel.connect();
			commander.println("cd " + buildLogsDir);
			commander.println("tar -cvf " + fileInfoVb.getName() + " " + command);
			commander.println("exit");
			commander.close();
			do {
				Thread.sleep(1000);
			} while (!shellChannel.isEOF());
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(buildLogsDir);
			try {
				InputStream ins = sftpChannel.get(fileInfoVb.getName());
				input = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
			} catch (SftpException exp) {
				try {
					InputStream ins = sftpChannel.get("BUILDCRON.log");
					input = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
					fileInfoVb.setName("BUILDCRON.log");
				} catch (SftpException ex) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to login to the FTP Server.");
			return null;
		} finally {
			if (session != null)
				session.disconnect();
		}
		return input;
	}

	public BufferedInputStream getLogFiles(FileInfoVb fileInfoVb, String command) {
		FTPClient ftpClient = null;
		TelnetConnection telnetConnection = null;
		BufferedInputStream input = null;
		try {
			setUploadDownloadDirFromDB();
			if (securedFtp) {
				return getLogFilesFromSFTP(fileInfoVb, command);
			}
			telnetConnection = new TelnetConnection(hostName, userName, password, prompt);
			telnetConnection.connect();
			telnetConnection.sendCommand("cd " + buildLogsDir);
			telnetConnection.sendCommand("tar -cvf " + fileInfoVb.getName() + " " + command);
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
			boolean response = ftpClient.login(getUserName(), getPassword());
			if (!response) {
				logger.error("Unable to login to the FTP Server.");
				return null;
			}
			response = ftpClient.changeWorkingDirectory(buildLogsDir);
			if (!response) {
				logger.error("Unable to login to the FTP Server.");
				return null;
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			input = new BufferedInputStream(ftpClient.retrieveFileStream(fileInfoVb.getName()));
			if (input == null) {
				input = new BufferedInputStream(ftpClient.retrieveFileStream("BUILDCRON.log"));
				fileInfoVb.setName("BUILDCRON.log");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to login to the FTP Server.");
			return null;
		} finally {
			if (ftpClient != null)
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (telnetConnection != null && telnetConnection.isConnected()) {
				telnetConnection.disconnect();
			}
		}
		return input;
	}

	public String getKnownHostsFileName() {
		return knownHostsFileName;
	}

	public void setKnownHostsFileName(String knownHostsFileName) {
		this.knownHostsFileName = knownHostsFileName;
	}

	public ExceptionCode listFilesFromFtpServer(int dirType, String orderBy) {
		ExceptionCode exceptionCode = null;
		TelnetConnection telnetConnection = null;
		try {
			setUploadDownloadDirFromDB();
			if (securedFtp) {
				return listFilesFromSFtpServer(dirType, orderBy);
			}
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			telnetConnection = new TelnetConnection(hostName, userName, password, prompt);
			// telnetConnection.connect(serverType);
			telnetConnection.connect();
			if (dirType == DIR_TYPE_DOWNLOAD)
				telnetConnection.sendCommand("cd " + downloadDir);
			else
				telnetConnection.sendCommand("cd " + buildLogsDir);
			String responseStr = telnetConnection.sendCommand("ls -ltc ");
			String[] fileEntryArray = responseStr.split("\r\n");
			FTPClientConfig conf = new FTPClientConfig(serverType);
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			List<String> fileEntryList = unixFtpEntryParser
					.preParse(new LinkedList<String>(Arrays.asList(fileEntryArray)));
			List<FTPFile> lfiles = new ArrayList<FTPFile>(fileEntryList.size());
			for (String fileEntry : fileEntryList) {
				FTPFile ftpFile = unixFtpEntryParser.parseFTPEntry(fileEntry);
				if (ftpFile != null)
					lfiles.add(ftpFile);
			}
			for (FTPFile fileName : lfiles) {
				if (fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar"))
					continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
				/*
				 * if(dirType == 1){ fileInfoVb.setDate(formatDate(fileName));
				 * lFileList.add(fileInfoVb); }
				 */
				if (dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD")
						&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "_") > 0)) {
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
							+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
					lFileList.add(fileInfoVb);
				} else if (dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON")
						&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") <= 0)) {
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
							+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
					lFileList.add(fileInfoVb);
				} else if ((dirType == 2 || dirType == 1)
						&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") > 0)) {
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
//	    	exceptionCode.setResponse(createParentChildRelations(createData(),orderBy,dirType));
			exceptionCode.setResponse(createParentChildRelations(lFileList, orderBy, dirType));
		} catch (FileNotFoundException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			throw new RuntimeCustomException(exceptionCode);
		} catch (IOException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			throw new RuntimeCustomException(exceptionCode);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			throw new RuntimeCustomException(exceptionCode);
		} finally {
			if (telnetConnection != null && telnetConnection.isConnected()) {
				telnetConnection.disconnect();
				telnetConnection = null;
			}
		}
		return exceptionCode;
	}

	private List<FileInfoVb> createParentChildRelations(List<FileInfoVb> legalTreeList, String orderBy, int dirType)
			throws IOException {
		List<FileInfoVb> lResult = new ArrayList<FileInfoVb>(0);
		Set set = new HashSet<FileInfoVb>();
		// Top Roots are added.
		for (FileInfoVb fileVb : legalTreeList) {
			if ("Date".equalsIgnoreCase(orderBy)) {
				// String date = fileVb.getName().substring(fileVb.getName().length()-14,
				// fileVb.getName().length()-4);
				if (fileVb.getDate() != null && fileVb.getDate() != "") {
					set.add(fileVb.getDate());
				}
			} else {
				String fileNeme = "";
				if (dirType == 2) {
					int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
					if (cout == 0) {
						int cout1 = StringUtils.countOccurrencesOf(fileVb.getName(), "-");
						if (cout1 == 0)
							fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
						else
							fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 15);
					} else if (cout > 1)
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 21);
				} else {
					int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
					if (cout == 1) {
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
					} else if (cout > 1) {
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 20);
					}
				}
				if (fileNeme != null && fileNeme.length() > 0) {
					set.add(fileNeme);
				}
			}
		}
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			String date = (String) iterator.next();
			FileInfoVb fileVb = new FileInfoVb();
			fileVb.setDescription(date);
			lResult.add(fileVb);
		}
		// For each top node add all child's and to that child's add sub child's
		// recursively.
		for (FileInfoVb legalVb : lResult) {
			addChilds(legalVb, legalTreeList, orderBy, dirType);
		}
		if ("Date".equalsIgnoreCase(orderBy)) {
			final SimpleDateFormat dtF = new SimpleDateFormat("dd-MM-yyyy");
			// set the empty lists to null. this is required for UI to display the leaf
			// nodes properly.
			Collections.sort(lResult, new Comparator<FileInfoVb>() {
				public int compare(FileInfoVb m1, FileInfoVb m2) {
					try {
						return dtF.parse(m1.getDescription()).compareTo(dtF.parse(m2.getDescription()));
					} catch (ParseException e) {
						return 0;
					}
				}
			});
			Collections.reverse(lResult);
		} else {
			Collections.sort(lResult, new Comparator<FileInfoVb>() {
				public int compare(FileInfoVb m1, FileInfoVb m2) {
					return m1.getDescription().compareTo(m2.getDescription());
				}
			});
		}
		return lResult;
	}

	public void addChilds(FileInfoVb vObject, List<FileInfoVb> legalTreeListCopy, String orderBy, int dirType) {
		for (FileInfoVb fileTreeVb : legalTreeListCopy) {
			if ("Date".equalsIgnoreCase(orderBy)) {
				// String date =
				// fileTreeVb.getName().substring(fileTreeVb.getName().length()-14,
				// fileTreeVb.getName().length()-4);
				if (vObject.getDescription().equalsIgnoreCase(fileTreeVb.getDate())) {
					if (vObject.getChildren() == null) {
						vObject.setChildren(new ArrayList<FileInfoVb>(0));
					}
					fileTreeVb.setDescription(fileTreeVb.getName());
					vObject.getChildren().add(fileTreeVb);
				}
			} else {
				String fileNeme = "";
				if (dirType == 2) {
					int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
					if (cout == 0) {
						int cout1 = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "-");
						if (cout1 == 0)
							fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
						else
							fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 15);
					} else if (cout > 1)
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 21);
				} else {
					int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
					if (cout == 1) {
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
					} else if (cout > 1) {
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 20);
					}
				}
				if (vObject.getDescription().equalsIgnoreCase(fileNeme)) {
					if (vObject.getChildren() == null) {
						vObject.setChildren(new ArrayList<FileInfoVb>(0));
					}
					fileTreeVb.setDescription(fileTreeVb.getName());
					vObject.getChildren().add(fileTreeVb);
				}
			}
		}
	}

	public ExceptionCode listFilesFromSFtpServer(int dirType, String orderBy) {
		ExceptionCode exceptionCode = null;
		try {
			setUploadDownloadDirFromDB();
			JSch jsch = new JSch();
			// jsch.setKnownHosts( getKnownHostsFileName() );
			Session session = jsch.getSession(getUserName(), getHostName());
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(getPassword());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			if (dirType == DIR_TYPE_DOWNLOAD) {
				sftpChannel.cd(downloadDir); // downloadDir
			} else {
				sftpChannel.cd(buildLogsDir);
			}
			Vector<ChannelSftp.LsEntry> vtc = sftpChannel.ls("*.*");
			sftpChannel.disconnect();
			session.disconnect();
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			FTPClientConfig conf = new FTPClientConfig(serverType);
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			List<FTPFile> lfiles = new ArrayList<FTPFile>(vtc.size());
			for (ChannelSftp.LsEntry lsEntry : vtc) {
				FTPFile file = unixFtpEntryParser.parseFTPEntry(lsEntry.getLongname());
				if (file != null)
					lfiles.add(file);
			}
			for (FTPFile fileName : lfiles) {
				if (fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar"))
					continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
				/*
				 * if(dirType == 1){ fileInfoVb.setDate(formatDate(fileName));
				 * lFileList.add(fileInfoVb); }
				 */
				if (dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD")
						&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "_") > 0)) {
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
							+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
					lFileList.add(fileInfoVb);
				} else if (dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON")
						&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") <= 0)) {
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
							+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
					lFileList.add(fileInfoVb);
				} else if ((dirType == 2 || dirType == 1)
						&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") > 0)) {
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Download");
			// exceptionCode = CommonUtils.getResultObject(SERVICE_NAME,
			// Constants.SUCCESSFUL_OPERATION, "Download", "");
			exceptionCode.setResponse(createParentChildRelations(lFileList, orderBy, dirType));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			throw new RuntimeCustomException(exceptionCode);
		} catch (IOException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			throw new RuntimeCustomException(exceptionCode);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	public ExceptionCode listFilesFromFtpWindowServer(int dirType, String orderBy) {
		TelnetConnection telnetConnection = null;

		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String folderPath = commonDao.findVisionVariableValue("JAVA_BUILDCRON_LOG_FILE_PATH");
			File directory = new File(folderPath);
			Map<String, List<File>> mapFiles = new TreeMap<>();
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			if (directory.exists()) {
				File[] listFiles = directory.listFiles();
				for (File fileName : listFiles) {
					if (fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar"))
						continue;
					FileInfoVb fileInfoVb = new FileInfoVb();
					fileInfoVb.setName(fileName.getName());
					fileInfoVb.setSize(formatFileSize(fileName.length()));
					/*
					 * if(dirType == 1){ //fileInfoVb.setDate(formatDate(fileName));
					 * lFileList.add(fileInfoVb); }
					 */
					if (dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD")
							&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "_") > 0)) {
						Calendar cal = new GregorianCalendar();
						int month = cal.get(Calendar.MONTH) + 1;
						int year = cal.get(Calendar.YEAR);
						int day = cal.get(Calendar.DAY_OF_MONTH);
						fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
								+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
						lFileList.add(fileInfoVb);
					} else if (dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON")
							&& StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") <= 0)) {
						Calendar cal = new GregorianCalendar();
						int month = cal.get(Calendar.MONTH) + 1;
						int year = cal.get(Calendar.YEAR);
						int day = cal.get(Calendar.DAY_OF_MONTH);
						fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2) + "-"
								+ CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" + year);
						lFileList.add(fileInfoVb);
					} else if ((dirType == 2 || dirType == 1)
							&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") > 0)) {
						fileInfoVb.setDate(formatDate1(fileName.toString()));
						lFileList.add(fileInfoVb);
					}
				}
			}
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
			exceptionCode.setResponse(createParentChildRelations(lFileList, orderBy, dirType));
		} catch (Exception e) {

		}
		return exceptionCode;
	}

	public ExceptionCode downloadFileFromWindowServer(String fileName,int dirType) throws IOException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ByteArrayOutputStream out = null;
		OutputStream outputStream = null;
		try {
			String filePath = commonDao.findVisionVariableValue("JAVA_BUILDCRON_LOG_FILE_PATH");
			String tmpFilePath = System.getProperty("java.io.tmpdir");
			String[] logFile = fileName.split(",");
			File lfile = new File(tmpFilePath + File.separator + "logs.zip");
			if (lfile.exists()) {
				lfile.delete();
			}
			if (logFile.length > 1) {
				FileOutputStream fos = new FileOutputStream(tmpFilePath + File.separator + "logs.zip");
				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
				for (int i = 0; i < logFile.length; i++) {
					if (!logFile[i].matches("[a-zA-Z0-9._-]+")) {
						throw new IllegalArgumentException("Invalid log file name: " + logFile[i]);
					}
					Path logFilePath = Paths.get(filePath, logFile[i]).normalize();
					if (!logFilePath.startsWith(filePath)) {
						throw new IllegalArgumentException("Invalid log file path: " + logFilePath);
					}
					File logFileP = logFilePath.toFile();
					FileInputStream fis = new FileInputStream(logFileP);
					ZipEntry ze = new ZipEntry(logFile[i]);
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					fis.close();
				}
				zipOut.close();
				fos.close();
			} else {
				if (!fileName.matches("[a-zA-Z0-9._-]+")) {
					throw new IllegalArgumentException("Invalid log file name");
				}
				Path logFilePath = Paths.get(filePath, fileName).normalize();
				if (!logFilePath.startsWith(filePath)) {
					throw new IllegalArgumentException("Invalid log file path");
				}
				File my_file = logFilePath.toFile();
				out = new ByteArrayOutputStream();
				FileInputStream in = new FileInputStream(my_file);
				out = new ByteArrayOutputStream();
				Path logPath = Paths.get(tmpFilePath, fileName).normalize();
				lfile = logPath.toFile();
				if (lfile.exists()) {
					lfile.delete();
				}
				outputStream = new FileOutputStream(lfile);
				int length = fileName.length();
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				while ((length = in.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
				out.writeTo(outputStream);
				outputStream.flush();
				outputStream.close();
				out.flush();
				out.close();
				in.close();
			}
			exceptionCode.setRequest(fileName);
			exceptionCode.setResponse(tmpFilePath);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Log File Downloaded");
			
			return exceptionCode;
		} catch (Exception ex) {
			// logger.error("Download Errror : " + ex.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			return exceptionCode;
		}finally {
			if(out!= null) {
				out.flush();
				out.close();
				
			}
		}
	}

}
/*
 * class SubbuildNumberStore{ private Integer lActualSubBuildNumber =1; private
 * Integer lAssignedSubBuildNumber =1; private ArrayList<Integer>
 * lBsdSequenceList = new ArrayList<Integer>(); public Integer
 * getLActualSubBuildNumber() { return lActualSubBuildNumber; } public void
 * setLActualSubBuildNumber(Integer actualSubBuildNumber) {
 * lActualSubBuildNumber = actualSubBuildNumber; } public Integer
 * getLAssignedSubBuildNumber() { return lAssignedSubBuildNumber; } public void
 * setLAssignedSubBuildNumber(Integer assignedSubBuildNumber) {
 * lAssignedSubBuildNumber = assignedSubBuildNumber; } public ArrayList<Integer>
 * getLBsdSequenceList() { return lBsdSequenceList; } public void
 * setLBsdSequenceList(ArrayList<Integer> bsdSequenceList) { lBsdSequenceList =
 * bsdSequenceList; } }
 */
