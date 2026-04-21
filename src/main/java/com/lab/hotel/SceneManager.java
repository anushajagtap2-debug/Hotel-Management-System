package com.lab.hotel;

import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneManager {
    private static Stage stage;

    private SceneManager() {
    }

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Avery Inn");
        stage.setMinWidth(1120);
        stage.setMinHeight(720);
    }

    public static void showLogin() {
        setScene("login.fxml", "Login");
    }

    public static void showDashboard(String role) {
        setScene("dashboard.fxml", "Dashboard", role);
    }

    private static void setScene(String fxml, String title) {
        setScene(fxml, title, null);
    }

    private static void setScene(String fxml, String title, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
            Parent root = loader.load();
            if (loader.getController() instanceof DashboardController dashboardController && role != null) {
                dashboardController.setRole(role);
            }
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(SceneManager.class
                    .getResource("styles/app.css")).toExternalForm());
            stage.setTitle("Avery Inn - " + title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load " + fxml, ex);
        }
    }
}
