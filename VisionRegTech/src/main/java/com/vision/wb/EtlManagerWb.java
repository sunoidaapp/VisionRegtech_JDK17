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
import com.vision.dao.EtlManagerDao;
import com.vision.dao.EtlManagerDetailsDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.CommonVb;
import com.vision.vb.EtlManagerDetailsVb;
import com.vision.vb.EtlManagerVb;
@Component
public class EtlManagerWb extends AbstractDynaWorkerBean<EtlManagerVb>{
	@Autowired
	private EtlManagerDao etlManagerDao;
	@Autowired
	private EtlManagerDetailsDao etlManagerDetailsDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	
	public static Logger logger = LoggerFactory.getLogger(EtlManagerWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);//status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);//record indicator
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7035);//Engine
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7036);//Event List
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7037);//Feed Category
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findAlphaSubTabsOrderByIntValues(7025);//extraction monthday 
			arrListLocal.add(collTemp);
			collTemp = commonDao.findVerificationRequiredAndStaticDelete("RA_MST_EXTRACTION_HEADER");
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<EtlManagerVb> getScreenDao() {
		return etlManagerDao;
	}
	@Override
	protected void setAtNtValues(EtlManagerVb vObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setVerifReqDeleteType(EtlManagerVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_MST_EXTRACTION_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	public ExceptionCode getQueryResults(EtlManagerVb vObject){
		int intStatus = 0;
		EtlManagerVb etlManagerVb = new EtlManagerVb();
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		Boolean pendFlag = true;
		List<EtlManagerVb> collTemp = etlManagerDao.getQueryResults(vObject,intStatus);
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(etlManagerDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		}else{
			collTemp.forEach(headerVb -> {
				List<EtlManagerDetailsVb> detailslst = etlManagerDetailsDao.getQueryEtlDetails(headerVb, 0);
				 headerVb.setEtlManagerDetaillst(detailslst);
			 });
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}
}
