package bikram.businessmanager.controller.inventory;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.inventory.Inventory;
import bikram.businessmanager.entity.inventory.InventoryTransaction;
import bikram.businessmanager.entity.inventory.TransactionType;
import bikram.businessmanager.service.InventoryService;
import bikram.businessmanager.service.InventorytranstionService;
import bikram.businessmanager.service.ServiceContainer;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.SessionContext;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryViewController implements Refreshable {

    @FXML private TableView<Inventory> inventoryTableView;
    @FXML private TableColumn<Inventory,String> dateColum;
    @FXML private TableColumn<Inventory,String> productColum;
    @FXML private TableColumn<Inventory, BigDecimal> stockColum;
    @FXML private TableColumn<Inventory,Void> actionColumn;

    @FXML private TextField serchField;
    @FXML private Label companylabelField;
    private final InventoryService inventoryService = ServiceProvider.services().getInventoryService();
    private final InventorytranstionService inventorytranstionService = ServiceProvider.services().getInventorytranstionService();

    @FXML
    public void initialize() {

        dateColum.setCellValueFactory(cell -> {

            Inventory inventory = cell.getValue();

            String value = (inventory != null && inventory.getDate() != null)
                    ? inventory.getDate().toString()
                    : "";

            return new javafx.beans.property.SimpleStringProperty(value);
        });

        productColum.setCellValueFactory(cell -> {

            Inventory inventory = cell.getValue();

            String value = (inventory != null
                    && inventory.getProduct() != null
                    && inventory.getProduct().getName() != null)
                    ? inventory.getProduct().getName()
                    : "Unknown";

            return new javafx.beans.property.SimpleStringProperty(value);
        });

        stockColum.setCellValueFactory(cell -> {

            Inventory inventory = cell.getValue();

            BigDecimal qty = (inventory != null && inventory.getQuantity() != null)
                    ? inventory.getQuantity()
                    : BigDecimal.ZERO;

            return new javafx.beans.property.SimpleObjectProperty<>(qty);
        });


        loadInventory();
    }

    /**
     * Load inventory using background thread
     */
    private void loadInventory() {

        Task<List<Inventory>> task = new Task<>() {
            @Override
            protected List<Inventory> call() {

                return inventoryService.getAllByCompany(SessionContext.getCurrentCompanyId());
            }
        };

        task.setOnSucceeded(e -> {
            inventoryTableView.setItems(
                    FXCollections.observableArrayList(task.getValue()));
        });

        new Thread(task).start();
    }

    /**
     * Search inventory
     */
    public void onSerchClick(ActionEvent event) {

        String keyword = serchField.getText();

        Task<List<Inventory>> task = new Task<>() {
            @Override
            protected List<Inventory> call() {
                return inventoryService.search(keyword);
            }
        };

        task.setOnSucceeded(e -> {
            inventoryTableView.setItems(
                    FXCollections.observableArrayList(task.getValue()));
        });

        new Thread(task).start();
    }

    @Override
    public void refresh() {
        loadInventory();
    }
}