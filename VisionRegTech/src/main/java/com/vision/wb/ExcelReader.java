//package com.vision.wb;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.DateUtil;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//public class ExcelReader {
//    public static void main(String[] args) {
//        String excelFilePath = "C:\\Users\\Welcome\\Desktop\\NPA_LOANS.xlsx";
//        try (FileInputStream fis = new FileInputStream(excelFilePath);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//
//            Sheet sheet = workbook.getSheetAt(2);
//            for (Row row : sheet) {
//                for (Cell cell : row) {
//                    int cellType = cell.getCellType();
//                    if (cellType == Cell.CELL_TYPE_STRING) {
//                        System.out.println("String value: " + cell.getStringCellValue());
//                    } else if (cellType == Cell.CELL_TYPE_NUMERIC || cellType == Cell.CELL_TYPE_FORMULA) {
//                        if (DateUtil.isCellDateFormatted(cell)) {
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                            System.out.println("Date value: " + dateFormat.format(cell.getDateCellValue()));
//                        } else {
//                            System.out.println("Numeric value: " + cell.getNumericCellValue());
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
