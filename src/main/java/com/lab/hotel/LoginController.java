package com.lab.hotel;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String USER_USERNAME = "user";
    private static final String USER_PASSWORD = "user123";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (ADMIN_USERNAME.equalsIgnoreCase(username) && ADMIN_PASSWORD.equals(password)) {
            SceneManager.showDashboard("admin");
            return;
        }
        if (USER_USERNAME.equalsIgnoreCase(username) && USER_PASSWORD.equals(password)) {
            SceneManager.showDashboard("user");
            return;
        }

        messageLabel.setText("Invalid login. Try admin / admin123 or user / user123.");
    }
}
