package bikram.businessmanager.controller.product;

import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.utils.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ProductDetailViewController {
    @FXML private AnchorPane root;
    @FXML private Label nameLabel;
    @FXML private Label barcodeLabel;
    @FXML private Label categoryLabel;
    @FXML private Label unitLabel;
    @FXML private Label costPriceLabel;
    @FXML private Label sellingPriceLabel;
    @FXML private Label vatApplicableLabel;
    @FXML private Label vatRateLabel;
    @FXML private Label companyLabel;
    @FXML private Label createdLabel;

    @FXML
    private void handleClose(ActionEvent event) {
        Navigator.closeOverlay(root);
    }

    public void setProduct(Product product) {
        if (product == null) return;

        nameLabel.setText(product.getName());
        barcodeLabel.setText(product.getBarcode());
        categoryLabel.setText(String.valueOf(product.getCategory()));
        unitLabel.setText(String.valueOf(product.getUnit()));
        costPriceLabel.setText(String.format("%.2f", product.getCostPrice()));
        sellingPriceLabel.setText(String.format("%.2f", product.getSellingPrice()));
        vatApplicableLabel.setText(product.isVatApplicable() ? "Yes" : "No");
        vatRateLabel.setText(String.format("%.2f", product.getVatRate()));
        companyLabel.setText(String.valueOf(product.getCompany()));
        createdLabel.setText(
                product.getCreatedAt() != null
                        ? product.getCreatedAt().toString()
                        : "-"
        );
    }

    @FXML private void on_back(ActionEvent event) {
        Navigator.back();
    }
}
