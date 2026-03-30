package bikram.businessmanager.utils;

import bikram.businessmanager.controller.Refreshable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class Navigator {

    private static StackPane mainContainer;
    private static Stage mainStage;

    private static final Stack<ViewState> history = new Stack<>();

    public static void setMainContainer(StackPane container) {
        mainContainer = container;
    }

    public static void init(Stage stage) {
        mainStage = stage;
    }

    public static <T> T navigate(String fxmlPath) {
        if (mainContainer == null) {
            throw new IllegalStateException("Main container not initialized.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
            Node view = loader.load();

            if (!mainContainer.getChildren().isEmpty()) {
                Parent current = (Parent) mainContainer.getChildren().get(0);
                history.push(new ViewState(current, null));
            }

            T controller = loader.getController();

            if (controller instanceof Refreshable refreshable) {
                refreshable.refresh();
            }

            mainContainer.getChildren().setAll(view);

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void back() {
        if (!history.isEmpty()) {
            ViewState previous = history.pop();

            mainContainer.getChildren().setAll(previous.view());

            Object controller = previous.controller();

            if (controller instanceof Refreshable refreshable) {
                refreshable.refresh();
            }
        }
    }

    public static <T> T showOverlay(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
            Node view = loader.load();

            mainContainer.getChildren().add(view);

            T controller = loader.getController();

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeOverlay(Node node) {
        mainContainer.getChildren().remove(node);
    }

    public static void closeCurrentOrBack() {
        if (!history.isEmpty()) {
            back();
        } else {
            mainStage.close();
        }
    }
}