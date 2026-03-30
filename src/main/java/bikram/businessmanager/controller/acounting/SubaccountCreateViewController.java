package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.entity.account.AccountCategory;
import bikram.businessmanager.entity.account.JournalLine;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.service.AccountService;
import bikram.businessmanager.service.ServiceContainer;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.service.SubAccountService;
import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

public class SubaccountCreateViewController implements Refreshable {
    private final AccountService service = ServiceProvider.services().getAccountService();
    private final SubAccountService subAccountService = ServiceProvider.services().getSubAccountService();
    @FXML private ComboBox<AccountCategory> account_catagery_combo_box;
    @FXML private TextField codefield;

    @FXML private TextField subAccountNameField;
    @FXML private ComboBox<Account> mainAccountSelectComboBox;
    @FXML private Button saveButton;
    @FXML
    public void initialize() {
        account_catagery_combo_box.getItems().setAll(AccountCategory.values());

        saveButton.disableProperty().bind(
                subAccountNameField.textProperty().isEmpty()
                        .or(mainAccountSelectComboBox.valueProperty().isNull())
        );

        mainAccountSelectComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                setText(empty || account == null ? null : account.getName());
            }
        });

        mainAccountSelectComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                setText(empty || account == null ? null : account.getName());
            }
        });

        loadAccounts();
    }

    private void loadAccounts() {
        new Thread(()->{
            List<Account> accounts = service.getAllByCompany(SessionContext.getCurrentCompanyId());
            javafx.application.Platform.runLater(()->{
                mainAccountSelectComboBox.getItems().clear();
                mainAccountSelectComboBox.getItems().setAll(accounts);
            });
        }).start();
    }


    @FXML
    private void saveSubAccount(ActionEvent event) {

        String name = subAccountNameField.getText();
        Account selectedAccount = mainAccountSelectComboBox.getValue();
        AccountCategory category = account_catagery_combo_box.getValue();

        if (name == null || name.isBlank()) {
            AppAlert.error(saveButton.getScene().getWindow(),"Sub account name is required.");
            return;
        }

        if (selectedAccount == null) {
            AppAlert.error(saveButton.getScene().getWindow(),"Please select main account.");
            return;
        }
        if (category ==null){
            AppAlert.error(saveButton.getScene().getWindow(),"please select account catagery.");
        }
        if (subAccountService.existsByNameAndAccount(name, selectedAccount)) {
            AppAlert.error(saveButton.getScene().getWindow(),"Sub account already exists.");
            return;
        }

        SubAccount subAccount = new SubAccount();
        subAccount.setName(name.trim());
        subAccount.setAccount(selectedAccount);
        subAccount.setCode(codefield.getText());
        subAccount.setAccountCategory(category);

        subAccountService.create(subAccount);
        AccountingViewController.refreshActive();
        AppAlert.sucess(saveButton.getScene().getWindow(),"SubAccount created sucessfully");
        clearForm();
    }
    private void clearForm() {
        subAccountNameField.clear();
        mainAccountSelectComboBox.getSelectionModel().clearSelection();
        codefield.clear();
    }

    @Override
    public void refresh() {
        loadAccounts();
    }

    public void on_back(ActionEvent event) {
        Navigator.back();
    }
}
