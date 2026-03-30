package bikram.businessmanager.controller.company;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CompanyTableController implements Refreshable {

    private final CompanyService service = ServiceProvider.services().getCompanyService();

    @FXML private Label titleLabel;
    @FXML private HBox hBox;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private TableView<Company> companyTableView;

    @FXML private TableColumn<Company, Long> idColum;
    @FXML private TableColumn<Company, String> nameColum;
    @FXML private TableColumn<Company, String> panNumbercolum;
    @FXML private TableColumn<Company, String> vatNumberColum;
    @FXML private TableColumn<Company, String> registrationNumberColum;
    @FXML private TableColumn<Company, String> addressColum;
    @FXML private TableColumn<Company, String> phoneColum;
    @FXML private TableColumn<Company, String> emailColum;
    @FXML private TableColumn<Company, String> fiscalyearColum;
    @FXML private TableColumn<Company, Boolean> isActive;
    @FXML private TableColumn<Company, Long> branchIdColum;
    @FXML private TableColumn<Company, String> createdAtcolum;

    private final ObservableList<Company> masterList =
            FXCollections.observableArrayList();

    private FilteredList<Company> filteredList;

    @FXML
    public void initialize() {

        companyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idColum.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColum.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        panNumbercolum.setCellValueFactory(new PropertyValueFactory<>("panNumber"));
        vatNumberColum.setCellValueFactory(new PropertyValueFactory<>("vatNumber"));
        registrationNumberColum.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        addressColum.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColum.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColum.setCellValueFactory(new PropertyValueFactory<>("email"));
        fiscalyearColum.setCellValueFactory(new PropertyValueFactory<>("fiscalYearStart"));
        createdAtcolum.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        isActive.setCellValueFactory(new PropertyValueFactory<>("active"));
        branchIdColum.setCellValueFactory(new PropertyValueFactory<>("branchId"));

        setupSearchFilter();
        setUpRowClick();   // 🔥 ADD THIS LINE
        loadCompanies();
    }

    // ==============================
    // Background Loading
    // ==============================

    private void loadCompanies() {

        ProgressIndicator loader = new ProgressIndicator();
        companyTableView.setPlaceholder(loader);

        Task<ObservableList<Company>> task = new Task<>() {
            @Override
            protected ObservableList<Company> call() {
                return FXCollections.observableArrayList(
                        service.getAll()
                );
            }
        };

        task.setOnSucceeded(event -> {
            masterList.setAll(task.getValue());
            companyTableView.setPlaceholder(new Label("No Companies Found"));
        });

        task.setOnFailed(event -> {
            companyTableView.setPlaceholder(new Label("Failed to load data"));
            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);   // important
        thread.start();
    }

    // ==============================
    // Real-Time Search
    // ==============================

    private void setupSearchFilter() {

        filteredList = new FilteredList<>(masterList, b -> true);
        companyTableView.setItems(filteredList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(company -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lower = newValue.toLowerCase();

                return company.getCompanyName().toLowerCase().contains(lower)
                        || company.getPanNumber().toLowerCase().contains(lower)
                        || company.getEmail().toLowerCase().contains(lower);
            });
        });
    }

    @FXML
    private void searchCompany() {
        // handled automatically by listener
    }

    @FXML
    private void searchByBtn() {
        // optional if you want button-triggered search
    }

    //popup for each row
    private void setUpRowClick(){
        companyTableView.setRowFactory(tv ->{
            TableRow<Company> row = new TableRow<>();

            row.setOnMouseClicked(event ->{
                if (!row.isEmpty() && event.getClickCount() ==2){
                    Company selectedCompany = row.getItem();
                    showCompanyOptionsDialog(selectedCompany);
                }
            });
            return row;
        });
    }

    private void showCompanyOptionsDialog(Company company) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Company Options");

        ButtonType editBtn = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        ButtonType viewBtn = new ButtonType("View Details", ButtonBar.ButtonData.APPLY);
        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.OTHER);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(editBtn, viewBtn, deleteBtn, cancelBtn);

        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");

        Label name = new Label("Company: " + company.getCompanyName());
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label pan = new Label("PAN: " + company.getPanNumber());
        Label email = new Label("Email: " + company.getEmail());

        content.getChildren().addAll(name, pan, email);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {

            if (button == editBtn) {
                openEditView(company);
            }

            if (button == viewBtn) {
                openDetailView(company);
            }

            if (button == deleteBtn) {
                confirmDelete(company);
            }

            return null;
        });

        dialog.showAndWait();
    }

    private void openDetailView(Company company) {
        try {
            CompanyDetailController controller = Navigator.navigate("/bikram/businessmanager/detailview/CompanyDetailView.fxml");
            controller.setCompany(company);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void confirmDelete(Company company) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Company");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.deleteById(company.getId());
                loadCompanies();
            }
        });
    }

    private void openEditView(Company company) {
        try {
            CompanyEditController controller = Navigator.navigate("/bikram/businessmanager/editview/CompanyEditView.fxml");
            controller.setCompany(company);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void refresh() {
        loadCompanies();
    }
}