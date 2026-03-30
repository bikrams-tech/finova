package bikram.businessmanager.controller.product;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Unit;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.entity.inventory.ProductCategory;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.ProductService;

import bikram.businessmanager.service.ServiceContainer;
import bikram.businessmanager.service.ServiceProvider;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.math.BigDecimal;
import java.util.List;

public class ProductEditController {

    private final ProductService service = ServiceProvider.services().getProductService();
    private final CompanyService companyService = ServiceProvider.services().getCompanyService();

    private final ValidationSupport validationSupport = new ValidationSupport();
    private Product product;
    private Runnable onSaveCallback;

    @FXML private TextField nameField;
    @FXML private TextField barcodeField;
    @FXML private ComboBox<ProductCategory> categoryCombo;
    @FXML private ComboBox<Unit> unitCombo;
    @FXML private ComboBox<Company> companyComboBox;

    @FXML private TextField costPriceField;
    @FXML private TextField sellingPriceField;
    @FXML private CheckBox vatApplicableCheck;
    @FXML private TextField vatRateField;

    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Button cancelBtn;

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

        companyComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Company item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCompanyName());
            }
        });

        companyComboBox.setButtonCell(new ListCell<>() {
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
                Validator.createEmptyValidator("Product name required")
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
                companyComboBox,
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

    private void loadCompanies() {

        new Thread(() -> {

            List<Company> companies = companyService.getAll();

            Platform.runLater(() ->
                    companyComboBox.setItems(
                            FXCollections.observableArrayList(companies)
                    )
            );

        }).start();
    }

    public void setProduct(Product product) {

        this.product = product;

        nameField.setText(product.getName());
        barcodeField.setText(product.getBarcode());
        categoryCombo.setValue(product.getCategory());
        unitCombo.setValue(product.getUnit());
        companyComboBox.setValue(product.getCompany());

        costPriceField.setText(product.getCostPrice().toPlainString());
        sellingPriceField.setText(product.getSellingPrice().toPlainString());

        vatApplicableCheck.setSelected(product.isVatApplicable());

        if (product.getVatRate() != null) {
            vatRateField.setText(product.getVatRate().toPlainString());
        }

    }

    @FXML
    private void saveProduct() {

        if (validationSupport.isInvalid()) {
            return;
        }

        try {

            product.setName(nameField.getText().trim());
            product.setBarcode(barcodeField.getText().trim());
            product.setCategory(categoryCombo.getValue());
            product.setUnit(unitCombo.getValue());
            product.setCompany(companyComboBox.getValue());

            product.setCostPrice(new BigDecimal(costPriceField.getText()));
            product.setSellingPrice(new BigDecimal(sellingPriceField.getText()));

            product.setVatApplicable(vatApplicableCheck.isSelected());

            product.setVatRate(
                    vatApplicableCheck.isSelected()
                            ? new BigDecimal(vatRateField.getText())
                            : BigDecimal.ZERO
            );

            service.update(product);

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update product.");
            alert.showAndWait();

        }
    }

    @FXML
    private void clearForm() {

        if (product != null) {
            setProduct(product);
        }

    }

    @FXML
    private void cancelTask() {

        ((Stage) nameField.getScene().getWindow()).close();

    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

}