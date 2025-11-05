package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.CbGlMappingsDao;
import com.vision.dao.CommonDao;
import com.vision.dao.RgBuildProcessDao;
import com.vision.util.CommonUtils;
import com.vision.vb.CbGlMappingsVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ReviewResultVb;

@Controller
public class CbGlMappingsWb extends AbstractDynaWorkerBean<CbGlMappingsVb> {

	private final RgBuildProcessDao rgBuildProcessDao;

	@Autowired
	private CbGlMappingsDao cbGlMappingsDao;
	@Autowired
	private CommonDao commonDao;

	CbGlMappingsWb(RgBuildProcessDao rgBuildProcessDao) {
		this.rgBuildProcessDao = rgBuildProcessDao;
	}

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5022);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5023);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5024);
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("CB_GL_MAPPINGS");
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
	protected List<ReviewResultVb> transformToReviewResults(List<CbGlMappingsVb> approvedCollection,
			List<CbGlMappingsVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

		ReviewResultVb lBankGlCode = new ReviewResultVb(rsb.getString("bankGlCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getBankGlCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getBankGlCode(),
				(!pendingCollection.get(0).getBankGlCode().equals(approvedCollection.get(0).getBankGlCode())));
		lResult.add(lBankGlCode);
//
		ReviewResultVb lRuleId = new ReviewResultVb(rsb.getString("ruleId"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getRuleId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleId(),
				(!pendingCollection.get(0).getRuleId().equals(approvedCollection.get(0).getRuleId())));
		lResult.add(lRuleId);

		ReviewResultVb lAttribute01At = new ReviewResultVb(rsb.getString("attribute01At"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getAttribute01At(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getAttribute01At(),
				(!pendingCollection.get(0).getAttribute01At().equals(approvedCollection.get(0).getAttribute01At())));
		lResult.add(lAttribute01At);

		ReviewResultVb lAttribute01 = new ReviewResultVb(rsb.getString("attribute01"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getAttribute01(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getAttribute01(),
				(!pendingCollection.get(0).getAttribute01().equals(approvedCollection.get(0).getAttribute01())));
		lResult.add(lAttribute01);

		ReviewResultVb lAttribute02At = new ReviewResultVb(rsb.getString("attribute02At"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getAttribute02At(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getAttribute02At(),
				(!pendingCollection.get(0).getAttribute02At().equals(approvedCollection.get(0).getAttribute02At())));
		lResult.add(lAttribute02At);

		ReviewResultVb lAttribute02 = new ReviewResultVb(rsb.getString("attribute02"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getAttribute02(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getAttribute02(),
				(!pendingCollection.get(0).getAttribute02().equals(approvedCollection.get(0).getAttribute02())));
		lResult.add(lAttribute02);

		ReviewResultVb lAttribute03At = new ReviewResultVb(rsb.getString("attribute03At"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getAttribute03At(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getAttribute03At(),
				(!pendingCollection.get(0).getAttribute03At().equals(approvedCollection.get(0).getAttribute03At())));
		lResult.add(lAttribute03At);

		ReviewResultVb lAttribute03 = new ReviewResultVb(rsb.getString("attribute03"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getAttribute03(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getAttribute03(),
				(!pendingCollection.get(0).getAttribute03().equals(approvedCollection.get(0).getAttribute03())));
		lResult.add(lAttribute03);

		ReviewResultVb lGlMapStatus = new ReviewResultVb(rsb.getString("glMapStatus"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlMapStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlMapStatusDesc(),
				(!pendingCollection.get(0).getGlMapStatusDesc()
						.equals(approvedCollection.get(0).getGlMapStatusDesc())));
		lResult.add(lGlMapStatus);

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
				(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;

	}

	public CbGlMappingsDao getCbGlMappingsDao() {
		return cbGlMappingsDao;
	}

	public void setCbGlMappingsDao(CbGlMappingsDao cbGlMappingsDao) {
		this.cbGlMappingsDao = cbGlMappingsDao;
	}

	@Override
	protected AbstractDao<CbGlMappingsVb> getScreenDao() {
		return cbGlMappingsDao;
	}

	@Override
	protected void setAtNtValues(CbGlMappingsVb vObject) {
		// set AT NT Values here
		vObject.setAttribute01At("5022");
		vObject.setAttribute02At("5023");
		vObject.setAttribute03At("5024");
	}

	@Override
	protected void setVerifReqDeleteType(CbGlMappingsVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("CB_GL_MAPPINGS");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

}