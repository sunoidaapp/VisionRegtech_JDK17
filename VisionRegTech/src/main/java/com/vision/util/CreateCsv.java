package com.vision.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vision.exception.ExceptionCode;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.ReportStgVb;
import com.vision.vb.ReportsWriterVb;


public class CreateCsv {
	public static final String CAP_COL = "captionColumn";
	public static final String DATA_COL = "dataColumn";
	public ExceptionCode exportDataToCSV(List<ColumnHeadersVb> columnHeaders, 
			List<ReportStgVb> reportsStgs, ReportsWriterVb reportWriterVb, int currentUserId) throws IOException{
		int colNum, rowNum = colNum =0;
		rowNum = 6;
		String csvSeparator = ",";
		String lineSeparator = "\n";
		int headerCnt  = 0;
		NumberFormat fromat = new DecimalFormat("0000000000000000.000000");
		String filePath = System.getProperty("java.io.tmpdir");
		if(!ValidationUtil.isValid(filePath)){
			filePath = System.getenv("TMP");
		}
		if(ValidationUtil.isValid(filePath)){
			filePath = filePath + File.separator;
		}
		FileWriter fw = new FileWriter(filePath+ValidationUtil.encode(reportWriterVb.getReportTitle())+"_"+currentUserId+".csv");
		PrintWriter out = new PrintWriter(fw);
		if(reportWriterVb.getLabelRowCount() >= 2){
			//Where Grouped Columns to be displayed.
			Map<String, List<String>> groupCols = new LinkedHashMap<String, List<String>>();
			rowNum++;
			for(ColumnHeadersVb columnHeadersVb1:columnHeaders){
				if((columnHeadersVb1.getLabelColNum() - reportWriterVb.getLabelColCount()) >0 &&  columnHeadersVb1.getLabelRowNum() <=1){
					//These are dataColumns which needs to be grouped.
					groupCols.put(columnHeadersVb1.getCaption(),new ArrayList<String>(5));
				}else if(columnHeadersVb1.getLabelRowNum() <=1){
					out.print(columnHeadersVb1.getCaption());
					out.print(csvSeparator);
					++colNum;
				}else{
					//Need to add these columns to the groups added above.
					for(String colGrp11: groupCols.keySet()){
						groupCols.get(colGrp11).add(columnHeadersVb1.getCaption());
					}
				}
			}
			for(String colGrp11: groupCols.keySet()){
				for(String colCap:groupCols.get(colGrp11)){
					out.print(colGrp11 +" "+ colCap);
					out.print(csvSeparator);
					++colNum;
				}
			}
		}else{
			for(ColumnHeadersVb columnHeadersVb:columnHeaders){
				out.print(columnHeadersVb.getCaption());
				out.print(csvSeparator);
				++colNum;
			}
		}
		headerCnt = colNum; 
		//Add data now.
		++rowNum;
		colNum = 0;
		for(ReportStgVb reportStgVb:reportsStgs){
			out.print(lineSeparator);
			for(int loopCount =0; loopCount < headerCnt; loopCount++){
				int index = 0; 
				String type = (reportWriterVb.getLabelColCount() - (loopCount+1)) >= 0 ? CAP_COL : DATA_COL;
				if(type.equalsIgnoreCase(CAP_COL)){
					index = (loopCount+1);
					out.print(findValue(reportStgVb, type, index));
					out.print(csvSeparator);
				}else{
					index = ((loopCount+1) - reportWriterVb.getLabelColCount());
					String value = findValue(reportStgVb, type, index);
					value = value.replaceAll(",", "");
					out.print(value);
					/*if(ValidationUtil.isValid(value)){
						if(ValidationUtil.isNumericDecimal(value)){
							
						}else{
							out.print(value);
						}
					}else{
						out.print("");
					}*/
					out.print(csvSeparator);
				}
			}
			rowNum++;
		}
		out.flush();
		out.close();
		fw.close();
		return CommonUtils.getResultObject("", 1, "", "");
	}
	
	private String findValue(ReportStgVb reportsStgs, String type, int index){
		if(CAP_COL.equalsIgnoreCase(type)){
			switch(index){
				case 1: return reportsStgs.getCaptionColumn1() == null ? "" : reportsStgs.getCaptionColumn1().replaceAll(",", "");
				case 2: return reportsStgs.getCaptionColumn2() == null ? "" : reportsStgs.getCaptionColumn2().replaceAll(",", "");
				case 3: return reportsStgs.getCaptionColumn3() == null ? "" : reportsStgs.getCaptionColumn3().replaceAll(",", "");
				case 4: return reportsStgs.getCaptionColumn4() == null ? "" : reportsStgs.getCaptionColumn4().replaceAll(",", "");
				case 5: return reportsStgs.getCaptionColumn5() == null ? "" : reportsStgs.getCaptionColumn5().replaceAll(",", "");
			}
		}else if (DATA_COL.equalsIgnoreCase(type)){
			switch(index){
				case 1 : return reportsStgs.getDataColumn1()  == null? "": reportsStgs.getDataColumn1() ;
				case 2 : return reportsStgs.getDataColumn2()  == null? "": reportsStgs.getDataColumn2() ;
				case 3 : return reportsStgs.getDataColumn3()  == null? "": reportsStgs.getDataColumn3() ;
				case 4 : return reportsStgs.getDataColumn4()  == null? "": reportsStgs.getDataColumn4() ;
				case 5 : return reportsStgs.getDataColumn5()  == null? "": reportsStgs.getDataColumn5() ;
				case 6 : return reportsStgs.getDataColumn6()  == null? "": reportsStgs.getDataColumn6() ;
				case 7 : return reportsStgs.getDataColumn7()  == null? "": reportsStgs.getDataColumn7() ;
				case 8 : return reportsStgs.getDataColumn8()  == null? "": reportsStgs.getDataColumn8() ;
				case 9 : return reportsStgs.getDataColumn9()  == null? "": reportsStgs.getDataColumn9() ;
				case 10: return reportsStgs.getDataColumn10() == null? "": reportsStgs.getDataColumn10(); 
				case 11: return reportsStgs.getDataColumn11() == null? "": reportsStgs.getDataColumn11(); 
				case 12: return reportsStgs.getDataColumn12() == null? "": reportsStgs.getDataColumn12(); 
				case 13: return reportsStgs.getDataColumn13() == null? "": reportsStgs.getDataColumn13(); 
				case 14: return reportsStgs.getDataColumn14() == null? "": reportsStgs.getDataColumn14(); 
				case 15: return reportsStgs.getDataColumn15() == null? "": reportsStgs.getDataColumn15(); 
				case 16: return reportsStgs.getDataColumn16() == null? "": reportsStgs.getDataColumn16(); 
				case 17: return reportsStgs.getDataColumn17() == null? "": reportsStgs.getDataColumn17(); 
				case 18: return reportsStgs.getDataColumn18() == null? "": reportsStgs.getDataColumn18(); 
				case 19: return reportsStgs.getDataColumn19() == null? "": reportsStgs.getDataColumn19(); 
				case 20: return reportsStgs.getDataColumn20() == null? "": reportsStgs.getDataColumn20(); 
				case 21: return reportsStgs.getDataColumn21() == null? "": reportsStgs.getDataColumn21(); 
				case 22: return reportsStgs.getDataColumn22() == null? "": reportsStgs.getDataColumn22(); 
				case 23: return reportsStgs.getDataColumn23() == null? "": reportsStgs.getDataColumn23(); 
				case 24: return reportsStgs.getDataColumn24() == null? "": reportsStgs.getDataColumn24(); 
				case 25: return reportsStgs.getDataColumn25() == null? "": reportsStgs.getDataColumn25(); 
				case 26: return reportsStgs.getDataColumn26() == null? "": reportsStgs.getDataColumn26(); 
				case 27: return reportsStgs.getDataColumn27() == null? "": reportsStgs.getDataColumn27(); 
				case 28: return reportsStgs.getDataColumn28() == null? "": reportsStgs.getDataColumn28(); 
				case 29: return reportsStgs.getDataColumn29() == null? "": reportsStgs.getDataColumn29(); 
				case 30: return reportsStgs.getDataColumn30() == null? "": reportsStgs.getDataColumn30(); 
				case 31: return reportsStgs.getDataColumn31() == null? "": reportsStgs.getDataColumn31(); 
				case 32: return reportsStgs.getDataColumn32() == null? "": reportsStgs.getDataColumn32(); 
				case 33: return reportsStgs.getDataColumn33() == null? "": reportsStgs.getDataColumn33(); 
				case 34: return reportsStgs.getDataColumn34() == null? "": reportsStgs.getDataColumn34(); 
				case 35: return reportsStgs.getDataColumn35() == null? "": reportsStgs.getDataColumn35(); 
				case 36: return reportsStgs.getDataColumn36() == null? "": reportsStgs.getDataColumn36(); 
				case 37: return reportsStgs.getDataColumn37() == null? "": reportsStgs.getDataColumn37(); 
				case 38: return reportsStgs.getDataColumn38() == null? "": reportsStgs.getDataColumn38(); 
				case 39: return reportsStgs.getDataColumn39() == null? "": reportsStgs.getDataColumn39(); 
				case 40: return reportsStgs.getDataColumn40() == null? "": reportsStgs.getDataColumn40();
			}
		}
		return "";
	}
	
	
	
	public void writeDataToCSV(String xmlData, PrintWriter out, String csvSeparator, String lineSeparator, List<String> colTyps ){
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			NumberFormat fromat = new DecimalFormat("0000000000000000.000000");
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			//InputStream is = new ByteArrayInputStream(xmlData.getBytes()); 
			InputStream is = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
			//parse using builder to get DOM representation of the XML file
			org.w3c.dom.Document dom = db.parse(is);
			org.w3c.dom.Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("tableRow");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {
					Element el = (Element)nl.item(i);
					NodeList ncl  = el.getChildNodes();
					for(int loopCount=0;loopCount<ncl.getLength();loopCount++){
						Element el1= (Element)ncl.item(loopCount);
						if("formatType".equalsIgnoreCase(el1.getNodeName())){
							continue;
						}
						if(!"N".equalsIgnoreCase(colTyps.get(loopCount))){
							if(el1.getFirstChild()!=null){
								if(el1.getFirstChild().getNodeValue() != null && el1.getFirstChild().getNodeValue().contains(",")){
									out.print("\""+el1.getFirstChild().getNodeValue()+"\"");
								}else{
									out.print(el1.getFirstChild().getNodeValue());
								}
								if(ncl.getLength() != (loopCount+1))
								   out.print(csvSeparator);
							}else{
								out.print("");
								if(ncl.getLength() != (loopCount+1))
								    out.print(csvSeparator);
							}
						}else{
							String value = "";
							if(el1.getFirstChild()!=null){
								value = el1.getFirstChild().getNodeValue();
								value = value.replaceAll(",", "");
								if(!value.isEmpty() || !value.equalsIgnoreCase(" ")){
									Double value1 = Double.parseDouble(value);
									out.print(fromat.format(value1));
									if(ncl.getLength() != (loopCount+1))
									   out.print(csvSeparator);
								}
							}else{
								out.print("");
								if(ncl.getLength() != (loopCount+1))
								    out.print(csvSeparator);
							}
						}
					}
					out.print(lineSeparator);
				}
			}
		} catch (TransformerFactoryConfigurationError e) {
			System.out.println("catch - TransformerFactoryConfigurationError  : "+e);
			e.printStackTrace();
		}catch(ParserConfigurationException pce) {
			System.out.println("catch - ParserConfigurationException  : "+pce);
			pce.printStackTrace();
		}catch(SAXException se) {
			System.out.println("catch - SAXException  : "+se);
			se.printStackTrace();
		}catch(IOException ioe) {
			System.out.println("catch - IOException  : "+ioe);
			ioe.printStackTrace();
		}
	}
	public List<ColumnHeadersVb> sortColumnHeaders(List<ColumnHeadersVb> columnHeaders){
		List<ColumnHeadersVb> columnHeadersTemp = new ArrayList<ColumnHeadersVb>();
		for(int loopCount = 0; loopCount < columnHeaders.size(); loopCount++){
			for(ColumnHeadersVb columnHeadersVb1 : columnHeaders){
				if(columnHeadersVb1.getLabelColNum() == (loopCount+1) /*&& columnHeadersVb1.getColSpanNum() == 0*/)
					columnHeadersTemp.add(columnHeadersVb1);
			}
		}
		return columnHeadersTemp;
	}
	public ExceptionCode exportDataToCSV1(List<ColumnHeadersVb> columnHeaders, 
			List<ReportStgVb> reportsStgs, ReportsWriterVb reportWriterVb, int currentUserId , String burstSequenceNo, String scheduleSequenceNo) throws IOException{
		int colNum, rowNum = colNum =0;
		rowNum = 6;
		String csvSeparator = ",";
		String lineSeparator = "\n";
		int headerCnt  = 0;
		NumberFormat fromat = new DecimalFormat("0000000000000000.000000");
		String filePath = System.getProperty("java.io.tmpdir");
		if(!ValidationUtil.isValid(filePath)){
			filePath = System.getenv("TMP");
		}
		if(ValidationUtil.isValid(filePath)){
			filePath = filePath + File.separator;
		}
		FileWriter fw = null; //new FileWriter(filePath+ValidationUtil.encode(reportWriterVb.getReportTitle())+"_"+currentUserId+".csv");
		
		if(ValidationUtil.isValid(burstSequenceNo) && ValidationUtil.isValid(scheduleSequenceNo)){
			fw =  new FileWriter(filePath+ValidationUtil.encode(reportWriterVb.getReportTitle())+"_"+currentUserId+"_"+burstSequenceNo+"_"+scheduleSequenceNo+".csv");
		}else{
			fw = new FileWriter(filePath+ValidationUtil.encode(reportWriterVb.getReportTitle())+"_"+currentUserId+".csv");
		}		
		
		PrintWriter out = new PrintWriter(fw);
		if(reportWriterVb.getLabelRowCount() >= 2){
			//Where Grouped Columns to be displayed.
			List<ColumnHeadersVb> columnHeadersTemp = sortColumnHeaders(columnHeaders);
			rowNum++;
			for(int loopCount = 0; loopCount < columnHeadersTemp.size(); loopCount++){
				ColumnHeadersVb columnHeadersVb1 = columnHeadersTemp.get(loopCount);
				if(columnHeadersVb1.getCaption().contains("<br/>")){
					String value=columnHeadersVb1.getCaption().replaceAll("<br/>"," ");
					columnHeadersVb1.setCaption(value);
				}
				out.print(columnHeadersVb1.getCaption());
				out.print(csvSeparator);
				++colNum;					
			}
		}else{
			for(ColumnHeadersVb columnHeadersVb:columnHeaders){
				if(columnHeadersVb.getCaption().contains("<br/>")){
					String value=columnHeadersVb.getCaption().replaceAll("<br/>"," ");
					columnHeadersVb.setCaption(value);
				}
				out.print(columnHeadersVb.getCaption());
				out.print(csvSeparator);
				++colNum;
			}
		}
		headerCnt = colNum; 
		//Add data now.
		++rowNum;
		colNum = 0;
		for(ReportStgVb reportStgVb:reportsStgs){
			out.print(lineSeparator);
			for(int loopCount =0; loopCount < reportWriterVb.getLabelColCount(); loopCount++){
				int index = 0; 
				String type = ""; //(reportWriterVb.getLabelColCount() - (loopCount+1)) >= 0 ? CAP_COL : DATA_COL;
				if(loopCount <reportWriterVb.getCaptionLabelColCount())
					type = CAP_COL;
				else
					type = DATA_COL;
				
				if(type.equalsIgnoreCase(CAP_COL)){
					index = (loopCount+1);
					out.print(findValue(reportStgVb, type, index));
					out.print(csvSeparator);
				}else{
					index = ((loopCount+1) - reportWriterVb.getCaptionLabelColCount());
					String value = findValue(reportStgVb, type, index);
					value = value.replaceAll(",", "");
					out.print(value);
					/*if(ValidationUtil.isValid(value)){
						if(ValidationUtil.isNumericDecimal(value)){
							
						}else{
							out.print(value);
						}
					}else{
						out.print("");
					}*/
					out.print(csvSeparator);
				}
			}
			rowNum++;
		}
		out.flush();
		out.close();
		fw.close();
		return CommonUtils.getResultObject("", 1, "", "");
	}	
	public ExceptionCode exportDataToCSVOld(List<ColumnHeadersVb> columnHeaders, 
			List<ReportStgVb> reportsStgs, ReportsWriterVb reportWriterVb, int currentUserId) throws IOException{
		int colNum, rowNum = colNum =0;
		rowNum = 6;
		String csvSeparator = ",";
		String lineSeparator = "\n";
		int headerCnt  = 0;
		NumberFormat fromat = new DecimalFormat("0000000000000000.000000");
		String filePath = System.getProperty("java.io.tmpdir");
		if(!ValidationUtil.isValid(filePath)){
			filePath = System.getenv("TMP");
		}
		if(ValidationUtil.isValid(filePath)){
			filePath = filePath + File.separator;
		}
		FileWriter fw = new FileWriter(filePath+ValidationUtil.encode(reportWriterVb.getReportTitle())+"_"+currentUserId+".csv");
		PrintWriter out = new PrintWriter(fw);
		if(reportWriterVb.getLabelRowCount() >= 2){
			//Where Grouped Columns to be displayed.
			Map<String, List<String>> groupCols = new LinkedHashMap<String, List<String>>();
			rowNum++;
			for(ColumnHeadersVb columnHeadersVb1:columnHeaders){
				if((columnHeadersVb1.getLabelColNum() - reportWriterVb.getLabelColCount()) >0 &&  columnHeadersVb1.getLabelRowNum() <=1){
					//These are dataColumns which needs to be grouped.
					groupCols.put(columnHeadersVb1.getCaption(),new ArrayList<String>(5));
				}else if(columnHeadersVb1.getLabelRowNum() <=1){
					out.print(columnHeadersVb1.getCaption());
					out.print(csvSeparator);
					++colNum;
				}else{
					//Need to add these columns to the groups added above.
					for(String colGrp11: groupCols.keySet()){
						groupCols.get(colGrp11).add(columnHeadersVb1.getCaption());
					}
				}
			}
			for(String colGrp11: groupCols.keySet()){
				for(String colCap:groupCols.get(colGrp11)){
					out.print(colGrp11 +" "+ colCap);
					out.print(csvSeparator);
					++colNum;
				}
			}
		}else{
			for(ColumnHeadersVb columnHeadersVb:columnHeaders){
				out.print(columnHeadersVb.getCaption());
				out.print(csvSeparator);
				++colNum;
			}
		}
		headerCnt = colNum; 
		//Add data now.
		++rowNum;
		colNum = 0;
		for(ReportStgVb reportStgVb:reportsStgs){
			out.print(lineSeparator);
			for(int loopCount =0; loopCount < headerCnt; loopCount++){
				int index = 0; 
				String type = (reportWriterVb.getLabelColCount() - (loopCount+1)) >= 0 ? CAP_COL : DATA_COL;
				if(type.equalsIgnoreCase(CAP_COL)){
					index = (loopCount+1);
					out.print(findValue(reportStgVb, type, index));
					out.print(csvSeparator);
				}else{
					index = ((loopCount+1) - reportWriterVb.getLabelColCount());
					String value = findValue(reportStgVb, type, index);
					value = value.replaceAll(",", "");
					out.print(value);
					/*if(ValidationUtil.isValid(value)){
						if(ValidationUtil.isNumericDecimal(value)){
							
						}else{
							out.print(value);
						}
					}else{
						out.print("");
					}*/
					out.print(csvSeparator);
				}
			}
			rowNum++;
		}
		out.flush();
		out.close();
		fw.close();
		return CommonUtils.getResultObject("", 1, "", "");
	}
}