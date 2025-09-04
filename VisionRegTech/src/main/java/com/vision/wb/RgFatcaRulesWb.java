package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.RgFatcaRulesDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.RgFatcaRulesVb;
import com.vision.vb.VisionUsersVb;

@Component
public class RgFatcaRulesWb extends AbstractDynaWorkerBean<RgFatcaRulesVb> {

	@Autowired
	private RgFatcaRulesDao rgfatcarulesDao;

	@Autowired
	CommonDao commonDao;

	public static Logger logger = LoggerFactory.getLogger(RgFatcaRulesWb.class);
	
	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			ArrayList<AlphaSubTabVb> countryLst = (ArrayList<AlphaSubTabVb>) getCommonDao().getCountry();
			ArrayList ctryLeBookLst = new ArrayList();
			for (AlphaSubTabVb ctryLeBook : countryLst) {
				ArrayList<AlphaSubTabVb> leBookLst = (ArrayList<AlphaSubTabVb>) getCommonDao()
						.getLeBook(ctryLeBook.getAlphaSubTab());
				ctryLeBook.setChildren(leBookLst);
				ctryLeBookLst.add(ctryLeBook);
			}
			arrListLocal.add(ctryLeBookLst);
			ArrayList<AlphaSubTabVb> countryList = (ArrayList<AlphaSubTabVb>) getCommonDao().getCountryList();
			arrListLocal.add(countryList);
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
			arrListLocal.add(country + "-" + leBook);
			collTemp = commonDao.getLegalEntity();
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<RgFatcaRulesVb> approvedCollection, List<RgFatcaRulesVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

//				ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
//					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
//					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry(),(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
//				lResult.add(lCountry);
//
//				ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
//					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
//					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
//				lResult.add(lLeBook);

		ReviewResultVb lFatcaPrimaryNationalityFlag = new ReviewResultVb("FATCA Primary Nationality Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFatcaPrimaryNationalityFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFatcaPrimaryNationalityFlag(),
				(!pendingCollection.get(0).getFatcaPrimaryNationalityFlag()
						.equals(approvedCollection.get(0).getFatcaPrimaryNationalityFlag())));
		lResult.add(lFatcaPrimaryNationalityFlag);

		ReviewResultVb lFatcaPrimaryDualNationalityFlag = new ReviewResultVb("FATCA Primary Dual Nationality Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFatcaPrimaryDualNationalityFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFatcaPrimaryDualNationalityFlag(),
				(!pendingCollection.get(0).getFatcaPrimaryDualNationalityFlag()
						.equals(approvedCollection.get(0).getFatcaPrimaryDualNationalityFlag())));
		lResult.add(lFatcaPrimaryDualNationalityFlag);

		ReviewResultVb lFatcaPrimaryResidenceFlag = new ReviewResultVb("FATCA Primary Residence Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFatcaPrimaryResidenceFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFatcaPrimaryResidenceFlag(),
				(!pendingCollection.get(0).getFatcaPrimaryResidenceFlag()
						.equals(approvedCollection.get(0).getFatcaPrimaryResidenceFlag())));
		lResult.add(lFatcaPrimaryResidenceFlag);

		ReviewResultVb lFatcaPrimaryDomicileFlag = new ReviewResultVb("FATCA Primary Domicile Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFatcaPrimaryDomicileFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFatcaPrimaryDomicileFlag(),
				(!pendingCollection.get(0).getFatcaPrimaryDomicileFlag()
						.equals(approvedCollection.get(0).getFatcaPrimaryDomicileFlag())));
		lResult.add(lFatcaPrimaryDomicileFlag);

		ReviewResultVb lFatcaFamilyNationalityFlag = new ReviewResultVb("FATCA Family Nationality Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFatcaFamilyNationalityFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFatcaFamilyNationalityFlag(),
				(!pendingCollection.get(0).getFatcaFamilyNationalityFlag()
						.equals(approvedCollection.get(0).getFatcaFamilyNationalityFlag())));
		lResult.add(lFatcaFamilyNationalityFlag);

		ReviewResultVb lFatcaFamilyDualNationalityFlag = new ReviewResultVb("FATCA Family Dual Nationality Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFatcaFamilyDualNationalityFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFatcaFamilyDualNationalityFlag(),
				(!pendingCollection.get(0).getFatcaFamilyDualNationalityFlag()
						.equals(approvedCollection.get(0).getFatcaFamilyDualNationalityFlag())));
		lResult.add(lFatcaFamilyDualNationalityFlag);

		ReviewResultVb lFatcaCountryList = new ReviewResultVb("FATCA Country List",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFatcaCountryList(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFatcaCountryList(),
				(!pendingCollection.get(0).getFatcaCountryList()
						.equals(approvedCollection.get(0).getFatcaCountryList())));
		lResult.add(lFatcaCountryList);

		ReviewResultVb lCrsPrimaryNationalityFlag = new ReviewResultVb("CRS Primary Nationality Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCrsPrimaryNationalityFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCrsPrimaryNationalityFlag(),
				(!pendingCollection.get(0).getCrsPrimaryNationalityFlag()
						.equals(approvedCollection.get(0).getCrsPrimaryNationalityFlag())));
		lResult.add(lCrsPrimaryNationalityFlag);

		ReviewResultVb lCrsPrimaryDualNationalityFlag = new ReviewResultVb("CRS Primary Dual Nationality Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCrsPrimaryDualNationalityFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCrsPrimaryDualNationalityFlag(),
				(!pendingCollection.get(0).getCrsPrimaryDualNationalityFlag()
						.equals(approvedCollection.get(0).getCrsPrimaryDualNationalityFlag())));
		lResult.add(lCrsPrimaryDualNationalityFlag);

		ReviewResultVb lCrsPrimaryResidenceFlag = new ReviewResultVb("CRS Primary Residence Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCrsPrimaryResidenceFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCrsPrimaryResidenceFlag(),
				(!pendingCollection.get(0).getCrsPrimaryResidenceFlag()
						.equals(approvedCollection.get(0).getCrsPrimaryResidenceFlag())));
		lResult.add(lCrsPrimaryResidenceFlag);

		ReviewResultVb lCrsPrimaryDomicileFlag = new ReviewResultVb("CRS Primary Domicile Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCrsPrimaryDomicileFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCrsPrimaryDomicileFlag(),
				(!pendingCollection.get(0).getCrsPrimaryDomicileFlag()
						.equals(approvedCollection.get(0).getCrsPrimaryDomicileFlag())));
		lResult.add(lCrsPrimaryDomicileFlag);

		ReviewResultVb lCrsFamilyNationalityFlag = new ReviewResultVb("CRS Family Nationality Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCrsFamilyNationalityFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCrsFamilyNationalityFlag(),
				(!pendingCollection.get(0).getCrsFamilyNationalityFlag()
						.equals(approvedCollection.get(0).getCrsFamilyNationalityFlag())));
		lResult.add(lCrsFamilyNationalityFlag);

		ReviewResultVb lCrsFamilyDualNationalityFlag = new ReviewResultVb("CRS Family Dual Nationality Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCrsFamilyDualNationalityFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCrsFamilyDualNationalityFlag(),
				(!pendingCollection.get(0).getCrsFamilyDualNationalityFlag()
						.equals(approvedCollection.get(0).getCrsFamilyDualNationalityFlag())));
		lResult.add(lCrsFamilyDualNationalityFlag);

		ReviewResultVb lCrsCountryList = new ReviewResultVb("CRS Country List",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCrsCountryList(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCrsCountryList(),
				(!pendingCollection.get(0).getCrsCountryList().equals(approvedCollection.get(0).getCrsCountryList())));
		lResult.add(lCrsCountryList);

//				ReviewResultVb lRuleStatusNt = new ReviewResultVb(rsb.getString("ruleStatusNt"),
//					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRuleStatusNt(),
//					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRuleStatusNt(),(!pendingCollection.get(0).getRuleStatusNt().equals(approvedCollection.get(0).getRuleStatusNt())));
//				lResult.add(lRuleStatusNt);
//
//				ReviewResultVb lRuleStatus = new ReviewResultVb(rsb.getString("ruleStatus"),
//					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRuleStatus(),
//					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRuleStatus(),(!pendingCollection.get(0).getRuleStatus().equals(approvedCollection.get(0).getRuleStatus())));
//				lResult.add(lRuleStatus);
//
//				ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
//						(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRecordIndicatorDesc(),
//						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordIndicatorDesc(),(!pendingCollection.get(0).getRecordIndicatorDesc().equals(approvedCollection.get(0).getRecordIndicatorDesc())));
//					lResult.add(lRecordIndicator);
//				ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
//						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMakerName() == 0?"":approvedCollection.get(0).getMakerName(),(!pendingCollection.get(0).getMakerName().equals(approvedCollection.get(0).getMakerName())));
//				lResult.add(lMaker);
//				ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
//						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(),(!pendingCollection.get(0).getVerifierName().equals(approvedCollection.get(0).getVerifierName())));
//				lResult.add(lVerifier);
		ReviewResultVb tempalteStatus = new ReviewResultVb("Rule Status",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								pendingCollection.get(0).getRuleStatus()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								approvedCollection.get(0).getRuleStatus()),
				(!String.valueOf(pendingCollection.get(0).getRuleStatus())
						.equals(String.valueOf(approvedCollection.get(0).getRuleStatus()))));
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
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
				(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
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

	public RgFatcaRulesDao getRgfatcarulesDao() {
		return rgfatcarulesDao;
	}

	public void setRgfatcarulesDao(RgFatcaRulesDao rgfatcarulesDao) {
		this.rgfatcarulesDao = rgfatcarulesDao;
	}

	@Override
	protected AbstractDao<RgFatcaRulesVb> getScreenDao() {
		return rgfatcarulesDao;
	}

	@Override
	protected void setAtNtValues(RgFatcaRulesVb vObject) {
//				set AT NT Values here
	}

	@Override
	protected void setVerifReqDeleteType(RgFatcaRulesVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("RgFatcaRules");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	public ExceptionCode getAllQueryPopupResult(RgFatcaRulesVb queryPopupObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			setVerifReqDeleteType(queryPopupObj);
			doFormateDataForQuery(queryPopupObj);
			List<RgFatcaRulesVb> arrListResult = getScreenDao().getQueryPopupResults(queryPopupObj);
			if (arrListResult != null && arrListResult.size() > 0) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(arrListResult);
				arrListResult.stream().forEach(n -> {
					n.setCrsCountryListcnt(n.getCrsCountryList().split(",").length);
					n.setFatcaCountryListcnt(n.getFatcaCountryList().split(",").length);
				});
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
}