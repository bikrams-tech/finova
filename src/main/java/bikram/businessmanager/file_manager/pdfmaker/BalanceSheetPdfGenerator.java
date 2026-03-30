package bikram.businessmanager.file_manager.pdfmaker;

import bikram.businessmanager.dto.BalanceSheetReport;
import bikram.businessmanager.dto.BalanceSheetRow;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BalanceSheetPdfGenerator {

    private static final float MARGIN = 50;
    private static final float ROW_HEIGHT = 25; // taller row for vertical spacing
    private static final float CELL_MARGIN = 5;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    public static byte[] generate(BalanceSheetReport report) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float y = PAGE_HEIGHT - MARGIN;

        // Title
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 20);
        content.newLineAtOffset(MARGIN, y);
        content.showText("Balance Sheet Report");
        content.endText();
        y -= 2 * ROW_HEIGHT;

        // Draw sections
        y = drawSection(content, y, "Assets", report.assets());
        y = drawSection(content, y, "Liabilities", report.liabilities());
        y = drawSection(content, y, "Equity", report.equity());

        // Totals
        y -= ROW_HEIGHT;
        drawTotal(content, y, "Total Assets", report.totalAssets());
        y -= ROW_HEIGHT;
        drawTotal(content, y, "Total Liabilities", report.totalLiabilities());
        y -= ROW_HEIGHT;
        drawTotal(content, y, "Total Equity", report.totalEquity());

        content.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        return baos.toByteArray();
    }

    private static float drawSection(PDPageContentStream content, float y, String title, List<BalanceSheetRow> rows) throws IOException {
        float tableWidth = PAGE_WIDTH - 2 * MARGIN;
        float col1Width = tableWidth * 0.7f;
        float col2Width = tableWidth * 0.3f;

        // Add spacing before section title
        y -= ROW_HEIGHT / 2; // extra space between sections

        // Section title
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.newLineAtOffset(MARGIN, y);
        content.showText(title);
        content.endText();
        y -= ROW_HEIGHT;

        // Draw table header background
        content.setNonStrokingColor(220);
        content.addRect(MARGIN, y - ROW_HEIGHT, col1Width + col2Width, ROW_HEIGHT);
        content.fill();
        content.setNonStrokingColor(0);

        // Draw header text vertically centered
        drawTextCentered(content, y, col1Width, col2Width, new String[]{"Account", "Amount"}, true);

        // Draw table borders for header
        drawRowBorders(content, y, col1Width, col2Width);
        y -= ROW_HEIGHT;

        // Draw data rows
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        for (BalanceSheetRow row : rows) {
            drawTextCentered(content, y, col1Width, col2Width, new String[]{row.accountName(), nf.format(row.amount())}, false);
            drawRowBorders(content, y, col1Width, col2Width);
            y -= ROW_HEIGHT;
        }

        y -= ROW_HEIGHT / 4; // spacing after section
        return y;
    }

    private static void drawTextCentered(PDPageContentStream content, float y, float col1Width, float col2Width, String[] cells, boolean isHeader) throws IOException {
        float fontSize = 12f;
        if (isHeader) fontSize = 12f; // header same size or increase if you want

        // Vertically center
        float textY = y - ROW_HEIGHT + (ROW_HEIGHT - fontSize) / 2 + 2; // +2 to adjust visually

        // Column 1
        content.beginText();
        content.setFont(isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
        content.newLineAtOffset(MARGIN + CELL_MARGIN, textY);
        content.showText(cells[0]);
        content.endText();

        // Column 2
        content.beginText();
        content.setFont(isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
        content.newLineAtOffset(MARGIN + col1Width + CELL_MARGIN, textY);
        content.showText(cells[1]);
        content.endText();
    }

    private static void drawRowBorders(PDPageContentStream content, float y, float col1Width, float col2Width) throws IOException {
        float x = MARGIN;

        // Horizontal line
        content.moveTo(x, y - ROW_HEIGHT);
        content.lineTo(x + col1Width + col2Width, y - ROW_HEIGHT);
        content.stroke();

        // Vertical line
        content.moveTo(x + col1Width, y);
        content.lineTo(x + col1Width, y - ROW_HEIGHT);
        content.stroke();
    }

    private static void drawTotal(PDPageContentStream content, float y, String label, BigDecimal value) throws IOException {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);

        // Label left
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(MARGIN, y);
        content.showText(label);
        content.endText();

        // Amount right
        float textWidth = 100; // fixed width for right-alignment
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(PAGE_WIDTH - MARGIN - textWidth, y);
        content.showText(nf.format(value));
        content.endText();

        // Draw line under total
        content.moveTo(MARGIN, y - 5);
        content.lineTo(PAGE_WIDTH - MARGIN, y - 5);
        content.stroke();
    }


}