package bikram.businessmanager.controller.company;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.dto.CompanyDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CompanySelectViewController implements Refreshable {

    private CompanyService service;

    @FXML private ListView<CompanyDto> companyListView;
    @FXML private TextField companyinputField;
    @FXML private ComboBox<CompanyDto> companyComboBox;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {

        service = ServiceProvider.services().getCompanyService();

        setupListView();
        setupComboBox();
        setupSelectionSync();

        loadAllCompanies();
    }

    private void setupListView() {

        companyListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CompanyDto company, boolean empty) {
                super.updateItem(company, empty);
                setText(empty || company == null ? null : company.name());
            }
        });
    }

    private void setupComboBox() {

        companyComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(CompanyDto company, boolean empty) {
                super.updateItem(company, empty);
                setText(empty || company == null ? null : company.name());
            }
        });

        companyComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CompanyDto company, boolean empty) {
                super.updateItem(company, empty);
                setText(empty || company == null ? null : company.name());
            }
        });
    }

    private void setupSelectionSync() {

        companyListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        companyComboBox.setValue(newVal);
                    }
                });
    }

    private void loadAllCompanies() {

        Task<List<CompanyDto>> task = new Task<>() {
            @Override
            protected List<CompanyDto> call() {
                return service.getallcompanyDto();
            }
        };

        showLoading(true);

        task.setOnSucceeded(e -> {

            List<CompanyDto> data = task.getValue();

            companyListView.getItems().setAll(data);
            companyComboBox.getItems().setAll(data);

            showLoading(false);
        });

        task.setOnFailed(e -> {
            showLoading(false);
            showError("Failed to load companies.");
        });

        new Thread(task).start();
    }

    public void searchCompany(ActionEvent actionEvent) {

        String keyword = companyinputField.getText();

        Task<List<CompanyDto>> task = new Task<>() {
            @Override
            protected List<CompanyDto> call() {
                return service.getAllCompanyDtoByName(keyword);
            }
        };

        showLoading(true);

        task.setOnSucceeded(e -> {

            List<CompanyDto> result = task.getValue();

            companyListView.getItems().setAll(result);
            companyComboBox.getItems().setAll(result);

            showLoading(false);
        });

        task.setOnFailed(e -> {
            showLoading(false);
            showError("Search failed.");
        });

        new Thread(task).start();
    }

    public void setCompany(ActionEvent actionEvent) {

        CompanyDto selected = companyListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            selected = companyComboBox.getValue();
        }

        if (selected == null) {
            showError("Please select a company.");
            return;
        }

        final CompanyDto finalSelected = selected;

        Task<Company> task = new Task<>() {
            @Override
            protected Company call() {
                return service.getById(finalSelected.id());
            }
        };

        showLoading(true);

        task.setOnSucceeded(e -> {

            Company company = task.getValue();

            SessionContext.setCurrentCompany(company);

            showLoading(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Company selected successfully!");
            alert.showAndWait();

            Navigator.navigate("/bikram/businessmanager/dashboard/dashboardview.fxml");
        });

        task.setOnFailed(e -> {
            showLoading(false);
            showError("Failed to set company.");
        });

        new Thread(task).start();
    }

    private void showLoading(boolean show) {

        loadingIndicator.setVisible(show);
        loadingIndicator.setManaged(show);
    }

    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public void onCreateNewCompany(ActionEvent event) {
        Navigator.showOverlay("/bikram/businessmanager/form/companyregisterform.fxml");
    }

    @Override
    public void refresh() {
        loadAllCompanies();
    }
}