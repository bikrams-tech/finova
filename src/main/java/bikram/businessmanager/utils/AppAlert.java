package bikram.businessmanager.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

public class AppAlert {
    public static void show(Window owner, Alert.AlertType alertType,String title,String message){
        Alert alert = new Alert(alertType);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.show();
    }

    public static void sucess(Window owner,String message){
        show(owner, Alert.AlertType.INFORMATION,"Sucess",message);
    }

    public static void error(Window owner,String message){
        show(owner, Alert.AlertType.ERROR,"Error",message);
    }

    public static void warning(Window owner,String message){
        show(owner, Alert.AlertType.WARNING,"Warning",message);
    }

    public static boolean conform(Window owner,String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setHeaderText(null);
        alert.setTitle("Conform");
        alert.setContentText(message);

        return alert.showAndWait().orElse(null) == ButtonType.OK;

    }
}
