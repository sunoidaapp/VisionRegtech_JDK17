package com.vision.wb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import jakarta.servlet.ServletContext;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.ServletContextAware;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.BuildControlsDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ReportsDao;
import com.vision.dao.RgFatcaRuleDao;
import com.vision.dao.RgFatcaRuleDetailsDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.CreateCsv;
import com.vision.util.ExcelExportUtil;
import com.vision.util.ValidationUtil;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.RgFatcaRuleDetailsVb;
import com.vision.vb.RgFatcaRuleVb;
import com.vision.vb.VisionUsersVb;

@Controller
public class RgFatcaRuleWb extends AbstractWorkerBean<RgFatcaRuleVb> implements ServletContextAware {

	private final BuildControlsDao buildControlsDao;
	private ServletContext servletContext;

	RgFatcaRuleWb(BuildControlsDao buildControlsDao) {
		this.buildControlsDao = buildControlsDao;
	}

	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}

	@Autowired
	private RgFatcaRuleDao rgFatcaRuleDao;

	@Autowired
	RgFatcaRuleDetailsDao rgFatcaRuleDetailsDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private ReportsDao reportsDao;
	@Value("${crs.fatca.jar.memory.min}")
	private String jarMemoryMin;

	@Value("${crs.fatca.jar.memory.max}")
	private String jarMemoryMax;

	@Value("${app.databaseType}")
	private String databaseType;
	@Value("${app.cloud}")
	private String cloud;

	public static Logger logger = LoggerFactory.getLogger(RgFatcaRuleWb.class);

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(3);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(857);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(854);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = commonDao.getLegalEntity();
			arrListLocal.add(collTemp);
			String country = "";
			String leBook = "";
			VisionUsersVb visionUsers = SessionContextHolder.getContext();
			if ("Y".equalsIgnoreCase(visionUsers.getUpdateRestriction())) {
				if (ValidationUtil.isValid(visionUsers.getCountry())) {
					country = visionUsers.getCountry();
				}
				if (ValidationUtil.isValid(visionUsers.getLeBook())) {
					leBook = visionUsers.getLeBook();
				}
			} else {
				country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
				leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			}
			String countryLeBook = country + "-" + leBook;
			arrListLocal.add(countryLeBook);

			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(856); // Filter Condition
			arrListLocal.add(collTemp);

			String entitySbu = commonDao.findVisionVariableValue("FATCA_ENTITY_SBU");
			if (!ValidationUtil.isValid(entitySbu))
				entitySbu = "CORP - Corporate";
			arrListLocal.add(entitySbu);

			String individualSbu = commonDao.findVisionVariableValue("FATCA_INDIVIDUAL_SBU");
			if (!ValidationUtil.isValid(individualSbu))
				individualSbu = "RETL - Retail";
			arrListLocal.add(individualSbu);

			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("RG_FATCA_RULE");
			arrListLocal.add(collTemp);

			String buildFlag = commonDao.findVisionVariableValue("RG_BUILD_FLAG");
			if (!ValidationUtil.isValid(buildFlag))
				buildFlag = "N";
			arrListLocal.add(buildFlag);
			String concatinat = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				concatinat = "+";
			} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
				concatinat = "||";
			} else if ("POSTGRES".equalsIgnoreCase(databaseType)) {
				concatinat = "||";
			}
			arrListLocal.add(concatinat);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<RgFatcaRuleVb> approvedCollection,
			List<RgFatcaRuleVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getCountry(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCountry(),
				(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
		lResult.add(lCountry);

		ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getLeBook(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getLeBook(),
				(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
		lResult.add(lLeBook);

		ReviewResultVb lVisionSbu = new ReviewResultVb(rsb.getString("visionSBU"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVisionSbuDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVisionSbuDesc(),
				(!pendingCollection.get(0).getVisionSbuDesc().equals(approvedCollection.get(0).getVisionSbuDesc())));
		lResult.add(lVisionSbu);

		ReviewResultVb lRuleId = new ReviewResultVb(rsb.getString("ruleId"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getRuleId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleId(),
				(!pendingCollection.get(0).getRuleId().equals(approvedCollection.get(0).getRuleId())));
		lResult.add(lRuleId);

		ReviewResultVb lRuleDescription = new ReviewResultVb(rsb.getString("ruleDescription"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleDescription(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleDescription(),
				(!pendingCollection.get(0).getRuleDescription()
						.equals(approvedCollection.get(0).getRuleDescription())));
		lResult.add(lRuleDescription);

		ReviewResultVb lPriority = new ReviewResultVb(rsb.getString("priority"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: "" + pendingCollection.get(0).getPriority(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: "" + approvedCollection.get(0).getPriority(),
				!(pendingCollection.get(0).getPriority() != approvedCollection.get(0).getPriority()));
		lResult.add(lPriority);

		ReviewResultVb lRuleStatus = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getStatusDesc(),
				(!pendingCollection.get(0).getStatusDesc().equals(approvedCollection.get(0).getStatusDesc())));
		lResult.add(lRuleStatus);

		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRecordIndicatorDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRecordIndicatorDesc(),
				(!pendingCollection.get(0).getRecordIndicatorDesc()
						.equals(approvedCollection.get(0).getRecordIndicatorDesc())));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb("Maker",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
				(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
				(!pendingCollection.get(0).getVerifierName().equals(approvedCollection.get(0).getVerifierName())));
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
				(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}

	protected List<ReviewResultVb> transformToReviewResultsDetails(List<RgFatcaRuleDetailsVb> approvedCollection,
			List<RgFatcaRuleDetailsVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

		ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getCountry(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCountry(),
				(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
		lResult.add(lCountry);

		ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getLeBook(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getLeBook(),
				(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
		lResult.add(lLeBook);

		ReviewResultVb lVisionSbu = new ReviewResultVb(rsb.getString("visionSBU"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVisionSbuDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVisionSbuDesc(),
				(!pendingCollection.get(0).getVisionSbu().equals(approvedCollection.get(0).getVisionSbu())));
		lResult.add(lVisionSbu);

		ReviewResultVb lRuleId = new ReviewResultVb(rsb.getString("ruleId"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getRuleId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleId(),
				(!pendingCollection.get(0).getRuleId().equals(approvedCollection.get(0).getRuleId())));
		lResult.add(lRuleId);

		ReviewResultVb lColumnName = new ReviewResultVb(rsb.getString("columnName"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getColumnName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getColumnName(),
				(!pendingCollection.get(0).getColumnName().equals(approvedCollection.get(0).getColumnName())));
		lResult.add(lColumnName);

		ReviewResultVb lRuleType = new ReviewResultVb(rsb.getString("ruleType"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleTypeDesc(),
				(!pendingCollection.get(0).getRuleType().equals(approvedCollection.get(0).getRuleType())));
		lResult.add(lRuleType);

		ReviewResultVb lRuleDescription = new ReviewResultVb(rsb.getString("ruleDescription"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleDescription(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleDescription(),
				(!pendingCollection.get(0).getRuleDescription()
						.equals(approvedCollection.get(0).getRuleDescription())));
		lResult.add(lRuleDescription);

		ReviewResultVb lRuleSequence = new ReviewResultVb(rsb.getString("ruleSequence"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: "" + pendingCollection.get(0).getRuleSequence(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: "" + approvedCollection.get(0).getRuleSequence(),
				(!(pendingCollection.get(0).getRuleSequence() != approvedCollection.get(0).getRuleSequence())));
		lResult.add(lRuleSequence);

		ReviewResultVb lLookupTableName = new ReviewResultVb(rsb.getString("lookupTableName"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getLookupTableName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getLookupTableName(),
				(!pendingCollection.get(0).getLookupTableName()
						.equals(approvedCollection.get(0).getLookupTableName())));
		lResult.add(lLookupTableName);

		ReviewResultVb lLookupColumn = new ReviewResultVb(rsb.getString("lookupColumn"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getLookupColumn(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getLookupColumn(),
				(!pendingCollection.get(0).getLookupColumn().equals(approvedCollection.get(0).getLookupColumn())));
		lResult.add(lLookupColumn);

		ReviewResultVb lLookupCondition = new ReviewResultVb(rsb.getString("lookupCondition"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getLookupCondition(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getLookupCondition(),
				(!pendingCollection.get(0).getLookupCondition()
						.equals(approvedCollection.get(0).getLookupCondition())));
		lResult.add(lLookupCondition);

		ReviewResultVb lValuePattFlag = new ReviewResultVb(rsb.getString("valuePattFlag"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getValuePattFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getValuePattFlag(),
				(!pendingCollection.get(0).getValuePattFlag().equals(approvedCollection.get(0).getValuePattFlag())));
		lResult.add(lValuePattFlag);

		ReviewResultVb lValuePattValues = new ReviewResultVb(rsb.getString("valuePattValues"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getValuePattValues(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getValuePattValues(),
				(!pendingCollection.get(0).getValuePattValues()
						.equals(approvedCollection.get(0).getValuePattValues())));
		lResult.add(lValuePattValues);

		ReviewResultVb lRuleAlphaFlag = new ReviewResultVb(rsb.getString("ruleAlphaFlag"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleAlphaFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleAlphaFlag(),
				(!pendingCollection.get(0).getRuleAlphaFlag().equals(approvedCollection.get(0).getRuleAlphaFlag())));
		lResult.add(lRuleAlphaFlag);

		ReviewResultVb lRuleAlpha = new ReviewResultVb(rsb.getString("ruleAlpha"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleAlphaDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleAlphaDesc(),
				(!pendingCollection.get(0).getRuleAlpha().equals(approvedCollection.get(0).getRuleAlpha())));
		lResult.add(lRuleAlpha);

		ReviewResultVb lRuleAlphaChar = new ReviewResultVb(rsb.getString("ruleAlphaChar"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleAlphaChar(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleAlphaChar(),
				(!pendingCollection.get(0).getRuleAlphaChar().equals(approvedCollection.get(0).getRuleAlphaChar())));
		lResult.add(lRuleAlphaChar);

		ReviewResultVb lRuleNumFlag = new ReviewResultVb(rsb.getString("ruleNumFlag"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleNumFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleNumFlag(),
				(!pendingCollection.get(0).getRuleNumFlag().equals(approvedCollection.get(0).getRuleNumFlag())));
		lResult.add(lRuleNumFlag);

		ReviewResultVb lRuleNum = new ReviewResultVb(rsb.getString("ruleNum"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleNumDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleNumDesc(),
				(!pendingCollection.get(0).getRuleNum().equals(approvedCollection.get(0).getRuleNum())));
		lResult.add(lRuleNum);

		ReviewResultVb lRuleNumChar = new ReviewResultVb(rsb.getString("ruleNumChar"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleNumChar(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleNumChar(),
				(!pendingCollection.get(0).getRuleNumChar().equals(approvedCollection.get(0).getRuleNumChar())));
		lResult.add(lRuleNumChar);

		ReviewResultVb lRuleLenMin = new ReviewResultVb(rsb.getString("ruleLenMin"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: "" + pendingCollection.get(0).getRuleLenMin(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: "" + approvedCollection.get(0).getRuleLenMin(),
				(!(pendingCollection.get(0).getRuleLenMin() != approvedCollection.get(0).getRuleLenMin())));
		lResult.add(lRuleLenMin);

		ReviewResultVb lRuleLenMax = new ReviewResultVb(rsb.getString("ruleLenMax"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: "" + pendingCollection.get(0).getRuleLenMax(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: "" + approvedCollection.get(0).getRuleLenMax(),
				(!(pendingCollection.get(0).getRuleLenMax() != approvedCollection.get(0).getRuleLenMax())));
		lResult.add(lRuleLenMax);

		ReviewResultVb lRuleStatus = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getStatusDesc(),
				(!pendingCollection.get(0).getStatusDesc().equals(approvedCollection.get(0).getStatusDesc())));
		lResult.add(lRuleStatus);

		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRecordIndicatorDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRecordIndicatorDesc(),
				(!pendingCollection.get(0).getRecordIndicatorDesc()
						.equals(approvedCollection.get(0).getRecordIndicatorDesc())));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
				(!pendingCollection.get(0).getMakerName().equals(approvedCollection.get(0).getMakerName())));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
				(!pendingCollection.get(0).getVerifierName().equals(approvedCollection.get(0).getVerifierName())));
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
				(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;

	}

	public RgFatcaRuleDao getRgFatcaRuleDao() {
		return rgFatcaRuleDao;
	}

	public void setRgFatcaRuleDao(RgFatcaRuleDao rgFatcaRuleDao) {
		this.rgFatcaRuleDao = rgFatcaRuleDao;
	}

	@Override
	protected AbstractDao<RgFatcaRuleVb> getScreenDao() {
		return rgFatcaRuleDao;
	}

	@Override
	protected void setAtNtValues(RgFatcaRuleVb vObject) {
		vObject.setVisionSbuAt(3);
		vObject.setRuleStatusNt(1);
		vObject.setRecordIndicatorNt(7);
	}

	@Override
	protected void setVerifReqDeleteType(RgFatcaRuleVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("RG_FATCA_RULE");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	public ExceptionCode getQueryResults(RgFatcaRuleVb vObject) {
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<RgFatcaRuleVb> collTemp = getScreenDao().getQueryResults(vObject, intStatus);
		if (collTemp.size() == 0) {
			intStatus = 0;
			collTemp = getScreenDao().getQueryResults(vObject, intStatus);
		}
		if (collTemp.size() == 0) {
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} else {
//					doSetDesctiptionsAfterQuery(((ArrayList<RgFatcaRuleVb>)collTemp).get(0));
			RgFatcaRuleVb rgFatcaRuleVb = ((ArrayList<RgFatcaRuleVb>) collTemp).get(0);
			rgFatcaRuleVb.setVerificationRequired(vObject.isVerificationRequired());
			List<RgFatcaRuleDetailsVb> rgFatcaRuleDetailsLists = getRgFatcaRuleDetailsDao()
					.getQueryResultsAllByParent(rgFatcaRuleVb);
			rgFatcaRuleVb.setRuleDetailsList(rgFatcaRuleDetailsLists);

			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
//					getScreenDao().fetchMakerVerifierNames(((ArrayList<RgFatcaRuleVb>)collTemp).get(0));
			((ArrayList<RgFatcaRuleVb>) collTemp).get(0).setVerificationRequired(vObject.isVerificationRequired());
			((ArrayList<RgFatcaRuleVb>) collTemp).get(0).setStaticDelete(vObject.isStaticDelete());
			exceptionCode.setResponse(collTemp);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}

	public RgFatcaRuleDetailsDao getRgFatcaRuleDetailsDao() {
		return rgFatcaRuleDetailsDao;
	}

	public void setRgFatcaRuleDetailsDao(RgFatcaRuleDetailsDao rgFatcaRuleDetailsDao) {
		this.rgFatcaRuleDetailsDao = rgFatcaRuleDetailsDao;
	}

	public List<ReviewResultVb> reviewRecordDetails(RgFatcaRuleDetailsVb vObject) {
		try {
			List<RgFatcaRuleDetailsVb> approvedCollection = getRgFatcaRuleDetailsDao().getQueryResults(vObject, 0);
			List<RgFatcaRuleDetailsVb> pendingCollection = getRgFatcaRuleDetailsDao().getQueryResults(vObject, 1);
			return transformToReviewResultsDetails(approvedCollection, pendingCollection);
		} catch (Exception ex) {
			return null;
		}
	}

	public ArrayList reviewRecord(RgFatcaRuleVb vObject) {
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			List<RgFatcaRuleVb> approvedCollection = getRgFatcaRuleDao().getQueryResults(vObject, 0);
			List<RgFatcaRuleVb> pendingCollection = getRgFatcaRuleDao().getQueryResults(vObject, 1);
			List<ReviewResultVb> groupReviewList = transformToReviewResults(approvedCollection, pendingCollection);
			arrListLocal.add(groupReviewList);

			Map<String, ArrayList<Object>> childList = new HashMap<String, ArrayList<Object>>();

			List<RgFatcaRuleDetailsVb> rgFatcaRuleDetailsList = getRgFatcaRuleDetailsDao()
					.getQueryApprOrPendResultsByParent(vObject, 1);
			ArrayList<Object> childGlMappingsListAll = null;
			boolean isBalaTypes = false;
			if (rgFatcaRuleDetailsList != null && rgFatcaRuleDetailsList.size() > 0) {
				childGlMappingsListAll = new ArrayList<Object>();
				for (RgFatcaRuleDetailsVb vObjectTemp : rgFatcaRuleDetailsList) {
					if (vObjectTemp.getRecordIndicator() == 1) {
						isBalaTypes = true;
						List<ReviewResultVb> reviewResultOucsList = reviewRecordDetails(vObjectTemp);
						childGlMappingsListAll.add(reviewResultOucsList);
					}
				}
			}
			if (isBalaTypes) {
				childList.put("DETAILS", childGlMappingsListAll);
			} else {
				childList.put("DETAILS", null);
			}
			arrListLocal.add(childList);
			return arrListLocal;
		} catch (Exception ex) {
			return null;
		}
	}

	public ExceptionCode getDataByValueOrPattern(RgFatcaRuleDetailsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = rgFatcaRuleDao.getDataByValueOrPattern(vObject);
			return exceptionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the getAllQueryPopupResult results.", ex);
			return null;
		}
	}

	public ExceptionCode getQueryResultsToDisplay(RgFatcaRuleDetailsVb queryPopupObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<RgFatcaRuleDetailsVb> arrListResult = getRgFatcaRuleDetailsDao()
					.getQueryResultsToDisplay(queryPopupObj);
			if (arrListResult != null && arrListResult.size() > 0) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(arrListResult);
			} else {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}
			exceptionCode.setOtherInfo(queryPopupObj);
			return exceptionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the getAllQueryPopupResult results.", ex);
			return null;
		}
	}

	public ExceptionCode downloadCustomerRuleResults(RgFatcaRuleDetailsVb dObj, int currentUserId) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int rowNum = 0;
		StringBuffer strBufApprove = new StringBuffer("");

		try {
			strBufApprove = getRgFatcaRuleDetailsDao().getDisplayQueryWithAllColumns(dObj);
			String orderBy = "ORDER BY PRIORITY";
			ReportsVb vObject = new ReportsVb();
			vObject.setObjectType("G");
			vObject.setFinalExeQuery(strBufApprove.toString());
			vObject.setDbConnection("DEFAULT");
			vObject.setReportTitle("Customer FATCA Match Results");
			List<HashMap<String, String>> dataLst = null;
			List<HashMap<String, String>> totalLst = null;
			vObject.setSortField(orderBy);
			vObject.setMaxRecords(1000);
			exceptionCode = reportsDao.getResultData(vObject, true);
			ReportsVb resultVb = new ReportsVb();
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
					totalLst = resultVb.getTotal();
				}
			}
			SXSSFWorkbook workBook = new SXSSFWorkbook(500);
			String sheetName = vObject.getReportTitle().trim();
			vObject.setReportTitle(sheetName);
			List<ColumnHeadersVb> colHeaderslst = vObject.getColumnHeaderslst();
			List<String> colTypes = new ArrayList<String>();
			Map<Integer, Integer> columnWidths = new HashMap<Integer, Integer>(colHeaderslst.size());
			for (int loopCnt = 0; loopCnt < colHeaderslst.size(); loopCnt++) {
				columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
				ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
				if (colHVb.getColspan() <= 1 && colHVb.getNumericColumnNo() != 99) {
					colTypes.add(colHVb.getColType());
				}
			}

			SXSSFSheet sheet = (SXSSFSheet) workBook.createSheet(vObject.getReportTitle());
			boolean createHeadersAndFooters = true;
			Map<Integer, XSSFCellStyle> styls = ExcelExportUtil.createStyles(workBook);
			int ctr = 1;
			int sheetCnt = 3;
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			do {
				if ((rowNum + vObject.getMaxRecords()) > SpreadsheetVersion.EXCEL2007.getMaxRows()) {
					rowNum = 0;
					sheet = (SXSSFSheet) workBook.createSheet("" + sheetCnt);
					sheetCnt++;
					createHeadersAndFooters = true;
				}
				if (createHeadersAndFooters) {
					rowNum = ExcelExportUtil.writeHeadersRA(vObject, colHeaderslst, rowNum, sheet, styls, colTypes,
							columnWidths);
					sheet.createFreezePane(vObject.getFreezeColumn(), rowNum);
				}
				createHeadersAndFooters = false;
				// writing data into excel
				ctr++;
				rowNum = ExcelExportUtil.writeReportDataRA(workBook, vObject, colHeaderslst, dataLst, sheet, rowNum,
						styls, colTypes, columnWidths, false, assetFolderUrl);
				vObject.setCurrentPage(ctr);
				dataLst = new ArrayList();
				exceptionCode = reportsDao.getResultData(vObject, true);
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
				}
			} while (dataLst != null && !dataLst.isEmpty());
			int headerCnt = 0;
			headerCnt = colTypes.size();
			int noOfSheets = workBook.getNumberOfSheets();
			for (int a = 1; a < noOfSheets; a++) {
				sheet = (SXSSFSheet) workBook.getSheetAt(a);
				int loopCount = 0;
				for (loopCount = 0; loopCount < headerCnt; loopCount++) {
					sheet.setColumnWidth(loopCount, columnWidths.get(loopCount));
				}
			}
			String filePath = System.getProperty("java.io.tmpdir");
			if (!ValidationUtil.isValid(filePath)) {
				filePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(filePath)) {
				filePath = filePath + File.separator;
			}
			File lFile = new File(
					filePath + ValidationUtil.encode(vObject.getReportTitle()) + "_" + currentUserId + ".xlsx");
			if (lFile.exists()) {
				lFile.delete();
			}
			lFile.createNewFile();
			FileOutputStream fileOS = new FileOutputStream(lFile);
			workBook.write(fileOS);
			fileOS.flush();
			fileOS.close();
			exceptionCode.setResponse(filePath);
			exceptionCode.setOtherInfo(vObject.getReportTitle() + "_" + currentUserId);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode downloadCustomerRuleResultsCsv(RgFatcaRuleDetailsVb dObj, int currentUserId) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int rowNum = 0;
		StringBuffer strBufApprove = new StringBuffer("");
		long min = 0;
		long max = 5000;
		PrintWriter out = null;
		FileWriter fw = null;
		CreateCsv csv = new CreateCsv();
		String csvSeparator = ",";
		String lineSeparator = "\n";
		CreateCsv createCSV = new CreateCsv();
		try {

			strBufApprove = getRgFatcaRuleDetailsDao().getDisplayQueryWithAllColumns(dObj);
			String orderBy = "ORDER BY PRIORITY";
			ReportsVb vObject = new ReportsVb();
			vObject.setObjectType("G");
			vObject.setFinalExeQuery(strBufApprove.toString());
			vObject.setDbConnection("DEFAULT");
			vObject.setReportTitle("Customer FATCA Match Results");
			vObject.setSortField(orderBy);
			vObject.setMaxRecords(1000);
			vObject.setCsvDelimiter(",");
			List<HashMap<String, String>> dataLst = null;
			List<HashMap<String, String>> totalLst = null;

			exceptionCode = reportsDao.getResultData(vObject, true);
			ReportsVb resultVb = new ReportsVb();
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
					totalLst = resultVb.getTotal();
				}
			}

			String filePath = System.getProperty("java.io.tmpdir");
			if (!ValidationUtil.isValid(filePath)) {
				filePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(filePath)) {
				filePath = filePath + File.separator;
			}
			String[] hiddenColumns = null;
			if (ValidationUtil.isValid(vObject.getColumnsToHide())) {
				hiddenColumns = vObject.getColumnsToHide().split("!@#");
			}

			List<ColumnHeadersVb> colHeaderslst = vObject.getColumnHeaderslst();
			List<ColumnHeadersVb> updatedLst = new ArrayList();
			List<ColumnHeadersVb> columnHeadersFinallst = new ArrayList<ColumnHeadersVb>();
			colHeaderslst.forEach(colHeadersVb -> {
				if (colHeadersVb.getColspan() <= 1) {
					columnHeadersFinallst.add(colHeadersVb);
				}
			});
			for (ColumnHeadersVb columnHeadersVb : columnHeadersFinallst) {
				if (hiddenColumns != null) {
					for (int ctr = 0; ctr < hiddenColumns.length; ctr++) {
						if (columnHeadersVb.getDbColumnName().equalsIgnoreCase(hiddenColumns[ctr])) {
							updatedLst.add(columnHeadersVb);
							break;
						}
					}
				}
			}

			if (updatedLst != null && !updatedLst.isEmpty()) {
				columnHeadersFinallst.removeAll(updatedLst);
			}

			fw = new FileWriter(
					filePath + ValidationUtil.encode(vObject.getReportTitle()) + "_" + currentUserId + ".csv");
			out = new PrintWriter(fw);
			int ctr = 1;
			String csvSeperator = "";
			if ("\t".equalsIgnoreCase(vObject.getCsvDelimiter()))
				csvSeperator = "	";
			else
				csvSeperator = vObject.getCsvDelimiter();

			rowNum = createCSV.writeHeadersToCsv(columnHeadersFinallst, vObject, fw, rowNum, out, csvSeperator);
			do {
				ctr++;
				rowNum = createCSV.writeDataToCsv(columnHeadersFinallst, dataLst, vObject, currentUserId, fw, rowNum,
						out, csvSeperator);
				vObject.setCurrentPage(ctr);
				dataLst = new ArrayList();
				exceptionCode = reportsDao.getResultData(vObject, true);
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
				}
			} while (dataLst != null && !dataLst.isEmpty());

			if (totalLst != null)
				rowNum = createCSV.writeDataToCsv(columnHeadersFinallst, totalLst, vObject, currentUserId, fw, rowNum,
						out, csvSeperator);

			out.flush();
			out.close();
			fw.close();
			exceptionCode.setResponse(filePath);
			exceptionCode.setOtherInfo(vObject.getReportTitle() + "_" + currentUserId);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode getAllQueryPopupResult(RgFatcaRuleVb queryPopupObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			setVerifReqDeleteType(queryPopupObj);
			doFormateDataForQuery(queryPopupObj);
			List<RgFatcaRuleVb> arrListResult = rgFatcaRuleDao.getQueryPopupResults(queryPopupObj);
			if (arrListResult != null && arrListResult.size() > 0) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				RgFatcaRuleDetailsVb detailsVb = new RgFatcaRuleDetailsVb();
				detailsVb.setCountry(queryPopupObj.getCountry());
				detailsVb.setLeBook(queryPopupObj.getLeBook());
				detailsVb.setVisionSbu(queryPopupObj.getVisionSbu());

				detailsVb.setCount(true);
				getRgFatcaRuleDetailsDao().getQueryResultsToDisplay(detailsVb);
				if (detailsVb.isCount() && detailsVb.getTotalRows() > 0)
					queryPopupObj.setScreenName("DATA_EXISTS");
				for (RgFatcaRuleVb ruleVb : arrListResult) {
					List<RgFatcaRuleDetailsVb> rgFatcaRuleDetailsList = rgFatcaRuleDetailsDao
							.getQueryResultsAllByParent(ruleVb);
					if (rgFatcaRuleDetailsList != null && rgFatcaRuleDetailsList.size() > 0) {
						ruleVb.setRuleCount(rgFatcaRuleDetailsList.size());
					}
				}
				exceptionCode.setResponse(arrListResult);
				exceptionCode.setResponse1("Y");
			} else {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}
			exceptionCode.setOtherInfo(queryPopupObj);
			return exceptionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the getAllQueryPopupResult results.", ex);
			return null;
		}
	}

	public ExceptionCode runBuilds(RgFatcaRuleVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int exitVal = 1;
		try {
			String execsPath = commonDao.findVisionVariableValue("RG_BUILD_PATH");
			String jarName = commonDao.findVisionVariableValue("RG_BUILD_JAR_NAME");
			String logPath = commonDao.findVisionVariableValue("RG_BUILDLOG_PATH");

			// Validation
			if (!ValidationUtil.isValid(execsPath)) {
				exceptionCode.setErrorMsg("RG FATCA Build Run Failed! Missing RG_BUILD_PATH");
				return exceptionCode;
			}
			if (!ValidationUtil.isValid(jarName)) {
				exceptionCode.setErrorMsg("RG FATCA Build Run Failed! Missing RG_BUILD_JAR_NAME");
				return exceptionCode;
			}
			if (!ValidationUtil.isValid(logPath)) {
				exceptionCode.setErrorMsg("RG FATCA Build Run Failed! Missing RG_BUILDLOG_PATH");
				return exceptionCode;
			}
			if (!"Y".equalsIgnoreCase(cloud)) {
				// Memory options from application.properties
				String memoryOptions = "-Xms" + jarMemoryMin + " -Xmx" + jarMemoryMax;

				String jarExecCmd = "java " + memoryOptions + " -jar " + execsPath + jarName + " "
						+ vObject.getCountry() + " " + vObject.getLeBook() + " " + vObject.getVisionSbu() + " "
						+ "01-Jan-1900 FATCA N Y";

				logger.info("Executing: " + jarExecCmd);

				Process proc = Runtime.getRuntime().exec(jarExecCmd);

				// Consume stdout
				new Thread(() -> {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
						reader.lines().forEach(logger::info);
					} catch (IOException e) {
						logger.error("Error reading stdout", e);
					}
				}).start();

				// Consume stderr
				new Thread(() -> {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
						reader.lines().forEach(logger::error);
					} catch (IOException e) {
						logger.error("Error reading stderr", e);
					}
				}).start();

				// Wait for process to finish
				exitVal = proc.waitFor();
				logger.info("Exit Value from Jar [" + exitVal + "]");
			} else {
				exitVal = 0;
			}

			// Set exception code
			if (exitVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("RG FATCA Build Run Successfully!");
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("RG FATCA Build Run Failed!");
			}

		} catch (Exception ex) {
			logger.error("Exception while running RG FATCA Build", ex);
			exceptionCode.setErrorMsg("RG FATCA Build Run Failed! Error: " + ex.getMessage());
		}

		return exceptionCode;
	}

	public ExceptionCode updateBuildFlag(RgFatcaRuleVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();

		int retVal = commonDao.doUpdateVisionVariables("RG_BUILD_FLAG", vObject.getValue());

		if (retVal > 0) {
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Build Flag Updated Successfully");
		} else {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Build Flag Updation Failed");
		}

		return exceptionCode;
	}
}