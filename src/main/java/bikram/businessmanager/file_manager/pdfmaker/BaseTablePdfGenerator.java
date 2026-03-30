package bikram.businessmanager.file_manager.pdfmaker;

import bikram.businessmanager.file_manager.ReportGenerator;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;

public abstract class BaseTablePdfGenerator<T> implements ReportGenerator<T> {

    protected float MARGIN = 50;
    protected float ROW_HEIGHT = 25;

    protected float PAGE_WIDTH = PDRectangle.A4.getWidth();
    protected float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    protected void drawRow(PDPageContentStream content, float y, String col1, String col2) throws IOException {
        // same logic as BalanceSheetPdfGenerator, text vertically centered
    }

    protected float drawSection(PDPageContentStream content, float y, String title, List<> rows) throws IOException {
        // title + header + rows
        return y;
    }
}
