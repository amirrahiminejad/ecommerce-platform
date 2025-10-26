package com.webrayan.commerce.core.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import com.itextpdf.text.BaseColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Cell;
import com.itextpdf.text.FontFactory;

public class ExportUtil {
    public static <T> byte[] exportToExcel(List<T> data, Class<T> clazz, ExportExcelOptions options) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(options != null && options.getSheetName() != null ? options.getSheetName() : "Data");
            Field[] fields = clazz.getDeclaredFields();
            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) (options != null && options.getHeaderFontSize() > 0 ? options.getHeaderFontSize() : 12));
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Row style
            CellStyle rowStyle = workbook.createCellStyle();
            Font rowFont = workbook.createFont();
            rowFont.setFontHeightInPoints((short) (options != null && options.getRowFontSize() > 0 ? options.getRowFontSize() : 11));
            rowStyle.setFont(rowFont);
            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fields[i].getName());
                cell.setCellStyle(headerStyle);
                // تنظیم اندازه ستون
                int width = options != null && options.getColumnWidths() != null && options.getColumnWidths().length > i ? options.getColumnWidths()[i] : 20;
                sheet.setColumnWidth(i, width * 256);
            }
            // Data rows
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < fields.length; j++) {
                    Object value = fields[j].get(data.get(i));
                    Cell cell = row.createCell(j);
                    cell.setCellValue(value != null ? value.toString() : "");
                    cell.setCellStyle(rowStyle);
                    // رنگ سطرها (مثلاً سطرهای زوج)
                    if (options != null && options.isAlternateRowColor() && i % 2 == 1) {
                        CellStyle altStyle = workbook.createCellStyle();
                        altStyle.cloneStyleFrom(rowStyle);
                        altStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                        altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        cell.setCellStyle(altStyle);
                    }
                }
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IllegalAccessException e) {
            throw new IOException("Error exporting to Excel", e);
        }
    }

    public static <T> byte[] exportToPdf(List<T> data, Class<T> clazz, ExportPdfOptions options) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        Field[] fields = clazz.getDeclaredFields();
        PdfPTable table = new PdfPTable(fields.length);
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, options != null && options.getHeaderFontSize() > 0 ? options.getHeaderFontSize() : 12, com.itextpdf.text.Font.BOLD, options != null && options.getHeaderBgColor() != null ? options.getHeaderBgColor() : BaseColor.DARK_GRAY);
        PdfPCell headerCell;
        for (Field field : fields) {
            field.setAccessible(true);
            headerCell = new PdfPCell(new Phrase(field.getName(), headerFont));
            headerCell.setBackgroundColor(options != null && options.getHeaderBgColor() != null ? options.getHeaderBgColor() : BaseColor.DARK_GRAY);
            table.addCell(headerCell);
        }
        com.itextpdf.text.Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, options != null && options.getRowFontSize() > 0 ? options.getRowFontSize() : 11);
        for (int i = 0; i < data.size(); i++) {
            for (Field field : fields) {
                Object value = field.get(data.get(i));
                PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", rowFont));
                // Alternate row color
                if (options != null && options.isAlternateRowColor() && i % 2 == 1) {
                    cell.setBackgroundColor(options.getAlternateRowColor() != null ? options.getAlternateRowColor() : BaseColor.LIGHT_GRAY);
                }
                table.addCell(cell);
            }
        }
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    // کلاس‌های تنظیمات ظاهری
    public static class ExportExcelOptions {
        private String sheetName;
        private int[] columnWidths;
        private int headerFontSize = 12;
        private int rowFontSize = 11;
        private boolean alternateRowColor = false;
        // ...getter/setter...
        public String getSheetName() { return sheetName; }
        public void setSheetName(String sheetName) { this.sheetName = sheetName; }
        public int[] getColumnWidths() { return columnWidths; }
        public void setColumnWidths(int[] columnWidths) { this.columnWidths = columnWidths; }
        public int getHeaderFontSize() { return headerFontSize; }
        public void setHeaderFontSize(int headerFontSize) { this.headerFontSize = headerFontSize; }
        public int getRowFontSize() { return rowFontSize; }
        public void setRowFontSize(int rowFontSize) { this.rowFontSize = rowFontSize; }
        public boolean isAlternateRowColor() { return alternateRowColor; }
        public void setAlternateRowColor(boolean alternateRowColor) { this.alternateRowColor = alternateRowColor; }
    }
    public static class ExportPdfOptions {
        private int headerFontSize = 12;
        private int rowFontSize = 11;
        private BaseColor headerBgColor = BaseColor.DARK_GRAY;
        private boolean alternateRowColor = false;
        private BaseColor alternateRowColorValue = BaseColor.LIGHT_GRAY;
        // ...getter/setter...
        public int getHeaderFontSize() { return headerFontSize; }
        public void setHeaderFontSize(int headerFontSize) { this.headerFontSize = headerFontSize; }
        public int getRowFontSize() { return rowFontSize; }
        public void setRowFontSize(int rowFontSize) { this.rowFontSize = rowFontSize; }
        public BaseColor getHeaderBgColor() { return headerBgColor; }
        public void setHeaderBgColor(BaseColor headerBgColor) { this.headerBgColor = headerBgColor; }
        public boolean isAlternateRowColor() { return alternateRowColor; }
        public void setAlternateRowColor(boolean alternateRowColor) { this.alternateRowColor = alternateRowColor; }
        public BaseColor getAlternateRowColor() { return alternateRowColorValue; }
        public void setAlternateRowColor(BaseColor alternateRowColorValue) { this.alternateRowColorValue = alternateRowColorValue; }
    }
}
