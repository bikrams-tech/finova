package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.entity.account.JournalEntryStatus;
import bikram.businessmanager.service.JournalEntryService;
import bikram.businessmanager.service.ServiceContainer;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class JournalEntryListViewController implements Refreshable {

    @FXML public TableColumn<JournalEntry , String> dateColumn;
    @FXML private TableView<JournalEntry> journalTable;

    @FXML private TableColumn<JournalEntry,String> voucherColumn;
    @FXML private TableColumn<JournalEntry,String> typeColumn;
    @FXML private TableColumn<JournalEntry,JournalEntryStatus> statusColumn;

    @FXML private TableColumn<JournalEntry,BigDecimal> debitColumn;
    @FXML private TableColumn<JournalEntry,BigDecimal> creditColumn;
    private  JournalEntryService journalEntryService;
    private static final java.time.format.DateTimeFormatter DATE_FORMAT =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        journalEntryService = ServiceProvider.services().getJournalEntryService();
        setupColumns();
        loadData();
        setRowClick();
    }

    private void setupColumns() {

        dateColumn.setCellValueFactory(data -> {

            var date = data.getValue().getDate();

            String formatted = date == null
                    ? ""
                    : date.format(DATE_FORMAT);

            return new SimpleStringProperty(formatted);
        });


        voucherColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getVoucherNo())
        );

        typeColumn.setCellValueFactory(data -> {
            var type = data.getValue().getVoucherType();
            return new SimpleStringProperty(type != null ? type.name() : "UNKNOWN");
        });

        statusColumn.setCellValueFactory(
                data -> new SimpleObjectProperty<>(data.getValue().getStatus())
        );


        debitColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(calculateDebit(data.getValue()))
        );

        creditColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(calculateCredit(data.getValue()))
        );
    }

    private BigDecimal calculateDebit(JournalEntry entry) {
        return entry.getLine()
                .stream()
                .map(l -> l.getDebit())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateCredit(JournalEntry entry) {
        return entry.getLine()
                .stream()
                .map(l -> l.getCredit())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void loadData() {

        new Thread(() -> {

            List<JournalEntry> list =
                    journalEntryService.getAllByCompany(
                            SessionContext.getCurrentCompanyId()
                    );

            ObservableList<JournalEntry> entries =
                    javafx.collections.FXCollections.observableArrayList(list);

            javafx.application.Platform.runLater(() ->
                    journalTable.setItems(entries)
            );

        }).start();
    }
    public void setRowClick() {

        journalTable.setRowFactory(tv -> {
            TableRow<JournalEntry> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (!row.isEmpty() && event.getClickCount() == 2) {

                    JournalEntry selectedEntry = row.getItem();

                    openJournalDetail(selectedEntry);

                }

            });

            return row;
        });
    }

    private void openJournalDetail(JournalEntry entry) {

        JournalEntryLineViewController controller =
                Navigator.navigate("/bikram/businessmanager/accountingView/report/journalEntryLineView.fxml");

        if (controller != null) {
            controller.setJournalEntry(entry);
        }

    }

    @Override
    public void refresh() {
        loadData();
    }

    @FXML private void on_back(ActionEvent event) {
        Navigator.back();
    }
}
