package bikram.businessmanager.controller.counter;

import bikram.businessmanager.entity.PaymentMethod;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.entity.inventory.Sale;
import bikram.businessmanager.entity.inventory.SaleItem;
import bikram.businessmanager.service.ProductService;
import bikram.businessmanager.service.SalesService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CounterViewController {

    private final ProductService productService = ServiceProvider.services().getProductService();
    @FXML private FlowPane product_card_container;

    @FXML private Label company_label;
    @FXML private Label date_time_label;
    @FXML private TextField search_field;
    @FXML private TextField barcodeField;

    @FXML private TableView<SaleItem> sales_table;

    @FXML private TableColumn<SaleItem, Long> index_colum;
    @FXML private TableColumn<SaleItem, String> item_name_colum;
    @FXML private TableColumn<SaleItem, BigDecimal> quintity_colum;
    @FXML private TableColumn<SaleItem, BigDecimal> price_colum;
    @FXML private TableColumn<SaleItem, BigDecimal> tax_colum;
    @FXML private TableColumn<SaleItem, BigDecimal> discount_column;
    @FXML private TableColumn<SaleItem, BigDecimal> total_amount_colum;

    @FXML private HBox detail_container;

    @FXML private Label total_item_label;
    @FXML private Label total_tax_label;
    @FXML private Label total_discount_label;
    @FXML private Label total_amount_label;

    private final ObservableList<SaleItem> saleItems = FXCollections.observableArrayList();

    private List<Product> allProducts = new ArrayList<>();

    @FXML
    public void initialize() {

        setupTable();

        sales_table.setItems(saleItems);

        Label emptyLabel = new Label("No product added");
        emptyLabel.setStyle("""
            -fx-font-size: 16px;
            -fx-text-fill: #888;
            -fx-font-weight: bold;
        """);
        sales_table.setPlaceholder(emptyLabel);

        search_field.textProperty().addListener((obs, oldVal, newVal) -> {
            filterProducts(newVal);
        });

        setUpProduct();
    }

    private void setupTable() {

        index_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        sales_table.getItems().indexOf(data.getValue()) + 1L
                ));

        item_name_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getProduct().getName()));

        quintity_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getQuantity()));

        price_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getUnitPrice()));

        tax_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getTax()));

        discount_column.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getDiscount()));

        total_amount_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getTotalPrice()));

        sales_table.setEditable(true);

        quintity_colum.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        quintity_colum.setOnEditCommit(e -> {
            SaleItem item = e.getRowValue();
            item.setQuantity(e.getNewValue());
            item.calculateAmounts();
            calculateTotals();
            sales_table.refresh();
        });

        price_colum.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        price_colum.setOnEditCommit(e -> {
            SaleItem item = e.getRowValue();
            item.setUnitPrice(e.getNewValue());
            item.calculateAmounts();
            calculateTotals();
            sales_table.refresh();
        });

        discount_column.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        discount_column.setOnEditCommit(e -> {
            SaleItem item = e.getRowValue();
            item.setDiscount(e.getNewValue());
            item.calculateAmounts();
            calculateTotals();
            sales_table.refresh();
        });
    }

    @FXML
    private void handle_barcode_scan() {

        String barcode = barcodeField.getText().trim();

        if (barcode.isEmpty()) return;

        Product product = findProductByBarcode(barcode);

        if (product != null) {
            addProductToCart(product);
        }

        barcodeField.clear();
        barcodeField.requestFocus();
    }

    private Product findProductByBarcode(String barcode) {

        return allProducts.stream()
                .filter(p -> barcode.equals(p.getBarcode()))
                .findFirst()
                .orElse(null);
    }

    private void addProductToCart(Product product) {

        for (SaleItem item : saleItems) {

            if (item.getProduct().getId().equals(product.getId())) {

                item.setQuantity(item.getQuantity().add(BigDecimal.ONE));
                item.calculateAmounts();

                sales_table.refresh();
                calculateTotals();
                return;
            }
        }

        SaleItem item = SaleItem.builder()
                .product(product)
                .quantity(BigDecimal.ONE)
                .unitPrice(product.getSellingPrice())
                .discount(BigDecimal.ZERO)
                .tax(product.getTaxRate() == null ? BigDecimal.ZERO : product.getTaxRate())
                .build();

        item.calculateAmounts();

        saleItems.add(item);

        calculateTotals();
    }

    private void calculateTotals() {

        BigDecimal totalItems = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (SaleItem item : saleItems) {
            totalItems = totalItems.add(item.getQuantity());
            totalTax = totalTax.add(item.getTax());
            totalDiscount = totalDiscount.add(item.getDiscount());
            grandTotal = grandTotal.add(item.getTotalPrice());
        }

        total_item_label.setText(totalItems.toPlainString());
        total_tax_label.setText(totalTax.toPlainString());
        total_discount_label.setText(totalDiscount.toPlainString());
        total_amount_label.setText(grandTotal.toPlainString());
    }

    private void setUpProduct() {

        Task<List<Product>> task = new Task<>() {
            @Override
            protected List<Product> call() {
                return productService.getAllByCompany(SessionContext.getCurrentCompanyId());
            }
        };

        task.setOnSucceeded(e -> {
            allProducts = task.getValue();
            renderProducts(allProducts);
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void renderProducts(List<Product> products) {
        product_card_container.getChildren().clear();

        products.forEach(product ->
                product_card_container.getChildren().add(createProductCard(product))
        );
    }

    private VBox createProductCard(Product product) {

        VBox card = new VBox(10);
        card.setPrefWidth(180);
        card.setPrefHeight(100);

        String normalStyle = """
            -fx-background-color: #334155;
            -fx-padding: 15;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: #475569;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12),10,0,0,3);
        """;

        String hoverStyle = """
            -fx-background-color: #475569;
            -fx-padding: 15;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: #22c55e;
            -fx-effect: dropshadow(gaussian, rgba(34,197,94,0.25),12,0,0,4);
        """;

        card.setStyle(normalStyle);

        Label name = new Label(product.getName());
        name.setWrapText(true);
        name.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");

        Label price = new Label("¥ " + product.getSellingPrice());
        price.setStyle("-fx-font-size:15px; -fx-text-fill:#22c55e;");

        card.getChildren().addAll(name, price);

        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(normalStyle));

        card.setOnMouseClicked(e -> addProductToCart(product));

        return card;
    }

    private void filterProducts(String keywords) {

        if (keywords == null || keywords.isBlank()) {
            renderProducts(allProducts);
            return;
        }

        List<Product> filtered = allProducts.stream()
                .filter(product ->
                        product.getName().toLowerCase().contains(keywords.toLowerCase()) ||
                                (product.getBarcode() != null &&
                                        product.getBarcode().contains(keywords)))
                .toList();

        renderProducts(filtered);
    }

    @FXML
    private void on_clear_cart(ActionEvent event) {
        saleItems.clear();
        calculateTotals();
    }

    @FXML
    private void on_hold_bill(ActionEvent event) {
    }

    @FXML
    private void on_payment(ActionEvent actionEvent) {
        if (saleItems.isEmpty()){
            AppAlert.error(search_field.getScene().getWindow(),"Cart is empty");
        }

        Sale sale = new Sale();
        sale.setSalesDate(LocalDateTime.now());
        sale.setCompany(SessionContext.getCurrentCompany());
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (SaleItem item :saleItems){
            totalTax = totalTax.add(item.getTax());
            totalDiscount = totalDiscount.add(item.getDiscount());
            grandTotal = grandTotal.add(item.getTotalPrice());
        }
        sale.setGrandTotal(grandTotal);
        sale.setDiscount(totalDiscount);
        sale.setTax(totalTax);
        for (SaleItem item : saleItems){
            item.setSales(sale);
        }
        sale.setSalesItems(new ArrayList<>(saleItems));

        PaymentViewController controller = Navigator.showOverlay("/bikram/businessmanager/ui/payment_view.fxml");
        controller.setSale(sale);

    }


    @FXML private void filterProducts(ActionEvent event) {
        filterProducts(search_field.getText());
    }

    @FXML private void on_customer(ActionEvent event) {
    }
}