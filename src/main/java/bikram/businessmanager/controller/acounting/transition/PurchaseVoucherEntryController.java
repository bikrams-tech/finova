package bikram.businessmanager.controller.acounting.transition;

import bikram.businessmanager.controller.Refreshable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class PurchaseVoucherEntryController implements Refreshable {
    @FXML private TextField voucher_no_field;
    @FXML private DatePicker date_picker_field;
    @FXML private TextField reference_field;
    @FXML private TableView table_view;
    @FXML private TableColumn amount_column;
    @FXML private TableColumn description_column;
    @FXML private TableColumn debit_column;
    @FXML private TableColumn credit_column;
    @FXML private TextArea narration_text_area_field;
    @FXML private GridPane total_crdit_field;
    @FXML private TextField total_debit_field;

    @FXML private void add_line(ActionEvent event) {
    }

    @FXML private void remove_line(ActionEvent event) {
    }

    @FXML private void save(ActionEvent event) {
    }

    @FXML private void print(ActionEvent event) {
    }

    @FXML private void cancel(ActionEvent event) {
    }

    @Override
    public void refresh() {
    }
}
