package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.CbGlCodesDao;
import com.vision.dao.CommonDao;
import com.vision.util.CommonUtils;
import com.vision.vb.CbGlCodesVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ReviewResultVb;

@Controller
public class CbGlCodesWb extends AbstractDynaWorkerBean<CbGlCodesVb> {

	@Autowired
	private CbGlCodesDao cbGlCodesDao;
	@Autowired
	private CommonDao commonDao;
	public static Logger logger = LoggerFactory.getLogger(CbGlCodesWb.class);

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7001);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5004);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5005);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5006);
			arrListLocal.add(collTemp);

			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5030);
			arrListLocal.add(collTemp);
//			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
//			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add("");
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
	protected List<ReviewResultVb> transformToReviewResults(List<CbGlCodesVb> approvedCollection,
			List<CbGlCodesVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
//
//				ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
//					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
//					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry(),(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
//				lResult.add(lCountry);
//
//				ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
//					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
//					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
//				lResult.add(lLeBook);
//
		ReviewResultVb lGlCode = new ReviewResultVb(rsb.getString("glCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getGlCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlCode(),
				(!pendingCollection.get(0).getGlCode().equals(approvedCollection.get(0).getGlCode())));
		lResult.add(lGlCode);

		ReviewResultVb lGlDescription = new ReviewResultVb(rsb.getString("glDescription"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlDescription(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlDescription(),
				(!pendingCollection.get(0).getGlDescription().equals(approvedCollection.get(0).getGlDescription())));
		lResult.add(lGlDescription);

		ReviewResultVb lGlType = new ReviewResultVb(rsb.getString("glType"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlTypeDesc(),
				(!pendingCollection.get(0).getGlTypeDesc().equals(approvedCollection.get(0).getGlTypeDesc())));
		lResult.add(lGlType);

		ReviewResultVb lParentGlCode = new ReviewResultVb(rsb.getString("parentGlCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getParentGlCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getParentGlCode(),
				(!pendingCollection.get(0).getParentGlCode().equals(approvedCollection.get(0).getParentGlCode())));
		lResult.add(lParentGlCode);

		ReviewResultVb lGlAttribute1 = new ReviewResultVb(rsb.getString("glAttribute1"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlAttribute1(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlAttribute1(),
				(!pendingCollection.get(0).getGlAttribute1().equals(approvedCollection.get(0).getGlAttribute1())));
		lResult.add(lGlAttribute1);

		ReviewResultVb lGlAttribute2 = new ReviewResultVb(rsb.getString("glAttribute2"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlAttribute2(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlAttribute2(),
				(!pendingCollection.get(0).getGlAttribute2().equals(approvedCollection.get(0).getGlAttribute2())));
		lResult.add(lGlAttribute2);

		ReviewResultVb lGlAttribute3 = new ReviewResultVb(rsb.getString("glAttribute3"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlAttribute3(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlAttribute3(),
				(!pendingCollection.get(0).getGlAttribute3().equals(approvedCollection.get(0).getGlAttribute3())));
		lResult.add(lGlAttribute3);

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
				(!pendingCollection.get(0).getMakerName().equals(approvedCollection.get(0).getMakerName())));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
				(pendingCollection.get(0).getVerifier()!= (approvedCollection.get(0).getVerifier())));
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

	public CbGlCodesDao getCbGlCodesDao() {
		return cbGlCodesDao;
	}

	public void setCbGlCodesDao(CbGlCodesDao cbGlCodesDao) {
		this.cbGlCodesDao = cbGlCodesDao;
	}

	@Override
	protected AbstractDao<CbGlCodesVb> getScreenDao() {
		return cbGlCodesDao;
	}

	@Override
	protected void setAtNtValues(CbGlCodesVb vObject) {
//				set AT NT Values here
	}

	@Override
	protected void setVerifReqDeleteType(CbGlCodesVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("CB_GL_CODES");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

}