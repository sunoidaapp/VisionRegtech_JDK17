package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.AccountsMappingDao;
import com.vision.util.CommonUtils;
import com.vision.vb.AccountsMappingVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
@Controller
public class AccountsMappingWb extends AbstractDynaWorkerBean<AccountsMappingVb>{
		@Autowired
		private AccountsMappingDao accountsMappingDao;
		public static Logger logger = LoggerFactory.getLogger(AccountsMappingWb.class);
		
		public ArrayList getPageLoadValues(){
			List collTemp = null;
			ArrayList<Object> arrListLocal = new ArrayList<Object>();
			try{
				collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
				arrListLocal.add(collTemp);
				collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
				arrListLocal.add(collTemp);
				collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("ACCOUNTS_MAPPING");
				arrListLocal.add(collTemp);
				return arrListLocal;
			}catch(Exception ex){
				ex.printStackTrace();
				logger.error("Exception in getting the Page load values.", ex);
				return null;
			}
		}
		@Override
		protected void doSetDesctiptionsAfterQuery(AccountsMappingVb vObject){
			List<AccountsMappingVb> lResult = getAccountsMappingDao().getAccountName(vObject);
			if(lResult != null && !lResult.isEmpty()){
				vObject.setAccountNoDesc(lResult.get(0).getAccountNoDesc());
			}		
		}
		public AccountsMappingDao getAccountsMappingDao() {
			return accountsMappingDao;
		}
		public void setAccountsMappingDao(AccountsMappingDao accountsMappingDao) {
			this.accountsMappingDao = accountsMappingDao;
		}
		@Override
		protected AbstractDao<AccountsMappingVb> getScreenDao() {
			return accountsMappingDao;
		}		
		@Override
		protected void setVerifReqDeleteType(AccountsMappingVb vObject){
			ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("ACCOUNTS_MAPPING");
			vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
			vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
		}
		@Override
		protected void setAtNtValues(AccountsMappingVb vObject){
			vObject.setRecordIndicatorNt(7);
			vObject.setAccountsMappingStatusNt(1);
		}
		@Override
		protected List<ReviewResultVb> transformToReviewResults(List<AccountsMappingVb> approvedCollection, List<AccountsMappingVb> pendingCollection) {
			ResourceBundle rsb = CommonUtils.getResourceManger();
			ArrayList collTemp = getPageLoadValues();
			if(pendingCollection != null)
				getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
			if(approvedCollection != null)
				getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
			ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
	      /*  ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry());
	        lResult.add(lCountry);*/
	        ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook());
	        lResult.add(lLeBook);
	        ReviewResultVb lAccountNo = new ReviewResultVb(rsb.getString("accountNo"),
	    			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getAccountNo(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getAccountNo());
	        lResult.add(lAccountNo);
	        ReviewResultVb lProductDr = new ReviewResultVb(rsb.getString("productDr"),
	    			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProductDr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProductDr());
	        lResult.add(lProductDr);   
	        ReviewResultVb lProductCr = new ReviewResultVb(rsb.getString("productCr"),
				    (pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProductCr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProductCr());
	        lResult.add(lProductCr); 
	        ReviewResultVb lFrlLineBsDr = new ReviewResultVb(rsb.getString("frlLineBSDr"),
	    			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFrlLineBsDr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFrlLineBsDr());
	        lResult.add(lFrlLineBsDr); 
	        ReviewResultVb lFrlLineBsCr = new ReviewResultVb(rsb.getString("frlLineBSCr"),
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFrlLineBsCr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFrlLineBsCr());
	        lResult.add(lFrlLineBsCr);
	        ReviewResultVb lFrlLinePlDr = new ReviewResultVb(rsb.getString("frlLinePLDr"),
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFrlLinePlDr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFrlLinePlDr());
	        lResult.add(lFrlLinePlDr);
	        ReviewResultVb lFrlLinePlCr = new ReviewResultVb(rsb.getString("frlLinePLCr"),
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFrlLinePlCr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFrlLinePlCr());
	        lResult.add(lFrlLinePlCr);
	        ReviewResultVb lMrlLineDr = new ReviewResultVb(rsb.getString("MrlLineDr"),
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMrlLineDr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMrlLineDr());
	        lResult.add(lMrlLineDr);
	        ReviewResultVb lMrlLineCr = new ReviewResultVb(rsb.getString("MrlLineCr"),
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMrlLineCr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMrlLineCr());
	        lResult.add(lMrlLineCr);
	        ReviewResultVb lAccountsMappingStatus = new ReviewResultVb(rsb.getString("accountsMappingStatus"),
	    			(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getAccountsMappingStatus()),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getAccountsMappingStatus()));
	        lResult.add(lAccountsMappingStatus);   
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
}
