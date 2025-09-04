package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.GlMappingDao;
import com.vision.dao.NumSubTabDao;
import com.vision.util.CommonUtils;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.GLMappingVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.VisionUsersVb;

@Component
public class GLMappingWb extends AbstractDynaWorkerBean<GLMappingVb> {

	@Autowired
	GlMappingDao glMappingDao;
	
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	
	@Override
	protected AbstractDao<GLMappingVb> getScreenDao() {
		return glMappingDao;
	}

	@Override
	protected void setAtNtValues(GLMappingVb vObject) {
		// TODO Auto-generated method stub
		
	}
	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);// status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);// record indicator
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(1004);// GL_MAPPING_ATTRIBUTE_TYPE_AT
			arrListLocal.add(collTemp);
			collTemp = glMappingDao.getTemplatName();// Template names
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().getLegalEntity();
			arrListLocal.add(collTemp);
			String country = "";
			String leBook = "";
			VisionUsersVb visionUsers = SessionContextHolder.getContext();
			if("Y".equalsIgnoreCase(visionUsers.getUpdateRestriction())) {
				if(ValidationUtil.isValid(visionUsers.getCountry())) {
					country = visionUsers.getCountry();
				}
				if(ValidationUtil.isValid(visionUsers.getLeBook())) {
					leBook = visionUsers.getLeBook();
				}
			}else {
				country = getCommonDao().findVisionVariableValue("DEFAULT_COUNTRY");
				leBook = getCommonDao().findVisionVariableValue("DEFAULT_LE_BOOK");
			}
			String countryLeBook = country + "-" + leBook;
			arrListLocal.add(countryLeBook);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	protected void setVerifReqDeleteType(GLMappingVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("GL_MAPPING");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<GLMappingVb> approvedCollection,
			List<GLMappingVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		
//		ReviewResultVb lCountry = new ReviewResultVb("Country",
//				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
//						: pendingCollection.get(0).getCountry(),
//				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
//						: approvedCollection.get(0).getCountry(),
//				(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
//		lResult.add(lCountry);
//
//		ReviewResultVb lLeBook = new ReviewResultVb("LeBook",
//				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
//						: pendingCollection.get(0).getLeBook(),
//				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
//						: approvedCollection.get(0).getLeBook(),
//				(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
//		lResult.add(lLeBook);

		ReviewResultVb lTemplateName = new ReviewResultVb("Template ID",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getTemplateId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getTemplateId(),
						(!pendingCollection.get(0).getTemplateId()
								.equals(approvedCollection.get(0).getTemplateId())));
		lResult.add(lTemplateName);
		ReviewResultVb lGeneralDescription = new ReviewResultVb(rsb.getString("glCodes"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlCode(),
						(!pendingCollection.get(0).getGlCode()
								.equals(approvedCollection.get(0).getGlCode())));
		lResult.add(lGeneralDescription);
		ReviewResultVb lFeedCategory = new ReviewResultVb("GL Mapping Group ID",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlMappingGroupId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlMappingGroupId(),
						(!pendingCollection.get(0).getGlMappingGroupId()
								.equals(approvedCollection.get(0).getGlMappingGroupId())));
		lResult.add(lFeedCategory);
		ReviewResultVb lStagingName = new ReviewResultVb("GL Mapping Sequence",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlMappingSeq(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlMappingSeq(),
						(!pendingCollection.get(0).getGlMappingSeq()
								.equals(approvedCollection.get(0).getGlMappingSeq())));
		lResult.add(lStagingName);
		ReviewResultVb lViewName = new ReviewResultVb("GL Mapping Attribute Type",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlMappingAttrType(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlMappingAttrType(),
						(!pendingCollection.get(0).getGlMappingAttrType()
								.equals(approvedCollection.get(0).getGlMappingAttrType())));
		lResult.add(lViewName);
//		ReviewResultVb lEffectiveDate = new ReviewResultVb("GL Mapping Attribute",
//				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
//						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
//								Integer.parseInt(pendingCollection.get(0).getGlMappinAttrType())),
//				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
//						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
//								Integer.parseInt(approvedCollection.get(0).getGlMappinAttrType())),
//						(pendingCollection.get(0).getGlMappinAttrType()!=(approvedCollection.get(0).getGlMappinAttrType())));
//		lResult.add(lEffectiveDate);
		ReviewResultVb lProgram = new ReviewResultVb("CB GL Code",
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getCbGlCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCbGlCode(),
						(!pendingCollection.get(0).getCbGlCode()
								.equals(approvedCollection.get(0).getCbGlCode())));
		lResult.add(lProgram);
		
		ReviewResultVb lTemplateStatus = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								pendingCollection.get(0).getGlMappingStatus()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								approvedCollection.get(0).getGlMappingStatus()),
						(pendingCollection.get(0).getGlMappingStatus()!=(approvedCollection.get(0).getGlMappingStatus())));
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

}
