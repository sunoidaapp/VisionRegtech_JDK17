package com.vision.wb;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.FeesConfigDetailsDao;
import com.vision.dao.FeesConfigHeadersDao;
import com.vision.dao.FeesConfigTierDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.CommonVb;
import com.vision.vb.FeesConfigDetailsVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.FeesConfigTierVb;
@Component
public class FeesConfigHeadersWb extends AbstractDynaWorkerBean<FeesConfigHeaderVb>{
	@Autowired
	private FeesConfigHeadersDao feesConfigHeadersDao;
	@Autowired
	private FeesConfigDetailsDao feesConfigDetailsDao;
	@Autowired
	private FeesConfigTierDao feesConfigTierDao;
	
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	
	public static Logger logger = LoggerFactory.getLogger(FeesConfigHeadersWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);//status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);//record indicator
			arrListLocal.add(collTemp);
			collTemp= commonDao.getVisionBusinessDate();
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<FeesConfigHeaderVb> getScreenDao() {
		return feesConfigHeadersDao;
	}
	@Override
	protected void setAtNtValues(FeesConfigHeaderVb vObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setVerifReqDeleteType(FeesConfigHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_MST_FEES_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	public ExceptionCode getQueryResults(FeesConfigHeaderVb vObject){
		int intStatus = 1;
		FeesConfigHeaderVb transLineHeaderVb = new FeesConfigHeaderVb();
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		Boolean pendFlag = true;
		List<FeesConfigDetailsVb> feesConfigDetaillst = new ArrayList<FeesConfigDetailsVb>();
		//List<FeesConfigTierVb> feesConfigTierlst = new ArrayList<FeesConfigTierVb>();
		List<FeesConfigHeaderVb> collTemp = feesConfigHeadersDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = feesConfigHeadersDao.getQueryResults(vObject,intStatus);
			pendFlag = false;
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(feesConfigHeadersDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		}else{
			if(!pendFlag) {
				feesConfigDetaillst = feesConfigDetailsDao.getFeesConfigDetails(vObject,Constants.STATUS_ZERO);
				feesConfigDetaillst.forEach(detailVb -> {
					detailVb.setVerificationRequired(vObject.isVerificationRequired());
					detailVb.setNewRecord(true);
					List<FeesConfigTierVb> feesConfigTierlst = feesConfigTierDao.getFeesConfigTier(detailVb,Constants.STATUS_ZERO);
					detailVb.setFeesTierlst(feesConfigTierlst);
				});
			}else {
				feesConfigDetaillst = feesConfigDetailsDao.getFeesConfigDetails(vObject,Constants.STATUS_DELETE);
				feesConfigDetaillst.forEach(detailVb -> {
					detailVb.setVerificationRequired(vObject.isVerificationRequired());
					detailVb.setNewRecord(true);
					List<FeesConfigTierVb> feesConfigTierlst = feesConfigTierDao.getFeesConfigTier(detailVb,Constants.STATUS_DELETE);
					detailVb.setFeesTierlst(feesConfigTierlst);
				});
			}
			if(feesConfigDetaillst != null && !feesConfigDetaillst.isEmpty()) {
				collTemp.get(0).setFeesConfigDetaillst(feesConfigDetaillst);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}
}
