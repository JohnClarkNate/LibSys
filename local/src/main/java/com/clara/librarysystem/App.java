package com.clara.librarysystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file and create the root node
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clara/librarysystem/table.fxml"));
            Parent root = loader.load();

            // Set up the scene and stage properties
            Scene scene = new Scene(root);
            primaryStage.setTitle("Library Meeting Room Reservation - Records");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);  // Make the window non-resizable
            primaryStage.show();  // Show the stage (window)
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading the table.fxml file.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
