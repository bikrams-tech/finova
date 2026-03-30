package bikram.businessmanager.controller.ui;

import bikram.businessmanager.dto.BalanceSheetReport;
import bikram.businessmanager.file_manager.pdfmaker.BalanceSheetPdfGenerator;
import bikram.businessmanager.utils.FileSaver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;

public class FileManagerController {

    @FXML private TextField customNameField;
    @FXML private ListView<File> pdfListView;
    @FXML private ListView<File> excelListView;

    private BalanceSheetReport balanceSheetReport; // inject or set from main controller

    @FXML
    public void initialize() {
        refreshFileList();
    }

    @FXML
    public void onSavePdf(ActionEvent event) {
        try {
            if (balanceSheetReport == null) return;
            String name = customNameField.getText();
            byte[] pdfBytes = BalanceSheetPdfGenerator.generate(balanceSheetReport);
            FileSaver.saveFile(pdfBytes, "PDF", name);
            refreshFileList();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void onSaveExcel(ActionEvent event) {
        try {
            if (balanceSheetReport == null) return;
            String name = customNameField.getText();
            byte[] excelBytes = ExcelGenerator.generateExcel(balanceSheetReport);
            FileSaver.saveFile(excelBytes, "Excel", name);
            refreshFileList();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void onPreviewPdf() {
        File selected = pdfListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            PDDocument doc = PDDocument.load(selected);
            PDFRenderer renderer = new PDFRenderer(doc);
            BufferedImage img = renderer.renderImageWithDPI(0, 150);
            doc.close();

            Image fxImage = SwingFXUtils.toFXImage(img, null);
            ImageView iv = new ImageView(fxImage);
            iv.setFitWidth(500);
            iv.setPreserveRatio(true);

            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(new VBox(iv)));
            stage.setTitle(selected.getName());
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void onPreviewExcel() {
        File selected = excelListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try (Workbook workbook = new XSSFWorkbook(selected)) {
            Sheet sheet = workbook.getSheetAt(0);
            TableView<List<String>> tableView = new TableView<>();
            int numCols = sheet.getRow(1).getLastCellNum(); // header row
            for (int i = 0; i < numCols; i++) {
                final int colIndex = i;
                TableColumn<List<String>, String> col = new TableColumn<>(sheet.getRow(1).getCell(i).toString());
                col.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get(colIndex)));
                tableView.getColumns().add(col);
            }

            for (Row r : sheet) {
                List<String> rowData = new ArrayList<>();
                for (int i = 0; i < numCols; i++) {
                    rowData.add(r.getCell(i) != null ? r.getCell(i).toString() : "");
                }
                tableView.getItems().add(rowData);
            }

            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(new VBox(tableView)));
            stage.setTitle(selected.getName());
            stage.show();

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshFileList() {
        pdfListView.getItems().clear();
        excelListView.getItems().clear();
        File[] files = FileSaver.listAllFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().toLowerCase().endsWith(".pdf")) pdfListView.getItems().add(f);
                if (f.getName().toLowerCase().endsWith(".xlsx")) excelListView.getItems().add(f);
            }
        }
    }

    public void setBalanceSheetReport(BalanceSheetReport report) {
        this.balanceSheetReport = report;
    }
}
