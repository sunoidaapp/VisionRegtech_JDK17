package com.vision.wb;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.CronStatusDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CronStatusVb;

@Controller
public class CronStatusWb extends AbstractDynaWorkerBean<CronStatusVb>{
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
    private String scriptDir = "/home/vision/scripts";

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

	public String getScriptDir() {
		return scriptDir;
	}

	public void setScriptDir(String scriptDir) {
		this.scriptDir = scriptDir;
	}

	@Autowired
	private CronStatusDao cronStatusDao;
	
	public CronStatusDao getCronStatusDao() {
		return cronStatusDao;
	}

	public void setCronStatusDao(CronStatusDao cronStatusDao) {
		this.cronStatusDao = cronStatusDao;
	}

	public static Logger logger = LoggerFactory.getLogger(CronStatusWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		String environmentParam ="";
		CronStatusVb vObjCronData= null;
		try{
			environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if(!ValidationUtil.isValid(environmentParam))
 				environmentParam="UAT";
			
			arrListLocal.add(environmentParam);
			collTemp = getCronStatusDao().findEnvironment();
			arrListLocal.add(collTemp);
			if(!ValidationUtil.isValid(environmentParam)){
				if(collTemp!=null && collTemp.size()>0){
					vObjCronData = (CronStatusVb)collTemp.get(0);
					environmentParam=vObjCronData.getServerEnvironment().toUpperCase();
					environmentParam=environmentParam.replaceAll(" ", environmentParam);
				}
			}
			CronStatusVb vObject = new CronStatusVb();
			vObject.setVerificationRequired(false);
			List<CronStatusVb> coolTemp1 = getCronStatusDao().findCronStatusList(vObject, environmentParam);
			
			String serverName = "";
			String node = "";
			for(CronStatusVb vObj : coolTemp1){
				 if(serverName.equalsIgnoreCase(vObj.getServerName())){
					 vObj.setServerName("");
				 }else{
					 serverName = vObj.getServerName();
				 }
				 if(node.equalsIgnoreCase(vObj.getNodeName())){
					 vObj.setNodeName("");
				 }else{
					 node = vObj.getNodeName();
				 }
			}
			arrListLocal.add(coolTemp1);
			arrListLocal.add(vObject);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	public ExceptionCode startCron(CronStatusVb vObject){
		setProcessDirFromDB();
		ExceptionCode exceptionCode = null;
		TelnetConnection telnetConnection = null;
		
		if(!"BUILD_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"ADF_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"NON_ADF_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"PING_CRON".equalsIgnoreCase(vObject.getCronName())){

			String cronName = vObject.getCronName()+'_'+vObject.getServerName()+'_'+vObject.getNodeName();
			int status =getCronStatusDao().doUpdateStartCron(cronName);
			if(status!=1){
				exceptionCode = CommonUtils.getResultObject("Cron Status", 5, "Start Cron", cronName+" not found in Vision Variables");
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}else{
			if(isSecuredFtp()){
				return startSecuredCron(vObject);
			}else{
				try{
					String accHostName=vObject.getNodeIp().trim();
					String userName=vObject.getNodeUser().trim();
					String password=vObject.getNodePwd().trim();
					telnetConnection = new TelnetConnection(accHostName, userName, password, processDir, '>');
					telnetConnection.connect();
					telnetConnection.startCronStatus(vObject.getCronName());
					Thread.sleep(5000);
				}catch(Exception rex){
					rex.printStackTrace();
					exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Start Cron", rex.getMessage());
					exceptionCode.setOtherInfo(vObject);
					return exceptionCode;
				}finally{
					if(telnetConnection != null && telnetConnection.isConnected()){
						telnetConnection.disconnect();
						telnetConnection = null;
					}
				}
			}
		}
		return getQueryResults(vObject);
	}
	
	
	public ExceptionCode startSecuredCronALL(CronStatusVb vObject){
		ExceptionCode exceptionCode  = null;
		try
		{
			List<CronStatusVb> list = getCronStatusDao().getQueryResults(vObject);
			if(list !=null && !list.isEmpty()) {
				for(CronStatusVb coStatusVb : list) {
					startCron(coStatusVb);
				}
			}
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Start Cron", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		return getQueryResults(vObject);
	}
	
	public ExceptionCode stopSecuredCronALL(CronStatusVb vObject){
		ExceptionCode exceptionCode  = null;
		try
		{
			List<CronStatusVb> list = getCronStatusDao().getQueryResults(vObject);
			if(list !=null && !list.isEmpty()) {
				for(CronStatusVb coStatusVb : list) {
					stopCron(coStatusVb);
				}
			}
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Stop Cron", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		return getQueryResults(vObject);
	}
	public ExceptionCode terminateSecuredCronALL(CronStatusVb vObject){
		ExceptionCode exceptionCode  = null;
		try
		{
			List<CronStatusVb> list = getCronStatusDao().getQueryResults(vObject);
			if(list !=null && !list.isEmpty()) {
				for(CronStatusVb coStatusVb : list) {
					terminateCron(coStatusVb);
				}
			}
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Terminate Cron", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		return getQueryResults(vObject);
	}
	
	private ExceptionCode startSecuredCron(CronStatusVb vObject){
		/*ExceptionCode exceptionCode  = null;
		Session session = null;
		Channel channel = null;
		try
		{
			JSch jsch = new JSch(); 
			session = jsch.getSession(vObject.getNodeUser().trim(), vObject.getNodeIp().trim(), 22);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config); 
			session.setPassword(vObject.getNodePwd().trim()); 
			session.connect(); 
			channel = session.openChannel("shell");  
			OutputStream inputstream_for_the_channel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			channel.connect();
//			commander.println("cd "+scriptDir);
			if("BUILD_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("startBuildCron.sh");
			}else if("ADF_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("startADFCron.sh");
			}else if("NON_ADF_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("startNonADFCron.sh");
			}else if("PING_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("startPingCron.sh");
			}
			//commander.println("runcron.sh");
			commander.println("exit");
			commander.close();
			do {Thread.sleep(1000);} while(!channel.isEOF()); 
			Thread.sleep(1000);
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Start Cron", "");
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
		}*/
		int status =getCronStatusDao().doUpdateStopCron(vObject.getCronName(),"STARTED");
		getCronStatusDao().doInsertionAudit(vObject, "START");
		return getQueryResults(vObject);
	}
	
	public ExceptionCode stopCron(CronStatusVb vObject){
		setProcessDirFromDB();
		ExceptionCode exceptionCode = null;
		TelnetConnection telnetConnection = null;
		if(!"BUILD_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"ADF_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"NON_ADF_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"PING_CRON".equalsIgnoreCase(vObject.getCronName())){

			String cronName = vObject.getCronName()+'_'+vObject.getServerName()+'_'+vObject.getNodeName();
			int status =getCronStatusDao().doUpdateStopCron(cronName,"STOPPED");
			if(status!=1){
				exceptionCode = CommonUtils.getResultObject("Cron Status", 5, "Stop Cron", cronName+" not found in Vision Variables");
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}else{
			if(isSecuredFtp()){
				return stopSecuredCron(vObject);
			}else{
				try{
					String accHostName=vObject.getNodeIp().trim();
					String userName=vObject.getNodeUser().trim();
					String password=vObject.getNodePwd().trim();
					telnetConnection = new TelnetConnection(accHostName, userName, password, processDir, '>');
					telnetConnection.connect();
					telnetConnection.stopCronStatus(vObject.getCronName());
					Thread.sleep(5000);
				}catch(Exception rex){
					rex.printStackTrace();
					exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Stop Cron", rex.getMessage());
					exceptionCode.setOtherInfo(vObject);
					return exceptionCode;
				}finally{
					if(telnetConnection != null && telnetConnection.isConnected()){
						telnetConnection.disconnect();
						telnetConnection = null;
					}
				}
			}
		}
		return getQueryResults(vObject);
	}
	public ExceptionCode terminateCron(CronStatusVb vObject){
		setProcessDirFromDB();
		ExceptionCode exceptionCode = null;
		TelnetConnection telnetConnection = null;
		if(!"BUILD_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"ADF_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"NON_ADF_CRON".equalsIgnoreCase(vObject.getCronName())
				&& !"PING_CRON".equalsIgnoreCase(vObject.getCronName())){

			String cronName = vObject.getCronName()+'_'+vObject.getServerName()+'_'+vObject.getNodeName();
			int status =getCronStatusDao().doUpdateStopCron(cronName,"TERMINATED");
			if(status!=1){
				exceptionCode = CommonUtils.getResultObject("Cron Status", 5, "Terminate Cron", cronName+" not found in Vision Variables");
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}else{
			if(isSecuredFtp()){
				return terminateSecuredCron(vObject);
			}else{
				try{
					String accHostName=vObject.getNodeIp().trim();
					String userName=vObject.getNodeUser().trim();
					String password=vObject.getNodePwd().trim();
					telnetConnection = new TelnetConnection(accHostName, userName, password, processDir, '>');
					telnetConnection.connect();
					telnetConnection.terminateCronStatus(vObject.getCronName());
					Thread.sleep(5000);
				}catch(Exception rex){
					rex.printStackTrace();
					exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Terminate Cron", rex.getMessage());
					exceptionCode.setOtherInfo(vObject);
					return exceptionCode;
				}finally{
					if(telnetConnection != null && telnetConnection.isConnected()){
						telnetConnection.disconnect();
						telnetConnection = null;
					}
				}
			}
		}
		return getQueryResults(vObject);
	}
	

	private ExceptionCode stopSecuredCron(CronStatusVb vObject) {
		/*ExceptionCode exceptionCode  = null;
		Session session = null;
		Channel channel = null;
		try
		{
			JSch jsch = new JSch(); 
			session = jsch.getSession(vObject.getNodeUser().trim(), vObject.getNodeIp().trim(), 22);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config); 
			session.setPassword(vObject.getNodePwd().trim()); 
			session.connect(); 
			channel = session.openChannel("shell");  
			OutputStream inputstream_for_the_channel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			channel.connect();
			//commander.println("cd "+scriptDir);
			if("BUILD_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("stopBuildCron.sh");
			}else if("ADF_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("stopADFCron.sh");
			}else if("NON_ADF_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("stopNonADFCron.sh");
			}else if("PING_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("stopPingCron.sh");
			}
			
			//commander.println("stopcron.sh");
			commander.println("exit");
			commander.close();
			do {Thread.sleep(1000); } while(!channel.isEOF()); 
			
		}catch(ConnectException E){
			E.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Stop cron", E.getMessage());
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.SUCCESSFUL_OPERATION, "Stop cron", "");
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
		}*/
		int status =getCronStatusDao().doUpdateStopCron(vObject.getCronName(),"STOPPED");
		getCronStatusDao().doInsertionAudit(vObject, "STOP");
		return getQueryResults(vObject);
	}
	private ExceptionCode terminateSecuredCron(CronStatusVb vObject) {
		/*ExceptionCode exceptionCode  = null;
		Session session = null;
		Channel channel = null;
		try
		{
			JSch jsch = new JSch(); 
			session = jsch.getSession(vObject.getNodeUser().trim(), vObject.getNodeIp().trim(), 22);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config); 
			session.setPassword(vObject.getNodePwd().trim()); 
			session.connect(); 
			channel = session.openChannel("shell");  
			OutputStream inputstream_for_the_channel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			channel.connect();
			//commander.println("cd "+scriptDir);
			if("BUILD_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("terminateBuildCron.sh");
			}else if("ADF_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("terminateterminateADFCron.sh");
			}else if("NON_ADF_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("terminateNonADFCron.sh");
			}else if("PING_CRON".equalsIgnoreCase(vObject.getCronName().trim())){
				commander.println("terminatePingCron.sh");
			}
			
			//commander.println("terminatecron.sh");
			commander.println("exit");
			commander.close();
			do {Thread.sleep(1000); } while(!channel.isEOF()); 
			
		}catch(ConnectException E){
			E.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.ERRONEOUS_OPERATION, "Terminate cron", E.getMessage());
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(Exception rex){
			rex.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Cron Status", Constants.SUCCESSFUL_OPERATION, "Terminate cron", "");
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
		}*/
		int status =getCronStatusDao().doUpdateStopCron(vObject.getCronName(),"TERMINATED");
		getCronStatusDao().doInsertionAudit(vObject, "TERMINATE");
		return getQueryResults(vObject);
	}
		
	@Override
	public ExceptionCode getQueryResults(CronStatusVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		vObject.setTotalRows(0);
		List<CronStatusVb> collTemp = getCronStatusDao().getQueryResults(vObject,intStatus);
		String serverName = "";
		String node = "";
		for(CronStatusVb vObj : collTemp){
			 if(serverName.equalsIgnoreCase(vObj.getServerName())){
				 vObj.setServerName("");
			 }else{
				 serverName = vObj.getServerName();
			 }
			 if(node.equalsIgnoreCase(vObj.getNodeName())){
				 vObj.setNodeName("");
			 }else{
				 node = vObj.getNodeName();
			 }
		}
		
		String cronNameDelimited="";
		String cronStatusDelimited="";
		String cronStatusFromDB = getVisionVariableValue("VISION_BUILD_CRON");
		if(cronStatusFromDB == null || cronStatusFromDB.isEmpty()){
			cronStatusFromDB = "Stopped";
		}
		if(!("STOPPED".equalsIgnoreCase(cronStatusFromDB) || "Stopping".equalsIgnoreCase(cronStatusFromDB))){
			if(getCronStatusDao().getLastFetchIntervel() > 2){
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
		String dbDateTime = getCronStatusDao().getSystemDate();
		dbDateTime = dbDateTime.replace('/', '-');
		//vObject.setDbDateTime(dbDateTime);
		vObject.setCronName(cronNameDelimited);
		vObject.setCronStatus(cronStatusDelimited);
		//vObject.setCronCount(getBuildSchedulesDao().findCronCount(environmentParam));
		ExceptionCode exceptionCode = CommonUtils.getResultObject(getCronStatusDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
		exceptionCode.setOtherInfo(vObject);
		exceptionCode.setResponse(collTemp);
		return exceptionCode;
	}
	private void setProcessDirFromDB(){
    	String uploadLogFilePathFromDB = getVisionVariableValue("BUILDCRON_EXECUTABLES_PATH");
    	if(uploadLogFilePathFromDB != null && !uploadLogFilePathFromDB.isEmpty()){
    		processDir = uploadLogFilePathFromDB;
    	}
    }
	@Override
	protected AbstractDao<CronStatusVb> getScreenDao() {
		return cronStatusDao;
	}

	@Override
	protected void setAtNtValues(CronStatusVb object) {
		object.setRecordIndicator(7);
		object.setCronStatusAt(210);
	}

	@Override
	public void setVerifReqDeleteType(CronStatusVb object) {
		object.setVerificationRequired(false);
	}

}