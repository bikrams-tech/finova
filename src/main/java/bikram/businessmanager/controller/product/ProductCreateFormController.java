package bikram.businessmanager.controller.product;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Unit;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.entity.inventory.ProductCategory;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.ProductService;
import bikram.businessmanager.service.ServiceContainer;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.Navigator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductCreateFormController {
    private final ProductService service = ServiceProvider.services().getProductService();
    private final CompanyService companyService = ServiceProvider.services().getCompanyService();

    private final ValidationSupport validationSupport = new ValidationSupport();
    @FXML
    private ScrollPane root;

    @FXML private TextField nameField;
    @FXML private TextField barcodeField;
    @FXML private ComboBox<ProductCategory> categoryCombo;
    @FXML private ComboBox<Unit> unitCombo;
    @FXML private TextField costPriceField;
    @FXML private TextField sellingPriceField;
    @FXML private CheckBox vatApplicableCheck;
    @FXML private TextField vatRateField;
    @FXML private ComboBox<Company> companyField;

    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Button cancelBtn;

    private Runnable onSaveCallback;

    @FXML
    public void initialize() {

        loadEnums();
        setupCompanyCombo();
        setupVatBehaviour();
        setupValidation();
        loadCompanies();
    }

    private void loadEnums() {

        categoryCombo.setItems(FXCollections.observableArrayList(ProductCategory.values()));
        unitCombo.setItems(FXCollections.observableArrayList(Unit.values()));

        categoryCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(ProductCategory item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDisplayName());
            }
        });

        categoryCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProductCategory item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDisplayName());
            }
        });
    }

    private void setupCompanyCombo() {

        companyField.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Company item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCompanyName());
            }
        });

        companyField.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Company item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCompanyName());
            }
        });
    }

    private void setupVatBehaviour() {

        vatRateField.disableProperty()
                .bind(vatApplicableCheck.selectedProperty().not());

        vatApplicableCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal) {
                vatRateField.clear();
            }

        });
    }

    private void setupValidation() {

        validationSupport.registerValidator(
                nameField,
                Validator.createEmptyValidator("Product name is required")
        );

        validationSupport.registerValidator(
                categoryCombo,
                Validator.createEmptyValidator("Category required")
        );

        validationSupport.registerValidator(
                unitCombo,
                Validator.createEmptyValidator("Unit required")
        );

        validationSupport.registerValidator(
                companyField,
                Validator.createEmptyValidator("Company required")
        );

        validationSupport.registerValidator(
                costPriceField,
                Validator.createPredicateValidator(
                        v -> isBigDecimal(v),
                        "Invalid cost price"
                )
        );

        validationSupport.registerValidator(
                sellingPriceField,
                Validator.createPredicateValidator(
                        v -> isBigDecimal(v),
                        "Invalid selling price"
                )
        );

        validationSupport.registerValidator(
                vatRateField,
                Validator.createPredicateValidator(
                        v -> !vatApplicableCheck.isSelected() || isBigDecimal(v),
                        "Invalid VAT rate"
                )
        );

        saveBtn.disableProperty()
                .bind(validationSupport.invalidProperty());
    }

    private boolean isBigDecimal(Object value) {

        try {
            new BigDecimal(value.toString());
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public void saveProduct() {

        if (validationSupport.isInvalid()) {
            return;
        }

        try {

            Product product = new Product();

            product.setName(nameField.getText().trim());
            product.setBarcode(barcodeField.getText().trim());
            product.setCategory(categoryCombo.getValue());
            product.setUnit(unitCombo.getValue());
            product.setCostPrice(new BigDecimal(costPriceField.getText()));
            product.setSellingPrice(new BigDecimal(sellingPriceField.getText()));

            product.setVatApplicable(vatApplicableCheck.isSelected());

            product.setVatRate(
                    vatApplicableCheck.isSelected()
                            ? new BigDecimal(vatRateField.getText())
                            : BigDecimal.ZERO
            );

            product.setCompany(companyField.getValue());
            product.setCreatedAt(LocalDateTime.now());

            service.create(product);

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            AppAlert.sucess(saveBtn.getScene().getWindow(),
                    "Product saved successfully");

            clearForm();

        } catch (Exception e) {

            e.printStackTrace();

            AppAlert.error(saveBtn.getScene().getWindow(),
                    "Failed to save product");

        }
    }

    private void loadCompanies() {

        new Thread(() -> {

            List<Company> list = companyService.getAll();

            Platform.runLater(() ->
                    companyField.setItems(FXCollections.observableArrayList(list))
            );

        }).start();
    }

    public void clearForm() {

        nameField.clear();
        barcodeField.clear();
        categoryCombo.getSelectionModel().clearSelection();
        unitCombo.getSelectionModel().clearSelection();
        costPriceField.clear();
        sellingPriceField.clear();
        vatApplicableCheck.setSelected(false);
        vatRateField.clear();
        companyField.getSelectionModel().clearSelection();
    }

    public void cancelTask() {

        Navigator.closeOverlay(root);

    }



    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

}