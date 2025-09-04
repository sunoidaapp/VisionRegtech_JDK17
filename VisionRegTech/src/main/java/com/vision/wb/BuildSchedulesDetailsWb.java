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
import com.vision.dao.BuildControlsDao;
import com.vision.dao.BuildSchedulesDao;
import com.vision.dao.BuildSchedulesDetailsDao;
import com.vision.dao.ProgramsDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.BuildControlsVb;
import com.vision.vb.BuildSchedulesDetailsVb;
import com.vision.vb.BuildSchedulesVb;
import com.vision.vb.ProgramsVb;

@Controller
public class BuildSchedulesDetailsWb extends AbstractDynaWorkerBean<BuildSchedulesDetailsVb>  implements ApplicationContextAware{
	public ApplicationContext applicationContext;
	
	@Autowired
	private BuildSchedulesDetailsDao buildSchedulesDetailsDao;
	
	@Autowired
	private BuildSchedulesDao buildSchedulesDao;
	
	@Autowired
	private ProgramsDao programsDao;
	
	@Autowired
	private BuildControlsDao buildControlsDao;
	
	@Override
	protected AbstractDao<BuildSchedulesDetailsVb> getScreenDao() {
		return buildSchedulesDetailsDao;
	}

	@Override
	protected void setAtNtValues(BuildSchedulesDetailsVb object) {
		object.setModuleStatusAt(211);
		object.setRunItAt(207);
		object.setProgramTypeAt(201);
	}

	@Override
	protected void setVerifReqDeleteType(BuildSchedulesDetailsVb object) {
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
	public ExceptionCode getMajorBuildList(BuildSchedulesVb pBuildSchedulesVb,String operation){
		List<ProgramsVb> arrListLocal;
		arrListLocal = getProgramsDao().getMajorBuildList();
		ExceptionCode exceptionCode = CommonUtils.getResultObject(getBuildSchedulesDetailsDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
		if("Add".equalsIgnoreCase(operation)){
			String dbDateTime = getBuildSchedulesDetailsDao().getSystemDate();
			dbDateTime = dbDateTime.replace('/', '-');
			pBuildSchedulesVb.setScheduledDate(dbDateTime);
			pBuildSchedulesVb.setSubmitterId(SessionContextHolder.getContext().getVisionId());
		}
		exceptionCode.setOtherInfo(pBuildSchedulesVb);
		exceptionCode.setResponse(arrListLocal);
		return exceptionCode;
	}
	
	public ExceptionCode getQueryList(BuildSchedulesVb pBuildSchedulesVb,String operation){
		ExceptionCode exceptionCode = null;
		List<BuildControlsVb> buildControls = null;
		try{
			String buildCronDebugMode = getVisionVariableValue("BUILDCRON_DEBUG_MODE");
			if(buildCronDebugMode == null || buildCronDebugMode.isEmpty()){
				buildCronDebugMode = "N";
			}else if("Y".equalsIgnoreCase(buildCronDebugMode) || "YES".equalsIgnoreCase(buildCronDebugMode)){  
				buildCronDebugMode = "Y";	
		    }
		    else{
		    	buildCronDebugMode =  "N";	 
		    }
		    if(pBuildSchedulesVb.getCountry() != null && !pBuildSchedulesVb.getCountry().isEmpty() 
		    		&& pBuildSchedulesVb.getLeBook() != null && !pBuildSchedulesVb.getLeBook().isEmpty() && 
		    		!("ZZ".equalsIgnoreCase(pBuildSchedulesVb.getCountry()) && "999".equalsIgnoreCase(pBuildSchedulesVb.getLeBook()))){
		    	buildControls = getBuildControlsDao().getQueryList(pBuildSchedulesVb);
			    if(buildControls != null && buildControls.size()>0){
			    	pBuildSchedulesVb.setBuildControls(buildControls);
			    }
		    }else{
		    	buildControls = getBuildControlsDao().getDistinctCtryFromBuildControls(pBuildSchedulesVb.getBuild());
		    	if(buildControls.size() == 1){
		    		pBuildSchedulesVb.setCountry(buildControls.get(0).getCountry());
		    		pBuildSchedulesVb.setLeBook(buildControls.get(0).getLeBook());
		    		buildControls = getBuildControlsDao().getQueryList(pBuildSchedulesVb);
			    }else{
			    	for(BuildControlsVb lBVb: buildControls){
			    		if("ZZ".equalsIgnoreCase(lBVb.getCountry()) && "99".equalsIgnoreCase(lBVb.getLeBook())){
			    			pBuildSchedulesVb.setCountry("ZZ");
				    		pBuildSchedulesVb.setLeBook("99");
			    			break;
			    		}
			    	}
			    	if(pBuildSchedulesVb.getCountry() == null || pBuildSchedulesVb.getCountry().isEmpty() || pBuildSchedulesVb.getLeBook() == null || pBuildSchedulesVb.getLeBook().isEmpty()  ){
			    		if(buildControls != null && !buildControls.isEmpty()){
			    			pBuildSchedulesVb.setCountry(buildControls.get(0).getCountry());
			    			pBuildSchedulesVb.setLeBook(buildControls.get(0).getLeBook());
			    		}
			    	}
			    	if(pBuildSchedulesVb.getCountry() != null && !pBuildSchedulesVb.getCountry().isEmpty() && pBuildSchedulesVb.getLeBook() !=null && !pBuildSchedulesVb.getLeBook().isEmpty()  ){
			    		buildControls = getBuildControlsDao().getQueryList(pBuildSchedulesVb);
			    	}
			    }
		    }
		    if("Modify".equalsIgnoreCase(operation)){
		    	List<BuildSchedulesDetailsVb> lBuildSchedulesDetails = getBuildSchedulesDetailsDao().getQueryDisplayResults(pBuildSchedulesVb.getBuildNumber()+"");
		    	for(BuildControlsVb lBuildControlsVb : buildControls){
		    		lBuildControlsVb.setRunIt("N");
		    		lBuildControlsVb.setDebug("N");
		    		for(BuildSchedulesDetailsVb lBuildSchedulesDetailsVb : lBuildSchedulesDetails){
		    			if(lBuildSchedulesDetailsVb.getAssociatedBuild().equalsIgnoreCase(lBuildControlsVb.getBuild()) && lBuildSchedulesDetailsVb.getBuildModule().equalsIgnoreCase(lBuildControlsVb.getBuildModule())){
				    		lBuildControlsVb.setRunIt(lBuildSchedulesDetailsVb.getRunIt());
				    		lBuildControlsVb.setDebug(lBuildSchedulesDetailsVb.getDebugMode());
		    			}
		    		}
		    	}
		    }else{
		    	for(BuildControlsVb lBuildControlsVb : buildControls){
		    		lBuildControlsVb.setDebug(buildCronDebugMode);
		    	}
		    }
		    if("Add".equalsIgnoreCase(operation) && !ValidationUtil.isValid(pBuildSchedulesVb.getScheduledDate())){
			    String dbDateTime = getBuildSchedulesDetailsDao().getSystemDate();
				dbDateTime = dbDateTime.replace('/', '-');
				pBuildSchedulesVb.setScheduledDate(dbDateTime);
		    }	
		    exceptionCode = CommonUtils.getResultObject(getBuildSchedulesDetailsDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			exceptionCode.setResponse(buildHierarchicalData(buildControls));
			exceptionCode.setRequest(buildControls);
			//createChart((List<BuildControlsVb>) exceptionCode.getResponse());
			return exceptionCode;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("getQueryList",ex);
			exceptionCode = CommonUtils.getResultObject(getBuildSchedulesDetailsDao().getServiceName(), Constants.ERRONEOUS_OPERATION, "Query", "");
			exceptionCode.setOtherInfo(pBuildSchedulesVb);
			return exceptionCode;
		}
	}
	public List<BuildControlsVb> buildHierarchicalData(List<BuildControlsVb> pBuildControls){
		List<BuildControlsVb> buildControls = new ArrayList<BuildControlsVb>();
		if(pBuildControls == null || pBuildControls.isEmpty()){
			return buildControls;
		}
		DeepCopy<BuildControlsVb> deepCopy = new DeepCopy<BuildControlsVb>();
		BuildControlsVb currentParent = null;
		for(BuildControlsVb buildControlsVb : pBuildControls ){
			if(buildControlsVb.getBuildLevel() == 1){
				currentParent = deepCopy.copy(buildControlsVb);
				buildControls.add(currentParent);
				continue;
			}
			if(currentParent != null && currentParent.getBuildLevel() < buildControlsVb.getBuildLevel()){
				BuildControlsVb tempBuildControlsVb = deepCopy.copy(buildControlsVb);
				tempBuildControlsVb.setParent(currentParent);
				currentParent.getChildren().add(tempBuildControlsVb);
				currentParent = tempBuildControlsVb;
			}else if(currentParent != null && currentParent.getBuildLevel() == buildControlsVb.getBuildLevel()){
				currentParent = currentParent.getParent();
				BuildControlsVb tempBuildControlsVb = deepCopy.copy(buildControlsVb);
				tempBuildControlsVb.setParent(currentParent);
				currentParent.getChildren().add(tempBuildControlsVb);
			}else if(currentParent != null){
				currentParent = currentParent.getParent().getParent();
				currentParent.getChildren().add(deepCopy.copy(buildControlsVb));
			}
		}
		return buildControls;
	}
	private String constructTreeData(List<BuildControlsVb> vObjects, StringBuffer htmlData, int idex){
		int subIndex = 10000;
		htmlData.append("{\""+"total"+"\"").append(":"+vObjects.size()+",").append("\""+"rows"+"\"").append(":[");
		for (int Ctr = 0; Ctr < vObjects.size(); Ctr++){
			BuildControlsVb dataObject = (BuildControlsVb) vObjects.get(Ctr);
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
			    		BuildControlsVb dataObject1 = (BuildControlsVb) dataObject.getChildren().get(Ctr1);
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
	private void createChart(List<BuildControlsVb> vObjects){
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
	public BuildSchedulesDetailsDao getBuildSchedulesDetailsDao() {
		return buildSchedulesDetailsDao;
	}

	public void setBuildSchedulesDetailsDao(BuildSchedulesDetailsDao buildSchedulesDetailsDao) {
		this.buildSchedulesDetailsDao = buildSchedulesDetailsDao;
	}

	public ProgramsDao getProgramsDao() {
		return programsDao;
	}

	public void setProgramsDao(ProgramsDao programsDao) {
		this.programsDao = programsDao;
	}

	public BuildControlsDao getBuildControlsDao() {
		return buildControlsDao;
	}

	public void setBuildControlsDao(BuildControlsDao buildControlsDao) {
		this.buildControlsDao = buildControlsDao;
	}

	public BuildSchedulesDao getBuildSchedulesDao() {
		return buildSchedulesDao;
	}

	public void setBuildSchedulesDao(BuildSchedulesDao buildSchedulesDao) {
		this.buildSchedulesDao = buildSchedulesDao;
	}
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext=arg0;
	}
	
	/*public String getDateForBusinessFeedDate(String Country,String fullLeBook, String majorBuild) {
		String dateFromVBD = getBuildSchedulesDao().getDateForBusinessFeedDate(Country,fullLeBook,majorBuild);
		return dateFromVBD;
	}*/
}
