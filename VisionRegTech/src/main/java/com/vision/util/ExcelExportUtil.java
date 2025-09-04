package com.vision.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.net.util.Base64;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vision.exception.ExceptionCode;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.PromptIdsVb;
import com.vision.vb.ReportStgVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.ReportsWriterVb;
import com.vision.vb.TemplateNameVb;
import com.vision.vb.UploadFilingVb;
import com.vision.vb.WidgetVb;

public class ExcelExportUtil {
	public static int MAX_FETCH_RECORDS = 3000;
	public static int CELL_STYLE_HEADER_CAP_COL = 0; // csHeaderCaptionCol
	public static int CELL_STYLE_MID_HEADER_CAP_COL = 1; // csMidHeaderCaptionCol
	public static int CELL_STYLE_HEADER_DATA_COL = 2; // csHeaderDataCol
	public static int CELL_STYLE_DETAILS_CAP_COL = 3; // csDataAlt1 - White back ground
	public static int CELL_STYLE_DETAILS_CAP_COL_ALT = 4; // csDataAlt2 -Cream back Ground
	public static int CELL_STYLE_DETAILS_DATA_COL = 5; // csDataAlt1Data - White back ground - For Numeric Fields
	public static int CELL_STYLE_DETAILS_DATA_COL_ALT = 6; // csDataAlt2Data -Cream back Ground - For Numeric Fields
	public static int CELL_STYLE_SUMMERY_CAP_COL = 7; // csSumary
	public static int CELL_STYLE_SUMMERY_DATA_COL = 8; // csSumaryData - For Numeric Fields
	public static int CELL_STYLE_SUMMERY_CAP_COL_ALT = 9; // csSumaryAlt2 - Cream back Ground
	public static int CELL_STYLE_SUMMERY_DATA_COL_ALT = 10; // csSumaryData - Cream back Ground - For Numeric Fields
	public static int CELL_STYLE_TITLE_CAP = 11; // csTitleCaption - require for Scheduler
	public static int CELL_STYLE_PROMPTS = 12; // csPrompt - require for Scheduler
	public static int CELL_STYLE_HEADER_CAP_COL_TOP = 13; // csHeaderCaptionColTop

	public static int CELL_STYLE_DETAILS_DATA_COL_COUNT = 14; // csDataAlt1Data - White back ground - For Numeric Fields
																// (Non Decimals)
	public static int CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT = 15; // csDataAlt2Data -Cream back Ground - For Numeric
																	// Fields (Non Decimals)
	public static int CELL_STYLE_SUMMERY_DATA_COL_COUNT = 16; // csSumaryData - For Numeric Fields (Non Decimals)
	public static int CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT = 17; // csSumaryData - Cream back Ground - For Numeric
																	// Fields (Non Decimals)
	public static int CELL_STYLE_TITLES = 18; // csSumaryData
	public static int CELL_STYLE_REPORT_PROMPTS = 19;
	public static int CELL_STYLE_REPORT_PROMPTS_CAP = 20;

	public static final String CAP_COL = "captionColumn";
	public static final String DATA_COL = "dataColumn";

//	private static XSSFCellStyle createStyle(Workbook workBook, XSSFColor color, short allign, Font font,
//			String formatString, short borderBottom, XSSFColor borderBottomColor) {
//		XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
//		style.setFillForegroundColor(color);
//		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		style.setAlignment(allign);
//		style.setFont(font);
//		if (formatString != null) {
//			style.setDataFormat((short) BuiltinFormats.getBuiltinFormat(formatString));
//		}
//		if (borderBottom != CellStyle.BORDER_NONE) {
//			style.setBorderBottom(borderBottom);
//			style.setBorderRight(borderBottom);
//			style.setBottomBorderColor(borderBottomColor);
//			style.setRightBorderColor(borderBottomColor);
//		}
//		return style;
//	}
	private static XSSFCellStyle createStyle(Workbook workBook, XSSFColor color, HorizontalAlignment allign, Font font, 
			String formatString, BorderStyle borderBottom, XSSFColor borderBottomColor){
		XSSFCellStyle style = (XSSFCellStyle)workBook.createCellStyle();
		style.setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(allign);
	    style.setFont(font);
	    if(formatString != null){
	    	style.setDataFormat((short)BuiltinFormats.getBuiltinFormat(formatString));	
	    }
	    if(borderBottom !=BorderStyle.NONE){
	    	style.setBorderBottom(borderBottom);
	    	style.setBorderRight(borderBottom);
	    	style.setBottomBorderColor(borderBottomColor);
	    	style.setRightBorderColor(borderBottomColor);
	    }
	    return style;
	}

//	private static Font createFont(Workbook workBook, short colorIdx, short fontWeight, String fontName,
//			int heightInPoints) {
//		Font font = workBook.createFont();
//		font.setColor(colorIdx);
//		font.setBoldweight(fontWeight);
//		font.setFontName(fontName);
//		font.setFontHeightInPoints((short) heightInPoints);
//		return font;
//	}
	private static Font createFont(Workbook workBook, short colorIdx, Boolean fontWeight, String fontName, int heightInPoints){
		Font font = workBook.createFont();
		font.setColor(colorIdx);
		font.setBold(fontWeight);
		font.setFontName(fontName);
		font.setFontHeightInPoints((short)heightInPoints);
		return font;
	}
	
//	public static Map<Integer, XSSFCellStyle> createStyles(Workbook workBook) {
//		Map<Integer, XSSFCellStyle> styles = new HashMap<Integer, XSSFCellStyle>();
//		Font fontHeader = createFont(workBook, IndexedColors.WHITE.index, Font.BOLDWEIGHT_BOLD, "verdana", 10);
//		Font fontData = createFont(workBook, IndexedColors.BLACK.index, Font.BOLDWEIGHT_NORMAL, "verdana", 10);
//		Font fontSummary = createFont(workBook, IndexedColors.BLACK.index, Font.BOLDWEIGHT_BOLD, "verdana", 10);
//		Font fontHeaderTitle = createFont(workBook, IndexedColors.GREEN.index, Font.BOLDWEIGHT_BOLD, "verdana", 10);
//
//		/* byte[] greenClr = {(byte) 79, (byte) 98, (byte) 40}; */
//		// byte[] greenClr = {(byte) 3, (byte) 80, (byte) 122};
//		byte[] greenClr = { (byte) 0, (byte) 92, (byte) 140 };
//		XSSFColor greenXClor = new XSSFColor(greenClr);
//		byte[] pinkClr = { (byte) 230, (byte) 184, (byte) 183 };
//		XSSFColor pinkXClor = new XSSFColor(pinkClr);
//		XSSFColor whiteClr = new XSSFColor();
//		whiteClr.setIndexed(IndexedColors.WHITE.index);
//		byte[] creemClr = { (byte) 205, (byte) 226, (byte) 236 };
//		XSSFColor creemXClor = new XSSFColor(creemClr);
//		// Dark pink for Summary
//		byte[] darkPinkClr = { (byte) 177, (byte) 19, (byte) 27 };
//		XSSFColor blackClr = new XSSFColor(darkPinkClr);
//		byte[] DgreenClr = { (byte) 54, (byte) 67, (byte) 27 };
//		XSSFColor DgreenXClor = new XSSFColor(DgreenClr);
//
//		XSSFCellStyle csHeaderCaptionColTop = createStyle(workBook, greenXClor, CellStyle.ALIGN_CENTER_SELECTION,
//				fontHeader, null, CellStyle.BORDER_THIN, whiteClr); // For Multi headers reports
//		XSSFCellStyle csHeaderCaptionCol = createStyle(workBook, greenXClor, CellStyle.ALIGN_LEFT, fontHeader, null,
//				CellStyle.BORDER_NONE, null);// For header
//		XSSFCellStyle csMidHeaderCaptionCol = createStyle(workBook, DgreenXClor, CellStyle.ALIGN_LEFT, fontHeader, null,
//				CellStyle.BORDER_NONE, null);// For Mid header
//		XSSFCellStyle csDataAlt1 = createStyle(workBook, whiteClr, CellStyle.ALIGN_GENERAL, fontData,
//				BuiltinFormats.getBuiltinFormat(0), CellStyle.BORDER_THIN, pinkXClor); // For Caption With background
//		XSSFCellStyle csDataAlt1Data = createStyle(workBook, whiteClr, CellStyle.ALIGN_RIGHT, fontData,
//				BuiltinFormats.getBuiltinFormat(4), CellStyle.BORDER_THIN, pinkXClor); // For Data With background
//		XSSFCellStyle csDataAlt2 = createStyle(workBook, creemXClor, CellStyle.ALIGN_GENERAL, fontData,
//				BuiltinFormats.getBuiltinFormat(0), CellStyle.BORDER_THIN, pinkXClor);// For Caption With out background
//		XSSFCellStyle csDataAlt2Data = createStyle(workBook, creemXClor, CellStyle.ALIGN_RIGHT, fontData,
//				BuiltinFormats.getBuiltinFormat(4), CellStyle.BORDER_THIN, pinkXClor);// For Data With out background
//		XSSFCellStyle csHeaderDataCol = createStyle(workBook, greenXClor, CellStyle.ALIGN_RIGHT, fontHeader, null,
//				CellStyle.BORDER_NONE, null);// For header Right Align
//		XSSFCellStyle csReportTitle = createStyle(workBook, whiteClr, CellStyle.ALIGN_CENTER_SELECTION, fontHeaderTitle,
//				null, CellStyle.BORDER_NONE, greenXClor); // For Headings
//
//		XSSFCellStyle csSumary = createStyle(workBook, whiteClr, CellStyle.ALIGN_GENERAL, fontSummary, null,
//				CellStyle.BORDER_THIN, pinkXClor);
//		csSumary.setBorderTop(CellStyle.BORDER_THIN);
//		csSumary.setTopBorderColor(pinkXClor);
//
//		XSSFCellStyle csSumaryData = createStyle(workBook, whiteClr, CellStyle.ALIGN_RIGHT, fontSummary,
//				BuiltinFormats.getBuiltinFormat(4), CellStyle.BORDER_THIN, pinkXClor); // For Data Summary
//		csSumaryData.setBorderTop(CellStyle.BORDER_THIN);
//		csSumaryData.setTopBorderColor(pinkXClor);
//
//		XSSFCellStyle csSumaryAlt2 = createStyle(workBook, creemXClor, CellStyle.ALIGN_GENERAL, fontSummary, null,
//				CellStyle.BORDER_THIN, pinkXClor);
//		csSumaryAlt2.setBorderTop(CellStyle.BORDER_THIN);
//		csSumaryAlt2.setTopBorderColor(pinkXClor);
//
//		XSSFCellStyle csSumaryDataAlt2 = createStyle(workBook, creemXClor, CellStyle.ALIGN_RIGHT, fontSummary,
//				BuiltinFormats.getBuiltinFormat(4), CellStyle.BORDER_THIN, pinkXClor); // For Data Summary with Alt clr
//		csSumaryDataAlt2.setBorderTop(CellStyle.BORDER_THIN);
//		csSumaryDataAlt2.setTopBorderColor(pinkXClor);
//
//		XSSFCellStyle csDataAlt1DataForCount = createStyle(workBook, whiteClr, CellStyle.ALIGN_RIGHT, fontData,
//				BuiltinFormats.getBuiltinFormat(3), CellStyle.BORDER_THIN, pinkXClor); // For Data With background
//		XSSFCellStyle csDataAlt2DataForCount = createStyle(workBook, creemXClor, CellStyle.ALIGN_RIGHT, fontData,
//				BuiltinFormats.getBuiltinFormat(3), CellStyle.BORDER_MEDIUM, pinkXClor);// For Data With out background
//		XSSFCellStyle csSumaryDataForCount = createStyle(workBook, whiteClr, CellStyle.ALIGN_RIGHT, fontSummary,
//				BuiltinFormats.getBuiltinFormat(3), CellStyle.BORDER_THIN, pinkXClor); // For Data Summary
//		csSumaryDataForCount.setBorderTop(CellStyle.BORDER_THIN);
//		csSumaryDataForCount.setTopBorderColor(pinkXClor);
//		XSSFCellStyle csSumaryDataAlt2ForCount = createStyle(workBook, creemXClor, CellStyle.ALIGN_RIGHT, fontSummary,
//				BuiltinFormats.getBuiltinFormat(3), CellStyle.BORDER_THIN, pinkXClor); // For Data Summary with Alt clr
//		csSumaryDataAlt2ForCount.setBorderTop(CellStyle.BORDER_THIN);
//		csSumaryDataAlt2ForCount.setTopBorderColor(pinkXClor);
//
//		styles.put(CELL_STYLE_HEADER_CAP_COL_TOP, csHeaderCaptionColTop);
//		styles.put(CELL_STYLE_HEADER_CAP_COL, csHeaderCaptionCol);
//		styles.put(CELL_STYLE_MID_HEADER_CAP_COL, csMidHeaderCaptionCol);
//		styles.put(CELL_STYLE_HEADER_DATA_COL, csHeaderDataCol);
//		styles.put(CELL_STYLE_DETAILS_CAP_COL, csDataAlt1);
//		styles.put(CELL_STYLE_DETAILS_CAP_COL_ALT, csDataAlt2);
//		styles.put(CELL_STYLE_DETAILS_DATA_COL, csDataAlt1Data);
//		styles.put(CELL_STYLE_DETAILS_DATA_COL_ALT, csDataAlt2Data);
//		styles.put(CELL_STYLE_SUMMERY_CAP_COL, csSumary);
//		styles.put(CELL_STYLE_SUMMERY_DATA_COL, csSumaryData);
//		styles.put(CELL_STYLE_SUMMERY_CAP_COL_ALT, csSumaryAlt2);
//		styles.put(CELL_STYLE_SUMMERY_DATA_COL_ALT, csSumaryDataAlt2);
//
//		styles.put(CELL_STYLE_DETAILS_DATA_COL_COUNT, csDataAlt1DataForCount);
//		styles.put(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT, csDataAlt2DataForCount);
//		styles.put(CELL_STYLE_SUMMERY_DATA_COL_COUNT, csSumaryDataForCount);
//		styles.put(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT, csSumaryDataAlt2ForCount);
//		styles.put(CELL_STYLE_TITLES, csReportTitle);
//
//		return styles;
//	}
	public static Map<Integer, XSSFCellStyle> createStyles(Workbook workBook){
        Map<Integer, XSSFCellStyle> styles = new HashMap<Integer, XSSFCellStyle>();
		Font fontHeader = createFont(workBook, IndexedColors.WHITE.index, true, "verdana", 10);
		Font fontData  = createFont(workBook, IndexedColors.BLACK.index, false, "verdana", 10);
		Font fontSummary = createFont(workBook, IndexedColors.BLACK.index, true, "verdana", 10);
		Font fontHeaderTitle = createFont(workBook, IndexedColors.GREEN.index,true, "verdana", 10);
		
		/*byte[] greenClr = {(byte) 79, (byte) 98, (byte) 40};*/
	//	byte[] greenClr = {(byte) 3, (byte) 80, (byte) 122};
		byte[] greenClr = {(byte) 0, (byte) 92, (byte) 140};
		XSSFColor greenXClor = new XSSFColor(greenClr);
		byte[] pinkClr = {(byte) 230, (byte) 184, (byte) 183};
		XSSFColor pinkXClor = new XSSFColor(pinkClr);
		XSSFColor whiteClr = new XSSFColor();
	    whiteClr.setIndexed(IndexedColors.WHITE.index);
	    byte[] creemClr = {(byte) 205, (byte) 226, (byte) 236};
		XSSFColor creemXClor = new XSSFColor(creemClr);
		//Dark pink for Summary 
		byte[] darkPinkClr = {(byte) 177, (byte) 19, (byte) 27};
		XSSFColor blackClr = new XSSFColor(darkPinkClr);
		byte[] DgreenClr = {(byte) 54, (byte) 67, (byte) 27};
		XSSFColor DgreenXClor = new XSSFColor(DgreenClr);
		
		
		XSSFCellStyle csHeaderCaptionColTop = createStyle(workBook, greenXClor, HorizontalAlignment.CENTER_SELECTION, fontHeader, null, BorderStyle.THIN , whiteClr); //For Multi headers reports
		XSSFCellStyle csHeaderCaptionCol =  createStyle(workBook, greenXClor, HorizontalAlignment.LEFT, fontHeader, null, BorderStyle.NONE, null);// For header
		XSSFCellStyle csMidHeaderCaptionCol =  createStyle(workBook, DgreenXClor, HorizontalAlignment.LEFT, fontHeader, null, BorderStyle.NONE , null);// For Mid header
	    XSSFCellStyle csDataAlt1 = createStyle(workBook, whiteClr, HorizontalAlignment.GENERAL,fontData, BuiltinFormats.getBuiltinFormat(0), BorderStyle.THIN, pinkXClor); //For Caption With background
	    XSSFCellStyle csDataAlt1Data =  createStyle(workBook, whiteClr, HorizontalAlignment.RIGHT, fontData, BuiltinFormats.getBuiltinFormat(4), BorderStyle.THIN, pinkXClor); //For Data With background
	    XSSFCellStyle csDataAlt2 =  createStyle(workBook, creemXClor, HorizontalAlignment.GENERAL, fontData, BuiltinFormats.getBuiltinFormat(0), BorderStyle.THIN, pinkXClor);//For Caption With out background
	    XSSFCellStyle csDataAlt2Data =  createStyle(workBook, creemXClor, HorizontalAlignment.RIGHT, fontData, BuiltinFormats.getBuiltinFormat(4), BorderStyle.THIN, pinkXClor);//For Data With out background
		XSSFCellStyle csHeaderDataCol = createStyle(workBook, greenXClor, HorizontalAlignment.RIGHT, fontHeader, null, BorderStyle.NONE , null);// For header Right Align
		XSSFCellStyle csReportTitle =  createStyle(workBook, whiteClr, HorizontalAlignment.CENTER_SELECTION, fontHeaderTitle, null, BorderStyle.NONE , greenXClor); //For Headings
		
		XSSFCellStyle csSumary = createStyle(workBook, whiteClr, HorizontalAlignment.GENERAL, fontSummary, null, BorderStyle.THIN, pinkXClor);
		csSumary.setBorderTop(BorderStyle.THIN);
		csSumary.setTopBorderColor(pinkXClor);
		
		XSSFCellStyle csSumaryData = createStyle(workBook, whiteClr, HorizontalAlignment.RIGHT, fontSummary, BuiltinFormats.getBuiltinFormat(4), BorderStyle.THIN, pinkXClor); //For Data Summary
		csSumaryData.setBorderTop(BorderStyle.THIN);
		csSumaryData.setTopBorderColor(pinkXClor);
		
		XSSFCellStyle csSumaryAlt2 = createStyle(workBook, creemXClor, HorizontalAlignment.GENERAL, fontSummary, null,BorderStyle.THIN, pinkXClor);
		csSumaryAlt2.setBorderTop(BorderStyle.THIN);
		csSumaryAlt2.setTopBorderColor(pinkXClor);
	
		XSSFCellStyle csSumaryDataAlt2 = createStyle(workBook, creemXClor, HorizontalAlignment.RIGHT, fontSummary, BuiltinFormats.getBuiltinFormat(4), BorderStyle.THIN, pinkXClor); //For Data Summary with Alt clr
		csSumaryDataAlt2.setBorderTop(BorderStyle.THIN);
		csSumaryDataAlt2.setTopBorderColor(pinkXClor);

		
		XSSFCellStyle csDataAlt1DataForCount =  createStyle(workBook, whiteClr, HorizontalAlignment.RIGHT, fontData, BuiltinFormats.getBuiltinFormat(3), BorderStyle.THIN, pinkXClor); //For Data With background
		XSSFCellStyle csDataAlt2DataForCount =  createStyle(workBook, creemXClor, HorizontalAlignment.RIGHT, fontData, BuiltinFormats.getBuiltinFormat(3), BorderStyle.MEDIUM, pinkXClor);//For Data With out background
		XSSFCellStyle csSumaryDataForCount = createStyle(workBook, whiteClr, HorizontalAlignment.RIGHT, fontSummary, BuiltinFormats.getBuiltinFormat(3), BorderStyle.THIN, pinkXClor); //For Data Summary
		csSumaryDataForCount.setBorderTop(BorderStyle.THIN);
		csSumaryDataForCount.setTopBorderColor(pinkXClor);
		XSSFCellStyle csSumaryDataAlt2ForCount = createStyle(workBook, creemXClor, HorizontalAlignment.RIGHT, fontSummary, BuiltinFormats.getBuiltinFormat(3), BorderStyle.THIN, pinkXClor); //For Data Summary with Alt clr
		csSumaryDataAlt2ForCount.setBorderTop(BorderStyle.THIN);
		csSumaryDataAlt2ForCount.setTopBorderColor(pinkXClor);
		
		
	
		
		styles.put(CELL_STYLE_HEADER_CAP_COL_TOP, csHeaderCaptionColTop);
		styles.put(CELL_STYLE_HEADER_CAP_COL,csHeaderCaptionCol);
		styles.put(CELL_STYLE_MID_HEADER_CAP_COL,csMidHeaderCaptionCol);
		styles.put(CELL_STYLE_HEADER_DATA_COL, csHeaderDataCol);
		styles.put(CELL_STYLE_DETAILS_CAP_COL,csDataAlt1);
		styles.put(CELL_STYLE_DETAILS_CAP_COL_ALT,csDataAlt2);
		styles.put(CELL_STYLE_DETAILS_DATA_COL,csDataAlt1Data);
		styles.put(CELL_STYLE_DETAILS_DATA_COL_ALT,csDataAlt2Data);
		styles.put(CELL_STYLE_SUMMERY_CAP_COL,csSumary);
		styles.put(CELL_STYLE_SUMMERY_DATA_COL,csSumaryData);
		styles.put(CELL_STYLE_SUMMERY_CAP_COL_ALT,csSumaryAlt2);
		styles.put(CELL_STYLE_SUMMERY_DATA_COL_ALT,csSumaryDataAlt2);
		
		styles.put(CELL_STYLE_DETAILS_DATA_COL_COUNT,csDataAlt1DataForCount);
		styles.put(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT,csDataAlt2DataForCount);
		styles.put(CELL_STYLE_SUMMERY_DATA_COL_COUNT,csSumaryDataForCount);
		styles.put(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT,csSumaryDataAlt2ForCount);
		styles.put(CELL_STYLE_TITLES,csReportTitle);
		
        return styles;
	}

	public static void createTemplateFile(File lFile) {
		FileOutputStream fileOS = null;
		try {
			if (lFile.exists()) {
				lFile.delete();
			}
			lFile.createNewFile();
			// Create a template file and save it on to disk
			XSSFWorkbook workBook = new XSSFWorkbook();
			workBook.createSheet();
			fileOS = new FileOutputStream(lFile);
			workBook.write(fileOS);
			fileOS.flush();
			fileOS.close();
			fileOS = null;
			workBook = null;
		} catch (Exception e) {
			if (fileOS != null) {
				try {
					fileOS.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	public static void writeHeadersForListData(Sheet sheet, List<ColumnHeadersVb> columnHeaders,
			Map<Integer, XSSFCellStyle> styles, int rowNum, Map<Integer, Integer> columnWidths) {
		int colNum = 0;
		Row row = null;
		Cell cell = null;
		// Plain Data to be displayed.
		row = sheet.createRow(rowNum);
		// row.setRowStyle(csHeader);
		// Add headers
		try {
			for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
				cell = row.createCell(colNum);
				String caption = columnHeadersVb.getCaption() == null ? " " : columnHeadersVb.getCaption();
				if (caption.contains(",")) {
					String[] parts = caption.split(",");
					caption = parts[1];
				}
				cell.setCellValue(caption.replaceAll("_", " "));
				cell.setCellStyle(styles.get(CELL_STYLE_HEADER_CAP_COL));
				columnWidths.put(colNum, (int) getColumnWidth(cell, (styles.get(CELL_STYLE_HEADER_CAP_COL)).getFont(),
						true, columnWidths.get(colNum)));
				++colNum;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int writeHeadersForListData(ReportsWriterVb reportWriterVb, Sheet sheet,
			List<ColumnHeadersVb> columnHeaders, Map<Integer, XSSFCellStyle> styles, int rowNum,
			Map<Integer, Integer> columnWidths) {
		int colNum = 0;
		Row row = null;
		Row row1 = null;
		Row row2 = null;
		Row row3 = null;
		Row row4 = null;
		Row row5 = null;
		Row row6 = null;
		Row row7 = null;
		Cell cell = null;
		int firstHeaderRowno = rowNum;
//		row1 = sheet.createRow(firstHeaderRowno);
//		rowNum++;
		for (int rowIndex = 1; rowIndex <= reportWriterVb.getLabelRowCount(); rowIndex++) {
			if (rowIndex == 1) {
				row1 = sheet.createRow(rowNum);
			} else if (rowIndex == 2) {
				row2 = sheet.createRow(rowNum);
			} else if (rowIndex == 3) {
				row3 = sheet.createRow(rowNum);
			} else if (rowIndex == 4) {
				row4 = sheet.createRow(rowNum);
			} else if (rowIndex == 5) {
				row5 = sheet.createRow(rowNum);
			} else if (rowIndex == 6) {
				row6 = sheet.createRow(rowNum);
			} else if (rowIndex == 7) {
				row7 = sheet.createRow(rowNum);
			}
			for (int colIndex = 0; colIndex < reportWriterVb.getLabelColCount(); colIndex++) {
				if (rowIndex == 1) {
					cell = row1.createCell(colIndex);
				} else if (rowIndex == 2) {
					cell = row2.createCell(colIndex);
				} else if (rowIndex == 3) {
					cell = row3.createCell(colIndex);
				} else if (rowIndex == 4) {
					cell = row4.createCell(colIndex);
				} else if (rowIndex == 5) {
					cell = row5.createCell(colIndex);
				} else if (rowIndex == 6) {
					cell = row6.createCell(colIndex);
				} else if (rowIndex == 7) {
					cell = row7.createCell(colIndex);
				}
				cell.setCellStyle(styles.get(CELL_STYLE_HEADER_CAP_COL_TOP));
			}
			rowNum++;
		}
		for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
			int rowStart = firstHeaderRowno + columnHeadersVb.getLabelRowNum() - 1;
			int rowEnd = rowStart + (columnHeadersVb.getRowspan() != 0 ? columnHeadersVb.getRowspan() - 1 : 0);
			int columnStart = columnHeadersVb.getLabelColNum() - 1;
			// int columnEnd = (columnHeadersVb.getColSpanNum() == 0 ?
			// columnHeadersVb.getLabelColNum() : columnHeadersVb.getColSpanNum())-1;
			int columnEnd = ((columnHeadersVb.getColspan() - 1) == -1 ? 0 : columnHeadersVb.getColspan() - 1);
			/*
			 * if("Registered".equalsIgnoreCase(columnHeadersVb.getCaption())){
			 * System.out.println("rowStart["+rowStart+"] rowEnd["+rowEnd+"] columnStart["
			 * +columnStart+"] end["+columnEnd+"]"); }
			 */
			if (columnHeadersVb.getLabelRowNum() == 1) {
				row = row1;
			} else if (columnHeadersVb.getLabelRowNum() == 2) {
				row = row2;
			} else if (columnHeadersVb.getLabelRowNum() == 3) {
				row = row3;
			} else if (columnHeadersVb.getLabelRowNum() == 4) {
				row = row4;
			} else if (columnHeadersVb.getLabelRowNum() == 5) {
				row = row5;
			} else if (columnHeadersVb.getLabelRowNum() == 6) {
				row = row6;
			} else if (columnHeadersVb.getLabelRowNum() == 7) {
				row = row7;
			}
			cell = row.getCell(columnStart);
			if (columnHeadersVb.getColspan() != 0 || columnHeadersVb.getRowspan() != 0) {
				int end = columnStart + columnEnd;
				/*
				 * if("Registered".equalsIgnoreCase(columnHeadersVb.getCaption())){
				 * System.out.println("rowStart["+rowStart+"] rowEnd["+rowEnd+"] columnStart["
				 * +columnStart+"] end["+end+"]"); }
				 */
				sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, columnStart, end));
			}
			cell.setCellStyle(styles.get(CELL_STYLE_HEADER_CAP_COL_TOP));
			cell.setCellValue(columnHeadersVb.getCaption().replaceAll("_", " "));
			if (columnHeadersVb.getCaption().contains("<br/>")) {
				columnHeadersVb.setCaption(columnHeadersVb.getCaption().replaceAll("<br/>", "\n"));
				cell.setCellValue(columnHeadersVb.getCaption().replaceAll("_", " "));
				cell.getCellStyle().setWrapText(true);
			}
			if (columnHeadersVb.getColspan() == 0) {
				columnWidths.put(colNum, (int) getColumnWidth(cell, (styles.get(CELL_STYLE_HEADER_CAP_COL)).getFont(),
						true, columnWidths.get(colNum)));
				++colNum;
			}
		}
		return rowNum;
	}

	private static double getColumnWidth(Cell cell, boolean useMergedCells, double width) {
		DataFormatter formatter = new DataFormatter();
		int defaultCharWidth = 5;// (int)layout.getAdvance();
		/*
		 * double cellWidth = SheetUtil.getCellWidth(cell, defaultCharWidth, formatter,
		 * useMergedCells); if (cellWidth != -1) { cellWidth *= 256; int maxColumnWidth
		 * = 255*256; // The maximum column width for an individual cell is 255
		 * characters if (cellWidth > maxColumnWidth) { cellWidth = maxColumnWidth; }
		 * width = Math.max(width, cellWidth); }
		 */
		return width;
	}

	private static double getColumnWidth(Cell cell, Font font, boolean useMergedCells, double width) {
		DataFormatter formatter = new DataFormatter();
		int defaultCharWidth = 5;// (int)layout.getAdvance();
//        double cellWidth = SheetUtil.getCellWidth(cell, defaultCharWidth, formatter, useMergedCells);
//        if (cellWidth != -1) {
//        	cellWidth *= 256;
//		    int maxColumnWidth = 255*256; // The maximum column width for an individual cell is 255 characters
//		    if (cellWidth > maxColumnWidth) {
//		    	cellWidth = maxColumnWidth;
//		    }=========================
//		    width = Math.max(width, cellWidth);
//		}
		return width;
	}

	public static void addImageAndAutoSizeColsForListData(Workbook workBook, Sheet sheet, String imageData,
			int headerCnt, int rowNum, Map<Integer, Integer> columnWidths) {
		for (int loopCount = 0; loopCount < headerCnt; loopCount++) {
			sheet.setColumnWidth(loopCount, columnWidths.get(loopCount));
		}
		if (imageData != null) {
			ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, headerCnt), (short) 4);
//			anchor.setAnchorType(3);
			anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
			int colorIndex = workBook.addPicture(Base64.decodeBase64(imageData), Workbook.PICTURE_TYPE_PNG);
			Drawing drawing = sheet.createDrawingPatriarch();
			drawing.createPicture(anchor, colorIndex);
		}
		workBook.setPrintArea(workBook.getSheetIndex(sheet), 0, headerCnt + 1, 0, rowNum + 1);
	}

	private static int getColumnNoForWidth(Sheet sheet, int headerCnt) {
		float width = 0;
		for (int loopCnt = 0; loopCnt < headerCnt; loopCnt++) {
			width += sheet.getColumnWidth(loopCnt);
			if (width >= 32000) {
				return loopCnt;
			}
		}
		return headerCnt;
	}

	public static int writeListData(Sheet sheet, List<String> colTyps, Map<Integer, XSSFCellStyle> styls,
			String xmlData, int rowNum, Map<Integer, Integer> columnWidths) {
		Row row = null;
		Cell cell = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xmlData.getBytes(Charset.forName("UTF-8")));
			// parse using builder to get DOM representation of the XML file
			org.w3c.dom.Document dom = db.parse(is);
			org.w3c.dom.Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("tableRow");
			if (nl != null && nl.getLength() > 0) {
				for (int loopCnt = 0; loopCnt < nl.getLength(); loopCnt++) {
					row = sheet.createRow(rowNum);
					// get the employee element
					Element el = (Element) nl.item(loopCnt);
					NodeList ncl = el.getChildNodes();
					NodeList formatTypeNodes = el.getElementsByTagName("formatType");
					NodeList headerTextNodes = el.getElementsByTagName("headerText");

					String formatType = "";
					String headerText = "";
					/*
					 * if(formatTypeNodes != null && formatTypeNodes.item(0) != null) formatType =
					 * formatTypeNodes.item(0).getTextContent(); if(headerTextNodes != null &&
					 * headerTextNodes.item(0) != null) headerText =
					 * headerTextNodes.item(0).getTextContent();
					 */
					if (headerTextNodes != null && headerTextNodes.item(0) != null)
						headerText = headerTextNodes.item(0) == null || headerTextNodes.item(0).getFirstChild() == null
								? ""
								: headerTextNodes.item(0).getFirstChild().getNodeValue();
					if (formatTypeNodes != null && formatTypeNodes.item(0) != null)
						formatType = formatTypeNodes.item(0) == null || formatTypeNodes.item(0).getFirstChild() == null
								? ""
								: formatTypeNodes.item(0).getFirstChild().getNodeValue();

					for (int loopCount = 0; loopCount < ncl.getLength(); loopCount++) {
						cell = row.createCell(loopCount);
						Font font = null;
						Element el1 = (Element) ncl.item(loopCount);
						if (el1 != null && "formatType".equalsIgnoreCase(el1.getNodeName())) {
							continue;
						}
						if (el1 != null && "headerText".equalsIgnoreCase(el1.getNodeName())) {
							continue;
						}

						if ("N".equalsIgnoreCase(colTyps.get(loopCount))) {
							String cellValue = el1 != null && el1.getFirstChild() != null
									? ValidationUtil.replaceComma(el1.getFirstChild().getNodeValue().trim())
									: "";
							if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
									&& (loopCnt == 0 || loopCnt % 2 == 0)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
								font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT).getFont();
							} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
								font = styls.get(CELL_STYLE_SUMMERY_DATA_COL).getFont();
							} else if ("B".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
								font = styls.get(CELL_STYLE_DETAILS_CAP_COL).getFont();
							} else if (loopCnt == 0 || loopCnt % 2 == 0) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT).getFont();
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
							}
							if (ValidationUtil.isNumericDecimal(cellValue)) {
//								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.valueOf(cellValue));
							} else {
								cell.setCellValue(cellValue);
							}
						} else if ("C".equalsIgnoreCase(colTyps.get(loopCount))) {
							if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
									&& (loopCnt == 0 || loopCnt % 2 == 0)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
								font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT).getFont();
							} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
								font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT).getFont();
							} else if ("B".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
								font = styls.get(CELL_STYLE_DETAILS_CAP_COL).getFont();
							} else if (loopCnt == 0 || loopCnt % 2 == 0) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT).getFont();
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT).getFont();
							}
							String cellValue = el1 != null && el1.getFirstChild() != null
									? ValidationUtil.replaceComma(el1.getFirstChild().getNodeValue())
									: "";
							if (ValidationUtil.isValidId(cellValue)) {
//								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.valueOf(cellValue));
							} else {
								cell.setCellValue(cellValue);
							}
						} else {
							if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
									&& (loopCnt == 0 || loopCnt % 2 == 0)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
								font = styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT).getFont();
							} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
								font = styls.get(CELL_STYLE_SUMMERY_CAP_COL).getFont();
							} else if ("B".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
								font = styls.get(CELL_STYLE_DETAILS_CAP_COL).getFont();
							} else if (loopCnt == 0 || loopCnt % 2 == 0) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT));
								font = styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT).getFont();
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
								font = styls.get(CELL_STYLE_DETAILS_CAP_COL).getFont();
							}

							String cellValue = el1 == null || el1.getFirstChild() == null ? ""
									: el1.getFirstChild().getNodeValue();
							cell.setCellValue(cellValue);
						}
						columnWidths.put(loopCount,
								(int) getColumnWidth(cell, font, true, columnWidths.get(loopCount)));
					}
					rowNum++;
				}
			}
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return rowNum;
	}

	public static int writeListDataModify(ReportsWriterVb reportsWriterVb, Sheet sheet, List<String> colTyps,
			Map<Integer, XSSFCellStyle> styls, String xmlData, int rowNum, Map<Integer, Integer> columnWidths,
			List<ColumnHeadersVb> columnHeaders) {
		Row row = null;
		Cell cell = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xmlData.getBytes(Charset.forName("UTF-8")));
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);
			Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("tableRow");
			if (nl != null && nl.getLength() > 0) {
				for (int loopCnt = 0; loopCnt < nl.getLength(); loopCnt++) {
					row = sheet.createRow(rowNum);
					// get the employee element
					Element el = (Element) nl.item(loopCnt);
					int internalStatus = 0;
					try {
						NodeList internalStatusNode = el.getElementsByTagName("INTERNAL_STATUS");
						internalStatus = Integer.parseInt(internalStatusNode.item(0).getFirstChild().getTextContent());
					} catch (NullPointerException npe) {
						internalStatus = 0;
					} catch (Exception e) {
						internalStatus = 0;
					}
					NodeList ncl = el.getChildNodes();
					NodeList formatTypeNodes = el.getElementsByTagName("formatType");
					NodeList headerTextNodes = el.getElementsByTagName("headerText");

					String formatType = "";
					String headerText = "";
					if (headerTextNodes != null && headerTextNodes.item(0) != null)
						headerText = headerTextNodes.item(0) == null || headerTextNodes.item(0).getFirstChild() == null
								? ""
								: headerTextNodes.item(0).getFirstChild().getNodeValue();
					if (formatTypeNodes != null && formatTypeNodes.item(0) != null)
						formatType = formatTypeNodes.item(0) == null || formatTypeNodes.item(0).getFirstChild() == null
								? ""
								: formatTypeNodes.item(0).getFirstChild().getNodeValue();

					if ("HT".equalsIgnoreCase(formatType) || "FHT".equalsIgnoreCase(formatType)) {
						int colNum = 0;
						// Plain Data to be displayed.
						for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
							cell = row.createCell(colNum);
							String caption = "";
							if (colNum == 0) {
								caption = headerText == null ? " " : headerText;
							}
							cell.setCellValue(caption.replaceAll("_", " "));
							cell.setCellStyle(styls.get(CELL_STYLE_MID_HEADER_CAP_COL));
							columnWidths.put(colNum,
									(int) getColumnWidth(cell, (styls.get(CELL_STYLE_MID_HEADER_CAP_COL)).getFont(),
											true, columnWidths.get(colNum)));
							++colNum;
						}
						rowNum = rowNum + 1;
						row = sheet.createRow(rowNum);
					}

					if ("H".equalsIgnoreCase(formatType) || "HT".equalsIgnoreCase(formatType)
							|| "FHT".equalsIgnoreCase(formatType)) {
						writeHeadersForListData(reportsWriterVb, sheet, columnHeaders, styls, rowNum, columnWidths);
						// rowNum = rowNum + 1;
						// row = sheet.createRow(rowNum);
					}

					for (int loopCount = 0; loopCount < ncl.getLength(); loopCount++) {
						cell = row.createCell(loopCount);
						Font font = null;
						Element el1 = (Element) ncl.item(loopCount);
						if (!"INTERNAL_STATUS".equalsIgnoreCase(el1.getTagName())
								&& !("EFFECTIVE_DATE".equalsIgnoreCase(el1.getTagName()) && internalStatus == 0)) {
							if (el1 != null && "formatType".equalsIgnoreCase(el1.getNodeName())) {
								continue;
							}
							if (el1 != null && "headerText".equalsIgnoreCase(el1.getNodeName())) {
								continue;
							}

							if ("N".equalsIgnoreCase(colTyps.get(loopCount))) {
								String cellValue = el1 != null && el1.getFirstChild() != null
										? ValidationUtil.replaceComma(el1.getFirstChild().getNodeValue().trim())
										: "";
								if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
										&& (loopCnt == 0 || loopCnt % 2 == 0)) {
									cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
									font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT).getFont();
								} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
									cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
									font = styls.get(CELL_STYLE_SUMMERY_DATA_COL).getFont();
								} else if ("B".equalsIgnoreCase(formatType)) {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
									font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
								} else if (loopCnt == 0 || loopCnt % 2 == 0) {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT));
									font = styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT).getFont();
								} else {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
									font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
								}
								if (ValidationUtil.isNumericDecimal(cellValue)) {
//									cell.setCellType(Cell.CELL_TYPE_NUMERIC);
									cell.setCellType(CellType.NUMERIC);
									cell.setCellValue(Double.valueOf(cellValue));
								} else {
									cell.setCellValue(cellValue);
								}
							} else if ("C".equalsIgnoreCase(colTyps.get(loopCount))) {
								if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
										&& (loopCnt == 0 || loopCnt % 2 == 0)) {
									cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
									font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT).getFont();
								} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
									cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
									font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT).getFont();
								} else if ("B".equalsIgnoreCase(formatType)) {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
									font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
								} else if (loopCnt == 0 || loopCnt % 2 == 0) {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT));
									font = styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT).getFont();
								} else {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
									font = styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT).getFont();
								}
								String cellValue = el1 != null && el1.getFirstChild() != null
										? ValidationUtil.replaceComma(el1.getFirstChild().getNodeValue())
										: "";
								if (ValidationUtil.isValidId(cellValue)) {
//									cell.setCellType(Cell.CELL_TYPE_NUMERIC);
									cell.setCellType(CellType.NUMERIC);
									cell.setCellValue(Double.valueOf(cellValue));
								} else {
									cell.setCellValue(cellValue);
								}
							} else {
								if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
										&& (loopCnt == 0 || loopCnt % 2 == 0)) {
									cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
									font = styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT).getFont();
								} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
									cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
									font = styls.get(CELL_STYLE_SUMMERY_CAP_COL).getFont();
								} else if ("B".equalsIgnoreCase(formatType)) {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
									font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
								} else if (loopCnt == 0 || loopCnt % 2 == 0) {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT));
									font = styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT).getFont();
									/*
									 * For template design export to change row color to red or blue to indicate the
									 * change in template
									 */
									/*
									 * if(internalStatus==1 || internalStatus==2){
									 * cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_BLUE)); font =
									 * styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_BLUE).getFont(); }else
									 * if(internalStatus==3){
									 * cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_RED)); font =
									 * styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_RED).getFont(); }else{
									 * cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT)); font =
									 * styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT).getFont(); }
									 */
								} else {
									cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
									font = styls.get(CELL_STYLE_DETAILS_CAP_COL).getFont();
									/*
									 * For template design export to change row color to red or blue to indicate the
									 * change in template
									 */
									/*
									 * if(internalStatus==1 || internalStatus==2){
									 * cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_BLUE)); font =
									 * styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_BLUE).getFont(); }else
									 * if(internalStatus==3){
									 * cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_RED)); font =
									 * styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_RED).getFont(); }else{
									 * cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL)); font =
									 * styls.get(CELL_STYLE_DETAILS_CAP_COL).getFont(); }
									 */
								}
								String cellValue = el1 == null || el1.getFirstChild() == null ? ""
										: el1.getFirstChild().getNodeValue();
								cell.setCellValue(cellValue);
							}
							columnWidths.put(loopCount,
									(int) getColumnWidth(cell, font, true, columnWidths.get(loopCount)));
						}
					}
					rowNum++;
				}
			}

		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return rowNum;
	}

//	private static void createStylesForPrompts(Workbook workBook, Map<Integer, XSSFCellStyle> styles) {
//
//		XSSFCellStyle csTitleCaption = (XSSFCellStyle) workBook.createCellStyle();
//		XSSFCellStyle csPrompt = (XSSFCellStyle) workBook.createCellStyle();
//
//		Font fontHeader1 = workBook.createFont();
//		fontHeader1.setColor(IndexedColors.BLACK.index);
//		fontHeader1.setBoldweight(Font.BOLDWEIGHT_BOLD);
//		fontHeader1.setFontName("verdana");
//		fontHeader1.setFontHeightInPoints((short) 10);
//
//		Font fontHeader = workBook.createFont();
//		fontHeader.setColor(IndexedColors.BLACK.index);
//		fontHeader.setFontName("verdana");
//		fontHeader.setFontHeightInPoints((short) 8);
//
//		/*
//		 * XSSFColor creemXClor = new XSSFColor();
//		 * creemXClor.setIndexed(IndexedColors.WHITE.index);
//		 */
//		// byte[] creemClr = {(byte) 235, (byte) 241, (byte) 222};
//		byte[] creemClr = { (byte) 205, (byte) 226, (byte) 236 };
//		XSSFColor creemXClor = new XSSFColor(creemClr);
//		csTitleCaption.setFillForegroundColor(creemXClor);
//		csTitleCaption.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		csTitleCaption.setAlignment(CellStyle.ALIGN_CENTER);
//		csTitleCaption.setFont(fontHeader1);
//
//		csPrompt.setFillForegroundColor(creemXClor);
//		csPrompt.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		csPrompt.setAlignment(CellStyle.ALIGN_CENTER);
//		csPrompt.setFont(fontHeader);
//
//		styles.put(CELL_STYLE_TITLE_CAP, csTitleCaption);
//		styles.put(CELL_STYLE_PROMPTS, csPrompt);
//	}
private static void createStylesForPrompts(Workbook workBook,  Map<Integer, XSSFCellStyle> styles){
		
		XSSFCellStyle csTitleCaption = (XSSFCellStyle)  workBook.createCellStyle();
		XSSFCellStyle csPrompt = (XSSFCellStyle)  workBook.createCellStyle();
		
		Font fontHeader1 = workBook.createFont();
		fontHeader1.setColor(IndexedColors.BLACK.index);
		fontHeader1.setBold(true);
		fontHeader1.setFontName("verdana");
		fontHeader1.setFontHeightInPoints((short)10);
		
		Font fontHeader = workBook.createFont();
		fontHeader.setColor(IndexedColors.BLACK.index);
		fontHeader.setFontName("verdana");
		fontHeader.setFontHeightInPoints((short)8);
		
/*		XSSFColor creemXClor = new XSSFColor();
		creemXClor.setIndexed(IndexedColors.WHITE.index);*/
		//byte[] creemClr = {(byte) 235, (byte) 241, (byte) 222};	
		byte[] creemClr = {(byte) 205, (byte) 226, (byte) 236};	
		XSSFColor creemXClor = new XSSFColor(creemClr);		
	    csTitleCaption.setFillForegroundColor(creemXClor);
	    csTitleCaption.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    csTitleCaption.setAlignment(HorizontalAlignment.CENTER);
	    csTitleCaption.setFont(fontHeader1);
	    
	    csPrompt.setFillForegroundColor(creemXClor);
	    csPrompt.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    csPrompt.setAlignment(HorizontalAlignment.CENTER);
	    csPrompt.setFont(fontHeader);
	    
	    styles.put(CELL_STYLE_TITLE_CAP, csTitleCaption);
	    styles.put(CELL_STYLE_PROMPTS, csPrompt);
	}

	public static int createPrompts(ReportsWriterVb reportWriterVb, List<PromptIdsVb> prompts, Sheet sheet,
			Workbook workBook, String assetFolderUrl, Map<Integer, XSSFCellStyle> styles, int headerCnt) {
		int intRow = 1;
		Row row = null;
		Cell cell = null;
		createStylesForPrompts(workBook, styles);
		try {
			int intCol = 0;
			row = sheet.createRow(intRow);
			intRow++;
//				intCol++;
			int loopCount = 5;// getColumnWidth(sheet, headerCnt);
			cell = row.createCell(intCol);
			sheet.setColumnWidth(0, 6000);
			drawImageToSheet(workBook, sheet, "logo.png", 0, 1, 1, 4, true, assetFolderUrl);
			sheet.setColumnWidth(loopCount + 1, 6000);
			drawImageToSheet(workBook, sheet, "Sunoida_Excel.PNG", 6, 7, 1, 4, true, assetFolderUrl);
			intCol++;
			cell = row.createCell(intCol);
			cell.setCellValue(reportWriterVb.getReportTitle());
			cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
			intCol++;

			int cnt = 1;
			String prvCtrl = "";
			String lbl = "";
			if (prompts != null && prompts.size() > 0) {
				for (PromptIdsVb promptIdsVbTemp12 : prompts) {
					row = sheet.createRow(intRow);
					intCol = 1;
					if (cnt == 3) {
						cnt = 1;
						intCol = 1;
						lbl = StringUtils.trimTrailingWhitespace(lbl);
						cell = row.createCell(intCol);
						cell.setCellValue(lbl);
						cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
						// intCol++;
						intRow++;
						lbl = "";
						row = sheet.createRow(intRow);
					}
					if ("COMBO".equalsIgnoreCase(promptIdsVbTemp12.getPromptType())
							|| "TREE".equalsIgnoreCase(promptIdsVbTemp12.getPromptType())
							|| promptIdsVbTemp12.getPromptType().contains("TEXT")) {
						if ((lbl + promptIdsVbTemp12.getPromptString() + " : "
								+ promptIdsVbTemp12.getSelectedValue1().getField2()).length() < 50)
							lbl = lbl + "                ";
						if ((lbl + promptIdsVbTemp12.getPromptString() + " : "
								+ promptIdsVbTemp12.getSelectedValue1().getField2()).length() < 100)
							lbl = lbl + "        ";
						String promptStrData = "";
						if (promptIdsVbTemp12.getPromptType().contains("TEXT")) {
							promptStrData = promptIdsVbTemp12.getSelectedValue1().getField1();
						} else {
							promptStrData = promptIdsVbTemp12.getSelectedValue1().getField2();
						}
						lbl = StringUtils.trimLeadingWhitespace(lbl) + promptIdsVbTemp12.getPromptString() + " : "
								+ promptStrData + "	";
						prvCtrl = promptIdsVbTemp12.getPromptType();
					} else if ("RANGE".equalsIgnoreCase(promptIdsVbTemp12.getPromptType())) {
						if (ValidationUtil.isValid(prvCtrl)) {
//								System.out.println("lbl : "+lbl);
							lbl = StringUtils.trimWhitespace(lbl);
							cell = row.createCell(intCol);
							cell.setCellValue(lbl);
							cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
							intRow++;
							lbl = "";
							row = sheet.createRow(intRow);
						}
						String tabs = "                ";
						if ((promptIdsVbTemp12.getPromptString() + "     From : "
								+ promptIdsVbTemp12.getSelectedValue1().getField1() + "," + " To : "
								+ promptIdsVbTemp12.getSelectedValue2().getField1()).length() < 100) {
							tabs = "                ";
						}
						lbl = promptIdsVbTemp12.getPromptString() + "     From : "
								+ promptIdsVbTemp12.getSelectedValue1().getField1() + "," + tabs + " To : "
								+ promptIdsVbTemp12.getSelectedValue2().getField1();
						lbl = StringUtils.trimTrailingWhitespace(lbl);
						cell = row.createCell(intCol);
					} else {
						if (promptIdsVbTemp12.getSelectedValue1() != null) {
							if ((lbl + promptIdsVbTemp12.getPromptString() + " : "
									+ promptIdsVbTemp12.getSelectedValue1().getField2()).length() < 50)
								lbl = lbl + "                ";
							if ((lbl + promptIdsVbTemp12.getPromptString() + " : "
									+ promptIdsVbTemp12.getSelectedValue1().getField2()).length() < 100)
								lbl = lbl + "        ";
							String promptStrData = "";
							promptStrData = promptIdsVbTemp12.getSelectedValue1().getField1();
							lbl = StringUtils.trimLeadingWhitespace(lbl) + promptIdsVbTemp12.getPromptString() + " : "
									+ promptStrData + "    ";
							prvCtrl = promptIdsVbTemp12.getPromptType();
						}
					}
					cnt++;
				}
			}
			lbl = StringUtils.trimTrailingWhitespace(lbl);
			cell = row.createCell(intCol);
			intCol++;
			cell.setCellValue(lbl);
			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));

			intRow = intRow + 2;
			row = sheet.createRow(intRow);
			cell = row.createCell(1);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm:ss a");
			String text = reportWriterVb.getMakerName() + "                " + dateFormat.format(new Date());
			cell.setCellValue(text);
			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));

			if ("R".equalsIgnoreCase(reportWriterVb.getReportType())) {
				intRow++;
				row = sheet.createRow(intRow);
				cell = row.createCell(1);
				cell.setCellValue("Scaling Factor :" + reportWriterVb.getScalingFactor());
				cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
			}
			for (int i = 1; i <= intRow; i++) {
				sheet.addMergedRegion(new CellRangeAddress(i, i, 1, loopCount));
			}
//				intRow++;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return intRow;
	}

	public static void addImagesToExcel(Sheet sheet, Workbook workBook, String assetFolderUrl) {
		try {
			int loopCount = 5;// getColumnWidth(sheet, headerCnt);
			sheet.setColumnWidth(0, 6000);
			FileInputStream is = new FileInputStream(assetFolderUrl + "/Vision_Excel.PNG");
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			is.close();
			Drawing drawing = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, 1), (short) 4);
//				XSSFClientAnchor anchor = new XSSFClientAnchor();
//			anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
			anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			anchor.setCol1(0);
			anchor.setRow1(2);
			/*
			 * anchor.setDx1(5000); anchor.setDx2(5000); anchor.setDy1(5000);
			 * anchor.setDy2(5000); System.out.println("Dx1 : "+anchor.getDx1());
			 * System.out.println("Dx2 : "+anchor.getDx2());
			 * System.out.println("Dy1 : "+anchor.getDy1());
			 * System.out.println("Dy2 : "+anchor.getDy2());
			 */
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.resize(1);

			sheet.setColumnWidth(loopCount + 1, 6000);
			FileInputStream is1 = new FileInputStream(assetFolderUrl + "/Sunoida_Excel.PNG");
			byte[] bytes1 = IOUtils.toByteArray(is1);
			int pictureIdx1 = workBook.addPicture(bytes1, Workbook.PICTURE_TYPE_PNG);
			is1.close();
			Drawing drawing1 = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, loopCount + 2), (short) 4);
//			anchor1.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
			anchor1.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			anchor1.setCol1(loopCount + 1);
			anchor1.setRow1(2);
//				anchor1.setCol2(loopCount+2);
			Picture pict1 = drawing1.createPicture(anchor1, pictureIdx1);
			pict1.resize(1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int writeHeaders(ReportsWriterVb reportWriterVb, List<ColumnHeadersVb> columnHeaders, int rowNum,
			SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styls, List<String> colTypes,
			Map<Integer, Integer> columnWidths) {
		Row row = null;
		Row row1 = null;
		Row row2 = null;
		Row row3 = null;
		Row row4 = null;
		Row row5 = null;
		Row row6 = null;
		Row row7 = null;
		Cell cell = null;
		int firstHeaderRowno = rowNum;
		for (int rowIndex = 1; rowIndex <= reportWriterVb.getLabelRowCount(); rowIndex++) {
			if (rowIndex == 1) {
				row1 = sheet.createRow(rowNum);
			} else if (rowIndex == 2) {
				row2 = sheet.createRow(rowNum);
			} else if (rowIndex == 3) {
				row3 = sheet.createRow(rowNum);
			} else if (rowIndex == 4) {
				row4 = sheet.createRow(rowNum);
			} else if (rowIndex == 5) {
				row5 = sheet.createRow(rowNum);
			} else if (rowIndex == 6) {
				row6 = sheet.createRow(rowNum);
			} else if (rowIndex == 7) {
				row7 = sheet.createRow(rowNum);
			}
			for (int colIndex = 0; colIndex < reportWriterVb.getLabelColCount(); colIndex++) {
				if (rowIndex == 1) {
					cell = row1.createCell(colIndex);
				} else if (rowIndex == 2) {
					cell = row2.createCell(colIndex);
				} else if (rowIndex == 3) {
					cell = row3.createCell(colIndex);
				} else if (rowIndex == 4) {
					cell = row4.createCell(colIndex);
				} else if (rowIndex == 5) {
					cell = row5.createCell(colIndex);
				} else if (rowIndex == 6) {
					cell = row6.createCell(colIndex);
				} else if (rowIndex == 7) {
					cell = row7.createCell(colIndex);
				}
				cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
			}
			rowNum++;
		}
		for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
			int rowStart = firstHeaderRowno + columnHeadersVb.getLabelRowNum() - 1;
			int rowEnd = rowStart + (columnHeadersVb.getRowspan() != 0 ? columnHeadersVb.getRowspan() - 1 : 0);
			int columnStart = columnHeadersVb.getLabelColNum() - 1;

			int columnEnd = (columnHeadersVb.getColspan() != 0 ? columnHeadersVb.getColspan() - 1 : 0);
			// int columnEnd = (columnHeadersVb.getColSpanNum() == 0 ?
			// columnHeadersVb.getLabelColNum() : columnHeadersVb.getColSpanNum())-1;
			if (columnHeadersVb.getLabelRowNum() == 1) {
				row = row1;
			} else if (columnHeadersVb.getLabelRowNum() == 2) {
				row = row2;
			} else if (columnHeadersVb.getLabelRowNum() == 3) {
				row = row3;
			} else if (columnHeadersVb.getLabelRowNum() == 4) {
				row = row4;
			} else if (columnHeadersVb.getLabelRowNum() == 5) {
				row = row5;
			} else if (columnHeadersVb.getLabelRowNum() == 6) {
				row = row6;
			} else if (columnHeadersVb.getLabelRowNum() == 7) {
				row = row7;
			}
			cell = row.getCell(columnStart);
			if (columnHeadersVb.getColspan() != 0 || columnHeadersVb.getRowspan() != 0) {
				int end = columnStart + columnEnd;
				sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, columnStart, end));
			}
			cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
			cell.setCellValue(columnHeadersVb.getCaption());
			if (columnHeadersVb.getCaption().contains("<br/>")) {
				columnHeadersVb.setCaption(columnHeadersVb.getCaption().replaceAll("<br/>", "\n"));
				cell.setCellValue(columnHeadersVb.getCaption().replaceAll("_", " "));
				cell.getCellStyle().setWrapText(true);
			}
		}
		return rowNum;
	}

	public static int writeReportData(List<ReportStgVb> reportsStgs, ReportsWriterVb reportWriterVb, SXSSFSheet sheet,
			int rowNum, int headerCnt, Map<Integer, XSSFCellStyle> styls, List<String> columnTypes,
			Map<Integer, Integer> columnWidths) {
		Row row = null;
		Cell cell = null;
		for (ReportStgVb reportStgVb : reportsStgs) {
			row = sheet.createRow(rowNum);
			for (int loopCount = 0; loopCount < reportWriterVb.getLabelColCount(); loopCount++) {
				cell = row.createCell(loopCount);
				int index = 0;
//				String type = (reportWriterVb.getLabelColCount() - (loopCount+1)) >= 0 ? CAP_COL : DATA_COL;
				String colType = columnTypes.size() > loopCount && columnTypes.get(loopCount) != null
						? columnTypes.get(loopCount)
						: "T";
				String type = "";
				if (loopCount < reportWriterVb.getCaptionLabelColCount())
					type = CAP_COL;
				else
					type = DATA_COL;

				if (CAP_COL.equalsIgnoreCase(type)) {
					index = (loopCount + 1);
					switch (getStyle(reportStgVb, rowNum, "T")) {
					case 1:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
						break;
					case 2:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT));
						break;
					case 3:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
						break;
					default:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
					}
					XSSFRichTextString string = new XSSFRichTextString(findValue(reportStgVb, type, index));
					cell.setCellValue(string);
					columnWidths.put(loopCount, (int) getColumnWidth(cell, null, true, columnWidths.get(loopCount)));
				} else {
					index = ((loopCount + 1) - reportWriterVb.getCaptionLabelColCount());
					switch (getStyle(reportStgVb, rowNum, colType)) {
					case 1:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
						break;
					case 2:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT));
						break;
					case 3:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
						break;

					case 5:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
						break;
					case 6:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT));
						break;
					case 7:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
						break;
					case 8:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
						break;

					default:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
					}
					String cellValue = ValidationUtil.replaceComma(findValue(reportStgVb, type, index));
					if (!ValidationUtil.isNumericDecimal(cellValue)) {
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
						cell.setCellValue(cellValue);
					} else if ("C".equalsIgnoreCase(colType) && ValidationUtil.isValidId(cellValue)) {
//						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellType(CellType.NUMERIC);
						// cell.setCellValue(Integer.parseInt(cellValue));
						cell.setCellValue(Long.parseLong(cellValue));
					} else if (ValidationUtil.isNumericDecimal(cellValue)) {
//						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(Double.valueOf(cellValue));
					} else {
						cell.setCellValue(cellValue);
					}
					/*
					 * if(reportWriterVb.getLabelRowCount() < 2)
					 * columnWidths.put(loopCount,(int)getColumnWidth(cell, null, true,
					 * columnWidths.get(loopCount)));
					 */
				}
			}
			rowNum++;
		}
		return rowNum;
	}

	public static String findValue(ReportStgVb reportsStgs, String type, int index) {
		if (CAP_COL.equalsIgnoreCase(type)) {
			switch (index) {
			case 1:
				return reportsStgs.getCaptionColumn1() == null ? "" : reportsStgs.getCaptionColumn1();
			case 2:
				return reportsStgs.getCaptionColumn2() == null ? "" : reportsStgs.getCaptionColumn2();
			case 3:
				return reportsStgs.getCaptionColumn3() == null ? "" : reportsStgs.getCaptionColumn3();
			case 4:
				return reportsStgs.getCaptionColumn4() == null ? "" : reportsStgs.getCaptionColumn4();
			case 5:
				return reportsStgs.getCaptionColumn5() == null ? "" : reportsStgs.getCaptionColumn5();
			}
		} else if (DATA_COL.equalsIgnoreCase(type)) {
			switch (index) {
			case 1:
				return reportsStgs.getDataColumn1() == null ? "" : reportsStgs.getDataColumn1();
			case 2:
				return reportsStgs.getDataColumn2() == null ? "" : reportsStgs.getDataColumn2();
			case 3:
				return reportsStgs.getDataColumn3() == null ? "" : reportsStgs.getDataColumn3();
			case 4:
				return reportsStgs.getDataColumn4() == null ? "" : reportsStgs.getDataColumn4();
			case 5:
				return reportsStgs.getDataColumn5() == null ? "" : reportsStgs.getDataColumn5();
			case 6:
				return reportsStgs.getDataColumn6() == null ? "" : reportsStgs.getDataColumn6();
			case 7:
				return reportsStgs.getDataColumn7() == null ? "" : reportsStgs.getDataColumn7();
			case 8:
				return reportsStgs.getDataColumn8() == null ? "" : reportsStgs.getDataColumn8();
			case 9:
				return reportsStgs.getDataColumn9() == null ? "" : reportsStgs.getDataColumn9();
			case 10:
				return reportsStgs.getDataColumn10() == null ? "" : reportsStgs.getDataColumn10();
			case 11:
				return reportsStgs.getDataColumn11() == null ? "" : reportsStgs.getDataColumn11();
			case 12:
				return reportsStgs.getDataColumn12() == null ? "" : reportsStgs.getDataColumn12();
			case 13:
				return reportsStgs.getDataColumn13() == null ? "" : reportsStgs.getDataColumn13();
			case 14:
				return reportsStgs.getDataColumn14() == null ? "" : reportsStgs.getDataColumn14();
			case 15:
				return reportsStgs.getDataColumn15() == null ? "" : reportsStgs.getDataColumn15();
			case 16:
				return reportsStgs.getDataColumn16() == null ? "" : reportsStgs.getDataColumn16();
			case 17:
				return reportsStgs.getDataColumn17() == null ? "" : reportsStgs.getDataColumn17();
			case 18:
				return reportsStgs.getDataColumn18() == null ? "" : reportsStgs.getDataColumn18();
			case 19:
				return reportsStgs.getDataColumn19() == null ? "" : reportsStgs.getDataColumn19();
			case 20:
				return reportsStgs.getDataColumn20() == null ? "" : reportsStgs.getDataColumn20();
			case 21:
				return reportsStgs.getDataColumn21() == null ? "" : reportsStgs.getDataColumn21();
			case 22:
				return reportsStgs.getDataColumn22() == null ? "" : reportsStgs.getDataColumn22();
			case 23:
				return reportsStgs.getDataColumn23() == null ? "" : reportsStgs.getDataColumn23();
			case 24:
				return reportsStgs.getDataColumn24() == null ? "" : reportsStgs.getDataColumn24();
			case 25:
				return reportsStgs.getDataColumn25() == null ? "" : reportsStgs.getDataColumn25();
			case 26:
				return reportsStgs.getDataColumn26() == null ? "" : reportsStgs.getDataColumn26();
			case 27:
				return reportsStgs.getDataColumn27() == null ? "" : reportsStgs.getDataColumn27();
			case 28:
				return reportsStgs.getDataColumn28() == null ? "" : reportsStgs.getDataColumn28();
			case 29:
				return reportsStgs.getDataColumn29() == null ? "" : reportsStgs.getDataColumn29();
			case 30:
				return reportsStgs.getDataColumn30() == null ? "" : reportsStgs.getDataColumn30();
			case 31:
				return reportsStgs.getDataColumn31() == null ? "" : reportsStgs.getDataColumn31();
			case 32:
				return reportsStgs.getDataColumn32() == null ? "" : reportsStgs.getDataColumn32();
			case 33:
				return reportsStgs.getDataColumn33() == null ? "" : reportsStgs.getDataColumn33();
			case 34:
				return reportsStgs.getDataColumn34() == null ? "" : reportsStgs.getDataColumn34();
			case 35:
				return reportsStgs.getDataColumn35() == null ? "" : reportsStgs.getDataColumn35();
			case 36:
				return reportsStgs.getDataColumn36() == null ? "" : reportsStgs.getDataColumn36();
			case 37:
				return reportsStgs.getDataColumn37() == null ? "" : reportsStgs.getDataColumn37();
			case 38:
				return reportsStgs.getDataColumn38() == null ? "" : reportsStgs.getDataColumn38();
			case 39:
				return reportsStgs.getDataColumn39() == null ? "" : reportsStgs.getDataColumn39();
			case 40:
				return reportsStgs.getDataColumn40() == null ? "" : reportsStgs.getDataColumn40();
			}
		}
		return "";
	}

	protected static int getStyle(ReportStgVb reportStgVb, int rowNum, String colType) {
		if ((rowNum % 2) == 0 && ("S".equalsIgnoreCase(reportStgVb.getFormatType())
				|| "G".equalsIgnoreCase(reportStgVb.getFormatType()))) {
			if ("C".equalsIgnoreCase(colType))
				return 5;
			return 1; // Summary with Background
		} else if ((rowNum % 2) == 0) {
			if ("C".equalsIgnoreCase(colType))
				return 6;
			return 2; // Non Summary with background.
		} else if ("S".equalsIgnoreCase(reportStgVb.getFormatType())
				|| "G".equalsIgnoreCase(reportStgVb.getFormatType())) {
			if ("C".equalsIgnoreCase(colType))
				return 7;
			return 3;// Summary with out Background
		} else {
			if ("C".equalsIgnoreCase(colType))
				return 8;
			return 4;// Non Summary With out background
		}
	}

	public static int createPromptsWidget(WidgetVb widgetVb, Sheet sheet, Workbook workBook, String assetFolderUrl,
			Map<Integer, XSSFCellStyle> styles, int headerCnt) {
		int intRow = 1;
		Row row = null;
		Cell cell = null;
		createStylesForPrompts(workBook, styles);
		try {
			int intCol = 0;
			row = sheet.createRow(intRow);
			intRow++;
			intCol++;
			int loopCount = 5;// getColumnWidth(sheet, headerCnt);
			sheet.setColumnWidth(0, 6000);
			FileInputStream is = new FileInputStream(assetFolderUrl + "/Vision_Excel.PNG");
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			is.close();
			Drawing drawing = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, 1), (short) 4);
//			anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
			anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			anchor.setCol1(0);
			anchor.setRow1(2);
			/*
			 * anchor.setDx1(5000); anchor.setDx2(5000); anchor.setDy1(5000);
			 * anchor.setDy2(5000); System.out.println("Dx1 : "+anchor.getDx1());
			 * System.out.println("Dx2 : "+anchor.getDx2());
			 * System.out.println("Dy1 : "+anchor.getDy1());
			 * System.out.println("Dy2 : "+anchor.getDy2());
			 */
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.resize(1);

			cell = row.createCell(intCol);
			cell.setCellValue(widgetVb.getReportTitle());
			cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
			intCol++;
			sheet.setColumnWidth(loopCount + 1, 6000);
			FileInputStream is1 = new FileInputStream(assetFolderUrl + "/Sunoida_Excel.PNG");
			byte[] bytes1 = IOUtils.toByteArray(is1);
			int pictureIdx1 = workBook.addPicture(bytes1, Workbook.PICTURE_TYPE_PNG);
			is1.close();
			Drawing drawing1 = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, loopCount + 2), (short) 4);
//			anchor1.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
			anchor1.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
			anchor1.setCol1(loopCount + 1);
			anchor1.setRow1(2);
			Picture pict1 = drawing1.createPicture(anchor1, pictureIdx1);
			pict1.resize(1);
			int cnt = 1;
			String prvCtrl = "";
			String lbl = "";
			lbl = StringUtils.trimTrailingWhitespace(lbl);
			cell = row.createCell(intCol);
			intCol++;
			cell.setCellValue(lbl);
			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));

			intRow = intRow + 2;
			row = sheet.createRow(intRow);
			cell = row.createCell(1);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm:ss a");
			String text = widgetVb.getMakerName() + "                " + dateFormat.format(new Date());
			cell.setCellValue(text);
			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));

			for (int i = 1; i <= intRow; i++) {
				sheet.addMergedRegion(new CellRangeAddress(i, i, 1, loopCount));
			}
//				intRow++;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return intRow;
	}

	public static int writeListDataModify(Sheet sheet, List<String> colTyps, Map<Integer, XSSFCellStyle> styls,
			String xmlData, int rowNum, Map<Integer, Integer> columnWidths, List<ColumnHeadersVb> columnHeaders) {
		Row row = null;
		Cell cell = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xmlData.getBytes(Charset.forName("UTF-8")));
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);
			Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("tableRow");
			if (nl != null && nl.getLength() > 0) {
				for (int loopCnt = 0; loopCnt < nl.getLength(); loopCnt++) {
					row = sheet.createRow(rowNum);
					// get the employee element
					Element el = (Element) nl.item(loopCnt);
					NodeList ncl = el.getChildNodes();
					NodeList formatTypeNodes = el.getElementsByTagName("formatType");
					NodeList headerTextNodes = el.getElementsByTagName("headerText");

					String formatType = "";
					String headerText = "";
					/*
					 * if(formatTypeNodes != null && formatTypeNodes.item(0) != null) formatType =
					 * formatTypeNodes.item(0).getTextContent(); if(headerTextNodes != null &&
					 * headerTextNodes.item(0) != null) headerText =
					 * headerTextNodes.item(0).getTextContent();
					 */
					if (headerTextNodes != null && headerTextNodes.item(0) != null)
						headerText = headerTextNodes.item(0) == null || headerTextNodes.item(0).getFirstChild() == null
								? ""
								: headerTextNodes.item(0).getFirstChild().getNodeValue();
					if (formatTypeNodes != null && formatTypeNodes.item(0) != null)
						formatType = formatTypeNodes.item(0) == null || formatTypeNodes.item(0).getFirstChild() == null
								? ""
								: formatTypeNodes.item(0).getFirstChild().getNodeValue();

					if ("HT".equalsIgnoreCase(formatType) || "FHT".equalsIgnoreCase(formatType)) {
						int colNum = 0;
						// Plain Data to be displayed.
						for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
							cell = row.createCell(colNum);
							String caption = "";
							if (colNum == 0) {
								caption = headerText == null ? " " : headerText;
							}
							cell.setCellValue(caption.replaceAll("_", " "));
							cell.setCellStyle(styls.get(CELL_STYLE_MID_HEADER_CAP_COL));
							columnWidths.put(colNum,
									(int) getColumnWidth(cell, (styls.get(CELL_STYLE_MID_HEADER_CAP_COL)).getFont(),
											true, columnWidths.get(colNum)));
							++colNum;
						}
						rowNum = rowNum + 1;
						row = sheet.createRow(rowNum);
					}

					if ("H".equalsIgnoreCase(formatType) || "HT".equalsIgnoreCase(formatType)
							|| "FHT".equalsIgnoreCase(formatType)) {
						writeHeadersForListData(sheet, columnHeaders, styls, rowNum, columnWidths);
						// rowNum = rowNum + 1;
						// row = sheet.createRow(rowNum);
					}

					for (int loopCount = 0; loopCount < ncl.getLength(); loopCount++) {
						cell = row.createCell(loopCount);
						Font font = null;
						Element el1 = (Element) ncl.item(loopCount);
						if (el1 != null && "formatType".equalsIgnoreCase(el1.getNodeName())) {
							continue;
						}
						if (el1 != null && "headerText".equalsIgnoreCase(el1.getNodeName())) {
							continue;
						}

						if ("N".equalsIgnoreCase(colTyps.get(loopCount))) {
							String cellValue = el1 != null && el1.getFirstChild() != null
									? ValidationUtil.replaceComma(el1.getFirstChild().getNodeValue().trim())
									: "";
							if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
									&& (loopCnt == 0 || loopCnt % 2 == 0)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
								font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT).getFont();
							} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
								font = styls.get(CELL_STYLE_SUMMERY_DATA_COL).getFont();
							} else if ("B".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
							} else if (loopCnt == 0 || loopCnt % 2 == 0) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT).getFont();
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
							}
							if (ValidationUtil.isNumericDecimal(cellValue)) {
//								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.valueOf(cellValue));
							} else {
								cell.setCellValue(cellValue);
							}
						} else if ("C".equalsIgnoreCase(colTyps.get(loopCount))) {
							if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
									&& (loopCnt == 0 || loopCnt % 2 == 0)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
								font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT).getFont();
							} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
								font = styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT).getFont();
							} else if ("B".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
							} else if (loopCnt == 0 || loopCnt % 2 == 0) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT).getFont();
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT).getFont();
							}
							String cellValue = el1 != null && el1.getFirstChild() != null
									? ValidationUtil.replaceComma(el1.getFirstChild().getNodeValue())
									: "";
							if (ValidationUtil.isValidId(cellValue)) {
//								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.valueOf(cellValue));
							} else {
								cell.setCellValue(cellValue);
							}
						} else {
							if (("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType))
									&& (loopCnt == 0 || loopCnt % 2 == 0)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
								font = styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT).getFont();
							} else if ("S".equalsIgnoreCase(formatType) || "G".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
								font = styls.get(CELL_STYLE_SUMMERY_CAP_COL).getFont();
							} else if ("B".equalsIgnoreCase(formatType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
								font = styls.get(CELL_STYLE_DETAILS_DATA_COL).getFont();
							} else if (loopCnt == 0 || loopCnt % 2 == 0) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT));
								font = styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT).getFont();
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
								font = styls.get(CELL_STYLE_DETAILS_CAP_COL).getFont();
							}
							String cellValue = el1 == null || el1.getFirstChild() == null ? ""
									: el1.getFirstChild().getNodeValue();
							cell.setCellValue(cellValue);
						}
						columnWidths.put(loopCount,
								(int) getColumnWidth(cell, font, true, columnWidths.get(loopCount)));
					}
					rowNum++;
				}
			}

		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return rowNum;
	}

	public static int writeHeadersOld(ReportsWriterVb reportWriterVb, List<ColumnHeadersVb> columnHeaders, int rowNum,
			SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styls, List<String> colTypes,
			Map<Integer, Integer> columnWidths) {
		int colNum = 0;
		Row row = null;
		Cell cell = null;
		if (reportWriterVb.getLabelRowCount() >= 2) {
			// Where Grouped Columns to be displayed.
			Map<String, List<String>> groupCols = new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> groupColTypes = new LinkedHashMap<String, List<String>>();
			row = sheet.createRow(rowNum);
			rowNum++;
			Row row1 = sheet.createRow(rowNum);
			for (ColumnHeadersVb columnHeadersVb1 : columnHeaders) {
				if ((columnHeadersVb1.getLabelColNum() - reportWriterVb.getLabelColCount()) > 0
						&& columnHeadersVb1.getLabelRowNum() <= 1) {
					// These are dataColumns which needs to be grouped.
					groupCols.put(columnHeadersVb1.getCaption(), new ArrayList<String>(5));
					groupColTypes.put(columnHeadersVb1.getCaption(), new ArrayList<String>(5));
				} else if (columnHeadersVb1.getLabelRowNum() <= 1) {
					// These are captionColumns
					cell = row.createCell(colNum);
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
					cell = row1.createCell(colNum);
					cell.setCellValue(columnHeadersVb1.getCaption());
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL));
					colTypes.add(columnHeadersVb1.getColType());
					++colNum;
				} else {
					// Need to add these columns to the groups added above.
					for (String colGrp11 : groupCols.keySet()) {
						groupCols.get(colGrp11).add(columnHeadersVb1.getCaption());
						groupColTypes.get(colGrp11).add(columnHeadersVb1.getColType());
					}
				}
			}
			for (String colGrp11 : groupCols.keySet()) {
				cell = row.createCell(colNum);
				cell.setCellValue(colGrp11);
				cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
				CellRangeAddress cellrange = new CellRangeAddress(rowNum - 1, rowNum - 1, colNum,
						colNum + groupCols.get(colGrp11).size() - 1);
				sheet.addMergedRegion(cellrange);
				for (String colCap : groupCols.get(colGrp11)) {
					cell = row1.createCell(colNum);
					cell.setCellValue(colCap);
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_DATA_COL));
					++colNum;
				}
				for (String colType : groupColTypes.get(colGrp11)) {
					colTypes.add(colType);
				}
			}
		} else {
			// Plain Data to be displayed.
			row = sheet.createRow(rowNum);
			// row.setRowStyle(csHeader);
			// Add headers
			for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
				cell = row.createCell(colNum);
				cell.setCellValue(columnHeadersVb.getCaption());
				String type = (reportWriterVb.getLabelColCount() - (colNum + 1)) >= 0 ? CAP_COL : DATA_COL;
				if (CAP_COL.equalsIgnoreCase(type)) {
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL));
				} else {
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_DATA_COL));
				}
				columnWidths.put(colNum, (int) getColumnWidth(cell, true, columnWidths.get(colNum)));
				colTypes.add(columnHeadersVb.getColType());
				++colNum;
			}
		}
		return colNum;
	}

	public static int writeReportDataOld(List<ReportStgVb> reportsStgs, ReportsWriterVb reportWriterVb,
			SXSSFSheet sheet, int rowNum, int headerCnt, Map<Integer, XSSFCellStyle> styls, List<String> columnTypes,
			Map<Integer, Integer> columnWidths) {
		Row row = null;
		Cell cell = null;
		for (ReportStgVb reportStgVb : reportsStgs) {
			row = sheet.createRow(rowNum);
			for (int loopCount = 0; loopCount < headerCnt; loopCount++) {
				cell = row.createCell(loopCount);
				int index = 0;
				String type = (reportWriterVb.getLabelColCount() - (loopCount + 1)) >= 0 ? CAP_COL : DATA_COL;
				String colType = columnTypes.size() > loopCount && columnTypes.get(loopCount) != null
						? columnTypes.get(loopCount)
						: "T";
				if (CAP_COL.equalsIgnoreCase(type)) {
					index = (loopCount + 1);
					switch (getStyle(reportStgVb, rowNum, "T")) {
					case 1:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
						break;
					case 2:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT));
						break;
					case 3:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
						break;
					default:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
					}
					XSSFRichTextString string = new XSSFRichTextString(findValue(reportStgVb, type, index));
					cell.setCellValue(string);
					columnWidths.put(loopCount, (int) getColumnWidth(cell, null, true, columnWidths.get(loopCount)));
				} else {
					index = ((loopCount + 1) - reportWriterVb.getLabelColCount());
					switch (getStyle(reportStgVb, rowNum, colType)) {
					case 1:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
						break;
					case 2:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT));
						break;
					case 3:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
						break;

					case 5:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
						break;
					case 6:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT));
						break;
					case 7:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
						break;
					case 8:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
						break;

					default:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
					}
					String cellValue = ValidationUtil.replaceComma(findValue(reportStgVb, type, index));
					if ("C".equalsIgnoreCase(colType) && ValidationUtil.isValidId(cellValue)) {
//						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(Integer.parseInt(cellValue));
					} else if (ValidationUtil.isNumericDecimal(cellValue)) {
//						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(Double.valueOf(cellValue));
					} else {
						cell.setCellValue(cellValue);
					}
					if (reportWriterVb.getLabelRowCount() < 2)
						columnWidths.put(loopCount,
								(int) getColumnWidth(cell, null, true, columnWidths.get(loopCount)));
				}
			}
			rowNum++;
		}
		return rowNum;
	}

	public static int createTemplateDesignPrompts(TemplateNameVb templateNameVb, ReportsWriterVb reportWriterVb,
			List<PromptIdsVb> prompts, Sheet sheet, Workbook workBook, String assetFolderUrl,
			Map<Integer, XSSFCellStyle> styles, int headerCnt) {
		int intRow = 1;
		Row row = null;
		Cell cell = null;
		createStylesForPrompts(workBook, styles);
		ResourceBundle rsb = CommonUtils.getResourceManger();

		CellStyle styleFillColor = workBook.createCellStyle();
		styleFillColor.setFillForegroundColor(IndexedColors.WHITE.index);
//		styleFillColor.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styleFillColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

//		Font fontTemplateData1 = createFont(workBook, IndexedColors.DARK_BLUE.index, Font.BOLDWEIGHT_BOLD, "garamond",
//				48);
		Font fontTemplateData1 = createFont(workBook, IndexedColors.DARK_BLUE.index, true, "garamond", 48);
		CellStyle style2 = workBook.createCellStyle();
		style2.setWrapText(true);
		style2.setFont(fontTemplateData1);
//		style2.setAlignment(CellStyle.ALIGN_CENTER);
		 style2.setAlignment(HorizontalAlignment.CENTER);

		try {
			int intCol = 0;
			row = sheet.createRow(intRow);
			sheet.getRow(intRow).setRowStyle(styleFillColor);
			CellRangeAddress region = CellRangeAddress.valueOf("$A$" + (2) + ":$N$+" + (5));
			frame(region, sheet, workBook, "");

			intRow++;
			intCol++;
			int loopCount = 5;// getColumnWidth(sheet, headerCnt);
			// sheet.setColumnWidth(0, 6000);
			FileInputStream is = new FileInputStream(assetFolderUrl + "/Vision_Excel.PNG");
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			is.close();
			Drawing drawing = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, 1), (short) 4);
			anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			anchor.setCol1(0);
			anchor.setRow1(2);
			anchor.setCol2(2);
			anchor.setRow2(4);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
//				pict.resize(1);

			// sheet.setColumnWidth(loopCount+1, 6000);
			FileInputStream is1 = new FileInputStream(assetFolderUrl + "/Login_28_06.PNG");
			byte[] bytes1 = IOUtils.toByteArray(is1);
			int pictureIdx1 = workBook.addPicture(bytes1, Workbook.PICTURE_TYPE_PNG);
			is1.close();
			Drawing drawing1 = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, 11), (short) 4);
			anchor1.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			anchor1.setCol1(11);
			anchor1.setRow1(2);
			anchor1.setCol2(13);
			anchor1.setRow2(4);
			Picture pict1 = drawing1.createPicture(anchor1, pictureIdx1);
//				pict1.resize(1);

			// sheet.setColumnWidth(loopCount+2, 6000);
			FileInputStream is2 = new FileInputStream(assetFolderUrl + "/Vision_Excel.PNG");
			byte[] bytes2 = IOUtils.toByteArray(is2);
			int pictureIdx2 = workBook.addPicture(bytes2, Workbook.PICTURE_TYPE_PNG);
			is2.close();
			Drawing drawing2 = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor2 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, loopCount + 3), (short) 4);
			anchor2.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			anchor2.setCol1(5);
			anchor2.setRow1(10);
			anchor2.setCol2(8);
			anchor2.setRow2(14);
			Picture pict2 = drawing2.createPicture(anchor2, pictureIdx2);
//				pict2.resize(1.5);

			// sheet.setColumnWidth(loopCount+2, 6000);
			XSSFRow row3 = (XSSFRow) sheet.createRow(15);
			sheet.getRow(15).setRowStyle(styleFillColor);
			XSSFCell cell3 = row3.createCell((short) 0);
			cell3.setCellStyle(style2);
			sheet.addMergedRegion(new CellRangeAddress(15, 17, 0, 13));
			cell3.setCellValue(rsb.getString("alertAdfTemplates"));

			// sheet.setColumnWidth(loopCount+2, 6000);
			FileInputStream is3 = new FileInputStream(assetFolderUrl + "/Sunoida_Excel.PNG");
			byte[] bytes3 = IOUtils.toByteArray(is3);
			int pictureIdx3 = workBook.addPicture(bytes3, Workbook.PICTURE_TYPE_PNG);
			is3.close();
			Drawing drawing3 = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor3 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, loopCount + 3), (short) 4);
//			anchor3.setAnchorType(ClientAnchor.MOVE_AND_RESIZE);
			anchor3.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
			anchor3.setCol1(5);
			anchor3.setRow1(21);
			anchor3.setCol2(8);
			anchor3.setRow2(31);
			Picture pict3 = drawing3.createPicture(anchor3, pictureIdx3);
//				pict3.resize(1.8);
			intRow = 32;
			CellRangeAddress region1 = CellRangeAddress.valueOf("$F$" + (21) + ":$H$+" + (32));
			frame(region1, sheet, workBook, "logo");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return intRow;
	}

//	private static void frame(CellRangeAddress region, Sheet sheet, Workbook wb, String type) {
//		sheet.addMergedRegion(region);
//
//		short borderMediumDashed = CellStyle.BORDER_THICK;
//
//		if ("logo".equalsIgnoreCase(type)) {
//			borderMediumDashed = CellStyle.BORDER_THIN;
//		}
//
//		if ("text".equalsIgnoreCase(type)) {
//			borderMediumDashed = CellStyle.BORDER_THICK;
//			RegionUtil.setBorderBottom(borderMediumDashed, region, sheet, wb);
//			RegionUtil.setBottomBorderColor(IndexedColors.DARK_BLUE.index, region, sheet, wb);
//
//		} else {
//			RegionUtil.setBorderBottom(borderMediumDashed, region, sheet, wb);
//			RegionUtil.setBorderTop(borderMediumDashed, region, sheet, wb);
//			RegionUtil.setBorderLeft(borderMediumDashed, region, sheet, wb);
//			RegionUtil.setBorderRight(borderMediumDashed, region, sheet, wb);
//		}
//	}

	private static void frame(CellRangeAddress region,Sheet sheet, Workbook wb,String type){
	    sheet.addMergedRegion(region);

	    BorderStyle  borderMediumDashed = BorderStyle.THICK;

	    if("logo".equalsIgnoreCase(type)){
	    	borderMediumDashed = BorderStyle.THIN;
	    }
	    
	    if("text".equalsIgnoreCase(type)){
	    	borderMediumDashed = BorderStyle.THICK;
	   	    RegionUtil.setBorderBottom(borderMediumDashed, region, sheet);
	   	    RegionUtil.setBottomBorderColor(IndexedColors.DARK_BLUE.index,region, sheet);

	    }else{
	    	RegionUtil.setBorderBottom(borderMediumDashed, region, sheet);
	   	    RegionUtil.setBorderTop(borderMediumDashed, region, sheet);
	   	    RegionUtil.setBorderLeft(borderMediumDashed, region, sheet);
	   	    RegionUtil.setBorderRight(borderMediumDashed, region, sheet);
	    }
	}
	public static int writeBNRTemplateData(TemplateNameVb templateNameVb, ReportsWriterVb reportsWriterVb, Sheet sheet1,
			Map<Integer, XSSFCellStyle> styles, int rowNum, Workbook workBook) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		rowNum++;
		CellRangeAddress region = CellRangeAddress.valueOf("$A$" + (rowNum) + ":$N$+" + (rowNum));
		frame(region, sheet1, workBook, "text");

		XSSFColor whiteClr = new XSSFColor();
		whiteClr.setIndexed(IndexedColors.WHITE.index);

		CellStyle styleFillColor = workBook.createCellStyle();
		styleFillColor.setFillForegroundColor(IndexedColors.WHITE.index);
		   styleFillColor.setFillPattern(FillPatternType.SOLID_FOREGROUND); 

		    Font fontTemplateData = createFont(workBook, IndexedColors.BLACK.index, false, "garamond", 12);
		CellStyle style1 = workBook.createCellStyle();
		style1.setWrapText(true);
		style1.setFont(fontTemplateData);

		 Font fontTemplateData1 = createFont(workBook, IndexedColors.BLACK.index, true, "garamond", 12);
		CellStyle style2 = workBook.createCellStyle();
		style2.setWrapText(true);
		style2.setFont(fontTemplateData1);

		XSSFRow row4 = (XSSFRow) sheet1.createRow(rowNum);
		XSSFCell cell4 = row4.createCell((short) 0);
		cell4.setCellStyle(style2);
		cell4.setCellValue(rsb.getString("alertTrademarks"));
		sheet1.getRow(rowNum).setRowStyle(styleFillColor);
		sheet1.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 13));
		rowNum++;
		rowNum++;

		XSSFRow row3 = (XSSFRow) sheet1.createRow(rowNum);
		XSSFCell cell3 = row3.createCell((short) 0);
		cell3.setCellStyle(style1);
		cell3.setCellValue(rsb.getString("alertAllIdentitiesAreTheTrademarksOfTheRespectiveOwners"));
		sheet1.getRow(rowNum).setRowStyle(styleFillColor);
		sheet1.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 13));
		rowNum++;
		rowNum++;

		XSSFRow row2 = (XSSFRow) sheet1.createRow(rowNum);
		XSSFCell cell2 = row2.createCell((short) 0);
		cell2.setCellStyle(style2);
		cell2.setCellValue("Â© " + rsb.getString("alertCopyrightSunoidaSolutions"));
		sheet1.getRow(rowNum).setRowStyle(styleFillColor);
		sheet1.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 13));
		rowNum++;
		rowNum++;

		XSSFRow row = (XSSFRow) sheet1.createRow(rowNum);
		XSSFCell cell = row.createCell((short) 0);
		cell.setCellStyle(style1);
		cell.setCellValue(rsb.getString("alertCopyrightSunoidaSolutionsDesc1"));
		int rowNumInc = rowNum + 2;
		sheet1.getRow(rowNum).setRowStyle(styleFillColor);
		sheet1.getRow(rowNumInc).setRowStyle(styleFillColor);
		sheet1.addMergedRegion(new CellRangeAddress(rowNum, rowNumInc, 0, 13));
		rowNumInc++;
		rowNumInc++;

		XSSFRow row9 = (XSSFRow) sheet1.createRow(rowNumInc);
		XSSFCell cell9 = row9.createCell((short) 0);
		cell9.setCellStyle(style1);
		int rowNumIncD = rowNumInc + 1;
		sheet1.getRow(rowNumInc).setRowStyle(styleFillColor);
		sheet1.getRow(rowNumIncD).setRowStyle(styleFillColor);
		sheet1.addMergedRegion(new CellRangeAddress(rowNumInc, rowNumIncD, 0, 13));
		cell9.setCellValue(rsb.getString("alertCopyrightSunoidaSolutionsDesc2"));
		rowNumIncD++;
		rowNumIncD++;

		XSSFRow row5 = (XSSFRow) sheet1.createRow(rowNumIncD);
		XSSFCell cell5 = row5.createCell((short) 0);
		cell5.setCellStyle(style2);
		cell5.setCellValue(rsb.getString("alertRevisionHistory"));
		sheet1.getRow(rowNumIncD).setRowStyle(styleFillColor);
		sheet1.addMergedRegion(new CellRangeAddress(rowNumIncD, rowNumIncD, 0, 13));
		rowNumIncD++;
		rowNumIncD++;

		/* Table Structure */
		XSSFRow row6 = (XSSFRow) sheet1.createRow(49);
		row6.setHeightInPoints((2 * sheet1.getDefaultRowHeightInPoints()));

		sheet1.getRow(49).setRowStyle(styleFillColor);
		XSSFCell cell6 = row6.createCell((short) 1);
		cell6.setCellStyle(style2);
//			 style2.setAlignment(CellStyle.ALIGN_CENTER);
		sheet1.addMergedRegion(new CellRangeAddress(49, 49, 1, 3));
		cell6.setCellValue(rsb.getString("SlNo"));

		XSSFRow slNo = (XSSFRow) sheet1.createRow(50);
		sheet1.getRow(50).setRowStyle(styleFillColor);
		XSSFCell cellSlNo = slNo.createCell((short) 1);
		cellSlNo.setCellStyle(style1);
		sheet1.addMergedRegion(new CellRangeAddress(50, 50, 1, 3));
		cellSlNo.setCellValue("1");

		XSSFRow row7 = (XSSFRow) sheet1.getRow(49);
		sheet1.getRow(49).setRowStyle(styleFillColor);
		XSSFCell cell7 = row7.createCell((short) 4);
//			 style2.setAlignment(CellStyle.ALIGN_CENTER);
		cell7.setCellStyle(style2);
		sheet1.addMergedRegion(new CellRangeAddress(49, 49, 4, 6));
		cell7.setCellValue(rsb.getString("alertCreatedModifiedBy"));

		XSSFRow maker = (XSSFRow) sheet1.getRow(50);
		sheet1.getRow(50).setRowStyle(styleFillColor);
		XSSFCell cellMaker = maker.createCell((short) 4);
		cellMaker.setCellStyle(style1);
		sheet1.addMergedRegion(new CellRangeAddress(50, 50, 4, 6));
		cellMaker.setCellValue(templateNameVb.getMakerName());

		XSSFRow row8 = (XSSFRow) sheet1.getRow(49);
		sheet1.getRow(49).setRowStyle(styleFillColor);
		XSSFCell cell8 = row8.createCell((short) 7);
//			 style2.setAlignment(CellStyle.ALIGN_CENTER);
		cell8.setCellStyle(style2);
		sheet1.addMergedRegion(new CellRangeAddress(49, 49, 7, 9));
		cell8.setCellValue(rsb.getString("alertApprovedBy"));

		XSSFRow verifier = (XSSFRow) sheet1.getRow(50);
		sheet1.getRow(50).setRowStyle(styleFillColor);
		XSSFCell cellVerifier = verifier.createCell((short) 7);
		cellVerifier.setCellStyle(style1);
		sheet1.addMergedRegion(new CellRangeAddress(50, 50, 7, 9));
		cellVerifier.setCellValue(templateNameVb.getVerifierName());

		XSSFRow row12 = (XSSFRow) sheet1.getRow(49);
		sheet1.getRow(49).setRowStyle(styleFillColor);
		XSSFCell cell12 = row12.createCell((short) 10);
//			 style2.setAlignment(CellStyle.ALIGN_CENTER);
		cell12.setCellStyle(style2);
		sheet1.addMergedRegion(new CellRangeAddress(49, 49, 10, 11));
		cell12.setCellValue(rsb.getString("alertVersionNo"));

		XSSFRow verNo = (XSSFRow) sheet1.getRow(50);
		sheet1.getRow(50).setRowStyle(styleFillColor);
		XSSFCell cellVerNo = verNo.createCell((short) 10);
		cellVerNo.setCellStyle(style1);
		sheet1.addMergedRegion(new CellRangeAddress(50, 50, 10, 11));
		cellVerNo.setCellValue(templateNameVb.getCurrVersionNo());

		XSSFRow row13 = (XSSFRow) sheet1.getRow(49);
		sheet1.getRow(49).setRowStyle(styleFillColor);
		XSSFCell cell13 = row13.createCell((short) 12);
//			 style2.setAlignment(CellStyle.ALIGN_CENTER);
		cell13.setCellStyle(style2);
		sheet1.addMergedRegion(new CellRangeAddress(49, 49, 12, 13));
		cell13.setCellValue(rsb.getString("date"));

		XSSFRow date = (XSSFRow) sheet1.getRow(50);
		sheet1.getRow(50).setRowStyle(styleFillColor);
		XSSFCell cellDate = date.createCell((short) 12);
		cellDate.setCellStyle(style1);
		sheet1.addMergedRegion(new CellRangeAddress(50, 50, 12, 13));
		cellDate.setCellValue(templateNameVb.getDateLastModified());

		CellRangeAddress region1 = CellRangeAddress.valueOf("$B$" + (50) + ":$N$+" + (51));
		frame(region1, sheet1, workBook, "logo");

		return rowNumIncD;
	}

	public static int writeDataForVisionTables(Sheet sheet, List<ColumnHeadersVb> columnHeaders,
			Map<Integer, XSSFCellStyle> styles, int rowNum, Map<Integer, Integer> columnWidths) {
		int colNum = 0;
		Row row = null;
		Cell cell = null;
		// Plain Data to be displayed.
		row = sheet.createRow(rowNum);
		// row.setRowStyle(csHeader);
		// Add headers
		for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
			cell = row.createCell(colNum);
			String caption = columnHeadersVb.getDbColumnName() == null ? " " : columnHeadersVb.getDbColumnName();
			caption = caption.replaceAll("_", " ");
			cell.setCellValue(caption);
			// cell.setCellStyle(styles.get(CELL_STYLE_HEADER_CAP_COL));
			columnWidths.put(colNum, (int) getColumnWidth(cell, (styles.get(CELL_STYLE_HEADER_CAP_COL)).getFont(), true,
					columnWidths.get(colNum)));
			++colNum;
		}
		return colNum;
	}

	public static int writeHeadersDashBoard(ReportsWriterVb reportWriterVb, List<ColumnHeadersVb> columnHeaders,
			int rowNum, int columnNum, SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styls, List<String> colTypes) {
		int colNum = columnNum;
		Row row = null;
		Cell cell = null;
		int titleRowNum = rowNum - 1;
		int titleColumnNum = colNum;
		row = sheet.getRow(titleRowNum);
		cell = row.createCell(colNum);
		cell.setCellValue(reportWriterVb.getReportDescription());
		cell.setCellStyle(styls.get(CELL_STYLE_TITLES));

		if (reportWriterVb.getLabelRowCount() >= 2) {
			// Where Grouped Columns to be displayed.
			Map<String, List<String>> groupCols = new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> groupColTypes = new LinkedHashMap<String, List<String>>();
			row = sheet.getRow(rowNum);
			rowNum++;
			Row row1 = sheet.getRow(rowNum);
			cell = row.createCell(colNum);

			for (ColumnHeadersVb columnHeadersVb1 : columnHeaders) {
				if ((columnHeadersVb1.getLabelColNum() - reportWriterVb.getLabelColCount()) > 0
						&& columnHeadersVb1.getLabelRowNum() <= 1) {
					// These are dataColumns which needs to be grouped.
					groupCols.put(columnHeadersVb1.getCaption(), new ArrayList<String>(5));
					groupColTypes.put(columnHeadersVb1.getCaption(), new ArrayList<String>(5));
				} else if (columnHeadersVb1.getLabelRowNum() <= 1) {
					// These are captionColumns
					cell = row.createCell(colNum);
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
					cell = row.createCell(colNum);
					cell.setCellValue(columnHeadersVb1.getCaption());
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL));
					Row row2 = sheet.getRow(rowNum);
					Cell cell2 = null;
					cell2 = row2.createCell(colNum);
					cell2.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL));
					colTypes.add(columnHeadersVb1.getColType());
					++colNum;
				} else {
					// Need to add these columns to the groups added above.
					for (String colGrp11 : groupCols.keySet()) {
						groupCols.get(colGrp11).add(columnHeadersVb1.getCaption());
						groupColTypes.get(colGrp11).add(columnHeadersVb1.getColType());
					}
				}
			}
			for (String colGrp11 : groupCols.keySet()) {
				cell = row.createCell(colNum);
				cell.setCellValue(colGrp11);
				cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
				CellRangeAddress cellrange = new CellRangeAddress(rowNum - 1, rowNum - 1, colNum,
						colNum + groupCols.get(colGrp11).size() - 1);
				sheet.addMergedRegion(cellrange);
				for (String colCap : groupCols.get(colGrp11)) {
					cell = row1.createCell(colNum);
					cell.setCellValue(colCap);
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_DATA_COL));
					++colNum;
				}
				for (String colType : groupColTypes.get(colGrp11)) {
					colTypes.add(colType);
				}
			}
		} else {
			// Plain Data to be displayed.
			row = sheet.getRow(rowNum);
			// Add headers
			for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
				cell = row.createCell(colNum);
				cell.setCellValue(columnHeadersVb.getCaption());
				String type = (reportWriterVb.getLabelColCount() - (colNum + 1)) >= 0 ? CAP_COL : DATA_COL;
				if (CAP_COL.equalsIgnoreCase(type)) {
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
				} else {
					cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
				}
				colTypes.add(columnHeadersVb.getColType());
				++colNum;
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(titleRowNum, titleRowNum, titleColumnNum, colNum - 1));
		return colNum;
	}

	public static int writeHeadersForVisionTables(Sheet sheet, List<ColumnHeadersVb> columnHeaders,
			Map<Integer, XSSFCellStyle> styles, int rowNum, Map<Integer, Integer> columnWidths) {
		int colNum = 0;
		Row row = null;
		Cell cell = null;
		// Plain Data to be displayed.
		row = sheet.createRow(rowNum);
		// row.setRowStyle(csHeader);
		// Add headers
		for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
			cell = row.createCell(colNum);
			String caption = columnHeadersVb.getCaption() == null ? " " : columnHeadersVb.getCaption();
			if (!"INTERNAL_STATUS".equalsIgnoreCase(caption)) {
				if (caption.contains(",")) {
					String[] parts = caption.split(",");
					caption = parts[1];
				}
				caption = caption.replaceAll("_", " ");
				cell.setCellValue(caption);
				cell.setCellStyle(styles.get(CELL_STYLE_HEADER_CAP_COL));
				columnWidths.put(colNum, (int) getColumnWidth(cell, (styles.get(CELL_STYLE_HEADER_CAP_COL)).getFont(),
						true, columnWidths.get(colNum)));
				++colNum;
			}
		}
		return colNum;
	}

	public static int writeReportDataDashBoard(List<ReportStgVb> reportsStgs, ReportsWriterVb reportWriterVb,
			SXSSFSheet sheet, int rowNum, int columnNum, int headerCnt, Map<Integer, XSSFCellStyle> styls,
			List<String> columnTypes) {
		Row row = null;
		Cell cell = null;
		for (ReportStgVb reportStgVb : reportsStgs) {
			row = sheet.getRow(rowNum);
			for (int loopCount = 0; loopCount < headerCnt; loopCount++) {
				cell = row.createCell(loopCount + columnNum);
				int index = 0;
				String type = (reportWriterVb.getLabelColCount() - (loopCount + 1)) >= 0 ? CAP_COL : DATA_COL;
				String colType = columnTypes.size() > loopCount && columnTypes.get(loopCount) != null
						? columnTypes.get(loopCount)
						: "T";
				if (CAP_COL.equalsIgnoreCase(type)) {
					index = (loopCount + 1);
					switch (getStyle(reportStgVb, rowNum, "T")) {
					case 1:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
						break;
					case 2:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT));
						break;
					case 3:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
						break;
					default:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
					}
					XSSFRichTextString string = new XSSFRichTextString(findValue(reportStgVb, type, index));
					cell.setCellValue(string);
				} else {
					index = ((loopCount + 1) - reportWriterVb.getLabelColCount());
					switch (getStyle(reportStgVb, rowNum, colType)) {
					case 1:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
						break;
					case 2:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT));
						break;
					case 3:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
						break;

					case 5:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
						break;
					case 6:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT));
						break;
					case 7:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
						break;
					case 8:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
						break;

					default:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
					}
					String cellValue = ValidationUtil.replaceComma(findValue(reportStgVb, type, index));
					if ("C".equalsIgnoreCase(colType) && ValidationUtil.isValidId(cellValue)) {
//						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(Long.parseLong(cellValue));
					} else if (ValidationUtil.isNumericDecimal(cellValue)) {
//						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellType(CellType.NUMERIC);
//						cell.setCellValue(Double.valueOf(cellValue));
						cell.setCellValue(Integer.parseInt(cellValue));
					} else {
						cell.setCellValue(cellValue);
					}
				}
			}
			rowNum++;
		}
		return rowNum;
	}

	/* Grid Export */

	public static void createRowAndColumnsForGridReport(int rowNum, SXSSFSheet sheet, int rowEnd, int columnEnd) {
		Row r = null;
		Cell c = null;
		for (int rowIndex = rowNum; rowIndex <= rowEnd; rowIndex++) {
			r = sheet.getRow(rowIndex);
			if (r == null) {
				r = sheet.createRow(rowIndex);
			}
			for (int cellIndex = 0; cellIndex <= columnEnd; cellIndex++) {
				c = r.getCell(cellIndex);
				if (c == null) {
					c = r.createCell(cellIndex);
					c.setCellValue("");
				}
			}
		}
	}

	public static int writeDataHeadersForGridMutilpleReport(ReportsWriterVb reportWriterVb, int rowNum,
			SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styls, List<ColumnHeadersVb> columnHeadersList) {
		Row row = null;
		Cell cell = null;
		int firstHeaderRowno = rowNum;
		int maxRowSpan = 0;
		int dataColumnStart = 0;
		for (ColumnHeadersVb columnHeadersVb : columnHeadersList) {
			if (!"T".equalsIgnoreCase(columnHeadersVb.getColType())) {
				int rowStart = firstHeaderRowno + columnHeadersVb.getLabelRowNum();
				int rowEnd = rowStart
						+ (columnHeadersVb.getRowSpanNum() != 0 ? columnHeadersVb.getRowSpanNum() - 1 : 0);
				int columnStart = columnHeadersVb.getLabelColNum() - 1;

				int columnEnd = (columnHeadersVb.getColSpanNum() != 0 ? columnHeadersVb.getColSpanNum() - 1 : 0);
				int getRowNum = firstHeaderRowno + columnHeadersVb.getLabelRowNum();
				row = sheet.getRow(getRowNum);
				cell = row.createCell(columnStart);
				cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
				cell.setCellValue(columnHeadersVb.getCaption());
				if ((columnHeadersVb.getColSpanNum() > 1) || columnHeadersVb.getRowSpanNum() > 1) {
					int end = columnStart + columnEnd;
					sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, columnStart, end));
					if (maxRowSpan < columnHeadersVb.getRowSpanNum()) {
						maxRowSpan = columnHeadersVb.getRowSpanNum();
					}
				}
				if (dataColumnStart == 0) {
					dataColumnStart = columnHeadersVb.getLabelColNum();
				}
				if (dataColumnStart > columnHeadersVb.getLabelColNum()) {
					dataColumnStart = columnHeadersVb.getLabelColNum();
				}
			}
		}
		rowNum = firstHeaderRowno + maxRowSpan;
		reportWriterVb.setDbStatus(dataColumnStart);
		return rowNum + 1;
	}

	public static int writeXmlDataToExcelForGridExport(ExceptionCode gridDataExceptionCode,
			Map<Integer, XSSFCellStyle> styles, int rowNum, SXSSFSheet sheet) {
		try {
			ArrayList masterList = (ArrayList) gridDataExceptionCode.getResponse();
			boolean isMultiLevel = (masterList.size() == 1) ? false : true;
			rowNum = writeHeaderForGridExcelExport(rowNum, sheet, styles,
					((ArrayList<String>) masterList.get(0)).get(0), isMultiLevel);
			for (int masterListIndex = 0; masterListIndex < masterList.size(); masterListIndex++) {
				rowNum = writeDataForGridExcelExport(rowNum, sheet, styles,
						((ArrayList<String>) masterList.get(masterListIndex)).get(0),
						((ArrayList<String>) masterList.get(masterListIndex)).get(1), isMultiLevel);
			}
			return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERRONEOUS_OPERATION;
		}
	}

	public static int writeHeaderForGridExcelExport(int rowNum, SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styles,
			String headerXml, boolean isMultiLevel) {
		try {
			Workbook workbook = sheet.getWorkbook();
			XSSFCellStyle localHeaderStyle = styles.get(CELL_STYLE_HEADER_CAP_COL_TOP);
			localHeaderStyle.setBorderBottom(BorderStyle.THIN);
			localHeaderStyle.setBorderTop(BorderStyle.THIN);
			localHeaderStyle.setBorderRight(BorderStyle.THIN);
			localHeaderStyle.setBorderLeft(BorderStyle.THIN);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(headerXml.getBytes(Charset.forName("UTF-8")));
			Document doc = dBuilder.parse(new InputSource(is));
			doc.getDocumentElement().normalize();

			Element rootElement = doc.getDocumentElement();
			int maxColumnCount = 0;
			NodeList rowList = rootElement.getChildNodes();
			for (int rowIndex = 0; rowIndex < rowList.getLength(); rowIndex++) {
				Node rowNode = rowList.item(rowIndex);
				Element rowElement = (Element) rowNode;
				Row row = sheet.createRow((Integer.parseInt(rowElement.getAttribute("LABEL_ROW_NUM")) - 1) + rowNum);
				NodeList colList = rowElement.getChildNodes();
				for (int colIndex = 0; colIndex < colList.getLength(); colIndex++) {
					Node colNode = colList.item(colIndex);
					Element colElement = (Element) colNode;
					if (!isMultiLevel || !"T".equalsIgnoreCase(colElement.getAttribute("COL_TYPE"))) {
						int rowSpan = (ValidationUtil.isValid(colElement.getAttribute("ROW_SPAN")))
								? Integer.parseInt(colElement.getAttribute("ROW_SPAN"))
								: 1;
						int colSpan = (ValidationUtil.isValid(colElement.getAttribute("COL_SPAN")))
								? Integer.parseInt(colElement.getAttribute("COL_SPAN"))
								: 1;
						int colId = Integer.parseInt(colElement.getAttribute("LABEL_COL_NUM")) - 1;
						Cell cell = row.createCell(colId);
						cell.setCellValue(colElement.getTextContent());
						cell.setCellStyle(localHeaderStyle);
						CellRangeAddress cellRangeAddress = null;
						if (rowSpan > 1 && colSpan > 1)
							cellRangeAddress = new CellRangeAddress((rowIndex + rowNum),
									((rowIndex + rowNum) + (rowSpan - 1)), colId, (colId + (colSpan - 1)));
						else if (rowSpan > 1)
							cellRangeAddress = new CellRangeAddress((rowIndex + rowNum),
									((rowIndex + rowNum) + (rowSpan - 1)), colId, colId);
						else if (colSpan > 1)
							cellRangeAddress = new CellRangeAddress((rowIndex + rowNum), (rowIndex + rowNum), colId,
									(colId + (colSpan - 1)));
						if (cellRangeAddress != null) {
							sheet.addMergedRegion(cellRangeAddress);
							setBorderForCell(cell, cellRangeAddress, sheet, workbook);
						}
					}
				}
				maxColumnCount = maxColumnCount < colList.getLength() ? colList.getLength() : maxColumnCount;
			}
			rowNum = rowList.getLength() + rowNum;
			return rowNum;
		} catch (Exception e) {
			e.printStackTrace();
			return rowNum;
		}
	}

	public static int writeDataForGridExcelExport(int rowNum, SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styles,
			String headerXml, String dataXml, boolean isMultiLevel) {
		try {
			Workbook workbook = sheet.getWorkbook();
			XSSFCellStyle localHeaderStyle = styles.get(CELL_STYLE_HEADER_CAP_COL_TOP);
			localHeaderStyle.setBorderBottom(BorderStyle.THIN);
			localHeaderStyle.setBorderTop(BorderStyle.THIN);
			localHeaderStyle.setBorderRight(BorderStyle.THIN);
			localHeaderStyle.setBorderLeft(BorderStyle.THIN);

			CellStyle style = sheet.getWorkbook().createCellStyle();
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			if (isMultiLevel) {
				InputStream is = new ByteArrayInputStream(headerXml.getBytes(Charset.forName("UTF-8")));
				Document doc = dBuilder.parse(new InputSource(is));
				doc.getDocumentElement().normalize();

				Element rootElement = doc.getDocumentElement();
				int maxColumnCount = 0;
				NodeList rowList = rootElement.getChildNodes();
				for (int rowIndex = 0; rowIndex < rowList.getLength(); rowIndex++) {
					Node rowNode = rowList.item(rowIndex);
					Element rowElement = (Element) rowNode;
					Row row = sheet
							.createRow((Integer.parseInt(rowElement.getAttribute("LABEL_ROW_NUM")) - 1) + rowNum);
					NodeList colList = rowElement.getChildNodes();
					for (int colIndex = 0; colIndex < colList.getLength(); colIndex++) {
						Node colNode = colList.item(colIndex);
						Element colElement = (Element) colNode;
						if ("T".equalsIgnoreCase(colElement.getAttribute("COL_TYPE"))) {
							int rowSpan = (ValidationUtil.isValid(colElement.getAttribute("ROW_SPAN")))
									? Integer.parseInt(colElement.getAttribute("ROW_SPAN"))
									: 1;
							int colSpan = (ValidationUtil.isValid(colElement.getAttribute("COL_SPAN")))
									? Integer.parseInt(colElement.getAttribute("COL_SPAN"))
									: 1;
							int colId = Integer.parseInt(colElement.getAttribute("LABEL_COL_NUM")) - 1;
							Cell cell = row.createCell(colId);
							cell.setCellValue(colElement.getTextContent());
							cell.setCellStyle(localHeaderStyle);
							CellRangeAddress cellRangeAddress = null;
							if (rowSpan > 1 && colSpan > 1)
								cellRangeAddress = new CellRangeAddress((rowIndex + rowNum),
										((rowIndex + rowNum) + (rowSpan - 1)), colId, (colId + (colSpan - 1)));
							else if (rowSpan > 1)
								cellRangeAddress = new CellRangeAddress((rowIndex + rowNum),
										((rowIndex + rowNum) + (rowSpan - 1)), colId, colId);
							else if (colSpan > 1)
								cellRangeAddress = new CellRangeAddress((rowIndex + rowNum), (rowIndex + rowNum), colId,
										(colId + (colSpan - 1)));
							if (cellRangeAddress != null) {
								sheet.addMergedRegion(cellRangeAddress);
								setBorderForCell(cell, cellRangeAddress, sheet, workbook);
							}
						}
					}
					maxColumnCount = maxColumnCount < colList.getLength() ? colList.getLength() : maxColumnCount;
				}
				rowNum = rowList.getLength() + rowNum;
			}

			InputStream is = new ByteArrayInputStream(dataXml.getBytes(Charset.forName("UTF-8")));
			Document doc = dBuilder.parse(new InputSource(is));
			doc.getDocumentElement().normalize();

			Element rootElement = doc.getDocumentElement();
			int maxColumnCount = 0;
			NodeList rowList = rootElement.getChildNodes();
			for (int rowIndex = 0; rowIndex < rowList.getLength(); rowIndex++) {
				Node rowNode = rowList.item(rowIndex);
				Element rowElement = (Element) rowNode;
				Row row = sheet.createRow((Integer.parseInt(rowElement.getAttribute("ROW_ID")) - 1) + rowNum);
				NodeList colList = rowElement.getChildNodes();
				for (int colIndex = 0; colIndex < colList.getLength(); colIndex++) {
					Node colNode = colList.item(colIndex);
					Element colElement = (Element) colNode;
					int rowSpan = (ValidationUtil.isValid(colElement.getAttribute("ROW_SPAN")))
							? Integer.parseInt(colElement.getAttribute("ROW_SPAN"))
							: 1;
					int colSpan = (ValidationUtil.isValid(colElement.getAttribute("COL_SPAN")))
							? Integer.parseInt(colElement.getAttribute("COL_SPAN"))
							: 1;
					int colId = Integer.parseInt(colElement.getAttribute("COL_ID")) - 1;
					Cell cell = row.createCell(colId);
					cell.setCellValue(colElement.getTextContent());
					cell.setCellStyle(style);
					CellRangeAddress cellRangeAddress = null;
					if (rowSpan > 1 && colSpan > 1)
						cellRangeAddress = new CellRangeAddress((rowIndex + rowNum),
								((rowIndex + rowNum) + (rowSpan - 1)), colId, (colId + (colSpan - 1)));
					else if (rowSpan > 1)
						cellRangeAddress = new CellRangeAddress((rowIndex + rowNum),
								((rowIndex + rowNum) + (rowSpan - 1)), colId, colId);
					else if (colSpan > 1)
						cellRangeAddress = new CellRangeAddress((rowIndex + rowNum), (rowIndex + rowNum), colId,
								(colId + (colSpan - 1)));
					if (cellRangeAddress != null) {
						sheet.addMergedRegion(cellRangeAddress);
						setBorderForCell(cell, cellRangeAddress, sheet, workbook);
					}
				}
				maxColumnCount = maxColumnCount < colList.getLength() ? colList.getLength() : maxColumnCount;
			}
			rowNum = rowList.getLength() + rowNum;
			return rowNum;
		} catch (Exception e) {
			e.printStackTrace();
			return rowNum;
		}
	}

	public static ExceptionCode writeXmlDataToExcelForChartExport(ExceptionCode chartDataExceptionCode,
			Map<Integer, XSSFCellStyle> styles, int rowNum, SXSSFSheet sheet) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String chartXmlData = (String) chartDataExceptionCode.getResponse();
			int maxColCount = writeChartDataToExcel(sheet, styles, chartXmlData, rowNum);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setResponse(maxColCount);
			return exceptionCode;
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public static int writeChartDataToExcel(SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styles, String chartXmlData,
			int rowNum) throws Exception {
		XSSFCellStyle localHeaderStyle = styles.get(CELL_STYLE_HEADER_CAP_COL_TOP);
		localHeaderStyle.setBorderBottom(BorderStyle.THIN);
//		localHeaderStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//		localHeaderStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//		localHeaderStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		localHeaderStyle.setBorderBottom(BorderStyle.THIN);
		localHeaderStyle.setBorderTop(BorderStyle.THIN);
		localHeaderStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(chartXmlData.getBytes(Charset.forName("UTF-8")));
		Document doc = dBuilder.parse(new InputSource(is));
		doc.getDocumentElement().normalize();

		Element rootElement = doc.getDocumentElement();
		NodeList rowList = rootElement.getChildNodes();
		int maxColumnLength = 0;
		/* Write header with header style */
		for (int rowIndex = 0; rowIndex < 1; rowIndex++) {
			Node rowNode = rowList.item(rowIndex);
			Element rowElement = (Element) rowNode;
			Row row = sheet.createRow((Integer.parseInt(rowElement.getAttribute("ROW_ID")) - 1) + rowNum);
			NodeList colList = rowElement.getChildNodes();
			for (int colIndex = 0; colIndex < colList.getLength(); colIndex++) {
				Node colNode = colList.item(colIndex);
				Element colElement = (Element) colNode;
				int colId = Integer.parseInt(colElement.getAttribute("COL_ID")) - 1;
				Cell cell = row.createCell(colId);
				cell.setCellValue(colElement.getTextContent());
				cell.setCellStyle(localHeaderStyle);
			}
			maxColumnLength = (maxColumnLength < colList.getLength()) ? colList.getLength() : maxColumnLength;
		}
		/* Write data with header style */
		for (int rowIndex = 1; rowIndex < rowList.getLength(); rowIndex++) {
			Node rowNode = rowList.item(rowIndex);
			Element rowElement = (Element) rowNode;
			Row row = sheet.createRow((Integer.parseInt(rowElement.getAttribute("ROW_ID")) - 1) + rowNum);
			NodeList colList = rowElement.getChildNodes();
			for (int colIndex = 0; colIndex < colList.getLength(); colIndex++) {
				Node colNode = colList.item(colIndex);
				Element colElement = (Element) colNode;
				int colId = Integer.parseInt(colElement.getAttribute("COL_ID")) - 1;
				Cell cell = row.createCell(colId);
				cell.setCellValue(colElement.getTextContent());
				cell.setCellStyle(style);
			}
			maxColumnLength = (maxColumnLength < colList.getLength()) ? colList.getLength() : maxColumnLength;
		}
		return maxColumnLength;
	}

	public static void setBorderForCell(Cell cell, CellRangeAddress cellRangeAddress, Sheet sheet, Workbook workbook) {
		try {
			  RegionUtil.setBorderTop(cell.getCellStyle().getBorderTop(), cellRangeAddress, sheet);
		        RegionUtil.setBorderLeft(cell.getCellStyle().getBorderLeft(), cellRangeAddress, sheet);
		        RegionUtil.setBorderRight(cell.getCellStyle().getBorderRight(), cellRangeAddress, sheet);
		        RegionUtil.setBorderBottom(cell.getCellStyle().getBorderBottom(), cellRangeAddress, sheet);
//			RegionUtil.setBorderTop(cell.getCellStyle().getBorderTop(), cellRangeAddress, sheet, workbook);
//			RegionUtil.setBorderLeft(cell.getCellStyle().getBorderLeft(), cellRangeAddress, sheet, workbook);
//			RegionUtil.setBorderRight(cell.getCellStyle().getBorderRight(), cellRangeAddress, sheet, workbook);
//			RegionUtil.setBorderBottom(cell.getCellStyle().getBorderBottom(), cellRangeAddress, sheet, workbook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int writeReportDataMdm(List<ReportStgVb> reportsStgs, ReportsWriterVb reportWriterVb,
			SXSSFSheet sheet, int rowNum, int headerCnt, Map<Integer, XSSFCellStyle> styls, List<String> columnTypes,
			Map<Integer, Integer> columnWidths) {
		Row row = null;
		Cell cell = null;
		int rowSpanNumStart = 0;
		int rowSpanNumEnd = 0;
		String spanValue = "";
		for (ReportStgVb reportStgVb : reportsStgs) {
			row = sheet.createRow(rowNum);
			for (int loopCount = 0; loopCount < columnTypes.size(); loopCount++) {
				String cellValue = "";
				cell = row.createCell(loopCount);
				int index = 0;
//				String type = (reportWriterVb.getLabelColCount() - (loopCount+1)) >= 0 ? CAP_COL : DATA_COL;
				String colType = columnTypes.get(loopCount);
				String type = "";
				if ("T".equalsIgnoreCase(colType)) {
					type = CAP_COL;
				} else {
					type = DATA_COL;
				}
				if (CAP_COL.equalsIgnoreCase(type)) {
					index = (loopCount + 1);
					switch (getStyle(reportStgVb, rowNum, "T")) {
					case 1:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
						break;
					case 2:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL_ALT));
						break;
					case 3:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
						break;
					default:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
					}
					XSSFRichTextString string = new XSSFRichTextString(findValue(reportStgVb, DATA_COL, index));
					cellValue = string.toString();
					cell.setCellValue(string);
					columnWidths.put(loopCount, (int) getColumnWidth(cell, null, true, columnWidths.get(loopCount)));
				} else {
					index = ((loopCount + 1) - reportWriterVb.getCaptionLabelColCount());
					switch (getStyle(reportStgVb, rowNum, colType)) {
					case 1:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
						break;
					case 2:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_ALT));
						break;
					case 3:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
						break;

					case 5:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
						break;
					case 6:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT));
						break;
					case 7:
						cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
						break;
					case 8:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
						break;

					default:
						cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
					}
					cellValue = ValidationUtil.replaceComma(findValue(reportStgVb, DATA_COL, index));
					/*
					 * if(!ValidationUtil.isNumericDecimal(cellValue)) {
					 * cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
					 * cell.setCellValue(cellValue); }else
					 */if ("C".equalsIgnoreCase(colType) && ValidationUtil.isValidId(cellValue)) {
//						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellType(CellType.NUMERIC);
						// cell.setCellValue(Integer.parseInt(cellValue));
						cell.setCellValue(Long.parseLong(cellValue));
					} else if (ValidationUtil.isNumericDecimal(cellValue)) {
//						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(Double.valueOf(cellValue));
					} else {
						cell.setCellValue(cellValue);
					}
					/*
					 * if(reportWriterVb.getLabelRowCount() < 2)
					 * columnWidths.put(loopCount,(int)getColumnWidth(cell, null, true,
					 * columnWidths.get(loopCount)));
					 */
				}
				/*
				 * if(loopCount == 0) { if(!cellValue.equalsIgnoreCase(spanValue)) {
				 * sheet.addMergedRegion(new CellRangeAddress(rowSpanNumStart, rowSpanNumEnd, 0,
				 * 0)); spanValue = cellValue; rowSpanNumStart = rowNum; rowSpanNumEnd = rowNum
				 * - 1; }else { rowSpanNumEnd = rowNum; } }
				 */
			}
			rowNum++;
		}
		return rowNum;
	}

	public static int writeHeadersMdm(ReportsWriterVb reportWriterVb, List<ColumnHeadersVb> columnHeaders, int rowNum,
			SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styls, List<String> colTypes,
			Map<Integer, Integer> columnWidths, ReportStgVb reportStgVb) {
		Row row = null;
		Row row1 = null;
		Row row2 = null;
		Row row3 = null;
		Row row4 = null;
		Row row5 = null;
		Row row6 = null;
		Row row7 = null;
		Cell cell = null;
		Row rowH = sheet.createRow(rowNum);
		Cell cellH = rowH.createCell(0);
		Cell cellH1 = rowH.createCell(1);
		Cell cellH2 = rowH.createCell(2);
		cellH.setCellValue(reportStgVb.getDataColumn3() + " : " + reportStgVb.getDataColumn1() + " - "
				+ reportStgVb.getDataColumn2());
		cellH.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
		cellH1.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
		cellH2.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));
		rowNum = rowNum + 1;
		int firstHeaderRowno = rowNum;
		for (int rowIndex = 1; rowIndex <= reportWriterVb.getLabelRowCount(); rowIndex++) {
			if (rowIndex == 1) {
				row1 = sheet.createRow(rowNum);
			} else if (rowIndex == 2) {
				row2 = sheet.createRow(rowNum);
			} else if (rowIndex == 3) {
				row3 = sheet.createRow(rowNum);
			} else if (rowIndex == 4) {
				row4 = sheet.createRow(rowNum);
			} else if (rowIndex == 5) {
				row5 = sheet.createRow(rowNum);
			} else if (rowIndex == 6) {
				row6 = sheet.createRow(rowNum);
			} else if (rowIndex == 7) {
				row7 = sheet.createRow(rowNum);
			}
			for (int colIndex = 0; colIndex < reportWriterVb.getLabelColCount(); colIndex++) {
				if (rowIndex == 1) {
					cell = row1.createCell(colIndex);
				} else if (rowIndex == 2) {
					cell = row2.createCell(colIndex);
				} else if (rowIndex == 3) {
					cell = row3.createCell(colIndex);
				} else if (rowIndex == 4) {
					cell = row4.createCell(colIndex);
				} else if (rowIndex == 5) {
					cell = row5.createCell(colIndex);
				} else if (rowIndex == 6) {
					cell = row6.createCell(colIndex);
				} else if (rowIndex == 7) {
					cell = row7.createCell(colIndex);
				}
				cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
			}
			rowNum++;
		}
		for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
			int rowStart = firstHeaderRowno + columnHeadersVb.getLabelRowNum() - 1;
			int rowEnd = rowStart + (columnHeadersVb.getRowspan() != 0 ? columnHeadersVb.getRowspan() - 1 : 0);
			int columnStart = columnHeadersVb.getLabelColNum() - 1;

			int columnEnd = (columnHeadersVb.getColspan() != 0 ? columnHeadersVb.getColspan() - 1 : 0);
			// int columnEnd = (columnHeadersVb.getColSpanNum() == 0 ?
			// columnHeadersVb.getLabelColNum() : columnHeadersVb.getColSpanNum())-1;
			if (columnHeadersVb.getLabelRowNum() == 1) {
				row = row1;
			} else if (columnHeadersVb.getLabelRowNum() == 2) {
				row = row2;
			} else if (columnHeadersVb.getLabelRowNum() == 3) {
				row = row3;
			} else if (columnHeadersVb.getLabelRowNum() == 4) {
				row = row4;
			} else if (columnHeadersVb.getLabelRowNum() == 5) {
				row = row5;
			} else if (columnHeadersVb.getLabelRowNum() == 6) {
				row = row6;
			} else if (columnHeadersVb.getLabelRowNum() == 7) {
				row = row7;
			}
			cell = row.getCell(columnStart);
			if (columnHeadersVb.getColspan() != 0 || columnHeadersVb.getRowspan() != 0) {
				int end = columnStart + columnEnd;
				sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, columnStart, end));
			}
			cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
			cell.setCellValue(columnHeadersVb.getCaption());
			if (columnHeadersVb.getCaption().contains("<br/>")) {
				columnHeadersVb.setCaption(columnHeadersVb.getCaption().replaceAll("<br/>", "\n"));
				cell.setCellValue(columnHeadersVb.getCaption().replaceAll("_", " "));
				cell.getCellStyle().setWrapText(true);
			}
		}
		return rowNum;
	}

	public static int createPrompts(ReportsVb reportsVb, Sheet sheet,
			Workbook workBook, String assetFolderUrl, Map<Integer, XSSFCellStyle> styles, int headerCnt) {
		int intRow = 0;
		Row row = null;
		Cell cell = null;
		createStylesForPrompts(workBook, styles);
		try {
			int intCol = 0;
			row = sheet.createRow(intRow);
			// intRow++;
			intCol++;
			int loopCount = 5;// getColumnWidth(sheet, headerCnt);
			sheet.setColumnWidth(0, 6000);
			FileInputStream is = new FileInputStream(assetFolderUrl + "/Product_Logo.PNG");
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			is.close();
			Drawing drawing = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, 1), (short) 4);
			anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			anchor.setCol1(0);
			anchor.setRow1(0);
			/*
			 * anchor.setDx1(5000); anchor.setDx2(5000); anchor.setDy1(5000);
			 * anchor.setDy2(5000); System.out.println("Dx1 : "+anchor.getDx1());
			 * System.out.println("Dx2 : "+anchor.getDx2());
			 * System.out.println("Dy1 : "+anchor.getDy1());
			 * System.out.println("Dy2 : "+anchor.getDy2());
			 */
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			if (pict != null)
				pict.resize(1);

			cell = row.createCell(intCol);
			cell.setCellValue(reportsVb.getReportTitle());
			cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
			intCol++;
			sheet.setColumnWidth(loopCount + 1, 6000);
			FileInputStream is1 = new FileInputStream(assetFolderUrl + "/Bank_Logo.PNG");
			byte[] bytes1 = IOUtils.toByteArray(is1);
			int pictureIdx1 = workBook.addPicture(bytes1, Workbook.PICTURE_TYPE_PNG);
			is1.close();
			Drawing drawing1 = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
					(short) getColumnNoForWidth(sheet, loopCount + 2), (short) 4);
			anchor1.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
			anchor1.setCol1(loopCount + 1);
			anchor1.setRow1(0);
			Picture pict1 = drawing1.createPicture(anchor1, pictureIdx1);
			if (pict1 != null)
				pict1.resize(1);
			int cnt = 1;
			String prvCtrl = "";
			intRow++;
			row = sheet.createRow(intRow);
			cell = row.createCell(1);
			cell.setCellValue(reportsVb.getPromptLabel());
			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
			intRow++;
			row = sheet.createRow(intRow);
			cell = row.createCell(1);
			cell.setCellValue(reportsVb.getDrillDownLabel());
			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
			for (int i = 0; i <= intRow; i++) {
				sheet.addMergedRegion(new CellRangeAddress(i, i, 1, loopCount));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return intRow;
	}
//	public static int createPrompts(ReportsVb reportsVb, Sheet sheet, Workbook workBook, String assetFolderUrl,
//			Map<Integer, XSSFCellStyle> styles, int headerCnt) {
//		int intRow = 0;
//		Row row = null;
//		Cell cell = null;
//		createStylesForPrompts(workBook, styles);
//		try {
//			int intCol = 0;
//			row = sheet.createRow(intRow);
//			// intRow++;
//			intCol++;
//			int loopCount = 5;// getColumnWidth(sheet, headerCnt);
//			sheet.setColumnWidth(0, 6000);
//			FileInputStream is = new FileInputStream(assetFolderUrl + "/Product_Logo.PNG");
//			byte[] bytes = IOUtils.toByteArray(is);
//			int pictureIdx = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
//			is.close();
//			Drawing drawing = sheet.createDrawingPatriarch();
//			XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
//					(short) getColumnNoForWidth(sheet, 1), (short) 4);
//			anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
//			anchor.setCol1(0);
//			anchor.setRow1(0);
//			/*
//			 * anchor.setDx1(5000); anchor.setDx2(5000); anchor.setDy1(5000);
//			 * anchor.setDy2(5000); System.out.println("Dx1 : "+anchor.getDx1());
//			 * System.out.println("Dx2 : "+anchor.getDx2());
//			 * System.out.println("Dy1 : "+anchor.getDy1());
//			 * System.out.println("Dy2 : "+anchor.getDy2());
//			 */
//			Picture pict = drawing.createPicture(anchor, pictureIdx);
//			if (pict != null)
//				pict.resize(1);
//
//			cell = row.createCell(intCol);
//			cell.setCellValue(reportsVb.getReportTitle());
//			cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
//			intCol++;
//			sheet.setColumnWidth(loopCount + 1, 6000);
//			FileInputStream is1 = new FileInputStream(assetFolderUrl + "/Bank_Logo.PNG");
//			byte[] bytes1 = IOUtils.toByteArray(is1);
//			int pictureIdx1 = workBook.addPicture(bytes1, Workbook.PICTURE_TYPE_PNG);
//			is1.close();
//			Drawing drawing1 = sheet.createDrawingPatriarch();
//			XSSFClientAnchor anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0,
//					(short) getColumnNoForWidth(sheet, loopCount + 2), (short) 4);
//			anchor1.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
//			anchor1.setCol1(loopCount + 1);
//			anchor1.setRow1(0);
//			Picture pict1 = drawing1.createPicture(anchor1, pictureIdx1);
//			if (pict1 != null)
//				pict1.resize(1);
//			int cnt = 1;
//			String prvCtrl = "";
//			intRow++;
//			row = sheet.createRow(intRow);
//			cell = row.createCell(1);
//			cell.setCellValue(reportsVb.getPromptLabel());
//			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
//			intRow++;
//			row = sheet.createRow(intRow);
//			cell = row.createCell(1);
//			cell.setCellValue(reportsVb.getDrillDownLabel());
//			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
//			for (int i = 0; i <= intRow; i++) {
//				sheet.addMergedRegion(new CellRangeAddress(i, i, 1, loopCount));
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return intRow;
//	}

	public static int writeHeadersRA(ReportsVb reportsVb, List<ColumnHeadersVb> columnHeaders, int rowNum,
			SXSSFSheet sheet, Map<Integer, XSSFCellStyle> styls, List<String> colTypes,
			Map<Integer, Integer> columnWidths) {
		Row row = null;
		Row row1 = null;
		Row row2 = null;
		Row row3 = null;
		Row row4 = null;
		Row row5 = null;
		Row row6 = null;
		Row row7 = null;
		Cell cell = null;
		Row rowH = sheet.createRow(rowNum);
		Cell cellH = rowH.createCell(0);
		Cell cellH1 = rowH.createCell(1);
		Cell cellH2 = rowH.createCell(2);
		// cellH.setCellValue(reportStgVb.getDataColumn3()+" :
		// "+reportStgVb.getDataColumn1()+" - "+reportStgVb.getDataColumn2());
		cellH.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
		cellH1.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
		cellH2.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
		if (ValidationUtil.isValid(reportsVb.getScreenName())) {
			row = sheet.createRow(rowNum);
			cell = row.createCell(0);
			cell.setCellValue(reportsVb.getScreenName());
			cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
			ArrayList<ColumnHeadersVb> columnHeadersFinallst = new ArrayList<ColumnHeadersVb>();
			columnHeaders.forEach(colHeadersVb -> {
				if (colHeadersVb.getColspan() <= 1) {
					columnHeadersFinallst.add(colHeadersVb);
				}
			});
			// cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
			// sheet.setColumnWidth(5 + 1, 6000);
			int colSize = columnHeadersFinallst.size();
			if (columnHeadersFinallst.size() == 0)
				colSize = 6;
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, (colSize - 1)));
			rowNum++;
			reportsVb.setScreenName("");
		}
		int firstHeaderRowno = rowNum;
		int maxHeaderRow = columnHeaders.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
		int maxHeaderCol = columnHeaders.stream().mapToInt(ColumnHeadersVb::getLabelColNum).max().orElse(0);

		for (int rowIndex = 1; rowIndex <= maxHeaderRow; rowIndex++) {
			if (rowIndex == 1) {
				row1 = sheet.createRow(rowNum);
			} else if (rowIndex == 2) {
				row2 = sheet.createRow(rowNum);
			} else if (rowIndex == 3) {
				row3 = sheet.createRow(rowNum);
			} else if (rowIndex == 4) {
				row4 = sheet.createRow(rowNum);
			} else if (rowIndex == 5) {
				row5 = sheet.createRow(rowNum);
			} else if (rowIndex == 6) {
				row6 = sheet.createRow(rowNum);
			} else if (rowIndex == 7) {
				row7 = sheet.createRow(rowNum);
			}
			for (int colIndex = 0; colIndex < maxHeaderCol; colIndex++) {
				if (rowIndex == 1) {
					cell = row1.createCell(colIndex);
				} else if (rowIndex == 2) {
					cell = row2.createCell(colIndex);
				} else if (rowIndex == 3) {
					cell = row3.createCell(colIndex);
				} else if (rowIndex == 4) {
					cell = row4.createCell(colIndex);
				} else if (rowIndex == 5) {
					cell = row5.createCell(colIndex);
				} else if (rowIndex == 6) {
					cell = row6.createCell(colIndex);
				} else if (rowIndex == 7) {
					cell = row7.createCell(colIndex);
				}
				cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
			}
			rowNum++;
		}
		int colNum = 0;
		for (ColumnHeadersVb columnHeadersVb : columnHeaders) {
			int rowStart = firstHeaderRowno + columnHeadersVb.getLabelRowNum() - 1;
			int rowEnd = 0;
			if (columnHeadersVb.getRowspan() != 0 && columnHeadersVb.getRowspan() != 1) {
				rowEnd = rowStart + (columnHeadersVb.getRowspan() - 1);
			} else {
				rowEnd = rowStart + 0;
				/*
				 * if(columnHeadersVb.getRowspan() == 0) {
				 * 
				 * }else if(columnHeadersVb.getRowspan() == 1){ rowEnd = rowStart+1; }
				 */
			}
			// int rowEnd = rowStart+((columnHeadersVb.getRowspan() != 0 ||
			// columnHeadersVb.getRowspan() != 1) ?
			// columnHeadersVb.getRowspan()-1:if(columnHeadersVb.getRowspan() == 0)? ));
			int columnStart = columnHeadersVb.getLabelColNum() - 1;
			int columnEnd = 0;
			if (columnHeadersVb.getColspan() != 0 && columnHeadersVb.getColspan() != 1) {
				columnEnd = columnHeadersVb.getColspan() - 1;
			} else {
				columnEnd = 0;
				/*
				 * if(columnHeadersVb.getColspan() == 0) { columnEnd = 0; }else
				 * if(columnHeadersVb.getColspan() == 1){ columnEnd = 1; }
				 */
			}
			// int columnEnd = ((columnHeadersVb.getColspan() != 0 ||
			// columnHeadersVb.getColspan() != 1) ?columnHeadersVb.getColspan()-1:0);
			// int columnEnd = (columnHeadersVb.getColSpanNum() == 0 ?
			// columnHeadersVb.getLabelColNum() : columnHeadersVb.getColSpanNum())-1;
			if (columnHeadersVb.getLabelRowNum() == 1) {
				row = row1;
			} else if (columnHeadersVb.getLabelRowNum() == 2) {
				row = row2;
			} else if (columnHeadersVb.getLabelRowNum() == 3) {
				row = row3;
			} else if (columnHeadersVb.getLabelRowNum() == 4) {
				row = row4;
			} else if (columnHeadersVb.getLabelRowNum() == 5) {
				row = row5;
			} else if (columnHeadersVb.getLabelRowNum() == 6) {
				row = row6;
			} else if (columnHeadersVb.getLabelRowNum() == 7) {
				row = row7;
			}
			cell = row.getCell(columnStart);
			if ((columnHeadersVb.getColspan() != 0 && columnHeadersVb.getColspan() != 1)
					|| (columnHeadersVb.getRowspan() != 0 && columnHeadersVb.getRowspan() != 1)) {
				int end = columnStart + columnEnd;
				sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, columnStart, end));
			}
			cell.setCellStyle(styls.get(CELL_STYLE_HEADER_CAP_COL_TOP));
			cell.setCellValue(columnHeadersVb.getCaption());
			if (columnHeadersVb.getCaption().contains("<br/>")) {
				columnHeadersVb.setCaption(columnHeadersVb.getCaption().replaceAll("<br/>", "\n"));
				cell.setCellValue(columnHeadersVb.getCaption().replaceAll("_", " "));
				cell.getCellStyle().setWrapText(true);
			}
			columnWidths.put(colNum, (int) getColumnWidth(cell, (styls.get(CELL_STYLE_HEADER_CAP_COL)).getFont(), true,
					columnWidths.get(colNum)));
			colNum++;
		}
		return rowNum;
	}

	public static int writeReportDataRA(ReportsVb reportVb, List<ColumnHeadersVb> colHeaderslst,
			List<HashMap<String, String>> dataLst, SXSSFSheet sheet, int rowNum, Map<Integer, XSSFCellStyle> styls,
			List<String> columnTypes, Map<Integer, Integer> columnWidths, Boolean totalRow) {
		Row row = null;
		Cell cell = null;
		String[] capGrpCols = null;
		ArrayList<String> groupingCols = new ArrayList<String>();
		String screenSortColumn = reportVb.getScreenSortColumn();
		ArrayList<ColumnHeadersVb> columnHeadersFinallst = new ArrayList<ColumnHeadersVb>();
		colHeaderslst.forEach(colHeadersVb -> {
			if (colHeadersVb.getColspan() <= 1 && colHeadersVb.getNumericColumnNo() != 99) {
				columnHeadersFinallst.add(colHeadersVb);
			}
		});
		if (ValidationUtil.isValid(reportVb.getScreenGroupColumn())) {
			reportVb.setPdfGroupColumn(reportVb.getScreenGroupColumn());
		}
		if (ValidationUtil.isValid(reportVb.getPdfGroupColumn()))
			capGrpCols = reportVb.getPdfGroupColumn().split("!@#");

		if (reportVb.getTotalRows() <= reportVb.getMaxRecords() && !totalRow && capGrpCols != null
				&& capGrpCols.length > 0) {
			for (String grpStr : capGrpCols) {
				for (ColumnHeadersVb colHeader : columnHeadersFinallst) {
					if (grpStr.equalsIgnoreCase(colHeader.getCaption().toUpperCase())) {
						groupingCols.add(colHeader.getDbColumnName());
						break;
					}
				}
			}
		}
		final String[] grpColNames = capGrpCols;
		Map<String, List<HashMap<String, String>>> groupingMap = new HashMap<String, List<HashMap<String, String>>>();
		/*
		 * //To write the individual report Caption , The report caption stored in
		 * screenName object in wb if(ValidationUtil.isValid(reportVb.getScreenName()))
		 * { row = sheet.createRow(rowNum); cell = row.createCell(0);
		 * cell.setCellValue(reportVb.getScreenName());
		 * cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
		 * sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6)); rowNum++;
		 * reportVb.setScreenName(""); }
		 */

		if (reportVb.getTotalRows() <= reportVb.getMaxRecords() && !totalRow
				&& (groupingCols != null && groupingCols.size() > 0)) {
			switch (groupingCols.size()) {
			case 1:
				groupingMap = dataLst.stream()
						.collect(Collectors.groupingBy(m -> (m.get(groupingCols.get(0))) == null ? ""
								: grpColNames[0] + ": " + m.get(groupingCols.get(0))));
				break;
			case 2:
				groupingMap = dataLst.stream()
						.collect(Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0)) + " >> " + m.get(groupingCols.get(1))) == null ? ""
										: grpColNames[0] + ": " + m.get(groupingCols.get(0)) + " >> " + grpColNames[1]
												+ ": " + m.get(groupingCols.get(1))));
				break;
			case 3:
				groupingMap = dataLst.stream()
						.collect(Collectors.groupingBy(m -> (m.get(groupingCols.get(0)) + " >> "
								+ m.get(groupingCols.get(1)) + " >> " + m.get(groupingCols.get(2))) == null
										? ""
										: grpColNames[0] + ": " + m.get(groupingCols.get(0)) + " >> " + grpColNames[1]
												+ ": " + m.get(groupingCols.get(1)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(2))));
				break;
			case 4:
				groupingMap = dataLst.stream().collect(
						Collectors.groupingBy(m -> (m.get(groupingCols.get(0)) + " >> " + m.get(groupingCols.get(1))
								+ " >> " + m.get(groupingCols.get(2)) + " >> " + m.get(groupingCols.get(3))) == null
										? ""
										: grpColNames[0] + ": " + m.get(groupingCols.get(0)) + " >> " + grpColNames[1]
												+ ": " + m.get(groupingCols.get(1)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(2)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(3))));
				break;
			case 5:
				groupingMap = dataLst.stream()
						.collect(Collectors.groupingBy(m -> (m.get(groupingCols.get(0)) + " >> "
								+ m.get(groupingCols.get(1)) + " >> " + m.get(groupingCols.get(2)) + " >> "
								+ m.get(groupingCols.get(3)) + " >> " + m.get(groupingCols.get(4))) == null
										? ""
										: grpColNames[0] + ": " + m.get(groupingCols.get(0)) + " >> " + grpColNames[1]
												+ ": " + m.get(groupingCols.get(1)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(2)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(3)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(4))));
				break;
			}
			Map<String, List<HashMap<String, String>>> sortedMap = new TreeMap<String, List<HashMap<String, String>>>();
			if (ValidationUtil.isValid(screenSortColumn)) {
				if (screenSortColumn.contains(groupingCols.get(0))) {
					String value = screenSortColumn.substring(9, screenSortColumn.length()).toUpperCase();
					String[] col = value.split(",");
					for (int i = 0; i < col.length; i++) {
						if (col[i].contains(groupingCols.get(0))) {
							String val = col[i];
							if (val.contains("DESC")) {
								sortedMap = new TreeMap<String, List<HashMap<String, String>>>(
										Collections.reverseOrder());
								sortedMap.putAll(groupingMap);
							} else {
								sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
							}
						}
					}
				} else {
					sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
				}
			} else {
				sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
			}
			// Map<String, List < HashMap<String, String> >> sortedMap = new TreeMap<String,
			// List < HashMap<String, String> >>(groupingMap);
			Set entrySet = sortedMap.entrySet();
			Iterator it = entrySet.iterator();
			while (it.hasNext()) {
				Map.Entry dataVal = (Map.Entry) it.next();
				List<HashMap<String, String>> grpDataLst = (List) dataVal.getValue();
				String key = (String) dataVal.getKey();
				if (key.isEmpty())
					continue;
				row = sheet.createRow(rowNum);
				cell = row.createCell(0);
				cell.setCellValue(dataVal.getKey().toString());
				cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
				sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, columnHeadersFinallst.size() - 1));
				rowNum++;
				rowNum = writeDatatoExcel(reportVb, colHeaderslst, grpDataLst, sheet, rowNum, styls, columnTypes,
						columnWidths, columnHeadersFinallst, row, cell, true);
			}
		} else {
			rowNum = writeDatatoExcel(reportVb, colHeaderslst, dataLst, sheet, rowNum, styls, columnTypes, columnWidths,
					columnHeadersFinallst, row, cell, false);
		}
		return rowNum;
	}

	private static int writeDatatoExcel(ReportsVb reportVb, List<ColumnHeadersVb> colHeaderslst,
			List<HashMap<String, String>> dataLst, SXSSFSheet sheet, int rowNum, Map<Integer, XSSFCellStyle> styls,
			List<String> columnTypes, Map<Integer, Integer> columnWidths,
			ArrayList<ColumnHeadersVb> columnHeadersFinallst, Row row, Cell cell, Boolean calculateTotal) {
		int rowSpanNumStart = 0;
		int rowSpanNumEnd = 0;
		String spanValue = "";
		String formatType = "D";
		HashMap<String, String> sumMap = new HashMap<String, String>();
		List<HashMap<String, String>> summationLst = new ArrayList();
		Double sumVal = 0.0;
		String firstColHeaderName = columnHeadersFinallst.get(0).getDbColumnName();
		if (dataLst != null) {
			for (HashMap dataMap : dataLst) {
				if (dataMap.containsKey("FORMAT_TYPE"))
					formatType = dataMap.get("FORMAT_TYPE").toString();
				row = sheet.createRow(rowNum);
				for (int loopCount = 0; loopCount < columnHeadersFinallst.size(); loopCount++) {

					ColumnHeadersVb colHeadersVb = columnHeadersFinallst.get(loopCount);
					if (colHeadersVb.getColspan() > 1 || colHeadersVb.getNumericColumnNo() == 99)
						continue;
					String cellValue = "";
					cell = row.createCell(loopCount);
					String colType = colHeadersVb.getColType();
					String type = "";
					if ("T".equalsIgnoreCase(colHeadersVb.getColType())) {
						type = CAP_COL;
					} else {
						type = DATA_COL;
					}
					if (CAP_COL.equalsIgnoreCase(type)) {
						String orgValue = "";
						if (dataMap.containsKey(colHeadersVb.getDbColumnName())) {
							orgValue = ((dataMap.get(colHeadersVb.getDbColumnName())) != null
									|| dataMap.get(colHeadersVb.getDbColumnName()) == "")
											? dataMap.get(colHeadersVb.getDbColumnName()).toString()
											: "";
						}
						if ("S".equalsIgnoreCase(formatType)) {
							cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
							/*
							 * if(loopCount == 0 && !ValidationUtil.isValid(orgValue)) orgValue = "Total :";
							 */
						} else if ("ST".equalsIgnoreCase(formatType) || "FT".equalsIgnoreCase(formatType)) {
							cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
							/*
							 * if(loopCount == 0 && !ValidationUtil.isValid(orgValue)) orgValue =
							 * "Sub Total :";
							 */
						} else {
							cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
						}
						XSSFRichTextString string = new XSSFRichTextString(orgValue);
						cellValue = string.toString();
						cell.setCellValue(string);
						columnWidths.put(loopCount,
								(int) getColumnWidth(cell, null, true, columnWidths.get(loopCount)));
					} else {
						if ("S".equalsIgnoreCase(formatType)) {
							if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
							}
						} else if ("ST".equalsIgnoreCase(formatType) || "FT".equalsIgnoreCase(formatType)) {
							if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
							}
						} else {
							if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)
									|| "NR".equalsIgnoreCase(colType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
							}
						}
						String orgValue = "";
						if (dataMap.containsKey(colHeadersVb.getDbColumnName()))
							orgValue = ((dataMap.get(colHeadersVb.getDbColumnName())) != null
									|| dataMap.get(colHeadersVb.getDbColumnName()) == "")
											? dataMap.get(colHeadersVb.getDbColumnName()).toString()
											: "";

						// String orgValue =
						// dataMap.containsKey(colHeadersVb.getDbColumnName())?dataMap.get(colHeadersVb.getDbColumnName()).toString():"0";
						if (ValidationUtil.isValid(orgValue)) {
							if ("NR".equalsIgnoreCase(colType) || "TR".equalsIgnoreCase(colType)) {
								cellValue = orgValue;
							} else {
								cellValue = ValidationUtil.replaceComma(orgValue);
							}
						}
						if (ValidationUtil.isNumericDecimal(cellValue)) {
//							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(Double.valueOf(cellValue));
						} else {
							cell.setCellValue(cellValue);
						}
						columnWidths.put(loopCount,
								(int) getColumnWidth(cell, null, true, columnWidths.get(loopCount)));
						if ("NR".equalsIgnoreCase(colType) || "TR".equalsIgnoreCase(colType))
							continue;
						if (calculateTotal) {
							String prevValstr = sumMap.containsKey(colHeadersVb.getDbColumnName())
									? sumMap.get(colHeadersVb.getDbColumnName())
									: "0";
							if (!ValidationUtil.isValid(prevValstr))
								prevValstr = "0";
							if (!ValidationUtil.isValid(cellValue))
								cellValue = "0";
							Double strVal = Double.parseDouble(prevValstr);
							sumVal = strVal + Double.parseDouble(cellValue);
							DecimalFormat formatter = new DecimalFormat("####.00");
							String cellText = formatter.format(sumVal);
							sumMap.put(colHeadersVb.getDbColumnName(), cellText);
						}
					}
					/*
					 * if(loopCount == 0) { if(cellValue.equalsIgnoreCase(spanValue)) {
					 * sheet.addMergedRegion(new CellRangeAddress(rowSpanNumStart, rowSpanNumEnd, 0,
					 * 0)); spanValue = cellValue; rowSpanNumStart = rowNum; rowSpanNumEnd = rowNum
					 * - 1; }else { rowSpanNumEnd = rowNum; } }
					 */
				}
				rowNum++;
			}
		} else {
			row = sheet.createRow(rowNum);
			cell = row.createCell(0);
			cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
			cell.setCellValue("No Data Found");
			rowNum++;
		}
		// This is calculate the Groupwise total only when total row < max record
		if (calculateTotal) {
			if (!sumMap.isEmpty()) {
				sumMap.put("FORMAT_TYPE", "ST");
				sumMap.put(firstColHeaderName, "Sub Total");
				summationLst.add(sumMap);
				rowNum = writeDatatoExcel(reportVb, colHeaderslst, summationLst, sheet, rowNum, styls, columnTypes,
						columnWidths, columnHeadersFinallst, row, cell, false);
			}
		}
		return rowNum;
	}

	protected static int getStyle(ReportsVb reportStgVb, int rowNum, String colType) {
		if ((rowNum % 2) == 0 && ("S".equalsIgnoreCase(reportStgVb.getFormatType())
				|| "G".equalsIgnoreCase(reportStgVb.getFormatType()))) {
			if ("C".equalsIgnoreCase(colType))
				return 5;
			return 1; // Summary with Background
		} else if ((rowNum % 2) == 0) {
			if ("C".equalsIgnoreCase(colType))
				return 6;
			return 2; // Non Summary with background.
		} else if ("S".equalsIgnoreCase(reportStgVb.getFormatType())
				|| "G".equalsIgnoreCase(reportStgVb.getFormatType())) {
			if ("C".equalsIgnoreCase(colType))
				return 7;
			return 3;// Summary with out Background
		} else {
			if ("C".equalsIgnoreCase(colType))
				return 8;
			return 4;// Non Summary With out background
		}
	}

	// added by Prakashika on 03-nov-2022 for ADF schedules report download
	private static void drawImageToSheet(Workbook workbook, Sheet sheet, String imageName, int startCol, int endCol,
			int startRow, int endRow, boolean applicationImage, String assetFolderUrl) throws IOException {
		InputStream imageInputStream = null;
//		applicationImage =false;

		imageInputStream = new FileInputStream(assetFolderUrl + File.separator + imageName);

		byte[] logoBytes = IOUtils.toByteArray(imageInputStream);
		int logoPictureIdx = workbook.addPicture(logoBytes, Workbook.PICTURE_TYPE_PNG);
		imageInputStream.close();
		CreationHelper logoHelper = workbook.getCreationHelper();
		Drawing logoDrawing = sheet.createDrawingPatriarch();
		ClientAnchor logoAnchor = logoHelper.createClientAnchor();
		logoAnchor.setCol1(startCol);
		logoAnchor.setCol2(endCol);
		logoAnchor.setRow1(startRow);
		logoAnchor.setRow2(endRow);
		Picture logoPict = logoDrawing.createPicture(logoAnchor, logoPictureIdx);
	}

	public static int createPromptsUploadExlce(UploadFilingVb reportWriterVb, List<PromptIdsVb> prompts, Sheet sheet,
			Workbook workBook, String assetFolderUrl, Map<Integer, XSSFCellStyle> styles, int headerCnt) {
		int intRow = 1;
		Row row = null;
		Cell cell = null;
		createStylesForPrompts(workBook, styles);
		try {
			System.out.println("createPromptsUploadExlce : " + prompts.size());
			int intCol = 0;
			row = sheet.createRow(intRow);
			intRow++;
//				intCol++;
			int loopCount = 4;// getColumnWidth(sheet, headerCnt);
			cell = row.createCell(intCol);
			sheet.setColumnWidth(0, 6000);
			drawImageToSheet(workBook, sheet, "logo.png", 0, 1, 1, 4, true, assetFolderUrl);
			sheet.setColumnWidth(loopCount + 1, 6000);
			drawImageToSheet(workBook, sheet, "Sunoida_Excel.PNG", 5, 6, 1, 4, true, assetFolderUrl);
			intCol++;
			cell = row.createCell(intCol);
			cell.setCellValue("Upload Filing Report");
			cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
			intCol++;

			int cnt = 1;
			String prvCtrl = "";
			String lbl = "";
			if (prompts != null && prompts.size() > 0) {
				for (PromptIdsVb promptIdsVbTemp12 : prompts) {
					row = sheet.createRow(intRow);
					intCol = 1;
					if (cnt == 3) {
						cnt = 1;
						intCol = 1;
						lbl = StringUtils.trimTrailingWhitespace(lbl);
						cell = row.createCell(intCol);
						cell.setCellValue(lbl);
						cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
						// intCol++;
						intRow++;
						lbl = "";
						row = sheet.createRow(intRow);
					}
					if ("COMBO".equalsIgnoreCase(promptIdsVbTemp12.getPromptType())
							|| "TREE".equalsIgnoreCase(promptIdsVbTemp12.getPromptType())
							|| promptIdsVbTemp12.getPromptType().contains("TEXT")) {
						if ((lbl + promptIdsVbTemp12.getPromptString() + " : " + promptIdsVbTemp12.getPromptDesc())
								.length() < 50)
							lbl = lbl + "                ";
						if ((lbl + promptIdsVbTemp12.getPromptString() + " : " + promptIdsVbTemp12.getPromptDesc())
								.length() < 100)
							lbl = lbl + "        ";
						String promptStrData = "";
						if (promptIdsVbTemp12.getPromptType().contains("TEXT")) {
							promptStrData = promptIdsVbTemp12.getPromptDesc();
						} else {
							promptStrData = promptIdsVbTemp12.getPromptDesc();
						}
						lbl = StringUtils.trimLeadingWhitespace(lbl) + promptIdsVbTemp12.getPromptString() + " : "
								+ promptStrData + "	";
						prvCtrl = promptIdsVbTemp12.getPromptType();
					} else if ("RANGE".equalsIgnoreCase(promptIdsVbTemp12.getPromptType())) {
						if (ValidationUtil.isValid(prvCtrl)) {
//								System.out.println("lbl : "+lbl);
							lbl = StringUtils.trimWhitespace(lbl);
							cell = row.createCell(intCol);
							cell.setCellValue(lbl);
							cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));
							intRow++;
							lbl = "";
							row = sheet.createRow(intRow);
						}
						String tabs = "                ";
						if ((promptIdsVbTemp12.getPromptString() + "     From : " + promptIdsVbTemp12.getPromptDesc()
								+ "," + " To : " + promptIdsVbTemp12.getPromptDesc()).length() < 100) {
							tabs = "                ";
						}
						lbl = promptIdsVbTemp12.getPromptString() + "     From : " + promptIdsVbTemp12.getPromptDesc()
								+ "," + tabs + " To : " + promptIdsVbTemp12.getSelectedValue2().getField1();
						lbl = StringUtils.trimTrailingWhitespace(lbl);
						cell = row.createCell(intCol);
					} else {
						if (promptIdsVbTemp12 != null) {
							if ((lbl + promptIdsVbTemp12.getPromptString() + " : " + promptIdsVbTemp12.getPromptDesc())
									.length() < 50)
								lbl = lbl + "                ";
							if ((lbl + promptIdsVbTemp12.getPromptString() + " : " + promptIdsVbTemp12.getPromptDesc())
									.length() < 100)
								lbl = lbl + "        ";
							String promptStrData = "";
							promptStrData = promptIdsVbTemp12.getPromptDesc();
							lbl = StringUtils.trimLeadingWhitespace(lbl) + promptIdsVbTemp12.getPromptString() + " : "
									+ promptStrData + "    ";
							prvCtrl = promptIdsVbTemp12.getPromptType();
						}
					}
					cnt++;
				}
			}
			lbl = StringUtils.trimTrailingWhitespace(lbl);
			cell = row.createCell(intCol);
			intCol++;
			cell.setCellValue(lbl);
			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));

			intRow = intRow + 2;
			row = sheet.createRow(intRow);
			cell = row.createCell(1);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm:ss a");
			String text = reportWriterVb.getMakerName() + "                " + dateFormat.format(new Date());
			cell.setCellValue(text);
			cell.setCellStyle(styles.get(CELL_STYLE_PROMPTS));

			for (int i = 1; i <= intRow; i++) {
				sheet.addMergedRegion(new CellRangeAddress(i, i, 1, loopCount));
			}
//				intRow++;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return intRow;
	}
	
	public static int createPromptsPage(ReportsVb reportsVb, Sheet sheet,
			Workbook workBook, String assetFolderUrl, Map<Integer, XSSFCellStyle> styles, int headerCnt) {
		int intRow = 0;
		Row row = null;
		Cell cell = null;
		createStylesForPrompts(workBook, styles);
		byte[] tClr = getRGB(reportsVb.getApplicationTheme());
		try {
			XSSFColor sPinkclr = new XSSFColor();
			// byte[] tClr = {(byte) 177, (byte) 24, (byte) 124};
			sPinkclr = new XSSFColor(tClr);

			XSSFColor greyClr = new XSSFColor();
			byte[] greyXclr = {(byte) 242, (byte) 244, (byte) 242};
			greyClr = new XSSFColor(greyXclr);

			String promptLabel[] = null;
			if(ValidationUtil.isValid(reportsVb.getPromptLabel())) {
				promptLabel = reportsVb.getPromptLabel().split("!@#");
			}
			int intCol = 0;
			float rowheight = 19.5f;
			int loopCount = 2;
			row = sheet.createRow(intRow);
			for(int i=0; i<=loopCount; i++) {
				cell = row.createCell(i);
				cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
			}
			
			intCol++;

			sheet.setDisplayGridlines(false);
			Row row1 = sheet.createRow(0);
			drawImageToSheet(workBook, sheet, "Product_Logo.png", 0, 1, 0, 2, true, assetFolderUrl);
			drawImageToSheet(workBook, sheet, "Bank_Logo.png", 2, 3, 0, 2, true, assetFolderUrl);

			int cnt = 1;
			intRow = 4;
			row = sheet.createRow(intRow);
			row.setHeightInPoints(rowheight);
			cell = row.createCell(0);
			cell.setCellValue("Report Title");
			styles.get(CELL_STYLE_REPORT_PROMPTS_CAP).setVerticalAlignment(VerticalAlignment.CENTER);
			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
			cell = row.createCell(1);
			cell.setCellValue(reportsVb.getReportTitle());
			styles.get(CELL_STYLE_REPORT_PROMPTS_CAP).setVerticalAlignment(VerticalAlignment.CENTER);
			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
			intRow++;
			if(promptLabel != null && promptLabel.length>0) {
				for(int i=0; i<promptLabel.length; i++) {
					String[] promptArr = null;
					String[] val = null;
					promptArr = promptLabel[i].split(":");
					val = promptArr[1].split(",");
					row = sheet.createRow(intRow);
					row.setHeightInPoints(rowheight);
					cell = row.createCell(0);
					if(val.length>2) {
						promptArr[1] = val[0]+","+val[1]+" (+) "+(val.length-2);
					}else if(val.length == 2){
						promptArr[1] = val[0]+","+val[1];
					}
					cell.setCellValue(promptArr[0]);
					cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
					cell = row.createCell(1);
					cell.setCellValue(promptArr[1]);
					styles.get(CELL_STYLE_REPORT_PROMPTS).getFont().setColor(sPinkclr);
					styles.get(CELL_STYLE_REPORT_PROMPTS).setVerticalAlignment(VerticalAlignment.CENTER);
					cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS));
					intRow++;
				}
			}
			row = sheet.createRow(intRow);
			row.setHeightInPoints(rowheight);
			cell = row.createCell(0);
			cell.setCellValue("Generated on");
			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
			cell = row.createCell(1);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm:ss a");
			cell.setCellValue(dateFormat.format(new Date()));
			styles.get(CELL_STYLE_REPORT_PROMPTS).getFont().setColor(sPinkclr);
			styles.get(CELL_STYLE_REPORT_PROMPTS).setVerticalAlignment(VerticalAlignment.CENTER);
			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS));
			intRow++;
			row = sheet.createRow(intRow);
			row.setHeightInPoints(rowheight);
			cell = row.createCell(0);
			cell.setCellValue("Generated By");
			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
			cell = row.createCell(1);
			cell.setCellValue(reportsVb.getMakerName());
			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS));
			intRow = intRow+2;
			row = sheet.createRow(intRow);
			row.setHeightInPoints(rowheight);
			for(int i=0; i<=loopCount; i++) {
				cell = row.createCell(i);
				if(i==1) {
					cell.setCellValue("Powered by Sunoida");
				}
				styles.get(CELL_STYLE_TITLE_CAP).getFont().setColor(sPinkclr);
				styles.get(CELL_STYLE_TITLE_CAP).setVerticalAlignment(VerticalAlignment.CENTER);
			    cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
			    styles.get(CELL_STYLE_TITLE_CAP).setBorderBottom(BorderStyle.THIN);
			    styles.get(CELL_STYLE_TITLE_CAP).setBorderRight(BorderStyle.NONE);
			    if(i==loopCount) {
			   	//styles.get(CELL_STYLE_TITLE_CAP).setBorderRight(HSSFBorderStyle.THIN);
					cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
			    	XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
					style.setBorderRight(BorderStyle.THIN);
					style.setBorderBottom(BorderStyle.THIN);
					style.setFillForegroundColor(greyClr);
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell.setCellStyle(style);
				}
			}
			CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 2, 0, loopCount);
			sheet.addMergedRegion(cellRangeAddress1);
			RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress1, sheet);
			
			CellRangeAddress cellRangeAddress2 = new CellRangeAddress(3, intRow-1, loopCount, loopCount);
			sheet.addMergedRegion(cellRangeAddress2);
			RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress2, sheet);
			
			
			CellRangeAddress cellRangeAddress3 = new CellRangeAddress(3, 3, 0, loopCount-1);
			sheet.addMergedRegion(cellRangeAddress3);
			RegionUtil.setBorderBottom(BorderStyle.NONE, cellRangeAddress3, sheet);

			CellRangeAddress cellRangeAddress9 = new CellRangeAddress(intRow-1, intRow-1, 0, 1);
			sheet.addMergedRegion(cellRangeAddress9);
			RegionUtil.setBorderBottom(BorderStyle.NONE, cellRangeAddress9, sheet);

			sheet.setColumnWidth(0, 20 * 256);
			sheet.setColumnWidth(1, 56 * 256);
			sheet.setColumnWidth(2, 20 * 256);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return intRow;
	}

//	public static int createPromptsPage(ReportsVb reportsVb, Sheet sheet, Workbook workBook, String assetFolderUrl,
//			Map<Integer, XSSFCellStyle> styles, int headerCnt) {
//		int intRow = 0;
//		Row row = null;
//		Cell cell = null;
//		createStylesForPrompts(workBook, styles);
//		byte[] tClr = getRGB(reportsVb.getApplicationTheme());
//		try {
//			XSSFColor sPinkclr = new XSSFColor();
//			// byte[] tClr = {(byte) 177, (byte) 24, (byte) 124};
//			sPinkclr = new XSSFColor(tClr);
//
//			XSSFColor greyClr = new XSSFColor();
//			byte[] greyXclr = { (byte) 242, (byte) 244, (byte) 242 };
//			greyClr = new XSSFColor(greyXclr);
//
//			String promptLabel[] = null;
//			if (ValidationUtil.isValid(reportsVb.getPromptLabel())) {
//				promptLabel = reportsVb.getPromptLabel().split("!@#");
//			}
//			int intCol = 0;
//			float rowheight = 19.5f;
//			int loopCount = 2;
//			row = sheet.createRow(intRow);
//			for (int i = 0; i <= loopCount; i++) {
//				cell = row.createCell(i);
//				cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
//			}
//
//			intCol++;
//
//			sheet.setDisplayGridlines(false);
//			Row row1 = sheet.createRow(0);
//			drawImageToSheet(workBook, sheet, "Product_Logo.png", 0, 1, 0, 2, true, assetFolderUrl);
//			drawImageToSheet(workBook, sheet, "Bank_Logo.png", 2, 3, 0, 2, true, assetFolderUrl);
//
//			int cnt = 1;
//			intRow = 4;
//			row = sheet.createRow(intRow);
//			row.setHeightInPoints(rowheight);
//			cell = row.createCell(0);
//			cell.setCellValue("Report Title");
//			styles.get(CELL_STYLE_REPORT_PROMPTS_CAP).setVerticalAlignment(VerticalAlignment.CENTER);
//			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
//			cell = row.createCell(1);
//			cell.setCellValue(reportsVb.getReportTitle());
//			styles.get(CELL_STYLE_REPORT_PROMPTS_CAP).setVerticalAlignment(VerticalAlignment.CENTER);
//			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
//			intRow++;
//			if (promptLabel != null && promptLabel.length > 0) {
//				for (int i = 0; i < promptLabel.length; i++) {
//					String[] promptArr = null;
//					String[] val = null;
//					promptArr = promptLabel[i].split(":");
//					val = promptArr[1].split(",");
//					row = sheet.createRow(intRow);
//					row.setHeightInPoints(rowheight);
//					cell = row.createCell(0);
//					if (val.length > 2) {
//						promptArr[1] = val[0] + "," + val[1] + " (+) " + (val.length - 2);
//					} else if (val.length == 2) {
//						promptArr[1] = val[0] + "," + val[1];
//					}
//					cell.setCellValue(promptArr[0]);
//					cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
//					cell = row.createCell(1);
//					cell.setCellValue(promptArr[1]);
//					styles.get(CELL_STYLE_REPORT_PROMPTS).getFont().setColor(sPinkclr);
//					styles.get(CELL_STYLE_REPORT_PROMPTS).setVerticalAlignment(VerticalAlignment.CENTER);
//					cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS));
//					intRow++;
//				}
//			}
//			row = sheet.createRow(intRow);
//			row.setHeightInPoints(rowheight);
//			cell = row.createCell(0);
//			cell.setCellValue("Generated on");
//			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
//			cell = row.createCell(1);
//			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm:ss a");
//			cell.setCellValue(dateFormat.format(new Date()));
//			styles.get(CELL_STYLE_REPORT_PROMPTS).getFont().setColor(sPinkclr);
////			styles.get(CELL_STYLE_REPORT_PROMPTS).setAlignment(CellStyle.VERTICAL_CENTER);
//			styles.get(CELL_STYLE_REPORT_PROMPTS).setVerticalAlignment(VerticalAlignment.CENTER);
//			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS));
//			intRow++;
//			row = sheet.createRow(intRow);
//			row.setHeightInPoints(rowheight);
//			cell = row.createCell(0);
//			cell.setCellValue("Generated By");
//			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS_CAP));
//			cell = row.createCell(1);
//			cell.setCellValue(reportsVb.getMakerName());
//			cell.setCellStyle(styles.get(CELL_STYLE_REPORT_PROMPTS));
//			intRow = intRow + 2;
//			row = sheet.createRow(intRow);
//			row.setHeightInPoints(rowheight);
//			for (int i = 0; i <= loopCount; i++) {
//				cell = row.createCell(i);
//				if (i == 1) {
//					cell.setCellValue("Powered by Sunoida");
//				}
//				styles.get(CELL_STYLE_TITLE_CAP).getFont().setColor(sPinkclr);
//				styles.get(CELL_STYLE_TITLE_CAP).setVerticalAlignment(VerticalAlignment.CENTER);
//				cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
////				styles.get(CELL_STYLE_TITLE_CAP).setBorderBottom(HSSFCellStyle.BORDER_THIN);
//				styles.get(CELL_STYLE_TITLE_CAP).setBorderBottom(BorderStyle.THIN);
////				styles.get(CELL_STYLE_TITLE_CAP).setBorderRight(HSSFCellStyle.BORDER_NONE);
//				styles.get(CELL_STYLE_TITLE_CAP).setBorderRight(BorderStyle.NONE);
//				if (i == loopCount) {
//					// styles.get(CELL_STYLE_TITLE_CAP).setBorderRight(HSSFCellStyle.BORDER_THIN);
//					cell.setCellStyle(styles.get(CELL_STYLE_TITLE_CAP));
//					XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
//					style.setBorderRight(BorderStyle.THIN);
//					style.setBorderBottom(BorderStyle.THIN);
//					style.setFillForegroundColor(greyClr);
////					style.setFillPattern(CellStyle.SOLID_FOREGROUND);
//					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//					cell.setCellStyle(style);
//				}
//			}
//			CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 2, 0, loopCount);
//			sheet.addMergedRegion(cellRangeAddress1);
////			RegionUtil.setBorderRight(CellStyle.BORDER_THIN, cellRangeAddress1, sheet, workBook);
//			RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress1, sheet, workBook);
//
//			CellRangeAddress cellRangeAddress2 = new CellRangeAddress(3, intRow - 1, loopCount, loopCount);
//			sheet.addMergedRegion(cellRangeAddress2);
//			RegionUtil.setBorderRight(CellStyle.BORDER_THIN, cellRangeAddress2, sheet, workBook);
//
//			CellRangeAddress cellRangeAddress3 = new CellRangeAddress(3, 3, 0, loopCount - 1);
//			sheet.addMergedRegion(cellRangeAddress3);
//			RegionUtil.setBorderBottom(CellStyle.BORDER_NONE, cellRangeAddress3, sheet, workBook);
//
//			CellRangeAddress cellRangeAddress9 = new CellRangeAddress(intRow - 1, intRow - 1, 0, 1);
//			sheet.addMergedRegion(cellRangeAddress9);
//			RegionUtil.setBorderBottom(CellStyle.BORDER_NONE, cellRangeAddress9, sheet, workBook);
//
//			sheet.setColumnWidth(0, 20 * 256);
//			sheet.setColumnWidth(1, 56 * 256);
//			sheet.setColumnWidth(2, 20 * 256);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return intRow;
//	}

	public static byte[] getRGB(final String rgb) {
		byte[] sunoidaClr = null;
		if (!ValidationUtil.isValid(rgb)) {
			sunoidaClr = new byte[] { (byte) 177, (byte) 24, (byte) 124 };
		} else {
			final int[] ret = new int[3];
			for (int i = 0; i < 3; i++) {
				ret[i] = Integer.parseInt(rgb.substring(i * 2, i * 2 + 2), 16);
			}
			sunoidaClr = new byte[] { (byte) ret[0], (byte) ret[1], (byte) ret[2] };
		}
		return sunoidaClr;
	}

	public static Map<Integer, XSSFCellStyle> createStyles1(Workbook workBook, String applicationTheme) {
		Map<Integer, XSSFCellStyle> styles = new HashMap<Integer, XSSFCellStyle>();
		Font fontHeader = createFont(workBook, IndexedColors.WHITE.index, true, "Calibri", 10);
		Font fontData  = createFont(workBook, IndexedColors.BLACK.index, false, "Calibri", 10);
		Font fontSummary = createFont(workBook, IndexedColors.BLACK.index, true, "Calibri", 10);
		Font fontHeaderTitle = createFont(workBook, IndexedColors.GREEN.index, true, "Calibri", 10);
		Font fontDetails  = createFont(workBook, IndexedColors.WHITE.index, true, "Calibri", 10);
		Font fontPrompts  = createNewFont(workBook, true, "Calibri", 10);

		/* byte[] greenClr = {(byte) 79, (byte) 98, (byte) 40}; */
		// byte[] greenClr = {(byte) 3, (byte) 80, (byte) 122};
		byte[] greenClr = { (byte) 0, (byte) 92, (byte) 140 };
		XSSFColor greenXClor = new XSSFColor(greenClr);
		byte[] pinkClr = { (byte) 230, (byte) 184, (byte) 183 };
		XSSFColor pinkXClor = new XSSFColor(pinkClr);
		XSSFColor whiteClr = new XSSFColor();
		whiteClr.setIndexed(IndexedColors.WHITE.index);
		byte[] creemClr = { (byte) 205, (byte) 226, (byte) 236 };
		XSSFColor creemXClor = new XSSFColor(creemClr);
		// Dark pink for Summary
		byte[] darkPinkClr = { (byte) 177, (byte) 19, (byte) 27 };
		XSSFColor blackClr = new XSSFColor(darkPinkClr);
		byte[] DgreenClr = { (byte) 54, (byte) 67, (byte) 27 };
		XSSFColor DgreenXClor = new XSSFColor(DgreenClr);

		byte[] sunoidaPinkClr = getRGB(applicationTheme); // {(byte) 177, (byte) 24, (byte) 124};
		// byte[] sunoidaPinkClr = {(byte) 177, (byte) 24, (byte) 124};
		XSSFColor sunoidaPinkXClr = new XSSFColor(sunoidaPinkClr);
		// grey color
		byte[] lightGrey = { (byte) 242, (byte) 244, (byte) 242 };
		XSSFColor lightGreyXColor = new XSSFColor(lightGrey);

		XSSFColor blackcolor = new XSSFColor();
		blackcolor.setIndexed(IndexedColors.BLACK.index);

		XSSFCellStyle csHeaderCaptionColTop = createStyle(workBook, sunoidaPinkXClr, HorizontalAlignment.CENTER_SELECTION, fontHeader, null, BorderStyle.THIN , whiteClr); //For Multi headers reports
		XSSFCellStyle csHeaderCaptionCol =  createStyle(workBook, greenXClor,HorizontalAlignment.LEFT, fontHeader, null, BorderStyle.NONE , null);// For header
		XSSFCellStyle csMidHeaderCaptionCol =  createStyle(workBook, DgreenXClor, HorizontalAlignment.LEFT, fontHeader, null, BorderStyle.NONE , null);// For Mid header
	    XSSFCellStyle csDataAlt1 = createStyle(workBook, whiteClr, HorizontalAlignment.GENERAL,fontData, BuiltinFormats.getBuiltinFormat(0), BorderStyle.THIN, pinkXClor); //For Caption With background
	    XSSFCellStyle csDataAlt1Data =  createStyle(workBook, whiteClr, HorizontalAlignment.RIGHT, fontData, BuiltinFormats.getBuiltinFormat(4), BorderStyle.THIN, pinkXClor); //For Data With background
	    XSSFCellStyle csDataAlt2 =  createStyle(workBook, creemXClor, HorizontalAlignment.GENERAL, fontData, BuiltinFormats.getBuiltinFormat(0), BorderStyle.THIN, pinkXClor);//For Caption With out background
	    XSSFCellStyle csDataAlt2Data =  createStyle(workBook, creemXClor, HorizontalAlignment.RIGHT, fontData, BuiltinFormats.getBuiltinFormat(4), BorderStyle.THIN, pinkXClor);//For Data With out background
		XSSFCellStyle csHeaderDataCol = createStyle(workBook, greenXClor, HorizontalAlignment.RIGHT, fontHeader, null, BorderStyle.NONE , null);// For header Right Align
		XSSFCellStyle csReportTitle =  createStyle(workBook, whiteClr, HorizontalAlignment.CENTER_SELECTION, fontHeaderTitle, null, BorderStyle.NONE , greenXClor); //For Headings
		
		XSSFCellStyle csSumary = createStyle(workBook, whiteClr, HorizontalAlignment.GENERAL, fontSummary, null, BorderStyle.THIN, pinkXClor);
		csSumary.setBorderTop(BorderStyle.THIN);
		csSumary.setTopBorderColor(pinkXClor);
		
		XSSFCellStyle csSumaryData = createStyle(workBook, whiteClr, HorizontalAlignment.RIGHT, fontSummary, BuiltinFormats.getBuiltinFormat(4), BorderStyle.THIN, pinkXClor); //For Data Summary
		csSumaryData.setBorderTop(BorderStyle.THIN);
		/*
		 * XSSFCellStyle csSumaryAlt2 = createStyle(workBook, creemXClor,
		 * CellStyle.ALIGN_GENERAL, fontSummary, null,CellStyle.BORDER_THIN, pinkXClor);
		 * csSumaryAlt2.setBorderTop(CellStyle.BORDER_THIN);
		 * csSumaryAlt2.setTopBorderColor(pinkXClor);
		 */

		XSSFCellStyle csSumaryAlt2 = createStyle(workBook, lightGreyXColor, HorizontalAlignment.GENERAL, fontPrompts, null,BorderStyle.THIN, pinkXClor);
		csSumaryAlt2.setBorderTop(BorderStyle.THIN);
		csSumaryAlt2.setTopBorderColor(pinkXClor);

		XSSFCellStyle csSumaryDataAlt2 = createStyle(workBook, lightGreyXColor, HorizontalAlignment.RIGHT, fontPrompts, BuiltinFormats.getBuiltinFormat(4), BorderStyle.THIN, pinkXClor); //For Data Summary with Alt clr
		csSumaryDataAlt2.setBorderTop(BorderStyle.THIN);
		csSumaryDataAlt2.setTopBorderColor(pinkXClor);
		
		XSSFCellStyle csPromptsCaption = createStyle(workBook, sunoidaPinkXClr, HorizontalAlignment.CENTER, fontDetails, null, BorderStyle.THIN, whiteClr);
		csPromptsCaption.setBorderBottom(BorderStyle.THIN);
		// csSumary.setBorderRight(CellStyle.BORDER_THICK);

		XSSFCellStyle csReportPrompt = createStyle(workBook, lightGreyXColor, HorizontalAlignment.CENTER, fontPrompts, null, BorderStyle.THIN, blackcolor);
		csReportPrompt.setBorderBottom(BorderStyle.THIN);
		XSSFCellStyle csDataAlt1DataForCount =  createStyle(workBook, whiteClr, HorizontalAlignment.RIGHT, fontData, BuiltinFormats.getBuiltinFormat(3), BorderStyle.THIN, pinkXClor); //For Data With background
		XSSFCellStyle csDataAlt2DataForCount =  createStyle(workBook, lightGreyXColor, HorizontalAlignment.RIGHT, fontPrompts, BuiltinFormats.getBuiltinFormat(3), BorderStyle.MEDIUM, pinkXClor);//For Data With out background
		XSSFCellStyle csSumaryDataForCount = createStyle(workBook, whiteClr, HorizontalAlignment.RIGHT, fontSummary, BuiltinFormats.getBuiltinFormat(3), BorderStyle.THIN, pinkXClor); //For Data Summary
		csSumaryDataForCount.setBorderTop(BorderStyle.THIN);
		csSumaryDataForCount.setTopBorderColor(pinkXClor);
		XSSFCellStyle csSumaryDataAlt2ForCount = createStyle(workBook, lightGreyXColor, HorizontalAlignment.RIGHT, fontPrompts, BuiltinFormats.getBuiltinFormat(3), BorderStyle.THIN, pinkXClor); //For Data Summary with Alt clr
		
		csSumaryDataAlt2ForCount.setBorderTop(BorderStyle.THIN);
		csSumaryDataAlt2ForCount.setTopBorderColor(pinkXClor);

		styles.put(CELL_STYLE_HEADER_CAP_COL_TOP, csHeaderCaptionColTop);
		styles.put(CELL_STYLE_HEADER_CAP_COL, csHeaderCaptionCol);
		styles.put(CELL_STYLE_MID_HEADER_CAP_COL, csMidHeaderCaptionCol);
		styles.put(CELL_STYLE_HEADER_DATA_COL, csHeaderDataCol);
		styles.put(CELL_STYLE_DETAILS_CAP_COL, csDataAlt1);
		styles.put(CELL_STYLE_DETAILS_CAP_COL_ALT, csDataAlt2);
		styles.put(CELL_STYLE_DETAILS_DATA_COL, csDataAlt1Data);
		styles.put(CELL_STYLE_DETAILS_DATA_COL_ALT, csDataAlt2Data);
		styles.put(CELL_STYLE_SUMMERY_CAP_COL, csSumary);
		styles.put(CELL_STYLE_SUMMERY_DATA_COL, csSumaryData);
		styles.put(CELL_STYLE_SUMMERY_CAP_COL_ALT, csSumaryAlt2);
		styles.put(CELL_STYLE_SUMMERY_DATA_COL_ALT, csSumaryDataAlt2);

		styles.put(CELL_STYLE_DETAILS_DATA_COL_COUNT, csDataAlt1DataForCount);
		styles.put(CELL_STYLE_DETAILS_DATA_COL_COUNT_ALT, csDataAlt2DataForCount);
		styles.put(CELL_STYLE_SUMMERY_DATA_COL_COUNT, csSumaryDataForCount);
		styles.put(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT, csSumaryDataAlt2ForCount);
		styles.put(CELL_STYLE_TITLES, csReportTitle);
		styles.put(CELL_STYLE_REPORT_PROMPTS_CAP, csPromptsCaption);
		styles.put(CELL_STYLE_REPORT_PROMPTS, csReportPrompt);

		return styles;
	}

//	private static Font createNewFont(Workbook workBook, short fontWeight, String fontName, int heightInPoints) {
//		Font font = workBook.createFont();
//		byte[] greenClr = { (byte) 177, (byte) 24, (byte) 124 };
//		XSSFColor rowClr = new XSSFColor(greenClr);
//		font.setColor(rowClr.getIndexed());
//		font.setBoldweight(fontWeight);
//		font.setFontName(fontName);
//		font.setFontHeightInPoints((short) heightInPoints);
//		return font;
//	}
	private static Font createNewFont(Workbook workBook, boolean fontWeight, String fontName, int heightInPoints){
		Font font = workBook.createFont();
		byte[] greenClr = {(byte) 177, (byte) 24, (byte) 124};
		XSSFColor rowClr = new XSSFColor(greenClr);
		font.setColor(rowClr.getIndexed());
		font.setBold(fontWeight);
		font.setFontName(fontName);
		font.setFontHeightInPoints((short)heightInPoints);
		return font;
	}
	public static int writeReportDataRA(Workbook workBook, ReportsVb reportVb, List<ColumnHeadersVb> colHeaderslst,
			List<HashMap<String, String>> dataLst, SXSSFSheet sheet, int rowNum, Map<Integer, XSSFCellStyle> styls,
			List<String> columnTypes, Map<Integer, Integer> columnWidths, Boolean totalRow, String assetFolderUrl) {
		Row row = null;
		Cell cell = null;
		String[] capGrpCols = null;
		ArrayList<String> groupingCols = new ArrayList<String>();
		String screenSortColumn = reportVb.getScreenSortColumn();
		ArrayList<ColumnHeadersVb> columnHeadersFinallst = new ArrayList<ColumnHeadersVb>();
		colHeaderslst.forEach(colHeadersVb -> {
			if (colHeadersVb.getColspan() <= 1 && colHeadersVb.getNumericColumnNo() != 99) {
				columnHeadersFinallst.add(colHeadersVb);
			}
		});
		if (ValidationUtil.isValid(reportVb.getScreenGroupColumn())) {
			reportVb.setPdfGroupColumn(reportVb.getScreenGroupColumn());
		}
		if (ValidationUtil.isValid(reportVb.getPdfGroupColumn()))
			capGrpCols = reportVb.getPdfGroupColumn().split("!@#");

		if (reportVb.getTotalRows() <= reportVb.getMaxRecords() && !totalRow && capGrpCols != null
				&& capGrpCols.length > 0) {
			for (String grpStr : capGrpCols) {
				for (ColumnHeadersVb colHeader : columnHeadersFinallst) {
					if (grpStr.equalsIgnoreCase(colHeader.getCaption().toUpperCase())) {
						groupingCols.add(colHeader.getDbColumnName());
						break;
					}
				}
			}
		}
		final String[] grpColNames = capGrpCols;
		Map<String, List<HashMap<String, String>>> groupingMap = new HashMap<String, List<HashMap<String, String>>>();

		if (reportVb.getTotalRows() <= reportVb.getMaxRecords() && !totalRow
				&& (groupingCols != null && groupingCols.size() > 0)) {
			switch (groupingCols.size()) {
			case 1:
				groupingMap = dataLst.stream()
						.collect(Collectors.groupingBy(m -> (m.get(groupingCols.get(0))) == null ? ""
								: grpColNames[0] + ": " + m.get(groupingCols.get(0))));
				break;
			case 2:
				groupingMap = dataLst.stream()
						.collect(Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0)) + " >> " + m.get(groupingCols.get(1))) == null ? ""
										: grpColNames[0] + ": " + m.get(groupingCols.get(0)) + " >> " + grpColNames[1]
												+ ": " + m.get(groupingCols.get(1))));
				break;
			case 3:
				groupingMap = dataLst.stream()
						.collect(Collectors.groupingBy(m -> (m.get(groupingCols.get(0)) + " >> "
								+ m.get(groupingCols.get(1)) + " >> " + m.get(groupingCols.get(2))) == null
										? ""
										: grpColNames[0] + ": " + m.get(groupingCols.get(0)) + " >> " + grpColNames[1]
												+ ": " + m.get(groupingCols.get(1)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(2))));
				break;
			case 4:
				groupingMap = dataLst.stream().collect(
						Collectors.groupingBy(m -> (m.get(groupingCols.get(0)) + " >> " + m.get(groupingCols.get(1))
								+ " >> " + m.get(groupingCols.get(2)) + " >> " + m.get(groupingCols.get(3))) == null
										? ""
										: grpColNames[0] + ": " + m.get(groupingCols.get(0)) + " >> " + grpColNames[1]
												+ ": " + m.get(groupingCols.get(1)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(2)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(3))));
				break;
			case 5:
				groupingMap = dataLst.stream()
						.collect(Collectors.groupingBy(m -> (m.get(groupingCols.get(0)) + " >> "
								+ m.get(groupingCols.get(1)) + " >> " + m.get(groupingCols.get(2)) + " >> "
								+ m.get(groupingCols.get(3)) + " >> " + m.get(groupingCols.get(4))) == null
										? ""
										: grpColNames[0] + ": " + m.get(groupingCols.get(0)) + " >> " + grpColNames[1]
												+ ": " + m.get(groupingCols.get(1)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(2)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(3)) + " >> " + grpColNames[2] + ": "
												+ m.get(groupingCols.get(4))));
				break;
			}
			Map<String, List<HashMap<String, String>>> sortedMap = new TreeMap<String, List<HashMap<String, String>>>();
			if (ValidationUtil.isValid(screenSortColumn)) {
				if (screenSortColumn.contains(groupingCols.get(0))) {
					String value = screenSortColumn.substring(9, screenSortColumn.length()).toUpperCase();
					String[] col = value.split(",");
					for (int i = 0; i < col.length; i++) {
						if (col[i].contains(groupingCols.get(0))) {
							String val = col[i];
							if (val.contains("DESC")) {
								sortedMap = new TreeMap<String, List<HashMap<String, String>>>(
										Collections.reverseOrder());
								sortedMap.putAll(groupingMap);
							} else {
								sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
							}
						}
					}
				} else {
					sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
				}
			} else {
				sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
			}
			// Map<String, List < HashMap<String, String> >> sortedMap = new TreeMap<String,
			// List < HashMap<String, String> >>(groupingMap);
			Set entrySet = sortedMap.entrySet();
			Iterator it = entrySet.iterator();
			while (it.hasNext()) {
				Map.Entry dataVal = (Map.Entry) it.next();
				List<HashMap<String, String>> grpDataLst = (List) dataVal.getValue();
				String key = (String) dataVal.getKey();
				if (key.isEmpty())
					continue;
				row = sheet.createRow(rowNum);
				cell = row.createCell(0);
				cell.setCellValue(dataVal.getKey().toString());
				cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
				sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, columnHeadersFinallst.size() - 1));
				rowNum++;
				rowNum = writeDatatoExcel(workBook, reportVb, colHeaderslst, grpDataLst, sheet, rowNum, styls,
						columnTypes, columnWidths, columnHeadersFinallst, row, cell, true, assetFolderUrl);
			}
		} else {
			rowNum = writeDatatoExcel(workBook, reportVb, colHeaderslst, dataLst, sheet, rowNum, styls, columnTypes,
					columnWidths, columnHeadersFinallst, row, cell, false, assetFolderUrl);
		}
		return rowNum;
	}

	private static int writeDatatoExcel(Workbook workBook, ReportsVb reportVb, List<ColumnHeadersVb> colHeaderslst,
			List<HashMap<String, String>> dataLst, SXSSFSheet sheet, int rowNum, Map<Integer, XSSFCellStyle> styls,
			List<String> columnTypes, Map<Integer, Integer> columnWidths,
			ArrayList<ColumnHeadersVb> columnHeadersFinallst, Row row, Cell cell, Boolean calculateTotal,
			String assetFolderUrl) {
		int rowSpanNumStart = 0;
		int rowSpanNumEnd = 0;
		String spanValue = "";
		String formatType = "D";
		HashMap<String, String> sumMap = new HashMap<String, String>();
		List<HashMap<String, String>> summationLst = new ArrayList();
		Double sumVal = 0.0;
		String firstColHeaderName = "";
		if (columnHeadersFinallst != null && columnHeadersFinallst.size() > 0)
			firstColHeaderName = columnHeadersFinallst.get(0).getDbColumnName();
		int imgCol = 0;
		Boolean hideColumn = false;
		if (dataLst != null) {
			for (HashMap dataMap : dataLst) {
				if (dataMap.containsKey("FORMAT_TYPE"))
					formatType = dataMap.get("FORMAT_TYPE").toString();
				row = sheet.createRow(rowNum);
				for (int loopCount = 0; loopCount < columnHeadersFinallst.size(); loopCount++) {

					ColumnHeadersVb colHeadersVb = columnHeadersFinallst.get(loopCount);
					if (colHeadersVb.getColspan() > 1 || colHeadersVb.getNumericColumnNo() == 99)
						continue;
					String cellValue = "";
					cell = row.createCell(loopCount);
					String colType = colHeadersVb.getColType();
					String colDiffer = colHeadersVb.getColorDiff();
					String type = "";
					if ("T".equalsIgnoreCase(colHeadersVb.getColType())) {
						type = CAP_COL;
					} else if ("P".equalsIgnoreCase(colHeadersVb.getColType())) {
						type = "GROWTH_IMG";
					} else {
						type = DATA_COL;
					}
					if (CAP_COL.equalsIgnoreCase(type)) {
						String orgValue = "";
						if (dataMap.containsKey(colHeadersVb.getDbColumnName())) {
							orgValue = ((dataMap.get(colHeadersVb.getDbColumnName())) != null
									|| dataMap.get(colHeadersVb.getDbColumnName()) == "")
											? dataMap.get(colHeadersVb.getDbColumnName()).toString()
											: "";
						}

						if ("S".equalsIgnoreCase(formatType)) {
							cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL_ALT));
							if (ValidationUtil.isValid(reportVb.getGrandTotalCaption()) && loopCount == 0) {
								orgValue = reportVb.getGrandTotalCaption();
							}
						} else if ("ST".equalsIgnoreCase(formatType) || "FT".equalsIgnoreCase(formatType)) {
							cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_CAP_COL));
						} else {
							cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
						}
						XSSFRichTextString string = new XSSFRichTextString(orgValue);
						cellValue = string.toString();
						cell.setCellValue(string);
						columnWidths.put(loopCount,
								(int) getColumnWidth(cell, null, true, columnWidths.get(loopCount)));
					} else if ("GROWTH_IMG".equalsIgnoreCase(type)) {
						String orgValue = "";
						imgCol = loopCount;
						hideColumn = true;
					} else {
						if ("S".equalsIgnoreCase(formatType)) {
							if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT_ALT));
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_ALT));
							}
						} else if ("ST".equalsIgnoreCase(formatType) || "FT".equalsIgnoreCase(formatType)) {
							if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL_COUNT));
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_SUMMERY_DATA_COL));
							}
						} else {
							if ("I".equalsIgnoreCase(colType) || "S".equalsIgnoreCase(colType)
									|| "NR".equalsIgnoreCase(colType)) {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL_COUNT));
							} else {
								cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_DATA_COL));
							}
						}
						String orgValue = "";
						if (dataMap.containsKey(colHeadersVb.getDbColumnName()))
							orgValue = ((dataMap.get(colHeadersVb.getDbColumnName())) != null
									|| dataMap.get(colHeadersVb.getDbColumnName()) == "")
											? dataMap.get(colHeadersVb.getDbColumnName()).toString()
											: "";

						if (ValidationUtil.isValid(orgValue)) {
							if ("NR".equalsIgnoreCase(colType) || "TR".equalsIgnoreCase(colType)) {
								cellValue = orgValue;
							} else {
								cellValue = ValidationUtil.replaceComma(orgValue.trim());
							}

							if (ValidationUtil.isNumericDecimal(cellValue)) {
//								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.valueOf(cellValue));
							} else {
								if (!"NR".equalsIgnoreCase(colType) && !"TR".equalsIgnoreCase(colType)) {
									Double val = Double.parseDouble(cellValue);
									DecimalFormat formatValue = new DecimalFormat("0.00");
									cell.setCellValue(formatValue.format(val));
								} else {
									cell.setCellValue(cellValue);
								}
							}
						} else {
							cell.setCellValue(cellValue);
						}
						columnWidths.put(loopCount,
								(int) getColumnWidth(cell, null, true, columnWidths.get(loopCount)));
						if ("NR".equalsIgnoreCase(colType) || "TR".equalsIgnoreCase(colType))
							continue;
						if (calculateTotal) {
							String prevValstr = sumMap.containsKey(colHeadersVb.getDbColumnName())
									? sumMap.get(colHeadersVb.getDbColumnName())
									: "0";
							if (!ValidationUtil.isValid(prevValstr))
								prevValstr = "0";
							if (!ValidationUtil.isValid(cellValue))
								cellValue = "0";
							Double strVal = Double.parseDouble(prevValstr);
							sumVal = strVal + Double.parseDouble(cellValue);
							DecimalFormat formatter = new DecimalFormat("####.00");
							String cellText = formatter.format(sumVal);
							sumMap.put(colHeadersVb.getDbColumnName(), cellText);
						}
					}
				}
				rowNum++;
			}
		} else {
			row = sheet.createRow(rowNum);
			cell = row.createCell(0);
			cell.setCellStyle(styls.get(CELL_STYLE_DETAILS_CAP_COL));
			cell.setCellValue("No Data Found");
			rowNum++;
		}
		// This is calculate the Groupwise total only when total row < max record
		if (calculateTotal) {
			if (!sumMap.isEmpty()) {
				sumMap.put("FORMAT_TYPE", "ST");
				sumMap.put(firstColHeaderName, "Sub Total");
				summationLst.add(sumMap);
				rowNum = writeDatatoExcel(workBook, reportVb, colHeaderslst, summationLst, sheet, rowNum, styls,
						columnTypes, columnWidths, columnHeadersFinallst, row, cell, false, assetFolderUrl);
			}
		}
		if (hideColumn)
			sheet.setColumnHidden(imgCol, true);
		return rowNum;
	}
}