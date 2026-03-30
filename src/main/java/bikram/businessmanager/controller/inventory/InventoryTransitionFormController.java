package bikram.businessmanager.controller.inventory;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.*;
import bikram.businessmanager.entity.inventory.Inventory;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.entity.inventory.TransactionType;
import bikram.businessmanager.service.*;
import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public class InventoryTransitionFormController implements Refreshable {
    @FXML private VBox mainVbox;
    @FXML private Button saveBtn;
    @FXML private VBox hboxField;
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label transitionLabel;
    @FXML private TextField qtyField;
    private ComboBox<PaymentMethod> paymentMethodCombo;
    private Product product;
    private Label customerLabel;
    private ComboBox<Customer> customerComboBox;
    private ComboBox<Supplier> supplierComboBox;
    private TransactionType transactionType;
    // services
    private InventoryService inventoryService = ServiceProvider.services().getInventoryService();
    private InventorytranstionService inventorytranstionService = ServiceProvider.services().getInventorytranstionService();
    private SupplierService supplierService = ServiceProvider.services().getSupplierService();
    private CustomerService customerService = ServiceProvider.services().getCustomerService();
    private ValidationSupport validationSupport = new ValidationSupport();
    private PurchaseService purchaseService = ServiceProvider.services().getPurchaseService();
    private SalesService salesService = ServiceProvider.services().getSalesService();

    private Customer customer;
    private Supplier supplier;
    private Company current_company;
    private Long currentCompanyId;
    private BigDecimal quantity;
    private PaymentMethod paymentMethod;

    @FXML
    public void initialize() {
        setup_validation();
        saveBtn.disableProperty().bind(validationSupport.invalidProperty());
    }

    @FXML
    private void onSaveClick(ActionEvent event) {

        try {
            /// /
            this.customer = customerComboBox != null ? customerComboBox.getValue() : null;
            this.supplier = supplierComboBox != null ?  supplierComboBox.getValue() :null;
            this.current_company = SessionContext.getCurrentCompany();
            this.quantity = new BigDecimal(qtyField.getText());
            this.paymentMethod=paymentMethodCombo != null ? paymentMethodCombo.getValue() :null;
            this.currentCompanyId = SessionContext.getCurrentCompanyId();
            handle_save();
            AppAlert.sucess(titleLabel.getScene().getWindow(),"sucessfully purchase "+product.getName()+" ->"+ quantity);
            titleLabel.getScene().getWindow().hide();

        } catch (Exception e) {
            AppAlert.warning(saveBtn.getScene().getWindow(),"Failed to save " + transactionType + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handle_save() {
        if (current_company == null){
            current_company = SessionContext.getCurrentCompany();
        }

        try {

            switch (transactionType) {

                case PURCHASE ->
                        purchaseService.purchase(current_company, product, quantity, supplier, paymentMethod);

                case SALE ->
                        salesService.sale(current_company, product, quantity, customer, paymentMethod);

                case ADJUSTMENT ->
                        inventoryService.adjustment(current_company, product, quantity);

                case DAMAGE ->
                        inventoryService.damage(current_company.getId(), product, quantity);
            }

            AppAlert.sucess(saveBtn.getScene().getWindow(),"Successfully saved " + transactionType);

        } catch (Exception e) {

            AppAlert.error(saveBtn.getScene().getWindow(),"Failed to save " + transactionType);

        } finally {

            Navigator.closeOverlay(mainVbox);

        }
    }

    @FXML
    public void initData(Product product, TransactionType type) {

        this.product = product;
        this.transactionType = type;

        titleLabel.setText(type + " - " + product.getName());
        dateLabel.setText(LocalDateTime.now().toString());
        transitionLabel.setText(type.toString());

        switch (type) {

            case PURCHASE -> {
                addSupplierField();
                qtyField.setPromptText("Enter purchase quantity");
            }

            case SALE -> {
                addCustomerField();
                qtyField.setPromptText("Enter selling quantity");
            }

            case ADJUSTMENT ->
                    qtyField.setPromptText("Enter adjustment quantity");

            case DAMAGE ->
                    qtyField.setPromptText("Enter damage quantity");
        }
    }

    private void addCustomerField() {
        customerLabel = new Label("Customer:");
        customerComboBox = new ComboBox<>();
        customerComboBox.setPromptText("Select Customer");
        customerComboBox.setItems(
                FXCollections .observableArrayList(customerService.getAllByCompany(SessionContext.getCurrentCompanyId()))
        );
        customerComboBox.setCellFactory(data ->new ListCell<>(){
            @Override
            protected void updateItem(Customer item,boolean empty){
                super.updateItem(item,empty);

                if (empty || item == null){
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        customerComboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Customer item,boolean empty){
                super.updateItem(item,empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        paymentMethodCombo = new ComboBox<>();
        validationSupport.registerValidator(paymentMethodCombo,
                Validator.createEmptyValidator("Select Payment Method"));
        paymentMethodCombo.setPromptText("Select payment method");
        paymentMethodCombo.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        this.customerComboBox = customerComboBox;
        this.paymentMethodCombo =paymentMethodCombo;

        hboxField.getChildren().addAll(customerLabel,customerComboBox,paymentMethodCombo);
    }

    private void addSupplierField() {

        Label label = new Label("Supplier:");
        ComboBox<Supplier> combo = new ComboBox<>();

        Label paymentMethodLabel = new Label("Payment Method");
        paymentMethodCombo = new ComboBox<>();

        validationSupport.registerValidator(
                paymentMethodCombo,
                Validator.createEmptyValidator("Select Payment Method")
        );

        paymentMethodCombo.setPromptText("Select payment method");
        paymentMethodCombo.setItems(FXCollections.observableArrayList(PaymentMethod.values()));

        combo.setPromptText("Select Supplier");
        combo.setItems(FXCollections.observableArrayList(
                supplierService.getAllByCompany(SessionContext.getCurrentCompanyId())
        ));

        this.supplierComboBox = combo;

        supplierComboBox.setCellFactory(data -> new ListCell<>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        supplierComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        hboxField.getChildren().addAll(label, combo, paymentMethodLabel, paymentMethodCombo);
    }

    public void setup_validation() {
        validationSupport.registerValidator(
                qtyField, Validator.createEmptyValidator("quantity is required")
        );
        validationSupport.registerValidator(qtyField,
                Validator.createPredicateValidator(
                        text -> {
                            try {
                                new BigDecimal(qtyField.getText());
                                return true;
                            } catch (Exception e) {
                                return false;
                            }
                        }, "Quantity must be a number"
                ));
    }

    @Override
    public void refresh() {
    }
}
