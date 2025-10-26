package com.webrayan.store.util;

import com.itextpdf.text.BaseColor;
import com.webrayan.store.core.common.entity.Tag;
import com.webrayan.store.core.util.ExportUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * نمونه تست برای استفاده از ExportUtil جهت خروجی اکسل و PDF
 */
public class ExportUtilTest {

    @Test
    void testExportToExcelWithOptions() throws Exception {
        // آماده‌سازی داده نمونه
        List<Tag> tags = Arrays.asList(
                createTag(1L, "java"),
                createTag(2L, "spring")
        );
        // تنظیمات ظاهری
        ExportUtil.ExportExcelOptions options = new ExportUtil.ExportExcelOptions();
        options.setSheetName("Tags");
        options.setColumnWidths(new int[]{10, 30});
        options.setHeaderFontSize(14);
        options.setRowFontSize(12);
        options.setAlternateRowColor(true);
        // خروجی اکسل
        byte[] excelBytes = ExportUtil.exportToExcel(tags, Tag.class, options);
        assertNotNull(excelBytes);
        // بررسی باز شدن فایل اکسل (بدون خطا)
        assertDoesNotThrow(() -> WorkbookFactory.create(new ByteArrayInputStream(excelBytes)));
    }

    @Test
    void testExportToPdfWithOptions() throws Exception {
        List<Tag> tags = Arrays.asList(
                createTag(1L, "java"),
                createTag(2L, "spring")
        );
        ExportUtil.ExportPdfOptions options = new ExportUtil.ExportPdfOptions();
        options.setHeaderFontSize(16);
        options.setRowFontSize(12);
        options.setHeaderBgColor(BaseColor.BLUE);
        options.setAlternateRowColor(true);
        options.setAlternateRowColor(BaseColor.YELLOW);
        // خروجی PDF
        byte[] pdfBytes = ExportUtil.exportToPdf(tags, Tag.class, options);
        assertNotNull(pdfBytes);
        // بررسی اینکه فایل PDF خالی نیست
        assertTrue(pdfBytes.length > 100);
    }

    // متد کمکی برای ساخت نمونه Tag
    private Tag createTag(Long id, String name) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }
}

/**
 * راهنمای استفاده:
 *
 * ExportExcelOptions options = new ExportExcelOptions();
 * options.setSheetName("MySheet");
 * options.setColumnWidths(new int[]{20, 40});
 * options.setHeaderFontSize(14);
 * options.setRowFontSize(12);
 * options.setAlternateRowColor(true);
 * byte[] excel = ExportUtil.exportToExcel(list, Entity.class, options);
 *
 * ExportPdfOptions pdfOptions = new ExportPdfOptions();
 * pdfOptions.setHeaderFontSize(16);
 * pdfOptions.setRowFontSize(12);
 * pdfOptions.setHeaderBgColor(BaseColor.BLUE);
 * pdfOptions.setAlternateRowColor(true);
 * pdfOptions.setAlternateRowColor(BaseColor.YELLOW);
 * byte[] pdf = ExportUtil.exportToPdf(list, Entity.class, pdfOptions);
 */
