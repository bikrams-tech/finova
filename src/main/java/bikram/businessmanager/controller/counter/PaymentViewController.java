package bikram.businessmanager.controller.counter;

import bikram.businessmanager.entity.PaymentMethod;
import bikram.businessmanager.entity.inventory.Sale;
import bikram.businessmanager.service.SalesService;
import bikram.businessmanager.service.ServiceProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class PaymentViewController {
    private final SalesService salesService = ServiceProvider.services().getSalesService();

    private Sale sale;
    private PaymentMethod paymentMethod;

    @FXML private VBox cashBox;
    @FXML private VBox cardBox;
    @FXML private VBox qrBox;

    @FXML private Label total_amount_label;
    @FXML private TextField receiveField;
    @FXML private Label return_amount_label;

    @FXML
    public void initialize() {
        return_amount_label.setText("0");
    }

    public void setSale(Sale sale) {
        this.sale = sale;

        if (sale != null) {
            total_amount_label.setText(sale.getGrandTotal().toPlainString());
        }
    }

    public boolean validation() {

        if (sale == null) {
            showAlert("Sale is empty");
            return false;
        }

        if (paymentMethod == null) {
            showAlert("Please select payment method");
            return false;
        }

        if (receiveField.getText() == null || receiveField.getText().isBlank()) {
            showAlert("Please enter amount");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void calculateReturn(KeyEvent event) {

        if (sale == null) return;

        try {
            BigDecimal received = new BigDecimal(receiveField.getText());
            BigDecimal grandTotal = sale.getGrandTotal();

            if (received.compareTo(grandTotal) >= 0) {
                BigDecimal returnAmount = received.subtract(grandTotal);
                return_amount_label.setText(returnAmount.toPlainString());
            } else {
                return_amount_label.setText("0");
            }

        } catch (Exception e) {
            return_amount_label.setText("0");
        }
    }

    @FXML
    private void selectCashPayment(MouseEvent event) {
        paymentMethod = PaymentMethod.CASH;
    }

    @FXML
    private void selectCardPayment(MouseEvent event) {
        paymentMethod = PaymentMethod.CREDIT;
    }

    @FXML
    private void selectQrPayment(MouseEvent event) {
        paymentMethod = PaymentMethod.QR;
    }

    @FXML
    private void cancel(ActionEvent event) {
    }

    @FXML
    private void back(ActionEvent event) {
    }

    @FXML
    private void conform_payment(ActionEvent event) {

        if (!validation()) return;

        sale.setPaymentMethod(paymentMethod);

        try {
            boolean issale = salesService.multipalSale(sale);
            if (issale){
                showAlert("Payment successful");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}