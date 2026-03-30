package bikram.businessmanager.controller.company;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.utils.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class CompanyDetailController {

    @FXML private VBox root;
    @FXML private Label companyId;
    @FXML private Label fiscalEndLabel;
    @FXML private Label fiscalStartLabel;
    @FXML private Label typeLabel;
    @FXML private Label ownerLabel;
    @FXML private Label industryLabel;
    @FXML private Label nameLabel;
    @FXML private Label panLabel;
    @FXML private Label vatLabel;
    @FXML private Label registrationLabel;
    @FXML private Label addressLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label branchLabel;
    @FXML private Label createdLabel;



    public void setCompany(Company company) {
        companyId.setText(String.valueOf(company.getId()));
        nameLabel.setText(company.getCompanyName());
        panLabel.setText(company.getPanNumber());
        vatLabel.setText(company.getVatNumber());
        registrationLabel.setText(company.getRegistrationNumber());
        addressLabel.setText(company.getAddress());
        phoneLabel.setText(company.getPhone());
        emailLabel.setText(company.getEmail());
        branchLabel.setText(String.valueOf(company.getBranchId()));
        createdLabel.setText(String.valueOf(company.getCreatedAt()));
        fiscalStartLabel.setText(String.valueOf(company.getFiscalYearStart()));
        fiscalEndLabel.setText(String.valueOf(company.getFiscalYearEnd()));
        ownerLabel.setText(company.getOwnerName());
        typeLabel.setText(String.valueOf(company.getCompanyType()));
        industryLabel.setText(String.valueOf(company.getIndustryType()));
    }

    @FXML
    private void handleClose() {
        Navigator.back();
    }

    @FXML private void on_back(ActionEvent event) {
        Navigator.back();
    }
}