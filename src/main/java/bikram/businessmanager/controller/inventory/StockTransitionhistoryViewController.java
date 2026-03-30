package bikram.businessmanager.controller.inventory;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.inventory.InventoryTransaction;
import bikram.businessmanager.service.InventorytranstionService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.SessionContext;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class StockTransitionhistoryViewController implements Refreshable {

    @FXML private Pagination pagination;
    @FXML private VBox main_vbox;

    private final int PAGE_SIZE = 20;

    @FXML private Label today_date_time;
    @FXML private Label company_label_title;

    @FXML private TableView<InventoryTransaction> table_view;

    @FXML private TableColumn<InventoryTransaction, String> date_colum;
    @FXML private TableColumn<InventoryTransaction, String> product_colum;
    @FXML private TableColumn<InventoryTransaction, BigDecimal> quaintity_colum;
    @FXML private TableColumn<InventoryTransaction, String> transition_type_colum;
    @FXML private TableColumn<InventoryTransaction, String> refrence_colum;

    private final InventorytranstionService inventorytranstionService =
            ServiceProvider.services().getInventorytranstionService();

    @FXML
    public void initialize() {

        today_date_time.setText(LocalDateTime.now().toString());

        company_label_title.setText(
                SessionContext.getCurrentCompany().getCompanyName()
        );

        setTable();

        setupPagination();
    }

    private void setupPagination() {

        long totalRows =
                inventorytranstionService.countByCompany(
                        SessionContext.getCurrentCompanyId()
                );

        int pageCount =
                (int) Math.ceil((double) totalRows / PAGE_SIZE);

        pagination.setPageCount(pageCount);

        pagination.currentPageIndexProperty().addListener(
                (obs, oldIndex, newIndex) ->
                        loadPage(newIndex.intValue())
        );

        loadPage(0);
    }

    private void loadPage(int pageIndex) {

        new Thread(() -> {

            int offset = pageIndex * PAGE_SIZE;

            List<InventoryTransaction> list =
                    inventorytranstionService.getPageByCompany(
                            SessionContext.getCurrentCompanyId(),
                            PAGE_SIZE,
                            offset
                    );

            javafx.application.Platform.runLater(() ->
                    table_view.setItems(
                            FXCollections.observableArrayList(list)
                    )
            );

        }).start();
    }

    private void setTable() {

        table_view.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        date_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getDate().toString()
                )
        );

        product_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getProduct().getName()
                )
        );

        quaintity_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getQuantity()
                )
        );

        transition_type_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getType().toString()
                )
        );

        refrence_colum.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getReference()
                )
        );
    }

    @Override
    public void refresh() {
        loadPage(pagination.getCurrentPageIndex());
    }
}