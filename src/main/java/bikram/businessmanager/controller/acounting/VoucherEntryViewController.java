package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.CompanyDto;
import bikram.businessmanager.dto.JournalLineDto;
import bikram.businessmanager.dto.SubAccountDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Customer;
import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.entity.account.VoucherType;
import bikram.businessmanager.service.*;

import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VoucherEntryViewController implements Refreshable {

    @FXML private TableColumn<JournalLineDto, String> noteColumField;
    @FXML private Button addlineButton;
    @FXML private TextField noteTextField;
    @FXML private TableView<JournalLineDto> voucherTable;
    @FXML private TableColumn<JournalLineDto, SubAccountDto> accountColumField;
    @FXML private TableColumn<JournalLineDto, BigDecimal> debitColumField;
    @FXML private TableColumn<JournalLineDto, BigDecimal> creditColumnField;
    @FXML private TableColumn<JournalLineDto, Void> actionColumnField;

    @FXML private TextField voucherNoField;
    @FXML private DatePicker datePickerField;
    @FXML private ComboBox<CompanyDto> companySelectCombo;
    @FXML private ComboBox<Customer> customerSelectCombo;
    @FXML private ComboBox<VoucherType> voucherSelectCombo;
    @FXML private ComboBox<SubAccountDto> accountSelectCombo;

    @FXML private TextField debitInputField;
    @FXML private TextField creditInputField;
    @FXML private Label totalDebitLabelField;
    @FXML private Label totalCreditLabelField;
    private final CompanyService companyService = ServiceProvider.services().getCompanyService();
    private final SubAccountService subAccountService = ServiceProvider.services().getSubAccountService();
    private final JournalEntryService journalEntryService = ServiceProvider.services().getJournalEntryService();

    private final ObservableList<JournalLineDto> lines =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        companySelectCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(CompanyDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        });

        companySelectCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CompanyDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        });
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

        voucherSelectCombo.getItems().setAll(VoucherType.values());

        setupTable();
        setupActionColumn();
        loadInitialData();
    }

    // ================= TABLE =================

    private void setupTable() {

        accountColumField.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getSubAccount()
                )
        );

        accountColumField.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(SubAccountDto account, boolean empty) {
                super.updateItem(account, empty);
                setText(empty || account == null
                        ? null
                        : account.getAccountName() + " → " + account.getSubAccountName());
            }
        });

        debitColumField.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getDebit()
                )
        );

        creditColumnField.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getCredit()
                )
        );
        noteColumField.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getNote()
                )
        );

        voucherTable.setItems(lines);
    }

    // ================= ADD LINE =================

    @FXML
    private void addLine() {

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
        System.out.println("Added line. Size = " + lines.size());

        calculateTotals();
        clearLineInputs();
    }

    private void clearLineInputs() {
        debitInputField.clear();
        creditInputField.clear();
        accountSelectCombo.getSelectionModel().clearSelection();
        voucherSelectCombo.getSelectionModel().clearSelection();
    }

    // ================= CALCULATIONS =================

    private void calculateTotals() {

        BigDecimal totalDebit = lines.stream()
                .map(JournalLineDto::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lines.stream()
                .map(JournalLineDto::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalDebitLabelField.setText(totalDebit.toString());
        totalCreditLabelField.setText(totalCredit.toString());
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

        new Thread(() -> {

            var subaccounts = subAccountService.getAllDtos(SessionContext.getCurrentCompany().getId());
            List<CompanyDto> companies = companyService.getallcompanyDto();

            javafx.application.Platform.runLater(() -> {
                accountSelectCombo.getItems().setAll(subaccounts);
                companySelectCombo.getItems().setAll(companies);
            });

        }).start();

        totalDebitLabelField.setText("0");
        totalCreditLabelField.setText("0");
    }

    // ================= ACTION COLUMN =================

    private void setupActionColumn() {

        actionColumnField.setCellFactory(col -> new TableCell<>() {

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

    // ================= ALERTS =================

    private void clearForm() {
        lines.clear();
        totalDebitLabelField.setText("0");
        totalCreditLabelField.setText("0");
        debitInputField.clear();
        creditInputField.clear();
        accountSelectCombo.getSelectionModel().clearSelection();
    }


    public void clear(ActionEvent event) {
        clearForm();
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
            Company company = companyService.getById(companySelectCombo.getValue().id());
            // Create JournalEntry entity
            JournalEntry journalEntry = new JournalEntry();
            journalEntry.setVoucherNo(voucherNoField.getText());
            journalEntry.setCompany(company);
            journalEntry.setCustomer(customerSelectCombo.getValue());
            journalEntry.setVoucherType(voucherSelectCombo.getValue());
            journalEntry.setDate(datePickerField.getValue());

            // Convert each JournalLineDto to JournalLine entity
            for (JournalLineDto dto : lines) {
                bikram.businessmanager.entity.account.JournalLine line = new bikram.businessmanager.entity.account.JournalLine();
                line.setSubAccount(subAccountService.getSubAccountByIdAndCompany(companySelectCombo.getValue().id(),dto.getSubAccount().getId())); // fetch entity
                line.setDebit(dto.getDebit());
                line.setCredit(dto.getCredit());
                line.setNote(dto.getNote());
                line.setJournalEntry(journalEntry);
                line.setCompany(company);// link line to parent entry

                journalEntry.getLine().add(line);
            }

            // Save using service
            journalEntryService.postJournalEntrywithoutem(journalEntry);

            AppAlert.sucess(noteTextField.getScene().getWindow(),"Voucher saved successfully!");
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            AppAlert.error(noteTextField.getScene().getWindow(),"Failed to save voucher: " + e.getMessage());
        }
    }

    @Override
    public void refresh() {
        loadInitialData();
    }

    public void on_back(ActionEvent event) {
        Navigator.back();
    }
}