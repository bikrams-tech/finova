package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.ProfitLossReport;
import bikram.businessmanager.dto.ProfitandLossDto;
import bikram.businessmanager.service.JournalEntryService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProfitLossController implements Refreshable {

    @FXML
    private TableView<ProfitandLossDto> table;

    @FXML
    private TableColumn<ProfitandLossDto, String> accountCol;

    @FXML
    private TableColumn<ProfitandLossDto, String> typeCol;

    @FXML
    private TableColumn<ProfitandLossDto, BigDecimal> amountCol;

    @FXML
    private Label incomeLabel;

    @FXML
    private Label expenseLabel;

    @FXML
    private Label profitLabel;

    private final JournalEntryService service =
            ServiceProvider.services().getJournalEntryService();

    @FXML
    public void initialize() {

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        accountCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().accountName())
        );

        typeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().accountType().name())
        );

        amountCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().amount())
        );

        loadData();
    }

    private void loadData() {

        ProfitLossReport report = service.getProfitAndLoss(
                1L,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );

        table.getItems().setAll(report.rows());

        incomeLabel.setText(report.totalIncome().toString());
        expenseLabel.setText(report.totalExpense().toString());
        profitLabel.setText(report.netProfit().toString());
    }

    @Override
    public void refresh() {
        loadData();
    }

    @FXML
    private void on_back(ActionEvent event) {
        Navigator.back();
    }
}