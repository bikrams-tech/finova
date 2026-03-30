package bikram.businessmanager.controller.dashbord;

import bikram.businessmanager.utils.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class MaindashboardviewController {
    @FXML private ScrollPane root;
    @FXML private Label totalProductLabel;
    @FXML private Label totalEmployeeLabel;
    @FXML private Label totalSalesLabel;
    @FXML private Label TotalIncomeLabel;
    @FXML private BarChart barChartForSales;
    @FXML private LineChart lineChartForSales;


    Long companyId = SessionContext.getCurrentCompany().getId();


    @FXML
    public void initialize() {
        barChartForSales.setAnimated(true);
        lineChartForSales.setAnimated(true);

        barChartForSales.setLegendVisible(false);
        lineChartForSales.setLegendVisible(true);
        loadBarChart();
        loadLineChart();
    }


    private void loadBarChart() {

        barChartForSales.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Weekly Sales");

        // Example static data (replace with DB later)
        series.getData().add(new XYChart.Data<>("Mon", 12000));
        series.getData().add(new XYChart.Data<>("Tue", 8000));
        series.getData().add(new XYChart.Data<>("Wed", 15000));
        series.getData().add(new XYChart.Data<>("Thu", 7000));
        series.getData().add(new XYChart.Data<>("Fri", 20000));
        series.getData().add(new XYChart.Data<>("Sat", 25000));
        series.getData().add(new XYChart.Data<>("Sun", 10000));

        barChartForSales.getData().add(series);
    }

    private void loadLineChart() {

        lineChartForSales.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        series.getData().add(new XYChart.Data<>("Jan", 120000));
        series.getData().add(new XYChart.Data<>("Feb", 140000));
        series.getData().add(new XYChart.Data<>("Mar", 110000));
        series.getData().add(new XYChart.Data<>("Apr", 160000));
        series.getData().add(new XYChart.Data<>("May", 180000));
        series.getData().add(new XYChart.Data<>("Jun", 210000));

        lineChartForSales.getData().add(series);
    }
}
