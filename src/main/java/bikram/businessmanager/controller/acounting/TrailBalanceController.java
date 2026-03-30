package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.TrailBalanceRow;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.service.TrailBalanceService;
import bikram.businessmanager.utils.SessionContext;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TrailBalanceController implements Refreshable {
    private final TrailBalanceService trailBalanceService = ServiceProvider.services().getTrailBalanceService();

    @FXML private TableView<TrailBalanceRow> table_view;
    @FXML private TableColumn<TrailBalanceRow, LocalDate> date_column;
    @FXML private TableColumn<TrailBalanceRow,String> title_column;
    @FXML private TableColumn<TrailBalanceRow,String> invoice_column;
    @FXML private TableColumn<TrailBalanceRow, BigDecimal> debit_column;
    @FXML private TableColumn<TrailBalanceRow,BigDecimal> credit_column;
    @FXML private Label total_debit;
    @FXML private Label total_credit;

    @FXML private void initialize(){
        setUpTable();
        loadData();
    }

    private void setUpTable(){
        table_view.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        date_column.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().date()));
        title_column.setCellValueFactory(data->
                new SimpleObjectProperty<>(data.getValue().account()));
        invoice_column.setCellValueFactory(data->
                new SimpleObjectProperty<>(data.getValue().invoiceNo()));
        debit_column.setCellValueFactory(data->
                new SimpleObjectProperty<>(data.getValue().debit()));
        credit_column.setCellValueFactory(data->
                new SimpleObjectProperty<>(data.getValue().credit()));
    }

    private void loadData(){
        Task<List<TrailBalanceRow>> task = new Task<List<TrailBalanceRow>>() {
            @Override
            protected List<TrailBalanceRow> call() throws Exception {
                return trailBalanceService.getTrailBalanceRow(SessionContext.getCurrentCompanyId());
            }
        };
        task.setOnSucceeded(e->{
            List<TrailBalanceRow> rows =  task.getValue();
            table_view.getItems().setAll(rows);
            calculateTotal(rows);
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace();
            throw new RuntimeException("failed to load trail balance");
        });

        new Thread(task).start();
    }
    private void calculateTotal(List<TrailBalanceRow> rows){
        BigDecimal total_dr = BigDecimal.ZERO;
        BigDecimal total_cr = BigDecimal.ZERO;
        for (TrailBalanceRow row : rows){

            if ((row.debit()) != null) {
                total_dr = total_dr.add(row.debit());
            }
            if ((row.credit() != null)) {
                total_cr = total_cr.add(row.credit());
            }
        }
        total_debit.setText(total_dr.toString());
        total_credit.setText(total_cr.toString());

    }

    @Override
    public void refresh() {
        loadData();
    }
}
