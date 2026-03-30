package bikram.businessmanager.file_manager.pdfmaker;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class InvoiceGenerator {
    public static void generateInvoice() throws Exception{
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

// Title
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Invoice");
        contentStream.endText();

// Customer info
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(50, 720);
        contentStream.showText("Customer: John Doe");
        contentStream.endText();

// Table (simple example)
        float yPosition = 680;
        String[][] data = {
                {"Item", "Qty", "Price"},
                {"Apple", "2", "$3.00"},
                {"Orange", "5", "$7.50"}
        };
        for (String[] row : data) {
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(String.join("    ", row));
            contentStream.endText();
            yPosition -= 20;
        }

        contentStream.close();
        document.save("invoice.pdf");
        document.close();
    }
}
