package com.vision.wb;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.BusinessLineConfigDao;
import com.vision.dao.BusinessLineConfigGLDao;
import com.vision.dao.CommonDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.BusinessLineGLVb;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.CommonVb;
@Component
public class BusinessLineConfigWb extends AbstractDynaWorkerBean<BusinessLineHeaderVb>{
	@Autowired
	private BusinessLineConfigDao businessLineConfigDao;
	@Autowired
	private BusinessLineConfigGLDao businessLineConfigGLDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	
	public static Logger logger = LoggerFactory.getLogger(BusinessLineConfigWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	public ExceptionCode getQueryResults(BusinessLineHeaderVb vObject){
		int intStatus = 1;
		BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		Boolean pendFlag = true;
		List<BusinessLineGLVb> businessGlList = new ArrayList<BusinessLineGLVb>();
		List<BusinessLineHeaderVb> collTemp = businessLineConfigDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = businessLineConfigDao.getQueryResults(vObject,intStatus);
			pendFlag = false;
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(businessLineConfigDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		}else{
			if(!pendFlag) {
				businessGlList = businessLineConfigGLDao.getBusinessGLDetails(vObject,Constants.STATUS_ZERO);
			}else {
				businessGlList = businessLineConfigGLDao.getBusinessGLDetails(vObject,Constants.STATUS_DELETE);
			}
			if(businessGlList != null && !businessGlList.isEmpty()) {
				collTemp.get(0).setBusinessLineGllst(businessGlList);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}
	@Override
	protected AbstractDao<BusinessLineHeaderVb> getScreenDao() {
		return businessLineConfigDao;
	}
	@Override
	protected void setAtNtValues(BusinessLineHeaderVb vObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setVerifReqDeleteType(BusinessLineHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_MST_BUSINESS_LINE_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
}
