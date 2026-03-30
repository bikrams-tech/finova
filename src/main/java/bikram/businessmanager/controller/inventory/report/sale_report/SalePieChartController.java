package bikram.businessmanager.controller.inventory.report.sale_report;

import bikram.businessmanager.dto.sale.SaleReportDto;
import bikram.businessmanager.service.SalesService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.SessionContext;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;

import java.util.List;

public class SalePieChartController {
    @FXML private BarChart<String , Number> barchart_by_time;
    @FXML private LineChart<String ,Number> barchart_by_day;
    @FXML private LineChart<String ,Number> barchart_by_month;
    @FXML private PieChart piechart_by_productCategory;
    @FXML private BarChart<String ,Number> top_product_bar_chart;
    @FXML private PieChart piechart_by_paymentMethod;
    private final SalesService service = ServiceProvider.services().getSalesService();

    private Long companyId = SessionContext.getCurrentCompanyId();
    private List<SaleReportDto> saleReportDtos;

    private void initialize(){
        loadReportByProduct();
    }
    private void setUpBarChartByProduct(){

    }
    public void loadReportByProduct(){
        Task<List<SaleReportDto>> task = new Task<List<SaleReportDto>>() {
            @Override
            protected List<SaleReportDto> call() throws Exception {
                return service.getSaleReportByProduct(companyId);
            }
        };
        task.setOnSucceeded(e->{
            this.saleReportDtos = task.getValue();
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace();
            throw new RuntimeException("failed to load sales report dto for piechart");
        });
    }

}
