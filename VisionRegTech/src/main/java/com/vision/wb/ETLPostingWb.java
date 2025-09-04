package com.vision.wb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.EtlPostingsDao;
import com.vision.vb.CommonVb;
import com.vision.vb.EtlPostingsVb;
@Component
public class ETLPostingWb extends AbstractDynaWorkerBean<EtlPostingsVb>{

	@Autowired
	EtlPostingsDao etlPostingsDao;
	
	@Autowired
	CommonDao commonDao;
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		EtlPostingsVb etlPostingsVb = new EtlPostingsVb();
		try{
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			String countryLebook = country+ "-" +leBook;
			String businessDate = commonDao.getVisionBusinessDate(countryLebook);
			arrListLocal.add(etlPostingsVb);
			etlPostingsVb.setBusinessDate(businessDate);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	protected void setVerifReqDeleteType(EtlPostingsVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_TRN_POSTING_HISTORY");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(false);
		//vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	protected AbstractDao<EtlPostingsVb> getScreenDao() {
		// TODO Auto-generated method stub
		return etlPostingsDao;
	}

	@Override
	protected void setAtNtValues(EtlPostingsVb vObject) {
		// TODO Auto-generated method stub
		
	}

}
