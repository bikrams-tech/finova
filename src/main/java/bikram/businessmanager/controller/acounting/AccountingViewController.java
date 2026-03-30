package bikram.businessmanager.controller.acounting;

import bikram.businessmanager.controller.Refreshable;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;

public class AccountingViewController {
    private static Refreshable activeController;

    private boolean collapsed;

    /// sidebar
    @FXML private VBox sidebar;
    @FXML private Button toogleBtn;
    @FXML private Button journalBtn;
    @FXML private Button paymentVoucherBtn;
    @FXML private Button reciptVoucherBtn;
    @FXML private Button ledgerBtn;
    @FXML private Button trailBtn;
    @FXML private Button profitlossBtn;
    @FXML private Button balenceSheetBtn;
    @FXML private Button taxBtn;
    @FXML private Button accountSettingBtn;



    public static void setActiveController(Refreshable controller){
        activeController = controller;
    }


    @FXML
    private void toggleSidebar() {

        double targetWidth = collapsed ? 220 : 60;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(sidebar.prefWidthProperty(), targetWidth))
        );

        timeline.setOnFinished(e -> {
            updateSidebarButtons();
            collapsed = !collapsed;
        });

        timeline.play();
    }
    public static void refreshActive(){
        if(activeController != null){
            activeController.refresh();
        }
    }

    private void updateSidebarButtons() {

        if (collapsed) {
            toogleBtn.setText("✖️");
            journalBtn.setText("📘Journal Entry");
            paymentVoucherBtn.setText("💰Payment Voucher");
            reciptVoucherBtn.setText("💵 Receipt Voucher");
            ledgerBtn.setText("📂Ledger");
            trailBtn.setText("📊Trail Balance");
            profitlossBtn.setText("📉Profit Loss");
            balenceSheetBtn.setText("🏦Balance Sheet");
            taxBtn.setText("💸Tax");
            accountSettingBtn.setText("⚙Account Setting");
        } else {
            toogleBtn.setText("☰");
            journalBtn.setText("📘");
            paymentVoucherBtn.setText("💰");
            reciptVoucherBtn.setText("💵");
            ledgerBtn.setText("📂");
            trailBtn.setText("📊");
            profitlossBtn.setText("📉");
            balenceSheetBtn.setText("🏦");
            taxBtn.setText("💸");
            accountSettingBtn.setText("⚙");
        }
    }

    @FXML private StackPane AccountingContentArea;

    @FXML
    private void initialize() {
        Company company = SessionContext.getCurrentCompany();
    }

    @FXML private void openNewVoucherEntryPage(ActionEvent event) {
        loadView("bikram/businessmanager/accountingView/voucherEntryView.fxml");
    }

    @FXML private void createNewAccountPage(ActionEvent event) {
        loadView("bikram/businessmanager/accountingView/AccountCreateFormView.fxml");
    }


    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/"+fxml));
            Parent view = loader.load();
            Object controller = loader.getController();

            if (controller instanceof Refreshable refreshable){
                setActiveController(refreshable);
            }

            AccountingContentArea.getChildren().clear();
            AccountingContentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML public void createSubAccount(ActionEvent event) {
        loadView("bikram/businessmanager/accountingView/subAccountcreateview.fxml");
    }

    @FXML public void onJournalViewEntryOpen(ActionEvent event) {
        loadView("bikram/businessmanager/accountingView/JournalEntryView.fxml");
    }

    @FXML private void onshowJournalEntryTable(ActionEvent event) {
        loadView("bikram/businessmanager/accountingView/report/JournalEntryList.fxml");
    }

    @FXML private void onLedgerView(ActionEvent event) {
        loadView("bikram/businessmanager/accountingView/report/ledgerEntryView.fxml");
    }

    public void onAccountTableView(ActionEvent event) {
        loadView("bikram/businessmanager/accountingView/report/ledgerAccountTableView.fxml");
    }

    public void onSubAccountTableView(ActionEvent event) {
        loadView("bikram/businessmanager/accountingView/report/AccountListView.fxml");
    }

    public void onProfitAndLoss(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/accountingView/report/profitAndloss.fxml");
    }

    public void on_contra_entry(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/accountingView/transition/contra_voucher_entry.fxml");
    }

    public void on_journal_entry(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/accountingView/JournalEntryView.fxml");
    }

    public void on_payment_voucher(ActionEvent event) {
    }

    public void on_recipt_voucher(ActionEvent event) {
    }

    public void on_ledger_acount(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/accountingView/report/ledgerAccountTableView.fxml");
    }

    public void on_trail_balance(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/accountingView/report/trail_balance_view.fxml");
    }

    public void on_profit_loss(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/accountingView/report/profitAndloss.fxml");
    }

    public void on_balance_sheet(ActionEvent event) {
        Navigator.navigate("/bikram/businessmanager/accountingView/report/balanceSheetView.fxml");
    }

    public void on_tax(ActionEvent event) {
    }

    public void on_account_setting(ActionEvent event) {

    }
}
