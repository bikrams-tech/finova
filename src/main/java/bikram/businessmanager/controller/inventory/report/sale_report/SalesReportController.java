package bikram.businessmanager.controller.inventory.report.sale_report;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.sale.SaleTableDto;
import bikram.businessmanager.service.SalesService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.SessionContext;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SalesReportController implements Refreshable {
    @FXML private TableColumn<SaleTableDto,Long> id_colum;
    @FXML private TableColumn<SaleTableDto,String> invoice_colum;
    @FXML private TableColumn<SaleTableDto,String> customer_name_colum;
    @FXML private TableColumn<SaleTableDto,String> payment_method_culom;
    @FXML private TableColumn<SaleTableDto,String> total_item_colum;
    @FXML private TableColumn<SaleTableDto, BigDecimal> total_amount_colum;
    @FXML private TableColumn<SaleTableDto,String> date_colum;
    @FXML private ScrollPane scroll_pane;
    @FXML private VBox vbox_under_scroll_pane;
    @FXML private TableView<SaleTableDto> sales_table_view;
    @FXML private Label date_label;

    private final SalesService service = ServiceProvider.services().getSalesService();

    @FXML private void initialize(){
        date_label.setText(LocalDate.now().toString());
        setupTable();
        loadData();
    }

    private void setupTable(){
        scroll_pane.setFitToWidth(true);
        sales_table_view.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        id_colum.setCellValueFactory(data-> new SimpleObjectProperty<>(data.getValue().id()));
        invoice_colum.setCellValueFactory(data-> new SimpleObjectProperty<>(data.getValue().invoiceNo()));
        customer_name_colum.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().customerName()));
        payment_method_culom.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().paymentMethod().toString()));
        total_item_colum.setCellValueFactory(data-> new SimpleObjectProperty<>(String.valueOf(data.getValue().total_item())));
        total_amount_colum.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().grand_total()));
        date_colum.setCellValueFactory(data ->new SimpleObjectProperty<>(data.getValue().saleDate().toString()));
    }

    private void loadData(){
        Task<List<SaleTableDto>> task = new Task<List<SaleTableDto>>() {
            @Override
            protected List<SaleTableDto> call() throws Exception {
                return service.getSaleTableDto(SessionContext.getCurrentCompanyId());
            }
        };
        task.setOnSucceeded(e->{
            sales_table_view.getItems().clear();
            sales_table_view.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace();
            throw new RuntimeException("failed to load saleTableDto");
        });

        new Thread(task).start();
    }

    @Override
    public void refresh() {
        loadData();
    }
}
