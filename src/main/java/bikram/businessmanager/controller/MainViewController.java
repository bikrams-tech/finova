package bikram.businessmanager.controller;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import bikram.businessmanager.utils.SessionPersistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class MainViewController {
    private final CompanyService companyService = ServiceProvider.services().getCompanyService();
    @FXML private Button back_btn;

    @FXML private MenuBar mainMenuBar;
    @FXML private MenuItem traningMenuItem;
    @FXML private MenuItem officeMeniItem;
    @FXML private MenuItem counterMenuItem;
    @FXML private MenuItem addProductMenuItem;
    @FXML private StackPane mainContainer;
    @FXML private BorderPane borderPane;


    @FXML
    public void initialize() {

        borderPane.setRight(null);
    }

    public void changeToTraningMode(ActionEvent actionEvent) {
    }


    public void changeToOfficeMode(ActionEvent actionEvent) {
    }

    public void changeToCounterMode(ActionEvent actionEvent) {
    }

    public void addProduct(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/form/productcreateform.fxml");
        //Navigator.loadView("/bikram/businessmanager/form/productcreateform.fxml");
    }

    public void goToSaveCompanyForm(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/form/companyregisterform.fxml");
    }

    public void showCompanies(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/table/companyTable.fxml");
    }

    public void showProductTable(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/table/ProductTable.fxml");
    }

    public void openEmployeeCreateForm(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/form/EmployeeForm.fxml");
    }

    public void openDashboard(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/dashboard/dashboardview.fxml");
    }

    public void on_stock_view(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/table/stockview.fxml");
    }

    public void on_view_stock_transition(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/table/stock_transition_view.fxml");
    }
    public StackPane getMainContainer(){
        return mainContainer;
    }

    public void on_office_mode(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/dashboard/dashboardview.fxml");
    }

    public void on_counter_mode(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/dashboard/counter_view.fxml");
    }

    @FXML private void on_login(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/ui/login_view.fxml");
    }

    public void on_back(ActionEvent actionEvent) {
        Navigator.back();
    }
}
