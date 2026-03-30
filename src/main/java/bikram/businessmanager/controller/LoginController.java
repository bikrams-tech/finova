package bikram.businessmanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class LoginController {

    @FXML private VBox left_vbox;
    @FXML private TextField user_name_Field;
    @FXML private PasswordField password_field;

    @FXML
    public void initialize() {
        setUpImage();
    }

    private void setUpImage() {
        // Load image from resources
        Image image = new Image(getClass().getResource("/bikram/businessmanager/image/lock.png").toExternalForm());

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        // Create circular mask
        Circle mask = new Circle(50, 50, 50); // centerX, centerY, radius
        imageView.setClip(mask);

        left_vbox.getChildren().add(imageView);
    }

    @FXML
    private void handle_login(ActionEvent event) {
        // Login logic here
    }
}