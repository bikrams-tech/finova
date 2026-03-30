package bikram.businessmanager.controller.company;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.CompanyType;
import bikram.businessmanager.entity.IndustryType;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CompanyEditController {
    private final CompanyService service = ServiceProvider.services().getCompanyService();

    @FXML private TextField companyNameField;
    @FXML private TextField panField;
    @FXML private TextField vatField;
    @FXML private TextField registrationField;
    @FXML private ComboBox<CompanyType> companyTypeCombo;
    @FXML private TextField ownerField;
    @FXML private ComboBox<IndustryType> industryField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker fiscalYearStartPicker;
    @FXML private DatePicker fiscalYearEndPicker;
    @FXML private CheckBox activeCheck;
    @FXML private TextField branchField;


    private Company company; // store selected company

    @FXML
    public void initialize() {
        // Populate ComboBoxes
        companyTypeCombo.getItems().setAll(CompanyType.values());
        industryField.getItems().setAll(IndustryType.values());

        // CompanyType ComboBox: display name in dropdown and selected button
        companyTypeCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(CompanyType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getDisplayName());
            }
        });
        companyTypeCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CompanyType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getDisplayName());
            }
        });

        // IndustryType ComboBox: display name in dropdown and selected button
        industryField.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(IndustryType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getDisplayName());
            }
        });
        industryField.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(IndustryType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getDisplayName());
            }
        });
    }

    public void setCompany(Company company) {
        this.company = company;

        companyNameField.setText(company.getCompanyName());
        panField.setText(company.getPanNumber());
        vatField.setText(company.getVatNumber());
        registrationField.setText(company.getRegistrationNumber());
        addressField.setText(company.getAddress());
        phoneField.setText(company.getPhone());
        emailField.setText(company.getEmail());
        branchField.setText(String.valueOf(company.getBranchId()));
        companyTypeCombo.setValue(company.getCompanyType());
        industryField.setValue(company.getIndustryType());
        ownerField.setText(company.getOwnerName());

        if (company.getFiscalYearStart() != null) {
            fiscalYearStartPicker.setValue(company.getFiscalYearStart());
        }
        if (company.getFiscalYearEnd() != null) {
            fiscalYearEndPicker.setValue(company.getFiscalYearEnd());
        }
    }

    @FXML
    private void handleUpdate() {

        company.setCompanyName(companyNameField.getText());
        company.setPanNumber(panField.getText());
        company.setVatNumber(vatField.getText());
        company.setRegistrationNumber(registrationField.getText());
        company.setAddress(addressField.getText());
        company.setPhone(phoneField.getText());
        company.setEmail(emailField.getText());
        company.setBranchId(Long.parseLong(branchField.getText()));
        company.setFiscalYearStart(fiscalYearStartPicker.getValue());
        company.setFiscalYearEnd(fiscalYearEndPicker.getValue());
        company.setCompanyType(companyTypeCombo.getValue());
        company.setIndustryType(industryField.getValue());
        company.setOwnerName(ownerField.getText());

        CompanyService service = ServiceProvider.services().getCompanyService();
        service.update(company);

        // close window
        Navigator.navigate("/bikram/businessmanager/table/companyTable.fxml");
    }
    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        Navigator.navigate("/bikram/businessmanager/table/companyTable.fxml");
    }
    }

