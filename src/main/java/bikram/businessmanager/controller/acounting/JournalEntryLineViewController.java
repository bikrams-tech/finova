package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.entity.account.JournalLine;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.utils.Navigator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;

public class JournalEntryLineViewController implements Refreshable {
    @FXML private TableView<JournalLine> tableView;
    @FXML private TableColumn<JournalLine, SubAccount> accountColumn;
    @FXML private TableColumn<JournalLine,String> descriptioncolumn;
    @FXML private TableColumn<JournalLine, BigDecimal> debitcolumn;
    @FXML private TableColumn<JournalLine,BigDecimal> creditColumn;
    @FXML private Label totalDebitLabel;
    @FXML private Label totlCreditLabel;
    private JournalEntry journalEntry;

    public void setJournalEntry(JournalEntry entry) {
        this.journalEntry = entry;
        loadData();
    }

    private void loadData(){

        if(journalEntry == null){
            return;
        }

        ObservableList<JournalLine> lines =
                javafx.collections.FXCollections.observableArrayList(
                        journalEntry.getLine() == null ? java.util.List.of() : journalEntry.getLine()
                );

        tableView.setItems(lines);

        BigDecimal totalDebit = lines.stream()
                .map(JournalLine::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lines.stream()
                .map(JournalLine::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalDebitLabel.setText(totalDebit.toString());
        totlCreditLabel.setText(totalCredit.toString());
    }

    @FXML
    public void initialize() {
        setupColumns();
    }

    private void setupColumns() {

        accountColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getSubAccount()));

        accountColumn.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(SubAccount item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        descriptioncolumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNote()));

        debitcolumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getDebit()));

        creditColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getCredit()));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setTableMenuButtonVisible(true);
        tableView.setEditable(false);
        tableView.setSelectionModel(null);

        tableView.setPlaceholder(new Label("No journal lines"));

        totalDebitLabel.setStyle("-fx-font-weight:bold;");
        totlCreditLabel.setStyle("-fx-font-weight:bold;");
    }

    @Override
    public void refresh() {
        loadData();
    }

   @FXML private void on_back(ActionEvent event) {
       Navigator.back();
    }
}
