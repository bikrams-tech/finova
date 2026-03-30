package bikram.businessmanager.file_manager.excelMaker;

import bikram.businessmanager.dto.BalanceSheetReport;
import bikram.businessmanager.dto.BalanceSheetRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class BalanceSheetXcelMaker {
    public static byte[] generateExcel(BalanceSheetReport report) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Balance Sheet");

        int rowNum = 0;

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // Helper to write a section
        rowNum = writeSection(sheet, rowNum, "Assets", report.assets(), headerStyle);
        rowNum = writeSection(sheet, rowNum, "Liabilities", report.liabilities(), headerStyle);
        rowNum = writeSection(sheet, rowNum, "Equity", report.equity(), headerStyle);

        // Totals
        rowNum = writeTotal(sheet, rowNum, "Total Assets", report.totalAssets(), headerStyle);
        rowNum = writeTotal(sheet, rowNum, "Total Liabilities", report.totalLiabilities(), headerStyle);
        rowNum = writeTotal(sheet, rowNum, "Total Equity", report.totalEquity(), headerStyle);

        // Autosize columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    private static int writeSection(Sheet sheet, int rowNum, String title, List<BalanceSheetRow> rows, CellStyle headerStyle) {
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(headerStyle);

        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Account");
        headerRow.createCell(1).setCellValue("Amount");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);

        for (BalanceSheetRow row : rows) {
            Row r = sheet.createRow(rowNum++);
            r.createCell(0).setCellValue(row.accountName());
            r.createCell(1).setCellValue(row.amount().doubleValue());
        }
        rowNum++; // extra space after section
        return rowNum;
    }

    private static int writeTotal(Sheet sheet, int rowNum, String label, java.math.BigDecimal value, CellStyle headerStyle) {
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(0);
        cell.setCellValue(label);
        cell.setCellStyle(headerStyle);

        Cell valCell = row.createCell(1);
        valCell.setCellValue(value.doubleValue());
        valCell.setCellStyle(headerStyle);
        return rowNum;
    }
}
