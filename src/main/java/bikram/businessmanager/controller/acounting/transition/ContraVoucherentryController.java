package bikram.businessmanager.controller.acounting.transition;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.SubAccountDto;
import bikram.businessmanager.service.ContraEntryService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.service.SubAccountService;
import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.validation.ValidationSupport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ContraVoucherentryController implements Refreshable {
    private final SubAccountService subAccountService = ServiceProvider.services().getSubAccountService();
    private final ContraEntryService contraEntryService = ServiceProvider.services().getContraEntryService();
    @FXML private Button saveBtn;
    @FXML private Label dateLabel;
    @FXML private ComboBox<SubAccountDto> fromAccountBox;
    @FXML private Label fromBalanceLabel;
    @FXML private ComboBox<SubAccountDto> toAccountBox;
    @FXML private Label toBalanceLabel;
    @FXML private TextField amountField;
    @FXML private TextField referenceField;
    @FXML private TextArea narrationArea;
    ValidationSupport validationSupport = new ValidationSupport();

    public void initialize() {
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        setUpAccountComboBox();
        setUpBalanceLabel();
        loadAccountData();
    }
    private void setUpAccountComboBox() {

        fromAccountBox.setCellFactory(data -> createAccountCell());
        fromAccountBox.setButtonCell(createAccountCell());

        toAccountBox.setCellFactory(data -> createAccountCell());
        toAccountBox.setButtonCell(createAccountCell());
    }

    private ListCell<SubAccountDto> createAccountCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(SubAccountDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getSubAccountName());
            }
        };
    }

    private void setUpBalanceLabel(){
        fromAccountBox.setOnAction(e->{
            SubAccountDto selected = fromAccountBox.getValue();
            if (selected != null) {
                loadBalance(selected.getId(), fromBalanceLabel);
                validateAccounts();
            }
        });
        toAccountBox.setOnAction(e->{
            SubAccountDto selected = toAccountBox.getValue();
            if (selected != null) {
                loadBalance(selected.getId(), toBalanceLabel);
                validateAccounts();
            }
        });
    }

    private void loadAccountData(){
        Task<List<SubAccountDto>> task = new Task<List<SubAccountDto>>() {
            @Override
            protected List<SubAccountDto> call() throws Exception {
                return subAccountService.getAllContraAllowDto(SessionContext.getCurrentCompanyId());
            }
        };
        task.setOnSucceeded(e->{
            List<SubAccountDto> accountDtoList = task.getValue();
            fromAccountBox.getItems().clear();
            toAccountBox.getItems().clear();

            fromAccountBox.getItems().addAll(accountDtoList);
            toAccountBox.getItems().addAll(accountDtoList);
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    private void loadBalance(Long accountId,Label targetlabel){
        Long currentCompanyId = SessionContext.getCurrentCompanyId();
        Task<BigDecimal> task = new Task<BigDecimal>() {
            @Override
            protected BigDecimal call() throws Exception {
                return subAccountService.getBalance(currentCompanyId,accountId);
            }
        };
        task.setOnSucceeded(e->{
            BigDecimal balance = task.getValue();
            targetlabel.setText("Balance: Rs" + String.format("%,.2f", balance));
        });
        task.setOnFailed(e->{
            targetlabel.setText("Balance: Error");
            task.getException().printStackTrace();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    private void validateAccounts() {
        SubAccountDto from = fromAccountBox.getValue();
        SubAccountDto to = toAccountBox.getValue();

        if (from != null && to != null && from.getId().equals(to.getId())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("From and To account cannot be same.");
            alert.show();

            toAccountBox.setValue(null);
        }
    }
    private boolean validateAmount() {
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                AppAlert.warning(saveBtn.getScene().getWindow(),"Amount must be greater than zero");
                return false;
            }

            return true;

        } catch (Exception e) {
            AppAlert.warning(saveBtn.getScene().getWindow(),"Invalid amount");
            return false;
        }
    }


    @Override
    public void refresh() {
        loadAccountData();
    }

    @FXML private void cancelTask(ActionEvent event) {
        dateLabel.getScene().getWindow().hide();
    }

    @FXML private void saveContraEntry(ActionEvent event) {
        SubAccountDto fromAcc = fromAccountBox.getValue();
        SubAccountDto toAcc = toAccountBox.getValue();
        Long fromAccId = 0L;
        Long toAccId = 0L;
        BigDecimal amount = BigDecimal.ZERO;
        if (fromAcc  != null  && toAcc != null && amountField != null){
            fromAccId = fromAcc.getId();
            toAccId = toAcc.getId();
            amount = new BigDecimal(amountField.getText());
        }
        try {
            contraEntryService.postContraEntry(fromAccId,toAccId,amount);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to create contra entry");
        }

    }

    private void validatuion(){
        //validationSupport.registerValidator(fromAccountBox.getItems().)
    }

    @FXML private void on_back(ActionEvent event) {
        Navigator.back();
    }
}
