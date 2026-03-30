package bikram.businessmanager.controller.product;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.controller.inventory.InventoryTransitionFormController;
import bikram.businessmanager.entity.inventory.Inventory;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.entity.inventory.ProductCategory;
import bikram.businessmanager.entity.Unit;
import bikram.businessmanager.entity.inventory.TransactionType;
import bikram.businessmanager.service.ProductService;
import bikram.businessmanager.service.ServiceContainer;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductTableController implements Refreshable {

    private final ProductService service = ServiceProvider.services().getProductService();
    private final ObservableList<Product> masterList = FXCollections.observableArrayList();
    private FilteredList<Product> filteredList;

    @FXML
    public TextField searchField;
    @FXML
    public Button searchBtn;
    @FXML
    public TableView<Product> productTableView;
    @FXML
    public TableColumn<Product, Long> idColumn;
    @FXML
    public TableColumn<Product, String> nameColumn;
    @FXML
    public TableColumn<Product, String> barcodeColumn;
    @FXML
    public TableColumn<Product, ProductCategory> categoryColumn;
    @FXML
    public TableColumn<Product, Unit> unitColumn;
    @FXML
    public TableColumn<Product, BigDecimal> costPriceColumn;
    @FXML
    public TableColumn<Product, BigDecimal> sellingPriceColumn;
    @FXML
    public TableColumn<Product, Boolean> vatApplicableColumn;
    @FXML
    public TableColumn<Product, BigDecimal> vatRateColumn;
    @FXML
    public TableColumn<Product, LocalDateTime> createdAtColumn;
    @FXML
    private Pagination pagination;

    private final int ROWS_PER_PAGE = 20;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {

        productTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setupColumns();
        setupFilteredList();
        setupRowFactory();
        loadProducts();
    }

    // ==============================
    // Column Setup
    // ==============================
    private void setupColumns() {

        idColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        nameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getName()));
        barcodeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getBarcode()));
        categoryColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCategory()));
        unitColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getUnit()));
        costPriceColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCostPrice()));
        sellingPriceColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getSellingPrice()));
        vatApplicableColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().isVatApplicable()));
        vatRateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getVatRate()));
        createdAtColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCreatedAt()));

        costPriceColumn.setCellFactory(col -> formatBigDecimal());
        sellingPriceColumn.setCellFactory(col -> formatBigDecimal());
        vatRateColumn.setCellFactory(col -> formatBigDecimal());

        categoryColumn.setCellFactory(col -> formatEnum());
        unitColumn.setCellFactory(col -> formatEnum());

        vatApplicableColumn.setCellFactory(col -> formatBooleanCheckbox());

        createdAtColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : DATE_FORMAT.format(value));
            }
        });
    }

    // ==============================
    // Row Double Click
    // ==============================
    private void setupRowFactory() {

        productTableView.setRowFactory(tv -> {

            TableRow<Product> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Product selectedProduct = row.getItem();
                    showProductOptionsDialog(selectedProduct);
                }
            });

            return row;
        });
    }

    // ==============================
    // Search Filter
    // ==============================
    private void setupFilteredList() {

        filteredList = new FilteredList<>(masterList, b -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            filteredList.setPredicate(product -> {

                if (newVal == null || newVal.isEmpty()) return true;

                String lower = newVal.toLowerCase();

                return product.getName().toLowerCase().contains(lower)
                        || product.getBarcode().toLowerCase().contains(lower)
                        || product.getCategory().toString().toLowerCase().contains(lower)
                        || product.getUnit().toString().toLowerCase().contains(lower);
            });

            setupPagination();
        });
    }

    // ==============================
    // Load Products
    // ==============================
    private void loadProducts() {

        ProgressIndicator loader = new ProgressIndicator();
        productTableView.setPlaceholder(loader);

        Task<ObservableList<Product>> task = new Task<>() {
            @Override
            protected ObservableList<Product> call() {
                return FXCollections.observableArrayList(service.getAllByCompany(SessionContext.getCurrentCompanyId()));
            }
        };

        task.setOnSucceeded(event -> {
            masterList.setAll(task.getValue());
            setupPagination();
            productTableView.setPlaceholder(new Label("No Products Found"));
        });

        task.setOnFailed(event -> {
            productTableView.setPlaceholder(new Label("Failed to load products"));
            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private ObservableList<Product> getCurrentPageItems(int pageIndex) {

        if (filteredList == null || filteredList.isEmpty())
            return FXCollections.observableArrayList();

        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredList.size());

        return FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex));
    }

    private void setupPagination() {

        int pageCount = (int) Math.ceil((double) filteredList.size() / ROWS_PER_PAGE);

        pagination.setPageCount(pageCount > 0 ? pageCount : 1);

        pagination.setPageFactory(pageIndex -> {
            productTableView.setItems(getCurrentPageItems(pageIndex));
            return productTableView;
        });
    }

    // ==============================
    // Product Options Dialog
    // ==============================
    private void showProductOptionsDialog(Product product) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Product Options");

        ButtonType editBtn = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        ButtonType viewBtn = new ButtonType("View Details", ButtonBar.ButtonData.APPLY);
        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.OTHER);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(editBtn, viewBtn, deleteBtn, cancelBtn);

        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");

        content.getChildren().addAll(
                new Label("Product: " + product.getName()),
                new Label("Category: " + product.getCategory()),
                new Label("Price: " + product.getSellingPrice())
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button == editBtn) openEditView(product);
            if (button == viewBtn) openDetailView(product);
            if (button == deleteBtn) confirmDelete(product);
            return null;
        });

        dialog.showAndWait();
    }

    // ==============================
    // Edit / Detail / Delete
    // ==============================
    private void openDetailView(Product product) {

        try {

            ProductDetailViewController controller =
                    Navigator.showOverlay("/bikram/businessmanager/detailview/ProductDetailView.fxml");

            controller.setProduct(product);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEditView(Product product) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/bikram/businessmanager/editview/ProductEditView.fxml"));

            Parent root = loader.load();

            ProductEditController controller = loader.getController();
            controller.setProduct(product);

            controller.setOnSaveCallback(this::loadProducts);

            Stage stage = new Stage();
            stage.setTitle("Edit Product");
            stage.setScene(new Scene(root));
            stage.setWidth(650);
            stage.setHeight(620);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmDelete(Product product) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Delete Product");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {

                service.deleteById(product.getId());
                loadProducts();
            }
        });
    }

    // ==============================
    // Table Formatters
    // ==============================
    private TableCell<Product, BigDecimal> formatBigDecimal() {

        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {

                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
                }
            }
        };
    }

    private <T extends Enum<T>> TableCell<Product, T> formatEnum() {

        return new TableCell<>() {
            @Override
            protected void updateItem(T value, boolean empty) {

                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.name());
            }
        };
    }

    private TableCell<Product, Boolean> formatBooleanCheckbox() {

        return new TableCell<>() {

            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean value, boolean empty) {

                super.updateItem(value, empty);

                if (empty || value == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(value);
                    checkBox.setDisable(true);
                    setGraphic(checkBox);
                }
            }
        };
    }

    public void searchByBtn(ActionEvent actionEvent) {

        setupPagination();
    }

    private void openTransactionForm(Product product, TransactionType type) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/bikram/businessmanager/form/stockTransitionForm.fxml"));

            Parent root = loader.load();

            InventoryTransitionFormController controller = loader.getController();
            controller.initData(product, type);

            Stage stage = new Stage();
            stage.setTitle(type.toString());
            stage.setScene(new Scene(root));
            stage.initOwner(productTableView.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPurchaseClick(ActionEvent event) {
        Product selected = productTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a product.");
            return;
        }
        openTransactionForm(selected,TransactionType.PURCHASE);
    }

    public void onSaleClick(ActionEvent event) {
        Product selected = productTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a product.");
            return;
        }
        openTransactionForm(selected,TransactionType.SALE);
    }

    public void onDamageClick(ActionEvent event) {
        Product selected = productTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a product.");
            return;
        }
        openTransactionForm(selected,TransactionType.DAMAGE);
    }

    public void onAdjustmentClick(ActionEvent event) {
        Product selected = productTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a product.");
            return;
        }
        openTransactionForm(selected,TransactionType.ADJUSTMENT);
    }

    public void onCurentStockclick(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/table/stockview.fxml");
    }

    private void showWarning(String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.initOwner(productTableView.getScene().getWindow());
        alert.showAndWait();
    }

    @Override
    public void refresh() {
        loadProducts();
    }
}