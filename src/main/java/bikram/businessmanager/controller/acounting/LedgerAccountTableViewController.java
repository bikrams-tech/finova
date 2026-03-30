package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.service.AccountService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.shape.SVGPath;

import java.util.List;

public class LedgerAccountTableViewController implements Refreshable {
    @FXML private TableColumn<Account,String> codeColumn;
    @FXML private TableView<Account> ledgerAccountTableView;
    @FXML private TableColumn<Account ,Long> accountIdColum;
    @FXML private TableColumn<Account ,String> accountNameColum;
    @FXML private TableColumn<Account ,String> accountTypeColumn;

    private final AccountService accountService = ServiceProvider.services().getAccountService();
    @FXML
    public void initialize() {
        setupColumns();
        loadSubAccounts();
    }
    private void setupColumns() {
        ledgerAccountTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        accountIdColum.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));

        accountNameColum.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getName()));

        accountTypeColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAccountType().toString()));

        codeColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCode()));
    }


    public void loadSubAccounts() {
        Task<List<Account>> task = new Task<List<Account>>() {
            @Override
            protected List<Account> call() throws Exception {
                return accountService.getAllByCompany(SessionContext.getCurrentCompanyId());
            }
        };
        task.setOnSucceeded(e ->{
            ledgerAccountTableView.setItems(
                    FXCollections.observableArrayList(task.getValue())
            );
        });
        new Thread(task).start();
    }

    @Override
    public void refresh() {

        loadSubAccounts();
    }

    public void refressData(ActionEvent event) {
        loadSubAccounts();
    }

    @FXML private void on_back(ActionEvent event) {
        Navigator.back();
    }

}
