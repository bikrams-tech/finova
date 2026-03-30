package bikram.businessmanager.controller.dashbord;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardviewController {


    @FXML private StackPane contentArea;;
    @FXML private BorderPane dashboardBoarderpane;
    @FXML private Label timeLabel;
    @FXML private Label companyLabel;


    @FXML private void initialize() {
        Company company = SessionContext.getCurrentCompany();
        if (company != null) {
            companyLabel.setText("Company: " + company.getCompanyName());
        }
    }

    private void loadView(String fxml) {
        try {
            Parent view = FXMLLoader.load(
                    getClass().getResource("/" + fxml)
            );

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void switchCompany(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/view/CompanySelectionView.fxml");
    }

    @FXML private void LogutUser() {
    }

    @FXML private void openDashboardPannel() {
        loadView("bikram/businessmanager/dashboard/mainDashboardview.fxml");
    }

    @FXML private void openProductpannel() {
        Navigator.navigate("/bikram/businessmanager/table/ProductTable.fxml");
    }

    @FXML private void openEmployeePanel() {
    }

    @FXML private void openSalesPannel() {
        Navigator.navigate("/bikram/businessmanager/accountingView/report/sale_report/sales_report.fxml");
    }

    @FXML private void openAccountingPannel() {
        Navigator.navigate("/bikram/businessmanager/accountingView/accountingView.fxml");
    }

    @FXML private void openAnalysisPanel() {
    }

    @FXML private void on_back(ActionEvent event) {
        Navigator.back();
    }
}
