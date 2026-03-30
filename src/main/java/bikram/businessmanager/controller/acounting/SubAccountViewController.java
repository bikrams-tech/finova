package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.SubAccountDtoWithAcount;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.entity.account.AccountCategory;
import bikram.businessmanager.entity.account.AccountNode;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.service.AccountService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.service.SubAccountService;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class SubAccountViewController implements Refreshable {

    @FXML private TableView<SubAccountDtoWithAcount> table_view;
    @FXML private TableColumn<SubAccountDtoWithAcount,String> sub_account_name;
    @FXML private TableColumn<SubAccountDtoWithAcount,String> main_account_name;
    @FXML private TableColumn<SubAccountDtoWithAcount, AccountCategory> account_catagiry;
    @FXML private TableColumn<SubAccountDtoWithAcount,String> code;

    private final SubAccountService subAccountService = ServiceProvider.services().getSubAccountService();

    @FXML private void initialize() {
        table_view.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        loadData();
        setUpTable();
    }

    private void setUpTable(){
        sub_account_name.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().subAccountName()));
        main_account_name.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().mainAccountName()));
        account_catagiry.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().accountCategory()));
        code.setCellValueFactory(data->
                new SimpleObjectProperty<>(data.getValue().code()));
    }




    @Override
    public void refresh() {
        loadData();
    }

    private void loadData(){
        Task<List<SubAccountDtoWithAcount>> task = new Task<List<SubAccountDtoWithAcount>>() {
            @Override
            protected List<SubAccountDtoWithAcount> call() throws Exception {
                return subAccountService.getallSubAccountDtoWithAccount(SessionContext.getCurrentCompanyId());
            }
        };
        task.setOnSucceeded(e->{
            table_view.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace();
            throw new RuntimeException("failed to load subaccount dto");
        });
        new Thread(task).start();
    }
}