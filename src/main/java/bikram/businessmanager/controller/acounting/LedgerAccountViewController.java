package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.JournalLineDto;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.service.JournalEntryService;
import bikram.businessmanager.service.ServiceContainer;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.service.SubAccountService;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LedgerAccountViewController implements Refreshable{

    @FXML private ComboBox<SubAccount> accountCombo;

    @FXML private Label totalDebitLabel;
    @FXML private Label totalCreditLabel;

    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;

    @FXML private TableView<JournalLineDto> tableView;

    @FXML private TableColumn<JournalLineDto,String> dateColumn;
    @FXML private TableColumn<JournalLineDto,String> voucherColumn;
    @FXML private TableColumn<JournalLineDto,String> descColumn;
    @FXML private TableColumn<JournalLineDto,BigDecimal> debitColumn;
    @FXML private TableColumn<JournalLineDto,BigDecimal> creditColumn;
    @FXML private TableColumn<JournalLineDto,BigDecimal> balanceColumn;
    private final JournalEntryService journalEntryService = ServiceProvider.services().getJournalEntryService();
    private final SubAccountService subAccountService = ServiceProvider.services().getSubAccountService();

    private static final DecimalFormat MONEY_FORMAT =
            new DecimalFormat("#,##0.00");

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final ObservableList<SubAccount> masterAccounts =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        setupColumns();
        setupAccountCombo();

        loadSubAccounts();
    }

    private void setupColumns() {

        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDate().format(DATE_FORMAT)
                ));

        voucherColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getVoucherNo()));

        descColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription()));

        debitColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getDebit()));

        creditColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getCredit()));

        balanceColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getBalance()));

        debitColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        creditColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        balanceColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupAccountCombo(){

        accountCombo.setEditable(false);

        accountCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(SubAccount item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        accountCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SubAccount item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    public void loadSubAccounts() {

        Task<List<SubAccount>> task = new Task<>() {
            @Override
            protected List<SubAccount> call() {

                return subAccountService.getAllByCompany(
                        SessionContext.getCurrentCompanyId()
                );
            }
        };

        task.setOnSucceeded(e -> {

            masterAccounts.setAll(task.getValue());

            FilteredList<SubAccount> filtered =
                    new FilteredList<>(masterAccounts, p -> true);

            accountCombo.setItems(filtered);

        });

        task.setOnFailed(e ->
                task.getException().printStackTrace()
        );

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void loadLedger(ActionEvent event) {

        SubAccount account = accountCombo.getValue();

        if(account == null){
            return;
        }

        loadLedger(account);
    }

    public void loadLedger(SubAccount account) {

        Task<ObservableList<JournalLineDto>> task = new Task<>() {
            @Override
            protected ObservableList<JournalLineDto> call() {

                List<JournalLineDto> list =
                        journalEntryService.getLedgerByCompany(
                                SessionContext.getCurrentCompany(),
                                account,
                                fromDate.getValue(),
                                toDate.getValue()
                        );

                return FXCollections.observableArrayList(list);
            }
        };

        task.setOnSucceeded(e -> {

            ObservableList<JournalLineDto> data = task.getValue();

            tableView.setItems(data);

            calculateTotals(data);

        });

        task.setOnFailed(e ->
                task.getException().printStackTrace()
        );

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void calculateTotals(List<JournalLineDto> lines){

        BigDecimal totalDebit = lines.stream()
                .map(JournalLineDto::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lines.stream()
                .map(JournalLineDto::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalDebitLabel.setText(MONEY_FORMAT.format(totalDebit));
        totalCreditLabel.setText(MONEY_FORMAT.format(totalCredit));
    }


    @Override
    public void refresh() {
        loadSubAccounts();
    }

    @FXML private void on_back(ActionEvent event) {
        Navigator.back();
    }
}