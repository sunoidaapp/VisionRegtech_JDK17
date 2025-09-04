package com.vision.util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.NodeList;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.vision.authentication.SessionContextHolder;
import com.vision.dao.CommonDao;
import com.vision.dao.ReportsDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.PromptIdsVb;
import com.vision.vb.ReportStgVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.ReportsWriterVb;
import com.vision.vb.VisionUsersVb;
@Component
public class PDFExportUtil {
	public static final String CAP_COL = "captionColumn";
	public static final String DATA_COL = "dataColumn";
	public static Logger logger = LoggerFactory.getLogger(PDFExportUtil.class);
	@Autowired
	ReportsDao reportsDao;
	@Autowired
	CommonDao commonDao;
	private static float[] mergeArrays(float[] source1,float[] source2){
		float[] destination = new float[source1.length+source2.length];
		int i=0;
		for(i = 0;i<source1.length;i++){
			destination[i] = source1[i];
		}
		for(int j = 0;j<source2.length;j++){
			destination[j+i] = source2[j];
		}
		return destination;
	} 
	
	private static float getSumOfWidths(Map<Integer, Float> colSizes, int colStart, int size, int labelColCount) {
		float totalWidth = 0f;
		for(int i=0;i<labelColCount;i++){
			totalWidth += colSizes.get(i);
		}
		for(int i=colStart;i<size;i++){
			totalWidth += colSizes.get(i);
		}
		return totalWidth;
	}
	private static PdfPTable createTable(PdfPTable sourceTable,List<PdfPRow> pdfpRows, int colStart, int colEnd, int currentPageCount, 
			int totalPages, int labelColCount) {
		// below comment by Praksah 
		//PdfPTable tempTable = new PdfPTable((colEnd-colStart)+labelColCount);
		PdfPTable tempTable = new PdfPTable(labelColCount);
		tempTable.setWidthPercentage(99);
		tempTable.setHeaderRows(sourceTable.getHeaderRows());
		tempTable.setSpacingAfter(10);
		tempTable.setSpacingBefore(10);
		tempTable.setSplitRows(false);
		tempTable.setSplitLate(false);
		//tempTable.getDefaultCell().setBackgroundColor(new BaseColor(79, 98, 40));
		tempTable.getDefaultCell().setBackgroundColor(new BaseColor(0, 92, 140));
		if(currentPageCount != totalPages){
			//Write the headers for other than 1st pages.
			List<PdfPRow> pdfpHRows = sourceTable.getRows(0, sourceTable.getHeaderRows());
			for(int loopCount=0;loopCount <pdfpHRows.size(); loopCount++){
				PdfPRow pdfpRow = pdfpHRows.get(loopCount);
				PdfPCell[] cells = pdfpRow.getCells();
				for(int j=0;j<labelColCount;j++){
					if(cells[j]!=null){
						tempTable.addCell(cells[j]);
					}
				}
				/*for(int j=colStart;j<colEnd;j++){
					if(cells[j]!=null)
						tempTable.addCell(cells[j]);
					else if(loopCount == 0  && colStart >1 && pdfpHRows.size() > 1){
						PdfPCell cell = new PdfPCell();
						cell.setBackgroundColor(new BaseColor(79, 98, 40));
						cell.setBorder(0|0&1|1);
						cell.setUseVariableBorders(true);
						cell.setBorderWidthTop(1);
						cell.setBorderWidthBottom(0.5f);
						cell.setNoWrap(true);
						tempTable.addCell(cell);
					}
				}*/
			}
			tempTable.completeRow();
		}
		for(PdfPRow pdfpRow :pdfpRows){
			PdfPCell[] cells = pdfpRow.getCells();
			for(int j=0;j<labelColCount;j++){
				if(cells[j]!=null)
					tempTable.addCell(cells[j]);
			}
			/*for(int j=colStart;j<colEnd;j++){
				if(cells[j]!=null)
					tempTable.addCell(cells[j]);
			}*/
			tempTable.completeRow();
		}
		return tempTable;
	}
	private static PdfPTable createTableOld(PdfPTable sourceTable,List<PdfPRow> pdfpRows, int colStart, int colEnd, int currentPageCount, 
			int totalPages, int labelColCount) {
		PdfPTable tempTable = new PdfPTable((colEnd-colStart)+labelColCount);
		tempTable.setWidthPercentage(99);
		tempTable.setHeaderRows(sourceTable.getHeaderRows());
		tempTable.setSpacingAfter(10);
		tempTable.setSpacingBefore(10);
		tempTable.setSplitRows(false);
		tempTable.setSplitLate(false);
		//tempTable.getDefaultCell().setBackgroundColor(new BaseColor(79, 98, 40));
		tempTable.getDefaultCell().setBackgroundColor(new BaseColor(0, 92, 140));
		
		if(currentPageCount != totalPages){
			//Write the headers for other than 1st pages.
			List<PdfPRow> pdfpHRows = sourceTable.getRows(0, sourceTable.getHeaderRows());
			for(int loopCount=0;loopCount <pdfpHRows.size(); loopCount++){
				PdfPRow pdfpRow = pdfpHRows.get(loopCount);
				PdfPCell[] cells = pdfpRow.getCells();
				for(int j=0;j<labelColCount;j++){
					if(cells[j]!=null)
						tempTable.addCell(cells[j]);
				}
				for(int j=colStart;j<colEnd;j++){
					if(cells[j]!=null)
						tempTable.addCell(cells[j]);
					else if(loopCount == 0  && colStart >1 && pdfpHRows.size() > 1){
						PdfPCell cell = new PdfPCell();
						cell.setBackgroundColor(new BaseColor(0, 92, 140));
						cell.setBorder(0|0&1|1);
						cell.setUseVariableBorders(true);
						cell.setBorderWidthTop(1);
						cell.setBorderWidthBottom(0.5f);
						cell.setNoWrap(true);
						tempTable.addCell(cell);
					}
				}
			}
			tempTable.completeRow();
		}
		for(PdfPRow pdfpRow :pdfpRows){
			PdfPCell[] cells = pdfpRow.getCells();
			for(int j=0;j<labelColCount;j++){
				if(cells[j]!=null)
					tempTable.addCell(cells[j]);
			}
			for(int j=colStart;j<colEnd;j++){
				if(cells[j]!=null)
					tempTable.addCell(cells[j]);
			}
			tempTable.completeRow();
		}
		return tempTable;
	}
	private static int getRowsPerPage(PdfPTable table, int rowStart, float pageHeight) {
		float totalH = rowStart != 0 ? table.getHeaderHeight(): 0f;
		for(int loopCount = rowStart; loopCount < table.getRows().size(); loopCount++){
			totalH = totalH + table.getRowHeight(loopCount);
			if(totalH > pageHeight) return loopCount-1;
			if(totalH == pageHeight) return loopCount;
		}
		return table.getRows().size();
	}
	private static String findValue(ReportStgVb reportsStgs, String type, int index){
		if(CAP_COL.equalsIgnoreCase(type)){
			switch(index){
				case 1: return reportsStgs.getCaptionColumn1() == null ? "\u00a0" : reportsStgs.getCaptionColumn1();
				case 2: return reportsStgs.getCaptionColumn2() == null ? "\u00a0" : reportsStgs.getCaptionColumn2();
				case 3: return reportsStgs.getCaptionColumn3() == null ? "\u00a0" : reportsStgs.getCaptionColumn3();
				case 4: return reportsStgs.getCaptionColumn4() == null ? "\u00a0" : reportsStgs.getCaptionColumn4();
				case 5: return reportsStgs.getCaptionColumn5() == null ? "\u00a0" : reportsStgs.getCaptionColumn5();
			}
		}else if (DATA_COL.equalsIgnoreCase(type)){
			switch(index){
				case 1 : return reportsStgs.getDataColumn1()  == null? "\u00a0": reportsStgs.getDataColumn1() ;
				case 2 : return reportsStgs.getDataColumn2()  == null? "\u00a0": reportsStgs.getDataColumn2() ;
				case 3 : return reportsStgs.getDataColumn3()  == null? "\u00a0": reportsStgs.getDataColumn3() ;
				case 4 : return reportsStgs.getDataColumn4()  == null? "\u00a0": reportsStgs.getDataColumn4() ;
				case 5 : return reportsStgs.getDataColumn5()  == null? "\u00a0": reportsStgs.getDataColumn5() ;
				case 6 : return reportsStgs.getDataColumn6()  == null? "\u00a0": reportsStgs.getDataColumn6() ;
				case 7 : return reportsStgs.getDataColumn7()  == null? "\u00a0": reportsStgs.getDataColumn7() ;
				case 8 : return reportsStgs.getDataColumn8()  == null? "\u00a0": reportsStgs.getDataColumn8() ;
				case 9 : return reportsStgs.getDataColumn9()  == null? "\u00a0": reportsStgs.getDataColumn9() ;
				case 10: return reportsStgs.getDataColumn10() == null? "\u00a0": reportsStgs.getDataColumn10(); 
				case 11: return reportsStgs.getDataColumn11() == null? "\u00a0": reportsStgs.getDataColumn11(); 
				case 12: return reportsStgs.getDataColumn12() == null? "\u00a0": reportsStgs.getDataColumn12(); 
				case 13: return reportsStgs.getDataColumn13() == null? "\u00a0": reportsStgs.getDataColumn13(); 
				case 14: return reportsStgs.getDataColumn14() == null? "\u00a0": reportsStgs.getDataColumn14(); 
				case 15: return reportsStgs.getDataColumn15() == null? "\u00a0": reportsStgs.getDataColumn15(); 
				case 16: return reportsStgs.getDataColumn16() == null? "\u00a0": reportsStgs.getDataColumn16(); 
				case 17: return reportsStgs.getDataColumn17() == null? "\u00a0": reportsStgs.getDataColumn17(); 
				case 18: return reportsStgs.getDataColumn18() == null? "\u00a0": reportsStgs.getDataColumn18(); 
				case 19: return reportsStgs.getDataColumn19() == null? "\u00a0": reportsStgs.getDataColumn19(); 
				case 20: return reportsStgs.getDataColumn20() == null? "\u00a0": reportsStgs.getDataColumn20(); 
				case 21: return reportsStgs.getDataColumn21() == null? "\u00a0": reportsStgs.getDataColumn21(); 
				case 22: return reportsStgs.getDataColumn22() == null? "\u00a0": reportsStgs.getDataColumn22(); 
				case 23: return reportsStgs.getDataColumn23() == null? "\u00a0": reportsStgs.getDataColumn23(); 
				case 24: return reportsStgs.getDataColumn24() == null? "\u00a0": reportsStgs.getDataColumn24(); 
				case 25: return reportsStgs.getDataColumn25() == null? "\u00a0": reportsStgs.getDataColumn25(); 
				case 26: return reportsStgs.getDataColumn26() == null? "\u00a0": reportsStgs.getDataColumn26(); 
				case 27: return reportsStgs.getDataColumn27() == null? "\u00a0": reportsStgs.getDataColumn27(); 
				case 28: return reportsStgs.getDataColumn28() == null? "\u00a0": reportsStgs.getDataColumn28(); 
				case 29: return reportsStgs.getDataColumn29() == null? "\u00a0": reportsStgs.getDataColumn29(); 
				case 30: return reportsStgs.getDataColumn30() == null? "\u00a0": reportsStgs.getDataColumn30(); 
				case 31: return reportsStgs.getDataColumn31() == null? "\u00a0": reportsStgs.getDataColumn31(); 
				case 32: return reportsStgs.getDataColumn32() == null? "\u00a0": reportsStgs.getDataColumn32(); 
				case 33: return reportsStgs.getDataColumn33() == null? "\u00a0": reportsStgs.getDataColumn33(); 
				case 34: return reportsStgs.getDataColumn34() == null? "\u00a0": reportsStgs.getDataColumn34(); 
				case 35: return reportsStgs.getDataColumn35() == null? "\u00a0": reportsStgs.getDataColumn35(); 
				case 36: return reportsStgs.getDataColumn36() == null? "\u00a0": reportsStgs.getDataColumn36(); 
				case 37: return reportsStgs.getDataColumn37() == null? "\u00a0": reportsStgs.getDataColumn37(); 
				case 38: return reportsStgs.getDataColumn38() == null? "\u00a0": reportsStgs.getDataColumn38(); 
				case 39: return reportsStgs.getDataColumn39() == null? "\u00a0": reportsStgs.getDataColumn39(); 
				case 40: return reportsStgs.getDataColumn40() == null? "\u00a0": reportsStgs.getDataColumn40();
			}
		}
		return "\u00a0";
	}
	private static int getStyle(ReportStgVb reportStgVb, int rowNum){
		if((rowNum %2) == 0 && ("S".equalsIgnoreCase(reportStgVb.getFormatType()) || "G".equalsIgnoreCase(reportStgVb.getFormatType()))){
			return 1; //Summary with Background
		}else if((rowNum %2) == 0){
			return 2; //Non Summary with background.
		}else if("S".equalsIgnoreCase(reportStgVb.getFormatType()) || "G".equalsIgnoreCase(reportStgVb.getFormatType())){
			return 3;//Summary with out Background
		}else{
			return 4;//Non Summary With out background
		}
	}
//	private static float drawHeaderAndFooters(Document document, ReportsWriterVb reportWriterVb, 
//			List<PromptIdsVb> prompts, String assetFolderUrl) throws MalformedURLException, IOException, DocumentException{
//		int height = 30;
//		BaseFont baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
//		PdfPTable borderTable = new PdfPTable(1);
//		borderTable.setWidthPercentage(100);
//		PdfPCell cellMain = new PdfPCell();
//		cellMain.setBorder(0&0&0&1);
//		//cellMain.setBackgroundColor(new BaseColor(90, 120, 138));
//		cellMain.setBorderWidth(1);
//		cellMain.setFixedHeight(40);
//		cellMain.setBackgroundColor(new BaseColor(205, 226, 236));
//		PdfPTable headerTable = new PdfPTable(3);
//		headerTable.setWidthPercentage(100);
//		headerTable.setSpacingAfter(2);
//		//promptsTable.setSpacingBefore(2);
//		int widths[] = {1,5,1};
//		headerTable.setWidths(widths);
//		Image visionLogoUrl = Image.getInstance(assetFolderUrl+"/vision_rep_pdf.png"); //Vision_rep_pdf.png
//		PdfPCell cell1 = new PdfPCell();
////		cell1.setImage(visionLogoUrl);
//		visionLogoUrl.scaleAbsolute(50f, 50f);
//		cell1.addElement(visionLogoUrl);
////		visionLogoUrl.setWidthPercentage(70);
////		visionLogoUrl.setBackgroundColor(new BaseColor(235, 241, 222));
//		cell1.setBackgroundColor(new BaseColor(205, 226, 236));
//		cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
//		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
//		//cell1.setFixedHeight(30);
//		if(prompts != null && prompts.size()>2){
//			visionLogoUrl.scaleAbsolute(60f, 60f);
//			cell1.setPaddingTop(10);
////			cell1.setPaddingBottom(6);
//		}
//		cell1.setBorder(0&0&0&0);
//		
//		headerTable.addCell(cell1);
//		List<PdfPCell> cells= new ArrayList<PdfPCell>();
//		if(prompts != null && !prompts.isEmpty()){
//			int cnt = 1;
//			String prvCtrl = "";
//			String lbl = "";
//			if(prompts.size()>2) height = 40;
//			for (PromptIdsVb promptIdsVbTemp12:prompts){
//				if("HIDDEN".equalsIgnoreCase(promptIdsVbTemp12.getPromptType())){
//					cnt++;
//					continue;
//				}
//				if(cnt==3){
//					cnt = 1;
//					lbl = StringUtils.trimTrailingWhitespace(lbl);
//					PdfPCell cell =new PdfPCell();
//					Font fontheading = new Font(baseFont);
//					fontheading.setSize(10);
//					fontheading.setStyle(Font.BOLD);
//					Phrase ph= new Phrase(lbl,fontheading);
//					cell.setPhrase(ph);
//					cell.setBackgroundColor(new BaseColor(205, 226, 236));
//					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//					cell.setBorder(0&0&0&0);
//					cells.add(cell);
//					lbl = "";
//					if(!"RANGE".equalsIgnoreCase(promptIdsVbTemp12.getPromptType()))
//						height += 10;
//				}
//				if("COMBO".equalsIgnoreCase(promptIdsVbTemp12.getPromptType()) || 
//					"TREE".equalsIgnoreCase(promptIdsVbTemp12.getPromptType()) ||
//					promptIdsVbTemp12.getPromptType().contains("TEXT")){
//					if((lbl +  promptIdsVbTemp12.getPromptString() + " : " +promptIdsVbTemp12.getSelectedValue1().getField2()).length() < 50)
//						lbl = lbl +"                ";
//					if((lbl +  promptIdsVbTemp12.getPromptString() + " : " +promptIdsVbTemp12.getSelectedValue1().getField2()).length() < 100)
//						lbl = lbl +"        ";
//					String promptStrData = "";
//					if(promptIdsVbTemp12.getPromptType().contains("TEXT")){
//						promptStrData = promptIdsVbTemp12.getSelectedValue1().getField1();
//					}else{
//						promptStrData = promptIdsVbTemp12.getSelectedValue1().getField2();
//					}
//					lbl = StringUtils.trimLeadingWhitespace(lbl) +  promptIdsVbTemp12.getPromptString() + " : " +promptStrData +"	";
//					prvCtrl = promptIdsVbTemp12.getPromptType();
//				}else if("RANGE".equalsIgnoreCase(promptIdsVbTemp12.getPromptType())){
//					if(ValidationUtil.isValid(prvCtrl)){
//						lbl = StringUtils.trimWhitespace(lbl);
//						PdfPCell cell =new PdfPCell();
//						Font fontheading = new Font(baseFont);
//						fontheading.setSize(10);
//						fontheading.setStyle(Font.BOLD);
//						Phrase ph= new Phrase(lbl,fontheading);
//						cell.setPhrase(ph);
//						cell.setBackgroundColor(new BaseColor(205, 226, 236));
//						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//						cell.setBorder(0&0&0&0);
//						cells.add(cell);
//						height += 10;
//					}
//					String tabs = "                ";
//					if((promptIdsVbTemp12.getPromptString() + "     From : " +promptIdsVbTemp12.getSelectedValue1().getField1() 
//							+","+" To : "+ promptIdsVbTemp12.getSelectedValue2().getField1()).length() <100){
//						tabs = "                ";
//					}
//					lbl = promptIdsVbTemp12.getPromptString() + "     From : " +promptIdsVbTemp12.getSelectedValue1().getField1() +","+tabs+" To : "
//						+ promptIdsVbTemp12.getSelectedValue2().getField1();
//					lbl = StringUtils.trimTrailingWhitespace(lbl);
//					PdfPCell cell =new PdfPCell();
//					Font fontheading = new Font(baseFont);
//					fontheading.setSize(10);
//					fontheading.setStyle(Font.BOLD);
//					Phrase ph= new Phrase(lbl,fontheading);
//					cell.setPhrase(ph);
//					cell.setBackgroundColor(new BaseColor(205, 226, 236));
//					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//					cell.setBorder(0&0&0&0);
//					cells.add(cell);
//				}else{
//					if((lbl +  promptIdsVbTemp12.getPromptString() + " : " +promptIdsVbTemp12.getSelectedValue1().getField2()).length() < 50)
//						lbl = lbl +"                ";
//					if((lbl +  promptIdsVbTemp12.getPromptString() + " : " +promptIdsVbTemp12.getSelectedValue1().getField2()).length() < 100)
//						lbl = lbl +"        ";
//					String promptStrData = "";
//					promptStrData = promptIdsVbTemp12.getSelectedValue1().getField1();
//					lbl = StringUtils.trimLeadingWhitespace(lbl) +  promptIdsVbTemp12.getPromptString() + " : " +promptStrData +"    ";
//					prvCtrl = promptIdsVbTemp12.getPromptType();
//				}
//				cnt++;
//			}
//			lbl = StringUtils.trimTrailingWhitespace(lbl);
//			PdfPCell cell =new PdfPCell();
//			Font fontheading = new Font(baseFont);
//			fontheading.setSize(8);
//			fontheading.setStyle(Font.BOLD);
//			Phrase ph= new Phrase(lbl,fontheading);
//			cell.setPhrase(ph);
//			cell.setBackgroundColor(new BaseColor(205, 226, 236));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//			cell.setBorder(0&0&0&0);
//			cells.add(cell);
//			if(prompts.size() >4)  height += 10;
//		}
//		PdfPTable promptsTable = new PdfPTable(1);
//		promptsTable.setWidthPercentage(100);
//		
//		PdfPCell cell3 = new PdfPCell();
//		Font fontheading = new Font(baseFont);
//		fontheading.setSize(10);
//		fontheading.setStyle(Font.BOLD);
//		Phrase ph= new Phrase(reportWriterVb.getReportTitle(),fontheading);
//		cell3.setPhrase(ph);
//		cell3.setBackgroundColor(new BaseColor(205, 226, 236));
//		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
//		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
//		cell3.setBorder(0&0&0&0);
//		//cell3.setFixedHeight(38);
//		promptsTable.addCell(cell3);
//		for(PdfPCell cell:cells){
//			promptsTable.addCell(cell);
//		}
//					
//		PdfPCell nestedCell = new PdfPCell();
//		nestedCell.addElement(promptsTable);
//		nestedCell.setPadding(0);
//		nestedCell.setBorder(0);
//		nestedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//		nestedCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//		nestedCell.setBackgroundColor(new BaseColor(205, 226, 236));
//		headerTable.addCell(nestedCell);
//		
//		Image sunoidaLogoUrl = Image.getInstance(assetFolderUrl+"/Sunoida_rep_pdf.PNG");
//		PdfPCell cell2 = new PdfPCell();
//		cell2.setImage(sunoidaLogoUrl);
//		cell2.setBackgroundColor(new BaseColor(205, 226, 236));
//		cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
//		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
//		cell2.setBorder(0&0&0&0);
//		cell2.setFixedHeight(30);
//		if(prompts != null && prompts.size()>2){
//			cell2.setPaddingTop(10);
////			cell2.setPaddingBottom(6);
//		}		
//		headerTable.addCell(cell2);
//		
//		cellMain.setFixedHeight(height);
//		cellMain.addElement(headerTable);
//		borderTable.addCell(cellMain);
//		document.add(borderTable);
//		
//		PdfPTable scalingTable = new PdfPTable(1);
//		scalingTable.setWidthPercentage(100);
//		PdfPCell cellMainSC = new PdfPCell();
//		cellMainSC.setBorder(1|1|1|1);
//		cellMainSC.setBorderColor(BaseColor.BLACK);
//		cellMainSC.setBorderColorBottom(BaseColor.BLACK);
//		cellMainSC.setBorderWidthBottom(1);
//		cellMainSC.setBackgroundColor(new BaseColor(205, 226, 236));
//		cellMainSC.setBorderWidth(1);
//		cellMainSC.setFixedHeight(15);
//		Font fontScF = new Font(baseFont);
//		fontScF.setSize(7);
//		cellMainSC.addElement(new Phrase("  Scaling Factor : "+ reportWriterVb.getScalingFactor(),fontScF));
//		cellMainSC.setHorizontalAlignment(Element.ALIGN_RIGHT);
//		cellMainSC.setVerticalAlignment(Element.ALIGN_MIDDLE);
//		scalingTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
//		scalingTable.addCell(cellMainSC);
//		document.add(scalingTable);
//		return scalingTable.calculateHeights(true) + borderTable.calculateHeights(true);
//	}
	static class InvPdfPageEventHelper extends com.itextpdf.text.pdf.PdfPageEventHelper{
		PdfTemplate total;
		PdfTemplate totalPages;
		BaseFont baseFont = null;
		int footerTextSize =7;
		int pageNumberAlignment = Element.ALIGN_LEFT;
		String footerText = "";
		String headerText = "";
		String assetFolderUrl = "";
		ReportsWriterVb reportWriterVb;
		List<PromptIdsVb> prompts = null;
		float headerHeight = 0f;
		ReportsVb reportsVb;
		public InvPdfPageEventHelper(String headerText, String footerText, String assetFolderUrl,
				List<PromptIdsVb> prompts,ReportsWriterVb reportWriterVb) throws DocumentException, IOException{
			this.headerText = headerText;
			this.footerText = footerText;
			this.assetFolderUrl = assetFolderUrl;
			this.prompts = prompts;
			this.reportWriterVb = reportWriterVb;
			baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
		}
		public InvPdfPageEventHelper(String headerText, String footerText, String assetFolderUrl,
				List<PromptIdsVb> prompts,ReportsVb reportsVb) throws DocumentException, IOException{
			this.headerText = headerText;
			this.footerText = footerText;
			this.assetFolderUrl = assetFolderUrl;
			this.prompts = prompts;
			this.reportsVb = reportsVb;
			baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
		}
		@Override
		public void onOpenDocument(PdfWriter writer, Document document) {
		    totalPages = writer.getDirectContent().createTemplate(100, 100);
		    totalPages.setBoundingBox(new Rectangle(-20, -20, 100, 100));
		}

		/*@Override
		public void onStartPage(PdfWriter writer, Document document) {
			try {
				headerHeight = drawHeaderAndFooters(document, reportWriterVb, prompts, assetFolderUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}*/
		@Override
		public void onStartPage(PdfWriter writer, Document document) {
			try {
				headerHeight = drawHeaderAndFootersRA(document, reportsVb, prompts, assetFolderUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
		    PdfContentByte cb = writer.getDirectContent();
		    Rectangle outLine = document.getPageSize();
			Rectangle footer = new Rectangle(outLine.getLeft(20) , outLine.getTop(20), outLine.getRight(20), outLine.getBottom(20));
			footer.setBorderWidth(1f);
			footer.setBorderColor(BaseColor.BLACK);
			footer.setBorder(Rectangle.BOX);
			cb.rectangle(footer);
		    cb.saveState();
		    String text = "";
		    float textBase = document.bottom() - 10;
		    cb.beginText();
		    cb.setFontAndSize(baseFont, footerTextSize);
	        cb.setTextMatrix((document.right() / 2), textBase);
	        cb.showText(footerText);
	        float textSize = baseFont.getWidthPoint(text, footerTextSize);
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm:ss a");
	    	text = dateFormat.format(new Date());
	        float adjust = baseFont.getWidthPoint("0", footerTextSize);
	        textSize = baseFont.getWidthPoint(text, footerTextSize);
	        cb.setTextMatrix(document.right() - textSize - adjust, textBase);
	        cb.showText(text);
	        
	        text = String.format("Page %s of ", writer.getPageNumber());
	    	textSize = baseFont.getWidthPoint(text, footerTextSize);
	        cb.setTextMatrix(document.left()+2, textBase);
	        cb.showText(text);
	        cb.endText();
	        cb.addTemplate(totalPages, document.left() + 2 + textSize, textBase);
	        cb.restoreState();
		}

		@Override
		public void onCloseDocument(PdfWriter writer, Document document) {
		    totalPages.beginText();
		    totalPages.setFontAndSize(baseFont, footerTextSize);
		    totalPages.setTextMatrix(0, 0);
		    totalPages.showText(String.valueOf(writer.getPageNumber() - 1));
		    totalPages.endText();
		}
	}
	private static String getChartType(int defChartType){
		switch(defChartType){
			case 1:return "Pie2D";
			case 2:return "Pie3D";
			case 6:return "Column3D";
			case 7:return "StackedColumn3D";
			case 9:return "Area2D";
			case 19:return "MSColumn3D";
			case 20:return "Bubble";
			case 21:return "MSColumn3DLineDY";
			case 22:return "Radar";
			case 23:return "HeatMap";
			case 24:return "MSLine";
			default:return "G";
		}
	}
	private static void createChart(ReportsWriterVb reportWriterVb, String jspFolder, String chartType, String chartName,int recordCnt){
		Writer writer = null;
		String xmlData = "";
        try {
			File file = new File(jspFolder+"/ExportExample/Data/"+chartName+".xml");
	        if(file.exists()){
	        	file.delete();
			}
			file.createNewFile();
	        xmlData = createChartsData(reportWriterVb, chartType, chartName, recordCnt,"");
	        writer = new BufferedWriter(new FileWriter(file));
        	writer.write(xmlData);
	        writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String createChartsData(ReportsWriterVb reportsWriterVb, String fcChartType, String chartName, int recordCnt,String captionFlag) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document document = db.newDocument();
		org.w3c.dom.Element rootElement = document.createElement("chart");
		document.appendChild(rootElement);
		if("NC".equalsIgnoreCase(captionFlag) == false){
			rootElement.setAttribute("caption", reportsWriterVb.getReportTitle());
		}
		String[] colors = {"#86A0B4","#A6C156","#0066CC","#D95000","#E4A379","#9B72CF","#FF9966","#AFD8F8","#F6BD0F","#8BBA00","#FF8E46","#008E8E","#D64646","#8E468E","#588526","#B3AA00"};
		String colorPallet1="AFD8F8,9D080D,F6BD0F,8BBA00,FF8E46,008E8E,D64646,8E468E,588526,B3AA00,008ED6,9D080D,A186BE,CC6600,FDC689,ABA000,F26D7D,FFF200,0054A6,F7941C,CC3300,006600,663300,6DCFF6,86A0B4,A6C156,0066CC,D95000,E4A379,9B72CF,FF9966,AFD8F8,F6BD0F,8BBA00,FF8E46,008E8E,D64646,8E468E,588526,B3AA00";
		String colorPallet2="9D080D,25357A,A186BE,CC6600,FDC689,ABA000,F26D7D,FFF200,0054A6,F7941C,CC3300,006600,663300,6DCFF6,86A0B4,A6C156,0066CC,D95000,E4A379,9B72CF,FF9966,AFD8F8,F6BD0F,8BBA00,FF8E46,008E8E,D64646,8E468E,588526,B3AA00,AFD8F8,F6BD0F,8BBA00,FF8E46,008E8E,D64646,8E468E,588526,B3AA00";
		String colorPallet3="A186BE,CC6600,FDC689,ABA000,F26D7D,FFF200,0054A6,F7941C,CC3300,006600,663300,6DCFF6,86A0B4,A6C156,0066CC,D95000,AFD8F8,F6BD0F,8BBA00,FF8E46,008E8E,D64646,8E468E,588526,B3AA00,008ED6,9D080D,E4A379,9B72CF,FF9966,AFD8F8,F6BD0F,8BBA00,FF8E46,008E8E,D64646,8E468E,588526,B3AA00";
		String colorPallet4="AFD8F8,F6BD0F,8BBA00,FF8E46,008E8E,D64646,8E468E,588526,B3AA00,006600,663300,6DCFF6,86A0B4,A6C156,0066CC,D95000,E4A379,9B72CF,FF9966,CC6600,FDC689,ABA000,F26D7D,FFF200,0054A6,F7941C,CC3300,AFD8F8,F6BD0F,8BBA00,FF8E46,008E8E,D64646,8E468E,588526,B3AA00,008ED6,9D080D,A186BE";

		List<ColumnHeadersVb> columnHeaders = reportsWriterVb.getColumnHeaders();
//		xmlData.append("<chart caption='"+reportsWriterVb.getReportTitle()+"' exportEnabled='1' exportAction='Save' exportAtClient='0' exportHandler='FCExporter' exportFileName='"+chartName+"' ");
		if("Pie3D".equalsIgnoreCase(fcChartType) || "Radar".equalsIgnoreCase(fcChartType)){
			rootElement.setAttribute("showLegend", "1");
		}	
		if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())){
			rootElement.setAttribute("xAxisName", columnHeaders.get(0).getCaption());
		}else if("L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())){
			rootElement.setAttribute("xAxisName", columnHeaders.get(1).getCaption());
		}else if("L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())){
			rootElement.setAttribute("xAxisName", columnHeaders.get(2).getCaption());
		}else{
			rootElement.setAttribute("yAxisName", columnHeaders.get(0).getCaption());
		}

		if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
			rootElement.setAttribute("yAxisName", columnHeaders.get(0).getCaption());
		}else if("L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
			rootElement.setAttribute("yAxisName", columnHeaders.get(1).getCaption());
		}else if("L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
			rootElement.setAttribute("yAxisName", columnHeaders.get(2).getCaption());
		}else{
			rootElement.setAttribute("xAxisName", columnHeaders.get(0).getCaption());
		}
		rootElement.setAttribute("showAboutMenuItem", "0");
		rootElement.setAttribute("showToolTip", "1");
		rootElement.setAttribute("canvasBorderThickness", "1");
		int dataColCount = 0;
		if(reportsWriterVb.getLabelRowCount() >1){
			int elmsInRowOne = 0;
			int elmsInRowTwo = 0;
			for(ColumnHeadersVb colHdrs: reportsWriterVb.getColumnHeaders()){
				if(colHdrs.getLabelRowNum() <=1){
					elmsInRowOne++;
				}else{
					elmsInRowTwo++;
				}
			}
			dataColCount = (elmsInRowOne -  reportsWriterVb.getLabelColCount()) * elmsInRowTwo;
		}else{
			dataColCount  = reportsWriterVb.getColumnHeaders().size() - reportsWriterVb.getLabelColCount();
		}
		if(dataColCount >= 2 && "Column3D".equalsIgnoreCase(fcChartType)){
			fcChartType = "MSColumn3D";
		}
		org.w3c.dom.Element styles = document.createElement("styles"); 	
		org.w3c.dom.Element definition = document.createElement("definition");
		org.w3c.dom.Element style = document.createElement("style");
		style.setAttribute("name", "myCaptionFont");
		style.setAttribute("type", "font");
		style.setAttribute("font", "Verdana");
		style.setAttribute("size", "10");
		style.setAttribute("bold", "1");
		style.setAttribute("color", "000000");
		definition.appendChild(style);
		org.w3c.dom.Element application = document.createElement("application");
		org.w3c.dom.Element apply = document.createElement("apply");
		apply.setAttribute("toObject", "Caption");
		apply.setAttribute("styles", "myCaptionFont");
		application.appendChild(apply);
		styles.appendChild(definition);
		styles.appendChild(application);
		rootElement.appendChild(styles);
		
		rootElement.setAttribute("palette",recordCnt+"");
		String colorPallet = recordCnt  == 1? colorPallet1: recordCnt == 2? colorPallet2: recordCnt == 3?colorPallet3:colorPallet4;
		rootElement.setAttribute("paletteColors", colorPallet);
		
		if("Pie3D".equalsIgnoreCase(fcChartType)){
			int colrCnt =0;
			long total = 0;
			for (ReportStgVb objFc1: reportsWriterVb.getReportsData()){
				String value = objFc1.getDataColumn1().replaceAll(",", "");
				if(!ValidationUtil.isValid(value)){
					value="0.00";
				}
				total += Math.abs(Double.parseDouble(value));
			}
			for(ReportStgVb objFc: reportsWriterVb.getReportsData()){
				String value = objFc.getDataColumn1().replaceAll(",", "");
				double perVal = (Math.abs(Double.parseDouble(objFc.getDataColumn1().replaceAll(",", ""))) * 100 / total);
				NumberFormat fromat = new DecimalFormat(".00");
				String value1 = fromat.format(perVal) == "0.00"?"0.01%":fromat.format(perVal) +"%";
				org.w3c.dom.Element set = document.createElement("set");
				set.setAttribute("label", objFc.getCaptionColumn1());
				set.setAttribute("color", colors[colrCnt]);
				if(!ValidationUtil.isValid(value)){
					value="0.00";
				}
				set.setAttribute("value", Math.abs(Double.parseDouble(value))+"");
				set.setAttribute("displayValue", value1);
				rootElement.appendChild(set);
				colrCnt++;
			}
		}if("Column3D".equalsIgnoreCase(fcChartType) || "Area2D".equalsIgnoreCase(fcChartType)){
			for (ReportStgVb objFc2: reportsWriterVb.getReportsData()){
				String value = objFc2.getDataColumn1().replaceAll(",", "");
				org.w3c.dom.Element set = document.createElement("set");
				set.setAttribute("label", objFc2.getCaptionColumn1());
				if(!ValidationUtil.isValid(value)){
					value="0.00";
				}
				set.setAttribute("value", Math.abs(Double.parseDouble(value))+"");
				rootElement.appendChild(set);
			}
		}else if("StackedColumn3D".equalsIgnoreCase(fcChartType) || "MSColumn3D".equalsIgnoreCase(fcChartType) || "MSLine".equalsIgnoreCase(fcChartType)){
			org.w3c.dom.Element categories = document.createElement("categories");
			rootElement.appendChild(categories);
			if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX()) || "L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX()) || "L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())){
				int intIndex= "L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())?1:"L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())?2:3;
				for(ReportStgVb objRData : reportsWriterVb.getReportsData()){
					String caption = (intIndex==1)? objRData.getCaptionColumn1():intIndex==2?objRData.getCaptionColumn2():objRData.getCaptionColumn3();
					/*caption = caption.replaceAll("<", "&lt;");
					caption = caption.replaceAll(">", "&gt;");*/
					org.w3c.dom.Element category = document.createElement("category");
					category.setAttribute("label", caption);
					categories.appendChild(category);
				}
			}else{
				for(int intloopCnt = reportsWriterVb.getLabelColCount();intloopCnt<reportsWriterVb.getColumnHeaders().size();intloopCnt++){
					ColumnHeadersVb obChRData = reportsWriterVb.getColumnHeaders().get(intloopCnt);
					String catptionCol = obChRData.getCaption();
					/*catptionCol = catptionCol.replaceAll("<", "&lt;");
					catptionCol = catptionCol.replaceAll(">", "&gt;");*/
					org.w3c.dom.Element category = document.createElement("category");
					category.setAttribute("label", catptionCol);
					categories.appendChild(category);
				}
			}
			
			ArrayList<List<org.w3c.dom.Element>> serAC = new ArrayList<List<org.w3c.dom.Element>>();
			ArrayList<org.w3c.dom.Element> catAC = new ArrayList<org.w3c.dom.Element>();
			Boolean firstLoop = true;
			for(int cnt=0;cnt< reportsWriterVb.getReportsData().size();cnt++){
				ReportStgVb objRData2 = reportsWriterVb.getReportsData().get(cnt);
				org.w3c.dom.Element dataset = document.createElement("dataset");
				if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn1());
				}else if("L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn2());
				}else if( "L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn2());
				}else{
					dataset.setAttribute("seriesName", reportsWriterVb.getColumnHeaders().get(cnt-1+reportsWriterVb.getLabelColCount()).getCaption());
				}
				catAC.add(dataset);
				for(int cnt2 = 0;cnt2<dataColCount;cnt2++){
					org.w3c.dom.Element set = document.createElement("set");
					String value = findValue(objRData2, cnt2+1);
					value = value.replaceAll(",", "");
					if(!ValidationUtil.isValid(value)){
						value="0.00";
					}
					set.setAttribute("value", Math.abs(Double.parseDouble(value))+"");
					if(firstLoop){
						serAC.add(new ArrayList<org.w3c.dom.Element>());
					}
					serAC.get(cnt2).add(set);
				}
				firstLoop = false;
			}
			for(int cnt5=0;cnt5<catAC.size();cnt5++){
				org.w3c.dom.Element seriesXML1 = catAC.get(cnt5);
				for(List<org.w3c.dom.Element> tmpAC: serAC){
					seriesXML1.appendChild(tmpAC.get(cnt5));
				}
				rootElement.appendChild(seriesXML1);
			}
		}else if(fcChartType == "Bubble"){
			int mult =1;
			org.w3c.dom.Element categories = document.createElement("categories");
			rootElement.appendChild(categories);
			if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())  || 
					"L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX()) || 
					"L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())){
				int intIndex = "L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())?1:"L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())?2:3;
				mult = reportsWriterVb.getReportsData().size() > 8? 1: 10;
				for(int intloopCnt = 0;intloopCnt< reportsWriterVb.getReportsData().size();intloopCnt++ ){
					ReportStgVb objRData = reportsWriterVb.getReportsData().get(intloopCnt);
					org.w3c.dom.Element category = document.createElement("category");
					category.setAttribute("label", findValue(objRData, CAP_COL, intIndex));
					category.setAttribute("x", ( mult*(intloopCnt+1))+"");
					category.setAttribute("showVerticalLine", "1");
					categories.appendChild(category);
				}
			}else{
				mult = reportsWriterVb.getColumnHeaders().size() > 8? 1: 10;
				for(int intloopCnt = reportsWriterVb.getLabelColCount();intloopCnt<reportsWriterVb.getColumnHeaders().size();intloopCnt++){
					ColumnHeadersVb obChRData = reportsWriterVb.getColumnHeaders().get(intloopCnt);
					org.w3c.dom.Element category = document.createElement("category");
					category.setAttribute("label", obChRData.getCaption());
					category.setAttribute("x", (mult*((intloopCnt-reportsWriterVb.getLabelColCount())+1))+"");
					category.setAttribute("showVerticalLine", "1");
					categories.appendChild(category);
				}
			}
			ArrayList<List<org.w3c.dom.Element>> serAC = new ArrayList<List<org.w3c.dom.Element>>();
			ArrayList<org.w3c.dom.Element> catAC = new ArrayList<org.w3c.dom.Element>();
			Boolean firstLoop = true;
			ArrayList<Double> values = new ArrayList<Double>();
			for(int cnt=0;cnt< reportsWriterVb.getReportsData().size();cnt++){
				ReportStgVb objRData2 = reportsWriterVb.getReportsData().get(cnt);
				org.w3c.dom.Element dataset = document.createElement("dataset");
				if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn1());
				}else if("L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn2());
				}else if( "L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn2());
				}else{
					dataset.setAttribute("seriesName", reportsWriterVb.getColumnHeaders().get(cnt-1+reportsWriterVb.getLabelColCount()).getCaption());
				}
				catAC.add(dataset);
				for(int cnt2 = 0;cnt2<dataColCount;cnt2++){
					org.w3c.dom.Element set = document.createElement("set");
					String value = findValue(objRData2, cnt2+1);
					value = value.replaceAll(",", "");
					if(!ValidationUtil.isValid(value))
						value = "0.00";
					set.setAttribute("y", Math.abs(Double.parseDouble(value))+"");
					set.setAttribute("x", (mult*(cnt2+1))+"");
					values.add(Math.abs(Double.parseDouble(value)));
					if(firstLoop){
						serAC.add(new ArrayList<org.w3c.dom.Element>());
					}
					serAC.get(cnt2).add(set);
				}
				firstLoop = false;
			}
			Collections.sort(values);
			for(int cnt5=0;cnt5<catAC.size();cnt5++){
				org.w3c.dom.Element seriesXML1 = catAC.get(cnt5);
				for(List<org.w3c.dom.Element> tmpAC: serAC){
					org.w3c.dom.Element element = tmpAC.get(cnt5);
					String val = element.getAttribute("y");
					int idx =  values.indexOf(Double.parseDouble(val));
					element.setAttribute("z", (idx+1)+"");
					seriesXML1.appendChild(element);
				}
				rootElement.appendChild(seriesXML1);
			}
			if(values != null && !values.isEmpty()){
				rootElement.setAttribute("yAxisMaxValue", (values.get(values.size()-1)*.05+values.get(values.size()-1))+"");
				rootElement.setAttribute("yAxisMinValue", (values.get(0)-values.get(0)*.05)+"");
			}
			rootElement.setAttribute("numDivLines", "10");
			rootElement.setAttribute("adjustDiv", "0");
			rootElement.setAttribute("bgColor", "FFFFFF,FFFFFF");
			rootElement.setAttribute("chartLeftMargin", "1");
			rootElement.setAttribute("chartRightMargin", "1");
			rootElement.setAttribute("showBorder", "0");
			rootElement.setAttribute("showValues", "1");
		}else if(fcChartType == "MSColumn3DLineDY"){
			org.w3c.dom.Element categories = document.createElement("categories");
			rootElement.appendChild(categories);
			if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX()) || "L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX()) || "L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())){
				int intIndex= "L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())?1:"L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())?2:3;
				for(ReportStgVb objRData : reportsWriterVb.getReportsData()){
					String caption = (intIndex==1)? objRData.getCaptionColumn1():intIndex==2?objRData.getCaptionColumn2():objRData.getCaptionColumn3();
					/*caption = caption.replaceAll("<", "&lt;");
					caption = caption.replaceAll(">", "&gt;");*/
					org.w3c.dom.Element category = document.createElement("category");
					category.setAttribute("label", caption);
					categories.appendChild(category);
				}
			}else{
				for(int intloopCnt = reportsWriterVb.getLabelColCount();intloopCnt<reportsWriterVb.getColumnHeaders().size();intloopCnt++){
					ColumnHeadersVb obChRData = reportsWriterVb.getColumnHeaders().get(intloopCnt);
					String catptionCol = obChRData.getCaption();
					/*catptionCol = catptionCol.replaceAll("<", "&lt;");
					catptionCol = catptionCol.replaceAll(">", "&gt;");*/
					org.w3c.dom.Element category = document.createElement("category");
					category.setAttribute("label", catptionCol);
					categories.appendChild(category);
				}
			}
			
			ArrayList<List<org.w3c.dom.Element>> serAC = new ArrayList<List<org.w3c.dom.Element>>();
			ArrayList<org.w3c.dom.Element> catAC = new ArrayList<org.w3c.dom.Element>();
			Boolean firstLoop = true;
			for(int cnt=0;cnt< reportsWriterVb.getReportsData().size();cnt++){
				ReportStgVb objRData2 = reportsWriterVb.getReportsData().get(cnt);
				org.w3c.dom.Element dataset = document.createElement("dataset");
				if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn1());
				}else if("L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn2());
				}else if( "L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn2());
				}else{
					dataset.setAttribute("seriesName", reportsWriterVb.getColumnHeaders().get(cnt-1+reportsWriterVb.getLabelColCount()).getCaption());
				}
				catAC.add(dataset);
				for(int cnt2 = 0;cnt2<dataColCount;cnt2++){
					org.w3c.dom.Element set = document.createElement("set");
					String value = findValue(objRData2, cnt2+1);
					value = value.replaceAll(",", "");
					if(!ValidationUtil.isValid(value)){
						value="0.00";
					}
					set.setAttribute("value", Math.abs(Double.parseDouble(value))+"");
					if(firstLoop){
						serAC.add(new ArrayList<org.w3c.dom.Element>());
					}
					serAC.get(cnt2).add(set);
				}
				firstLoop = false;
			}
			for(int cnt5=0;cnt5<catAC.size();cnt5++){
				org.w3c.dom.Element seriesXML1 = catAC.get(cnt5);
				if(cnt5 == 0){
					seriesXML1.setAttribute("showValues", "0");
				}else{
					seriesXML1.setAttribute("ParentYAxis", "s");
				}
				for(List<org.w3c.dom.Element> tmpAC: serAC){
					seriesXML1.appendChild(tmpAC.get(cnt5));
				}
				rootElement.appendChild(seriesXML1);
			}
		}else if(fcChartType.equalsIgnoreCase("Radar")){
			int mult =1;
			org.w3c.dom.Element categories = document.createElement("categories");
			rootElement.appendChild(categories);
			if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())  || 
					"L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX()) || 
					"L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())){
				int intIndex = "L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())?1:"L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisX())?2:3;
				mult = reportsWriterVb.getReportsData().size() > 8? 1: 10;
				for(int intloopCnt = 0;intloopCnt< reportsWriterVb.getReportsData().size();intloopCnt++ ){
					ReportStgVb objRData = reportsWriterVb.getReportsData().get(intloopCnt);
					org.w3c.dom.Element category = document.createElement("category");
					category.setAttribute("label", findValue(objRData, CAP_COL, intIndex));
					category.setAttribute("x", ( mult*(intloopCnt+1))+"");
					category.setAttribute("showVerticalLine", "1");
					categories.appendChild(category);
				}
			}else{
				mult = reportsWriterVb.getColumnHeaders().size() > 8? 1: 10;
				for(int intloopCnt = reportsWriterVb.getLabelColCount();intloopCnt<reportsWriterVb.getColumnHeaders().size();intloopCnt++){
					ColumnHeadersVb obChRData = reportsWriterVb.getColumnHeaders().get(intloopCnt);
					org.w3c.dom.Element category = document.createElement("category");
					category.setAttribute("label", obChRData.getCaption());
					category.setAttribute("x", (mult*((intloopCnt-reportsWriterVb.getLabelColCount())+1))+"");
					category.setAttribute("showVerticalLine", "1");
					categories.appendChild(category);
				}
			}
			ArrayList<List<org.w3c.dom.Element>> serAC = new ArrayList<List<org.w3c.dom.Element>>();
			ArrayList<org.w3c.dom.Element> catAC = new ArrayList<org.w3c.dom.Element>();
			Boolean firstLoop = true;
			ArrayList<Double> values = new ArrayList<Double>();
			for(int cnt=0;cnt< reportsWriterVb.getReportsData().size();cnt++){
				ReportStgVb objRData2 = reportsWriterVb.getReportsData().get(cnt);
				org.w3c.dom.Element dataset = document.createElement("dataset");
				if("D".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn1());
				}else if("L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn2());
				}else if( "L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
					dataset.setAttribute("seriesName", objRData2.getCaptionColumn3());
				}else{
					dataset.setAttribute("seriesName", reportsWriterVb.getColumnHeaders().get(cnt-1+reportsWriterVb.getLabelColCount()).getCaption());
				}
				catAC.add(dataset);
				
				for(int cnt2 = 0;cnt2<dataColCount;cnt2++){
					org.w3c.dom.Element set = document.createElement("set");
					String value = findValue(objRData2, cnt2+1);
					value = value.replaceAll(",", "");
					if(!ValidationUtil.isValid(value)){
						value="0.00";
					}
					set.setAttribute("y", Math.abs(Double.parseDouble(value))+"");
					set.setAttribute("x", (mult*(cnt2+1))+"");
					set.setAttribute("value", Math.abs(Double.parseDouble(value))+"");
					if(firstLoop){
						serAC.add(new ArrayList<org.w3c.dom.Element>());
					}
					serAC.get(cnt2).add(set);
				} 
				firstLoop = false;
			}
			Collections.sort(values);
			for(int cnt5=0;cnt5<catAC.size();cnt5++){
				org.w3c.dom.Element seriesXML1 = catAC.get(cnt5);
				for(List<org.w3c.dom.Element> tmpAC: serAC){
					org.w3c.dom.Element element = tmpAC.get(cnt5);
					String val = element.getAttribute("y");
					int idx =  values.indexOf(Double.parseDouble(val));
					element.setAttribute("z", (idx+1)+"");
					seriesXML1.appendChild(element);
				}
				rootElement.appendChild(seriesXML1);
			}
			if(values != null && !values.isEmpty()){
				rootElement.setAttribute("yAxisMinValue", (values.get(values.size()-1)*.05+values.get(values.size()-1))+"");
				rootElement.setAttribute("yAxisMinValue", (values.get(0)-values.get(0)*.05)+"");
			}
			rootElement.setAttribute("numDivLines", "2");
			rootElement.setAttribute("adjustDiv", "0");
			rootElement.setAttribute("bgColor", "FFFFFF,FFFFFF");
			rootElement.setAttribute("chartLeftMargin", "1");
			rootElement.setAttribute("chartRightMargin", "1");
			rootElement.setAttribute("showBorder", "0");
			rootElement.setAttribute("showValues", "1");
			rootElement.setAttribute("palettecolors", "#AFD8F8,#9D080D,#F6BD0F,#8BBA00,#FF8E46,#008E8E,#D64646,#8E468E,#588526,#B3AA00,#008ED6,#9D080D,#A186BE,#CC6600,#FDC689,#ABA000,#F26D7D,#FFF200,#0054A6,#F7941C,#CC3300,#006600,#663300,#6DCFF6,#86A0B4,#A6C156,#0066CC,#D95000,#E4A379,#9B72CF,#FF9966,#AFD8F8,#F6BD0F,#8BBA00,#FF8E46,#008E8E,#D64646,#8E468E,#588526,#B3AA00");
		}else if(fcChartType == "HeatMap"){
			org.w3c.dom.Element categories = document.createElement("categories");
			rootElement.appendChild(categories);
			ArrayList<List<org.w3c.dom.Element>> serAC = new ArrayList<List<org.w3c.dom.Element>>();
			ArrayList<org.w3c.dom.Element> catAC = new ArrayList<org.w3c.dom.Element>();
			Boolean firstLoop = true;
			ArrayList<Double> values = new ArrayList<Double>();
			ArrayList<org.w3c.dom.Element> colAC = new ArrayList<org.w3c.dom.Element>();
			org.w3c.dom.Element dataset1 = document.createElement("dataset");
			ArrayList tmpValueList = new ArrayList();
			for(int k = 1;k < columnHeaders.size()-1;k++){
				for(int cnt=0;cnt< reportsWriterVb.getReportsData().size();cnt++){
					org.w3c.dom.Element set = document.createElement("set");
					ColumnHeadersVb colTmpVb = columnHeaders.get(k); 
					set.setAttribute("rowid",colTmpVb.getCaption());
					ReportStgVb objRData2 = reportsWriterVb.getReportsData().get(cnt);
					if("L1".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
						set.setAttribute("columnid", objRData2.getCaptionColumn1());
					}else if("L2".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
						set.setAttribute("columnid", objRData2.getCaptionColumn2());
					}else if( "L3".equalsIgnoreCase(reportsWriterVb.getDashboards().getAxisY())){
						set.setAttribute("columnid", objRData2.getCaptionColumn3());
					}else{
						set.setAttribute("columnid", reportsWriterVb.getColumnHeaders().get(cnt-1+reportsWriterVb.getLabelColCount()).getCaption());
					}
					String value = findValue(objRData2, k);
					value = value.replaceAll(",", "");
					
					tmpValueList.add(value);
					if(!ValidationUtil.isValid(value)){
						value="0.00";
					}
					set.setAttribute("value",Math.abs(Double.parseDouble(value))+"");
					dataset1.appendChild(set);
				}
			}
			
			float max = 0f;
			for(int i=0; i < tmpValueList.size(); i++){
		      Object object = tmpValueList.get(i);
		      float k = Float.parseFloat(object.toString());
			  if(k > max){
		            max = k;
		      }
		    }
			float maxValueRange = max/4;
			float maxValueRange1 = maxValueRange + maxValueRange;
			float maxValueRange2 = maxValueRange1 + maxValueRange;
			float maxValueRange3 = maxValueRange2 + maxValueRange;
			float maxValueRange4 = maxValueRange3 + maxValueRange;
			
			Collections.sort(values);
			for(int cnt5=0;cnt5<catAC.size();cnt5++){
				org.w3c.dom.Element seriesXML1 = catAC.get(cnt5);
				for(List<org.w3c.dom.Element> tmpAC: serAC){
					org.w3c.dom.Element element = tmpAC.get(cnt5);
					String val = element.getAttribute("y");
					int idx =  values.indexOf(Double.parseDouble(val));
					element.setAttribute("z", (idx+1)+"");
					seriesXML1.appendChild(element);
				}
				rootElement.appendChild(seriesXML1);
			}
			org.w3c.dom.Element colorRange = document.createElement("colorRange");
			colorRange.setAttribute("gradient","0");
			
			org.w3c.dom.Element colorA = document.createElement("color");
			colorA.setAttribute("minValue","0");
			colorA.setAttribute("maxValue","1");
			colorA.setAttribute("code", "#E6FFFF");
			colorRange.appendChild(colorA);
			
			org.w3c.dom.Element color0 = document.createElement("color");
			color0.setAttribute("minValue","1");
			color0.setAttribute("maxValue",+maxValueRange+"");
			color0.setAttribute("code", "#66CCFF"); 
			colorRange.appendChild(color0);
			
			org.w3c.dom.Element colorB = document.createElement("color");
			colorB.setAttribute("minValue",+maxValueRange+"");
			colorB.setAttribute("maxValue",+maxValueRange1+"");
			colorB.setAttribute("code", "#FFFFCC");
			colorRange.appendChild(colorB);
			
			org.w3c.dom.Element color = document.createElement("color");
			color.setAttribute("minValue",+maxValueRange1+"");
			color.setAttribute("maxValue",+maxValueRange2+"");
			color.setAttribute("code", "#99CCFF");
			colorRange.appendChild(color);
			
			org.w3c.dom.Element color1 = document.createElement("color");
			color1.setAttribute("minValue",+maxValueRange2+"");
			color1.setAttribute("maxValue",+maxValueRange3+"");
			color1.setAttribute("code", "#4D94FF");
			colorRange.appendChild(color1);
			
			org.w3c.dom.Element color2 = document.createElement("color");
			color2.setAttribute("minValue",+maxValueRange3+"");
			color2.setAttribute("maxValue",+maxValueRange4+"");
			color2.setAttribute("code", "#FFDB94");
			colorRange.appendChild(color2);
			rootElement.appendChild(dataset1);
			rootElement.appendChild(colorRange);
			rootElement.setAttribute("formatNumber", "1");
			rootElement.setAttribute("formatNumberScale", "1");
		}
		return transformDOMtoString(document);
	}
	private static String transformDOMtoString(org.w3c.dom.Document document) throws TransformerException{
		DOMSource source = new DOMSource(document);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		transformer.transform(source, result);
		return sw.toString();
	}
	
	private static PdfPTable creataTables(ReportsWriterVb reportWriterVb,List<ColumnHeadersVb> columnHeaders,  
			List<ReportStgVb> reportsStgs, BaseFont baseFont){
		PdfPTable tabletemp = null;
		tabletemp = createTables(reportWriterVb, columnHeaders, baseFont);
		return tabletemp;
	}	
	private static PdfPTable createTables(ReportsWriterVb reportWriterVb, List<ColumnHeadersVb> columnHeaders, 
			BaseFont baseFont){
		PdfPTable table = null;
		int colNum = 0;
		int rowNum =1;
		int loopCnt = 0;
		int subColumnCnt = 0;
		Map<Integer,Float> colSizes = new HashMap<Integer, Float>();
		try{
			if(reportWriterVb.getLabelRowCount() >= 2){
				Map<String, List<String>> groupCols = new LinkedHashMap<String, List<String>>();
				for(ColumnHeadersVb columnHeadersVb1:columnHeaders){
					if((columnHeadersVb1.getLabelColNum() - reportWriterVb.getLabelColCount()) >0 &&  columnHeadersVb1.getLabelRowNum() <=1){
						//These are dataColumns which needs to be grouped.
						groupCols.put(columnHeadersVb1.getCaption(),new ArrayList<String>(3));
					}else if(columnHeadersVb1.getLabelRowNum() <=1){
						//Some reports has the caption columns as blank
						groupCols.put(columnHeadersVb1.getCaption()+"!#!#"+loopCnt,new ArrayList<String>(0));
						++colNum;
					}else{
						//Need to add these columns to the groups added above.
						int capColCnt =0;
						for(String colGrp11: groupCols.keySet()){
							if(capColCnt>=reportWriterVb.getLabelColCount()){
								groupCols.get(colGrp11).add(columnHeadersVb1.getCaption());
								++colNum;
							}
							++capColCnt;
						}
					}
					loopCnt++;
				}
				table = new PdfPTable(colNum);
				table.setWidthPercentage(100);
				table.setHeaderRows(2);
				table.setSpacingAfter(10);
				table.setSpacingBefore(10);
				Font font= new Font(baseFont);
				font.setColor(BaseColor.WHITE);
				font.setStyle(Font.BOLD);
				font.setSize(6);
				table.setSplitRows(false);
				table.setSplitLate(false);
				table.getDefaultCell().setBackgroundColor(new BaseColor(0, 92, 140));
				table.getDefaultCell().setNoWrap(true);
				loopCnt = 0;
				for(String colGrp11:groupCols.keySet()){
					colGrp11 = colGrp11.indexOf("!#!#") >=0 ? colGrp11.substring(0,colGrp11.indexOf("!#!#")):colGrp11;
					PdfPCell cell = new PdfPCell(new Phrase(colGrp11, font));
					cell.setBackgroundColor(new BaseColor(0, 92, 140));
					cell.setBorder(0|0&1|1);
					cell.setUseVariableBorders(true);
					cell.setBorderWidthTop(1);
					cell.setBorderWidthBottom(1);
					cell.setNoWrap(true);
					if(groupCols.get(colGrp11) != null && !groupCols.get(colGrp11).isEmpty()){
						cell.setColspan(groupCols.get(colGrp11).size());
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					}else{
						cell.setRowspan(2);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						colSizes.put(loopCnt, ColumnText.getWidth(cell.getPhrase()));
						loopCnt++;
					}
					//Req1uired to find the max colsize of each column.
					table.addCell(cell);
				}
				table.completeRow();
				table.getDefaultCell().setBackgroundColor(null);
				for(String colGrp11: groupCols.keySet()){
					if(groupCols.get(colGrp11) == null || groupCols.get(colGrp11).isEmpty()){
					}else{
						for(String colGrp:groupCols.get(colGrp11)){
							PdfPCell  cell = new PdfPCell(new Phrase(colGrp, font));
							cell.setBackgroundColor(new BaseColor(0, 92, 140));
							cell.setBorder(0|0&0&1);
							cell.setUseVariableBorders(true);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							//cell.setBorderWidthTop(1);
							cell.setBorderWidthBottom(1);
							cell.setNoWrap(true);
							table.addCell(cell);
							colSizes.put(loopCnt, ColumnText.getWidth(cell.getPhrase()));
							loopCnt++;
						}						
					}
				}
				for(String colGrp11: groupCols.keySet()){
					if(groupCols.get(colGrp11) == null || groupCols.get(colGrp11).isEmpty()){
					}else{
						for(String colGrp:groupCols.get(colGrp11)){
							subColumnCnt++;
						}
						break;
					}
				}
			}else{
				table = new PdfPTable(columnHeaders.size());
				table.setWidthPercentage(99);
				table.setHeaderRows(1);
				table.setSpacingAfter(10);
				table.setSpacingBefore(10);
				Font font= new Font(baseFont);
				font.setColor(BaseColor.WHITE);
				font.setStyle(Font.BOLD);
				font.setSize(6);
				table.getDefaultCell().setBackgroundColor(new BaseColor(0, 92, 140));
				table.getDefaultCell().setNoWrap(true);
				loopCnt = 0;
				for(ColumnHeadersVb columnHeadersVb:columnHeaders){
					PdfPCell cell = new PdfPCell(new Phrase(columnHeadersVb.getCaption(), font));
					cell.setBackgroundColor(new BaseColor(0, 92, 140));
					cell.setBorder(0|0&1|1);
					cell.setUseVariableBorders(true);
					String type = (reportWriterVb.getLabelColCount() - (loopCnt+1)) >= 0 ? CAP_COL : DATA_COL;
					if(CAP_COL.equalsIgnoreCase(type)){
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					}else{
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					}
					cell.setBorderWidthTop(1);
					cell.setBorderWidthBottom(1);
					cell.setNoWrap(true);
					table.addCell(cell);
					++colNum;
					if(columnHeadersVb.getCaption() == null || columnHeadersVb.getCaption().trim().isEmpty()){
						colSizes.put(loopCnt, 20f);
					}else{
						colSizes.put(loopCnt, ColumnText.getWidth(cell.getPhrase()));
					}
					
					loopCnt++;
				}
				table.getDefaultCell().setBackgroundColor(null);
			}
			PdfPCell cell = null;
			for(ReportStgVb reportStgVb:reportWriterVb.getReportsData()){
				for(int loopCount =0; loopCount < colNum; loopCount++){
					int index = 0;
					String type = (reportWriterVb.getLabelColCount() - (loopCount+1)) >= 0 ? CAP_COL : DATA_COL;
					String cellText = "";
					boolean isBold = false;
					if(type.equalsIgnoreCase(CAP_COL)){
						index = (loopCount+1);
						cellText = findValue(reportStgVb, type, index);
						switch(getStyle(reportStgVb,rowNum)){
							case 1:{Font font= new Font(baseFont);//Summary with Background
									font.setStyle(Font.BOLD);
									isBold = true;
									font.setSize(5);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0|0&1|1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(1);
									cell.setBorderWidthTop(1);
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							case 2:{Font font= new Font(baseFont);//Non Summary with background.
									font.setSize(5);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(1);
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							case 3:{Font font= new Font(baseFont);//Summary with out Background
									font.setSize(5);
									font.setStyle(Font.BOLD);
									isBold = true;
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0 & 0 & 1 & 1);
									cell.setBorderWidthTop(1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(1);
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							default:{Font font= new Font(baseFont);//Non Summary With out background
									font.setSize(5);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0 & 0 & 0 & 1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(1);
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
						}
					}else{
						index = ((loopCount+1) - reportWriterVb.getLabelColCount());
						cellText = findValue(reportStgVb, type, index);
						switch(getStyle(reportStgVb,rowNum)){
							case 1:{Font font= new Font(baseFont);
								font.setStyle(Font.BOLD);
								isBold = true;
								font.setSize(5);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0& 0 & 1|1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(1);
								cell.setBorderWidthTop(1);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setBackgroundColor(new BaseColor(205, 226, 236));
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						case 2:{Font font= new Font(baseFont);
								font.setSize(5);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0& 0 & 1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(1);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setBorderColorBottom(new BaseColor(230,184,183));
								cell.setBackgroundColor(new BaseColor(205, 226, 236));
								//cell.setPaddingLeft(0);
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						case 3:{Font font= new Font(baseFont);
								font.setSize(5);
								font.setStyle(Font.BOLD);
								isBold = true;
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0 & 0 & 1 | 1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(1);
								cell.setBorderWidthTop(1);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell); 
								break;}
						default:{Font font= new Font(baseFont);
								font.setSize(5);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0 & 0 & 0 & 1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(1);
								cell.setBorderColorBottom(new BaseColor(230,184,183));
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						}
					}
					if(cellText == null || cellText.trim().isEmpty()){
						colSizes.put(loopCount, Math.max(20, colSizes.get(loopCount)));
					}else{
						float width = Math.max(ColumnText.getWidth(cell.getPhrase()), colSizes.get(loopCount));
	/*						if(cellText.startsWith("11 Dummy")){
							String name = "";
	
						}*/
						//if(isBold) width += cellText.length() * 0.5;
						if(width<20) width = 20;
						colSizes.put(loopCount, width);
					}
				}
				rowNum++;
			}
			float sumOfLabelColWidth = 0f;
			float totalWidth = 0f;
			float[] colsArry = new float[colSizes.size()];
			for(int i=0;i<colSizes.size();i++){
				totalWidth += colSizes.get(i);
				colsArry[i] = colSizes.get(i);
				if(i < reportWriterVb.getLabelColCount()){
					sumOfLabelColWidth += colSizes.get(i);
				}
			}
			table.setTotalWidth(totalWidth);
			table.setWidths(colsArry);
		} // try close
		catch (Exception e) {
		}
		return table;
	}
	private static String findValue(ReportStgVb reportsStgs, int index){
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
		return "";
	}
	public static ExceptionCode exportListReportToPdf(List<ColumnHeadersVb> columnHeaders, 
			String xmlData, ReportsWriterVb reportsWriterVb, List<PromptIdsVb> prompts, String assetFolderUrl, String burstSequenceNo, String scheduleSequenceNo, List<String> colTyps){
		Document document =  null;
		PdfPTable table = null;
		Rectangle rectangle = new Rectangle(reportsWriterVb.getPdfWidth(), reportsWriterVb.getPdfHeight());
		document = new Document(rectangle, 20, 20, 20, 20);
		/*if("L".equalsIgnoreCase(reportsWriterVb.getOrientation())){
			document = new Document(rectangle, 20, 20, 20, 20);
		}else{
			document = new Document(rectangle, 20, 20, 20, 20);
		}*/
		try {
			String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			//System.out.print("File Path:"+ filePath);
			File lFile = null;
			if(ValidationUtil.isValid(burstSequenceNo) && ValidationUtil.isValid(scheduleSequenceNo)){
				lFile = new File(filePath+ValidationUtil.encode(reportsWriterVb.getReportTitle())+"_"+reportsWriterVb.getVerifier()+"_"+burstSequenceNo+"_"+scheduleSequenceNo+".pdf");				
			}else{
				lFile = new File(filePath+ValidationUtil.encode(reportsWriterVb.getReportTitle())+"_"+reportsWriterVb.getVerifier()+".pdf");
			}
			if(lFile.exists()){
				lFile.delete();
			}
			BaseFont baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
			lFile.createNewFile();
			FileOutputStream fips =  new FileOutputStream(lFile);
			PdfWriter writer = PdfWriter.getInstance(document,fips);
			InvPdfPageEventHelper invPdfEvt = new InvPdfPageEventHelper(null, reportsWriterVb.getMakerName(), assetFolderUrl, prompts, reportsWriterVb);
			writer.setPageEvent(invPdfEvt);
			document.open();
			int colNum = 0;
			int rowNum =1;
			int loopCnt = 0;
			int subColumnCnt = 0;
			boolean isgroupColumns = false;
			Map<Integer,Float> colSizes = new HashMap<Integer, Float>();
			// The Header Table creation
			if(columnHeaders != null){
				table = new PdfPTable(reportsWriterVb.getLabelColCount());
				table.setWidthPercentage(100);
				table.setHeaderRows(reportsWriterVb.getLabelRowCount());
				table.setSpacingAfter(10);
				table.setSpacingBefore(10);
				Font font= new Font(baseFont);
				font.setColor(BaseColor.WHITE);
				font.setStyle(Font.BOLD);
				font.setSize(7);
				if(reportsWriterVb.getLabelRowCount()!=1){
					table.setSplitRows(false);
					table.setSplitLate(false);
				}
				table.getDefaultCell().setBackgroundColor(new BaseColor(79, 98, 40));
				//table.getDefaultCell().setBackgroundColor(new BaseColor(0, 92, 140));
				table.getDefaultCell().setNoWrap(true);
				loopCnt = 0;
				boolean isNextRow = false;
				for(ColumnHeadersVb columnHeadersVb:columnHeaders){
					if(columnHeadersVb.getCaption().contains("<br/>")){
						String value=columnHeadersVb.getCaption().replaceAll("<br/>","\n");
						//value =  "\""+value+"\"";
						columnHeadersVb.setCaption(value);
					}
					PdfPCell cell = new PdfPCell(new Phrase(columnHeadersVb.getCaption(), font));
					cell.setBackgroundColor(new BaseColor(0, 92, 140));
					//cell.setBorder(0|0&1|1);
					cell.setUseVariableBorders(true);
					cell.setBorderWidthTop(1);
					cell.setBorderWidthBottom(0.5f);
					cell.setNoWrap(true);
					if(loopCnt == (reportsWriterVb.getLabelColCount()-1)){
						cell.setBorderWidthRight(1f);
					}
					cell.setBorderWidthTop(1);
					cell.setBorderWidthBottom(1f);
					String type = CAP_COL;
					if(loopCnt <reportsWriterVb.getCaptionLabelColCount())
						type = CAP_COL;
					else
						type = DATA_COL;
					if(CAP_COL.equalsIgnoreCase(type)){
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					}else{
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					}
					if(columnHeadersVb.getRowSpanNum()!=0){
						cell.setRowspan(columnHeadersVb.getRowSpanNum());
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					}
					if(columnHeadersVb.getColSpanNum()!=0)
						cell.setColspan(columnHeadersVb.getColSpanNum());
					cell.setNoWrap(false);
					if(columnHeadersVb.getLabelRowNum()>1 && !isNextRow){
						table.completeRow();
						table.getDefaultCell().setBackgroundColor(null);
						isNextRow = true;
					}
					table.addCell(cell);
					++colNum;
					if(columnHeadersVb.getColSpanNum()==0){
						/*if(reportsWriterVb.getCaptionLabelColCount() == 3){
							if(loopCnt < 2){
								colSizes.put(loopCnt, 40f);
							}else if(loopCnt == 2){
								colSizes.put(loopCnt, 200f);
							}else{
								colSizes.put(loopCnt, 47f);
							}
						}else{
							if(loopCnt == 0){
								colSizes.put(loopCnt, 40f);
							}else if(loopCnt == 1){
								colSizes.put(loopCnt, 200f);
							}else{
								colSizes.put(loopCnt, 47f);
							}
						}*/
						//colSizes.put(loopCnt, ColumnText.getWidth(cell.getPhrase()));
						colSizes.put(columnHeadersVb.getLabelColNum()-1, ColumnText.getWidth(cell.getPhrase()));
						loopCnt++;
					}
				}
			}
			//table.completeRow();
			//table.getDefaultCell().setBackgroundColor(null);
			// Writing The Header
			table.completeRow();
			table.getDefaultCell().setBackgroundColor(null);
			PdfPCell cell = null;
			int recCount = 0;
			// Writingthe data
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xmlData.getBytes(Charset.forName("UTF-8")));
			//parse using builder to get DOM representation of the XML file
			org.w3c.dom.Document dom = db.parse(is);
			org.w3c.dom.Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("tableRow");
			if(nl != null && nl.getLength() > 0) {
				for(int loopCntNew = 0 ; loopCntNew < nl.getLength();loopCntNew++) {
//					row = sheet.createRow(rowNum);
					//get the employee element
					org.w3c.dom.Element el = (org.w3c.dom.Element)nl.item(loopCntNew);
					int internalStatus = 0;
					try{
						NodeList internalStatusNode = el.getElementsByTagName("INTERNAL_STATUS");
						internalStatus = Integer.parseInt(internalStatusNode.item(0).getFirstChild().getTextContent());
					}catch(NullPointerException npe){
						internalStatus = 0;
					}catch(Exception e){
						internalStatus = 0;
					}
					NodeList ncl  = el.getChildNodes();
					NodeList formatTypeNodes = el.getElementsByTagName("formatType");
					NodeList headerTextNodes = el.getElementsByTagName("headerText"); 

					String formatType = "";
					String headerText = "";
					if(headerTextNodes != null && headerTextNodes.item(0) != null)
						headerText = headerTextNodes.item(0) == null || headerTextNodes.item(0).getFirstChild() == null? "":headerTextNodes.item(0).getFirstChild().getNodeValue();
					if(formatTypeNodes != null && formatTypeNodes.item(0) != null)
						formatType = formatTypeNodes.item(0) == null || formatTypeNodes.item(0).getFirstChild() == null? "":formatTypeNodes.item(0).getFirstChild().getNodeValue();
				
/*					if("HT".equalsIgnoreCase(formatType) || "FHT".equalsIgnoreCase(formatType)){
						int colNum =0;
						//Plain Data to be displayed.
						for(ColumnHeadersVb columnHeadersVb:columnHeaders){
							cell = row.createCell(colNum);
							String caption = ""; 
							if(colNum == 0){
								caption = headerText == null ? " ": headerText;	
							}
							cell.setCellValue(caption.replaceAll("_", " "));
							cell.setCellStyle(styls.get(CELL_STYLE_MID_HEADER_CAP_COL));
							columnWidths.put(colNum,(int)getColumnWidth(cell, (styls.get(CELL_STYLE_MID_HEADER_CAP_COL)).getFont(), true, columnWidths.get(colNum)));
							++colNum;
						}
						rowNum = rowNum + 1;
						row = sheet.createRow(rowNum);
					}*/
				   
					/*if("H".equalsIgnoreCase(formatType) || "HT".equalsIgnoreCase(formatType) || "FHT".equalsIgnoreCase(formatType)){
						writeHeadersForListData(reportsWriterVb, sheet, columnHeaders, styls, rowNum, columnWidths);
						//rowNum = rowNum + 1;
						//row = sheet.createRow(rowNum);
					}*/
					
					for(int loopCount=0;loopCount<ncl.getLength();loopCount++){
						//cell = row.createCell(loopCount);
//						Font font = null;
						org.w3c.dom.Element el1= (org.w3c.dom.Element)ncl.item(loopCount);
						if(!"INTERNAL_STATUS".equalsIgnoreCase(el1.getTagName())
								&& !("EFFECTIVE_DATE".equalsIgnoreCase(el1.getTagName()) && internalStatus==0)){
							if(el1 != null && "formatType".equalsIgnoreCase(el1.getNodeName())){
								continue; 
							}
							if(el1 != null && "headerText".equalsIgnoreCase(el1.getNodeName())){
								continue; 
							}
							
							String cellValue = el1 != null && el1.getFirstChild() != null? ValidationUtil.replaceComma(el1.getFirstChild().getNodeValue().trim()):"";
							String columnType =colTyps.get(loopCount);
							switch(getStyle(formatType,rowNum)){
								case 1:{Font font= new Font(baseFont);
									font.setStyle(Font.BOLD);
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellValue, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									//cell.setBorderWidthTop(1);
									if("N".equalsIgnoreCase(columnType))
										cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setPaddingLeft(3);
									cell.setPaddingRight(3);
									
									if(recCount ==ncl.getLength()-1){
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
									}
									if(loopCount==0){
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(230,184,183));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else{
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
								case 2:{Font font= new Font(baseFont);
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellValue, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									if("N".equalsIgnoreCase(columnType))
										cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setBorderColor(new BaseColor(230,184,183));
									//cell.setPaddingLeft(0);
									
									if(recCount ==ncl.getLength()-1){
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
									}
									if(loopCount==0){
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(230,184,183));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else{
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}
									cell.setPaddingLeft(3);
									cell.setPaddingRight(3);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
								case 3:{Font font= new Font(baseFont);
									font.setSize(7);
									font.setStyle(Font.BOLD);
									cell = new PdfPCell(new Phrase(cellValue, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									//cell.setBorderWidthTop(1);
									if("N".equalsIgnoreCase(columnType))
										cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setPaddingLeft(3);
									cell.setPaddingRight(3);
									
									if(recCount ==ncl.getLength()-1){
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
									}
									if(loopCount==0){
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(230,184,183));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else{
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}
									cell.setNoWrap(true);
									table.addCell(cell); 
									break;}
								default:{Font font= new Font(baseFont);
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellValue, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setBorderColor(new BaseColor(230,184,183));
									if("N".equalsIgnoreCase(columnType))
										cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									
									if(recCount ==ncl.getLength()-1){
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
									}
									if(loopCount==0){
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(230,184,183));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else{
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}
									cell.setPaddingLeft(3);
									cell.setPaddingRight(3);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
								}
							
							
							if(cellValue == null || cellValue.trim().isEmpty()){
								//colSizes.put(loopCount, Math.max(20, colSizes.get(loopCount)));
							}else{
								
								float width = Math.max(ColumnText.getWidth(cell.getPhrase()), colSizes.get(loopCount));
								if(width<20) width = 20;
									colSizes.put(loopCount, width);
							}
						}
					}
					rowNum++;
				}
			}
			
			/*
			for(ReportStgVb reportStgVb:reportsStgs){
				boolean highlight = false;
				if((Integer.parseInt(reportStgVb.getBoldFlag()) == 9 || Integer.parseInt(reportStgVb.getBoldFlag()) == 2 || Integer.parseInt(reportStgVb.getBoldFlag()) == 1)){
					highlight= true;
				}
				for(int loopCount =0; loopCount < reportsWriterVb.getLabelColCount(); loopCount++){
					int index = 0;
					String type = "";
					if(loopCount<reportsWriterVb.getCaptionLabelColCount()){
						type = CAP_COL;
					}else{
						type = DATA_COL;
					}
					String cellText = "";
					if(type.equalsIgnoreCase(CAP_COL)){
						index = (loopCount+1);
						cellText = findValue(reportStgVb, type, index);
						switch(getStyle(reportStgVb,rowNum)){
							case 1:{Font font= new Font(baseFont);//Summary with Background
									font.setStyle(Font.BOLD);
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									//cell.setBorderWidthTop(1);
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							case 2:{Font font= new Font(baseFont);//Non Summary with background.
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							case 3:{Font font= new Font(baseFont);//Summary with out Background
									font.setSize(7);
									font.setStyle(Font.BOLD);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									//cell.setBorderWidthTop(1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							default:{Font font= new Font(baseFont);//Non Summary With out background
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
						}
					}else{
						index = ((loopCount+1) - reportsWriterVb.getCaptionLabelColCount());
						cellText = findValue(reportStgVb, type, index);
						switch(getStyle(reportStgVb,rowNum)){
							case 1:{Font font= new Font(baseFont);
								font.setStyle(Font.BOLD);
								font.setSize(7);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0&0&1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(0.5f);
								//cell.setBorderWidthTop(1);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setBackgroundColor(new BaseColor(205, 226, 236));
								cell.setBorderColor(new BaseColor(230,184,183));
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						case 2:{Font font= new Font(baseFont);
								font.setSize(7);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0&0&1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(0.5f);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setBorderColorBottom(new BaseColor(230,184,183));
								cell.setBackgroundColor(new BaseColor(205, 226, 236));
								cell.setBorderColor(new BaseColor(230,184,183));
								//cell.setPaddingLeft(0);
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						case 3:{Font font= new Font(baseFont);
								font.setSize(7);
								font.setStyle(Font.BOLD);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0&0&1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(0.5f);
								//cell.setBorderWidthTop(1);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setBorderColor(new BaseColor(230,184,183));
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell); 
								break;}
						default:{Font font= new Font(baseFont);
								font.setSize(7);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0&0&1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(0.5f);
								cell.setBorderColorBottom(new BaseColor(230,184,183));
								cell.setBorderColor(new BaseColor(230,184,183));
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						}
					}
					if(cellText == null || cellText.trim().isEmpty()){
						//colSizes.put(loopCount, Math.max(20, colSizes.get(loopCount)));
					}else{
						
						float width = Math.max(ColumnText.getWidth(cell.getPhrase()), colSizes.get(loopCount));
						if(width<20) width = 20;
							colSizes.put(loopCount, width);
					}					
				}
				recCount++;
			}*/
			float totalWidth = 0f;
			float sumOfLabelColWidth = 0f;
			float[] colsArry = new float[colSizes.size()];
			for(int i=0;i<colSizes.size();i++){
				totalWidth += colSizes.get(i);
				colsArry[i] = colSizes.get(i);
				if(i < reportsWriterVb.getLabelColCount()){
					sumOfLabelColWidth += colSizes.get(i);
				}
			}
			//float pageWidth = "L".equalsIgnoreCase(reportsWriterVb.getOrientation()) ?  PageSize.A4_LANDSCAPE.rotate().getWidth() : PageSize.A4.getWidth();
			float pageWidth = reportsWriterVb.getPdfWidth();
			pageWidth = (pageWidth -  (document.leftMargin() + document.rightMargin()));
			if(totalWidth < pageWidth){
				float percentWidth= (float) ((totalWidth*100) /( pageWidth - 200));
				if(percentWidth>99) percentWidth = 99;
				int rowStart = 0;
				int rowEnd = table.getRows().size();
				table.setTotalWidth(totalWidth);
				table.setWidths(colsArry);
				float totalHeight = table.calculateHeights();
				float pageHeight =  reportsWriterVb.getPdfHeight() - (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight + table.spacingAfter());
				//PageSize.A4_LANDSCAPE.rotate().getHeight() =595,document.topMargin()=20,document.bottomMargin()=20, invPdfEvt.headerHeight=45, table.spacingAfter()=10
				
				int pages = 1;
				//If the page height is in fractions then we need to consider + 1 for the Page. 
				if(totalHeight > pageHeight){
					pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
					totalHeight += pages * table.getHeaderHeight() ;
					pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
				}
				int totalPages = pages;
				rowEnd = getRowsPerPage(table,rowStart,pageHeight);
				while(pages>0){
					List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
					PdfPTable tempTable = createTable(table,pdfpRows,reportsWriterVb.getLabelColCount(),table.getNumberOfColumns(),pages,totalPages,reportsWriterVb.getLabelColCount());
					tempTable.setTotalWidth(totalWidth);
					tempTable.setWidthPercentage(percentWidth);
					tempTable.setWidths(colsArry);
					//tempTable.setLockedWidth(true);
					document.add(tempTable);
					rowStart = rowEnd;
					rowEnd = getRowsPerPage(table,rowStart,pageHeight);
					pages--;
					if(pages-1 != 0)
						rowEnd = rowEnd - table.getHeaderRows();
					if(pages>0)
						document.newPage();
				}
				//document.add(table);
			}else{
				//int pages = Math.max(1,reportsStgs.size()%rowsPerPage==0?reportsStgs.size()/rowsPerPage:(reportsStgs.size()/rowsPerPage)+1);
				int rowStart = 0;
				int rowEnd = table.getRows().size();
				table.setTotalWidth(totalWidth);
				table.setWidths(colsArry);
				float totalHeight = table.calculateHeights();
				float pageHeight =  reportsWriterVb.getPdfHeight() - (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight);
				int pages = 1;
				//If the page height is in fractions then we need to consider + 1 for the Page.
				if(totalHeight > pageHeight){
					pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
				}
				int totalPages = pages;
				while(pages>0){
					int colStart = reportsWriterVb.getLabelColCount();
					int colEnd = colSizes.size();
					int colCount = reportsWriterVb.getLabelColCount();
					float labDatColWidths = sumOfLabelColWidth;
					rowEnd = getRowsPerPage(table,rowStart,pageHeight);
					//if(totalPages!=pages) rowEnd = rowEnd - table.getHeaderRows();
					List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
					boolean newPageAdded = false;
					int i=reportsWriterVb.getLabelColCount();
					while(i<colSizes.size()){
						labDatColWidths +=  colSizes.get(i);
						if(labDatColWidths >  pageWidth){
							if(!isgroupColumns){
								//If non group reports then remove the 3 columns to adjust to the screen  
								colEnd = i-1;
								i = colEnd;
							}else{
								colEnd = i-1;
							}
							PdfPTable tempTable = createTable(table,pdfpRows,colStart,colEnd,pages,totalPages,reportsWriterVb.getLabelColCount());
							float selecteColWidth = sumOfLabelColWidth;
							for(int j = colStart; j<colEnd; j++){
								selecteColWidth = selecteColWidth+ colsArry[j];
							}
							float percentWidth= (float) ((selecteColWidth*100) /( pageWidth - 200));
							if(percentWidth>99) percentWidth = 99;
							tempTable.setTotalWidth(selecteColWidth);
							tempTable.setWidthPercentage(percentWidth);
							float array[] = Arrays.copyOfRange(colsArry, 0, reportsWriterVb.getLabelColCount());
							tempTable.setWidths(mergeArrays(array,Arrays.copyOfRange(colsArry, colStart, colEnd)));
							colCount = reportsWriterVb.getLabelColCount();
							labDatColWidths = sumOfLabelColWidth + colSizes.get(i);
							colStart = colEnd;
							document.add(tempTable);
							document.newPage();
							newPageAdded = true;
						}
						i++;
						colCount++;
					}
					if(!newPageAdded)
						document.newPage();
					PdfPTable tempTable = createTable(table,pdfpRows,colStart,colSizes.size(),pages,totalPages,reportsWriterVb.getLabelColCount());
					float sumOfRemainingCols = getSumOfWidths(colSizes,colStart,colSizes.size(), reportsWriterVb.getLabelColCount());
					tempTable.setTotalWidth(sumOfRemainingCols + 20);
					
					float percentWidth= (float) ((sumOfRemainingCols*100) /( pageWidth - 200));
					if(percentWidth>99) percentWidth = 99;
					tempTable.setWidthPercentage(percentWidth);
					
					float array[] = Arrays.copyOfRange(colsArry, 0, reportsWriterVb.getLabelColCount());
					tempTable.setWidths(mergeArrays(array,Arrays.copyOfRange(colsArry, colStart, colSizes.size())));
					
				//	tempTable.setLockedWidth(true);
					//tempTable.setHorizontalAlignment(Element.ALIGN_MIDDLE);
					document.add(tempTable);
					//tempTable.writeSelectedRows(0, -1, 40, 100, writer.getDirectContent());
					rowStart = rowEnd;
					pages--;
					if(pages>0)
						document.newPage();
				}
			}
			document.close();
			writer.close();
		}catch (Exception e) {
			throw new RuntimeCustomException(e.getMessage());
		}
		return CommonUtils.getResultObject("", 1, "", "");
	}
	private static int getStyle(String formatType, int rowNum){
		if((rowNum %2) == 0 && ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))){
			return 1; //Summary with Background
		}else if((rowNum %2) == 0){
			return 2; //Non Summary with background.
		}else if("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)){
			return 3;//Summary with out Background
		}else{
			return 4;//Non Summary With out background
		}
	}
	public static ExceptionCode exportToPdfOld(List<ColumnHeadersVb> columnHeaders, 
			List<ReportStgVb> reportsStgs, ReportsWriterVb reportWriterVb, List<PromptIdsVb> prompts, String assetFolderUrl, String burstSequenceNo, String scheduleSequenceNo){
		Document document =  null;
		PdfPTable table = null;
		Rectangle rectangle = new Rectangle(reportWriterVb.getPdfWidth(), reportWriterVb.getPdfHeight());
		document = new Document(rectangle, 20, 20, 20, 20);
		/*if("L".equalsIgnoreCase(reportWriterVb.getOrientation())){
			document = new Document(rectangle, 20, 20, 20, 20);
		}else{
			document = new Document(rectangle, 20, 20, 20, 20);
		}*/
		try {
			String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			File lFile = null;
			if(ValidationUtil.isValid(burstSequenceNo) && ValidationUtil.isValid(scheduleSequenceNo)){
				lFile = new File(filePath+ValidationUtil.encode(reportWriterVb.getReportTitle())+"_"+reportWriterVb.getVerifier()+"_"+burstSequenceNo+"_"+scheduleSequenceNo+".pdf");				
			}else{
				lFile = new File(filePath+ValidationUtil.encode(reportWriterVb.getReportTitle())+"_"+reportWriterVb.getVerifier()+".pdf");
			}
			if(lFile.exists()){
				lFile.delete();
			}
			BaseFont baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
			lFile.createNewFile();
			FileOutputStream fips =  new FileOutputStream(lFile);
			PdfWriter writer = PdfWriter.getInstance(document,fips);
			InvPdfPageEventHelper invPdfEvt = new InvPdfPageEventHelper(null, reportWriterVb.getMakerName(), assetFolderUrl, prompts, reportWriterVb);
			writer.setPageEvent(invPdfEvt);
			document.open();
			int colNum = 0;
			int rowNum =1;
			int loopCnt = 0;
			int subColumnCnt = 0;
			boolean isgroupColumns = false;
			Map<Integer,Float> colSizes = new HashMap<Integer, Float>();
			if(reportWriterVb.getLabelRowCount() >= 2){
				Map<String, List<String>> groupCols = new LinkedHashMap<String, List<String>>();
				isgroupColumns = true;
				for(ColumnHeadersVb columnHeadersVb1:columnHeaders){
					if((columnHeadersVb1.getLabelColNum() - reportWriterVb.getLabelColCount()) >0 &&  columnHeadersVb1.getLabelRowNum() <=1){
						//These are dataColumns which needs to be grouped.
						groupCols.put(columnHeadersVb1.getCaption(),new ArrayList<String>(3));
					}else if(columnHeadersVb1.getLabelRowNum() <=1){
						//Some reports has the caption columns as blank
						groupCols.put(columnHeadersVb1.getCaption()+"!#!#"+loopCnt,new ArrayList<String>(0));
						++colNum;
					}else{
						//Need to add these columns to the groups added above.
						int capColCnt =0;
						for(String colGrp11: groupCols.keySet()){
							if(capColCnt>=reportWriterVb.getLabelColCount()){
								groupCols.get(colGrp11).add(columnHeadersVb1.getCaption());
								++colNum;
							}
							++capColCnt;
						}
					}
					loopCnt++;
				}
				table = new PdfPTable(colNum);
				table.setWidthPercentage(100);
				table.setHeaderRows(2);
				table.setSpacingAfter(10);
				table.setSpacingBefore(10);
				Font font= new Font(baseFont);
				font.setColor(BaseColor.WHITE);
				font.setStyle(Font.BOLD);
				font.setSize(7);
				table.setSplitRows(false);
				table.setSplitLate(false);
				table.getDefaultCell().setBackgroundColor(new BaseColor(0, 92, 140));
				table.getDefaultCell().setNoWrap(true);
				loopCnt = 0;
				for(String colGrp11:groupCols.keySet()){
					colGrp11 = colGrp11.indexOf("!#!#") >=0 ? colGrp11.substring(0,colGrp11.indexOf("!#!#")):colGrp11;
					PdfPCell cell = new PdfPCell(new Phrase(colGrp11, font));
					cell.setBackgroundColor(new BaseColor(0, 92, 140));
					cell.setBorder(0|0&1|1);
					cell.setUseVariableBorders(true);
					cell.setBorderWidthTop(1);
					cell.setBorderWidthBottom(0.5f);
					cell.setNoWrap(true);
					if(groupCols.get(colGrp11) != null && !groupCols.get(colGrp11).isEmpty()){
						cell.setColspan(groupCols.get(colGrp11).size());
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					}else{
						cell.setRowspan(2);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						colSizes.put(loopCnt, ColumnText.getWidth(cell.getPhrase()));
						loopCnt++;
					}
					//Req1uired to find the max colsize of each column.
					table.addCell(cell);
				}
				table.completeRow();
				table.getDefaultCell().setBackgroundColor(null);
				for(String colGrp11: groupCols.keySet()){
					if(groupCols.get(colGrp11) == null || groupCols.get(colGrp11).isEmpty()){
					}else{
						for(String colGrp:groupCols.get(colGrp11)){
							PdfPCell  cell = new PdfPCell(new Phrase(colGrp, font));
							cell.setBackgroundColor(new BaseColor(0, 92, 140));
							cell.setBorder(0|0&0&1);
							cell.setUseVariableBorders(true);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setBorderWidthTop(1);
							cell.setBorderWidthBottom(0.5f);
							cell.setNoWrap(true);
							table.addCell(cell);
							colSizes.put(loopCnt, ColumnText.getWidth(cell.getPhrase()));
							loopCnt++;
						}						
					}
				}
				for(String colGrp11: groupCols.keySet()){
					if(groupCols.get(colGrp11) == null || groupCols.get(colGrp11).isEmpty()){
					}else{
						if(groupCols.get(colGrp11) != null){
							subColumnCnt += groupCols.get(colGrp11).size();
						}
						break;
					}
				}
			}else{
				table = new PdfPTable(columnHeaders.size());
				table.setWidthPercentage(99);
				table.setHeaderRows(1);
				table.setSpacingAfter(10);
				table.setSpacingBefore(10);
				Font font= new Font(baseFont);
				font.setColor(BaseColor.WHITE);
				font.setStyle(Font.BOLD);
				font.setSize(7);
				table.getDefaultCell().setBackgroundColor(new BaseColor(0, 92, 140));
				table.getDefaultCell().setNoWrap(true);
				loopCnt = 0;
				for(ColumnHeadersVb columnHeadersVb:columnHeaders){
					PdfPCell cell = new PdfPCell(new Phrase(columnHeadersVb.getCaption(), font));
					cell.setBackgroundColor(new BaseColor(0, 92, 140));
					cell.setBorderColor(new BaseColor(230,184,183));
					cell.setBorder(0|0&1|1);
					cell.setUseVariableBorders(true);
					String type = (reportWriterVb.getLabelColCount() - (loopCnt+1)) >= 0 ? CAP_COL : DATA_COL;
					if(CAP_COL.equalsIgnoreCase(type)){
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					}else{
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					}
					cell.setBorderWidthTop(1);
					cell.setBorderWidthBottom(0.5f);
					cell.setNoWrap(true);
					table.addCell(cell);
					++colNum;
					if(columnHeadersVb.getCaption() == null || columnHeadersVb.getCaption().trim().isEmpty()){
						colSizes.put(loopCnt, 20f);
					}else{
						colSizes.put(loopCnt, ColumnText.getWidth(cell.getPhrase()));
					}
					
					loopCnt++;
				}
				table.getDefaultCell().setBackgroundColor(null);
			}
			PdfPCell cell = null;
			for(ReportStgVb reportStgVb:reportsStgs){
				for(int loopCount =0; loopCount < colNum; loopCount++){
					int index = 0;
					String type = (reportWriterVb.getLabelColCount() - (loopCount+1)) >= 0 ? CAP_COL : DATA_COL;
					String cellText = "";
					if(type.equalsIgnoreCase(CAP_COL)){
						index = (loopCount+1);
						cellText = findValue(reportStgVb, type, index);
						switch(getStyle(reportStgVb,rowNum)){
							case 1:{Font font= new Font(baseFont);//Summary with Background
									font.setStyle(Font.BOLD);
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									//cell.setBorderWidthTop(1);
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							case 2:{Font font= new Font(baseFont);//Non Summary with background.
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							case 3:{Font font= new Font(baseFont);//Summary with out Background
									font.setSize(7);
									font.setStyle(Font.BOLD);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									//cell.setBorderWidthTop(1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							default:{Font font= new Font(baseFont);//Non Summary With out background
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
						}
					}else{
						index = ((loopCount+1) - reportWriterVb.getLabelColCount());
						cellText = findValue(reportStgVb, type, index);
						switch(getStyle(reportStgVb,rowNum)){
							case 1:{Font font= new Font(baseFont);
								font.setStyle(Font.BOLD);
								font.setSize(7);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0&0&1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(0.5f);
								//cell.setBorderWidthTop(1);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setBackgroundColor(new BaseColor(205, 226, 236));
								cell.setBorderColor(new BaseColor(230,184,183));
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						case 2:{Font font= new Font(baseFont);
								font.setSize(7);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0&0&1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(0.5f);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setBorderColorBottom(new BaseColor(230,184,183));
								cell.setBackgroundColor(new BaseColor(205, 226, 236));
								cell.setBorderColor(new BaseColor(230,184,183));
								//cell.setPaddingLeft(0);
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						case 3:{Font font= new Font(baseFont);
								font.setSize(7);
								font.setStyle(Font.BOLD);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0&0&1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(0.5f);
								//cell.setBorderWidthTop(1);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setBorderColor(new BaseColor(230,184,183));
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell); 
								break;}
						default:{Font font= new Font(baseFont);
								font.setSize(7);
								cell = new PdfPCell(new Phrase(cellText, font));
								cell.setBorder(0&0&0&1);
								cell.setUseVariableBorders(true);
								cell.setBorderWidthBottom(0.5f);
								cell.setBorderColorBottom(new BaseColor(230,184,183));
								cell.setBorderColor(new BaseColor(230,184,183));
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
								cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
								cell.setPaddingLeft(3);
								cell.setPaddingRight(3);
								cell.setNoWrap(true);
								table.addCell(cell);
								break;}
						}
					}
					if(cellText == null || cellText.trim().isEmpty()){
						colSizes.put(loopCount, Math.max(20, colSizes.get(loopCount)));
					}else{
						
						float width = Math.max(ColumnText.getWidth(cell.getPhrase()), colSizes.get(loopCount));
						if(width<20) width = 20;
						colSizes.put(loopCount, width);
					}
				}
				rowNum++;
			}
			float totalWidth = 0f;
			float sumOfLabelColWidth = 0f;
			float[] colsArry = new float[colSizes.size()];
			for(int i=0;i<colSizes.size();i++){
				totalWidth += colSizes.get(i);
				colsArry[i] = colSizes.get(i);
				if(i < reportWriterVb.getLabelColCount()){
					sumOfLabelColWidth += colSizes.get(i);
				}
			}
			//float pageWidth = "L".equalsIgnoreCase(reportWriterVb.getOrientation()) ?  PageSize.A4_LANDSCAPE.rotate().getWidth() : PageSize.A4.getWidth();
			float pageWidth = reportWriterVb.getPdfWidth();
			pageWidth = (pageWidth -  (document.leftMargin() + document.rightMargin()));
			if(totalWidth < pageWidth){
				float percentWidth= (float) ((totalWidth*100) /( pageWidth - 200));
				if(percentWidth>99) percentWidth = 99;
				int rowStart = 0;
				int rowEnd = table.getRows().size();
				table.setTotalWidth(totalWidth);
				table.setWidths(colsArry);
				float totalHeight = table.calculateHeights();
				float pageHeight =  reportWriterVb.getPdfHeight() - (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight + table.spacingAfter());
				/*float pageHeight =  ("L".equalsIgnoreCase(reportWriterVb.getOrientation()) ?  PageSize.A4_LANDSCAPE.rotate().getHeight() : PageSize.A4.getHeight()) 
						- (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight + table.spacingAfter());*/
				//PageSize.A4_LANDSCAPE.rotate().getHeight() =595,document.topMargin()=20,document.bottomMargin()=20, invPdfEvt.headerHeight=45, table.spacingAfter()=10
				
				int pages = 1;
				//If the page height is in fractions then we need to consider + 1 for the Page. 
				if(totalHeight > pageHeight){
					pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
					totalHeight += pages * table.getHeaderHeight() ;
					pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
				}
				int totalPages = pages;
				rowEnd = getRowsPerPage(table,rowStart,pageHeight);
				while(pages>0){
					List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
					PdfPTable tempTable = createTableOld(table,pdfpRows,reportWriterVb.getLabelColCount(),table.getNumberOfColumns(),pages,totalPages,reportWriterVb.getLabelColCount());
					tempTable.setTotalWidth(totalWidth);
					tempTable.setWidthPercentage(percentWidth);
					tempTable.setWidths(colsArry);
					//tempTable.setLockedWidth(true);
					document.add(tempTable);
					rowStart = rowEnd;
					rowEnd = getRowsPerPage(table,rowStart,pageHeight);
					pages--;
					/*if(pages-1 != 0)
						rowEnd = rowEnd - table.getHeaderRows();*/
					if(pages>0)
						document.newPage();
				}
				//document.add(table);
			}else{
				//int pages = Math.max(1,reportsStgs.size()%rowsPerPage==0?reportsStgs.size()/rowsPerPage:(reportsStgs.size()/rowsPerPage)+1);
				int rowStart = 0;
				int rowEnd = table.getRows().size();
				table.setTotalWidth(totalWidth);
				table.setWidths(colsArry);
				float totalHeight = table.calculateHeights();
				float pageHeight =  reportWriterVb.getPdfHeight() - (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight);
				/*float pageHeight =  ("L".equalsIgnoreCase(reportWriterVb.getOrientation()) ?  PageSize.A4_LANDSCAPE.rotate().getHeight() : PageSize.A4.getHeight()) 
						- (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight);*/
				int pages = 1;
				//If the page height is in fractions then we need to consider + 1 for the Page.
				if(totalHeight > pageHeight){
					pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
				}
				int totalPages = pages;
				while(pages>0){
					int colStart = reportWriterVb.getLabelColCount();
					int colEnd = colSizes.size();
					int colCount = reportWriterVb.getLabelColCount();
					float labDatColWidths = sumOfLabelColWidth;
					rowEnd = getRowsPerPage(table,rowStart,pageHeight);
					//if(totalPages!=pages) rowEnd = rowEnd - table.getHeaderRows();
					List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
					boolean newPageAdded = false;
					int i=reportWriterVb.getLabelColCount();
					while(i<colSizes.size()){
						labDatColWidths +=  colSizes.get(i);
						if(labDatColWidths >  pageWidth){
							if(!isgroupColumns){
								//If non group reports then remove the 3 columns to adjust to the screen  
								colEnd = i-1;
								i = colEnd;
							}else{
								colEnd = i-1;
							}
							PdfPTable tempTable = createTableOld(table,pdfpRows,colStart,colEnd,pages,totalPages,reportWriterVb.getLabelColCount());
							float selecteColWidth = sumOfLabelColWidth;
							for(int j = colStart; j<colEnd; j++){
								selecteColWidth = selecteColWidth+ colsArry[j];
							}
							float percentWidth= (float) ((selecteColWidth*100) /( pageWidth - 200));
							if(percentWidth>99) percentWidth = 99;
							tempTable.setTotalWidth(selecteColWidth);
							tempTable.setWidthPercentage(percentWidth);
							float array[] = Arrays.copyOfRange(colsArry, 0, reportWriterVb.getLabelColCount());
							tempTable.setWidths(mergeArrays(array,Arrays.copyOfRange(colsArry, colStart, colEnd)));
							colCount = reportWriterVb.getLabelColCount();
							labDatColWidths = sumOfLabelColWidth + colSizes.get(i);
							colStart = colEnd;
							document.add(tempTable);
							document.newPage();
							newPageAdded = true;
						}
						i++;
						colCount++;
					}
					if(!newPageAdded)
						document.newPage();
					PdfPTable tempTable = createTableOld(table,pdfpRows,colStart,colSizes.size(),pages,totalPages,reportWriterVb.getLabelColCount());
					float sumOfRemainingCols = getSumOfWidths(colSizes,colStart,colSizes.size(), reportWriterVb.getLabelColCount());
					tempTable.setTotalWidth(sumOfRemainingCols + 20);
					
					float percentWidth= (float) ((sumOfRemainingCols*100) /( pageWidth - 200));
					if(percentWidth>99) percentWidth = 99;
					tempTable.setWidthPercentage(percentWidth);
					
					float array[] = Arrays.copyOfRange(colsArry, 0, reportWriterVb.getLabelColCount());
					tempTable.setWidths(mergeArrays(array,Arrays.copyOfRange(colsArry, colStart, colSizes.size())));
					
				//	tempTable.setLockedWidth(true);
					//tempTable.setHorizontalAlignment(Element.ALIGN_MIDDLE);
					document.add(tempTable);
					//tempTable.writeSelectedRows(0, -1, 40, 100, writer.getDirectContent());
					rowStart = rowEnd;
					pages--;
					if(pages>0)
						document.newPage();
				}
			}
			document.close();
			writer.close();
			/*// Get length of file in bytes
			long fileSizeInBytes = lFile.length();
			// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
			long fileSizeInKB = fileSizeInBytes / 1024;
			// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
			long fileSizeInMB = fileSizeInKB / 1024;
						
			 System.out.println("1File size in bytes is: " + fileSizeInBytes);
		     System.out.println("1File size in KB is : " + fileSizeInKB);
		     System.out.println("1File size in MB is :" + fileSizeInMB);
			
		     
		     if(fileSizeInKB>50){
		    	 // creating a zip file with different PDF documents
		    	 
		    	 File renameTo=new File(filePath+reportWriterVb.getReportTitle()+"_"+reportWriterVb.getVerifier()+".pdf");
		    	 if(renameTo.exists()){
		    		 renameTo.delete();
		    		 renameTo.createNewFile();
		    	 }
		    	 Files.move(lFile, renameTo);

				 System.out.println("1File : " +renameTo.getAbsolutePath()+renameTo.getName());
			

		         ZipOutputStream zip =
		             new ZipOutputStream(new FileOutputStream(filePath+"/reports.zip"));
		             ZipEntry entry = new ZipEntry(renameTo.getName());
		             zip.putNextEntry(entry);
		             zip.closeEntry();
		             zip.close();
		     }*/
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
		return CommonUtils.getResultObject("", 1, "", "");
	}
	public static ExceptionCode exportToPdfMdm(List<ColumnHeadersVb> columnHeaders, 
			List<ReportStgVb> reportsStgsAll, ReportsWriterVb reportsWriterVb, List<PromptIdsVb> prompts, String assetFolderUrl,List<String> columnTypes){
		Document document =  null;
		PdfPTable table = null;
		List<ReportStgVb> reportsStgs = new ArrayList<>();
		Rectangle rectangle = new Rectangle(reportsWriterVb.getPdfWidth(), reportsWriterVb.getPdfHeight());
		document = new Document(rectangle, 20, 20, 20, 20);
		/*if("L".equalsIgnoreCase(reportsWriterVb.getOrientation())){
			document = new Document(rectangle, 20, 20, 20, 20);
		}else{
			document = new Document(rectangle, 20, 20, 20, 20);
		}*/
		try {
			String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			//System.out.print("File Path:"+ filePath);
			File lFile = null;
			lFile = new File(filePath+ValidationUtil.encode(reportsWriterVb.getReportTitle())+"_"+reportsWriterVb.getVerifier()+".pdf");
			if(lFile.exists()){
				lFile.delete();
			}
			BaseFont baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
			lFile.createNewFile();
			FileOutputStream fips =  new FileOutputStream(lFile);
			PdfWriter writer = PdfWriter.getInstance(document,fips);
			InvPdfPageEventHelper invPdfEvt = new InvPdfPageEventHelper(null, reportsWriterVb.getMakerName(), assetFolderUrl, prompts, reportsWriterVb);
			writer.setPageEvent(invPdfEvt);
			document.open();
			int colNum = 0;
			int rowNum =1;
			int loopCnt = 0;
			int subColumnCnt = 0;
			boolean isgroupColumns = false;
			Map<Integer,Float> colSizes = new HashMap<Integer, Float>();
			for(ReportStgVb tempVb : reportsStgsAll) {
				reportsStgs = tempVb.getDetailReportlst();	
			
				// The Header Table creation
				if(columnHeaders != null){
					table = new PdfPTable(reportsWriterVb.getLabelColCount());
					table.setWidthPercentage(100);
					table.setHeaderRows(reportsWriterVb.getLabelRowCount());
					table.setSpacingAfter(10);
					table.setSpacingBefore(10);
					Font font= new Font(baseFont);
					font.setColor(BaseColor.WHITE);
					font.setStyle(Font.BOLD);
					font.setSize(7);
					if(reportsWriterVb.getLabelRowCount()!=1){
						table.setSplitRows(false);
						table.setSplitLate(false);
					}
					table.getDefaultCell().setBackgroundColor(new BaseColor(79, 98, 40));
					//table.getDefaultCell().setBackgroundColor(new BaseColor(0, 92, 140));
					table.getDefaultCell().setNoWrap(true);
					loopCnt = 0;
					boolean isNextRow = false;
					for(ColumnHeadersVb columnHeadersVb:columnHeaders){
						if(columnHeadersVb.getCaption().contains("<br/>")){
							String value=columnHeadersVb.getCaption().replaceAll("<br/>","\n");
							//value =  "\""+value+"\"";
							columnHeadersVb.setCaption(value);
						}
						PdfPCell cell = new PdfPCell(new Phrase(columnHeadersVb.getCaption(), font));
						cell.setBackgroundColor(new BaseColor(0, 92, 140));
						//cell.setBorder(0|0&1|1);
						cell.setUseVariableBorders(true);
						cell.setBorderWidthTop(0.5f);
						cell.setBorderWidthBottom(0.5f);
						cell.setNoWrap(true);
						
						cell.setBorderWidthTop(0.5f);
						cell.setBorderWidthBottom(0.5f);
						String type = CAP_COL;
						if(loopCnt <reportsWriterVb.getCaptionLabelColCount())
							type = CAP_COL;
						else
							type = DATA_COL;
						if(CAP_COL.equalsIgnoreCase(type)){
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						}else{
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						}
						if(columnHeadersVb.getRowSpanNum()!=0){
							cell.setRowspan(columnHeadersVb.getRowSpanNum());
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						}
						if(columnHeadersVb.getColSpanNum()!=0)
							cell.setColspan(columnHeadersVb.getColSpanNum());
						cell.setNoWrap(false);
						if(columnHeadersVb.getLabelRowNum()>1 && !isNextRow){
							table.completeRow();
							table.getDefaultCell().setBackgroundColor(null);
							isNextRow = true;
						}
						table.addCell(cell);
						++colNum;
						if(columnHeadersVb.getColSpanNum()==0){
							/*if(reportsWriterVb.getCaptionLabelColCount() == 3){
								if(loopCnt < 2){
									colSizes.put(loopCnt, 40f);
								}else if(loopCnt == 2){
									colSizes.put(loopCnt, 200f);
								}else{
									colSizes.put(loopCnt, 47f);
								}
							}else{
								if(loopCnt == 0){
									colSizes.put(loopCnt, 40f);
								}else if(loopCnt == 1){
									colSizes.put(loopCnt, 200f);
								}else{
									colSizes.put(loopCnt, 47f);
								}
							}*/
							//colSizes.put(loopCnt, ColumnText.getWidth(cell.getPhrase()));
							colSizes.put(columnHeadersVb.getLabelColNum()-1, ColumnText.getWidth(cell.getPhrase()));
							loopCnt++;
						}
					}
				}
				//table.completeRow();
				//table.getDefaultCell().setBackgroundColor(null);
				// Writing The Header
				table.completeRow();
				table.getDefaultCell().setBackgroundColor(null);
				PdfPCell cell = null;
				int recCount = 0;
				// Writingthe data
				for(ReportStgVb reportStgVb:reportsStgs){
					boolean highlight = false;
					/*if((Integer.parseInt(reportStgVb.getBoldFlag()) == 9 || Integer.parseInt(reportStgVb.getBoldFlag()) == 2 || Integer.parseInt(reportStgVb.getBoldFlag()) == 1)){
						highlight= true;
					}*/
					for(int loopCount =0; loopCount < columnTypes.size(); loopCount++){
						int index = 0;
						String type = "";
						String colType= columnTypes.get(loopCount);
						if("T".equalsIgnoreCase(colType)) {
							type = CAP_COL;
						}else {
							type = DATA_COL;
						}
						String cellText = "";
						if(type.equalsIgnoreCase(CAP_COL)){
							index = (loopCount+1);
							cellText = findValue(reportStgVb, DATA_COL, index);
							switch(getStyle(reportStgVb,recCount)){
								case 1:{Font font= new Font(baseFont);//Summary with Background
										font.setStyle(Font.BOLD);
										font.setSize(7);
										cell = new PdfPCell(new Phrase(cellText, font));
										cell.setBorder(0&0&0&1);
										cell.setUseVariableBorders(true);
										cell.setBorderWidthBottom(0.5f);
										//cell.setBorderWidthTop(1);
										cell.setBorderColor(new BaseColor(230,184,183));
										cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
										cell.setBackgroundColor(new BaseColor(205, 226, 236));
										
										if(recCount ==reportsStgs.size()-1){
											cell.setBorderWidthBottom(0.5f);
											cell.setBorderColorBottom(new BaseColor(230,184,183));
										}
										if(loopCount==0){
											cell.setBorderWidthLeft(0.5f);
											cell.setBorderColorLeft(new BaseColor(230,184,183));
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}else{
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}
										cell.setNoWrap(true);
										table.addCell(cell);
										break;}
								case 2:{Font font= new Font(baseFont);//Non Summary with background.
										font.setSize(7);
										cell = new PdfPCell(new Phrase(cellText, font));
										cell.setBorder(0&0&0&1);
										cell.setUseVariableBorders(true);
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColor(new BaseColor(230,184,183));
										cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
										cell.setBackgroundColor(new BaseColor(205, 226, 236));
										cell.setBorderColorBottom(new BaseColor(230,184,183));
										
										if(recCount ==reportsStgs.size()-1){
											cell.setBorderWidthBottom(0.5f);
											cell.setBorderColorBottom(new BaseColor(230,184,183));
										}
										if(loopCount==0){
											cell.setBorderWidthLeft(0.5f);
											cell.setBorderColorLeft(new BaseColor(230,184,183));
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}else{
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}
										cell.setNoWrap(true);
										table.addCell(cell);
										break;}
								case 3:{Font font= new Font(baseFont);//Summary with out Background
										font.setSize(7);
										font.setStyle(Font.BOLD);
										cell = new PdfPCell(new Phrase(cellText, font));
										cell.setBorder(0&0&0&1);
										//cell.setBorderWidthTop(1);
										cell.setUseVariableBorders(true);
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColor(new BaseColor(230,184,183));
										cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
										
										if(recCount ==reportsStgs.size()-1){
											cell.setBorderWidthBottom(0.5f);
											cell.setBorderColorBottom(new BaseColor(230,184,183));
										}
										if(loopCount==0){
											cell.setBorderWidthLeft(0.5f);
											cell.setBorderColorLeft(new BaseColor(230,184,183));
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}else{
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}
										cell.setNoWrap(true);
										table.addCell(cell);
										break;}
								default:{Font font= new Font(baseFont);//Non Summary With out background
										font.setSize(7);
										cell = new PdfPCell(new Phrase(cellText, font));
										cell.setBorder(0&0&0&1);
										cell.setUseVariableBorders(true);
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
										cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
										
										if(recCount ==reportsStgs.size()-1){
											cell.setBorderWidthBottom(0.5f);
											cell.setBorderColorBottom(new BaseColor(230,184,183));
										}
										if(loopCount==0){
											cell.setBorderWidthLeft(0.5f);
											cell.setBorderColorLeft(new BaseColor(230,184,183));
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}else{
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(230,184,183));
										}
										cell.setNoWrap(true);
										table.addCell(cell);
										break;}
							}
						}else{
							index = ((loopCount+1) - reportsWriterVb.getCaptionLabelColCount());
							cellText = findValue(reportStgVb, DATA_COL, index);
							switch(getStyle(reportStgVb,recCount)){
								case 1:{Font font= new Font(baseFont);
									font.setStyle(Font.BOLD);
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									//cell.setBorderWidthTop(1);
									cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setBorderColor(new BaseColor(230,184,183));
									
									if(recCount ==reportsStgs.size()-1){
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
									}
									if(loopCount==0){
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(230,184,183));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else{
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}
									cell.setPaddingLeft(3);
									cell.setPaddingRight(3);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							case 2:{Font font= new Font(baseFont);
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setBackgroundColor(new BaseColor(205, 226, 236));
									cell.setBorderColor(new BaseColor(230,184,183));
									
									if(recCount ==reportsStgs.size()-1){
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
									}
									if(loopCount==0){
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(230,184,183));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else{
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}
									//cell.setPaddingLeft(0);
									cell.setPaddingLeft(3);
									cell.setPaddingRight(3);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							case 3:{Font font= new Font(baseFont);
									font.setSize(7);
									font.setStyle(Font.BOLD);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									//cell.setBorderWidthTop(1);
									cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									cell.setBorderColor(new BaseColor(230,184,183));
									
									if(recCount ==reportsStgs.size()-1){
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
									}
									if(loopCount==0){
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(230,184,183));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else{
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}
									cell.setPaddingLeft(3);
									cell.setPaddingRight(3);
									cell.setNoWrap(true);
									table.addCell(cell); 
									break;}
							default:{Font font= new Font(baseFont);
									font.setSize(7);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0&0&0&1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColorBottom(new BaseColor(230,184,183));
									cell.setBorderColor(new BaseColor(230,184,183));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									
									if(recCount ==reportsStgs.size()-1){
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(230,184,183));
									}
									if(loopCount==0){
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(230,184,183));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else if(loopCount==reportsWriterVb.getLabelColCount()-1){
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}else{
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(230,184,183));
									}
									cell.setPaddingLeft(3);
									cell.setPaddingRight(3);
									cell.setNoWrap(true);
									table.addCell(cell);
									break;}
							}
						}
						if(cellText == null || cellText.trim().isEmpty()){
							//colSizes.put(loopCount, Math.max(20, colSizes.get(loopCount)));
						}else{
							
							float width = Math.max(ColumnText.getWidth(cell.getPhrase()), colSizes.get(loopCount));
							if(width<20) width = 20;
								colSizes.put(loopCount, width);
						}					
					}
					recCount++;
				}
				float totalWidth = 0f;
				float sumOfLabelColWidth = 0f;
				float[] colsArry = new float[colSizes.size()];
				for(int i=0;i<colSizes.size();i++){
					totalWidth += colSizes.get(i);
					colsArry[i] = colSizes.get(i);
					if(i < reportsWriterVb.getLabelColCount()){
						sumOfLabelColWidth += colSizes.get(i);
					}
				}
				//float pageWidth = "L".equalsIgnoreCase(reportsWriterVb.getOrientation()) ?  PageSize.A4_LANDSCAPE.rotate().getWidth() : PageSize.A4.getWidth();
				float pageWidth = reportsWriterVb.getPdfWidth();
				pageWidth = (pageWidth -  (document.leftMargin() + document.rightMargin()));
				if(totalWidth < pageWidth){
					float percentWidth= (float) ((totalWidth*100) /( pageWidth - 200));
					if(percentWidth>99) percentWidth = 99;
					int rowStart = 0;
					int rowEnd = table.getRows().size();
					table.setTotalWidth(totalWidth);
					table.setWidths(colsArry);
					float totalHeight = table.calculateHeights();
					float pageHeight =  reportsWriterVb.getPdfHeight() - (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight + table.spacingAfter());
					//PageSize.A4_LANDSCAPE.rotate().getHeight() =595,document.topMargin()=20,document.bottomMargin()=20, invPdfEvt.headerHeight=45, table.spacingAfter()=10
					
					int pages = 1;
					//If the page height is in fractions then we need to consider + 1 for the Page. 
					if(totalHeight > pageHeight){
						pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
						totalHeight += pages * table.getHeaderHeight() ;
						pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
					}
					int totalPages = pages;
					rowEnd = getRowsPerPage(table,rowStart,pageHeight);
					while(pages>0){
						List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
						PdfPTable tempTable = createTable(table,pdfpRows,reportsWriterVb.getLabelColCount(),table.getNumberOfColumns(),pages,totalPages,reportsWriterVb.getLabelColCount());
						tempTable.setTotalWidth(totalWidth);
						tempTable.setWidthPercentage(percentWidth);
						tempTable.setWidths(colsArry);
						//tempTable.setLockedWidth(true);
						document.add(tempTable);
						rowStart = rowEnd;
						rowEnd = getRowsPerPage(table,rowStart,pageHeight);
						pages--;
						if(pages-1 != 0)
							rowEnd = rowEnd - table.getHeaderRows();
						if(pages>0)
							document.newPage();
					}
					//document.add(table);
				}else{
					//int pages = Math.max(1,reportsStgs.size()%rowsPerPage==0?reportsStgs.size()/rowsPerPage:(reportsStgs.size()/rowsPerPage)+1);
					int rowStart = 0;
					int rowEnd = table.getRows().size();
					table.setTotalWidth(totalWidth);
					table.setWidths(colsArry);
					float totalHeight = table.calculateHeights();
					float pageHeight =  reportsWriterVb.getPdfHeight() - (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight);
					int pages = 1;
					//If the page height is in fractions then we need to consider + 1 for the Page.
					if(totalHeight > pageHeight){
						pages = Math.round(totalHeight/pageHeight) < (totalHeight/pageHeight) ? Math.round(totalHeight/pageHeight)  +1 : Math.round(totalHeight/pageHeight);
					}
					int totalPages = pages;
					while(pages>0){
						int colStart = reportsWriterVb.getLabelColCount();
						int colEnd = colSizes.size();
						int colCount = reportsWriterVb.getLabelColCount();
						float labDatColWidths = sumOfLabelColWidth;
						rowEnd = getRowsPerPage(table,rowStart,pageHeight);
						//if(totalPages!=pages) rowEnd = rowEnd - table.getHeaderRows();
						List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
						boolean newPageAdded = false;
						int i=reportsWriterVb.getLabelColCount();
						while(i<colSizes.size()){
							labDatColWidths +=  colSizes.get(i);
							if(labDatColWidths >  pageWidth){
								if(!isgroupColumns){
									//If non group reports then remove the 3 columns to adjust to the screen  
									colEnd = i-1;
									i = colEnd;
								}else{
									colEnd = i-1;
								}
								PdfPTable tempTable = createTable(table,pdfpRows,colStart,colEnd,pages,totalPages,reportsWriterVb.getLabelColCount());
								float selecteColWidth = sumOfLabelColWidth;
								for(int j = colStart; j<colEnd; j++){
									selecteColWidth = selecteColWidth+ colsArry[j];
								}
								float percentWidth= (float) ((selecteColWidth*100) /( pageWidth - 200));
								if(percentWidth>99) percentWidth = 99;
								tempTable.setTotalWidth(selecteColWidth);
								tempTable.setWidthPercentage(percentWidth);
								float array[] = Arrays.copyOfRange(colsArry, 0, reportsWriterVb.getLabelColCount());
								tempTable.setWidths(mergeArrays(array,Arrays.copyOfRange(colsArry, colStart, colEnd)));
								colCount = reportsWriterVb.getLabelColCount();
								labDatColWidths = sumOfLabelColWidth + colSizes.get(i);
								colStart = colEnd;
								document.add(tempTable);
								document.newPage();
								newPageAdded = true;
							}
							i++;
							colCount++;
						}
						if(!newPageAdded)
							document.newPage();
						PdfPTable tempTable = createTable(table,pdfpRows,colStart,colSizes.size(),pages,totalPages,reportsWriterVb.getLabelColCount());
						float sumOfRemainingCols = getSumOfWidths(colSizes,colStart,colSizes.size(), reportsWriterVb.getLabelColCount());
						tempTable.setTotalWidth(sumOfRemainingCols + 20);
						
						float percentWidth= (float) ((sumOfRemainingCols*100) /( pageWidth - 200));
						if(percentWidth>99) percentWidth = 99;
						tempTable.setWidthPercentage(percentWidth);
						
						float array[] = Arrays.copyOfRange(colsArry, 0, reportsWriterVb.getLabelColCount());
						tempTable.setWidths(mergeArrays(array,Arrays.copyOfRange(colsArry, colStart, colSizes.size())));
						
					//	tempTable.setLockedWidth(true);
						//tempTable.setHorizontalAlignment(Element.ALIGN_MIDDLE);
						document.add(tempTable);
						//tempTable.writeSelectedRows(0, -1, 40, 100, writer.getDirectContent());
						rowStart = rowEnd;
						pages--;
						if(pages>0)
							document.newPage();
					}
				}
			}
			document.close();
			writer.close();
		}catch (Exception e) {
			throw new RuntimeCustomException(e.getMessage());
		}
		return CommonUtils.getResultObject("", 1, "", "");
	}
	int colheaderFontSize = 7;
	int dataFontSize = 6;
	int summaryDataFontSize = 6;
	public  ExceptionCode exportToPdfRAWithGroup(List<ColumnHeadersVb> columnHeaders, List<HashMap<String, String>> dataLst, 
			 ReportsVb reportsVb, String assetFolderUrl,List<String> columnTypes,int currentUserId,List<HashMap<String, String>> totalLst,
			 Map < String, List < HashMap<String, String> >> groupingMap,ArrayList<ColumnHeadersVb> columnHeadersFinallst){
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			HashMap<String,String> sumMap = new HashMap<String,String>();
			Double sumVal = 0.0;
			Document document =  null;
			PdfPTable table = null;
			PdfPTable tempTable = null;
			
			float pageWidthSize = 0f;
			float pageHeightSize = 0f;
			int pdfWidthGrwth = reportsVb.getPdfGrwthPercent();
			if("P".equalsIgnoreCase(reportsVb.getReportOrientation())) {
				pageWidthSize = PageSize.A4.getWidth();
				pageHeightSize= PageSize.A4.getHeight();
				/*if(pdfWidthGrwth != 0) {
					pageWidthSize = pageWidthSize+(pageWidthSize*pdfWidthGrwth/100);
				}*/
				if(columnHeadersFinallst.size() > 12) {
					colheaderFontSize = (int)4;
					dataFontSize = (int)4;
					summaryDataFontSize = (int)4;
				}else if(columnHeadersFinallst.size() >= 8) {
					colheaderFontSize = (int)5;
					dataFontSize = (int)5;
					summaryDataFontSize = (int)5;
				}else if(columnHeadersFinallst.size() < 8) {
					colheaderFontSize = (int)6;
					dataFontSize = (int)6;
					summaryDataFontSize = (int)6;
				}
			}else{
				pageWidthSize = PageSize.A4.getHeight();
				pageHeightSize= PageSize.A4.getWidth();
				if(pdfWidthGrwth != 0) {
					pageWidthSize = pageWidthSize+(pageWidthSize*pdfWidthGrwth/100);
				}
				if(columnHeadersFinallst.size() > 15) {
					colheaderFontSize = (int)5;
					dataFontSize = (int)4;
					summaryDataFontSize = (int)4;
				}else if(columnHeadersFinallst.size() > 12) {
					colheaderFontSize = (int)6;
					dataFontSize = (int)5;
					summaryDataFontSize = (int)5;
				}else if(columnHeadersFinallst.size() <= 12) {
					colheaderFontSize = (int)7;
					dataFontSize = (int)6;
					summaryDataFontSize = (int)6;
				}
			}
			Rectangle rectangle = new Rectangle(pageWidthSize, pageHeightSize);
			document = new Document(rectangle, 20, 20, 20, 20);
			float widthAfterMargin = pageWidthSize - 40;
			String filePath = System.getProperty("java.io.tmpdir");
	
			File lFile = null;
			try {
				InvPdfPageEventHelper invPdfEvt = new InvPdfPageEventHelper(null, reportsVb.getMakerName(), assetFolderUrl,new ArrayList(), reportsVb);
				if (!ValidationUtil.isValid(filePath)) {
					filePath = System.getenv("TMP");
				}
				if (ValidationUtil.isValid(filePath)) {
					filePath = filePath + File.separator;
				}
				
				lFile = new File(
						filePath + ValidationUtil.encode(reportsVb.getReportTitle()) + "_" + currentUserId + ".pdf");
				if (lFile.exists()) {
					lFile.delete();
				}
				lFile.createNewFile();
				FileOutputStream fips =  new FileOutputStream(lFile);
				PdfWriter writer = PdfWriter.getInstance(document,fips);
				writer.setPageEvent(invPdfEvt);
				BaseFont baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
				document.open();
				
				int maxDataSize =  groupingMap.size();
				int itCounter = 0;
				Set entrySet = groupingMap.entrySet();
				Iterator it = entrySet.iterator();
				Boolean subTotalFlag = false;
				while (it.hasNext()) {
					itCounter++;
					Map.Entry dataVal = (Map.Entry) it.next();
					List<HashMap<String, String>> grpDataLst = (List) dataVal.getValue();
					String key = (String) dataVal.getKey();
				       if(key.isEmpty())
				    	   continue;
					dataLst = grpDataLst;
					subTotalFlag = false;
					sumMap = new HashMap<String, String>();
	
					PdfPTable groupingTable = new PdfPTable(1);
					groupingTable.setWidthPercentage(100);
					PdfPCell cellG;
					Font groupingFont = new Font(baseFont);
					groupingFont.setSize(8);
					groupingFont.setStyle(Font.BOLD);
					Phrase ph = new Phrase(dataVal.getKey().toString(), groupingFont);
					Paragraph p = new Paragraph(ph);
					cellG = new PdfPCell(p);
					cellG.setHorizontalAlignment(Element.ALIGN_LEFT);
					cellG.setBorder(0);
					cellG.setPaddingTop(4f);
					// cell.setBackgroundColor(new BaseColor(205, 226, 236));
					groupingTable.addCell(cellG);
					document.add(groupingTable);
	
					int colNum = 0;
					int rowNum = 1;
					int loopCnt = 0;
					int subColumnCnt = 0;
					Boolean totalRowFlag = false;
					boolean isgroupColumns = false;
					Map<Integer, Float> colSizes = new HashMap<Integer, Float>();
					int maxHeaderRow = columnHeaders.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
					int maxHeaderCol = columnHeaders.stream().mapToInt(ColumnHeadersVb::getLabelColNum).max().orElse(0);
	
					List<ColumnHeadersVb> colHeadersCol = columnHeaders.stream()
							.sorted(Comparator.comparingInt(ColumnHeadersVb::getLabelRowNum)).collect(Collectors.toList());
	
					if (columnHeaders != null) {
						table = new PdfPTable(maxHeaderCol);
						table.setWidthPercentage(100);
						table.setHeaderRows(maxHeaderRow);
						table.setSpacingAfter(12);
						table.setSpacingBefore(12);
						Font font = new Font(baseFont);
						font.setColor(BaseColor.WHITE);
						font.setStyle(Font.BOLD);
						font.setSize(colheaderFontSize);
						if (maxHeaderRow != 1) {
							table.setSplitRows(false);
							table.setSplitLate(false);
						}
						table.getDefaultCell().setBackgroundColor(new BaseColor(79, 98, 40));
						table.getDefaultCell().setNoWrap(true);
						loopCnt = 0;
						boolean isNextRow = false;
						for (ColumnHeadersVb columnHeadersVb : colHeadersCol) {
							if (columnHeadersVb.getCaption().contains("<br/>")) {
								String value = columnHeadersVb.getCaption().replaceAll("<br/>", "\n");
								columnHeadersVb.setCaption(value);
							}
							PdfPCell cell = new PdfPCell(new Phrase(columnHeadersVb.getCaption(), font));
							cell.setBackgroundColor(new BaseColor(0, 92, 140));
							cell.setUseVariableBorders(true);
							cell.setBorderWidthTop(0.5f);
							cell.setBorderWidthBottom(0.5f);
							cell.setBorderColor(new BaseColor(222, 226, 230));
							cell.setNoWrap(false);
							cell.setPaddingBottom(4f);
							cell.setPaddingTop(4f);
							cell.setPaddingLeft(2f);
							cell.setPaddingRight(2f);
	
							String type = "";
							String colType = columnHeadersVb.getColType();
							if ("T".equalsIgnoreCase(colType)) {
								type = CAP_COL;
							} else {
								type = DATA_COL;
							}
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	
							if (columnHeadersVb.getRowspan() != 0) {
								cell.setRowspan(columnHeadersVb.getRowspan());
								cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							}
							if (columnHeadersVb.getColspan() != 0)
								cell.setColspan(columnHeadersVb.getColspan());
							cell.setNoWrap(true);
							if (columnHeadersVb.getLabelRowNum() > 1 && !isNextRow) {
								table.completeRow();
								table.getDefaultCell().setBackgroundColor(null);
								isNextRow = true;
							}
							table.addCell(cell);
							++colNum;
							if (columnHeadersVb.getColspan()  <= 1 && columnHeadersVb.getNumericColumnNo() != 99) {
								float columnWidth = Float.parseFloat(columnHeadersVb.getColumnWidth());
								float pdfColWdth = widthAfterMargin * columnWidth / 100;
								colSizes.put(columnHeadersVb.getLabelColNum() - 1, pdfColWdth);
								loopCnt++;
							}
						}
					}
					// Writing The Header
					table.completeRow();
					table.getDefaultCell().setBackgroundColor(null);
					PdfPCell cell = null;
					int recCount = 0;
					int ctr = 1;
	
					// Writing the data
					do {
						for (HashMap dataLstMap : dataLst) {
							boolean highlight = false;
							String formatType = "D";
							if(dataLstMap.containsKey("FORMAT_TYPE")) {
								formatType = dataLstMap.get("FORMAT_TYPE").toString();
							}
							for (int loopCount = 0; loopCount < columnHeadersFinallst.size(); loopCount++) {
								ColumnHeadersVb colHeadersVb = columnHeadersFinallst.get(loopCount);
								if (colHeadersVb.getColspan() > 1 || colHeadersVb.getNumericColumnNo() == 99)
									continue;
								int index = 0;
								String type = "";
								String colType = columnTypes.get(loopCount);
								if ("T".equalsIgnoreCase(colType)) {
									type = CAP_COL;
								} else {
									type = DATA_COL;
								}
								String cellText = "";
								if (type.equalsIgnoreCase(CAP_COL)) {
									index = (loopCount + 1);
									String orgValue = "";
									if (dataLstMap.containsKey(colHeadersVb.getDbColumnName())) {
										orgValue = ((dataLstMap.get(colHeadersVb.getDbColumnName())) != null
												|| dataLstMap.get(colHeadersVb.getDbColumnName()) == "")
														? dataLstMap.get(colHeadersVb.getDbColumnName()).toString()
														: "";
									}
									cellText = orgValue;
									// String orgValue = dataLstMap.containsKey(colHeadersVb.getDbColumnName())?
									// dataLstMap.get(colHeadersVb.getDbColumnName()).toString(): "";
									
									
									// cellText = findValue(reportsVb, DATA_COL, index);
									if (totalRowFlag || subTotalFlag) {
										if (!ValidationUtil.isValid(orgValue) && loopCount == 0) {
											orgValue = "Total ";
										}
										cellText = orgValue;
										if (loopCount == 0) {
											if (subTotalFlag)
												cellText = "Sub Total";
											if (totalRowFlag)
												cellText = "Total";
										} else {
											cellText = "";
										}
										Font font = new Font(baseFont);// Summary with Background
										font.setStyle(Font.BOLD);
										font.setSize(summaryDataFontSize);
										cell = new PdfPCell(new Phrase(cellText, font));
										cell.setBorder(0 & 0 & 0 & 1);
										cell.setUseVariableBorders(true);
										cell.setBorderWidthBottom(0.5f);
										// cell.setBorderWidthTop(1);
										cell.setBorderColor(new BaseColor(222, 226, 230));
										cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
										cell.setPaddingBottom(4f);
										cell.setPaddingTop(4f);
										cell.setPaddingLeft(2f);
										cell.setPaddingRight(2f);
										if (totalRowFlag)
											cell.setBackgroundColor(new BaseColor(205, 226, 236));
	
										if (recCount == dataLst.size() - 1) {
											cell.setBorderWidthBottom(0.5f);
											cell.setBorderColorBottom(new BaseColor(222, 226, 230));
										}
										if (loopCount == 0) {
											cell.setBorderWidthLeft(0.5f);
											cell.setBorderColorLeft(new BaseColor(222, 226, 230));
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										} else if (loopCount == maxHeaderCol - 1) {
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										} else {
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										}
										cell.setNoWrap(true);
										table.addCell(cell);
									} else {
										Font font = new Font(baseFont);
										font.setSize(dataFontSize);
										cell = new PdfPCell(new Phrase(cellText, font));
										if("FT".equalsIgnoreCase(formatType) || "S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
											font.setStyle(Font.BOLD);
											if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
												font.setSize(summaryDataFontSize);
												cell.setBackgroundColor(new BaseColor(205, 226, 236));
											}
										}
										cell = new PdfPCell(new Phrase(cellText, font));
										cell.setBorder(0 & 0 & 0 & 1);
										cell.setUseVariableBorders(true);
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(222, 226, 230));
										cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
										cell.setPaddingBottom(4f);
										cell.setPaddingTop(4f);
										cell.setPaddingLeft(2f);
										cell.setPaddingRight(2f);
										if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
											cell.setBackgroundColor(new BaseColor(205, 226, 236));
										}
										if (recCount == dataLst.size() - 1) {
											cell.setBorderWidthBottom(0.5f);
											cell.setBorderColorBottom(new BaseColor(222, 226, 230));
										}
										if (loopCount == 0) {
											cell.setBorderWidthLeft(0.5f);
											cell.setBorderColorLeft(new BaseColor(222, 226, 230));
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										} else if (loopCount == maxHeaderCol - 1) {
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										} else {
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										}
										cell.setNoWrap(false);
										table.addCell(cell);
									}
								} else {
									String orgValue = "";
									// index = ((loopCount+1) - reportsWriterVb.getCaptionLabelColCount());
									if (dataLstMap.containsKey(colHeadersVb.getDbColumnName())) {
										orgValue = ((dataLstMap.get(colHeadersVb.getDbColumnName())) != null
												|| dataLstMap.get(colHeadersVb.getDbColumnName()) == "")
														? dataLstMap.get(colHeadersVb.getDbColumnName()).toString()
														: "";
									}
									// String orgValue = dataLstMap.containsKey(colHeadersVb.getDbColumnName())?
									// dataLstMap.get(colHeadersVb.getDbColumnName()).toString(): "";
									if (ValidationUtil.isValid(orgValue)) {
											if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)) {
												if (ValidationUtil.isNumericDecimal(orgValue)) {
													double amount = Double.parseDouble(orgValue);
													DecimalFormat formatter = new DecimalFormat("#,###");
													cellText = formatter.format(amount);
												} else {
													cellText = orgValue;
												}
										} else {
											if (ValidationUtil.isNumericDecimal(orgValue)) {
												if (!"NR".equalsIgnoreCase(colType) && !"TR".equalsIgnoreCase(colType)) {
													double amount = Double.parseDouble(orgValue);
													DecimalFormat formatter = new DecimalFormat("#,##0.00");
													cellText = formatter.format(amount);
												} else {
													cellText = orgValue;
												}
											} else {
												cellText = orgValue;
											}
										}
									} else {
										cellText = orgValue;
									}
									if (ValidationUtil.isValid(cellText) && "-0.00".equalsIgnoreCase(cellText)) {
										cellText = "0.00";
									}
									if (totalRowFlag || subTotalFlag) {
										Font font = new Font(baseFont);
										font.setStyle(Font.BOLD);
										font.setSize(summaryDataFontSize);
										cell = new PdfPCell(new Phrase(cellText, font));
										cell.setBorder(0 & 0 & 0 & 1);
										cell.setUseVariableBorders(true);
										cell.setBorderWidthBottom(0.5f);
										cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
										cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
										if (totalRowFlag)
											cell.setBackgroundColor(new BaseColor(205, 226, 236));
										cell.setBorderColor(new BaseColor(222, 226, 230));
										cell.setPaddingBottom(4f);
										cell.setPaddingTop(4f);
										cell.setPaddingLeft(4f);
										cell.setPaddingRight(4f);
	
										if (recCount == dataLst.size() - 1) {
											cell.setBorderWidthBottom(0.5f);
											cell.setBorderColorBottom(new BaseColor(222, 226, 230));
										}
										if (loopCount == 0) {
											cell.setBorderWidthLeft(0.5f);
											cell.setBorderColorLeft(new BaseColor(222, 226, 230));
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										} else if (loopCount == maxHeaderCol - 1) {
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										} else {
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										}
										cell.setNoWrap(true);
										table.addCell(cell);
									} else {
										Font font = new Font(baseFont);
										font.setSize(dataFontSize);
										cell = new PdfPCell(new Phrase(cellText, font));
										if("FT".equalsIgnoreCase(formatType) || "S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
											font.setStyle(Font.BOLD);
											if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
												font.setSize(summaryDataFontSize);
												cell.setBackgroundColor(new BaseColor(205, 226, 236));
											}
										}
										cell = new PdfPCell(new Phrase(cellText, font));
										cell.setBorder(0 & 0 & 0 & 1);
										cell.setUseVariableBorders(true);
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(222, 226, 230));
										cell.setBorderColor(new BaseColor(222, 226, 230));
										cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
										cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
										cell.setPaddingBottom(4f);
										cell.setPaddingTop(4f);
										cell.setPaddingLeft(4f);
										cell.setPaddingRight(4f);
										if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
											cell.setBackgroundColor(new BaseColor(205, 226, 236));
										}
										if (recCount == dataLst.size() - 1) {
											cell.setBorderWidthBottom(0.5f);
											cell.setBorderColorBottom(new BaseColor(222, 226, 230));
										}
										if (loopCount == 0) {
											cell.setBorderWidthLeft(0.5f);
											cell.setBorderColorLeft(new BaseColor(222, 226, 230));
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										} else if (loopCount == maxHeaderCol - 1) {
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										} else {
											cell.setBorderWidthRight(0.1f);
											cell.setBorderColorRight(new BaseColor(222, 226, 230));
										}
										cell.setNoWrap(true);
										table.addCell(cell);
										if(!"NR".equalsIgnoreCase(colType) && !"TR".equalsIgnoreCase(colType)) {
											String cellValue = orgValue.replaceAll(",", "");
										/*if("Y".equalsIgnoreCase(colHeadersVb.getSumFlag())) {*/
											String prevValstr = sumMap.containsKey(colHeadersVb.getDbColumnName())
													? sumMap.get(colHeadersVb.getDbColumnName())
													: "0";
											Double strVal = Double.parseDouble(prevValstr);
											sumVal = strVal + Double.parseDouble(cellValue);
											DecimalFormat formatter = new DecimalFormat("####.00");
											String cellText1 = formatter.format(sumVal);
											sumMap.put(colHeadersVb.getDbColumnName(), cellText1);
										}
										/*}*/
									}
								}
								if (cellText == null || cellText.trim().isEmpty()) {
									// colSizes.put(loopCount, Math.max(20, colSizes.get(loopCount)));
								} else {
	
									/*
									 * float width = Math.max(ColumnText.getWidth(cell.getPhrase()),
									 * colSizes.get(loopCount)); if (width < 20) width = 20; colSizes.put(loopCount,
									 * width);
									 */
								}
							}
							recCount++;
						}
						ctr++;
						reportsVb.setCurrentPage(ctr);
						dataLst = new ArrayList();
						if (itCounter == maxDataSize) {
							if (!subTotalFlag) {
								if(!sumMap.isEmpty()) {
									sumMap.put("FORMAT_TYPE", "ST");
									dataLst.add(sumMap);
									subTotalFlag = true;
								}
								totalRowFlag = false;
							} else if (!totalRowFlag) {
								dataLst = totalLst;
								totalRowFlag = true;
								subTotalFlag = true;
							}
						} else {
							if (!subTotalFlag) {
								if(!sumMap.isEmpty()) {
									sumMap.put("FORMAT_TYPE", "ST");
									dataLst.add(sumMap);
									subTotalFlag = true;
								}
								totalRowFlag = false;
							}
						}
					} while (dataLst != null && !dataLst.isEmpty());
	
					float totalWidth = 0f;
					float sumOfLabelColWidth = 0f;
					float[] colsArry = new float[colSizes.size()];
					for (int i = 0; i < colSizes.size(); i++) {
						totalWidth += colSizes.get(i);
						colsArry[i] = colSizes.get(i);
						if (i < maxHeaderCol) {
							sumOfLabelColWidth += colSizes.get(i);
						}
					}
					float pageWidth = "L".equalsIgnoreCase(reportsVb.getReportOrientation()) ? PageSize.A4.getHeight()
							: PageSize.A4.getWidth();
					float pageHeight = ("L".equalsIgnoreCase(reportsVb.getReportOrientation()) ? PageSize.A4.getWidth()
							: PageSize.A4.getHeight())
							- (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight + 50);
					// float pageWidth = reportsVb.getPdfWidth();
					// pageWidth = (pageWidth - (document.leftMargin() + document.rightMargin()));
					/* if (totalWidth < pageWidth) { */
					float percentWidth = (float) ((totalWidth * 100) / (pageWidth - 200));
					// if (percentWidth > 99)
					percentWidth = 99;
					int rowStart = 0;
					int rowEnd = table.getRows().size();
					table.setTotalWidth(totalWidth);
					table.setWidths(colsArry);
					float totalHeight = table.calculateHeights();
					// float pageHeight = reportsVb.getPdfHeight() - (document.topMargin() +
					// document.bottomMargin()+ invPdfEvt.headerHeight + table.spacingAfter());
					int pages = 1;
					if (totalHeight > pageHeight) {
						pages = Math.round(totalHeight / pageHeight) < (totalHeight / pageHeight)
								? Math.round(totalHeight / pageHeight) + 1
								: Math.round(totalHeight / pageHeight);
						totalHeight += pages * table.getHeaderHeight();
						pages = Math.round(totalHeight / pageHeight) < (totalHeight / pageHeight)
								? Math.round(totalHeight / pageHeight) + 1
								: Math.round(totalHeight / pageHeight);
					}
	
					int totalPages = pages;
					rowEnd = getRowsPerPage(table, rowStart, pageHeight);
					while (pages > 0) {
						List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
						tempTable = createTable(table, pdfpRows, maxHeaderCol, table.getNumberOfColumns(), pages,
								totalPages, maxHeaderCol);
						tempTable.setTotalWidth(totalWidth);
						tempTable.setWidthPercentage(percentWidth);
						tempTable.setWidths(colsArry);
						// tempTable.setLockedWidth(true);
						document.add(tempTable);
						rowStart = rowEnd;
						rowEnd = getRowsPerPage(table, rowStart, pageHeight);
						pages--;
						if (pages - 1 != 0)
							rowEnd = rowEnd - table.getHeaderRows();
						if (pages > 0)
							document.newPage();
					}
					if (itCounter < maxDataSize)
						document.newPage();
				}
				document.close();
				writer.close();
				exceptionCode.setResponse(filePath);
				exceptionCode.setOtherInfo(reportsVb.getReportTitle() + "_" + currentUserId);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			} catch (RuntimeCustomException e) {
				e.printStackTrace();
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				throw new RuntimeCustomException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			}
		}catch(Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	
	public  ExceptionCode exportToPdfRA(List<ColumnHeadersVb> columnHeaders, List<HashMap<String, String>> dataLst, ReportsVb reportsVb, 
			String assetFolderUrl,List<String> columnTypes,int currentUserId,List<HashMap<String, String>> totalLst,ArrayList<ColumnHeadersVb> columnHeadersFinallst){
		
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			
			Document document =  null;
			PdfPTable table = null;
			PdfPTable tempTable = null;
			
			float pageWidthSize = 0f;
			float pageHeightSize = 0f;
			int pdfWidthGrwth = reportsVb.getPdfGrwthPercent();
			if("P".equalsIgnoreCase(reportsVb.getReportOrientation())) {
				pageWidthSize = PageSize.A4.getWidth();
				pageHeightSize= PageSize.A4.getHeight();
				/*if(pdfWidthGrwth != 0) {
					pageWidthSize = pageWidthSize+(pageWidthSize*pdfWidthGrwth/100);
				}*/
				if(columnHeadersFinallst.size() > 12) {
					colheaderFontSize = (int)4;
					dataFontSize = (int)4;
					summaryDataFontSize = (int)4;
				}else if(columnHeadersFinallst.size() >= 8) {
					colheaderFontSize = (int)5;
					dataFontSize = (int)5;
					summaryDataFontSize = (int)5;
				}else if(columnHeadersFinallst.size() < 8) {
					colheaderFontSize = (int)6;
					dataFontSize = (int)6;
					summaryDataFontSize = (int)6;
				}
			}else{
				pageWidthSize = PageSize.A4.getHeight();
				pageHeightSize= PageSize.A4.getWidth();
				if(pdfWidthGrwth != 0) {
					pageWidthSize = pageWidthSize+(pageWidthSize*pdfWidthGrwth/100);
				}
				if(columnHeadersFinallst.size() > 15) {
					colheaderFontSize = (int)5;
					dataFontSize = (int)4;
					summaryDataFontSize = (int)5;
				}else if(columnHeadersFinallst.size() > 12) {
					colheaderFontSize = (int)6;
					dataFontSize = (int)5;
					summaryDataFontSize = (int)6;
				}else if(columnHeadersFinallst.size() <= 12) {
					colheaderFontSize = (int)7;
					dataFontSize = (int)6;
					summaryDataFontSize = (int)7;
				}
			}
			
			Rectangle rectangle = new Rectangle(pageWidthSize, pageHeightSize);
			document = new Document(rectangle, 20, 20, 20, 20);
			float widthAfterMargin = pageWidthSize - 40;
			
			try {
				String filePath = System.getProperty("java.io.tmpdir");
				if (!ValidationUtil.isValid(filePath)) {
					filePath = System.getenv("TMP");
				}
				if (ValidationUtil.isValid(filePath)) {
					filePath = filePath + File.separator;
				}
				File lFile = null;
				lFile = new File(
						filePath + ValidationUtil.encode(reportsVb.getReportTitle()) + "_" + currentUserId + ".pdf");
				if (lFile.exists()) {
					lFile.delete();
				}
				BaseFont baseFont = BaseFont.createFont(assetFolderUrl + "/VERDANA.TTF", BaseFont.WINANSI,
						BaseFont.EMBEDDED);
				lFile.createNewFile();
				FileOutputStream fips = new FileOutputStream(lFile);
				PdfWriter writer = PdfWriter.getInstance(document, fips);
				InvPdfPageEventHelper invPdfEvt = new InvPdfPageEventHelper(null, reportsVb.getMakerName(), assetFolderUrl,
						new ArrayList(), reportsVb);
				writer.setPageEvent(invPdfEvt);
				document.open();
				int colNum = 0;
				int rowNum = 1;
				int loopCnt = 0;
				int subColumnCnt = 0;
				Boolean totalRowFlag = false;
				boolean isgroupColumns = false;
				Map<Integer, Float> colSizes = new HashMap<Integer, Float>();
				int maxHeaderRow = columnHeaders.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
				int maxHeaderCol = columnHeaders.stream().mapToInt(ColumnHeadersVb::getLabelColNum).max().orElse(0);
	
				List<ColumnHeadersVb> colHeadersCol = columnHeaders.stream()
						.sorted(Comparator.comparingInt(ColumnHeadersVb::getLabelRowNum)).collect(Collectors.toList());
				
				// The Header Table creation
				if (columnHeaders != null) {
					table = new PdfPTable(maxHeaderCol);
					table.setWidthPercentage(100);
					table.setHeaderRows(maxHeaderRow);
					table.setSpacingAfter(12);
					table.setSpacingBefore(12);
					Font font = new Font(baseFont);
					font.setColor(BaseColor.WHITE);
					font.setStyle(Font.BOLD);
					font.setSize(colheaderFontSize);
					if (maxHeaderRow != 1) {
						table.setSplitRows(false);
						table.setSplitLate(false);
					}
					table.getDefaultCell().setBackgroundColor(new BaseColor(79, 98, 40)); // Default Color blue
					table.getDefaultCell().setNoWrap(true);
					loopCnt = 0;
					boolean isNextRow = false;
					for (ColumnHeadersVb columnHeadersVb : colHeadersCol) {
						if (columnHeadersVb.getCaption().contains("<br/>")) {
							String value = columnHeadersVb.getCaption().replaceAll("<br/>", "\n");
							columnHeadersVb.setCaption(value);
						}
						PdfPCell cell = new PdfPCell(new Phrase(columnHeadersVb.getCaption(), font));
						cell.setBackgroundColor(new BaseColor(0, 92, 140)); // Default Color blue
						//cell.setBackgroundColor(new BaseColor(177, 24, 124)); // Sunoida Latest Pink
						cell.setUseVariableBorders(true);
						cell.setBorderWidthTop(0.5f);
						cell.setBorderWidthBottom(0.5f);
						cell.setBorderColor(new BaseColor(222,226,230));
						cell.setNoWrap(true);
						cell.setPaddingBottom(4f);
						cell.setPaddingTop(4f);
						cell.setPaddingLeft(2f);
						cell.setPaddingRight(2f);
	
						String type = "";
						String colType = columnHeadersVb.getColType();
						if ("T".equalsIgnoreCase(colType)) {
							type = CAP_COL;
						} else {
							type = DATA_COL;
						}
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						
						if (columnHeadersVb.getRowspan() != 0) {
							cell.setRowspan(columnHeadersVb.getRowspan());
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						}
						if (columnHeadersVb.getColspan() != 0)
							cell.setColspan(columnHeadersVb.getColspan());
						cell.setNoWrap(true);
						if (columnHeadersVb.getLabelRowNum() > 1 && !isNextRow) {
							table.completeRow();
							table.getDefaultCell().setBackgroundColor(null);
							isNextRow = true;
						}
						table.addCell(cell);
						++colNum;
						if (columnHeadersVb.getColspan() <= 1) {
							if(columnHeadersVb.getNumericColumnNo() != 99) {
								float columnWidth = Float.parseFloat(columnHeadersVb.getColumnWidth());
								float pdfColWdth = widthAfterMargin*columnWidth/100;
								colSizes.put(columnHeadersVb.getLabelColNum() - 1, pdfColWdth);
								loopCnt++;
							}
						}
					}
				}
				// Writing The Header
				table.completeRow();
				table.getDefaultCell().setBackgroundColor(null);
				PdfPCell cell = null;
				int recCount = 0;
				int ctr = 1;
				// Writingthe data
				do {
					for (HashMap dataLstMap : dataLst) {
						boolean highlight = false;
						String formatType = "D";
						if(dataLstMap.containsKey("FORMAT_TYPE")) {
							formatType = dataLstMap.get("FORMAT_TYPE").toString();
						}
						for (int loopCount = 0; loopCount < columnHeadersFinallst.size(); loopCount++) {
							ColumnHeadersVb colHeadersVb = columnHeadersFinallst.get(loopCount);
							if (colHeadersVb.getColspan() > 1 || colHeadersVb.getNumericColumnNo() == 99)
								continue;
							int index = 0;
							String type = "";
							String colType = columnTypes.get(loopCount);
							if ("T".equalsIgnoreCase(colType)) {
								type = CAP_COL;
							} else {
								type = DATA_COL;
							}
							String cellText = "";
							String orgValue = "";
							if (type.equalsIgnoreCase(CAP_COL)) {
								index = (loopCount + 1);
								if(dataLstMap.containsKey(colHeadersVb.getDbColumnName())) {
									orgValue = ((dataLstMap.get(colHeadersVb.getDbColumnName())) != null ||  dataLstMap.get(colHeadersVb.getDbColumnName()) == "") ? dataLstMap.get(colHeadersVb.getDbColumnName()).toString() : "";
								}
								cellText = orgValue;
								if (totalRowFlag) {
									if (!ValidationUtil.isValid(orgValue) && loopCount == 0) {
										orgValue = "Total";
									}
									cellText = orgValue;
									
									Font font = new Font(baseFont);// Summary with Background
									font.setStyle(Font.BOLD);
									//font.setColor(177, 24, 124);//Sunoida Light pink color
									font.setSize(summaryDataFontSize);
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0 & 0 & 0 & 1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									// cell.setBorderWidthTop(1);
									cell.setBorderColor(new BaseColor(222,226,230));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setPaddingBottom(4f);
									cell.setPaddingTop(4f);
									cell.setPaddingLeft(2f);
									cell.setPaddingRight(2f);
									//cell.setBackgroundColor(new BaseColor(242,244,242));//Sunoida Light Grey
									cell.setBackgroundColor(new BaseColor(205, 226, 236));//light blue
	
									if (recCount == dataLst.size() - 1) {
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(222,226,230));
									}
									if (loopCount == 0) {
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(222,226,230));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									} else if (loopCount == maxHeaderCol - 1) {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									} else {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									}
									cell.setNoWrap(true);
									table.addCell(cell);
								} else {
									Font font = new Font(baseFont);
									font.setSize(dataFontSize);
									// FT - Text with BOLD
									//S OR ST - Text BOLD and with Background Color
									if (ValidationUtil.isValid(cellText)) {
										if ("FT".equalsIgnoreCase(formatType) || "S".equalsIgnoreCase(formatType)
												|| "ST".equalsIgnoreCase(formatType)) {
											font.setStyle(Font.BOLD);
											if ("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
												font.setSize(summaryDataFontSize);
											}
										}
									}
									
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0 & 0 & 0 & 1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColorBottom(new BaseColor(222,226,230));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setPaddingBottom(4f);
									cell.setPaddingTop(4f);
									cell.setPaddingLeft(2f);
									cell.setPaddingRight(2f);
									if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
										//cell.setBackgroundColor(new BaseColor(242,244,242));//light grey
										cell.setBackgroundColor(new BaseColor(205, 226, 236));
									}
									if (recCount == dataLst.size() - 1) {
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(222,226,230));
									}
									if (loopCount == 0) {
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(222,226,230));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									} else if (loopCount == maxHeaderCol - 1) {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									} else {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									}
									cell.setNoWrap(false);
									table.addCell(cell);
								}
							} else {
								// index = ((loopCount+1) - reportsWriterVb.getCaptionLabelColCount());
								if(dataLstMap.containsKey(colHeadersVb.getDbColumnName())) 
									orgValue = ((dataLstMap.get(colHeadersVb.getDbColumnName())) != null ||  dataLstMap.get(colHeadersVb.getDbColumnName()) == "") ? dataLstMap.get(colHeadersVb.getDbColumnName()).toString() : "";
								else 
									orgValue = "";
								//String orgValue = dataLstMap.containsKey(colHeadersVb.getDbColumnName())? dataLstMap.get(colHeadersVb.getDbColumnName()).toString(): "";
								if(ValidationUtil.isValid(orgValue)) {
									if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)) {
										if (ValidationUtil.isNumericDecimal(orgValue)) {
												double amount = Double.parseDouble(orgValue);
												DecimalFormat formatter = new DecimalFormat("#,###");
												cellText = formatter.format(amount);
											} else {
												cellText = orgValue;
											}
									} else {
										if (ValidationUtil.isNumericDecimal(orgValue)) {
											if (!"NR".equalsIgnoreCase(colType) && !"TR".equalsIgnoreCase(colType)) {
												double amount = Double.parseDouble(orgValue);
												DecimalFormat formatter = new DecimalFormat("#,##0.00");
												cellText = formatter.format(amount);
											} else {
												cellText = orgValue;
											}
										} else {
										cellText = orgValue;
										}
									}
								}else {
									cellText = orgValue;
								}
								if(ValidationUtil.isValid(cellText) && "-0.00".equalsIgnoreCase(cellText)) {
									cellText = "0.00";
								}
								if (totalRowFlag) {
									Font font = new Font(baseFont);
									font.setStyle(Font.BOLD);
									font.setSize(summaryDataFontSize);
									//font.setColor(177, 24, 124); //Sunoida Light pink color
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0 & 0 & 0 & 1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									//cell.setBackgroundColor(new BaseColor(242,244,242));//light grey
									cell.setBackgroundColor(new BaseColor(205, 226, 236));//light blue
									cell.setBorderColor(new BaseColor(222,226,230));
									cell.setPaddingBottom(4f);
									cell.setPaddingTop(4f);
									cell.setPaddingLeft(2f);
									cell.setPaddingRight(2f);
	
									if (recCount == dataLst.size() - 1) {
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(222,226,230));
									}
									if (loopCount == 0) {
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(222,226,230));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									} else if (loopCount == maxHeaderCol - 1) {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									} else {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									}
									cell.setNoWrap(true);
									table.addCell(cell);
								} else {
									Font font = new Font(baseFont);
									font.setSize(dataFontSize);
									cell = new PdfPCell(new Phrase(cellText, font));
									if("FT".equalsIgnoreCase(formatType) || "S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
										font.setStyle(Font.BOLD);
										if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
											font.setSize(summaryDataFontSize);
											//cell.setBackgroundColor(new BaseColor(242,244,242));//light grey
											cell.setBackgroundColor(new BaseColor(205, 226, 236));//light blue
										}
									}
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0 & 0 & 0 & 1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColorBottom(new BaseColor(222,226,230));
									cell.setBorderColor(new BaseColor(222,226,230));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									cell.setPaddingBottom(4f);
									cell.setPaddingTop(4f);
									cell.setPaddingLeft(2f);
									cell.setPaddingRight(2f);
									if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
										cell.setBackgroundColor(new BaseColor(205, 226, 236));
									}
									if (recCount == dataLst.size() - 1) {
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(222,226,230));
									}
									if (loopCount == 0) {
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(222,226,230));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									} else if (loopCount == maxHeaderCol - 1) {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									} else {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222,226,230));
									}
									cell.setNoWrap(true);
									table.addCell(cell);
								}
							}
							if (cellText == null || cellText.trim().isEmpty()) {
								// colSizes.put(loopCount, Math.max(20, colSizes.get(loopCount)));
							} else {
	
								float width = Math.max(ColumnText.getWidth(cell.getPhrase()), colSizes.get(loopCount));
								/*if (width < 20)
									width = 20;
								colSizes.put(loopCount, width);*/
							}
						}
						recCount++;
					}
					ctr++;
					reportsVb.setCurrentPage(ctr);
					dataLst = new ArrayList();
					if (!totalRowFlag) {
						exceptionCode = reportsDao.getResultData(reportsVb, true);
						if (ValidationUtil.isValid(exceptionCode.getResponse())) {
							ReportsVb resultVb = (ReportsVb) exceptionCode.getResponse();
							dataLst = resultVb.getGridDataSet();
							if (dataLst == null || dataLst.isEmpty()) {
								if(totalLst != null && !totalLst.isEmpty()) {
									dataLst.add(totalLst.get(0));
									totalRowFlag = true;
								}
							}
						}
					}
				} while (dataLst != null && !dataLst.isEmpty());
	
				float totalWidth = 0f;
				float sumOfLabelColWidth = 0f;
				float[] colsArry = new float[colSizes.size()];
				for (int i = 0; i < colSizes.size(); i++) {
					totalWidth += colSizes.get(i);
					colsArry[i] = colSizes.get(i);
					if (i < maxHeaderCol) {
						sumOfLabelColWidth += colSizes.get(i);
					}
				}
				float pageWidth = "L".equalsIgnoreCase(reportsVb.getReportOrientation()) ?  PageSize.A4.getHeight(): PageSize.A4.getWidth();
			    float pageHeight =  ("L".equalsIgnoreCase(reportsVb.getReportOrientation()) ?  PageSize.A4.getWidth(): PageSize.A4.getHeight()) 
						- (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight+50);
			    //float pageWidth = reportsVb.getPdfWidth();
				pageWidth = (pageWidth - (document.leftMargin() + document.rightMargin()));
				/*if (totalWidth < pageWidth) {*/
					float percentWidth = (float) ((totalWidth * 100) / (pageWidth - 200));
					//if (percentWidth > 99)
					percentWidth = 99;
					int rowStart = 0;
					int rowEnd = table.getRows().size();
					table.setTotalWidth(totalWidth);
					table.setWidths(colsArry);
					float totalHeight = table.calculateHeights();
					//float pageHeight = reportsVb.getPdfHeight() - (document.topMargin() + document.bottomMargin()+ invPdfEvt.headerHeight + table.spacingAfter());
	
					int pages = 1;
					if (totalHeight > pageHeight) {
						pages = Math.round(totalHeight / pageHeight) < (totalHeight / pageHeight)? Math.round(totalHeight / pageHeight) + 1
								: Math.round(totalHeight / pageHeight);
						totalHeight += pages * table.getHeaderHeight();
						pages = Math.round(totalHeight / pageHeight) < (totalHeight / pageHeight)? Math.round(totalHeight / pageHeight) + 1
								: Math.round(totalHeight / pageHeight);
					}
					
					int totalPages = pages;
					rowEnd = getRowsPerPage(table, rowStart, pageHeight);
					while (pages > 0) {
						List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
						tempTable = createTable(table, pdfpRows, maxHeaderCol, table.getNumberOfColumns(), pages,totalPages, maxHeaderCol);
						tempTable.setTotalWidth(totalWidth);
						tempTable.setWidthPercentage(percentWidth);
						tempTable.setWidths(colsArry);
						// tempTable.setLockedWidth(true);
						document.add(tempTable);
						if(rowEnd != table.getRows().size()) {
							rowStart = rowEnd;
							rowEnd = getRowsPerPage(table, rowStart, pageHeight);
							pages--;
							/*if (pages - 1 != 0)
								rowEnd = rowEnd - table.getHeaderRows();*/
							if (pages > 0)
								document.newPage();
						}else {
							pages = 0;
						}
					}
					// document.add(table);
					/*} else {
					int rowStart = 0;
					int rowEnd = table.getRows().size();
					table.setTotalWidth(totalWidth);
					table.setWidths(colsArry);
					float totalHeight = table.calculateHeights(true);
					float pageHeight = reportsVb.getPdfHeight()
							- (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight);
					int pages = 1;
					// If the page height is in fractions then we need to consider + 1 for the Page.
					if (totalHeight > pageHeight) {
						pages = Math.round(totalHeight / pageHeight) < (totalHeight / pageHeight)? Math.round(totalHeight / pageHeight) + 1
								: Math.round(totalHeight / pageHeight);
					}
					int totalPages = pages;
					while (pages > 0) {
						int colStart = maxHeaderCol;
						int colEnd = colSizes.size();
						int colCount = maxHeaderCol;
						float labDatColWidths = sumOfLabelColWidth;
						rowEnd = getRowsPerPage(table, rowStart, pageHeight);
						// if(totalPages!=pages) rowEnd = rowEnd - table.getHeaderRows();
						List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
						boolean newPageAdded = false;
						int i = maxHeaderCol;
						while (i < colSizes.size()) {
							labDatColWidths += colSizes.get(i);
							if (labDatColWidths > pageWidth) {
								if (!isgroupColumns) {
									// If non group reports then remove the 3 columns to adjust to the screen
									colEnd = i - 1;
									i = colEnd;
								} else {
									colEnd = i - 1;
								}
								tempTable = createTable(table, pdfpRows, colStart, colEnd, pages, totalPages,
										maxHeaderCol);
								float selecteColWidth = sumOfLabelColWidth;
								for (int j = colStart; j < colEnd; j++) {
									selecteColWidth = selecteColWidth + colsArry[j];
								}
								float percentWidth = (float) ((selecteColWidth * 100) / (pageWidth - 200));
								if (percentWidth > 99)
									percentWidth = 99;
								tempTable.setTotalWidth(selecteColWidth);
								tempTable.setWidthPercentage(percentWidth);
								float array[] = Arrays.copyOfRange(colsArry, 0, maxHeaderCol);
								tempTable.setWidths(mergeArrays(array, Arrays.copyOfRange(colsArry, colStart, colEnd)));
								colCount = maxHeaderCol;
								labDatColWidths = sumOfLabelColWidth + colSizes.get(i);
								colStart = colEnd;
								document.add(tempTable);
								document.newPage();
								newPageAdded = true;
							}
							i++;
							colCount++;
						}
						if (!newPageAdded)
							document.newPage();
						tempTable = createTable(table, pdfpRows, colStart, colSizes.size(), pages, totalPages,
								maxHeaderCol);
						float sumOfRemainingCols = getSumOfWidths(colSizes, colStart, colSizes.size(), maxHeaderCol);
						tempTable.setTotalWidth(sumOfRemainingCols + 20);
	
						float percentWidth = (float) ((sumOfRemainingCols * 100) / (pageWidth - 200));
						if (percentWidth > 99)
							percentWidth = 99;
						tempTable.setWidthPercentage(percentWidth);
	
						float array[] = Arrays.copyOfRange(colsArry, 0, maxHeaderCol);
						tempTable.setWidths(mergeArrays(array, Arrays.copyOfRange(colsArry, colStart, colSizes.size())));
	
						document.add(tempTable);
						rowStart = rowEnd;
						pages--;
						if (pages > 0)
							document.newPage();
					}
				}*/
				document.close();
				writer.close();
				exceptionCode.setResponse(filePath);
				exceptionCode.setOtherInfo(reportsVb.getReportTitle() + "_" + currentUserId);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			} catch (RuntimeCustomException e) {
				e.printStackTrace();
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				throw new RuntimeCustomException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				exceptionCode.setErrorMsg(e.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			}
		}catch(Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	private static float drawHeaderAndFootersRA(Document document, ReportsVb reportsVb, 
			List<PromptIdsVb> prompts, String assetFolderUrl) throws MalformedURLException, IOException, DocumentException{
		int height = 30;
		BaseFont baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
		PdfPTable borderTable = new PdfPTable(1);
		borderTable.setWidthPercentage(100);
		PdfPCell cellMain = new PdfPCell();
		cellMain.setBorder(0&0&0&1);
		//cellMain.setBackgroundColor(new BaseColor(90, 120, 138));
		cellMain.setBorderWidth(5);
		cellMain.setFixedHeight(150);
		//cellMain.setBackgroundColor(new BaseColor(205, 226, 236));//Std Vision Color
		cellMain.setBackgroundColor(new BaseColor(242,244,242)); // Light Grey
		PdfPTable headerTable = new PdfPTable(3);
		headerTable.setWidthPercentage(100);
		headerTable.setSpacingAfter(2);
		//promptsTable.setSpacingBefore(2);
		int widths[] = {1,5,1};
		headerTable.setWidths(widths);
		Image visionLogoUrl = Image.getInstance(assetFolderUrl+"/Product_Logo.PNG"); //Vision_rep_pdf.png
		PdfPCell cell1 = new PdfPCell();
//		cell1.setImage(visionLogoUrl);
		visionLogoUrl.scaleAbsolute(50f, 50f);
		cell1.addElement(visionLogoUrl);
//		visionLogoUrl.setWidthPercentage(70);
//		visionLogoUrl.setBackgroundColor(new BaseColor(235, 241, 222));
		///cell1.setBackgroundColor(new BaseColor(205, 226, 236));
		cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		//cell1.setFixedHeight(30);
		if(prompts != null && prompts.size()>2){
			visionLogoUrl.scaleAbsolute(60f, 60f);
			cell1.setPaddingTop(10);
//			cell1.setPaddingBottom(6);
		}
		cell1.setBorder(0&0&0&0);
		
		headerTable.addCell(cell1);
		List<PdfPCell> cells= new ArrayList<PdfPCell>();
			
		PdfPTable promptsTable = new PdfPTable(1);
		promptsTable.setWidthPercentage(100);
		
		PdfPCell cell3 = new PdfPCell();
		Font fontheading = new Font(baseFont);
		fontheading.setSize(8);
		fontheading.setStyle(Font.BOLD);
		Phrase ph= new Phrase(reportsVb.getReportTitle(),fontheading);
		cell3.setPhrase(ph);
		//cell3.setBackgroundColor(new BaseColor(205, 226, 236));
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setBorder(0&0&0&0);

		promptsTable.addCell(cell3);
		for(PdfPCell cell:cells){
			promptsTable.addCell(cell);
		}
					
		PdfPCell nestedCell = new PdfPCell();
		nestedCell.addElement(promptsTable);
		nestedCell.setPadding(0);
		nestedCell.setBorder(0);
		nestedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		nestedCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		//nestedCell.setBackgroundColor(new BaseColor(205, 226, 236));
		headerTable.addCell(nestedCell);
		
		Image sunoidaLogoUrl = Image.getInstance(assetFolderUrl+"/Bank_Logo.PNG");
		PdfPCell cell2 = new PdfPCell();
		sunoidaLogoUrl.scaleAbsolute(50f, 50f);
		cell2.setImage(sunoidaLogoUrl);
		//cell2.setBackgroundColor(new BaseColor(205, 226, 236));
		cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setBorder(0&0&0&0);
		cell2.setFixedHeight(30);
		if(prompts != null && prompts.size()>2){
			cell2.setPaddingTop(10);
//			cell2.setPaddingBottom(6);
		}		
		headerTable.addCell(cell2);
		
		cellMain.setFixedHeight(height);
		cellMain.setBorderWidth(0.5f);
		cellMain.setBorder(0&0&0&1);
		cellMain.addElement(headerTable);
		borderTable.addCell(cellMain);
		document.add(borderTable);
		Font promptFont = new Font(baseFont);
		promptFont.setSize(5);
		PdfPTable promptLabelTab = new PdfPTable(1);
		if (ValidationUtil.isValid(reportsVb.getPromptLabel())) {
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100);
		    PdfPCell cell;
		    Paragraph p = new Paragraph(new Phrase(reportsVb.getPromptLabel(),promptFont));
		    cell = new PdfPCell(p);
		    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    //cell.setBackgroundColor(new BaseColor(205, 226, 236));
		    table.addCell(cell);
		    document.add(table);
		}
		PdfPTable scalingTable = new PdfPTable(1);
		if (ValidationUtil.isValid(reportsVb.getDrillDownLabel())) {
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100);
		    PdfPCell cell;
		    Paragraph p = new Paragraph(new Phrase(reportsVb.getDrillDownLabel(),promptFont));
		    cell = new PdfPCell(p);
		    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    //cell.setBackgroundColor(new BaseColor(205, 226, 236));
		    table.addCell(cell);
		    document.add(table);
		}
//		return scalingTable.calculateHeights() + borderTable.calculateHeights()+promptLabelTab.calculateHeights();
		return borderTable.calculateHeights();
	}
	public  ExceptionCode exportMultiReportPdf(ReportsVb reportsVb, HashMap<String,ExceptionCode> resultMap,String assetFolderUrl){
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
		VisionUsersVb visionUsersVb =SessionContextHolder.getContext();
		int currentUserId = visionUsersVb.getVisionId();
		
		HashMap<String,String> sumMap = new HashMap<String,String>();
		ReportsVb vObject = new ReportsVb();
		Double sumVal = 0.0;
		Document document =  null;
		PdfPTable table = null;
		PdfPTable tempTable = null;
		
		float pageWidthSize = 0f;
		float pageHeightSize = 0f;
		List<ColumnHeadersVb> colHeaderslst = new ArrayList<>();
		List<HashMap<String, String>> dataLst = new ArrayList<>();
		List<HashMap<String, String>> totalLst = new ArrayList<>();
	
		pageWidthSize = PageSize.A4.getHeight();
		pageHeightSize= PageSize.A4.getWidth();
		
		colheaderFontSize = (int)6;
		dataFontSize = (int)5;
		summaryDataFontSize = (int)5;
		//String assetFolderUrl = commonDao.findVisionVariableValue("MDM_IMAGE_PATH");
		Rectangle rectangle = new Rectangle(pageWidthSize, pageHeightSize);
		document = new Document(rectangle, 20, 20, 20, 20);
		float widthAfterMargin = pageWidthSize - 40;
		String filePath = System.getProperty("java.io.tmpdir");

		File lFile = null;
		try {
			InvPdfPageEventHelper invPdfEvt = new InvPdfPageEventHelper(null, reportsVb.getMakerName(), assetFolderUrl,new ArrayList(), reportsVb);
			if (!ValidationUtil.isValid(filePath)) {
				filePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(filePath)) {
				filePath = filePath + File.separator;
			}
			lFile = new File(
					filePath + ValidationUtil.encode(reportsVb.getReportTitle()) + "_" + currentUserId + ".pdf");
			if (lFile.exists()) {
				lFile.delete();
			}
			lFile.createNewFile();
			FileOutputStream fips =  new FileOutputStream(lFile);
			PdfWriter writer = PdfWriter.getInstance(document,fips);
			writer.setPageEvent(invPdfEvt);
			BaseFont baseFont = BaseFont.createFont(assetFolderUrl+"/VERDANA.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
			document.open();
			
			int maxDataSize =  resultMap.size();
			int itCounter = 0;
			Set entrySet = resultMap.entrySet();
			Iterator it = entrySet.iterator();
			Boolean subTotalFlag = false;
			while (it.hasNext()) {
				//Map.Entry<String, ExceptionCode> entry = resultMap.entrySet();
				//ExceptionCode resultDataException = (ExceptionCode) entry.getValue();
				Map.Entry dataVal = (Map.Entry) it.next();
				ExceptionCode resultDataException  = (ExceptionCode) dataVal.getValue();
				vObject = (ReportsVb) resultDataException.getResponse();
				String reportTitle = (String)resultDataException.getRequest();
				String reportErrorMsg = resultDataException.getErrorMsg();
				//vObject.setScreenName(reportTitle);
				if(vObject != null)	{
					dataLst = vObject.getGridDataSet();
					totalLst = vObject.getTotal();
					colHeaderslst = ((ReportsVb) vObject).getColumnHeaderslst();
				} else {
					dataLst = new ArrayList<>();
					totalLst = new ArrayList<>();
					colHeaderslst = new ArrayList<>();
				}
				itCounter++;
				
				//dataLst = grpDataLst;
				subTotalFlag = false;
				sumMap = new HashMap<String, String>();
				ArrayList<ColumnHeadersVb> columnHeadersFinallst = new ArrayList<ColumnHeadersVb>();
				colHeaderslst.forEach(colHeadersVb -> {
					if(colHeadersVb.getColspan() <= 1) {
						columnHeadersFinallst.add(colHeadersVb);
					}
				});
				List<String> colTypes = new ArrayList<String>();
				Map<Integer,Integer> columnWidths = new HashMap<Integer,Integer>(colHeaderslst.size());
				for(int loopCnt= 0; loopCnt < colHeaderslst.size(); loopCnt++){
					columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
					ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
					if(colHVb.getColspan() <= 1) {
						colTypes.add(colHVb.getColType());
					}
				}
				PdfPTable groupingTable = new PdfPTable(1);
				groupingTable.setWidthPercentage(100);
				PdfPCell cellG;
				Font groupingFont = new Font(baseFont);
				groupingFont.setSize(8);
				groupingFont.setStyle(Font.BOLD);
				Phrase ph = new Phrase(reportTitle, groupingFont);
				Paragraph p = new Paragraph(ph);
				cellG = new PdfPCell(p);
				cellG.setHorizontalAlignment(Element.ALIGN_LEFT);
				cellG.setBorder(0);
				cellG.setPaddingTop(4f);
				// cell.setBackgroundColor(new BaseColor(205, 226, 236));
				groupingTable.addCell(cellG);
				document.add(groupingTable);

				int colNum = 0;
				int rowNum = 1;
				int loopCnt = 0;
				int subColumnCnt = 0;
				Boolean totalRowFlag = false;
				boolean isgroupColumns = false;
				Map<Integer, Float> colSizes = new HashMap<Integer, Float>();
				int maxHeaderRow = colHeaderslst.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
				int maxHeaderCol = colHeaderslst.stream().mapToInt(ColumnHeadersVb::getLabelColNum).max().orElse(0);

				List<ColumnHeadersVb> colHeadersCol = colHeaderslst.stream()
						.sorted(Comparator.comparingInt(ColumnHeadersVb::getLabelRowNum)).collect(Collectors.toList());

				if (colHeaderslst != null) {
					table = new PdfPTable(maxHeaderCol);
					table.setWidthPercentage(100);
					table.setHeaderRows(maxHeaderRow);
					table.setSpacingAfter(12);
					table.setSpacingBefore(12);
					Font font = new Font(baseFont);
					font.setColor(BaseColor.WHITE);
					font.setStyle(Font.BOLD);
					font.setSize(colheaderFontSize);
					if (maxHeaderRow != 1) {
						table.setSplitRows(false);
						table.setSplitLate(false);
					}
					table.getDefaultCell().setBackgroundColor(new BaseColor(79, 98, 40));
					table.getDefaultCell().setNoWrap(true);
					loopCnt = 0;
					boolean isNextRow = false;
					for (ColumnHeadersVb columnHeadersVb : colHeadersCol) {
						if (columnHeadersVb.getCaption().contains("<br/>")) {
							String value = columnHeadersVb.getCaption().replaceAll("<br/>", "\n");
							columnHeadersVb.setCaption(value);
						}
						PdfPCell cell = new PdfPCell(new Phrase(columnHeadersVb.getCaption(), font));
						cell.setBackgroundColor(new BaseColor(0, 92, 140)); //std blue color
						//cell.setBackgroundColor(new BaseColor(105,105,105)); //Dark Grey
						cell.setUseVariableBorders(true);
						cell.setBorderWidthTop(0.5f);
						cell.setBorderWidthBottom(0.5f);
						cell.setBorderColor(new BaseColor(222, 226, 230));
						cell.setNoWrap(false);
						cell.setPaddingBottom(4f);
						cell.setPaddingTop(4f);
						cell.setPaddingLeft(2f);
						cell.setPaddingRight(2f);

						String type = "";
						String colType = columnHeadersVb.getColType();
						if ("T".equalsIgnoreCase(colType)) {
							type = CAP_COL;
						} else {
							type = DATA_COL;
						}
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);

						if (columnHeadersVb.getRowspan() != 0) {
							cell.setRowspan(columnHeadersVb.getRowspan());
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						}
						if (columnHeadersVb.getColspan() != 0)
							cell.setColspan(columnHeadersVb.getColspan());
						cell.setNoWrap(true);
						if (columnHeadersVb.getLabelRowNum() > 1 && !isNextRow) {
							table.completeRow();
							table.getDefaultCell().setBackgroundColor(null);
							isNextRow = true;
						}
						table.addCell(cell);
						++colNum;
						if (columnHeadersVb.getColspan() == 0 || columnHeadersVb.getColspan() == 1) {
							float columnWidth = Float.parseFloat(columnHeadersVb.getColumnWidth());
							float pdfColWdth = widthAfterMargin * columnWidth / 100;
							colSizes.put(columnHeadersVb.getLabelColNum() - 1, pdfColWdth);
							loopCnt++;
						}
					}
				}
				// Writing The Header
				table.completeRow();
				table.getDefaultCell().setBackgroundColor(null);
				PdfPCell cell = null;
				int recCount = 0;
				int ctr = 1;

				// Writing the data
				do {
					if(dataLst != null) {
						for (HashMap dataLstMap : dataLst) {
							boolean highlight = false;
							String formatType = "D";
							if(dataLstMap.containsKey("FORMAT_TYPE")) {
								formatType = dataLstMap.get("FORMAT_TYPE").toString();
							}
							for (int loopCount = 0; loopCount < columnHeadersFinallst.size(); loopCount++) {
								ColumnHeadersVb colHeadersVb = columnHeadersFinallst.get(loopCount);
								if (colHeadersVb.getColspan() > 1)
									continue;
								int index = 0;
								String type = "";
								String colType = colTypes.get(loopCount);
								if ("T".equalsIgnoreCase(colType)) {
									type = CAP_COL;
								} else {
									type = DATA_COL;
								}
								String cellText = "";
								if (type.equalsIgnoreCase(CAP_COL)) {
									index = (loopCount + 1);
									String orgValue = "";
									if (dataLstMap.containsKey(colHeadersVb.getDbColumnName())) {
										orgValue = ((dataLstMap.get(colHeadersVb.getDbColumnName())) != null
												|| dataLstMap.get(colHeadersVb.getDbColumnName()) == "")
														? dataLstMap.get(colHeadersVb.getDbColumnName()).toString()
														: "";
									}
									cellText = orgValue;
									// cellText = findValue(reportsVb, DATA_COL, index);
									if (totalRowFlag || subTotalFlag) {
										if (!ValidationUtil.isValid(orgValue) && loopCount == 0) {
											orgValue = "Total ";
										}
										cellText = orgValue;
										if (loopCount == 0) {
											if (subTotalFlag)
												cellText = "Sub Total";
											if (totalRowFlag)
												cellText = "Total";
										} else {
											cellText = "";
										}
									}
									Font font = new Font(baseFont);
									font.setSize(dataFontSize);
									if (ValidationUtil.isValid(cellText)) {
										if ("FT".equalsIgnoreCase(formatType) || "S".equalsIgnoreCase(formatType)
												|| "ST".equalsIgnoreCase(formatType)) {
											font.setStyle(Font.BOLD);
											cell.setBackgroundColor(new BaseColor(205, 226, 236));
											if ("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
												font.setSize(summaryDataFontSize);
											}
										}
									}
									cell = new PdfPCell(new Phrase(cellText, font));
									cell.setBorder(0 & 0 & 0 & 1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColorBottom(new BaseColor(222, 226, 230));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
									cell.setPaddingBottom(4f);
									cell.setPaddingTop(4f);
									cell.setPaddingLeft(2f);
									cell.setPaddingRight(2f);
									if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
										cell.setBackgroundColor(new BaseColor(205, 226, 236));
									}
									if (recCount == dataLst.size() - 1) {
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(222, 226, 230));
									}
									if (loopCount == 0) {
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(222, 226, 230));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222, 226, 230));
									} else if (loopCount == maxHeaderCol - 1) {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222, 226, 230));
									} else {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222, 226, 230));
									}
									cell.setNoWrap(true);
									table.addCell(cell);
								} else {
									String orgValue = "";
									if (dataLstMap.containsKey(colHeadersVb.getDbColumnName())) {
										orgValue = ((dataLstMap.get(colHeadersVb.getDbColumnName())) != null
												|| dataLstMap.get(colHeadersVb.getDbColumnName()) == "")
														? dataLstMap.get(colHeadersVb.getDbColumnName()).toString()
														: "";
									}
									if (ValidationUtil.isValid(orgValue)) {
										if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)) {
											if (ValidationUtil.isNumericDecimal(orgValue)) {
												double amount = Double.parseDouble(orgValue);
												DecimalFormat formatter = new DecimalFormat("#,###");
												cellText = formatter.format(amount);
											} else {
												cellText = orgValue;
											}
										} else {
											if (ValidationUtil.isNumericDecimal(orgValue)) {
												double amount = Double.parseDouble(orgValue);
												DecimalFormat formatter = new DecimalFormat("#,##0.00");
												cellText = formatter.format(amount);
											} else {
												cellText = orgValue;
											}
										}
									} else {
										cellText = orgValue;
									}
									if (ValidationUtil.isValidId(cellText) && "-0.00".equalsIgnoreCase(cellText)) {
										cellText = "0.00";
									}
									
									Font font = new Font(baseFont);
									font.setSize(dataFontSize);
									cell = new PdfPCell(new Phrase(cellText, font));
									if("FT".equalsIgnoreCase(formatType) || "S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
										font.setStyle(Font.BOLD);
										if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
											font.setSize(summaryDataFontSize);
											cell.setBackgroundColor(new BaseColor(205, 226, 236));
										}
									}
									
									cell.setBorder(0 & 0 & 0 & 1);
									cell.setUseVariableBorders(true);
									cell.setBorderWidthBottom(0.5f);
									cell.setBorderColorBottom(new BaseColor(222, 226, 230));
									cell.setBorderColor(new BaseColor(222, 226, 230));
									cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
									cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
									cell.setPaddingBottom(4f);
									cell.setPaddingTop(4f);
									cell.setPaddingLeft(4f);
									cell.setPaddingRight(4f);
									if("S".equalsIgnoreCase(formatType) || "ST".equalsIgnoreCase(formatType)) {
										cell.setBackgroundColor(new BaseColor(205, 226, 236));
									}
									if (recCount == dataLst.size() - 1) {
										cell.setBorderWidthBottom(0.5f);
										cell.setBorderColorBottom(new BaseColor(222, 226, 230));
									}
									if (loopCount == 0) {
										cell.setBorderWidthLeft(0.5f);
										cell.setBorderColorLeft(new BaseColor(222, 226, 230));
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222, 226, 230));
									} else if (loopCount == maxHeaderCol - 1) {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222, 226, 230));
									} else {
										cell.setBorderWidthRight(0.1f);
										cell.setBorderColorRight(new BaseColor(222, 226, 230));
									}
									cell.setNoWrap(true);
									table.addCell(cell);
	
									String cellValue = orgValue.replaceAll(",", "");
									if("Y".equalsIgnoreCase(colHeadersVb.getSumFlag())) {
										String prevValstr = sumMap.containsKey(colHeadersVb.getDbColumnName())
												? sumMap.get(colHeadersVb.getDbColumnName())
												: "0";
										Double strVal = Double.parseDouble(prevValstr);
										sumVal = strVal + Double.parseDouble(cellValue);
										DecimalFormat formatter = new DecimalFormat("####.00");
										String cellText1 = formatter.format(sumVal);
										sumMap.put(colHeadersVb.getDbColumnName(), cellText1);
									}
								}
								if (cellText == null || cellText.trim().isEmpty()) {
									// colSizes.put(loopCount, Math.max(20, colSizes.get(loopCount)));
								} else {
	
									/*
									 * float width = Math.max(ColumnText.getWidth(cell.getPhrase()),
									 * colSizes.get(loopCount)); if (width < 20) width = 20; colSizes.put(loopCount,
									 * width);
									 */
								}
							}
							recCount++;
						}
					}else {
						PdfPTable noDataTab = new PdfPTable(1);
						noDataTab.setWidthPercentage(100);
						PdfPCell cellN;
						Font noDataFont = new Font(baseFont);
						noDataFont.setSize(7);
						noDataFont.setColor(new BaseColor(115,136,120));
						//groupingFont.setStyle(Font.BOLD);
						Phrase ph1 = new Phrase(reportErrorMsg, noDataFont);
						Paragraph p1 = new Paragraph(ph1);
						cellN = new PdfPCell(p1);
						cellN.setHorizontalAlignment(Element.ALIGN_CENTER);
						cellN.setBorder(0);
						cellN.setPaddingTop(4f);
						noDataTab.addCell(cellN);
						document.add(table);
						document.add(noDataTab);
					}
					ctr++;
					reportsVb.setCurrentPage(ctr);
					dataLst = new ArrayList();
					if (itCounter == maxDataSize) {
						if (!subTotalFlag) {
							if(!sumMap.isEmpty()) {
								sumMap.put("FORMAT_TYPE", "ST");
								dataLst.add(sumMap);
								subTotalFlag = true;
							}
							totalRowFlag = false;
						} else if (!totalRowFlag) {
							dataLst = totalLst;
							totalRowFlag = true;
							subTotalFlag = true;
						}
					} else {
						if (!subTotalFlag) {
							if(!sumMap.isEmpty()) {
								sumMap.put("FORMAT_TYPE", "ST");
								dataLst.add(sumMap);
								subTotalFlag = true;
							}
							totalRowFlag = false;
						}
					}
				} while (dataLst != null && !dataLst.isEmpty());

				float totalWidth = 0f;
				float sumOfLabelColWidth = 0f;
				float[] colsArry = new float[colSizes.size()];
				for (int i = 0; i < colSizes.size(); i++) {
					totalWidth += colSizes.get(i);
					colsArry[i] = colSizes.get(i);
					if (i < maxHeaderCol) {
						sumOfLabelColWidth += colSizes.get(i);
					}
				}
				float pageWidth = "L".equalsIgnoreCase(reportsVb.getReportOrientation()) ? PageSize.A4.getHeight()
						: PageSize.A4.getWidth();
				float pageHeight = ("L".equalsIgnoreCase(reportsVb.getReportOrientation()) ? PageSize.A4.getWidth()
						: PageSize.A4.getHeight())
						- (document.topMargin() + document.bottomMargin() + invPdfEvt.headerHeight + 50);
				// float pageWidth = reportsVb.getPdfWidth();
				// pageWidth = (pageWidth - (document.leftMargin() + document.rightMargin()));
				/* if (totalWidth < pageWidth) { */
				float percentWidth = (float) ((totalWidth * 100) / (pageWidth - 200));
				// if (percentWidth > 99)
				percentWidth = 99;
				int rowStart = 0;
				int rowEnd = table.getRows().size();
				table.setTotalWidth(totalWidth);
				table.setWidths(colsArry);
				float totalHeight = table.calculateHeights();
				// float pageHeight = reportsVb.getPdfHeight() - (document.topMargin() +
				// document.bottomMargin()+ invPdfEvt.headerHeight + table.spacingAfter());
				int pages = 1;
				if (totalHeight > pageHeight) {
					pages = Math.round(totalHeight / pageHeight) < (totalHeight / pageHeight)
							? Math.round(totalHeight / pageHeight) + 1
							: Math.round(totalHeight / pageHeight);
					totalHeight += pages * table.getHeaderHeight();
					pages = Math.round(totalHeight / pageHeight) < (totalHeight / pageHeight)
							? Math.round(totalHeight / pageHeight) + 1
							: Math.round(totalHeight / pageHeight);
				}

				int totalPages = pages;
				rowEnd = getRowsPerPage(table, rowStart, pageHeight);
				while (pages > 0) {
					List<PdfPRow> pdfpRows = table.getRows(rowStart, rowEnd);
					tempTable = createTable(table, pdfpRows, maxHeaderCol, table.getNumberOfColumns(), pages,
							totalPages, maxHeaderCol);
					tempTable.setTotalWidth(totalWidth);
					tempTable.setWidthPercentage(percentWidth);
					tempTable.setWidths(colsArry);
					// tempTable.setLockedWidth(true);
					document.add(tempTable);
					rowStart = rowEnd;
					rowEnd = getRowsPerPage(table, rowStart, pageHeight);
					pages--;
					if (pages - 1 != 0)
						rowEnd = rowEnd - table.getHeaderRows();
					if (pages > 0)
						document.newPage();
				}
				if (itCounter < maxDataSize)
					document.newPage();
			}
			document.close();
			writer.close();
			exceptionCode.setResponse(filePath);
			exceptionCode.setOtherInfo(reportsVb.getReportTitle() + "_" + currentUserId);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (RuntimeCustomException e) {
			e.printStackTrace();
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			throw new RuntimeCustomException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			return exceptionCode;
		}
		}catch(Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
}
