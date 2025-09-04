package com.vision.wb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.AdfControlsDao;
import com.vision.dao.AdfSchedulesDao;
import com.vision.dao.AdfSchedulesDetailsDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.AdfControlsVb;
import com.vision.vb.AdfSchedulesDetailsVb;
import com.vision.vb.AdfSchedulesVb;
@Controller
public class AdfSchedulesDetailsWb extends AbstractDynaWorkerBean<AdfSchedulesDetailsVb>  implements ApplicationContextAware{
	public ApplicationContext applicationContext;
	@Autowired
	private AdfSchedulesDetailsDao adfSchedulesDetailsDao;
	@Autowired
	private AdfSchedulesDao adfSchedulesDao;
	@Autowired
	private AdfControlsDao adfControlsDao;
	
	@Override
	protected AbstractDao<AdfSchedulesDetailsVb> getScreenDao() {
		return adfSchedulesDetailsDao;
	}

	@Override
	protected void setAtNtValues(AdfSchedulesDetailsVb object) {
		object.setModuleStatusAt(211);
		object.setRunItAt(207);
		object.setProgramTypeAt(201);
	}

	@Override
	protected void setVerifReqDeleteType(AdfSchedulesDetailsVb object) {
		object.setVerificationRequired(false);
	}
	/**
	 * 
	 * @return array list of Alpha Sub and Num Sub data required for the BuildSchedulesDetails Screen.  
	 */
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(211);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(207);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(201);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	/**
	 * 
	 * @return Array list of data required for BuildSchedulesDetailsAdd screen.  
	 */
	public ExceptionCode getMajorBuildList(AdfSchedulesVb pAdfSchedulesVb,AdfSchedulesVb arrAdfSchedulesVb,String operation){
		List<AdfSchedulesDetailsVb> arrListLocal;
//		arrListLocal = getProgramsDao().getMajorBuildList();
		arrListLocal = getAdfSchedulesDetailsDao().getMajorBuildList();
		ExceptionCode exceptionCode = CommonUtils.getResultObject(getAdfSchedulesDetailsDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
		if("Add".equalsIgnoreCase(operation)){
			String dbDateTime = getAdfSchedulesDetailsDao().getSystemDate();
			dbDateTime = dbDateTime.replace('/', '-');
			pAdfSchedulesVb.setScheduledDate(dbDateTime);
			pAdfSchedulesVb.setSubmitterId(SessionContextHolder.getContext().getVisionId());
		}
		exceptionCode.setOtherInfo(pAdfSchedulesVb);
		exceptionCode.setResponse(arrListLocal);
		return exceptionCode;
	}
	public ArrayList getPageLoadValuesForProcessControlStatus(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1084);
			arrListLocal.add(collTemp);
			collTemp = getAdfSchedulesDetailsDao().findActiveAlphaSubTabsByAlphaTab(1084);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1079);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	public ExceptionCode getQueryList(AdfSchedulesVb pAdfSchedulesVb,String operation){
		ExceptionCode exceptionCode = null;
		List<AdfControlsVb> adfControlsList = null;
		try{
			String buildCronDebugMode = System.getenv("ADFCRON_DEBUG_MODE");
			if(!ValidationUtil.isValid(buildCronDebugMode)){
				buildCronDebugMode = "N";
			}else if("Y".equalsIgnoreCase(buildCronDebugMode) || "YES".equalsIgnoreCase(buildCronDebugMode)){  
				buildCronDebugMode = "Y";	
			}else{
		  	buildCronDebugMode =  "N";	 
			}
		  if(pAdfSchedulesVb.getCountry() != null && !pAdfSchedulesVb.getCountry().isEmpty() 
		  		&& pAdfSchedulesVb.getLeBook() != null && !pAdfSchedulesVb.getLeBook().isEmpty() && 
		  		!("ZZ".equalsIgnoreCase(pAdfSchedulesVb.getCountry()) && "999".equalsIgnoreCase(pAdfSchedulesVb.getLeBook()))){
			  if("Add".equalsIgnoreCase(operation))
				  adfControlsList = getAdfControlsDao().getQueryList(pAdfSchedulesVb);
			  else if("Modify".equalsIgnoreCase(operation))
				  adfControlsList = getAdfControlsDao().getQueryListForModify(pAdfSchedulesVb);
			  if(adfControlsList != null && adfControlsList.size()>0){
			  	pAdfSchedulesVb.setAdfControlsList(adfControlsList);
			  }
		  }/*else{
		  	adfControlsVb = getAdfControlsDao().getDistinctCtryFromBuildControls(pAdfSchedulesVb.getBuild());
		  	if(adfControlsVb.size() == 1){
		  		pAdfSchedulesVb.setCountry(adfControlsVb.get(0).getCountry());
		  		pAdfSchedulesVb.setLeBook(adfControlsVb.get(0).getLeBook());
		  		adfControlsVb = getAdfControlsDao().getQueryList(pAdfSchedulesVb);
			  }else{
			  	for(AdfControlsVb lBVb: adfControlsVb){
			  		if("ZZ".equalsIgnoreCase(lBVb.getCountry()) && "999".equalsIgnoreCase(lBVb.getLeBook())){
			  			pAdfSchedulesVb.setCountry("ZZ");
			    		pAdfSchedulesVb.setLeBook("999");
			  			break;
			  		}
			  	}
			  	if(pAdfSchedulesVb.getCountry() == null || pAdfSchedulesVb.getCountry().isEmpty() || pAdfSchedulesVb.getLeBook() == null || pAdfSchedulesVb.getLeBook().isEmpty()  ){
			  		if(adfControlsVb != null && !adfControlsVb.isEmpty()){
			  			pAdfSchedulesVb.setCountry(adfControlsVb.get(0).getCountry());
			  			pAdfSchedulesVb.setLeBook(adfControlsVb.get(0).getLeBook());
			  		}
			  	}
			  	if(pAdfSchedulesVb.getCountry() != null && !pAdfSchedulesVb.getCountry().isEmpty() && pAdfSchedulesVb.getLeBook() !=null && !pAdfSchedulesVb.getLeBook().isEmpty()  ){
			  		adfControlsVb = getAdfControlsDao().getQueryList(pAdfSchedulesVb);
			  	}
			  }
		  }*/
		  if("Modify".equalsIgnoreCase(operation)){
		  	List<AdfSchedulesDetailsVb> lBuildSchedulesDetails = getAdfSchedulesDetailsDao().getQueryDisplayResults(pAdfSchedulesVb.getAdfNumber()+"");
		  	for(AdfControlsVb lAdfControlsVb : adfControlsList){
		  		lAdfControlsVb.setRunIt("N");
		  		lAdfControlsVb.setDebug("N");
		  		for(AdfSchedulesDetailsVb lAdfSchedulesDetailsVb : lBuildSchedulesDetails){
		  			if(lAdfSchedulesDetailsVb.getAssociatedBuild().equalsIgnoreCase(lAdfControlsVb.getBuild()) && lAdfSchedulesDetailsVb.getBuildModule().equalsIgnoreCase(lAdfControlsVb.getBuildModule())){
			    		lAdfControlsVb.setRunIt(lAdfSchedulesDetailsVb.getRunIt());
			    		lAdfControlsVb.setDebug(lAdfSchedulesDetailsVb.getDebugMode());
		  			}
		  		}
		  	}
		  }else{
		  	for(AdfControlsVb lAdfControlsVb : adfControlsList){
		  		lAdfControlsVb.setDebug(buildCronDebugMode);
		  	}
		  }
		  if("Add".equalsIgnoreCase(operation) && !ValidationUtil.isValid(pAdfSchedulesVb.getScheduledDate())){
			  String dbDateTime = getAdfSchedulesDetailsDao().getSystemDate();
			dbDateTime = dbDateTime.replace('/', '-');
			pAdfSchedulesVb.setScheduledDate(dbDateTime);
		  }	
		  exceptionCode = CommonUtils.getResultObject(getAdfSchedulesDetailsDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			exceptionCode.setResponse(buildHierarchicalData(adfControlsList));
			exceptionCode.setRequest(adfControlsList);
			//createChart((List<AdfControlsVb>) exceptionCode.getResponse());
			return exceptionCode;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("getQueryList",ex);
			exceptionCode = CommonUtils.getResultObject(getAdfSchedulesDetailsDao().getServiceName(), Constants.ERRONEOUS_OPERATION, "Query", "");
			exceptionCode.setOtherInfo(pAdfSchedulesVb);
			return exceptionCode;
		}
	}
	
	public List<AdfControlsVb> buildHierarchicalData(List<AdfControlsVb> pBuildControls){
		List<AdfControlsVb> buildControls = new ArrayList<AdfControlsVb>();
		if(pBuildControls == null || pBuildControls.isEmpty()){
			return buildControls;
		}
		DeepCopy<AdfControlsVb> deepCopy = new DeepCopy<AdfControlsVb>();
		AdfControlsVb currentParent = null;
		for(AdfControlsVb buildControlsVb : pBuildControls ){
			/*if(buildControlsVb.getBuildLevel() == 1){
				currentParent = deepCopy.copy(buildControlsVb);
				buildControls.add(currentParent);
				continue;
			}
			if(currentParent != null && currentParent.getBuildLevel() < buildControlsVb.getBuildLevel()){
				AdfControlsVb tempBuildControlsVb = deepCopy.copy(buildControlsVb);
				tempBuildControlsVb.setParent(currentParent);
				currentParent.getChildren().add(tempBuildControlsVb);
				currentParent = tempBuildControlsVb;
			}else if(currentParent != null && currentParent.getBuildLevel() == buildControlsVb.getBuildLevel()){
				currentParent = currentParent.getParent();
				AdfControlsVb tempBuildControlsVb = deepCopy.copy(buildControlsVb);
				tempBuildControlsVb.setParent(currentParent);
				currentParent.getChildren().add(tempBuildControlsVb);
			}else if(currentParent != null){
				currentParent = currentParent.getParent().getParent();
				currentParent.getChildren().add(deepCopy.copy(buildControlsVb));
			}*/
			currentParent = deepCopy.copy(buildControlsVb);
			buildControls.add(currentParent);
		}
		return buildControls;
	}
	private String constructTreeData(List<AdfControlsVb> vObjects, StringBuffer htmlData, int idex){
		int subIndex = 10000;
		htmlData.append("{\""+"total"+"\"").append(":"+vObjects.size()+",").append("\""+"rows"+"\"").append(":[");
		for (int Ctr = 0; Ctr < vObjects.size(); Ctr++){
			AdfControlsVb dataObject = (AdfControlsVb) vObjects.get(Ctr);
				if(Ctr!=0)
					htmlData.append(",{");
				else 
					htmlData.append("{");
		    	htmlData.append("\""+"id"+"\"").append(':').append(idex+",");
		    	htmlData.append("\""+"buildModule"+"\"").append(':').append("\""+dataObject.getBuildModule()+"\""+",");
		    	htmlData.append("\""+"programDescription"+"\"").append(':').append("\""+dataObject.getProgramDescription()+"\""+",");
		    	htmlData.append("\""+"runIt"+"\"").append(':').append("\""+dataObject.getRunIt()+"\""+",");
		    	htmlData.append("\""+"debug"+"\"").append(':').append("\""+dataObject.getDebug()+"\""+",");
		    	htmlData.append("\""+"fullBuild"+"\"").append(':').append("\""+dataObject.getFullBuild()+"\""+",");
		    	htmlData.append("\""+"lastBuildDate"+"\"").append(':').append("\""+dataObject.getLastBuildDate()+"\""+"");
		    	htmlData.append("}");
			    if (dataObject.getChildren() != null && dataObject.getChildren().size() > 0){
			    	//htmlData.append("\""+"children"+"\"").append(":[");
			    	for (int Ctr1 = 0; Ctr1 < dataObject.getChildren().size(); Ctr1++){
			    		AdfControlsVb dataObject1 = (AdfControlsVb) dataObject.getChildren().get(Ctr1);
			    		htmlData.append(",{");
			    		htmlData.append("\""+"id"+"\"").append(':').append(+subIndex+",");
			    		htmlData.append("\""+"buildModule"+"\"").append(':').append("\""+dataObject1.getBuildModule()+"\""+",");
			    		htmlData.append("\""+"programDescription"+"\"").append(':').append("\""+dataObject1.getProgramDescription()+"\""+",");
			    		htmlData.append("\""+"runIt"+"\"").append(':').append("\""+dataObject1.getRunIt()+"\""+",");
			    		htmlData.append("\""+"debug"+"\"").append(':').append("\""+dataObject1.getDebug()+"\""+",");
			    		htmlData.append("\""+"fullBuild"+"\"").append(':').append("\""+dataObject1.getFullBuild()+"\""+",");
				    	htmlData.append("\""+"lastBuildDate"+"\"").append(':').append("\""+dataObject1.getLastBuildDate()+"\",");
				    	htmlData.append("\""+"_parentId"+"\"").append(':').append(idex);
			    		htmlData.append("}");
			    	}
			    	//htmlData.append("]");
			    }
			    //htmlData.append("}");
			    htmlData.append("\n");

			//if(idex==2) break;
			subIndex++;
			idex++;
		}
		htmlData.append("]}");
		return htmlData.toString();
	}
	
	public ExceptionCode adfSchedulesDetailsAudit(AdfSchedulesDetailsVb adfSchedulesDetailsVb){
		ExceptionCode exceptionCode = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			List<AdfSchedulesDetailsVb> adfSchedules = null;
			adfSchedules = getAdfSchedulesDetailsDao().getAdfSchedulesAudit(adfSchedulesDetailsVb);
			arrListLocal.add(adfSchedules);
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
			exceptionCode.setOtherInfo(adfSchedulesDetailsVb);
			exceptionCode.setResponse(adfSchedules);
		}catch(RuntimeCustomException rex){
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			return exceptionCode;
		}
		return exceptionCode;
	}
	
	private void createChart(List<AdfControlsVb> vObjects){
		Writer writer = null;
		String xmlData = "";
		StringBuffer htmlData = new StringBuffer();
		int idex = 1;
        try {
			String filePath = ((WebApplicationContext)applicationContext).getServletContext().getRealPath("Vision");
			filePath = filePath.substring(0, filePath.length()-6);
			//System.out.print("File Path:"+ filePath);
			File lFile = new File(filePath+"treegrid_data1.json");
	        if(lFile.exists()){
	        	lFile.delete();
			}
	        lFile.createNewFile();
	        xmlData = constructTreeData(vObjects, htmlData, idex);
	        writer = new BufferedWriter(new FileWriter(lFile));
        	writer.write(xmlData);
	        writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public AdfSchedulesDetailsDao getAdfSchedulesDetailsDao() {
		return adfSchedulesDetailsDao;
	}

	public void setAdfSchedulesDetailsDao(AdfSchedulesDetailsDao adfSchedulesDetailsDao) {
		this.adfSchedulesDetailsDao = adfSchedulesDetailsDao;
	}

	public AdfControlsDao getAdfControlsDao() {
		return adfControlsDao;
	}

	public void setAdfControlsDao(AdfControlsDao adfControlsDao) {
		this.adfControlsDao = adfControlsDao;
	}

	public AdfSchedulesDao getAdfSchedulesDao() {
		return adfSchedulesDao;
	}

	public void setAdfSchedulesDao(AdfSchedulesDao adfSchedulesDao) {
		this.adfSchedulesDao = adfSchedulesDao;
	}
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext=arg0;
		
	}
	
	public String getDateFromVisionBusinessDate(AdfSchedulesVb vOnject) {
		String dateFromVBD = getAdfSchedulesDetailsDao().getDateFromVisionBusinessDate(vOnject);
		return dateFromVBD;
	}
	
	public String getDateForBusinessFeedDate(AdfSchedulesVb vOnject) {
		String dateFromVBD = getAdfSchedulesDao().getDateForBusinessFeedDate(vOnject);
		return dateFromVBD;
	}
}
