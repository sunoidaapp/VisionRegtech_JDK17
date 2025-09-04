package com.vision.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.rowset.CachedRowSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.exception.ExceptionCode;

@Component
public class ChartUtils {
	@Autowired
	CommonDao commonDao;
	
	public static Logger logger = LoggerFactory.getLogger(ChartUtils.class);
	
	public ExceptionCode updateReturnXmlForSingleRepeatTag(String repeatTagMain, String chartXML, String xAxisCol, String yAxisCol, String zAxisCol, String seriesCol, String returnChartXml, ResultSet rs, StringBuffer dataExistCheck,String ddKeyCol) throws SQLException{
		ExceptionCode exceptionCode = new ExceptionCode();
		Matcher matcherObj = Pattern.compile("\\<"+repeatTagMain+"(.*?)\\<\\/"+repeatTagMain+"\\>",Pattern.DOTALL).matcher(chartXML);
		if(matcherObj.find()){
			String tagString = "<"+repeatTagMain+matcherObj.group(1)+"</"+repeatTagMain+">";
			ArrayList<String> patternStrAL = new ArrayList<String>();
			ArrayList<String> patternStrColNameAL = new ArrayList<String>();
			Matcher valColMatcher = Pattern.compile("\\!\\@\\#(.*?)\\!\\@\\#",Pattern.DOTALL).matcher(tagString);
			
			while(valColMatcher.find()){
				patternStrAL.add(valColMatcher.group(1));
				patternStrColNameAL.add(getColumnName(valColMatcher.group(1), xAxisCol, yAxisCol, zAxisCol, seriesCol,ddKeyCol));
				if(!ValidationUtil.isValid(ddKeyCol)) {
					patternStrColNameAL.remove(ddKeyCol);
				}
			}
			
			rs.beforeFirst();
			StringBuffer formedXmlSB = new StringBuffer();
	    	while(rs.next()){
	    		if(patternStrColNameAL.size()==1){
		    		if(dataExistCheck.indexOf(","+rs.getObject(patternStrColNameAL.get(0))+",")==-1){
		    			String value = String.valueOf(rs.getObject(patternStrColNameAL.get(0)));
		    			formedXmlSB.append(tagString.replaceAll("\\!\\@\\#"+patternStrAL.get(0)+"\\!\\@\\#", ValidationUtil.isValid(value)?value:""));
			    		dataExistCheck.append(","+rs.getObject(patternStrColNameAL.get(0))+",");
		    		}
	    		}else if(patternStrColNameAL.size()>1){
	    			int arrListIndex = 0;
	    			String tempTagString = tagString;
	    			for(String colName:patternStrColNameAL){
	    				String value ="";
	    				try{
	    					value = String.valueOf(rs.getObject(colName));
	    				}catch(Exception e){
	    					exceptionCode.setErrorMsg(e.getCause().getMessage());
	    					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	    					if(!ValidationUtil.isValid(exceptionCode.getErrorMsg()))
	    						exceptionCode.setErrorMsg(e.getMessage());
							return exceptionCode;
	    				}
	    				tempTagString = tempTagString.replaceAll("\\!\\@\\#"+patternStrAL.get(arrListIndex)+"\\!\\@\\#", ValidationUtil.isValid(value)?value:"");
	    				arrListIndex++;
	    			}
	    			formedXmlSB.append(tempTagString);
	    		}
	    	}
	    	/* Update to return XML */
	    	Matcher returnMatcher = Pattern.compile("^(.*?)\\<"+repeatTagMain+"(.*?)\\<\\/"+repeatTagMain+"\\>(.*?)$",Pattern.DOTALL).matcher(returnChartXml);
	    	if(returnMatcher.find()){
	    		returnChartXml = returnMatcher.group(1)+formedXmlSB+returnMatcher.group(3);
	    	}
		}
		if(ValidationUtil.isValid(returnChartXml)) {
			exceptionCode.setResponse(returnChartXml);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("sucessful operation");
		}
		return exceptionCode;
	}
	public ExceptionCode updateReturnXmlForMultipleRepeatTagMultiY_NoSeries(String repeatTagMain, String xAxisCol, String yAxisCol, String zAxisCol, String seriesCol, String chartXML, ResultSet rs, StringBuffer dataExistCheck, CachedRowSet rsChild, String returnChartXml,String ddKeyCol) throws SQLException{
		ExceptionCode exceptionCode = new ExceptionCode();
		String[] repeatTagArr = repeatTagMain.split(",");
		try {
			if(repeatTagArr.length==2){
				Matcher matcherObj = Pattern.compile("\\<"+repeatTagArr[0]+"(.*?)\\<\\/"+repeatTagArr[0]+"\\>",Pattern.DOTALL).matcher(chartXML);
				if(matcherObj.find()){
					String fullTagString = "<"+repeatTagArr[0]+matcherObj.group(1)+"</"+repeatTagArr[0]+">";
					String parentTagStr = "";
					String childTagString = "";
					String parentReplaceString = "";
					String childReplaceString = "";
					StringBuffer formedXmlSB = new StringBuffer();
					
					/* Form parent tag String */
					Matcher parentTagMatcher = Pattern.compile("\\<"+repeatTagArr[0]+"(.*?)\\>",Pattern.DOTALL).matcher(fullTagString);
					if(parentTagMatcher.find()){
						parentTagStr = "<"+repeatTagArr[0]+parentTagMatcher.group(1)+">";
					}
					
					
					/* Get exact pattern from parent tag to be replaced with Y-Axis column name */
					String yAxisColArr[] = yAxisCol.split(",");
					Matcher replaceMatcher = Pattern.compile("\\!\\@\\#(.*?)\\!\\@\\#",Pattern.DOTALL).matcher(parentTagStr);
					if(replaceMatcher.find()){
						parentReplaceString = replaceMatcher.group(1);
					}
					
					/* Form child tag String */
					Matcher matcherChildObj = Pattern.compile("\\<"+repeatTagArr[1]+"(.*?)\\<\\/"+repeatTagArr[1]+"\\>",Pattern.DOTALL).matcher(fullTagString);
					if(matcherChildObj.find()){
						childTagString = "<"+repeatTagArr[1]+matcherChildObj.group(1)+"</"+repeatTagArr[1]+">";
					}
					
					replaceMatcher = Pattern.compile("\\!\\@\\#(.*?)\\!\\@\\#",Pattern.DOTALL).matcher(childTagString);
					if(replaceMatcher.find()){
						childReplaceString = replaceMatcher.group(1);
					}
					
					/* For every Y-Axis column Name from parent tag string [dataset] */
					for(String yCol:yAxisColArr){
						formedXmlSB.append(parentTagStr.replaceAll("\\!\\@\\#"+parentReplaceString+"\\!\\@\\#", yCol));
						
						rs.beforeFirst();
						while(rs.next()){
							formedXmlSB.append(childTagString.replaceAll("\\!\\@\\#"+childReplaceString+"\\!\\@\\#", String.valueOf(rs.getObject(yCol))));
						}
						formedXmlSB.append("</"+repeatTagArr[0]+">");
					}
					
			    	/* Update return XML */
			    	Matcher returnMatcher = Pattern.compile("^(.*?)\\<"+repeatTagArr[0]+"(.*?)\\<\\/"+repeatTagArr[0]+"\\>(.*?)$",Pattern.DOTALL).matcher(returnChartXml);
			    	if(returnMatcher.find()){
			    		returnChartXml = returnMatcher.group(1)+formedXmlSB+returnMatcher.group(3);
			    	}
				}
			}
			if(ValidationUtil.isValid(returnChartXml)) {
				exceptionCode.setResponse(returnChartXml);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("sucessful operation");
			}
			return exceptionCode;
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
		
		
	}
	public String getColumnName(String pattern, String xAxisCol, String yAxisCol, String zAxisCol, String seriesCol,String ddKeyCol){
		switch(pattern){
			case "X_AXIS":
				return xAxisCol;
			case "Y_AXIS":
				return yAxisCol;
			case "Z-AXIS":
				return zAxisCol;
			case "SERIES_AXIS":
				return seriesCol;
			case "DRILLDOWN_KEY":
				return ddKeyCol;
			default:
				return ".";
		}
	}
	public ExceptionCode updateReturnXmlForSingleRepeatTag_OnlyMeasure(String repeatTagMain, String chartXML, String xAxisCol, String yAxisCol, String zAxisCol, String seriesCol, String returnChartXml, ResultSet rs, StringBuffer dataExistCheck,String ddKeyCol) throws SQLException{
		ExceptionCode exceptionCode = new ExceptionCode();
		Matcher matcherObj = Pattern.compile("\\<"+repeatTagMain+"(.*?)\\<\\/"+repeatTagMain+"\\>",Pattern.DOTALL).matcher(chartXML);
		if(matcherObj.find()){
			String tagString = "<"+repeatTagMain+matcherObj.group(1)+"</"+repeatTagMain+">";
			String replaceTagString = "";
			Matcher valColMatcher = Pattern.compile("\\!\\@\\#(.*?)\\!\\@\\#",Pattern.DOTALL).matcher(tagString);
			while(valColMatcher.find()){
				replaceTagString = "Y_AXIS".equalsIgnoreCase(valColMatcher.group(1))?replaceTagString:valColMatcher.group(1);
			}
			rs.beforeFirst();
			StringBuffer formedXmlSB = new StringBuffer();
			while(rs.next()){
				String columnArr[] = yAxisCol.split(",");
				for(String columnName:columnArr){
					String value ="";
					try{
						value = String.valueOf(rs.getObject(columnName));
					}catch(Exception e){
						e.printStackTrace();
    					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
    					exceptionCode.setErrorMsg(e.getMessage());
						return exceptionCode;
					}
					value = tagString.replaceAll("\\!\\@\\#"+replaceTagString+"\\!\\@\\#", columnName).replaceAll("\\!\\@\\#Y_AXIS\\!\\@\\#", ValidationUtil.isValid(value)?value:"");
					if(formedXmlSB.indexOf(value)==-1)
						formedXmlSB.append(value);
				}
			}
	    	/* Update to return XML */
	    	Matcher returnMatcher = Pattern.compile("^(.*?)\\<"+repeatTagMain+"(.*?)\\<\\/"+repeatTagMain+"\\>(.*?)$",Pattern.DOTALL).matcher(returnChartXml);
	    	if(returnMatcher.find()){
	    		returnChartXml = returnMatcher.group(1)+formedXmlSB+returnMatcher.group(3);
	    	}
		}
		if(ValidationUtil.isValid(returnChartXml)) {
			exceptionCode.setResponse(returnChartXml);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}
		return exceptionCode;
	}
	public ExceptionCode updateReturnXmlForMultipleRepeatTagMultiY(String repeatTagMain, String xAxisCol, String yAxisCol, String zAxisCol, String seriesCol, String chartXML, ResultSet rs, StringBuffer dataExistCheck, CachedRowSet rsChild, String returnChartXml,String ddKeyCol) throws SQLException{
		ExceptionCode exceptionCode =new ExceptionCode();
		try {
			String[] repeatTagArr = repeatTagMain.split(",");
			if(repeatTagArr.length==2){
				Matcher matcherObj = Pattern.compile("\\<"+repeatTagArr[0]+"(.*?)\\<\\/"+repeatTagArr[0]+"\\>",Pattern.DOTALL).matcher(chartXML);
				if(matcherObj.find()){
					String fullTagString = "<"+repeatTagArr[0]+matcherObj.group(1)+"</"+repeatTagArr[0]+">";
					String parentTagStr = "";
					String colNameForParent = "";
					Matcher parentTagMatcher = Pattern.compile("\\<"+repeatTagArr[0]+"(.*?)\\>",Pattern.DOTALL).matcher(fullTagString);
					if(parentTagMatcher.find()){
						parentTagStr = "<"+repeatTagArr[0]+parentTagMatcher.group(1)+">";
					}
					Matcher valColMatcher = Pattern.compile("\\!\\@\\#(.*?)\\!\\@\\#",Pattern.DOTALL).matcher(parentTagStr);
					if(valColMatcher.find()){
						colNameForParent = getColumnName(valColMatcher.group(1), xAxisCol, yAxisCol, zAxisCol, seriesCol,ddKeyCol);
					}
					rs.beforeFirst();
					StringBuffer formedXmlSB = new StringBuffer();
					while(rs.next()){
			    		if(dataExistCheck.indexOf(","+rs.getObject(colNameForParent)+",")==-1){
			    			String value = String.valueOf(rs.getObject(colNameForParent));
			    			formedXmlSB.append(parentTagStr.replaceAll("\\!\\@\\#"+valColMatcher.group(1)+"\\!\\@\\#", ValidationUtil.isValid(value)?value:"" ));
			    			
			    			Matcher matcherChildObj = Pattern.compile("\\<"+repeatTagArr[1]+"(.*?)\\<\\/"+repeatTagArr[1]+"\\>",Pattern.DOTALL).matcher(fullTagString);
							if(matcherChildObj.find()){
								String childTagString = "<"+repeatTagArr[1]+matcherChildObj.group(1)+"</"+repeatTagArr[1]+">";
								
								ArrayList<String> patternStrAL = new ArrayList<String>();
								ArrayList<String> patternStrColNameAL = new ArrayList<String>();
								Matcher valColMatcherChild = Pattern.compile("\\!\\@\\#(.*?)\\!\\@\\#",Pattern.DOTALL).matcher(childTagString);
								
								while(valColMatcherChild.find()){
									patternStrAL.add(valColMatcherChild.group(1));
									patternStrColNameAL.add(getColumnName(valColMatcherChild.group(1), xAxisCol, yAxisCol, zAxisCol, seriesCol,ddKeyCol));
								}
								for(String pattern:patternStrAL){
									rsChild.beforeFirst();
									while(rsChild.next()){
										if(String.valueOf(rsChild.getObject(colNameForParent)).equalsIgnoreCase(String.valueOf(rs.getObject(colNameForParent))) ){
											for(String colName:patternStrColNameAL.get(0).split(",")){
												String valueChild = String.valueOf(rsChild.getObject(colName));
												formedXmlSB.append(childTagString.replaceAll("\\!\\@\\#"+pattern+"\\!\\@\\#", ValidationUtil.isValid(valueChild)?valueChild:""));
							    			}
										}
							    	}
								}
							}
							
							formedXmlSB.append("</"+repeatTagArr[0]+">");
				    		dataExistCheck.append(","+rs.getObject(colNameForParent)+",");
			    		}
			    	}
			    	/* Update return XML */
			    	Matcher returnMatcher = Pattern.compile("^(.*?)\\<"+repeatTagArr[0]+"(.*?)\\<\\/"+repeatTagArr[0]+"\\>(.*?)$",Pattern.DOTALL).matcher(returnChartXml);
			    	if(returnMatcher.find()){
			    		returnChartXml = returnMatcher.group(1)+formedXmlSB+returnMatcher.group(3);
			    	}
			    	if(ValidationUtil.isValid(returnChartXml)) {
						exceptionCode.setResponse(returnChartXml);
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					}
				}
			}
			return exceptionCode;
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
		
	}
	public ExceptionCode updateReturnXmlForMultipleRepeatTag(String repeatTagMain, String xAxisCol, String yAxisCol, String zAxisCol, String seriesCol, String chartXML, ResultSet rs, StringBuffer dataExistCheck, CachedRowSet rsChild, String returnChartXml,String ddKeyCol) throws SQLException{
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
		String[] repeatTagArr = repeatTagMain.split(",");
		if(repeatTagArr.length==2){
			Matcher matcherObj = Pattern.compile("\\<"+repeatTagArr[0]+"(.*?)\\<\\/"+repeatTagArr[0]+"\\>",Pattern.DOTALL).matcher(chartXML);
			if(matcherObj.find()){
				String fullTagString = "<"+repeatTagArr[0]+matcherObj.group(1)+"</"+repeatTagArr[0]+">";
				String parentTagStr = "";
				String colNameForParent = "";
				Matcher parentTagMatcher = Pattern.compile("\\<"+repeatTagArr[0]+"(.*?)\\>",Pattern.DOTALL).matcher(fullTagString);
				if(parentTagMatcher.find()){
					parentTagStr = "<"+repeatTagArr[0]+parentTagMatcher.group(1)+">";
				}
				Matcher valColMatcher = Pattern.compile("\\!\\@\\#(.*?)\\!\\@\\#",Pattern.DOTALL).matcher(parentTagStr);
				if(valColMatcher.find()){
					colNameForParent = getColumnName(valColMatcher.group(1), xAxisCol, yAxisCol, zAxisCol, seriesCol,ddKeyCol);
				}
				rs.beforeFirst();
				StringBuffer formedXmlSB = new StringBuffer();
				while(rs.next()){
		    		if(dataExistCheck.indexOf(","+rs.getObject(colNameForParent)+",")==-1){
		    			String value = String.valueOf(rs.getObject(colNameForParent));
		    			formedXmlSB.append(parentTagStr.replaceAll("\\!\\@\\#"+valColMatcher.group(1)+"\\!\\@\\#", ValidationUtil.isValid(value)?value:"" ));
		    			
		    			Matcher matcherChildObj = Pattern.compile("\\<"+repeatTagArr[1]+"(.*?)\\<\\/"+repeatTagArr[1]+"\\>",Pattern.DOTALL).matcher(fullTagString);
						if(matcherChildObj.find()){
							String childTagString = "<"+repeatTagArr[1]+matcherChildObj.group(1)+"</"+repeatTagArr[1]+">";
							
							ArrayList<String> patternStrAL = new ArrayList<String>();
							ArrayList<String> patternStrColNameAL = new ArrayList<String>();
							Matcher valColMatcherChild = Pattern.compile("\\!\\@\\#(.*?)\\!\\@\\#",Pattern.DOTALL).matcher(childTagString);
							
							while(valColMatcherChild.find()){
								patternStrAL.add(valColMatcherChild.group(1));
								patternStrColNameAL.add(getColumnName(valColMatcherChild.group(1), xAxisCol, yAxisCol, zAxisCol, seriesCol,ddKeyCol));
							}
							
							rsChild.beforeFirst();
							while(rsChild.next()){
								String tempTagString = childTagString;
								if(String.valueOf(rsChild.getObject(colNameForParent)).equalsIgnoreCase(String.valueOf(rs.getObject(colNameForParent))) ){
									int arrListIndex = 0;
									for(String colName:patternStrColNameAL){
										String valueChild = String.valueOf(rsChild.getObject(colName));
					    				tempTagString = tempTagString.replaceAll("\\!\\@\\#"+patternStrAL.get(arrListIndex)+"\\!\\@\\#", ValidationUtil.isValid(valueChild)?valueChild:"");
					    				arrListIndex++;
					    			}
									formedXmlSB.append(tempTagString);
								}
					    	}
						}
						
						formedXmlSB.append("</"+repeatTagArr[0]+">");
			    		dataExistCheck.append(","+rs.getObject(colNameForParent)+",");
		    		}
		    	}
		    	/* Update return XML */
		    	Matcher returnMatcher = Pattern.compile("^(.*?)\\<"+repeatTagArr[0]+"(.*?)\\<\\/"+repeatTagArr[0]+"\\>(.*?)$",Pattern.DOTALL).matcher(returnChartXml);
		    	if(returnMatcher.find()){
		    		returnChartXml = returnMatcher.group(1)+formedXmlSB+returnMatcher.group(3);
		    	}
			}
		}
		if(ValidationUtil.isValid(returnChartXml)) {
			exceptionCode.setResponse(returnChartXml);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("sucessful operation");
		}
		return exceptionCode;
		}catch(Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	public List<String> returnColorListBasedOnColorCount(int colorCount,String singleColor){
		Color randomColor = null;
		List<String> colorList = new ArrayList<String>();
		final int maxLimit = 255;
		colorList.add(singleColor);
		int difference = calculateDifferenceCountBase(colorCount);
		int breakMark = difference;
		if("ff0000".equalsIgnoreCase(singleColor)){
			do{
				randomColor = new Color(maxLimit, breakMark, breakMark);
				colorList.add(CommonUtils.rgb2Hex(randomColor));
				breakMark+=difference;
			}while(breakMark<maxLimit);
		}else if("00ff00".equalsIgnoreCase(singleColor)){
			do{
				randomColor = new Color(breakMark, maxLimit, breakMark);
				colorList.add(CommonUtils.rgb2Hex(randomColor));
				breakMark+=difference;
			}while(breakMark<maxLimit);
		}else if("0000ff".equalsIgnoreCase(singleColor)){
			do{
				randomColor = new Color(breakMark, breakMark, maxLimit);
				colorList.add(CommonUtils.rgb2Hex(randomColor));
				breakMark+=difference;
			}while(breakMark<maxLimit);
		}else{
			colorList = new ArrayList<String>();
			colorList.add("000000");
			do{
				randomColor = new Color(breakMark, breakMark, breakMark);
				colorList.add(CommonUtils.rgb2Hex(randomColor));
				breakMark+=difference;
			}while(breakMark<maxLimit);
		}
		return colorList;
	}
	public  int calculateDifferenceCountBase(int count){
		if(count<5)return 50; if(count<10)return 25; if(count<12)return 20; if(count<15)return 15;
		if(count<25)return 10; if(count<50)return 5; if(count<85)return 3; if(count<125)return 2;
		return  1;
	}
	
	public ExceptionCode getChartXML(String chartType, String  chartXml,ResultSet rs,CachedRowSet rsChild,String widgetTheme) {
		ExceptionCode exceptionCode = new ExceptionCode();
		boolean multiY_NoSeries = false;
		boolean onlyX_NoSeries = false;
		boolean onlyY_onlyMeasure = false;
		boolean onlyY_WithSeries = false;
		String xAxisCol = CommonUtils.getValueForXmlTag(chartXml, "X-AXIS");
		String yAxisCol = CommonUtils.getValueForXmlTag(chartXml, "Y-AXIS");
		if(!ValidationUtil.isValid(xAxisCol) || !ValidationUtil.isValid(yAxisCol)) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("X-Axis/Y-Axis value not maintained");
			return exceptionCode;
		}
		String zAxisCol = CommonUtils.getValueForXmlTag(chartXml, "Z-AXIS");
		String seriesCol = CommonUtils.getValueForXmlTag(chartXml, "SERIES");
		String measureProp = CommonUtils.getValueForXmlTag(chartXml, "MEASURE_PROP");
		String isCustomColor = CommonUtils.getValueForXmlTag(chartXml, "isCustomColor");
	    String isRadiantColor = CommonUtils.getValueForXmlTag(chartXml, "isRadiantColor");
		String enableColorPalette = CommonUtils.getValueForXmlTag(chartXml, "EnableColorPalette");
		String userDefinedColorCode = CommonUtils.getValueForXmlTag(chartXml, "ColorCode");
		String userDefinedPalette = CommonUtils.getValueForXmlTag(chartXml, "ColorPalette");
		String genricChartProperties = CommonUtils.getValueForXmlTag(chartXml,"GenericAttributes");
		String drillDownKey = CommonUtils.getValueForXmlTag(chartXml,"DRILLDOWN_KEY");
		try {
			if (ValidationUtil.isValid(xAxisCol) && !ValidationUtil.isValid(yAxisCol)
					&& !ValidationUtil.isValid(seriesCol))
				onlyX_NoSeries = true;
			else if (ValidationUtil.isValid(xAxisCol) && ValidationUtil.isValid(yAxisCol) && yAxisCol.indexOf(",") != -1
					&& !ValidationUtil.isValid(seriesCol))
				multiY_NoSeries = true;
			else if (!ValidationUtil.isValid(xAxisCol) && ValidationUtil.isValid(yAxisCol)
					&& yAxisCol.indexOf(",") != -1 && !ValidationUtil.isValid(seriesCol))
				onlyY_onlyMeasure = true;
			else if (!ValidationUtil.isValid(xAxisCol) && ValidationUtil.isValid(yAxisCol)
					&& yAxisCol.indexOf(",") != -1 && ValidationUtil.isValid(seriesCol))
				onlyY_WithSeries = true;

			String chartXmlPath = commonDao.findVisionVariableValue("PRD_CHARTXML_PATH");
			String chartXML="";
			if(!ValidationUtil.isValid(chartXmlPath)) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Chart Xml Path not maintained in Vision_Variables[PRD_CHARTXML_PATH]");
				return exceptionCode;
			}
			File file = new File(chartXmlPath+chartType+".xml");
			if(!file.exists()) {
	        	exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(chartXmlPath+chartType+ ".xml file not found!!");
				return exceptionCode;
		    }
			BufferedReader br = new BufferedReader(new FileReader(chartXmlPath+chartType+".xml"));
			StringBuilder chartXmlB = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	        	chartXmlB.append(line);
	        	chartXmlB.append("\n");
	            line = br.readLine();
	        }
	        br.close();
	        String chartMataDataXml = chartXmlB.toString();
			List<String> repeatTagList = new ArrayList<String>();
			Matcher matcherObj = Pattern.compile("\\<REPEATING_TAG\\>(.*?)\\<\\/REPEATING_TAG\\>", Pattern.DOTALL)
					.matcher(chartMataDataXml);
			while (matcherObj.find()) {
				String repeatTags = matcherObj.group(1).replaceAll("\n", "").replaceAll("\r", "")
						.replaceAll("\\s+", " ").trim();
				repeatTagList.add(repeatTags);
			}
			matcherObj = Pattern.compile("\\<chart(.*?)\\<\\/chart\\>", Pattern.DOTALL).matcher(chartMataDataXml);
			if (matcherObj.find()) {
	    		String tempChartXml = matcherObj.group(1);
	    		String postChartXml = tempChartXml.substring(tempChartXml.indexOf('>'),tempChartXml.length());
	    		tempChartXml = tempChartXml.substring(0, tempChartXml.indexOf('>'));
	    		if(ValidationUtil.isValid(genricChartProperties)){
		    		Matcher attributeMatcherObj = Pattern.compile("</(.*?)>",Pattern.DOTALL).matcher(genricChartProperties);
					while(attributeMatcherObj.find()){
						if(tempChartXml.indexOf(" "+attributeMatcherObj.group(1))==-1)
							if("exportFileName".equalsIgnoreCase(attributeMatcherObj.group(1))){
								String value = CommonUtils.getValueForXmlTag(genricChartProperties, attributeMatcherObj.group(1));
								if(!ValidationUtil.isValid(value)){
									value = ValidationUtil.isValid(CommonUtils.getValueForXmlTag(genricChartProperties, "caption"))
													? CommonUtils.getValueForXmlTag(genricChartProperties,"caption")
													: ValidationUtil.isValid(CommonUtils.getValueForXmlTag(genricChartProperties, "subcaption"))
																	? CommonUtils.getValueForXmlTag(genricChartProperties, "subcaption")
																	: "VisionCharts";
								}
								tempChartXml = tempChartXml + " exportFileName=\"" + value + "\"";
							}else
								tempChartXml = tempChartXml + " " + attributeMatcherObj.group(1) + "=\"" +CommonUtils.getValueForXmlTag(genricChartProperties, attributeMatcherObj.group(1)) + "\"";
					}
	    		}
				tempChartXml = tempChartXml + postChartXml;
	    		
	    		chartXML = "<chart"+tempChartXml+"</chart>";
	    	}
			String returnChartXml = chartXML;
			if (ValidationUtil.isValid(chartXML)) {
				for (String repeatTagMain : repeatTagList) {
					StringBuffer dataExistCheck = new StringBuffer();
					if (onlyX_NoSeries && repeatTagList.size() == 1) {
						exceptionCode = updateReturnXmlForSingleRepeatTag(repeatTagMain, chartXML, xAxisCol, yAxisCol,
								zAxisCol, seriesCol, returnChartXml, rsChild, dataExistCheck,drillDownKey);
					} else if (multiY_NoSeries && repeatTagList.size() == 2) {
						if (repeatTagMain.indexOf(",") == -1) {
							exceptionCode = updateReturnXmlForSingleRepeatTag(repeatTagMain, chartXML, xAxisCol,
									yAxisCol, zAxisCol, seriesCol, returnChartXml, rsChild, dataExistCheck,drillDownKey);
						} else {
							exceptionCode = updateReturnXmlForMultipleRepeatTagMultiY_NoSeries(repeatTagMain, xAxisCol,
									yAxisCol, zAxisCol, seriesCol, chartXML, rs, dataExistCheck, rsChild,
									returnChartXml,drillDownKey);
						}
					} else if (onlyY_onlyMeasure) {
						exceptionCode = updateReturnXmlForSingleRepeatTag_OnlyMeasure(repeatTagMain, chartXML,
								xAxisCol, yAxisCol, zAxisCol, seriesCol, returnChartXml, rsChild, dataExistCheck,drillDownKey);
					} else if (onlyY_WithSeries) {
						if (repeatTagMain.indexOf(",") == -1) {
							exceptionCode = updateReturnXmlForSingleRepeatTag_OnlyMeasure(repeatTagMain, chartXML,
									xAxisCol, yAxisCol, zAxisCol, seriesCol, returnChartXml, rsChild, dataExistCheck,drillDownKey);
						} else {
							exceptionCode = updateReturnXmlForMultipleRepeatTagMultiY(repeatTagMain, xAxisCol,
									yAxisCol, zAxisCol, seriesCol, chartXML, rs, dataExistCheck, rsChild,
									returnChartXml,drillDownKey);
						}
					} else {
						if (repeatTagMain.indexOf(",") == -1) {
							exceptionCode = updateReturnXmlForSingleRepeatTag(repeatTagMain, chartXML, xAxisCol,
									yAxisCol, zAxisCol, seriesCol, returnChartXml, rsChild, dataExistCheck,drillDownKey);
							
						} else {
							exceptionCode = updateReturnXmlForMultipleRepeatTag(repeatTagMain, xAxisCol, yAxisCol,
									zAxisCol, seriesCol, chartXML, rs, dataExistCheck, rsChild, returnChartXml,drillDownKey);
						}
					}
					if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
						returnChartXml = (String) exceptionCode.getResponse();
					else
						return exceptionCode;
					
				}
			}
			
			if (ValidationUtil.isValid(returnChartXml)) {
				returnChartXml = returnChartXml.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\\s+", " ");
			}
			List<String> colorAL = new ArrayList<String>();
			String systemPalette = "426FC0,59B697,F57E56,FFD700,DE7CB6,F8BB00,2B9C2B,ba40c3,b95c65,bac883,b96f13,bb5509,b9e5bb,bae653,ba36a1,ba5e94,bb425b,b965ef,ba8e7a,baddc8,b9048c,ba7242,b948b7,bafa01,ba989d,baf076,bb9a35,baca1b,b9abb3,baac4b,ba06bb,b9a090,bb69b6,babff7,b98b4b,bbde60,b9d10e,b90d17,ba7bcc,b9be61,b9789d,b92ae7,bb7c64,b9efde,b9ee47,ba8f12,b9215c,bab5d5,ba671f,b9b43e,baa228,b99706,b93e95,ba0524,ba494f,bb8687,b9bdc9,b86359,ba1a69,ba4be6,b94720,b9da99,bbade1,b98de2,ba0eae,b920c4,bb11dd,ba23f3,bb248b,ba0f46,b9966e,bae7ea,b982c0,b8a884,bad3a5,b902f4,bac18f,b9a91c,ba85ef,bab66d,ba2c7f,ba53d9,ba5471,bb3739,ba5dfc,b9c7eb,b96687,b9350a,bad43d,bbfc30,b97935,b93372,ba3f2d,ba2d17,baa3bf,bbcbb2,ba18d1,bafb98,b95143,ba8458,b9dc31,b9173a,b8edaf,ba70aa,b81f2d,bbf20e";
			if (ValidationUtil.isValid(userDefinedPalette)) {
				systemPalette = userDefinedPalette;
			}
			String color[] = systemPalette.split(",");
			colorAL = Arrays.asList(color[1].split(","));
			if (ValidationUtil.isValid(enableColorPalette) && "Y".equalsIgnoreCase(enableColorPalette)) {
				int colorReplaceIndex = 0;
				matcherObj = Pattern.compile("\\#COLOR\\_CODE\\#", Pattern.DOTALL).matcher(returnChartXml);
				while (matcherObj.find()) {
					colorReplaceIndex++;
				}
				String singleColor = "ba40c3";
				colorAL = returnColorListBasedOnColorCount(colorReplaceIndex, singleColor);
				colorReplaceIndex = 0;
				matcherObj = Pattern.compile("\\#COLOR\\_CODE\\#", Pattern.DOTALL).matcher(returnChartXml);
				while (matcherObj.find()) {
					try {
						returnChartXml = returnChartXml.replaceFirst("\\#COLOR\\_CODE\\#", color[colorReplaceIndex]);
					} catch (Exception e) {
						returnChartXml = returnChartXml.replaceFirst("\\#COLOR\\_CODE\\#", color[colorReplaceIndex]);
						colorReplaceIndex = 0;
					}
					colorReplaceIndex++;
				}
			} else {
				matcherObj = Pattern.compile("\\#COLOR\\_CODE\\#", Pattern.DOTALL).matcher(returnChartXml);
				while (matcherObj.find()) {
					try {
						returnChartXml = returnChartXml.replaceFirst("\\#COLOR\\_CODE\\#",
								ValidationUtil.isValid(userDefinedColorCode) ? userDefinedColorCode : color[0]);
					} catch (Exception e) {
						returnChartXml = returnChartXml.replaceFirst("\\#COLOR\\_CODE\\#",
								ValidationUtil.isValid(userDefinedColorCode) ? userDefinedColorCode : color[0]);
					}
				}
			}
			String temp = "ddkey=";
			String temp1 = "!@#DRILLDOWN_KEY!@#";
			temp = temp+"\""+temp1+"\"";
			if(returnChartXml.contains(temp))
				returnChartXml=returnChartXml.replaceAll(temp, "");
			if(ValidationUtil.isValid(returnChartXml)) {
				exceptionCode.setResponse(returnChartXml);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				return exceptionCode;
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("error while getting chartxml");
				return exceptionCode;
			}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Exception while getting the Chart xml");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
}
