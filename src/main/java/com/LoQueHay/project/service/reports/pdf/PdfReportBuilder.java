package com.LoQueHay.project.service.reports.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class PdfReportBuilder {

    public static byte[] buildReport(String title, List<String> columns, List<Map<String, Object>> rows) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 90, 36);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new FooterPageEvent());
            document.open();

            // Cabecera
            addHeader(document, title);

            // Tabla
            PdfPTable table = new PdfPTable(columns.size());
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            addTableHeader(table, columns);

            double[] totals = new double[columns.size()];
            int rowNum = 0;

            for (Map<String, Object> row : rows) {
                BaseColor bgColor = (rowNum % 2 == 0) ? BaseColor.WHITE : new BaseColor(245, 245, 245);
                addTableRow(table, columns, row, bgColor, totals);
                rowNum++;
            }

            addTotalRow(table, columns, totals);

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }

        return out.toByteArray();
    }

    private static void addHeader(Document document, String title) throws DocumentException {
        try {
            Image logo = Image.getInstance("src/main/resources/static/logo.png");
            logo.scaleToFit(90, 45);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);
        } catch (Exception ignored) {}

        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
        Paragraph pTitle = new Paragraph(title, titleFont);
        pTitle.setAlignment(Element.ALIGN_CENTER);
        pTitle.setSpacingAfter(5f);
        document.add(pTitle);

        Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL);
        Paragraph subTitle = new Paragraph("Fecha: " + LocalDate.now(), subFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        subTitle.setSpacingAfter(15f);
        document.add(subTitle);
    }

    private static void addTableHeader(PdfPTable table, List<String> columns) {
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
        BaseColor headerColor = new BaseColor(220, 220, 220);

        for (String col : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(col, font));
            cell.setBackgroundColor(headerColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingTop(4f);
            cell.setPaddingBottom(4f);
            cell.setPaddingLeft(6f);
            cell.setPaddingRight(6f);
            table.addCell(cell);
        }
    }

    private static void addTableRow(PdfPTable table, List<String> columns, Map<String, Object> row, BaseColor bgColor, double[] totals) {
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

        for (int i = 0; i < columns.size(); i++) {
            String col = columns.get(i);
            Object value = row.getOrDefault(col, "");

            PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", font));
            cell.setBackgroundColor(bgColor);
            cell.setPaddingTop(3f);
            cell.setPaddingBottom(3f);
            cell.setPaddingLeft(5f);
            cell.setPaddingRight(5f);

            if (value instanceof Number) {
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totals[i] += ((Number) value).doubleValue();
            } else {
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            }

            table.addCell(cell);
        }
    }

    private static void addTotalRow(PdfPTable table, List<String> columns, double[] totals) {
        boolean hasTotals = false;
        for (double t : totals) if (t != 0) { hasTotals = true; break; }
        if (!hasTotals) return;

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
        BaseColor totalColor = new BaseColor(220, 220, 220);

        for (int i = 0; i < columns.size(); i++) {
            PdfPCell cell;
            if (i == 0) {
                cell = new PdfPCell(new Phrase("Totales", font));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            } else if (totals[i] != 0) {
                cell = new PdfPCell(new Phrase(String.format("%.2f", totals[i]), font));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            } else {
                cell = new PdfPCell(new Phrase("", font));
            }
            cell.setBackgroundColor(totalColor);
            cell.setPaddingTop(3f);
            cell.setPaddingBottom(3f);
            cell.setPaddingLeft(5f);
            cell.setPaddingRight(5f);
            table.addCell(cell);
        }
    }

    static class FooterPageEvent extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.ITALIC);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable footer = new PdfPTable(1);
            try {
                footer.setWidths(new int[]{1});
                footer.setTotalWidth(520);
                footer.setLockedWidth(true);
                footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                footer.addCell(new Phrase(String.format("Página %d", writer.getPageNumber()), ffont));
                footer.writeSelectedRows(0, -1, 36, 30, writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }
    }
}
