package bikram.businessmanager.controller.acounting;


import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.CompanyDto;
import bikram.businessmanager.dto.JournalLineDto;
import bikram.businessmanager.dto.SubAccountDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Customer;
import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.entity.account.JournalEntryStatus;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.entity.account.VoucherType;
import bikram.businessmanager.service.*;
import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.BillNumberGenerator;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class JournalEntryViewController implements Refreshable {

    @FXML private ComboBox<Customer> customerSelectCombo;
    @FXML private TextField voucherNoField;
    @FXML private TextField noteTextField;
    @FXML private TextField debitInputField;
    @FXML private TextField creditInputField;
    @FXML
    private ComboBox<SubAccountDto> accountSelectCombo;
    @FXML
    private DatePicker datePickerField;
    @FXML
    private ComboBox<CompanyDto> companyCombo;
    @FXML
    private ComboBox<VoucherType> voucherTypeCombo;
    @FXML
    private TextField labelField;
    @FXML
    private TableView<JournalLineDto> journalTable;
    @FXML
    private TableColumn<JournalLineDto, SubAccountDto> accountColumn;
    @FXML
    private TableColumn<JournalLineDto, BigDecimal> debitColumn;
    @FXML
    private TableColumn<JournalLineDto, BigDecimal> creditColumn;
    @FXML
    private TableColumn<JournalLineDto, String> descriptionColumn;
    @FXML
    private TableColumn<JournalLineDto, Void> actionColumn;
    @FXML
    private Button addLineBtn;
    @FXML
    private Button removeLineBtn;
    @FXML
    private Label totalDebitLabel;
    @FXML
    private Label totalCreditLabel;

    private ObservableList<JournalLineDto> lines = FXCollections.observableArrayList();


    private final SubAccountService subAccountService = ServiceProvider.services().getSubAccountService();
    private final CompanyService companyService = ServiceProvider.services().getCompanyService();
    private final JournalEntryService journalEntryService = ServiceProvider.services().getJournalEntryService();

    private final Company currentCompany =
            companyService.getById(SessionContext.getCurrentCompanyId());



    @FXML
    public void initialize() {

        voucherTypeCombo.getItems().setAll(VoucherType.values());

        setupCompanyCombo();
        setupAccountCombo();
        setupTable();
        setupActionColumn();

        loadInitialData();
    }

    private void setupCompanyCombo() {

        companyCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(CompanyDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        });

        companyCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CompanyDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        });
    }

    private void setupAccountCombo() {

        accountSelectCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(SubAccountDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : item.getAccountName() + " → " + item.getSubAccountName());
            }
        });

        accountSelectCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SubAccountDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : item.getAccountName() + " → " + item.getSubAccountName());
            }
        });
    }

    @FXML
    private void saveVoucher() {

        if (lines.isEmpty()) {
            AppAlert.error(noteTextField.getScene().getWindow(),"No journal lines added.");
            return;
        }

        if (!isBalanced()) {
            AppAlert.error(noteTextField.getScene().getWindow(),"Journal is not balanced.");
            return;
        }

        try {

            Company company =
                    companyService.getById(companyCombo.getValue().id());

            JournalEntry journalEntry = new JournalEntry();

            journalEntry.setCompany(company);
            journalEntry.setVoucherType(voucherTypeCombo.getValue());

            LocalDate date =
                    datePickerField.getValue() == null
                            ? LocalDate.now()
                            : datePickerField.getValue();

            journalEntry.setDate(date);

            journalEntry.setVoucherNo(voucherNoField.getText());

            journalEntry.setStatus(JournalEntryStatus.POSTED);

            for (JournalLineDto dto : lines) {

                bikram.businessmanager.entity.account.JournalLine line =
                        new bikram.businessmanager.entity.account.JournalLine();

                line.setSubAccount(
                        subAccountService.getSubAccountByIdAndCompany(
                                company.getId(),
                                dto.getSubAccount().getId()
                        )
                );

                line.setDebit(dto.getDebit());
                line.setCredit(dto.getCredit());
                line.setNote(dto.getNote());
                line.setCompany(company);
                line.setJournalEntry(journalEntry);

                journalEntry.getLine().add(line);
            }

            journalEntryService.postJournalEntrywithoutem(journalEntry);

            showSuccess("Voucher saved successfully!");

            clearForm();

        } catch (Exception e) {

            e.printStackTrace();

            AppAlert.error(noteTextField.getScene().getWindow(),"Failed to save voucher: " + e.getMessage());
        }
    }



    private void setupTable() {

        accountColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getSubAccount()
                )
        );

        accountColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(SubAccountDto account, boolean empty) {
                super.updateItem(account, empty);
                setText(empty || account == null
                        ? null
                        : account.getAccountName() + " → " + account.getSubAccountName());
            }
        });

        debitColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getDebit()
                )
        );

        creditColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getCredit()
                )
        );
        descriptionColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getNote()
                )
        );

        journalTable.setItems(lines);
    }

    @FXML
    private void addLine(ActionEvent event) {
        SubAccountDto account = accountSelectCombo.getValue();

        if (account == null) {
            AppAlert.error(noteTextField.getScene().getWindow(),"Please select account.");
            return;
        }

        BigDecimal debit = parseAmount(debitInputField.getText());
        BigDecimal credit = parseAmount(creditInputField.getText());

        JournalLineDto line = JournalLineDto.builder()
                .subAccount(account)
                .debit(debit)
                .credit(credit)
                .note(noteTextField.getText())
                .build();

        lines.add(line);
        calculateTotals();
        clearLineInputs();
    }

    private void clearLineInputs() {
        debitInputField.clear();
        creditInputField.clear();
        accountSelectCombo.getSelectionModel().clearSelection();
        noteTextField.clear(); // add this
    }





    private void calculateTotals() {

        BigDecimal totalDebit = lines.stream()
                .map(JournalLineDto::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lines.stream()
                .map(JournalLineDto::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalDebitLabel.setText(totalDebit.toString());
        totalCreditLabel.setText(totalCredit.toString());
    }

    private boolean isBalanced() {

        BigDecimal totalDebit = lines.stream()
                .map(JournalLineDto::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lines.stream()
                .map(JournalLineDto::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalDebit.compareTo(totalCredit) == 0;
    }

    private BigDecimal parseAmount(String text) {
        if (text == null || text.isBlank()) return BigDecimal.ZERO;
        return new BigDecimal(text.trim());
    }

    // ================= LOAD =================

    private void loadInitialData() {

        datePickerField.setValue(LocalDate.now());

        Long companyId = currentCompany.getId();

        new Thread(() -> {

            List<SubAccountDto> subaccounts =
                    subAccountService.getAllDtos(companyId);

            List<CompanyDto> companies =
                    companyService.getallcompanyDto();

            javafx.application.Platform.runLater(() -> {

                accountSelectCombo.getItems().setAll(subaccounts);

                companyCombo.getItems().setAll(companies);

                companies.stream()
                        .filter(c -> c.id().equals(companyId))
                        .findFirst()
                        .ifPresent(c ->
                                companyCombo.getSelectionModel().select(c));

            });

        }).start();

        totalDebitLabel.setText("0");
        totalCreditLabel.setText("0");
    }

    // ================= ACTION COLUMN =================

    private void setupActionColumn() {

        actionColumn.setCellFactory(col -> new TableCell<>() {

            private final Button deleteButton = new Button("Delete");
            private final Button editButton = new Button("Edit");
            private final HBox box = new HBox(5, editButton, deleteButton);

            {
                deleteButton.setOnAction(e -> {
                    JournalLineDto line = getTableView().getItems().get(getIndex());
                    lines.remove(line);
                    calculateTotals();
                });

                editButton.setOnAction(e -> {
                    JournalLineDto line = getTableView().getItems().get(getIndex());

                    accountSelectCombo.setValue(line.getSubAccount());
                    debitInputField.setText(line.getDebit().toString());
                    creditInputField.setText(line.getCredit().toString());
                    noteTextField.setText(line.getNote());

                    lines.remove(line);
                    calculateTotals();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }



    private void clearForm() {
        lines.clear();
        totalDebitLabel.setText("0");
        totalCreditLabel.setText("0");
        debitInputField.clear();
        creditInputField.clear();
        accountSelectCombo.getSelectionModel().clearSelection();
    }


    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onSaveJournal(ActionEvent event) {
        saveVoucher();
    }

    @Override
    public void refresh() {
        loadInitialData();
    }

    public void on_back(ActionEvent event) {
        Navigator.back();
    }
}
