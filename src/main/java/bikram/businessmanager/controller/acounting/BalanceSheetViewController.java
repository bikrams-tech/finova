package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.BalanceSheetReport;
import bikram.businessmanager.dto.BalanceSheetRow;
import bikram.businessmanager.file_manager.pdfmaker.BalanceSheetPdfGenerator;
import bikram.businessmanager.service.BalanceSheetService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.SessionContext;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BalanceSheetViewController implements Refreshable {
    private BalanceSheetReport balanceSheetReport;

    private final BalanceSheetService service = ServiceProvider.services().balanceSheetService;

    @FXML
    private TableView<BalanceSheetRow> assetTable;

    @FXML
    private TableColumn<BalanceSheetRow, String> assetNameColumn;

    @FXML
    private TableColumn<BalanceSheetRow, BigDecimal> assetAmountColumn;

    @FXML
    private Label totalAssetLabel;

    @FXML
    private TableView<BalanceSheetRow> liabilityTable;

    @FXML
    private TableColumn<BalanceSheetRow, String> liabilityNameColumn;

    @FXML
    private TableColumn<BalanceSheetRow, BigDecimal> liabilityAmountColumn;

    @FXML
    private Label totalLiabilityLabel;

    @FXML
    private Label totalEquityLabel;

    public void initialize() {
        setUpTable();
        loadData();
    }

    private void loadData() {

        Task<BalanceSheetReport> task = new Task<>() {
            @Override
            protected BalanceSheetReport call() {
                return service.buildBalanceSheet(
                        SessionContext.getCurrentCompanyId(),
                        LocalDate.now()
                );
            }
        };

        task.setOnSucceeded(event -> {

            BalanceSheetReport report = task.getValue();

            assetTable.setItems(FXCollections.observableArrayList(report.assets()));
            liabilityTable.setItems(FXCollections.observableArrayList(report.liabilities()));

            totalAssetLabel.setText(report.totalAssets().toString());
            totalLiabilityLabel.setText(report.totalLiabilities().toString());
            totalEquityLabel.setText(report.totalEquity().toString());
            balanceSheetReport = task.getValue();
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            throw new RuntimeException("failed to load data");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void setUpTable() {
        assetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        liabilityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        assetNameColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().accountName())
        );

        assetAmountColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().amount())
        );

        liabilityNameColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().accountName())
        );

        liabilityAmountColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().amount())
        );
    }

    @Override
    public void refresh() {
        loadData();
    }

    @FXML
    public void on_preview_print(ActionEvent actionEvent) {
        try {
            if (balanceSheetReport == null) return;

            // Generate PDF bytes in memory
            byte[] pdfBytes = BalanceSheetPdfGenerator.generate(balanceSheetReport);

            // Load PDF in PDFBox for preview
            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));
            PDFRenderer renderer = new PDFRenderer(document);

            // Render first page as image for preview
            BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 150);
            document.close();

            // Convert to JavaFX Image
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

            // Create ImageView and fix size to A4 ratio
            ImageView imageView = new ImageView(fxImage);

            // A4 aspect ratio: width / height ≈ 595 / 842 ≈ 0.705
            double a4Width = 595;
            double a4Height = 842;

            // Scale image to fit within a reasonable window while keeping A4 ratio
            double scale = Math.min(500 / a4Width, 700 / a4Height); // max 500x700 px window
            imageView.setFitWidth(a4Width * scale);
            imageView.setFitHeight(a4Height * scale);
            imageView.setPreserveRatio(true);

            // Create preview window
            Button saveButton = new Button("Save PDF");
            VBox vbox = new VBox(10, imageView, saveButton);
            vbox.setStyle("-fx-padding: 10; -fx-alignment: center;");
            Stage previewStage = new Stage();
            previewStage.setTitle("Balance Sheet Preview");
            previewStage.setScene(new Scene(vbox));
            previewStage.show();

            // Save button handler
            saveButton.setOnAction(e -> {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setInitialFileName("BalanceSheet.pdf");
                java.io.File file = fileChooser.showSaveDialog(previewStage);
                if (file != null) {
                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                        fos.write(pdfBytes);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save_as_excell(ActionEvent actionEvent) {
    }

    public void save_as_pdf(ActionEvent actionEvent) {
    }

    public void print(ActionEvent actionEvent) {
        
    }
}