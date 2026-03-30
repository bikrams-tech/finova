package bikram.businessmanager.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileSaver {

    private static final String SAVE_DIR = "data_files"; // directory to store all files

    static {
        // create folder if not exists
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // Save bytes to file with timestamp prefix or custom name
    public static File saveFile(byte[] data, String prefix, String customName) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = (customName == null || customName.isEmpty())
                ? prefix + "_" + timestamp
                : customName;

        // Determine extension by prefix
        if (prefix.toLowerCase().contains("pdf")) fileName += ".pdf";
        else if (prefix.toLowerCase().contains("excel") || prefix.toLowerCase().contains("xls")) fileName += ".xlsx";
        else fileName += ".dat";

        File file = new File(SAVE_DIR, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        return file;
    }

    public static File[] listAllFiles() {
        File dir = new File(SAVE_DIR);
        return dir.listFiles();
    }
}