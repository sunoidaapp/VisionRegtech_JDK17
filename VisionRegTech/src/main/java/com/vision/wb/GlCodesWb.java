package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.*;
import com.vision.util.CommonUtils;
import com.vision.vb.CommonVb;
import com.vision.vb.GlCodesVb;
import com.vision.vb.ReviewResultVb;

@Controller
public class GlCodesWb extends AbstractDynaWorkerBean<GlCodesVb> {

	private final BuildControlsStatsDao buildControlsStatsDao;

	private final BuildControlsDao buildControlsDao;

	@Autowired
	private GlCodesDao glCodesDao;
	@Autowired
	private CommonDao commonDao;
	public static Logger logger = LoggerFactory.getLogger(GlCodesWb.class);

	GlCodesWb(BuildControlsDao buildControlsDao, BuildControlsStatsDao buildControlsStatsDao) {
		this.buildControlsDao = buildControlsDao;
		this.buildControlsStatsDao = buildControlsStatsDao;
	}

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7001);// GLType
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7002);// Interset basis
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5025);// Acct type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5026);// prd type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5027);// Chart of Acct
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5028); // gl1
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5029);// gl2
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5031);// gl3
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("GL_CODES");
			arrListLocal.add(collTemp);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
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
	protected List<ReviewResultVb> transformToReviewResults(List<GlCodesVb> approvedCollection,
			List<GlCodesVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
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

		ReviewResultVb lVisionGl = new ReviewResultVb(rsb.getString("visionGl"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVisionGl(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVisionGl(),
				(!pendingCollection.get(0).getVisionGl().equals(approvedCollection.get(0).getVisionGl())));
		lResult.add(lVisionGl);

		ReviewResultVb lGlDescription = new ReviewResultVb(rsb.getString("glDescription"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlDescription(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlDescription(),
				(!pendingCollection.get(0).getGlDescription().equals(approvedCollection.get(0).getGlDescription())));
		lResult.add(lGlDescription);



		ReviewResultVb lDefaultPoolCode = new ReviewResultVb(rsb.getString("defaultPoolCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDefaultPoolCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDefaultPoolCode(),
				(!pendingCollection.get(0).getDefaultPoolCode()
						.equals(approvedCollection.get(0).getDefaultPoolCode())));
		lResult.add(lDefaultPoolCode);


		ReviewResultVb lDrCrMapInd = new ReviewResultVb(rsb.getString("drCrMapInd"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDrCrMapInd(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDrCrMapInd(),
				(!pendingCollection.get(0).getDrCrMapInd().equals(approvedCollection.get(0).getDrCrMapInd())));
		lResult.add(lDrCrMapInd);

		ReviewResultVb lGlStatusDate = new ReviewResultVb(rsb.getString("glStatusDate"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlStatusDate(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlStatusDate(),
				(!pendingCollection.get(0).getGlStatusDate().equals(approvedCollection.get(0).getGlStatusDate())));
		lResult.add(lGlStatusDate);


		ReviewResultVb lAccountType = new ReviewResultVb(rsb.getString("accountType"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getAccountType(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getAccountType(),
				(!pendingCollection.get(0).getAccountType().equals(approvedCollection.get(0).getAccountType())));
		lResult.add(lAccountType);


		ReviewResultVb lProductType = new ReviewResultVb(rsb.getString("productType"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getProductType(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getProductType(),
				(!pendingCollection.get(0).getProductType().equals(approvedCollection.get(0).getProductType())));
		lResult.add(lProductType);


		ReviewResultVb lChartOfAccount = new ReviewResultVb(rsb.getString("chartOfAccount"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getChartOfAccount(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getChartOfAccount(),
				(!pendingCollection.get(0).getChartOfAccount().equals(approvedCollection.get(0).getChartOfAccount())));
		lResult.add(lChartOfAccount);


		ReviewResultVb lGlAttribute01 = new ReviewResultVb(rsb.getString("glAttribute01"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlAttribute01(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlAttribute01(),
				(!pendingCollection.get(0).getGlAttribute01().equals(approvedCollection.get(0).getGlAttribute01())));
		lResult.add(lGlAttribute01);


		ReviewResultVb lGlAttribute02 = new ReviewResultVb(rsb.getString("glAttribute02"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlAttribute02(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlAttribute02(),
				(!pendingCollection.get(0).getGlAttribute02().equals(approvedCollection.get(0).getGlAttribute02())));
		lResult.add(lGlAttribute02);

		ReviewResultVb lGlAttribute03 = new ReviewResultVb(rsb.getString("glAttribute03"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlAttribute03(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlAttribute03(),
				(!pendingCollection.get(0).getGlAttribute03().equals(approvedCollection.get(0).getGlAttribute03())));
		lResult.add(lGlAttribute03);

		ReviewResultVb lReserveCode = new ReviewResultVb(rsb.getString("reserveCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getReserveCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getReserveCode(),
				(!pendingCollection.get(0).getReserveCode().equals(approvedCollection.get(0).getReserveCode())));
		lResult.add(lReserveCode);

		ReviewResultVb lInterestBasis = new ReviewResultVb(
			    rsb.getString("interestBasis"),
			    (pendingCollection == null || pendingCollection.isEmpty()) ? "" 
			        : String.valueOf(pendingCollection.get(0).getInterestBasis()),
			    (approvedCollection == null || approvedCollection.isEmpty()) ? "" 
			        : String.valueOf(approvedCollection.get(0).getInterestBasis()),
			    (pendingCollection.get(0).getInterestBasis() != approvedCollection.get(0).getInterestBasis())
			);
			lResult.add(lInterestBasis);



		ReviewResultVb lGlStatus = new ReviewResultVb(rsb.getString("glStatus"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlStatusDesc(),
				(!pendingCollection.get(0).getGlStatusDesc().equals(approvedCollection.get(0).getGlStatusDesc())));
		lResult.add(lGlStatus);

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
				(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
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

	public GlCodesDao getGlCodesDao() {
		return glCodesDao;
	}

	public void setGlCodesDao(GlCodesDao glCodesDao) {
		this.glCodesDao = glCodesDao;
	}

	@Override
	protected AbstractDao<GlCodesVb> getScreenDao() {
		return glCodesDao;
	}

	@Override
	protected void setAtNtValues(GlCodesVb vObject) {

		vObject.setGlTypeNt(7001);
		vObject.setInterestBasisNt(7002);
		vObject.setAccountTypeAt(5025);
		vObject.setProductTypeAt(5026);
		vObject.setChartOfAccountAt(5027);
		vObject.setGlAttribute01At(5028);
		vObject.setGlAttribute02At(5029);
		vObject.setGlAttribute03At(5031);
	}

	@Override
	protected void setVerifReqDeleteType(GlCodesVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("GL_CODES");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

}
