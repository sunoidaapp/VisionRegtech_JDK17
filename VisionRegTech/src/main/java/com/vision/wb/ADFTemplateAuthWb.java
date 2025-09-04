package com.vision.wb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.vision.dao.ADFTemplateAuthDao;
import com.vision.dao.AbstractDao;
import com.vision.dao.VisionUploadDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.ADFTemplateAuthVb;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
@Controller
public class ADFTemplateAuthWb extends AbstractDynaWorkerBean<ADFTemplateAuthVb>{
	@Autowired
	private ADFTemplateAuthDao adfTemplateAuthDao;
	
	public ADFTemplateAuthDao getAdfTemplateAuthDao() {
		return adfTemplateAuthDao;
	}

	public void setAdfTemplateAuthDao(ADFTemplateAuthDao adfTemplateAuthDao) {
		this.adfTemplateAuthDao = adfTemplateAuthDao;
	}
	public static Logger logger = LoggerFactory.getLogger(ADFTemplateAuthWb.class);
	
	@Autowired
	private VisionUploadDao visionUploadDao;
	
	private String downloadDir;
	private String currentNode;
	
	@Value("${ftp.hostName}")
	private String hostName;

	@Value("${ftp.userName}")
	private String userName;

	@Value("${ftp.password}")
	private String password;

	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1084);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1065);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1077);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1079);
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("ADF_TEMPLATE_AUTH");
			arrListLocal.add(collTemp);
			ADFTemplateAuthVb adfTemplateAuthVb = new ADFTemplateAuthVb();
			List<ADFTemplateAuthVb> queryList = getQueryPopupResults(adfTemplateAuthVb);
			arrListLocal.add(queryList);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	
	@Override
	public ExceptionCode getQueryResults(ADFTemplateAuthVb vObject){
 		setVerifReqDeleteType(vObject);
		String leBook = getAdfTemplateAuthDao().getLeBook(vObject);
		vObject.setLeBook("");
		vObject.setLeBook(leBook);
		
		String legalVehicle = getAdfTemplateAuthDao().getLegalVehicle(vObject);
		vObject.setLegalVehicle(legalVehicle);
		String templateName = "ALL";
		if(ValidationUtil.isValid(vObject.getTemplateName())){
			templateName = vObject.getTemplateName();
		}
		ExceptionCode exceptionCodeNew = getAdfTemplateAuthDao().getQueryResults(vObject, templateName);
		List<ADFTemplateAuthVb> collTemp = (List<ADFTemplateAuthVb>) exceptionCodeNew.getResponse();
		if(collTemp == null || collTemp.size() == 0){
			exceptionCodeNew.setErrorCode(Constants.NO_RECORDS_FOUND);
		//	ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 41, "Query", "");
			exceptionCodeNew.setOtherInfo(vObject);
			exceptionCodeNew.setErrorMsg("No Records Found");
			return exceptionCodeNew;
		}else{
			doSetDesctiptionsAfterQuery(collTemp);
			doSetDesctiptionsAfterQuery(vObject);
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("ADF Template Auth - Query - Successful");
			return exceptionCode;
		}
	}
	
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<ADFTemplateAuthVb> approvedCollection, List<ADFTemplateAuthVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook());
		lResult.add(lLeBook);
		ReviewResultVb lprocessFrequency = new ReviewResultVb(rsb.getString("processFrequency"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProcessFrequency(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProcessFrequency());
		lResult.add(lprocessFrequency);
		ReviewResultVb lBusinessDate = new ReviewResultVb(rsb.getString("businessDate"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessDate(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessDate());
		lResult.add(lBusinessDate);
		ReviewResultVb lTemplateName = new ReviewResultVb(rsb.getString("templateName"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTemplateName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTemplateName());
		lResult.add(lTemplateName);
		/*ReviewResultVb recordsfetchedcount = new ReviewResultVb(rsb.getString("recordCount"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRecordCount() == -1?"":String.valueOf(pendingCollection.get(0).getRecordCount()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordCount() == -1?"":String.valueOf(approvedCollection.get(0).getRecordCount()));
	    lResult.add(recordsfetchedcount);*/
		ReviewResultVb lStartTime = new ReviewResultVb(rsb.getString("startTime"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getStartTime(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getStartTime());
		lResult.add(lStartTime);
		ReviewResultVb lEndTime = new ReviewResultVb(rsb.getString("endTime"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getEndTime(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getEndTime());
		lResult.add(lEndTime);
		ReviewResultVb lTimeCollapsed = new ReviewResultVb(rsb.getString("timeCollapsed"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTimeCollapsed(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTimeCollapsed());
		lResult.add(lTimeCollapsed);
		ReviewResultVb lComment = new ReviewResultVb(rsb.getString("comment"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getComment(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getComment());
		lResult.add(lComment);
		ReviewResultVb lReview = new ReviewResultVb(rsb.getString("review"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getReview(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getReview());
		lResult.add(lReview);
		ReviewResultVb lADFTempAuthStatus = new ReviewResultVb(rsb.getString("status"),(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>) collTemp.get(0),pendingCollection.get(0).getADFTempAuthStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>) collTemp.get(0),approvedCollection.get(0).getADFTempAuthStatus()));
		lResult.add(lADFTempAuthStatus);
		ReviewResultVb lAuthStatus = new ReviewResultVb(rsb.getString("authStatus"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(2),pendingCollection.get(0).getAuthStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(2),approvedCollection.get(0).getAuthStatus()));
		lResult.add(lAuthStatus);
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName());
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName());
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified());
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation());
		lResult.add(lDateCreation);
		return lResult;
	}
	
	protected AbstractDao<ADFTemplateAuthVb> getScreenDao() {
		return adfTemplateAuthDao;
	}
	@Override
	protected void setAtNtValues(ADFTemplateAuthVb vObject) {
		vObject.setADFTempAuthStatusAt(1084);
		vObject.setRecordIndicatorNt(7);			
	}
	@Override
	protected void setVerifReqDeleteType(ADFTemplateAuthVb object) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("ADF_TEMPLATE_AUTH");
		object.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		object.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	public ExceptionCode modifyRecord(ExceptionCode pRequestCode, List<ADFTemplateAuthVb> vObjects){
		ExceptionCode exceptionCode  = null;
		DeepCopy<ADFTemplateAuthVb> deepCopy = new DeepCopy<ADFTemplateAuthVb>();
		List<ADFTemplateAuthVb> clonedObject = null;
		ADFTemplateAuthVb vObject = null;
		try{
			setAtNtValues(vObjects);
			vObject = (ADFTemplateAuthVb) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = doValidate(vObjects);
			if(exceptionCode!=null && exceptionCode.getErrorMsg()!=""){
				return exceptionCode;
			}
			exceptionCode = getAdfTemplateAuthDao().doVerification(vObjects);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Modify Exception " + rex.getCode().getErrorMsg());
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}
	public ExceptionCode resubmissionRecord(ExceptionCode pRequestCode, List<ADFTemplateAuthVb> vObjects){
		ExceptionCode exceptionCode  = null;
		DeepCopy<ADFTemplateAuthVb> deepCopy = new DeepCopy<ADFTemplateAuthVb>();
		List<ADFTemplateAuthVb> clonedObject = null;
		ADFTemplateAuthVb vObject = null;
		try{
			setAtNtValues(vObjects);
			vObject = (ADFTemplateAuthVb) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = doValidate(vObjects);
			if(exceptionCode!=null && exceptionCode.getErrorMsg()!=""){
				return exceptionCode;
			}
			exceptionCode = getAdfTemplateAuthDao().doResubmitApprRecordADF(vObject,vObjects);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Modify Exception " + rex.getCode().getErrorMsg());
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}
	
	public ExceptionCode rejectRecord(ExceptionCode pRequestCode, List<ADFTemplateAuthVb> vObjects){
		ExceptionCode exceptionCode  = null;
		ExceptionCode exceptionCode1 =null;
		DeepCopy<ADFTemplateAuthVb> deepCopy = new DeepCopy<ADFTemplateAuthVb>();
		List<ADFTemplateAuthVb> clonedObject = null;
		ADFTemplateAuthVb vObject = null;
		try {
			setAtNtValues(vObjects);
			vObject = (ADFTemplateAuthVb) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = doValidate(vObjects);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				return exceptionCode;
			}
			exceptionCode = getAdfTemplateAuthDao().doRejectApprRecordADF(vObjects);
			if (exceptionCode.getErrorCode() == 1) {
				exceptionCode1 = getAdfTemplateAuthDao().doRejection(vObjects);
			}
			exceptionCode1.setOtherInfo(vObject);
			exceptionCode1.setResponse(vObjects);
			return exceptionCode1;
		}catch(RuntimeCustomException rex){
			logger.error("Reject Exception " + rex.getCode().getErrorMsg());
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}
	
	public ExceptionCode downloadExcelFileFromSFTP(String pFileNames) {
		ExceptionCode exceptionCode = new ExceptionCode();
		setUploadDownloadDirFromDB();
		Session session = null;
		String originalFileName = pFileNames+".xlsx";//RWF01_MTH_GLCODES_30-SEP-2016.xlsx
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(userName, hostName);
			{ 
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); 
				session.setPassword(password);
			}
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("channel connected");
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(downloadDir);

			String destFilePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(destFilePath))
				destFilePath = System.getenv("TMP");
			if(ValidationUtil.isValid(destFilePath))
				destFilePath = destFilePath + File.separator;
			
			File lFile = new File(destFilePath + originalFileName); //local 
			File lSourceFile = new File(originalFileName); 
			if(lFile.exists()){
				lFile.delete();
			}
			lFile.createNewFile();
			sftpChannel.get(lSourceFile.toString(), lFile.toString());
			exceptionCode.setResponse(destFilePath);
			exceptionCode.setOtherInfo(originalFileName);
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
	
	public void setUploadDownloadDirFromDB() {
    	String uploadLogFilePathFromDB = getVisionVariableValue("NON_ADF_XL_UPLOAD_PATH");
    	if(uploadLogFilePathFromDB != null && !uploadLogFilePathFromDB.isEmpty()){
    		downloadDir = uploadLogFilePathFromDB;
    	}
		String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
		if(!ValidationUtil.isValid(environmentParam))
			environmentParam="UAT";
		String environmentNode = System.getenv("VISION_NODE_NAME");
		if(!ValidationUtil.isValid(environmentNode))
			environmentNode="N3";
		if(ValidationUtil.isValid(getCurrentNode())) {
			environmentNode = getCurrentNode();
		}
    	String serverHost = visionUploadDao.getServerCredentials(environmentParam, environmentNode, "NODE_IP");
    	if(ValidationUtil.isValid(serverHost)){
    		hostName = serverHost;
    	}
    	String serverUserName = visionUploadDao.getServerCredentials(environmentParam, environmentNode, "NODE_USER");
    	if(ValidationUtil.isValid(serverUserName)){
    		userName = serverUserName;
    	}
    	String serverPassword = visionUploadDao.getServerCredentials(environmentParam, environmentNode, "NODE_PWD");
    	if(ValidationUtil.isValid(serverPassword)){
  		password = serverPassword;
    	//password = ValidationUtil.passwordDecrypt(serverPassword);
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

	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}


	public String getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	public ExceptionCode filterAdfTemplateAuth(ADFTemplateAuthVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<ADFTemplateAuthVb> collTemp = adfTemplateAuthDao.filterSchedules(vObject);
			if (collTemp == null) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setResponse(new ArrayList<>());
			} else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(collTemp);
				exceptionCode.setOtherInfo(vObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setResponse(new ArrayList<>());
		}
		return exceptionCode;
	}
	
}
