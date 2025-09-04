package com.vision.wb;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.TemplateNameDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TemplateNameVb;

@Component
public class TemplateNameWb extends AbstractWorkerBean<TemplateNameVb> {

	@Autowired
	private TemplateNameDao templateNameDao;
	public static Logger logger = LoggerFactory.getLogger(TemplateNameWb.class);

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1079);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(7601);
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	public ArrayList getPageLoadValuesForProgram(String program) {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1092);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1096);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1098);
			arrListLocal.add(collTemp);
			/*
			 * collTemp = getTemplateNameDao().getExecutionSequence(program);
			 * arrListLocal.add(collTemp);
			 */
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values for Template Design.", ex);
			return null;
		}
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<TemplateNameVb> approvedCollection,
			List<TemplateNameVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lTemplateName = new ReviewResultVb(rsb.getString("templateName"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getTemplateName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getTemplateName(),
						(!pendingCollection.get(0).getTemplateName()
								.equals(approvedCollection.get(0).getTemplateName())));
		lResult.add(lTemplateName);
		ReviewResultVb lGeneralDescription = new ReviewResultVb(rsb.getString("generalDescription"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGeneralDescription(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGeneralDescription(),
						(!pendingCollection.get(0).getGeneralDescription()
								.equals(approvedCollection.get(0).getGeneralDescription())));
		lResult.add(lGeneralDescription);
		ReviewResultVb lFeedCategory = new ReviewResultVb(rsb.getString("feedCategory"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFeedCategory(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFeedCategory(),
						(!pendingCollection.get(0).getFeedCategory()
								.equals(approvedCollection.get(0).getFeedCategory())));
		lResult.add(lFeedCategory);
		ReviewResultVb lStagingName = new ReviewResultVb(rsb.getString("stagingName"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFeedStgName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFeedStgName(),
						(!pendingCollection.get(0).getFeedStgName()
								.equals(approvedCollection.get(0).getFeedStgName())));
		lResult.add(lStagingName);
		ReviewResultVb lViewName = new ReviewResultVb(rsb.getString("viewName"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getViewName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getViewName(),
						(!pendingCollection.get(0).getViewName()
								.equals(approvedCollection.get(0).getViewName())));
		lResult.add(lViewName);
		ReviewResultVb lEffectiveDate = new ReviewResultVb(rsb.getString("effectiveDate"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getEffectiveDate(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getEffectiveDate(),
						(!pendingCollection.get(0).getEffectiveDate()
								.equals(approvedCollection.get(0).getEffectiveDate())));
		lResult.add(lEffectiveDate);
		ReviewResultVb lFreezeStatus = new ReviewResultVb(rsb.getString("defaultAcquProcessType"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
								pendingCollection.get(0).getDefaultAcquProcessType()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
								approvedCollection.get(0).getDefaultAcquProcessType()),
						(!pendingCollection.get(0).getDefaultAcquProcessType()
								.equals(approvedCollection.get(0).getDefaultAcquProcessType())));
		lResult.add(lFreezeStatus);
		ReviewResultVb lProgram = new ReviewResultVb(rsb.getString("program"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getProgram(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getProgram(),
						(!pendingCollection.get(0).getProgram()
								.equals(approvedCollection.get(0).getProgram())));
		lResult.add(lProgram);
		ReviewResultVb lProcessSequence = new ReviewResultVb("Process Sequence",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getProcessSequence(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getProcessSequence(),
						(!pendingCollection.get(0).getProcessSequence()
								.equals(approvedCollection.get(0).getProcessSequence())));
		lResult.add(lProcessSequence);
		ReviewResultVb lFilePattern = new ReviewResultVb("File Pattern",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFilePattern(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFilePattern(),
						(!pendingCollection.get(0).getFilePattern()
								.equals(approvedCollection.get(0).getFilePattern())));
		lResult.add(lFilePattern);
		ReviewResultVb lExcelFilePattern = new ReviewResultVb("Excel File Pattern",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getExcelFilePattern(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getExcelFilePattern(),
						(!pendingCollection.get(0).getExcelFilePattern()
								.equals(approvedCollection.get(0).getExcelFilePattern())));
		lResult.add(lExcelFilePattern);
		ReviewResultVb lExcelTemplateID = new ReviewResultVb("Excel Template ID",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getExcelTemplateId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getExcelTemplateId(),
						(!pendingCollection.get(0).getExcelTemplateId()
								.equals(approvedCollection.get(0).getExcelTemplateId())));
		lResult.add(lExcelTemplateID);
		ReviewResultVb lTemplateStatus = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								pendingCollection.get(0).getTemplateStatus()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								approvedCollection.get(0).getTemplateStatus()),
						(pendingCollection.get(0).getTemplateStatus()!=(approvedCollection.get(0).getTemplateStatus())));
		lResult.add(lTemplateStatus);
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								approvedCollection.get(0).getRecordIndicator()),
						(pendingCollection.get(0).getRecordIndicator()!=(approvedCollection.get(0).getRecordIndicator())));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
								(pendingCollection.get(0).getMaker()!=(approvedCollection.get(0).getMaker())));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
								(pendingCollection.get(0).getVerifier()!=(approvedCollection.get(0).getVerifier())));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateLastModified(),
						(!pendingCollection.get(0).getDateLastModified()
								.equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateCreation(),
						(!pendingCollection.get(0).getDateCreation()
								.equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}

	@Override
	protected void setVerifReqDeleteType(TemplateNameVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("TEMPLATE_NAMES");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	@Override
	protected void setAtNtValues(TemplateNameVb vObject) {
		vObject.setRecordIndicatorNt(7);
		vObject.setTemplateStatusNt(1);
	}

	public TemplateNameDao getTemplateNameDao() {
		return templateNameDao;
	}

	public void setTemplateNameDao(TemplateNameDao templateNameDao) {
		this.templateNameDao = templateNameDao;
	}

	@Override
	protected AbstractDao<TemplateNameVb> getScreenDao() {
		return templateNameDao;
	}

	public <E> ExceptionCode getTemplateNamesPopUp(TemplateNameVb vObject) {
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<TemplateNameVb> collTemp = getTemplateNameDao().getTemplatePopUp(vObject);
		if (collTemp.size() == 0) {
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} else {
			// doSetDesctiptionsAfterQuery(collTemp);
			doSetDesctiptionsAfterQuery(vObject);
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}
	
	public ExceptionCode getQueryResults(TemplateNameVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<TemplateNameVb> collTemp = templateNameDao.getQueryResults(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = templateNameDao.getQueryResults(vObject,Constants.STATUS_ZERO);
		}
		if(collTemp.size() == 0){
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			doSetDesctiptionsAfterQuery((TemplateNameVb) ((ArrayList<TemplateNameVb>)collTemp).get(0));
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			getScreenDao().fetchMakerVerifierNames((TemplateNameVb) ((ArrayList<TemplateNameVb>)collTemp).get(0));
			/*((ArrayList<E>)collTemp).get(0).setVerificationRequired(vObject.isVerificationRequired());
			((ArrayList<E>)collTemp).get(0).setStaticDelete(vObject.isStaticDelete());
			exceptionCode.setOtherInfo(((ArrayList<TemplateNameVb>)collTemp).get(0));*/
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}

	protected RuntimeCustomException buildRuntimeCustomException(ExceptionCode rObject) {
		RuntimeCustomException lException = new RuntimeCustomException(rObject);
		return lException;
	}

	protected String strErrorDesc = "";
	protected String strCurrentOperation = "";
	protected String serviceDesc = "";// Display Name of the service.

	public String getStrErrorDesc() {
		return strErrorDesc;
	}

	public void setStrErrorDesc(String strErrorDesc) {
		this.strErrorDesc = strErrorDesc;
	}

	public String getStrCurrentOperation() {
		return strCurrentOperation;
	}

	public void setStrCurrentOperation(String strCurrentOperation) {
		this.strCurrentOperation = strCurrentOperation;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	protected ExceptionCode getResultObject(int intErrorId) {
		if (intErrorId == Constants.WE_HAVE_ERROR_DESCRIPTION)
			return CommonUtils.getResultObject(serviceDesc, intErrorId, strCurrentOperation, strErrorDesc);
		else
			return CommonUtils.getResultObject(serviceDesc, intErrorId, strCurrentOperation, "");
	}

	/**
	 * updates the existing record in the approved or pending tables depending on
	 * Verification Required flag of the object.
	 * 
	 * @param vObject
	 * @return
	 */
	/* Commented For Template Design */
	/*
	 * public ExceptionCode modifyRecordpopup(ExceptionCode pRequestCode,
	 * List<TemplateNameVb> vObjects){ ExceptionCode exceptionCode = null;
	 * DeepCopy<TemplateNameVb> deepCopy = new DeepCopy<TemplateNameVb>();
	 * List<TemplateNameVb> clonedObject = null; TemplateNameVb vObject = null; try{
	 * vObject = (TemplateNameVb) pRequestCode.getOtherInfo();
	 * setVerifReqDeleteType(vObject); clonedObject =
	 * deepCopy.copyCollection(vObjects); if(exceptionCode!=null &&
	 * exceptionCode.getErrorMsg()!=""){ return exceptionCode; } exceptionCode =
	 * getTemplateNameDao().doUpdateApprRecordPopup(vObjects);
	 * exceptionCode.setOtherInfo(vObject); exceptionCode.setResponse(vObjects);
	 * return exceptionCode; }catch(RuntimeCustomException rex){
	 * logger.error("Modify Exception " + rex.getCode().getErrorMsg());
	 * logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
	 * exceptionCode = rex.getCode(); exceptionCode.setResponse(clonedObject);
	 * exceptionCode.setOtherInfo(vObject); return exceptionCode; } }
	 */

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doInsertApprRecordPopup(List<TemplateNameVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		@SuppressWarnings("unused")
		String strErrorDesc = "";
		String strCurrentOperation = Constants.ADD;
		String strApproveOperation = Constants.ADD;
		// setServiceDefaults();
		try {
			for (TemplateNameVb vObject : vObjects) {
				if (vObject.isChecked()) {
					exceptionCode = getTemplateNameDao().doInsertApprRecordForNonTransPopup(vObject);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (RuntimeCustomException rcException) {
			throw rcException;
		} catch (UncategorizedSQLException uSQLEcxception) {
			// strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Add.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	public File createTemplateFile(TemplateNameVb templateNameVb) {
		File lfile = null;
		FileChannel source = null;
		FileChannel destination = null;
		try {
			String fileName = templateNameVb.getTemplateName();
			// fileName = fileName.substring(0, fileName);
			System.out.println("fileName" + " " + fileName);
			String destFilePath = System.getProperty("java.io.tmpdir");
			if (!ValidationUtil.isValid(destFilePath)) {
				destFilePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(destFilePath)) {
				destFilePath = destFilePath + File.separator;
			}
			lfile = new File(destFilePath + fileName + "_" + SessionContextHolder.getContext().getVisionId() + ".xlsx");
			String filePath = lfile.getAbsolutePath();
			// filePath = filePath.substring(0, filePath.indexOf(fileName));
			System.out.println("filePath" + " " + filePath);
			if (filePath.contains("temp")) {
				filePath = filePath.substring(0, filePath.indexOf("temp"));
				System.out.println("filePath" + " " + filePath);
			}
			if (lfile.exists()) {
				lfile.delete();
			}
			File lSourceFile = new File(filePath + "CB/" + "TEMPLATE" + ".xlsx");
			if (!lSourceFile.exists()) {
				throw new RuntimeCustomException(
						"Invalid Report Configuration, Invalid file name or file does not exists @ " + filePath);
			}
			source = new RandomAccessFile(lSourceFile, "rw").getChannel();
			destination = new RandomAccessFile(lfile, "rw").getChannel();
			long position = 0;
			long count = source.size();
			source.transferTo(position, count, destination);
		} catch (Exception e) {
			throw new RuntimeCustomException("Invalid Report Configuration, Invalid file name or file does not exists");
		} finally {
			if (source != null) {
				try {
					source.close();
				} catch (Exception ex) {
				}
			}
			if (destination != null) {
				try {
					destination.close();
				} catch (Exception ex) {
				}
			}
			logger.info("Template File Successfully Created");
		}
		return lfile;
	}

	public ExceptionCode reviewRecord1(TemplateNameVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReviewResultVb> list = null;
		try {
			List<TemplateNameVb> approvedCollection = templateNameDao.getQueryResults(vObject, 0);
			List<TemplateNameVb> pendingCollection = templateNameDao.getQueryResults(vObject, 1);
			list = transformToReviewResults(approvedCollection, pendingCollection);
			exceptionCode.setResponse(list);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception ex) {
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}
}
