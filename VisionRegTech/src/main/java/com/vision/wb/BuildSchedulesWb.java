package com.vision.wb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.ConnectException;
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
import java.util.Vector;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.BuildControlsDao;
import com.vision.dao.BuildControlsStatsDao;
import com.vision.dao.BuildSchedulesDao;
import com.vision.dao.BuildSchedulesDetailsDao;
import com.vision.dao.ProgramsDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.BuildControlsStatsVb;
import com.vision.vb.BuildControlsVb;
import com.vision.vb.BuildSchedulesDetailsVb;
import com.vision.vb.BuildSchedulesVb;
import com.vision.vb.FileInfoVb;
import com.vision.vb.ProgramsVb;

@Controller
public class BuildSchedulesWb extends AbstractDynaWorkerBean<BuildSchedulesVb> {

	@Autowired
	private BuildSchedulesDao buildSchedulesDao; 
	@Autowired
	private BuildSchedulesDetailsDao buildSchedulesDetailsDao;

	@Autowired
	private ProgramsDao programsDao;
	
	@Autowired
	private BuildControlsDao buildControlsDao;
	
	@Autowired
	private BuildControlsStatsDao buildControlsStatsDao;
	

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
    private static final String SERVICE_NAME = "Vision Upload";
	
	@Override
	protected AbstractDao<BuildSchedulesVb> getScreenDao() {
		return buildSchedulesDao;
	}

	@Override
	protected void setAtNtValues(BuildSchedulesVb object) {
		object.setBuildScheduleStatusAt(206);
		object.setRecurringFrequencyAt(213);
	}

	@Override
	protected void setVerifReqDeleteType(BuildSchedulesVb object) {
		object.setVerificationRequired(false);
	}
	public ArrayList getPageLoadValues(BuildSchedulesVb lBuildSchedulesVb){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(206);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(213);
			arrListLocal.add(collTemp);
			setVerifReqDeleteType(lBuildSchedulesVb);
			ExceptionCode exception = getQueryResults(lBuildSchedulesVb);
			arrListLocal.add(exception);
			/*
			 * collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(211);
			 * arrListLocal.add(collTemp);
			 */
			collTemp = getProgramsDao().getMajorBuildList();
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	public ExceptionCode getQueryResults(BuildSchedulesVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<BuildSchedulesVb> collTemp = getBuildSchedulesDao().getQueryResults(vObject,intStatus);
		List<BuildSchedulesVb> collTemp1 = getBuildSchedulesDao().getQueryResultsForStatusBars(vObject, intStatus);
		String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
		if(!ValidationUtil.isValid(environmentParam))
			environmentParam="UAT";
		String cronNameDelimited="";
		String cronStatusDelimited="";
		List<BuildSchedulesVb> CronName=getBuildSchedulesDao().findCronName(vObject, environmentParam);
		List<BuildSchedulesVb> CronStatus=getBuildSchedulesDao().findCronStatus(vObject, environmentParam);
		if(CronName!=null) {
			for(int i=0;i<CronName.size();i++){
				if( ValidationUtil.isValid(cronNameDelimited) )
					cronNameDelimited=cronNameDelimited+","+CronName.get(i).getCronName();
				else
					cronNameDelimited=cronNameDelimited+CronName.get(i).getCronName();
				if( ValidationUtil.isValid(cronStatusDelimited) )
					cronStatusDelimited=cronStatusDelimited+","+CronStatus.get(i).getCronStatus();
				else
					cronStatusDelimited=cronStatusDelimited+CronStatus.get(i).getCronStatus();
			}
		}
		String cronStatusFromDB = getVisionVariableValue("VISION_BUILD_CRON");
		if(cronStatusFromDB == null || cronStatusFromDB.isEmpty()){
			cronStatusFromDB = "Stopped";
		}
		if(!("STOPPED".equalsIgnoreCase(cronStatusFromDB) || "Stopping".equalsIgnoreCase(cronStatusFromDB))){
			if(getBuildSchedulesDao().getLastFetchIntervel() > 2){
				cronStatusFromDB = "<font color='#5F04B4'><b>Not Responding</b></font>";
		    }
		    else{
		    	cronStatusFromDB = "<font color='green'><b>Running</b></font>";
		    }
		}else if("STOPPED".equalsIgnoreCase(cronStatusFromDB)){
			cronStatusFromDB = "<font color='red'><b>Stopped</b></font>";	
		}else if("Stopping".equalsIgnoreCase(cronStatusFromDB)){
			cronStatusFromDB = "<font color='#FF8000'><b>Stopping</b></font>";
		}
		String dbDateTime = getBuildSchedulesDetailsDao().getSystemDate();
		dbDateTime = dbDateTime.replace('/', '-');
		vObject.setDbDateTime(dbDateTime);
		vObject.setCronName(cronNameDelimited);
		vObject.setCronStatus(cronStatusDelimited);
		vObject.setCronCount(getBuildSchedulesDao().findCronCount(environmentParam));
		//setLogInformation(collTemp);
		vObject.setSubmitterId(SessionContextHolder.getContext().getVisionId());
		ExceptionCode exceptionCode = CommonUtils.getResultObject(getBuildSchedulesDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
		exceptionCode.setOtherInfo(vObject);
		exceptionCode.setResponse(collTemp);
		List<AlphaSubTabVb> collTempDesc = getBuildSchedulesDetailsDao().findActiveAlphaSubTabsByAlphaTabOrderBy(1204);
		List<BuildSchedulesVb> collTempStatus =new ArrayList<BuildSchedulesVb>();
		if(collTemp1!=null && collTempDesc!=null) {
				for(AlphaSubTabVb vObj:collTempDesc) {
					BuildSchedulesVb buildSchedulesVb = new BuildSchedulesVb();
					boolean avail=false;
					for(BuildSchedulesVb vObj1:collTemp1) {
						if(vObj1.getStatusDesc().equalsIgnoreCase(vObj.getDescription())) {
							avail=true;
							buildSchedulesVb.setParallelProcsCount(vObj1.getParallelProcsCount());
						}
					}
					if(avail==false) {
						buildSchedulesVb.setParallelProcsCount(0);
					}
					buildSchedulesVb.setBuildScheduleStatus(vObj.getAlphaSubTab());
					buildSchedulesVb.setStatusDesc(vObj.getDescription());
					collTempStatus.add(buildSchedulesVb);
				}
		}
		exceptionCode.setResponse1(collTempStatus);
		return exceptionCode;
	}
	
	public ExceptionCode getBuildScheduleDetails(BuildSchedulesVb vObject){
		ExceptionCode exceptionCode = null;
		BuildSchedulesVb vObjectTemp;
		vObjectTemp = getBuildSchedulesDao().getQueryResultsForDetails(vObject.getBuildNumber(),vObject.getBuild());
		if(vObjectTemp == null){
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.WE_HAVE_WARNING_DESCRIPTION, "Query", "Build has completed successfully.");
			exceptionCode.setOtherInfo(vObject);
		}else{
			List<BuildSchedulesDetailsVb> collTemp = getBuildSchedulesDetailsDao().getQueryDisplayResults(vObject.getBuildNumber());
			ProgramsVb programsVb = getProgramsDao().getSupportContactDetails(vObjectTemp.getBuild());
			vObjectTemp.setSupportContactNumber(programsVb.getSupportContactMobile());
			exceptionCode = CommonUtils.getResultObject(getBuildSchedulesDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
			vObjectTemp.setBuildSchedulesDetails(collTemp);
			exceptionCode.setOtherInfo(vObjectTemp);
		}
		return exceptionCode;
	}
	
	public ExceptionCode insertBuildSchedules(BuildSchedulesVb pBuildSchedulesVb, List<BuildControlsVb> pBuildControls,
			String operation){
		
		ExceptionCode exceptionCode  = null;
		try
		{   
			ProgramsVb lProgramsVb = getProgramsDao().getSupportContactDetails(pBuildSchedulesVb.getBuild());
			if(lProgramsVb != null){
				pBuildSchedulesVb.setSupportContact(lProgramsVb.getSupportContactMail());
				pBuildSchedulesVb.setSupportContactNumber(lProgramsVb.getSupportContactMobile());
				pBuildSchedulesVb.setNotify(lProgramsVb.getNotify());
			}
			ArrayList<BuildSchedulesDetailsVb> listBSDetails = new ArrayList<BuildSchedulesDetailsVb>();
			ArrayList<SubbuildNumberStore> pSubbuildNumbersList = new ArrayList<SubbuildNumberStore>();
			
			pBuildSchedulesVb.setBuildScheduleStatusAt(206);
			pBuildSchedulesVb.setRecurringFrequencyAt(213);
			parseRequest(pBuildControls, listBSDetails, pSubbuildNumbersList);
			pBuildSchedulesVb.setParallelProcsCount(pSubbuildNumbersList.size());
			pBuildSchedulesVb.setBuildSchedulesDetails(listBSDetails);
			//Expand Sub builds If Country is ZZ and LE Book is 999
			expandBuildSchedulesDeailsForCountryLeBook(pBuildSchedulesVb);
			//Code for Build Schedules Full Build.
			for (BuildControlsVb buildControlsVb: pBuildControls) {
				if("ZZ".equalsIgnoreCase(pBuildSchedulesVb.getCountry()) && "999".equalsIgnoreCase(pBuildSchedulesVb.getLeBook())){
					 if("Y".equalsIgnoreCase(buildControlsVb.getFullBuild()) && "Y".equalsIgnoreCase(buildControlsVb.getExpandFlag())){
						List<BuildControlsStatsVb> collTemp = null;
						BuildControlsStatsVb buildStatsVb = new BuildControlsStatsVb();
						/*String yrValue = getCommonDao().findVisionVariableValue("CURRENT_YEAR");*/
						String yrValue = pBuildSchedulesVb.getBusinessDate().substring(pBuildSchedulesVb.getBusinessDate().lastIndexOf("-")+1,pBuildSchedulesVb.getBusinessDate().length());
						String dateValue = getCommonDao().findVisionVariableValue("LAST_BUILD_DATE");
						String builddateValue=dateValue.replaceAll("/", "-");
						buildStatsVb.setYear(yrValue);
						buildStatsVb.setDataSource("A");
						buildStatsVb.setBuildModule(buildControlsVb.getBuildModule());
						buildStatsVb.setLastBuildDate(builddateValue);
						collTemp = getBuildControlsStatsDao().getQueryResultsForBuild(buildStatsVb);
						if(collTemp != null && collTemp.size() != 0){
							getBuildControlsStatsDao().doUpdateApprForAll(buildStatsVb);
						}
					 }else if("Y".equalsIgnoreCase(buildControlsVb.getFullBuild()) && "N".equalsIgnoreCase(buildControlsVb.getExpandFlag())){
						 List<BuildControlsStatsVb> collTemp = null;
						 BuildControlsStatsVb buildStatsVb = new BuildControlsStatsVb();
						 /*String yrValue = getCommonDao().findVisionVariableValue("CURRENT_YEAR");*/
						 String yrValue = pBuildSchedulesVb.getBusinessDate().substring(pBuildSchedulesVb.getBusinessDate().lastIndexOf("-")+1,pBuildSchedulesVb.getBusinessDate().length());
						 String dateValue = getCommonDao().findVisionVariableValue("LAST_BUILD_DATE");
						 String builddateValue=dateValue.replaceAll("/", "-");
						 buildStatsVb.setYear(yrValue);
						 buildStatsVb.setDataSource("A");
						 buildStatsVb.setBuildModule(buildControlsVb.getBuildModule());
						 buildStatsVb.setCountry(buildControlsVb.getCountry());
						 buildStatsVb.setLeBook(buildControlsVb.getLeBook());
						 buildStatsVb.setLastBuildDate(builddateValue);
						 collTemp = getBuildControlsStatsDao().getQueryResultsForReview(buildStatsVb, 0);
						 if(collTemp != null && collTemp.size() != 0){
							 getBuildControlsStatsDao().doUpdateApprRecord(buildStatsVb);
						 }
					 }
				}else{
					if("Y".equalsIgnoreCase(buildControlsVb.getFullBuild()) && "N".equalsIgnoreCase(buildControlsVb.getExpandFlag())){
						List<BuildControlsStatsVb> collTemp = null;
						BuildControlsStatsVb buildStatsVb = new BuildControlsStatsVb();
						/*String yrValue = getCommonDao().findVisionVariableValue("CURRENT_YEAR");*/
						String yrValue = pBuildSchedulesVb.getBusinessDate().substring(pBuildSchedulesVb.getBusinessDate().lastIndexOf("-")+1,pBuildSchedulesVb.getBusinessDate().length());
						String dateValue = getCommonDao().findVisionVariableValue("LAST_BUILD_DATE");
						String builddateValue=dateValue.replaceAll("/", "-");
						buildStatsVb.setYear(yrValue);
						buildStatsVb.setDataSource("A");
						buildStatsVb.setBuildModule(buildControlsVb.getBuildModule());
						buildStatsVb.setCountry(buildControlsVb.getCountry());
						buildStatsVb.setLeBook(buildControlsVb.getLeBook());
						buildStatsVb.setLastBuildDate(builddateValue);
						collTemp = getBuildControlsStatsDao().getQueryResultsForReview(buildStatsVb, 0);
						if(collTemp != null && collTemp.size() != 0){
							getBuildControlsStatsDao().doUpdateApprRecord(buildStatsVb);
						}						
					}
				}
			}
			if("Add".equalsIgnoreCase(operation)){
				pBuildSchedulesVb.setBuildScheduleStatus("P");
				exceptionCode = getBuildSchedulesDao().doInsertApprRecord(pBuildSchedulesVb);
			}else{
				exceptionCode = getBuildSchedulesDao().doUpdateApprRecord(pBuildSchedulesVb);
			}
			exceptionCode.setResponse(pBuildControls);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(pBuildControls);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}
	}
	
	public ExceptionCode transfer(ExceptionCode pExceptionCode, List<BuildSchedulesVb> pBuildSchedules,String updateNode){
		ExceptionCode exceptionCode  = null;
		BuildSchedulesVb pBuildSchedulesVb = (BuildSchedulesVb) pExceptionCode.getOtherInfo();
		try
		{
			exceptionCode = getBuildSchedulesDao().transfer(pBuildSchedules,updateNode);
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}
	}
	
	public ExceptionCode reInitiate(ExceptionCode pExceptionCode, List<BuildSchedulesVb> pBuildSchedules){
		ExceptionCode exceptionCode  = null;
		BuildSchedulesVb pBuildSchedulesVb = (BuildSchedulesVb) pExceptionCode.getOtherInfo();
		try
		{
			exceptionCode = getBuildSchedulesDao().reInitiate(pBuildSchedules);
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}
	}
	
	private void parseRequest(List<BuildControlsVb> pBuildControls, ArrayList<BuildSchedulesDetailsVb> listBSDetails,
					ArrayList<SubbuildNumberStore> pSubbuildNumbersList){
		BuildSchedulesDetailsVb vObjectBsd = null;
		for (BuildControlsVb buildControlsVb: pBuildControls) {
			vObjectBsd = new BuildSchedulesDetailsVb();
			int level = 1;
			vObjectBsd.setBuildModule(buildControlsVb.getBuildModule());
			if("Y".equalsIgnoreCase(buildControlsVb.getRunIt())) {
				vObjectBsd.setAssociatedBuild(buildControlsVb.getBuild());
				vObjectBsd.setSubBuildNumber(buildControlsVb.getSubBuildNumber());
				vObjectBsd.setExpandFlag(buildControlsVb.getExpandFlag());
				Integer lSubBuildNum = getSubBuildNumber(pSubbuildNumbersList, Integer.parseInt(vObjectBsd.getSubBuildNumber()));
				level = buildControlsVb.getBuildLevel();
				if(level > 1 && !isParentBuildAdded(buildControlsVb.getParent(),listBSDetails, lSubBuildNum) ){
					List<BuildSchedulesDetailsVb> lBuildSchedulesDetails 
						= getParents(buildControlsVb.getParent(), listBSDetails,lSubBuildNum,pSubbuildNumbersList, Integer.parseInt(vObjectBsd.getSubBuildNumber()));
					listBSDetails.addAll(lBuildSchedulesDetails);
				}
				doPutDetails(buildControlsVb,vObjectBsd,""+getBsdSequence(pSubbuildNumbersList, Integer.parseInt(vObjectBsd.getSubBuildNumber())));
				vObjectBsd.setSubBuildNumber(lSubBuildNum+"");
				listBSDetails.add(vObjectBsd);
			}
			if(buildControlsVb.getChildren() != null && !buildControlsVb.getChildren().isEmpty()){
				for(BuildControlsVb childControlsVb :buildControlsVb.getChildren()){
					childControlsVb.setParent(buildControlsVb);
				}
				parseRequest(buildControlsVb.getChildren(), listBSDetails, pSubbuildNumbersList);
			}
		}
	}
	/**
	 * Expands the Build Schedules Details for all countries in Build Controls if the Country is ZZ and LE Book is 999
	 * else assign the parent country and LE Book for the Build Schedules Details.
	 * @param pBuildSchedulesVb 
	 */
	private void expandBuildSchedulesDeailsForCountryLeBook(BuildSchedulesVb pBuildSchedulesVb) {
		if("ZZ".equalsIgnoreCase(pBuildSchedulesVb.getCountry()) && "999".equalsIgnoreCase(pBuildSchedulesVb.getLeBook())){
			ArrayList<SubbuildNumberStore> pSubbuildNumbersList = new ArrayList<SubbuildNumberStore>();
			Map<String,ArrayList<BuildControlsVb>> lBuildControlsMap = new HashMap<String, ArrayList<BuildControlsVb>>();
			ArrayList<BuildSchedulesDetailsVb> lDetails = new ArrayList<BuildSchedulesDetailsVb>();
			DeepCopy<BuildSchedulesDetailsVb> deepCopy = new DeepCopy<BuildSchedulesDetailsVb>();
			for(int loopCount=0;loopCount < pBuildSchedulesVb.getBuildSchedulesDetails().size(); loopCount++){
				BuildSchedulesDetailsVb lDetailsVb = pBuildSchedulesVb.getBuildSchedulesDetails().get(loopCount);
				if("N".equalsIgnoreCase(lDetailsVb.getExpandFlag())){
					BuildSchedulesDetailsVb lTempDetailsVb = deepCopy.copy(lDetailsVb);
					lTempDetailsVb.setSubBuildNumber(""+getSubBuildNumber(pSubbuildNumbersList, Integer.parseInt(lTempDetailsVb.getSubBuildNumber())));
					lTempDetailsVb.setBsdSequence(""+getBsdSequence(pSubbuildNumbersList, Integer.parseInt(lTempDetailsVb.getSubBuildNumber())));
					lTempDetailsVb.setCountry(pBuildSchedulesVb.getCountry());
					lTempDetailsVb.setLeBook(pBuildSchedulesVb.getLeBook());
					lDetails.add(lTempDetailsVb);
				}else{
					if(!isBuildAdded(lDetailsVb, lDetails)){
						ArrayList<BuildControlsVb> arrListControls  = (ArrayList<BuildControlsVb>) getDistinctCountryLeBook(lBuildControlsMap,lDetailsVb.getAssociatedBuild());
						if(arrListControls == null || arrListControls.isEmpty()){
							BuildSchedulesDetailsVb lTempDetailsVb = deepCopy.copy(lDetailsVb);
							lTempDetailsVb.setSubBuildNumber(""+getSubBuildNumber(pSubbuildNumbersList, Integer.parseInt(lTempDetailsVb.getSubBuildNumber())));
							lTempDetailsVb.setBsdSequence(""+getBsdSequence(pSubbuildNumbersList, Integer.parseInt(lTempDetailsVb.getSubBuildNumber())));
							lTempDetailsVb.setCountry(pBuildSchedulesVb.getCountry());
							lTempDetailsVb.setLeBook(pBuildSchedulesVb.getLeBook());
							lDetails.add(lTempDetailsVb);
						}else {
							ArrayList<BuildSchedulesDetailsVb> lChildDetails = getChilds(pBuildSchedulesVb.getBuildSchedulesDetails(), 
										lDetailsVb.getBuildModule(),loopCount+1);
							for(BuildControlsVb lBuildControlsVb : arrListControls){
								BuildSchedulesDetailsVb lTempDetailsVb = deepCopy.copy(lDetailsVb);
								lTempDetailsVb.setSubBuildNumber(""+getSubBuildNumber(pSubbuildNumbersList, Integer.parseInt(lTempDetailsVb.getSubBuildNumber())));
								lTempDetailsVb.setBsdSequence(""+getBsdSequence(pSubbuildNumbersList, Integer.parseInt(lTempDetailsVb.getSubBuildNumber())));
								lTempDetailsVb.setCountry(lBuildControlsVb.getCountry());
								lTempDetailsVb.setLeBook(lBuildControlsVb.getLeBook());
								lDetails.add(lTempDetailsVb);
								for(BuildSchedulesDetailsVb lBDetailsVb : lChildDetails){
									BuildSchedulesDetailsVb lTempDetailsChildVb = deepCopy.copy(lBDetailsVb);
									lTempDetailsChildVb.setSubBuildNumber(""+getSubBuildNumber(pSubbuildNumbersList, Integer.parseInt(lTempDetailsVb.getSubBuildNumber())));
									lTempDetailsChildVb.setBsdSequence(""+getBsdSequence(pSubbuildNumbersList, Integer.parseInt(lTempDetailsVb.getSubBuildNumber())));
									lTempDetailsChildVb.setCountry(lBuildControlsVb.getCountry());
									lTempDetailsChildVb.setLeBook(lBuildControlsVb.getLeBook());
									lDetails.add(lTempDetailsChildVb);
								}
							}
						}
					}
				}
			}
			pBuildSchedulesVb.getBuildSchedulesDetails().clear();
			pBuildSchedulesVb.getBuildSchedulesDetails().addAll(lDetails);
		}else{
			for(BuildSchedulesDetailsVb lDetailsVb: pBuildSchedulesVb.getBuildSchedulesDetails()){
				lDetailsVb.setCountry(pBuildSchedulesVb.getCountry());
				lDetailsVb.setLeBook(pBuildSchedulesVb.getLeBook());
			}
		}
		
	}
	private ArrayList<BuildControlsVb> getDistinctCountryLeBook(Map<String,ArrayList<BuildControlsVb>> pMapBuildControls, String pBuildName){
		if(pMapBuildControls == null){
			pMapBuildControls = new HashMap<String, ArrayList<BuildControlsVb>>();
		}
		if(pMapBuildControls.containsKey(pBuildName)){
			return pMapBuildControls.get(pBuildName);
		}
		ArrayList<BuildControlsVb> arrListControls  = (ArrayList<BuildControlsVb>) getBuildControlsDao().getDistCountryLeBook(pBuildName);
		pMapBuildControls.put(pBuildName, arrListControls);
		return arrListControls;
	}
	private ArrayList<BuildSchedulesDetailsVb> getChilds(List<BuildSchedulesDetailsVb> pDetails, String pBuild,int pLoopCount){
		ArrayList<BuildSchedulesDetailsVb> lDetails = new ArrayList<BuildSchedulesDetailsVb>();
			if(pLoopCount >= pDetails.size()){
				return lDetails;
			}
			for(int loopCount = pLoopCount; loopCount<pDetails.size(); loopCount++){
				BuildSchedulesDetailsVb lBuildSchedulesDetailsVb = pDetails.get(loopCount);
				if(lBuildSchedulesDetailsVb.getAssociatedBuild().equalsIgnoreCase(pBuild)){
					lDetails.addAll(getChilds(pDetails, lBuildSchedulesDetailsVb.getBuildModule(),loopCount));
				}else if(lBuildSchedulesDetailsVb.getBuildModule().equals(pBuild) && !isBuildAdded(lBuildSchedulesDetailsVb, lDetails)){
					lDetails.add(lBuildSchedulesDetailsVb);
				}else if (!(lBuildSchedulesDetailsVb.getBuildModule().equals(pBuild) && lBuildSchedulesDetailsVb.getAssociatedBuild().equals(pBuild))){
					return lDetails;
				}
			}
		return lDetails;
	}
	private boolean isBuildAdded(BuildSchedulesDetailsVb sourceBuild, List<BuildSchedulesDetailsVb> lBuildSchedulesDetails) {
		for(BuildSchedulesDetailsVb lBuildSchedulesDetailsVb : lBuildSchedulesDetails){
			if(lBuildSchedulesDetailsVb.getAssociatedBuild().equalsIgnoreCase(sourceBuild.getAssociatedBuild()) 
					&& lBuildSchedulesDetailsVb.getBuildModule().equalsIgnoreCase(sourceBuild.getBuildModule())
					&& sourceBuild.getSubBuildNumber().equalsIgnoreCase(lBuildSchedulesDetailsVb.getSubBuildNumber())) return true;
		}
		return false;
	}
	private Integer getSubBuildNumber(ArrayList<SubbuildNumberStore> pSubbuildNumbersList, Integer pSubbuildNumber){
		//Contains values of Actual Subbuild Number, Assigned Subbuild Number , BsdSequence
		for(SubbuildNumberStore lSubbildNumberStore: pSubbuildNumbersList){
			if(pSubbuildNumber != null && pSubbuildNumber.equals(lSubbildNumberStore.getLActualSubBuildNumber())){
				return lSubbildNumberStore.getLAssignedSubBuildNumber();
			}
		}
		if(pSubbuildNumbersList.isEmpty()){
			SubbuildNumberStore lSubbildNumberStore = new SubbuildNumberStore();
			lSubbildNumberStore.setLActualSubBuildNumber(pSubbuildNumber);
			lSubbildNumberStore.setLAssignedSubBuildNumber(1);
			pSubbuildNumbersList.add(lSubbildNumberStore);
			return  lSubbildNumberStore.getLAssignedSubBuildNumber();
		}
		SubbuildNumberStore lSubbildNumberStore = new SubbuildNumberStore();
		lSubbildNumberStore.setLActualSubBuildNumber(pSubbuildNumber);
		lSubbildNumberStore.setLAssignedSubBuildNumber(pSubbuildNumbersList.get(pSubbuildNumbersList.size() -1).getLAssignedSubBuildNumber()+1);
		pSubbuildNumbersList.add(lSubbildNumberStore);
		return  lSubbildNumberStore.getLAssignedSubBuildNumber();
	}
	private boolean isParentBuildAdded(BuildControlsVb buildControlsVb, List<BuildSchedulesDetailsVb> lBuildSchedulesDetails, int subBuildNumber) {
		if(buildControlsVb == null){
			return true;
		}
		for(BuildSchedulesDetailsVb lBuildSchedulesDetailsVb : lBuildSchedulesDetails){
			if(lBuildSchedulesDetailsVb.getBuildModule().equalsIgnoreCase(buildControlsVb.getBuildModule())
					&& String.valueOf(subBuildNumber).equalsIgnoreCase(lBuildSchedulesDetailsVb.getSubBuildNumber())) return true;
		}
		return false;
	}
	private ArrayList<BuildSchedulesDetailsVb> getParents(BuildControlsVb pBuildControlsVb, ArrayList<BuildSchedulesDetailsVb> pBuildSchedulesDetails, 
			int subBuildNumber, ArrayList<SubbuildNumberStore> pSubbuildNumbersList, Integer actualSubBuildNumber){
		ArrayList<BuildSchedulesDetailsVb> lBuildSchedulesDetails = new ArrayList<BuildSchedulesDetailsVb>();
		if(!isParentBuildAdded(pBuildControlsVb.getParent(), pBuildSchedulesDetails, subBuildNumber) && pBuildControlsVb.getBuildLevel() >1){
			List<BuildSchedulesDetailsVb> lBuildSchedulesDetailsTmp 
				= getParents(pBuildControlsVb.getParent(), pBuildSchedulesDetails, subBuildNumber, pSubbuildNumbersList,actualSubBuildNumber);
			lBuildSchedulesDetails.addAll(lBuildSchedulesDetailsTmp);
		}
		BuildSchedulesDetailsVb vObjectBsd = new BuildSchedulesDetailsVb();
		vObjectBsd.setBuildModule(pBuildControlsVb.getBuildModule());
		vObjectBsd.setAssociatedBuild(pBuildControlsVb.getBuild());
		vObjectBsd.setSubBuildNumber(subBuildNumber+"");
		Integer bsdSequence = getBsdSequence(pSubbuildNumbersList, actualSubBuildNumber);
		vObjectBsd.setBsdSequence(bsdSequence+"");
		vObjectBsd.setSubmitterId(SessionContextHolder.getContext().getVisionId()+"");
		vObjectBsd.setProgramType(pBuildControlsVb.getProgramType());
		vObjectBsd.setModuleStatus("P");
		vObjectBsd.setRunItAt(207);
		vObjectBsd.setProgramTypeAt(201);
		vObjectBsd.setModuleStatusAt(211);
		vObjectBsd.setRunIt(pBuildControlsVb.getRunIt());
		vObjectBsd.setFullBuildFlag(pBuildControlsVb.getFullBuild());
		vObjectBsd.setProgressPercent("0");
		vObjectBsd.setDebugMode(pBuildControlsVb.getDebug());
		vObjectBsd.setStartTime("");
		vObjectBsd.setEndTime("");
		vObjectBsd.setDateLastModified("");
		vObjectBsd.setDateCreation("");
		vObjectBsd.setExpandFlag(pBuildControlsVb.getExpandFlag());
		lBuildSchedulesDetails.add(vObjectBsd);
		return lBuildSchedulesDetails;
	}
	private Integer getBsdSequence(ArrayList<SubbuildNumberStore> pSubbuildNumbersList, Integer pSubbuildNumber){
		//Contains values of Actual Sub-build Number, Assigned Sub-build Number , BsdSequence
		for(SubbuildNumberStore lSubbildNumberStore: pSubbuildNumbersList){
			if(pSubbuildNumber != null && pSubbuildNumber.equals(lSubbildNumberStore.getLActualSubBuildNumber())){
				ArrayList<Integer> lBsdSequenceList = lSubbildNumberStore.getLBsdSequenceList();
				if(lBsdSequenceList.isEmpty()){
					lBsdSequenceList.add(1);
					return lBsdSequenceList.get(0);
				}
				else{
					Integer bsdSequence = lBsdSequenceList.get(lBsdSequenceList.size()-1);
					bsdSequence = bsdSequence+1;
					lBsdSequenceList.add(bsdSequence);
					return bsdSequence;
				}
			}
		}
		return 1;
	}
	private void doPutDetails(BuildControlsVb pBuildControlsVb, BuildSchedulesDetailsVb vObjectBsd, String bsdSequenceCount){

		vObjectBsd.setBsdSequence(bsdSequenceCount);
		vObjectBsd.setSubmitterId(SessionContextHolder.getContext().getVisionId()+"");
		vObjectBsd.setRunItAt(207);
		vObjectBsd.setProgramTypeAt(201);
		vObjectBsd.setProgramType(pBuildControlsVb.getProgramType());
		vObjectBsd.setModuleStatusAt(211);
		vObjectBsd.setModuleStatus("P");
		vObjectBsd.setExpandFlag(pBuildControlsVb.getExpandFlag());
		vObjectBsd.setRunIt(pBuildControlsVb.getRunIt());
		vObjectBsd.setProgressPercent("0");
		vObjectBsd.setDebugMode(pBuildControlsVb.getDebug());
		vObjectBsd.setFullBuildFlag(pBuildControlsVb.getFullBuild());
		vObjectBsd.setStartTime("");
		vObjectBsd.setEndTime("");
		vObjectBsd.setDateLastModified("");
		vObjectBsd.setDateCreation("");
	}
	public ExceptionCode terminateBuild(ExceptionCode pExceptionCode, List<BuildSchedulesVb> pBuildSchedules){
		ExceptionCode exceptionCode  = null;
		TelnetConnection telnetConnection = null;
		BuildSchedulesVb pBuildSchedulesVb = (BuildSchedulesVb) pExceptionCode.getOtherInfo();
		try
		{
			setProcessDirFromDB();
			if(isSecuredFtp()){
				return terminateSecuredBuild(pExceptionCode,pBuildSchedules);
			}
			String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if(!ValidationUtil.isValid(environmentParam))
				environmentParam="UAT";
			List<BuildSchedulesVb> lBuildSchList = new ArrayList<BuildSchedulesVb>(pBuildSchedules);
			for(BuildSchedulesVb lBuildSchedulesVb : lBuildSchList){
				if(lBuildSchedulesVb.isChecked()){
					String node=lBuildSchedulesVb.getNode();
					String accHostName =getBuildSchedulesDao().getServerCredentials( environmentParam, node, "NODE_IP");
					telnetConnection = new TelnetConnection(accHostName, userName, password, processDir, prompt);
					telnetConnection.connect();
					telnetConnection.killBuild(lBuildSchedulesVb.getBuildNumber());
				}
			}
			Thread.sleep(5000);
		}catch(ConnectException E){
			E.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.ERRONEOUS_OPERATION, "Kill bild", E.getMessage());
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.SUCCESSFUL_OPERATION, "Kill bild", "");
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}finally{
			if(telnetConnection != null && telnetConnection.isConnected()){
				telnetConnection.disconnect();
				telnetConnection = null;
			}
		}
		return getQueryResults(pBuildSchedulesVb);
	}
	private ExceptionCode terminateSecuredBuild(ExceptionCode pExceptionCode, List<BuildSchedulesVb> pBuildSchedules) {
		ExceptionCode exceptionCode  = null;
		BuildSchedulesVb pBuildSchedulesVb = (BuildSchedulesVb) pExceptionCode.getOtherInfo();
		Session session = null;
		Channel channel = null;
		try
		{
			JSch jsch = new JSch(); 
			String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if(!ValidationUtil.isValid(environmentParam))
				environmentParam="UAT";
			List<BuildSchedulesVb> lBuildSchList = new ArrayList<BuildSchedulesVb>(pBuildSchedules);
			for(BuildSchedulesVb lBuildSchedulesVb : lBuildSchList){
				if(lBuildSchedulesVb.isChecked()){
					String node=lBuildSchedulesVb.getNode();
					String accHostName =getBuildSchedulesDao().getServerCredentials( environmentParam, node, "NODE_IP");
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
					commander.println("cd "+processDir);
					commander.println("killbuild "+lBuildSchedulesVb.getBuildNumber()+";");
					commander.println("exit");
					commander.close();
					do {Thread.sleep(1000); } while(!channel.isEOF());
				}
				/*do {Thread.sleep(1000); } while(!channel.isEOF()); */
			}			
		}catch(ConnectException E){
			E.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.ERRONEOUS_OPERATION, "Kill bild", E.getMessage());
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.SUCCESSFUL_OPERATION, "Kill bild", "");
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}finally{
			if(channel != null && channel.isConnected()){
				channel.disconnect();
				channel = null;
			}
			if(session != null && session.isConnected()){
				session.disconnect();
				session = null;
			}
		}
		return getQueryResults(pBuildSchedulesVb);
	}
	private ExceptionCode terminateSecuredBuildOld(ExceptionCode pExceptionCode, List<BuildSchedulesVb> pBuildSchedules) {
		ExceptionCode exceptionCode  = null;
		BuildSchedulesVb pBuildSchedulesVb = (BuildSchedulesVb) pExceptionCode.getOtherInfo();
		Session session = null;
		Channel channel = null;
		try
		{
			JSch jsch = new JSch(); 
			session = jsch.getSession(getUserName(), getHostName(), 22);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config); 
			session.setPassword(getPassword()); 
			session.connect(); 
			channel = session.openChannel("shell");  
			OutputStream inputstream_for_the_channel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			channel.connect();
			commander.println("cd "+processDir);
			List<BuildSchedulesVb> lBuildSchList = new ArrayList<BuildSchedulesVb>(pBuildSchedules);
			for(BuildSchedulesVb lBuildSchedulesVb : lBuildSchList){
				if(lBuildSchedulesVb.isChecked()){
					commander.println("killbuild "+lBuildSchedulesVb.getBuildNumber()+";");
				}
			}
			commander.println("exit");
			commander.close();
			do {Thread.sleep(1000); } while(!channel.isEOF()); 
			
		}catch(ConnectException E){
			E.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.ERRONEOUS_OPERATION, "Kill bild", E.getMessage());
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.SUCCESSFUL_OPERATION, "Kill bild", "");
			exceptionCode.setResponse(pBuildSchedules);
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}finally{
			if(channel != null && channel.isConnected()){
				channel.disconnect();
				channel = null;
			}
			if(session != null && session.isConnected()){
				session.disconnect();
				session = null;
			}
		}
		return getQueryResults(pBuildSchedulesVb);
	}
	private ExceptionCode startSecuredCron(BuildSchedulesVb vObject){
		ExceptionCode exceptionCode  = null;
		Session session = null;
		Channel channel = null;
		try
		{
			JSch jsch = new JSch(); 
			session = jsch.getSession(getUserName(), getHostName(), 22);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config); 
			session.setPassword(getPassword()); 
			session.connect(); 
			channel = session.openChannel("shell");  
			OutputStream inputstream_for_the_channel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			channel.connect();
			commander.println("cd "+processDir);
			commander.println("runcron.sh");
			commander.println("exit");
			commander.close();
			do {Thread.sleep(1000);} while(!channel.isEOF()); 
			Thread.sleep(1000);
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.ERRONEOUS_OPERATION, "Start Cron", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}finally{
			if(channel != null && channel.isConnected()){
				channel.disconnect();
				channel = null;
			}
			if(session != null && session.isConnected()){
				session.disconnect();
				session = null;
			}
		}
		return getQueryResults(vObject);
	}
	public ExceptionCode startCron(BuildSchedulesVb vObject){
		setProcessDirFromDB();
		ExceptionCode exceptionCode = null;
		TelnetConnection telnetConnection = null;
		if(isSecuredFtp()){
			return startSecuredCron(vObject);
		}else{
			try{
				String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
				if(!ValidationUtil.isValid(environmentParam))
					environmentParam="UAT";
				String startCronNode=vObject.getStartCronNode().trim();
				String accHostName=getBuildSchedulesDao().getServerCredentials( environmentParam, "N"+startCronNode, "NODE_IP");
				telnetConnection = new TelnetConnection(accHostName, userName, password, processDir, prompt);
				telnetConnection.connect();
				telnetConnection.startCron();
				Thread.sleep(5000);
			}catch(Exception rex){
				rex.printStackTrace();
				exceptionCode = CommonUtils.getResultObject("Build Schedules", Constants.ERRONEOUS_OPERATION, "Start Cron", rex.getMessage());
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}finally{
				if(telnetConnection != null && telnetConnection.isConnected()){
					telnetConnection.disconnect();
					telnetConnection = null;
				}
			}
		}
		return getQueryResults(vObject);
	}
	private void setProcessDirFromDB(){
    	String uploadLogFilePathFromDB = getVisionVariableValue("BUILDCRON_EXECUTABLES_PATH");
    	if(uploadLogFilePathFromDB != null && !uploadLogFilePathFromDB.isEmpty()){
    		processDir = uploadLogFilePathFromDB;
    	}
    }
	public BuildSchedulesDao getBuildSchedulesDao() {
		return buildSchedulesDao;
	}

	public void setBuildSchedulesDao(BuildSchedulesDao buildSchedulesDao) {
		this.buildSchedulesDao = buildSchedulesDao;
	}

	public BuildSchedulesDetailsDao getBuildSchedulesDetailsDao() {
		return buildSchedulesDetailsDao;
	}

	public void setBuildSchedulesDetailsDao(
			BuildSchedulesDetailsDao buildSchedulesDetailsDao) {
		this.buildSchedulesDetailsDao = buildSchedulesDetailsDao;
	}

	public ProgramsDao getProgramsDao() {
		return programsDao;
	}

	public void setProgramsDao(ProgramsDao programsDao) {
		this.programsDao = programsDao;
	}

	public BuildControlsDao getBuildControlsDao() {
		return buildControlsDao;
	}

	public void setBuildControlsDao(BuildControlsDao buildControlsDao) {
		this.buildControlsDao = buildControlsDao;
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

	public String getKnownHostsFileName() {
		return knownHostsFileName;
	}

	public void setKnownHostsFileName(String knownHostsFileName) {
		this.knownHostsFileName = knownHostsFileName;
	}

	public String getProcessDir() {
		return processDir;
	}

	public void setProcessDir(String processDir) {
		this.processDir = processDir;
	}
	public BuildControlsStatsDao getBuildControlsStatsDao() {
		return buildControlsStatsDao;
	}

	public void setBuildControlsStatsDao(BuildControlsStatsDao buildControlsStatsDao) {
		this.buildControlsStatsDao = buildControlsStatsDao;
	}

	public char getPrompt() {
		return prompt;
	}

	public void setPrompt(char prompt) {
		this.prompt = prompt;
	}
	
	
	public ExceptionCode listFilesFromFtpServer(int dirType,String orderBy,String currentNodeName){
		ExceptionCode exceptionCode  = null;
		TelnetConnection telnetConnection = null;
		String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
		if(!ValidationUtil.isValid(environmentParam))
			environmentParam="UAT";
        try{ 
        	String accHostName =getBuildSchedulesDao().getServerCredentials( environmentParam, currentNodeName, "NODE_IP");
        	setUploadDownloadDirFromDB();
        	if(isSecuredFtp()){
        		return listFilesFromSFtpServer(dirType,orderBy,accHostName);
        	}
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			telnetConnection = new TelnetConnection(accHostName,userName,password, prompt);
			//telnetConnection.connect(getServerType());
			telnetConnection.connect();
			if(dirType == DIR_TYPE_DOWNLOAD)
				telnetConnection.sendCommand("cd "+downloadDir);
			else
				telnetConnection.sendCommand("cd "+buildLogsDir);
			String responseStr = telnetConnection.sendCommand("ls -ltc ");
			String[] fileEntryArray  = responseStr.split("\r\n");
			FTPClientConfig conf = new FTPClientConfig(getServerType());
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			List<String> fileEntryList = unixFtpEntryParser.preParse(new LinkedList<String>(Arrays.asList(fileEntryArray)));
			List<FTPFile> lfiles = new ArrayList<FTPFile>(fileEntryList.size());
			for(String fileEntry:fileEntryList){
				FTPFile ftpFile = unixFtpEntryParser.parseFTPEntry(fileEntry);
				if(ftpFile != null)
					lfiles.add(ftpFile);
			}
			for(FTPFile fileName:lfiles){
				if(fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar")) continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
				if(dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"_")>0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if(dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")<=0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if((dirType == 2 || dirType == 1)&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")>0)){
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}
	    	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
	    	exceptionCode.setResponse(createParentChildRelations(lFileList,orderBy,dirType));
        }
       catch(FileNotFoundException e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch(IOException e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch (Exception e) {
			e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
		}finally{
        	if(telnetConnection != null && telnetConnection.isConnected()){
        		telnetConnection.disconnect();
        		telnetConnection = null;
        	}
        }
        return exceptionCode;
	}
	private String formatFileSize(long numSize) {
        String strReturn;
        BigDecimal lSize = BigDecimal.valueOf((numSize)).divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
        if(lSize.floatValue() <=0){
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
	public ExceptionCode listFilesFromSFtpServer(int dirType,String orderBy,String accHostName){
		ExceptionCode exceptionCode  = null;
        try{
        	setUploadDownloadDirFromDB();
        	JSch jsch = new JSch();  
			jsch.setKnownHosts( getKnownHostsFileName() );
			Session session = jsch.getSession( getUserName(), accHostName ); 
			{   // "interactive" version   // can selectively update specified known_hosts file    
				// need to implement UserInfo interface  
				// MyUserInfo is a swing implementation provided in    
				//  examples/Sftp.java in the JSch dist   
				UserInfo ui = new MyUserInfo();   
				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
				session.setPassword( getPassword());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel( "sftp" ); 
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			if(dirType == DIR_TYPE_DOWNLOAD){
				sftpChannel.cd(downloadDir);
			}else{
				sftpChannel.cd(buildLogsDir);
			}
			Vector<ChannelSftp.LsEntry> vtc=  sftpChannel.ls("*.*");
			sftpChannel.disconnect();
			session.disconnect();
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			FTPClientConfig conf = new FTPClientConfig(getServerType());
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			List<FTPFile> lfiles = new ArrayList<FTPFile>(vtc.size());
			for(ChannelSftp.LsEntry lsEntry :  vtc) {
				FTPFile file= unixFtpEntryParser.parseFTPEntry(lsEntry.getLongname());
				if(file != null)
					lfiles.add(file);
			}
			for(FTPFile fileName:lfiles){
				if(fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar")) continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
/*				if(dirType == 1){
					fileInfoVb.setDate(formatDate(fileName));
					lFileList.add(fileInfoVb);
				}*/
				if(dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"_")>0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if(dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")<=0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if((dirType == 2 || dirType == 1)&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")>0)){
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}
	    	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
	    	exceptionCode.setResponse(createParentChildRelations(lFileList,orderBy,dirType));
        }
       catch(FileNotFoundException e){
    	   	e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch(IOException e){
        	e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch (Exception e) {
			e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
		}
        return exceptionCode;
	}
	 private void setUploadDownloadDirFromDB(){
	    	String uploadLogFilePathFromDB = getVisionVariableValue("VISION_XLUPL_LOG_FILE_PATH");
	    	if(uploadLogFilePathFromDB != null && !uploadLogFilePathFromDB.isEmpty()){
	    		downloadDir = uploadLogFilePathFromDB;
	    	}
	    	String uploadDataFilePathFromDB = getVisionVariableValue("VISION_XLUPL_DATA_FILE_PATH");
	    	if(uploadDataFilePathFromDB != null && !uploadDataFilePathFromDB.isEmpty()){
	    		uploadDir = uploadDataFilePathFromDB;
	    	}
	    	String buildLogsFilePathFromDB = getVisionVariableValue("BUILDCRON_LOG_FILE_PATH");
	    	if(buildLogsFilePathFromDB != null && !buildLogsFilePathFromDB.isEmpty()){
	    		buildLogsDir = buildLogsFilePathFromDB;
	    	}
	    	String scriptPathFromDB = getVisionVariableValue("BUILDCRON_SCRIPTS_PATH");
	    	if(scriptPathFromDB != null && !scriptPathFromDB.isEmpty()){
	    		scriptDir = scriptPathFromDB;
	    	}
	    	String uploadFileChkIntervelFromDB = getVisionVariableValue("VISION_XLUPL_FILE_CHK_INTVL");
	    	if(uploadFileChkIntervelFromDB != null && !uploadFileChkIntervelFromDB.isEmpty()){
	    		if(ValidationUtil.isValidId(uploadFileChkIntervelFromDB))
	    			uploadFileChkIntervel = Integer.valueOf(uploadFileChkIntervelFromDB);
	    	}
	    	String uploadDataFilePathFromCB = getVisionVariableValue("VISION_XLCB_DATA_FILE_PATH");
	    	if(uploadDataFilePathFromCB != null && !uploadDataFilePathFromCB.isEmpty()){
	    		cbDir = uploadDataFilePathFromCB;
	    	}
	    	
	    }
	 private String formatDate1(FTPFile fileName) {
			String fileName1  = fileName.getName();
			String year = fileName1.substring(fileName1.length()-14,fileName1.length()-10);
			String month  = fileName1.substring(fileName1.length()-9,fileName1.length()-7);
			String day = fileName1.substring(fileName1.length()-6,fileName1.length()-4); 
	        return CommonUtils.getFixedLength(day, "0", 2)+"-"+CommonUtils.getFixedLength(month, "0", 2)+"-"+year;
	    }	
	 private List<FileInfoVb> createParentChildRelations(List<FileInfoVb> legalTreeList,String orderBy,int dirType) throws IOException {
	        List<FileInfoVb> lResult = new ArrayList<FileInfoVb>(0);
	        Set set =new HashSet<FileInfoVb>();
	        //Top Roots are added. 
	        for(FileInfoVb fileVb:legalTreeList){
	        	if("Date".equalsIgnoreCase(orderBy)){
		        	//String date = fileVb.getName().substring(fileVb.getName().length()-14, fileVb.getName().length()-4);
		        	if(fileVb.getDate()!=null && fileVb.getDate()!=""){
		        		set.add(fileVb.getDate());
		             }
	        	}else{
	        		String fileNeme = "";
	        		if(dirType==2){
	        			int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
		        		if(cout == 0){
		        			int cout1 = StringUtils.countOccurrencesOf(fileVb.getName(), "-");
		        			if(cout1 == 0)
		        				fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
		        			else
		        				fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 15);
		        		}else if(cout>1)
		        			fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 21);
	        		}else{
	        			int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
	        			if(cout==1){
	        				fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
	        			}else if(cout>1){
	        				fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 20);
	        			}
	        		}
		        	if(fileNeme!=null && fileNeme.length()>0){
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
	        //For each top node add all child's and to that child's add sub child's recursively.
	        for(FileInfoVb legalVb:lResult){
	              addChilds(legalVb,legalTreeList,orderBy,dirType);
	        }
	       if("Date".equalsIgnoreCase(orderBy)){
	           final SimpleDateFormat dtF= new SimpleDateFormat("dd-MM-yyyy");
		        //set the empty lists to null. this is required for UI to display the leaf nodes properly.
		        Collections.sort(lResult, new Comparator<FileInfoVb>(){
		            public int compare (FileInfoVb m1, FileInfoVb m2){
		            	try {
							return dtF.parse(m1.getDescription()).compareTo(dtF.parse(m2.getDescription()));
						} catch (ParseException e) {
							return 0;
						}
		            }
		        });
		        Collections.reverse(lResult);
	       }else{
		        Collections.sort(lResult, new Comparator<FileInfoVb>(){
		            public int compare (FileInfoVb m1, FileInfoVb m2){
		            	return m1.getDescription().compareTo(m2.getDescription());
		            }
		        });
	       }
	        return lResult;
	    }
	 public void addChilds(FileInfoVb vObject, List<FileInfoVb> legalTreeListCopy,String orderBy,int dirType) {
	        for(FileInfoVb fileTreeVb:legalTreeListCopy){
	        	if("Date".equalsIgnoreCase(orderBy)){
		        	//String date = fileTreeVb.getName().substring(fileTreeVb.getName().length()-14, fileTreeVb.getName().length()-4);
			        if(vObject.getDescription().equalsIgnoreCase(fileTreeVb.getDate())){
			        	if(vObject.getChildren() == null){
			        		vObject.setChildren(new ArrayList<FileInfoVb>(0));
			             }
			            fileTreeVb.setDescription(fileTreeVb.getName());
			            vObject.getChildren().add(fileTreeVb);
			         }
	        	}else{
	        		String fileNeme = "";
	        		if(dirType==2){
	        			int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
		        		if(cout == 0){
		        			int cout1 = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "-");
		        			if(cout1 == 0)
		        				fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
		        			else
		        				fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 15);
		        		}else if(cout>1)
		        			fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 21);
	        		}else{
	        			int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
	        			if(cout==1){
	        				fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
	        			}else if(cout>1){
	        				fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 20);
	        			}        			
	        		}
			        if(vObject.getDescription().equalsIgnoreCase(fileNeme)){
			        	if(vObject.getChildren() == null){
			        		vObject.setChildren(new ArrayList<FileInfoVb>(0));
			             }
			            fileTreeVb.setDescription(fileTreeVb.getName());
			            vObject.getChildren().add(fileTreeVb);
			         }
	        	}
	        }
	    }
	 public class MyUserInfo implements UserInfo{
		    public String getPassword(){ return password; }
		    public boolean promptYesNo(String str){
		      return false;
		    }
		    public String getPassphrase(){ return null; }
		    public boolean promptPassphrase(String message){ return true; }
		    public boolean promptPassword(String message){ return false; }
		    
		    public void showMessage(String message){
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
}
class SubbuildNumberStore{
	private Integer lActualSubBuildNumber =1;
	private Integer lAssignedSubBuildNumber =1;
	private ArrayList<Integer> lBsdSequenceList = new ArrayList<Integer>();
	public Integer getLActualSubBuildNumber() {
		return lActualSubBuildNumber;
	}
	public void setLActualSubBuildNumber(Integer actualSubBuildNumber) {
		lActualSubBuildNumber = actualSubBuildNumber;
	}
	public Integer getLAssignedSubBuildNumber() {
		return lAssignedSubBuildNumber;
	}
	public void setLAssignedSubBuildNumber(Integer assignedSubBuildNumber) {
		lAssignedSubBuildNumber = assignedSubBuildNumber;
	}
	public ArrayList<Integer> getLBsdSequenceList() {
		return lBsdSequenceList;
	}
	public void setLBsdSequenceList(ArrayList<Integer> bsdSequenceList) {
		lBsdSequenceList = bsdSequenceList;
	}
}
