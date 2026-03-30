package bikram.businessmanager.file_manager;

import java.io.IOException;

public interface ReportGenerator<T> {
    byte[] generatePDF(T report) throws IOException;
    byte[] generateExcel(T report) throws IOException;
}
