package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.entity.account.AccountCategory;
import bikram.businessmanager.entity.account.AccountType;
import bikram.businessmanager.service.AccountService;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.Navigator;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;

import java.util.List;

public class AccountCreateFormController implements Refreshable {

    private final CompanyService companyService = ServiceProvider.services().getCompanyService();
    private final AccountService accountService = ServiceProvider.services().getAccountService();

    @FXML private ComboBox<Company> companySelectComboBox;
    @FXML private TextField codeField;
    @FXML private TextField accountNameField;
    @FXML private ComboBox<AccountType> accountTypeSelectCombo;


    public void initialize() {
        accountTypeSelectCombo.getItems().setAll(AccountType.values());
        loadCompanies();
    }

    public void setUpCompanyCombo(){
        companySelectComboBox.setCellFactory(p -> new ListCell<>(){
            @Override
            protected void updateItem(Company item,boolean empty){
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCompanyName());
            }
        });
        companySelectComboBox.setButtonCell(companySelectComboBox.getCellFactory().call(null));
    }



    @FXML private void saveAccount(ActionEvent event) {
        if (accountNameField.getText() == null || accountNameField.getText().trim().isEmpty()) {
            throw new IllegalStateException("Account name is required");
        }

        Account account = new Account();
        account.setName(accountNameField.getText());
        account.setAccountType(accountTypeSelectCombo.getValue());
        account.setCompany(companySelectComboBox.getValue());
        account.setCode(codeField.getText());
        accountService.create(account);
        AccountingViewController.refreshActive();
        AppAlert.sucess(accountNameField.getScene().getWindow(),"account create sucessfully");

        clearForm();
    }
    private void loadCompanies() {
        Task<List<Company>> task = new Task<>() {
            @Override
            protected List<Company> call() {
                return companyService.getAll();
            }
        };

        task.setOnSucceeded(e ->
                companySelectComboBox.getItems().setAll(task.getValue())
        );

        new Thread(task).start();
    }
    private void clearForm() {
        accountNameField.clear();
        accountTypeSelectCombo.getSelectionModel().clearSelection();
        companySelectComboBox.getSelectionModel().clearSelection();
        codeField.clear();
    }
    @Override
    public void refresh() {
        loadCompanies();
    }

    public void on_back(ActionEvent event) {
        Navigator.back();
    }
}
