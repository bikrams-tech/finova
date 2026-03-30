package bikram.businessmanager.controller.company;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.CompanyType;
import bikram.businessmanager.entity.IndustryType;
import bikram.businessmanager.service.*;
import bikram.businessmanager.utils.AppAlert;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import jakarta.enterprise.inject.build.compatible.spi.Validation;
import jakarta.persistence.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.time.LocalDate;

public class CompanyRegisterFormController {


    @FXML private Button saveBtn;
    @FXML private ScrollPane root;
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


    private CompanyService service = ServiceProvider.services().getCompanyService();
    private final SubAccountService subAccountService = ServiceProvider.services().getSubAccountService();
    private final AccountService accountService = ServiceProvider.services().getAccountService();
    private final ValidationSupport vs = new ValidationSupport();

    @FXML
    public void initialize() {
        // Populate ComboBoxes
        companyTypeCombo.getItems().setAll(CompanyType.values());
        industryField.getItems().setAll(IndustryType.values());

        setupComboBoxes();
        setupValidation();

        saveBtn.disableProperty().bind(vs.invalidProperty());
    }
    private void setupComboBoxes(){
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
    private void setupValidation() {

        vs.registerValidator(companyNameField,
                Validator.createEmptyValidator("Company name is required"));

        vs.registerValidator(panField,
                Validator.createEmptyValidator("PAN is required"));

        vs.registerValidator(phoneField,
                Validator.createRegexValidator(
                        "Phone must contain digits only",
                        "\\d+",
                        null));

        vs.registerValidator(emailField,
                Validator.createRegexValidator(
                        "Invalid email format",
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                        null));

        vs.registerValidator(branchField,
                Validator.createRegexValidator(
                        "Branch ID must be numeric",
                        "^\\d*$",
                        null
                ));

        vs.registerValidator(companyTypeCombo,
                Validator.createEmptyValidator("Select company type"));

        vs.registerValidator(industryField,
                Validator.createEmptyValidator("Select industry type"));

        vs.registerValidator(fiscalYearStartPicker,
                Validator.createEmptyValidator("Select fiscal start"));

        vs.registerValidator(fiscalYearEndPicker,
                Validator.createEmptyValidator("Select fiscal end"));
    }

    @FXML
    public void saveCompany(){
        if (vs.isInvalid()){
            AppAlert.error(companyNameField.getScene().getWindow(),"please fix input.");
            return;
        }
        try{
        Company company = new Company();
        company.setCompanyName(companyNameField.getText());
        company.setPanNumber(panField.getText());
        company.setVatNumber(getValue(vatField));
        company.setRegistrationNumber(getValue(registrationField));
        company.setAddress(getValue(addressField));
        company.setPhone(phoneField.getText());
        company.setEmail(emailField.getText());
        company.setFiscalYearStart(fiscalYearStartPicker.getValue());
        company.setFiscalYearEnd(fiscalYearEndPicker.getValue());
        company.setCompanyType(companyTypeCombo.getValue());
        company.setIndustryType(industryField.getValue());
        company.setOwnerName(getValue(ownerField));
        company.setActive(activeCheck.isSelected());

        company.setBranchId((parseOptionalLong(branchField)));

        service.create(company);
        // reload table in parent controller
        AppAlert.sucess(saveBtn.getScene().getWindow(), "Company saved successfully.");
            SessionContext.setCurrentCompany(company);
        Navigator.navigate("/bikram/businessmanager/dashboard/dashboardview.fxml");

        clearForm(null);
        } catch (Exception e){
            e.printStackTrace();
            AppAlert.error(saveBtn.getScene().getWindow(), "failed to saved.");
        }
    }
    private String getValue(TextField field){
        String value = field.getText().trim();
        return value.isEmpty() ? null :value;
    }
    private Long parseOptionalLong(TextField field){
        String value = field.getText().trim();
        return value.isEmpty() ? null : Long.valueOf(value);
    }

    @FXML
    public void clearForm(ActionEvent actionEvent) {

        companyNameField.clear();
        panField.clear();
        vatField.clear();
        registrationField.clear();
        addressField.clear();
        phoneField.clear();
        emailField.clear();
        fiscalYearStartPicker.setValue(LocalDate.now());
        fiscalYearEndPicker.setValue(LocalDate.now());
        industryField.setValue(null);
        activeCheck.setSelected(false);
        branchField.clear();
    }

    @FXML
    public void canceltask(ActionEvent actionEvent) {
        Navigator.back();
    }

}