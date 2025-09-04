package com.vision.wb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.NumSubTabDao;
import com.vision.dao.TemplateConfigHeaderDao;
import com.vision.dao.TemplateMappingDao;
import com.vision.dao.TemplateScheduleCronDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TemplateConfigVb;
import com.vision.vb.TemplateMappingVb;
import com.vision.vb.ValidationVb;

@Component
public class TemplateConfigWb extends AbstractDynaWorkerBean<TemplateConfigVb> {

	@Autowired
	private TemplateConfigHeaderDao templateConfigHeaderDao;
	@Autowired
	private TemplateMappingDao templateMappingDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private TemplateScheduleCronDao templateScheduleDao;

	@Override
	public AbstractDao<TemplateConfigVb> getScreenDao() {
		return templateConfigHeaderDao;
	}

	@Override
	protected void setAtNtValues(TemplateConfigVb vObject) {

	}

	@Override
	protected void setVerifReqDeleteType(TemplateConfigVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao
				.findVerificationRequiredAndStaticDelete("RG_TEMPLATE_CONFIG");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);// status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);// record indicator
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(100);// processFrequencyAt
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7602);// typeOfSubmissionAt
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7603);// csvDelimiterAt
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7604);// sourceTypeAt
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7605);// dbConnectivityType
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7606);// apiConnectivityType
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7607);// authConnectivityType
			arrListLocal.add(collTemp);
		
			/*
			 * ArrayList<AlphaSubTabVb> countryLst = (ArrayList<AlphaSubTabVb>)
			 * commonDao.getCountry(); ArrayList ctryLeBookLst = new ArrayList(); for
			 * (AlphaSubTabVb ctryLeBook : countryLst) { ArrayList<AlphaSubTabVb> leBookLst
			 * = (ArrayList<AlphaSubTabVb>) commonDao
			 * .getLeBook(ctryLeBook.getAlphaSubTab()); ctryLeBook.setChildren(leBookLst);
			 * ctryLeBookLst.add(ctryLeBook); } arrListLocal.add(ctryLeBookLst);
			 */
//			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
//			arrListLocal.add(country);
//			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
//			arrListLocal.add(leBook);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add(country + "-" + leBook);
			collTemp = commonDao.getLegalEntity();
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findAlphaSubTabsByAlphaTabInternalStatus(5008);
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	public ExceptionCode getQueryResults(TemplateConfigVb vObject) {
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		List<TemplateMappingVb> templatMappinglst = new ArrayList<TemplateMappingVb>();
		vObject.setReview(false);
		List<TemplateConfigVb> collTemp = templateConfigHeaderDao.getQueryResults(vObject, intStatus);
		if (collTemp == null || collTemp.size() == 0) {
			intStatus = 0;
			collTemp = templateConfigHeaderDao.getQueryResults(vObject, intStatus);
		}
		if (collTemp == null || collTemp.size() == 0) {
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} else {
			TemplateMappingVb templateMappingVb = new TemplateMappingVb();
			templateMappingVb.setCountry(vObject.getCountry());
			templateMappingVb.setLeBook(vObject.getLeBook());
			templateMappingVb.setTemplateId(vObject.getTemplateId());
			templateMappingVb.setMaxRecords(50);
			templatMappinglst = templateMappingDao.getQueryPopupResults(templateMappingVb);

			// templatMappinglst = templateMappingDao.getQueryResults(vObject,intStatus);

			if (templatMappinglst != null && !templatMappinglst.isEmpty()) {
				collTemp.get(0).setMappinglst(templatMappinglst);
			}
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.SUCCESSFUL_OPERATION,
					"Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}

	@Override
	public ExceptionCode reviewRecordNew(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReviewResultVb> list = null;
		try {
			if (!vObject.getIsChildReview()) {
				List<TemplateConfigVb> approvedCollection = getScreenDao().getQueryResults(vObject, 0);
				List<TemplateConfigVb> pendingCollection = getScreenDao().getQueryResults(vObject, 1);
				list = transformToReviewResults(approvedCollection, pendingCollection);
			} else {
				TemplateMappingVb templateMappingVb = new TemplateMappingVb();
				templateMappingVb.setCountry(vObject.getCountry());
				templateMappingVb.setLeBook(vObject.getLeBook());
				templateMappingVb.setTemplateId(vObject.getTemplateId());
				templateMappingVb.setSequenceNo(vObject.getSequenceNo());
				List<TemplateMappingVb> approvedCollection = templateMappingDao.getQueryResultsReview(templateMappingVb,
						0);
				List<TemplateMappingVb> pendingCollection = templateMappingDao.getQueryResultsReview(templateMappingVb,
						1);
				list = transformToReviewResultsMapping(approvedCollection, pendingCollection);
			}
			exceptionCode.setResponse(list);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception ex) {
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<TemplateConfigVb> approvedCollection,
			List<TemplateConfigVb> pendingCollection) {
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		try {
			ArrayList collTemp = getPageLoadValues();
			if (pendingCollection != null)
				getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
			if (approvedCollection != null)
				getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));

//			ReviewResultVb lCountry = new ReviewResultVb("Country",
//					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
//							: pendingCollection.get(0).getCountry(),
//					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
//							: approvedCollection.get(0).getCountry(),
//					(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
//			lResult.add(lCountry);
//
//			ReviewResultVb lLeBook = new ReviewResultVb("LeBook",
//					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
//							: pendingCollection.get(0).getLeBook(),
//					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
//							: approvedCollection.get(0).getLeBook(),
//					(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
//			lResult.add(lLeBook);

			ReviewResultVb ltemplateName = new ReviewResultVb("Template Name",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getTemplateId(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getTemplateId(),
					(!pendingCollection.get(0).getTemplateId().equals(approvedCollection.get(0).getTemplateId())));
			lResult.add(ltemplateName);

			ReviewResultVb ltemplateDesc = new ReviewResultVb("Template Description",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getTemplateDescription(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getTemplateDescription(),
					(!pendingCollection.get(0).getTemplateDescription()
							.equals(approvedCollection.get(0).getTemplateDescription())));
			lResult.add(ltemplateDesc);

			ReviewResultVb lProcessFrequency = new ReviewResultVb("Process Frequency",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
									pendingCollection.get(0).getProcessFrequency()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
									approvedCollection.get(0).getProcessFrequency()),
					(!pendingCollection.get(0).getProcessFrequency()
							.equals(approvedCollection.get(0).getProcessFrequency())));
			lResult.add(lProcessFrequency);

			ReviewResultVb lTypeOfSubmision = new ReviewResultVb("Type Of Submission",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
									pendingCollection.get(0).getTypeOfSubmission()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
									approvedCollection.get(0).getTypeOfSubmission()),
					(!pendingCollection.get(0).getTypeOfSubmission()
							.equals(approvedCollection.get(0).getTypeOfSubmission())));
			lResult.add(lTypeOfSubmision);

			ReviewResultVb lCsvDel = new ReviewResultVb("CSV Delimiter",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
									pendingCollection.get(0).getCsvDelimiter()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
									approvedCollection.get(0).getCsvDelimiter()),
					(!pendingCollection.get(0).getCsvDelimiter().equals(approvedCollection.get(0).getCsvDelimiter())));
			lResult.add(lCsvDel);

			ReviewResultVb lSourceType = new ReviewResultVb("Source Type",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(5),
									pendingCollection.get(0).getSourceType()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(5),
									approvedCollection.get(0).getSourceType()),
					(!pendingCollection.get(0).getSourceType().equals(approvedCollection.get(0).getSourceType())));
			lResult.add(lSourceType);

			ReviewResultVb lDbConnectivityType = new ReviewResultVb("DB Connectivity Type",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(6),
									pendingCollection.get(0).getDbConnectivityType()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(6),
									approvedCollection.get(0).getDbConnectivityType()),
					(!pendingCollection.get(0).getDbConnectivityType()
							.equals(approvedCollection.get(0).getDbConnectivityType())));
			lResult.add(lDbConnectivityType);

			ReviewResultVb lDbConnectivityDet = new ReviewResultVb("DB Connectivity Detail",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getDatabaseConnectivityDetails(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getDatabaseConnectivityDetails(),
					(!pendingCollection.get(0).getDatabaseConnectivityDetails()
							.equals(approvedCollection.get(0).getDatabaseConnectivityDetails())));
			lResult.add(lDbConnectivityDet);

			ReviewResultVb sourceTable = new ReviewResultVb("Source Table",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getSourceTable(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getSourceTable(),
					(!pendingCollection.get(0).getSourceTable().equals(approvedCollection.get(0).getSourceTable())));
			lResult.add(sourceTable);

			ReviewResultVb sourceTableFilter = new ReviewResultVb("Source Table Filter",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getSourceTableFilter(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getSourceTableFilter(),
					(!pendingCollection.get(0).getSourceTableFilter()
							.equals(approvedCollection.get(0).getSourceTableFilter())));
			lResult.add(sourceTableFilter);

			ReviewResultVb readinessScript = new ReviewResultVb("Readiness Script",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getSoureTableReadiness(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getSoureTableReadiness(),
					(!pendingCollection.get(0).getSoureTableReadiness()
							.equals(approvedCollection.get(0).getSoureTableReadiness())));
			lResult.add(readinessScript);

			ReviewResultVb uploadFileName = new ReviewResultVb("Upload File Name",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getUploadFileName(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getUploadFileName(),
					(!pendingCollection.get(0).getUploadFileName()
							.equals(approvedCollection.get(0).getUploadFileName())));
			lResult.add(uploadFileName);

			ReviewResultVb lApiConnectivityType = new ReviewResultVb("API Connectivity Type",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(7),
									pendingCollection.get(0).getApiConnectivityType()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(7),
									approvedCollection.get(0).getApiConnectivityType()),
					(!pendingCollection.get(0).getApiConnectivityType()
							.equals(approvedCollection.get(0).getApiConnectivityType())));
			lResult.add(lApiConnectivityType);

			ReviewResultVb lApiConnectivityDet = new ReviewResultVb("API Connectivity Detail",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getApiConnectivityDetails(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getApiConnectivityDetails(),
					(!pendingCollection.get(0).getApiConnectivityDetails()
							.equals(approvedCollection.get(0).getApiConnectivityDetails())));
			lResult.add(lApiConnectivityDet);

			ReviewResultVb lAuthConnectivityType = new ReviewResultVb("Auth Connectivity Type",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(8),
									pendingCollection.get(0).getAuthConnectivityType()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(8),
									approvedCollection.get(0).getAuthConnectivityType()),
					(!pendingCollection.get(0).getAuthConnectivityType()
							.equals(approvedCollection.get(0).getAuthConnectivityType())));
			lResult.add(lAuthConnectivityType);

			ReviewResultVb lAuthConnectivityDet = new ReviewResultVb("Auth Connectivity Detail",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getAuthConnectivityDetails(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getAuthConnectivityDetails(),
					(!pendingCollection.get(0).getAuthConnectivityDetails()
							.equals(approvedCollection.get(0).getAuthConnectivityDetails())));
			lResult.add(lAuthConnectivityDet);

			ReviewResultVb mainApiStructure = new ReviewResultVb("Main Api Structure",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getMainAPIStructure(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getMainAPIStructure(),
					(!pendingCollection.get(0).getMainAPIStructure()
							.equals(approvedCollection.get(0).getMainAPIStructure())));
			lResult.add(mainApiStructure);
			
			ReviewResultVb lDatalst = new ReviewResultVb("Data List",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getDataList(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getDataList(),
					(!pendingCollection.get(0).getDataList().equals(approvedCollection.get(0).getDataList())));
			lResult.add(lDatalst);
			
			ReviewResultVb lAutosubmit = new ReviewResultVb("Auto Submit",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getAutoSubmit(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getAutoSubmit(),
					(!pendingCollection.get(0).getAutoSubmit().equals(approvedCollection.get(0).getAutoSubmit())));
			lResult.add(lAutosubmit);
			
			ReviewResultVb lSubmissionTime = new ReviewResultVb("Submission Time",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getSubmissionTime(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getSubmissionTime(),
					(!(ValidationUtil.isValid(pendingCollection.get(0).getSubmissionTime())? pendingCollection.get(0).getSubmissionTime():"").equals(ValidationUtil.isValid(approvedCollection.get(0).getSubmissionTime())? approvedCollection.get(0).getSubmissionTime():"")));
//			(ValidationUtil.isValid(pendingCollection.get(0).getSubmissionTime())? pendingCollection.get(0).getSubmissionTime():"").equals(ValidationUtil.isValid(approvedCollection.get(0).getSubmissionTime())? approvedCollection.get(0).getSubmissionTime():"");
			
			
			lResult.add(lSubmissionTime);
			
			ReviewResultVb lCategory = new ReviewResultVb("Category",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getCategoryType(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getCategoryType(),
					(!pendingCollection.get(0).getCategoryType().equals(approvedCollection.get(0).getCategoryType())));
			lResult.add(lCategory);
			
			ReviewResultVb lCbkFileName = new ReviewResultVb("File Name",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getCbkFileName(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getCbkFileName(),
					(!(ValidationUtil.isValid(pendingCollection.get(0).getCbkFileName())
							? pendingCollection.get(0).getCbkFileName()
							: "")
							.equals(ValidationUtil.isValid(approvedCollection.get(0).getCbkFileName())
									? approvedCollection.get(0).getCbkFileName()
									: "")));
			lResult.add(lCbkFileName);

			ReviewResultVb tempalteStatus = new ReviewResultVb("Template Status",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
									pendingCollection.get(0).getTemplateStatus()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
									approvedCollection.get(0).getTemplateStatus()),
					(!String.valueOf(pendingCollection.get(0).getTemplateStatus())
							.equals(String.valueOf(approvedCollection.get(0).getTemplateStatus()))));
			lResult.add(tempalteStatus);

			ReviewResultVb lRecordIndicator = new ReviewResultVb("Record Indicator",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getRecordIndicatorDesc() == null ? ""
									: pendingCollection.get(0).getRecordIndicatorDesc(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getRecordIndicatorDesc() == null ? ""
									: approvedCollection.get(0).getRecordIndicatorDesc(),
					(pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
			lResult.add(lRecordIndicator);

			ReviewResultVb lMaker = new ReviewResultVb("Maker",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
					(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
			lResult.add(lMaker);
			ReviewResultVb lVerifier = new ReviewResultVb("Verifier",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getVerifier() == 0 ? ""
									: pendingCollection.get(0).getVerifierName(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getVerifier() == 0 ? ""
									: approvedCollection.get(0).getVerifierName(),
					(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
			lResult.add(lVerifier);
			ReviewResultVb lDateLastModified = new ReviewResultVb("Date Last Modified",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getDateLastModified(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getDateLastModified(),
					(!pendingCollection.get(0).getDateLastModified()
							.equals(approvedCollection.get(0).getDateLastModified())));
			lResult.add(lDateLastModified);
			ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getDateCreation(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getDateCreation(),
					(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
			lResult.add(lDateCreation);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lResult;
	}

	public List<ReviewResultVb> transformToReviewResultsMapping(List<TemplateMappingVb> approvedCollection,
			List<TemplateMappingVb> pendingCollection) {
		ArrayList<ReviewResultVb> lResult = new ArrayList<>();
		try {
			ReviewResultVb lCountry = new ReviewResultVb("Country",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getCountry(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getCountry(),
					(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
			lResult.add(lCountry);

			ReviewResultVb lLeBook = new ReviewResultVb("LeBook",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getLeBook(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getLeBook(),
					(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
			lResult.add(lLeBook);

			ReviewResultVb ltemplateName = new ReviewResultVb("Template Name",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getTemplateId(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getTemplateId(),
					(!pendingCollection.get(0).getTemplateId().equals(approvedCollection.get(0).getTemplateId())));
			lResult.add(ltemplateName);

			ReviewResultVb lSeqNo = new ReviewResultVb("Sequence No",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: String.valueOf(pendingCollection.get(0).getSequenceNo()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: String.valueOf(approvedCollection.get(0).getSequenceNo()),
					(!String.valueOf(pendingCollection.get(0).getSequenceNo())
							.equals(String.valueOf(approvedCollection.get(0).getSequenceNo()))));
			lResult.add(lSeqNo);

			ReviewResultVb lSourceCol = new ReviewResultVb("Source Column",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getSourceColumn(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getSourceColumn(),
					(!pendingCollection.get(0).getSourceColumn().equals(approvedCollection.get(0).getSourceColumn())));
			lResult.add(lSourceCol);

			ReviewResultVb lTargetCol = new ReviewResultVb("Target Column",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getTargetColumn(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getTargetColumn(),
					(!pendingCollection.get(0).getTargetColumn().equals(approvedCollection.get(0).getTargetColumn())));
			lResult.add(lTargetCol);

			ReviewResultVb mappingStatus = new ReviewResultVb("Mapping Status",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getMappingStatusDesc(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getMappingStatusDesc(),
					(pendingCollection.get(0).getMappingStatus() != approvedCollection.get(0).getMappingStatus()));
			lResult.add(mappingStatus);

			ReviewResultVb lRecordIndicator = new ReviewResultVb("Record Indicator",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getRecordIndicatorDesc() == null ? ""
									: pendingCollection.get(0).getRecordIndicatorDesc(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getRecordIndicatorDesc() == null ? ""
									: approvedCollection.get(0).getRecordIndicatorDesc(),
					(pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
			lResult.add(lRecordIndicator);

			ReviewResultVb lMaker = new ReviewResultVb("Maker",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
					(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
			lResult.add(lMaker);
			ReviewResultVb lVerifier = new ReviewResultVb("Verifier",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getVerifier() == 0 ? ""
									: pendingCollection.get(0).getVerifierName(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getVerifier() == 0 ? ""
									: approvedCollection.get(0).getVerifierName(),
					(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
			lResult.add(lVerifier);
			ReviewResultVb lDateLastModified = new ReviewResultVb("Date Last Modified",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getDateLastModified(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getDateLastModified(),
					(!pendingCollection.get(0).getDateLastModified()
							.equals(approvedCollection.get(0).getDateLastModified())));
			lResult.add(lDateLastModified);
			ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getDateCreation(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getDateCreation(),
					(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
			lResult.add(lDateCreation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lResult;
	}

	public ExceptionCode addConnectionDetails(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				return exceptionCode;
			}
			String varScript = dynamicScriptCreation(vObject);
			vObject.setConnectionScript(varScript);
			exceptionCode = doDbInsertUpdateToDynVarTable(vObject);
			if (exceptionCode.getErrorCode() != Constants.ERRONEOUS_OPERATION) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Connection Saved Successfully");
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public String dynamicScriptCreation(TemplateConfigVb vObject) {
		JSONObject extractVbData = new JSONObject(vObject);
		StringBuilder scriptFormation = new StringBuilder();
		if (ValidationUtil.isValid(vObject.getTagList())) {
			JSONArray tagListArr = (JSONArray) extractVbData.getJSONArray("tagList");
			if (tagListArr.length() != 0) {
				if ("DB".equalsIgnoreCase(vObject.getConnectionType())) {
					scriptFormation.append("{DATABASE_TYPE:#CONSTANT$@!" + vObject.getDbConnectivityType() + "#}");
					// scriptFormation.append("{DB_URL:#CONSTANT$@!" + vObject.getDbUrl() + "#}");
				}

				for (int i = 0; i < tagListArr.length(); i++) {
					JSONObject jsonObj = tagListArr.getJSONObject(i);
					String ch = fixJSONObject(jsonObj);
					JSONObject jsonData = new JSONObject(ch);
					String tagName = jsonData.getString("TAG");
					String encryption = jsonData.getString("ENCRYPTION");
					String value = jsonData.getString("VALUE");
					if ("yes".equalsIgnoreCase(encryption)) {
						scriptFormation.append("{" + tagName + ":#ENCRYPT$@!" + value + "#}");
					} else {
						scriptFormation.append("{" + tagName + ":#CONSTANT$@!" + value + "#}");
					}
				}
			}

			return String.valueOf(scriptFormation);
		} else {
			return commonDao.getScript(vObject.getDbConnectivityDetails());
		}
	}

	public String fixJSONObject(JSONObject obj) {
		String jsonString = obj.toString();
		for (int i = 0; i < obj.names().length(); i++) {
			try {
				jsonString = jsonString.replace(obj.names().getString(i), obj.names().getString(i).toUpperCase());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonString;
	}

	public ExceptionCode doDbInsertUpdateToDynVarTable(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = null;
		DeepCopy<TemplateConfigVb> deepCopy = new DeepCopy<TemplateConfigVb>();
		TemplateConfigVb clonedObject = null;
		try {
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = templateConfigHeaderDao.doInsertUpdateRecordForDbpopup(vObject);
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(rex.getMessage());
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}

	public ExceptionCode getConnectivityDetails(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String connectionDetails = "";
		String paramConnectivityType = "";
		String newRec = "true";
		String checkExist = "";
		String dbUrl = "";
		Boolean flag = true;
		try {
			if ("SCRIPT".equalsIgnoreCase(vObject.getConnectionType()))
				flag = false;
			// if(!vObject.getConDetails().equalsIgnoreCase("")&&!vObject.getConDetails().equalsIgnoreCase(null)){
			if (ValidationUtil.isValid(vObject.getConDetails())) {
				connectionDetails = templateConfigHeaderDao.getScriptValue(vObject.getConDetails(), flag);
				if ("DB".equalsIgnoreCase(vObject.getConnectionType())) {
					paramConnectivityType = templateConfigHeaderDao.getValue(connectionDetails, "DATABASE_TYPE");
					vObject.setConnectivityOption(paramConnectivityType);
				} else if ("FTP".equalsIgnoreCase(vObject.getConnectionType())
						|| "SERVER".equalsIgnoreCase(vObject.getConnectionType())) {
					paramConnectivityType = templateConfigHeaderDao.getValue(connectionDetails, "CONNECTIVITY_TYPE");
					vObject.setConnectivityOption(paramConnectivityType);
				}
				if (!"SCRIPT".equalsIgnoreCase(vObject.getConnectionType()))
					checkExist = templateConfigHeaderDao.checkExist(vObject.getConDetails());
			}
			if ("SCRIPT".equalsIgnoreCase(vObject.getConnectionType())) {
				if (ValidationUtil.isValid(connectionDetails))
					exceptionCode.setResponse(connectionDetails);
			} else {
				if (checkExist.equalsIgnoreCase("") || checkExist.equalsIgnoreCase(null)) {
					newRec = "true";
				} else {
					newRec = "false";
				}

				if ("DB".equalsIgnoreCase(vObject.getConnectionType()) && newRec.equalsIgnoreCase("false")) {
					dbUrl = templateConfigHeaderDao.getValue(connectionDetails, "DB_URL");
					exceptionCode.setOtherInfo(dbUrl);
				}
				vObject.setConnectionScript(connectionDetails);
				List<TemplateConfigVb> dataLst = new ArrayList<>();
				dataLst = templateConfigHeaderDao.getDataQuery(vObject);
				exceptionCode.setResponse(dataLst);
				/*
				 * for(TemplateConfigVb data :DbData){
				 * if("YES".equalsIgnoreCase(data.getEncryption()) &&
				 * ValidationUtil.isValid(data.getTagValue())){ String constCheck
				 * =CommonUtils.getConstant(dbDetails,data.getTagName().toUpperCase());
				 * if(constCheck.equalsIgnoreCase("ENCRYPT")){ String decryptedString =
				 * Crypto.decrypt(data.getTagValue(), data.getTagName().toUpperCase());
				 * data.setTagValue(decryptedString); } } }
				 */
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Connection Details Saved Successfully");
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode dbConTest(String variableScript) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String dbIP = "";
		String jdbcURL = CommonUtils.getValue(variableScript, "DB_URL");
		String serviceName = CommonUtils.getValue(variableScript, "SERVICE_NAME");
		String dbOracleSid = CommonUtils.getValue(variableScript, "SID");
		String user = CommonUtils.getValue(variableScript, "USER");
		if (!ValidationUtil.isValid(user))
			user = CommonUtils.getValue(variableScript, "DB_USER");
		String pwd = CommonUtils.getValue(variableScript, "PWD");
		if (!ValidationUtil.isValid(pwd))
			pwd = CommonUtils.getValue(variableScript, "DB_PWD");
		String dbPort = CommonUtils.getValue(variableScript, "DB_PORT");
		String dbName = CommonUtils.getValue(variableScript, "DB_NAME");
		String dataBaseType = CommonUtils.getValue(variableScript, "DATABASE_TYPE");
		String dbinstance = CommonUtils.getValue(variableScript, "DB_INSTANCE");
		String dbIp = CommonUtils.getValue(variableScript, "DB_IP");
		String serverName = CommonUtils.getValue(variableScript, "SERVER_NAME");
		String version = CommonUtils.getValue(variableScript, "JAR_VERSION");
		String hostName = serviceName;
		Boolean sidFlag = false;
		if (!ValidationUtil.isValid(hostName)) {
			hostName = dbOracleSid;
			sidFlag = true;
		}
		if (ValidationUtil.isValid(dbIp))
			dbIP = dbIp;
		if ("ORACLE".equalsIgnoreCase(dataBaseType)) {
			// System.out.println("-------- Oracle JDBC Connection Testing ------");
			Connection connection = null;
			try {
				if (!ValidationUtil.isValid(jdbcURL)) {
					Class.forName("oracle.jdbc.driver.OracleDriver");
					if (sidFlag)
						jdbcURL = "jdbc:oracle:thin:@//" + dbIP + ":" + dbPort + "/" + hostName;
					else
						jdbcURL = "jdbc:oracle:thin:@" + dbIp + ":" + dbPort + ":" + serviceName;
					connection = DriverManager.getConnection(jdbcURL, user, pwd);

				} else if (ValidationUtil.isValid(jdbcURL)) {
					connection = DriverManager.getConnection(jdbcURL);
				}
			} catch (Exception e) {
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			} finally {
				if (connection != null) {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("Oracle DB connected");
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else if ("MSSQL".equalsIgnoreCase(dataBaseType)) {
			// System.out.println("-------- MsSQL JDBC Connection Testing ------");
			Connection connection = null;
			try {
				if (!ValidationUtil.isValid(jdbcURL)) {
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					if ((ValidationUtil.isValid(dbinstance)) && (ValidationUtil.isValid(dbName))) {
						jdbcURL = "jdbc:sqlserver://" + dbIp + ":" + dbPort + ";instanceName=" + dbinstance
								+ ";databaseName=" + dbName + ";user=" + user + ";password=" + pwd;
					} else if ((ValidationUtil.isValid(dbinstance)) && (!ValidationUtil.isValid(dbName))) {
						jdbcURL = "jdbc:sqlserver://" + dbIp + ":" + dbPort + ";instanceName=" + dbinstance + ";user="
								+ user + ";password=" + pwd;
					} else if ((!ValidationUtil.isValid(dbinstance)) && (ValidationUtil.isValid(dbName))) {
						jdbcURL = "jdbc:sqlserver://" + dbIp + ":" + dbPort + ";databaseName=" + dbName + ";user="
								+ user + ";password=" + pwd;
					} else {
						jdbcURL = "jdbc:sqlserver://" + dbIp + ":" + dbPort + ";databaseName=" + dbName + ";user="
								+ user + ";password=" + pwd;
					}
					connection = DriverManager.getConnection(jdbcURL, user, pwd);
				} else if (ValidationUtil.isValid(jdbcURL)) {
					connection = DriverManager.getConnection(jdbcURL);
				}

			} catch (Exception e) {
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			} finally {
				if (connection != null) {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("MSSQL DB connected");
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else if ("MYSQL".equalsIgnoreCase(dataBaseType)) {
			// System.out.println("-------- MySQL JDBC Connection Testing ------");
			Connection connection = null;
			try {
				if (!ValidationUtil.isValid(jdbcURL)) {
					Class.forName("com.mysql.jdbc.Driver");
					jdbcURL = "jdbc:mysql://" + dbIp + ":" + dbPort + "/" + dbName + "?" + "user" + "=" + user + "@-:@"
							+ "password" + "=" + pwd;
					connection = DriverManager.getConnection(jdbcURL, user, pwd);
				} else if (ValidationUtil.isValid(jdbcURL)) {
					connection = DriverManager.getConnection(jdbcURL);
				}

			} catch (Exception e) {
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			} finally {
				if (connection != null) {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("MySql DB connected");
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else if ("POSTGRESQL".equalsIgnoreCase(dataBaseType)) {
			// System.out.println("-------- POSTGRESQL JDBC Connection Testing ------");
			Connection connection = null;
			try {
				if (!ValidationUtil.isValid(jdbcURL)) {
					exceptionCode.setResponse1(connection);
					Class.forName("org.postgresql.Driver");
					jdbcURL = "jdbc:postgresql://" + dbIp + ":" + dbPort + "/" + dbName + "?" + "user" + "=" + user
							+ "&" + "password" + "=" + pwd;
					connection = DriverManager.getConnection(jdbcURL, user, pwd);
				} else if (ValidationUtil.isValid(jdbcURL)) {
					connection = DriverManager.getConnection(jdbcURL);
				}
			} catch (Exception e) {
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			} finally {
				if (connection != null) {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("Postgresql DB connected");
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else if ("SYBASE".equalsIgnoreCase(dataBaseType)) {
			// System.out.println("-------- SYBASE JDBC Connection Testing ------");
			Connection connection = null;
			try {
				if (!ValidationUtil.isValid(jdbcURL)) {
					Class.forName("com.sybase.jdbc4.jdbc.SybDataSource");
					jdbcURL = "jdbc:sybase:Tds:" + dbIp + ":" + dbPort + "?ServiceName=" + serviceName;
				} else if (ValidationUtil.isValid(jdbcURL)) {
					connection = DriverManager.getConnection(jdbcURL);
				}
			} catch (Exception e) {
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			} finally {
				if (connection != null) {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("Sybase DB connected");
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		exceptionCode.setResponse(jdbcURL);
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		exceptionCode.setErrorMsg("Test connection Successful");
		return exceptionCode;
	}

	public ExceptionCode scriptTest(String varScript, TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String query = "SELECT * FROM ";
		String readiness = vObject.getSoureTableReadiness();
		Connection conn;
		ArrayList<ValidationVb> validationLst = new ArrayList<>();
		try {
			// exceptionCode = chkTrgtColumns(vObject);
			/*
			 * if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
			 * ValidationVb trgtApiColumnMatch =
			 * checkValidation("Target Column and Api Structure",
			 * exceptionCode.getErrorSevr(), exceptionCode.getErrorMsg());
			 * validationLst.add(trgtApiColumnMatch);
			 */
			conn = CommonUtils.getDBConnection(varScript);
			if (conn != null) {
				ValidationVb connectionChk = checkValidation("Database Connection", "Y", "Connected to database");
				validationLst.add(connectionChk);
				exceptionCode = chkReadiness(conn, readiness);
				ValidationVb readinessChk = checkValidation("Readiness Script", exceptionCode.getErrorSevr(),
						exceptionCode.getErrorMsg());
				validationLst.add(readinessChk);
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = chkColumns(conn, vObject);
					ValidationVb soruceChk = checkValidation("SourceTable Check", exceptionCode.getErrorSevr(),
							exceptionCode.getErrorMsg());
					validationLst.add(soruceChk);
					if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setErrorMsg("Validation Successful");
						exceptionCode.setResponse(validationLst);
					}
				}

			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Validation Failed");
				ValidationVb connectionChk = checkValidation("Connection", "N", "Connection Failed");
				validationLst.add(connectionChk);

			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Validation Failed");
		}
		exceptionCode.setResponse(validationLst);
		return exceptionCode;
	}

	public ExceptionCode chkReadiness(Connection conn, String query) {
		ExceptionCode exceptionCode = new ExceptionCode();
		PreparedStatement pstmt = null;
		boolean flag;
		try {
			pstmt = conn.prepareStatement(query);
			flag = pstmt.execute();
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Readiness Script Success");
			exceptionCode.setErrorSevr("Y");
		} catch (SQLException e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorSevr("N");
		}
		return exceptionCode;
	}

	public ExceptionCode chkColumns(Connection conn, TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		PreparedStatement pstmt = null;
		boolean flag;
		StringJoiner commaJoiner = new StringJoiner(",");
		try {
			String query = "SELECT ";
			for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
				commaJoiner.add(templateMappingVb.getSourceColumn());
			}
			query = query + commaJoiner.toString() + " FROM " + vObject.getSourceTable();
			if (ValidationUtil.isValid(vObject.getSourceTableFilter())
					&& !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
				query = query + " Where " + vObject.getSourceTableFilter();
			}

			pstmt = conn.prepareStatement(query);
			flag = pstmt.execute();
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Source Table is Available");
			exceptionCode.setErrorSevr("Y");
		} catch (SQLException e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorSevr("N");
		}
		return exceptionCode;
	}

//	public ExceptionCode formJson(TemplateConfigVb vObject) {
//		ExceptionCode exceptionCode = new ExceptionCode();
//		PreparedStatement pstmt = null;
//		Connection conn;
//		boolean flag;
//		ArrayList result = new ArrayList();
//		StringJoiner selectColumns = new StringJoiner(",");
//		String json = vObject.getMainAPIStructure();
//		ObjectMapper obj = new ObjectMapper();
//		try {
//			String query = "SELECT ";
//			for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
//				selectColumns.add(templateMappingVb.getSourceColumn());
//			}
//			query = query + selectColumns.toString() + " FROM " + vObject.getSourceTable();
//			if (ValidationUtil.isValid(vObject.getSourceTableFilter())
//					&& !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
//				query = query + " Where " + vObject.getSourceTableFilter();
//			}
//			String varScript = dynamicScriptCreation(vObject);
//			if (ValidationUtil.isValid(varScript)) {
//				conn = CommonUtils.getDBConnection(varScript);
//				if (conn != null) {
//					pstmt = conn.prepareStatement(query);
//					ResultSet rs = pstmt.executeQuery();
//					ResultSetMetaData metaData = rs.getMetaData();
//					int colCount = metaData.getColumnCount();
//					Boolean dataPresent = false;
//					while (rs.next()) {
//						JsonNode n1 = obj.readTree(json);
//						LinkedHashMap<String, String> resultData = new LinkedHashMap<String, String>();
//						if (n1.isObject()) {
//							Iterator<String> on = n1.fieldNames();
//							while (on.hasNext()) {
//								for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
//									dataPresent = true;
//									for (int cn = 1; cn <= colCount; cn++) {
//										String columnName = metaData.getColumnName(cn);
//										if (columnName.equals(templateMappingVb.getSourceColumn())) {
//											String targetCol = templateMappingVb.getTargetColumn();
//											String col = on.next();
//											if (col.equals(targetCol)) {
//												resultData.put(col, rs.getString(columnName));
//												break;
//											}
//										}
//									}
//								}
//							}
//							result.add(resultData);
//						}
//					}
//
//					if (dataPresent) {
//						ObjectMapper objectMapper = new ObjectMapper();
//						String jsonArray = objectMapper.writeValueAsString(result);
//
//						// Create the final JSON structure
//						ObjectNode finalJson = objectMapper.createObjectNode();
//						String instutionCode = System.getenv("VISION_RG_INSTIUTION_CODE");
//						String date = templateScheduleDao.reportDate();
//
//						finalJson.put("institutionCode", instutionCode);
//						finalJson.put("requestId", "");
//						finalJson.put("reportingDate", date);
//
//						if ("DB".equalsIgnoreCase(vObject.getSourceType()))
//							finalJson.put("isAttached", "N");
//						else
//							finalJson.put("isAttached", "Y");
//						// need to maintain the listing name
//						ArrayNode reportLst = finalJson.putArray("");
//						reportLst.addAll((ArrayNode) objectMapper.readTree(jsonArray));
//
//						exceptionCode.setResponse(finalJson.toString());
//						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
//					} else {
//						exceptionCode.setErrorMsg("No data found");
//					}
//				}
//			} else {
//				exceptionCode.setErrorMsg("Script not maintained properly!! ");
//			}
//		} catch (Exception e) {
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//			exceptionCode.setErrorSevr("N");
//		}
//		return exceptionCode;
//	}

	public ExceptionCode formCsv(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		PreparedStatement pstmt = null;
		Connection conn;
		boolean flag;
		ArrayList result = new ArrayList();
		StringJoiner selectColumns = new StringJoiner(",");
		try {
			String query = "SELECT ";
			for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
				selectColumns.add(templateMappingVb.getSourceColumn());
			}
			query = query + selectColumns.toString() + " FROM " + vObject.getSourceTable();
			if (ValidationUtil.isValid(vObject.getSourceTableFilter())) {
				query = query + " Where " + vObject.getSourceTableFilter();
			}

			String varScript = dynamicScriptCreation(vObject);
			if (ValidationUtil.isValid(varScript)) {
				conn = CommonUtils.getDBConnection(varScript);
				if (conn != null) {
					pstmt = conn.prepareStatement(query);
					ResultSet rs = pstmt.executeQuery();
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while (rs.next()) {
						LinkedHashMap<String, String> resultData = new LinkedHashMap<String, String>();
						dataPresent = true;
						for (int cn = 1; cn <= colCount; cn++) {
							String columnName = metaData.getColumnName(cn);
							resultData.put(columnName, rs.getString(columnName));
						}
						result.add(resultData);
					}
					if (dataPresent) {
						exceptionCode.setResponse(result);
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					} else {
						exceptionCode.setErrorMsg(" ");
					}
				}
			} else {
				exceptionCode.setErrorMsg("Script not maintained properly!! ");
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorSevr("N");
		}
		return exceptionCode;
	}

	/*
	 * public ExceptionCode chkTrgtColumns(TemplateConfigVb vObject) { ExceptionCode
	 * exceptionCode = new ExceptionCode(); String json =
	 * vObject.getMainAPIStructure(); ObjectMapper obj = new ObjectMapper(); boolean
	 * flag = true; try { JsonNode n1 = obj.readTree(json); for (TemplateMappingVb
	 * templateMappingVb : vObject.getMappinglst()) { Iterator<String> jsonNode =
	 * n1.fieldNames(); while (jsonNode.hasNext()) { String node = jsonNode.next();
	 * if (node.equals(templateMappingVb.getTargetColumn())) { flag = false; break;
	 * } } } if(!flag) { exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	 * exceptionCode.setErrorMsg("Matched"); exceptionCode.setErrorSevr("Y"); } }
	 * catch (JsonProcessingException e) {
	 * exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	 * exceptionCode.setErrorMsg(e.getMessage()); exceptionCode.setErrorSevr("N"); }
	 * 
	 * return exceptionCode; }
	 */

	protected ExceptionCode doValidate(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestrion = commonDao.getRestrictionsByUsers("35", operation);
		if (!"Y".equalsIgnoreCase(srtRestrion)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation + " " + Constants.userRestrictionMsg);
			return exceptionCode;
		}
		return exceptionCode;
	}

	public ValidationVb checkValidation(String criteria, String errServ, String errMsg) {
		ValidationVb validationVb = new ValidationVb();
		validationVb.setErrorMsg(errMsg);
		validationVb.setErrorSeverity(errServ);
		validationVb.setCriteria(criteria);
		return validationVb;
	}
	public ExceptionCode formXml(TemplateConfigVb vObject) {
	    ExceptionCode exceptionCode = new ExceptionCode();
	    PreparedStatement pstmt = null;
	    Connection conn = null;
	    StringBuilder xmlOutput = new StringBuilder();
	    StringJoiner selectColumns = new StringJoiner(",");

	    try {
	        // Constructing the SQL query
	        String query = "SELECT ";
	        for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
	            selectColumns.add(templateMappingVb.getSourceColumn());
	        }
	        query += selectColumns.toString() + " FROM " + vObject.getSourceTable();
	        if (ValidationUtil.isValid(vObject.getSourceTableFilter()) && !"NA".equalsIgnoreCase(vObject.getSourceTableFilter()) ) {
	            query += " WHERE " + vObject.getSourceTableFilter();
	        }

	        // Generate dynamic script and get the connection
	        String varScript = dynamicScriptCreation(vObject);
	        if (ValidationUtil.isValid(varScript)) {
	            conn = CommonUtils.getDBConnection(varScript);
	            if (conn != null) {
	                pstmt = conn.prepareStatement(query);
	                ResultSet rs = pstmt.executeQuery();
	                ResultSetMetaData metaData = rs.getMetaData();
	                int columnCount = metaData.getColumnCount();
	                boolean dataPresent = false;

	                // Start building the XML document
	                xmlOutput.append("<Results>");

	                while (rs.next()) {
	                    dataPresent = true;
	                    xmlOutput.append("<crs:AccountReport>");
	                    xmlOutput.append("<crs:ControllingPerson>");

	                    for (int i = 1; i <= columnCount; i++) {
	                        String columnName = metaData.getColumnName(i);
	                        String value = rs.getString(i);

	                        if (!ValidationUtil.isValid(value)) {
	                            value = "";  // Handle null or invalid values
	                        }

	                        // Transform the column name to XML tag format
	                        columnName = ValidationUtil.toTitleCase(columnName);
	                        columnName = columnName.replaceAll("_", "");

	                        // Create XML element
	                        String tagStart = "<crs:" + columnName + ">";
	                        String tagEnd = "</crs:" + columnName + ">";
	                        xmlOutput.append(tagStart).append(value).append(tagEnd);
	                    }

	                    xmlOutput.append("</crs:ControllingPerson>");
	                    xmlOutput.append("</crs:AccountReport>");
	                }

	                xmlOutput.append("</Results>");

	                if (dataPresent) {
	                    exceptionCode.setResponse(xmlOutput.toString());
	                    exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	                } else {
	                    exceptionCode.setErrorMsg("No data found.");
	                    exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	                }
	            }
	        } else {
	            exceptionCode.setErrorMsg("Script not maintained properly!!");
	            exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	        }
	    } catch (Exception e) {
	        exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	        exceptionCode.setErrorMsg(e.getMessage());
	        exceptionCode.setErrorSevr("N");
	    } finally {
	        // Close resources
	        if (pstmt != null) {
	            try {
	                pstmt.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        if (conn != null) {
	            try {
	                conn.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    return exceptionCode;
	}
	private ExceptionCode writeXmlToFile(String xmlData, String fileName) {
	    ExceptionCode exceptionCode = new ExceptionCode();
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        writer.write(xmlData);
	        exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	    } catch (IOException e) {
	        exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	        exceptionCode.setErrorMsg(e.getMessage());
	        exceptionCode.setErrorSevr("N");
	    }
	    return exceptionCode;
	}
}
