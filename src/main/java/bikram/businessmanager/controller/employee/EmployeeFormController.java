package bikram.businessmanager.controller.employee;

import bikram.businessmanager.entity.Employee;
import bikram.businessmanager.service.EmployeeService;
import bikram.businessmanager.service.ServiceProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EmployeeFormController {

    private final EmployeeService service = ServiceProvider.services().getEmployeeService();

    private Runnable onSaveCallback; // callback to refresh table

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField citizenshipNoField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField addressField;
    @FXML private TextField positionField;
    @FXML private TextField salaryField;
    @FXML private DatePicker hireDatePicker;
    @FXML private TextField statusField;

    public void initialize() {
        // Optional: Any initial setup
        firstNameField.requestFocus();
    }

    @FXML
    private void saveEmployee() {
        if (!validateForm()) return;

        try {
            Employee employee = new Employee();
            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setCitizenshipNo(citizenshipNoField.getText().trim());
            employee.setPhone(phoneNumberField.getText().trim());
            employee.setAddress(addressField.getText().trim());
            employee.setPosition(positionField.getText().trim());
            employee.setSalary(Double.parseDouble(salaryField.getText().trim()));
            employee.setHireDate(hireDatePicker.getValue().atStartOfDay());
            employee.setStatus(statusField.getText().trim());

            service.create(employee);

            // Refresh parent table
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee created successfully.");
            clearForm(null);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Failed to create Employee!");
            e.printStackTrace();
        }
    }

    @FXML
    private void clearForm(ActionEvent actionEvent) {
        firstNameField.clear();
        lastNameField.clear();
        citizenshipNoField.clear();
        phoneNumberField.clear();
        addressField.clear();
        positionField.clear();
        salaryField.clear();
        hireDatePicker.setValue(null);
        statusField.clear();
    }


    private boolean validateForm() {
        if (firstNameField.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First name is required.");
            return false;
        }
        if (lastNameField.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Last name is required.");
            return false;
        }
        if (salaryField.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Salary is required.");
            return false;
        }
        if (hireDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Hire date is required.");
            return false;
        }
        try {
            Double.parseDouble(salaryField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Salary must be a valid number.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }


    @FXML public void canceltask(ActionEvent actionEvent) {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }
}